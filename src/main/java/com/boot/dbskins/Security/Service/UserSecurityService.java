package com.boot.dbskins.Security.Service;


import com.boot.dbskins.Exception.SameUserInDatabaseEmail;
import com.boot.dbskins.Exception.SameUserInDatabaseLogin;
import com.boot.dbskins.Model.User;
import com.boot.dbskins.Repository.UserRepository;
import com.boot.dbskins.Security.Model.AuthRequestDTO;
import com.boot.dbskins.Security.Model.ChangePasswordDTO;
import com.boot.dbskins.Security.Model.RegistrationDTO;
import com.boot.dbskins.Security.Model.Roles;
import com.boot.dbskins.Security.Model.UserSecurity;
import com.boot.dbskins.Security.Repository.UserSecurityRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class UserSecurityService {
    private final UserSecurityRepository userSecurityRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    @Autowired
    public UserSecurityService(UserSecurityRepository userSecurityRepository, UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtils jwtUtils) {
        this.userSecurityRepository = userSecurityRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
    }

    @Transactional(rollbackFor = Exception.class)
    public void registration(@Valid RegistrationDTO registrationDto) {
        Optional<UserSecurity> securityByLogin = userSecurityRepository.findByUserLogin(registrationDto.getLogin());
        Optional<User> userByEmail = userRepository.findUserByEmail(registrationDto.getEmail());
        if (securityByLogin.isPresent()) {
            throw new SameUserInDatabaseLogin(registrationDto.getLogin());
        }
        if (userByEmail.isPresent()) {
            throw new SameUserInDatabaseEmail(registrationDto.getEmail());
        }
        User user = new User();
        user.setAge(registrationDto.getAge());
        user.setUsername(registrationDto.getUsername());
        user.setCreated(Timestamp.valueOf(LocalDateTime.now()));
        user.setBalance(0);
        user.setEmail(registrationDto.getEmail());
        User savedUser = userRepository.save(user);

        UserSecurity userSecurity = new UserSecurity();
        userSecurity.setUserLogin(registrationDto.getLogin());
        userSecurity.setUserPassword(passwordEncoder.encode(registrationDto.getPassword()));
        userSecurity.setRole(Roles.USER);
        userSecurity.setUserId(savedUser.getId());
        userSecurity.setIsBlocked(false);
        userSecurityRepository.save(userSecurity);
    }

    @Transactional(rollbackFor = Exception.class)
    public void createSuperAdmin() {
        Optional<UserSecurity> security = userSecurityRepository.findByUserLogin("superAdminTest");
        if (security.isPresent()) {
            throw new SameUserInDatabaseLogin("superAdmin already exist!");
        }
        User superAdminFromUser = new User();
        superAdminFromUser.setAge(0);
        superAdminFromUser.setUsername("superAdminTest");
        superAdminFromUser.setCreated(Timestamp.valueOf(LocalDateTime.now()));
        superAdminFromUser.setBalance(0);
        superAdminFromUser.setEmail("superadminTest@mail.ru");
        User savedSuperAdminFromUser = userRepository.save(superAdminFromUser);

        UserSecurity userSecurity = new UserSecurity();
        userSecurity.setUserLogin("superAdminTest");
        userSecurity.setUserPassword(passwordEncoder.encode("superAdminTest"));
        userSecurity.setRole(Roles.SUPERADMIN);
        userSecurity.setUserId(savedSuperAdminFromUser.getId());
        userSecurity.setIsBlocked(false);
        userSecurityRepository.save(userSecurity);
    }

    public Optional<String> generateToken(AuthRequestDTO authRequestDto) {
        Optional<UserSecurity> security = userSecurityRepository.findByUserLogin(authRequestDto.getLogin());
        if (security.isPresent()
                && passwordEncoder.matches(authRequestDto.getPassword(), security.get().getUserPassword())) {
            return Optional.of(jwtUtils.generateJwtToken(authRequestDto.getLogin()));
        }
        return Optional.empty();
    }

    public int updatePassword(Optional<UserSecurity> result, ChangePasswordDTO user) {
        String encodePassword = passwordEncoder.encode(user.getNewPassword());
        return userSecurityRepository.updateInfoAboutUserPassword(result.get().getUserId(), encodePassword);
    }

    public int updateUserLogin(Long id, String newLogin) {
        return userSecurityRepository.updateInfoAboutUserLogin(id, newLogin);
    }

    public int updateUserRole(Long id, Roles role) {
        return userSecurityRepository.updateInfoAboutUserRole(id, role.name());
    }

    public int updateUserBlock(Long id, Boolean newBlock) {
        return userSecurityRepository.updateInfoAboutUserBlock(id, newBlock);
    }
}
