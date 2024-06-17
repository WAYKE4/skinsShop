package com.boot.dbskins.Security.Service;

import com.boot.dbskins.Security.Model.UserSecurity;
import com.boot.dbskins.Security.Repository.UserSecurityRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
public class CustomUserDetailService implements UserDetailsService {

    private final UserSecurityRepository userSecurityRepository;

    @Autowired
    public CustomUserDetailService(UserSecurityRepository userSecurityRepository) {
        this.userSecurityRepository = userSecurityRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<UserSecurity> securityInfoOptional = userSecurityRepository.findByUserLogin(username);
        if (securityInfoOptional.isEmpty()) {
            throw new UsernameNotFoundException("Username not found: " + username);
        }
        UserSecurity security = securityInfoOptional.get();
        return User.builder()
                .username(security.getUserLogin())
                .roles(security.getRole().toString())
                .password(security.getUserPassword())
                .build();
    }

    public boolean isBlocked(String userlogin) throws UsernameNotFoundException {
        Optional<UserSecurity> securityInfoOptional = userSecurityRepository.findByUserLogin(userlogin);
        if (securityInfoOptional.isPresent()) {
            return securityInfoOptional.get().getIsBlocked();
        }
        return false;
    }
}