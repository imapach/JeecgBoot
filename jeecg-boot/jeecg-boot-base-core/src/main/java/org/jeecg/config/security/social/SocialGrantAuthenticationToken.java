package org.jeecg.config.security.social;

import org.jeecg.config.security.LoginType;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2AuthorizationGrantAuthenticationToken;

import java.util.Map;

/**
 * 社交模式认证专用token类型，方法spring authorization server进行认证流转，配合convert使用，配合github、企业微信、钉钉、微信登录使用
 * @author EightMonth
 * @date 2024/1/1
 */
public class SocialGrantAuthenticationToken extends OAuth2AuthorizationGrantAuthenticationToken {

    public SocialGrantAuthenticationToken(Authentication clientPrincipal, Map<String, Object> additionalParameters) {
        super(new AuthorizationGrantType(LoginType.SOCIAL), clientPrincipal, additionalParameters);
    }

}
