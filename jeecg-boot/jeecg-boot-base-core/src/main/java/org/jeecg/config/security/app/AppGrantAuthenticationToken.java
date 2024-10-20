package org.jeecg.config.security.app;

import org.jeecg.config.security.LoginType;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2AuthorizationGrantAuthenticationToken;

import java.util.Map;

/**
 * APP模式认证专用token类型，方法spring authorization server进行认证流转，配合convert使用
 * @author EightMonth
 * @date 2024/1/1
 */
public class AppGrantAuthenticationToken extends OAuth2AuthorizationGrantAuthenticationToken {

    public AppGrantAuthenticationToken(Authentication clientPrincipal, Map<String, Object> additionalParameters) {
        super(new AuthorizationGrantType(LoginType.APP), clientPrincipal, additionalParameters);
    }

}
