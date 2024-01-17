package com.meow.footprint.global.security.oauth;

import com.meow.footprint.domain.member.entity.Member;
import com.meow.footprint.domain.member.entity.SocialType;
import com.meow.footprint.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
    private final MemberRepository memberRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        String userNameAttributeName = userRequest.getClientRegistration().getProviderDetails()
                .getUserInfoEndpoint().getUserNameAttributeName();

        OAuthAttributes attributes = OAuthAttributes.of(SocialType.valueOf(registrationId.toUpperCase()), userNameAttributeName, oAuth2User.getAttributes());

        Member member = saveOrUpdate(attributes);
        Collection<? extends GrantedAuthority> authorities =member.getRole().stream()
                        .map(role -> new SimpleGrantedAuthority(role.name()))
                        .collect(Collectors.toList());

        return new DefaultOAuth2User(
                authorities,
                oAuth2User.getAttributes(),
                attributes.getNameAttributeKey());
    }


    private Member saveOrUpdate(OAuthAttributes attributes) {
        Optional<Member> member = memberRepository.findById(attributes.getOauth2UserInfo().getId());
        if(!member.isEmpty())
            return member.get();
        return memberRepository.save(attributes.toEntity());
    }
}