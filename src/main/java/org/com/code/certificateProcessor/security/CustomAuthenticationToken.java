package org.com.code.certificateProcessor.security;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class CustomAuthenticationToken extends UsernamePasswordAuthenticationToken {

    //  增加 signInType 字段
    private String signInType;

    // 构造函数 (用于登录前，封装请求)
    public CustomAuthenticationToken(Object principal, Object credentials, String signInType) {
        /**
         *  public UsernamePasswordAuthenticationToken(Object principal, Object credentials) {
         *         super((Collection)null);
         *         this.principal = principal;
         *         this.credentials = credentials;
         *         this.setAuthenticated(false);
         *     }
         */
        super(principal, credentials);
        this.signInType = signInType;
    }

    // 构造函数 (用于登录成功后，封装用户信息)
    public CustomAuthenticationToken(Object principal, Object credentials,
                                     Collection<? extends GrantedAuthority> authorities, String signInType) {
        /**
         *  public UsernamePasswordAuthenticationToken(Object principal, Object credentials, Collection<? extends GrantedAuthority> authorities) {
         *         super(authorities);
         *         this.principal = principal;
         *         this.credentials = credentials;
         *         super.setAuthenticated(true);
         *     }
         */
        super(principal, credentials, authorities);
        this.signInType = signInType;
    }

    public String getSignInType() {
        return signInType;
    }
}
