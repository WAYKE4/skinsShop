package com.boot.dbskins.Security.Controller;

import com.boot.dbskins.Annotation.IsBlocked;
import com.boot.dbskins.Model.User;
import com.boot.dbskins.Repository.UserRepository;
import com.boot.dbskins.Security.Model.AuthRequestDTO;
import com.boot.dbskins.Security.Model.AuthResponseDTO;
import com.boot.dbskins.Security.Model.ChangeEmailDTO;
import com.boot.dbskins.Security.Model.ChangePasswordDTO;
import com.boot.dbskins.Security.Model.ChangeUserLoginDTO;
import com.boot.dbskins.Security.Model.RegistrationDTO;
import com.boot.dbskins.Security.Model.UserSecurity;
import com.boot.dbskins.Security.Repository.UserSecurityRepository;
import com.boot.dbskins.Security.Service.UserSecurityService;
import com.boot.dbskins.Service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.annotation.PostConstruct;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.Objects;
import java.util.Optional;

@RestController
@RequestMapping("/security")
@Slf4j
@SecurityRequirement(name = "Bearer Authentication")

public class SecurityController {
    private final UserSecurityService userSecurityService;
    private final UserSecurityRepository userSecurityRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final UserService userService;

    @Autowired
    public SecurityController(UserSecurityService userSecurityService, UserSecurityRepository userSecurityRepository,
                              PasswordEncoder passwordEncoder,
                              UserRepository userRepository, UserService userService) {
        this.userSecurityService = userSecurityService;
        this.userSecurityRepository = userSecurityRepository;
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.userService = userService;
    }

    @PostConstruct
    public void init() {
        Optional<UserSecurity> superadmin = userSecurityRepository.findByUserLogin("superAdminTest");
        if (superadmin.isEmpty()) {
            userSecurityService.createSuperAdmin();
            log.info("SUPERADMIN CREATED!");
        }
        log.info("SUPERADMIN ALREADY CREATED!");
    }

    @Operation(summary = "Регистрация")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Регистрация прошла успешна"),
            @ApiResponse(responseCode = "403", description = "Отказано в доспупе(валидация)" +
                    "или вы уже авторизованы!"),
            @ApiResponse(responseCode = "409", description = "Пользователь уже существует"),
    })
    @PostMapping("/registration")
    public ResponseEntity<HttpStatus> registration(@RequestBody @Valid RegistrationDTO registrationDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication.isAuthenticated() && !(authentication instanceof AnonymousAuthenticationToken)) {
            log.info("You are already logged in! " + authentication.getName());
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        userSecurityService.registration(registrationDto);
        log.info("New user has been registered! " + registrationDto.getLogin());
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @Operation(summary = "Выдача токена")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Токен выдан успешно"),
            @ApiResponse(responseCode = "403", description = "Отказано в доспупе"),
            @ApiResponse(responseCode = "401", description = "Не аутентифицированы"),
    })
    @PostMapping("/token")
    public ResponseEntity<AuthResponseDTO> generateToken(@RequestBody AuthRequestDTO authRequest) {
        Optional<String> token = userSecurityService.generateToken(authRequest);
        if (token.isPresent()) {
            log.info("Token issued " + authRequest.getLogin());
            return new ResponseEntity<>(new AuthResponseDTO(token.get()), HttpStatus.CREATED);
        }
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

    @Operation(summary = "Смена пароля")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Пароль изменен успешно"),
            @ApiResponse(responseCode = "403", description = "Отказано в доспупе"),
            @ApiResponse(responseCode = "400", description = "Ошибка в запросе"),
    })
    @IsBlocked
    @Transactional(rollbackFor = Exception.class)
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN', 'USER')")
    @PutMapping("/change-password")
    public ResponseEntity<HttpStatus> changePassword(@RequestBody @Valid ChangePasswordDTO user, Principal
            principal) {
        Optional<UserSecurity> result = userSecurityRepository.findByUserLogin(principal.getName());
        if (result.isPresent()) {
            if (Objects.equals(user.getLogin(), result.get().getUserLogin())) {
                if (passwordEncoder.matches(user.getOldPassword(), result.get().getUserPassword())) {
                    if (!Objects.equals(user.getNewPassword(), user.getOldPassword())) {
                        if (userSecurityService.updatePassword(result, user) > 0) {
                            log.info("User with ID: " + result.get().getUserId() + " change password ");
                            return new ResponseEntity<>(HttpStatus.CREATED);
                        }
                        log.error("Something wrong with DB!");
                    }
                    return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                }
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

    @Operation(summary = "Смена Логина")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Логин изменен успешно"),
            @ApiResponse(responseCode = "403", description = "Отказано в доспупе"),
            @ApiResponse(responseCode = "400", description = "Ошибка в запросе"),
    })
    @IsBlocked
    @Transactional(rollbackFor = Exception.class)
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN', 'USER')")
    @PutMapping("/change-username")
    public ResponseEntity<HttpStatus> changeUserLogin(@RequestBody @Valid ChangeUserLoginDTO user, Principal
            principal) {
        Optional<UserSecurity> result = userSecurityRepository.findByUserLogin(principal.getName());
        if (result.isPresent()) {
            if (Objects.equals(user.getOldUserLogin(), result.get().getUserLogin())) {
                if (!Objects.equals(user.getNewUserLogin(), user.getOldUserLogin())) {
                    if (userSecurityService.updateUserLogin(result.get().getUserId(), user.getNewUserLogin()) > 0) {
                        log.info("User with ID: " + result.get().getUserId() + " change login ");
                        return new ResponseEntity<>(HttpStatus.CREATED);
                    }
                    log.error("Something wrong with DB!");
                }
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

    @Operation(summary = "Смена почты")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Почта изменена успешно"),
            @ApiResponse(responseCode = "403", description = "Отказано в доспупе"),
            @ApiResponse(responseCode = "400", description = "Ошибка в запросе"),
    })
    @IsBlocked
    @Transactional(rollbackFor = Exception.class)
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN', 'USER')")
    @PutMapping("/change-email")
    public ResponseEntity<HttpStatus> changeEmail(@RequestBody @Valid ChangeEmailDTO user, Principal principal) {
        Optional<UserSecurity> result = userSecurityRepository.findByUserLogin(principal.getName());
        Optional<User> userFromRepository = userRepository.findUserById(result.get().getUserId());

        if (result.isPresent()) {
            if (Objects.equals(user.getLogin(), result.get().getUserLogin())) {
                if (!Objects.equals(user.getNewEmail(), userFromRepository.get().getEmail())) {
                    if (userService.updateUserEmail(result.get().getUserId(), user.getNewEmail()) > 0) {
                        log.info("User with ID: " + result.get().getUserId() + " change email ");
                        return new ResponseEntity<>(HttpStatus.CREATED);
                    }
                    log.error("Something wrong with DB!");
                }
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }
}
