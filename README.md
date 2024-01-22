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

![image](https://github.com/Footprint-meow/Footprint-BackEnd/assets/74866067/ece22223-8550-4b1b-aa7c-4df1ac72d478)


>패키지 구조

패키지 구조를 도메인형으로 나누어 직관적으로 구분이 가능하도록 설계하였습니다.

```
└─src
    ├─main
    │  ├─java
    │  │  └─com
    │  │      └─meow
    │  │          └─footprint
    │  │              ├─domain
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
    │  │              │      ├─exception
    │  │              │      ├─repository
    │  │              │      └─service
    │  │              └─global
    │  │                  ├─config
    │  │                  ├─result
    │  │                  │  └─error
    │  │                  │      └─exception
    │  │                  ├─security
    │  │                  │  ├─filter
    │  │                  │  └─oauth
    │  │                  └─util
    │  └─resources
    │      ├─static
    │      └─templates
```

>ERD

![image](https://github.com/Footprint-meow/Footprint-BackEnd/assets/74866067/52a774f1-e86d-419c-af06-4bbaaa5e2e5b)



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
자체 회원가입,로그인 + OAuth2 소셜로그인(카카오,네이버,구글) 구현. (비회원이라면 회원가입 후) 자체 JWT토큰을 발급합니다.

스프링 시큐리티의 필터를 이용하여 토큰 발급,인증 과정을 처리하였습니다.

<br/>

- securityConfig


```
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(authorizeRegistry -> authorizeRegistry
                .requestMatchers(whiteList).permitAll()
                .anyRequest().authenticated())
                .formLogin(FormLoginConfigurer::disable)
                .csrf(CsrfConfigurer::disable)
                .cors(corsConfigurer -> corsConfigurer.configurationSource(corsConfigurationSource()))
                .sessionManagement(sessionManagementConfigurer -> sessionManagementConfigurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtFilter(), UsernamePasswordAuthenticationFilter.class)
                .oauth2Login(oAuth2LoginConfigurer -> oAuth2LoginConfigurer
                        .userInfoEndpoint(userInfoEndpointConfig -> userInfoEndpointConfig
                                .userService(oAuth2UserService))
                        .successHandler(oauthSuccessHandler));
        return http.build();
```

<br/>

- CustomOAuth2UserService

```
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
    private final MemberRepository memberRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        String userNameAttributeName = userRequest.getClientRegistration().getProviderDetails()
                .getUserInfoEndpoint().getUserNameAttributeName();

        OAuthAttributes attributes = OAuthAttributes.of(SocialType.valueOf(registrationId.toUpperCase()), userNameAttributeName, oAuth2User.getAttributes());

        log.info(attributes.toString());
        Member member = saveOrUpdate(attributes);
        Collection<? extends GrantedAuthority> authorities =member.getRole().stream()
                        .map(role -> new SimpleGrantedAuthority(role.name()))
                        .collect(Collectors.toList());

        return new DefaultOAuth2User(
                authorities,
                attributes.getOauth2UserInfo().attributes,
                attributes.getNameAttributeKey());
    }
    ...
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
