package org.com.code.certificateProcessor.security.userDetailService;

import org.com.code.certificateProcessor.mapper.StudentMapper;
import org.com.code.certificateProcessor.pojo.entity.Student;
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
public class StudentUserDetailsService implements UserDetailsService {
    @Autowired
    private StudentMapper studentMapper;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Student student = studentMapper.getStudentAuth(username);
        if (student == null) {
            throw new UsernameNotFoundException("用户不存在");
        }

        String[] auths = student.getAuth().split(" ");
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        for (String auth : auths) {
            GrantedAuthority authority = new SimpleGrantedAuthority(auth);
            authorities.add(authority);
        }
        return User.builder()
                .username(username)
                .password(student.getPassword())
                .authorities(authorities)
                .build();
    }
}
