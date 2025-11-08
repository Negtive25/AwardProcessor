package org.com.code.certificateProcessor.security.userDetailService;

import org.com.code.certificateProcessor.mapper.AdminMapper;
import org.com.code.certificateProcessor.pojo.entity.Admin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;

@Component
public class AdminUserDetailsService implements UserDetailsService {
    @Autowired
    private AdminMapper adminMapper;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Admin admin = adminMapper.getAdminAuth(username);
        if (admin == null) {
            throw new UsernameNotFoundException("用户不存在");
        }

        String[] roles = admin.getAuth().split(" ");
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        for (String role : roles) {
            GrantedAuthority authority = new SimpleGrantedAuthority(role);
            authorities.add(authority);
        }
        return User.builder()
                .username(username)
                .password(admin.getPassword())
                .authorities(authorities)
                .build();
    }
}
