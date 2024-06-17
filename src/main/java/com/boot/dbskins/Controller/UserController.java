package com.boot.dbskins.Controller;

import com.boot.dbskins.Annotation.IsBlocked;
import com.boot.dbskins.Model.Skins;
import com.boot.dbskins.Model.User;
import com.boot.dbskins.Repository.UserRepository;
import com.boot.dbskins.Security.Model.Roles;
import com.boot.dbskins.Security.Model.UserSecurity;
import com.boot.dbskins.Security.Repository.UserSecurityRepository;
import com.boot.dbskins.Service.SkinService;
import com.boot.dbskins.Service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@RestController
@Slf4j
@SecurityRequirement(name = "Bearer Authentication")
public class UserController {
    private final SkinService skinService;
    private final UserService userService;
    private final UserSecurityRepository userSecurityRepository;
    private final UserRepository userRepository;

    @Autowired
    public UserController(SkinService skinService, UserService userService, UserSecurityRepository userSecurityRepository, UserRepository userRepository) {
        this.skinService = skinService;
        this.userService = userService;
        this.userSecurityRepository = userSecurityRepository;
        this.userRepository = userRepository;
    }


    @Operation(summary = "Актуальная информация о пользователе")
    @ApiResponses({
            @ApiResponse(responseCode = "404", description = "Пользователь не найден"),
            @ApiResponse(responseCode = "200", description = "Пользователь найден"),
            @ApiResponse(responseCode = "403", description = "Отказано в доспупе"),
    })
    @IsBlocked
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN', 'USER')")
    @GetMapping("/allSkins/getInfoAboutUser/{id}")
    ResponseEntity<User> getInfoAboutCurrentUser(@PathVariable Long id, Principal principal) {

        Optional<UserSecurity> result = userSecurityRepository.findByUserLogin(principal.getName());
        Optional<User> user = userService.getInfoAboutCurrentUser(id);

        if (result.isPresent()) {
            if (result.get().getRole().equals(Roles.ADMIN) || result.get().getRole().equals(Roles.SUPERADMIN)) {
                if (user.isPresent()) {
                    log.info("Get info about user with id " + id);
                    return new ResponseEntity<>(user.get(), HttpStatus.OK);
                }
                log.error("Invalid userId");
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            } else if (result.get().getRole().equals(Roles.USER)) {
                if (user.isPresent()) {
                    if (result.get().getUserId() == user.get().getId()) {
                        return new ResponseEntity<>(user.get(), HttpStatus.OK);
                    }
                    return new ResponseEntity<>(HttpStatus.FORBIDDEN);
                }
                log.error("Invalid userId");
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        }
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

    @Operation(summary = "Актуальная информация о скинах пользователя")
    @ApiResponses({
            @ApiResponse(responseCode = "404", description = "Пользователь не найден"),
            @ApiResponse(responseCode = "200", description = "Пользователь найден"),
            @ApiResponse(responseCode = "403", description = "Отказано в доспупе"),
    })
    @IsBlocked
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN', 'USER')")
    @GetMapping("/userSkins/{id}")
    ResponseEntity<List<Skins>> findUserSkinsByUserId(@PathVariable Long id) {
        Optional<List<Skins>> allSkinsByUserId = skinService.getUserSkinsByUserId(id);
        Optional<User> User = userService.getInfoAboutCurrentUser(id);
        if (User.isPresent()) {
            log.info("Get info about user skins with id " + id);
            return new ResponseEntity<>(allSkinsByUserId.get(), HttpStatus.OK);
        }
        log.error("Invalid userId");
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @Operation(summary = "Пополнение баланса")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Баланс пополнен"),
            @ApiResponse(responseCode = "403", description = "Отказано в доспупе"),
            @ApiResponse(responseCode = "400", description = "Ошибка в запросе"),
    })
    @IsBlocked
    @Transactional(rollbackFor = Exception.class)
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN', 'USER')")
    @PostMapping("/balance-replenishment/{balance}")
    ResponseEntity<HttpStatus> balanceReplenishment(@PathVariable int balance, Principal principal) {
        Optional<UserSecurity> result = userSecurityRepository.findByUserLogin(principal.getName());
        if (balance <= 0) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        int newBalance;
        if (result.isPresent()) {
            Optional<User> user = userRepository.findUserById(result.get().getUserId());
            newBalance = user.get().getBalance() + balance;
            userRepository.updateInfoAboutUser(user.get().getId(), newBalance);
            return new ResponseEntity<>(HttpStatus.CREATED);
        }
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }
}