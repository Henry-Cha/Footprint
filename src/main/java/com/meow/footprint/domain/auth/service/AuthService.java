package com.meow.footprint.domain.auth.service;

import com.meow.footprint.domain.auth.dto.OAuthLoginReq;
import com.meow.footprint.domain.member.dto.LoginTokenDTO;

public interface AuthService {

    LoginTokenDTO loginOauth(OAuthLoginReq loginReq);
}
