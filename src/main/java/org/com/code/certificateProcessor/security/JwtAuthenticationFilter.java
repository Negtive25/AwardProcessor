package org.com.code.certificateProcessor.security;

import io.jsonwebtoken.io.IOException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.com.code.certificateProcessor.service.JWTService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.util.Collection;
import java.util.Collections;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JWTService jwtService;


    //使用filter中的init()方法来在filter的生命周期中我们手动注入需要使用的Service；

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException, java.io.IOException {

        // 1. 提取请求头中的JWT令牌
        String token = request.getHeader("token");

        if(token == null|| token.trim().isEmpty()){
            filterChain.doFilter(request, response);
            return;
        }
        // 2. 验证请求头中的JWT令牌
        String identification= jwtService.checkToken(token);

        if(identification == null){
            filterChain.doFilter(request, response);
            return;
        }
        String[]identificationArray = identification.split("_");
        String userId =  identificationArray[0];
        String auth = identificationArray[1];

        // 3. 如果令牌有效，则设置用户信息到SecurityContextHolder中
        //将用户的权限信息,然后将它存储到Security的上下文中
        Collection<GrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority(auth));

        //将UserDetails信息打包到UsernamePasswordAuthenticationToken中
        Authentication authentication =
                new UsernamePasswordAuthenticationToken(userId, "null", authorities);

        SecurityContextHolder.getContext().setAuthentication(authentication);

        /**
         * 如果令牌有效，则设置用户信息到SecurityContextHolder中,
         * 然后filterChain.doFilter(request, response)把网页请求放行给后面的过滤器链
         * 然后后面的表单登录过滤器链发现有SecurityContextHolder里面有用户账号信息的完整上下文,
         * 然后就会表单登录过滤器直接放行,就不用表单登录了
         */
        filterChain.doFilter(request, response);
    }
}