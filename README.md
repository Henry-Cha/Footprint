# Footprint-BackEnd
방명록 서비스 입니다.

api 명세 : https://jeweled-maiasaura-181.notion.site/Ver-1-9ac35007ef24404e95b2c998591ea90b?pvs=4

<br/>

## 개요

> 백엔드 주요 사용 기술
- Spring Boot 3.2
- Java17
- Spring Security 6.2 + JWT + Oauth2
- Spring Data JPA
- Query DSL
- MySQL (AWS RDS)
- Redis (AWS ElastiCache)
- AWS (ec2,s3)
- Swagger
- Rest api


> 시스템 아키텍처
> 
![image](https://github.com/Henry-Cha/Footprint/assets/74866067/de42a44d-abcb-4c4f-96e6-b53d237deee5)



>패키지 구조

패키지 구조를 도메인형으로 나누어 직관적으로 구분이 가능하도록 설계하였습니다.

```
└─main
    │  ├─java
    │  │  └─com
    │  │      └─meow
    │  │          └─footprint
    │  │              ├─domain
    │  │              │  ├─auth
    │  │              │  │  ├─api
    │  │              │  │  ├─controller
    │  │              │  │  ├─dto
    │  │              │  │  └─service
    │  │              │  ├─footprint
    │  │              │  │  ├─controller
    │  │              │  │  ├─dto
    │  │              │  │  ├─entity
    │  │              │  │  ├─repository
    │  │              │  │  └─service
    │  │              │  ├─guestbook
    │  │              │  │  ├─controller
    │  │              │  │  ├─dto
    │  │              │  │  ├─entity
    │  │              │  │  ├─repository
    │  │              │  │  └─service
    │  │              │  └─member
    │  │              │      ├─controller
    │  │              │      ├─dto
    │  │              │      ├─entity
    │  │              │      ├─repository
    │  │              │      └─service
    │  │              └─global
    │  │                  ├─config
    │  │                  ├─result
    │  │                  │  └─error
    │  │                  │      └─exception
    │  │                  ├─security
    │  │                  │  └─filter
    │  │                  └─util
    │  └─resources
    ...
```
>ERD

![Footprint](https://github.com/Henry-Cha/Footprint/assets/74866067/5e692387-106a-4398-8fce-0d1de34b7a9e)




## 주요 코드

- QR코드 생성

```
public class QrCodeUtil {
    private final ImageUploader imageUploader;
    @Value("${qrCode.width}")
    private int width;
    @Value("${qrCode.height}")
    private int height;
    @Value("${qrCode.type}")
    private String  type;
    public String qrCodeGenerate(long guestbookId, String link){
        try {
            String fileName = "guestbook" + guestbookId + "qrcode." + type;
            BitMatrix bitMatrix = new QRCodeWriter().encode(link,BarcodeFormat.QR_CODE,width,height);
            BufferedImage qrImage = MatrixToImageWriter.toBufferedImage(bitMatrix);
            return imageUploader.uploadBufferedImage(qrImage, fileName);
        } catch (Exception e) {
            throw new BusinessException(FAIL_TO_QR_GEN);
        }
    }
}
```

<br/>

- Email 인증
```
    public void createMimeEmailForm(String toEmail,
                                    String title,
                                    String text) {
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, "utf-8");
        String htmlMsg = "<h1>인증코드 : " +text+ "</h1>";
        try {
            helper.setText(htmlMsg, true);
            helper.setTo(toEmail);
            helper.setSubject(title);
            helper.setFrom("Footprint@Footprint.com");
            emailSender.send(message);
        }catch (MessagingException e){
            throw new BusinessException(FAIL_TO_SEND_EMAIL);
        }
    }
```

<br/>


- 사용자 위치(좌표) 검사

```
    public boolean checkLocation(double latBook,double lonBook,double latFoot,double lonFoot){
        double theta = lonBook - lonFoot;
        double dist = Math.sin(Math.toRadians(latBook))
                * Math.sin(Math.toRadians(latFoot))
                + Math.cos(Math.toRadians(latBook))
                * Math.cos(Math.toRadians(latFoot))
                * Math.cos(Math.toRadians(theta));
        dist = Math.acos(dist);
        dist = Math.toDegrees(dist);
        dist *= 60*1.1515*1609.344;  //meter단위 거리
        return dist <= DEGREE;
    }
```

<br/>


- DTO와 Entity 분리

관심사 분리로 예상치 못한 에러를 방지하고, 필요한 데이터만 전달합니다.




## Security
자체 회원가입,로그인 + OAuth2 소셜로그인(카카오,네이버) 구현. (비회원이라면 회원가입 후) 자체 JWT토큰을 발급합니다.

스프링 시큐리티의 필터를 이용하여 토큰 인증 과정을 처리합니다.

소셜로그인 과정은 CSR 환경을 고려하여 프론트에서 인가코드 발급 후, 인가코드를 전달받아 처리하도록 구현하였습니다.

비슷한 Oauth2 처리 과정에서 코드 중복을 줄이기 위해, 관련 클래스를 추상화하였습니다.

<br/>

- AuthServiceImpl
  
```
    @Override
    public LoginTokenDTO loginOauth(OAuthLoginReq loginReq) {
        try {
            ClientRegistration provider = inMemoryClient.findByRegistrationId(
                loginReq.getProviderName().getSocialName()); //provider 찾음
            OAuthApi oAuthApi = null;
            switch (loginReq.getProviderName()) {
                case KAKAO -> oAuthApi = new KakaoOAuthApi(provider, loginReq);
                case NAVER -> oAuthApi = new NaverOAuthApi(provider, loginReq);
            }
            OAuth2UserInfo oAuth2UserInfo = oAuthApi.loginProcess();
            Member member = memberRepository.findById(oAuth2UserInfo.getEmail())
                .orElseGet(() -> createNewMember(oAuth2UserInfo));
        .
        .
        .
    }
```
<br/>

- OAuthApi

```
public abstract class OAuthApi {

    private ClientRegistration provider;
    private OAuthLoginReq oAuthLoginReq;

    public OAuth2UserInfo loginProcess() {
        OAuthTokenRes oAuthToken = getOAuthToken(provider, oAuthLoginReq);
        Map<String, Object> param = getUserAttribute(provider,
            oAuthToken.getAccessToken());
        return makeUserInfo(param);
    }

    public abstract OAuthTokenRes getOAuthToken(ClientRegistration provider,
        OAuthLoginReq loginReq);

    private Map<String, Object> getUserAttribute(ClientRegistration clientRegistration,
        String oauth2Token) {
        return WebClient.create()
            .get()
            .uri(clientRegistration.getProviderDetails().getUserInfoEndpoint().getUri())
            .headers(header -> header.setBearerAuth(oauth2Token))
            .retrieve()
            .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {
            })
            .block();
    }

    public abstract OAuth2UserInfo makeUserInfo(Map<String, Object> param);
}
```


## 응답 객체
RestController의 반환 객체를 통일하였습니다.

<br/>

- ResultResponse

```
public class ResultResponse {
    @Schema(description = "Http 상태 코드")
    private final int status;
    @Schema(description = "응답 메세지")
    private final String message;
    @Schema(description = "응답 데이터")
    private final Object data;

    private ResultResponse(ResultCode resultCode, Object data) {
        this.status = resultCode.getStatus();
        this.message = resultCode.getMessage();
        this.data = data;
    }

...

}
```
<br/>

- 성공 응답 JSON 예시
```
{
  "status": 200,
  "message": "회원정보 조회에 성공하였습니다.",
  "data": { .. }
}
```
<br/>

- ErrorResponse

요청 실패 시 에러응답 객체를 통일하였습니다.
```
public class ErrorResponse {
    private int status;
    private String message;
    private List<FieldError> errors;

    private ErrorResponse(final ErrorCode code, final List<FieldError> errors) {
        this.message = code.getMessage();
        this.status = code.getStatus();
        this.errors = errors;
    }

    private ErrorResponse(final ErrorCode code) {
        this.message = code.getMessage();
        this.status = code.getStatus();
        this.errors = new ArrayList<>();
    }
    ...
}
```
<br/>


- 에러 응답 JSON 예시
```
{
  "status": 400,
  "message": "회원 id가 존재하지 않습니다.",
  "errors": []
}
```
<br/>

- GlobalExceptionHandler


```
@RestControllerAdvice
public class GlobalExceptionHandler {
    ...
    
    @ExceptionHandler
    protected ResponseEntity<ErrorResponse> handleBusinessException(BusinessException e) { //기타 개발자 정의 예외
        final ErrorCode errorCode = e.getErrorCode();
        final ErrorResponse response = ErrorResponse.of(errorCode, e.getErrors());
        return new ResponseEntity<>(response, HttpStatus.valueOf(errorCode.getStatus()));
    }
    
    ...
}
```

## 컨벤션

>자바 네이밍 컨벤션 준수 (카멜케이스 등)


>커밋 컨벤션

```
type: subject

body (optional)
...
...
...

footer (optional)
```

타입	설명
- feat	새로운 기능 추가
- fix	버그/코드 수정
- docs	문서 수정
- style	공백, 세미콜론 등 스타일 수정
- refactor	코드 리팩토링
- perf	성능 개선
- test	테스트 추가
- chore	빌드 과정 또는 보조 기능(문서 생성기능 등) 수정
