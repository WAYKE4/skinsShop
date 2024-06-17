package com.boot.dbskins.Security.Controller;

import com.boot.dbskins.Annotation.IsBlocked;
import com.boot.dbskins.Exception.ForbiddenException;
import com.boot.dbskins.Security.Model.GiveAdminDTO;
import com.boot.dbskins.Security.Model.Roles;
import com.boot.dbskins.Security.Model.UserSecurity;
import com.boot.dbskins.Security.Repository.UserSecurityRepository;
import com.boot.dbskins.Security.Service.UserSecurityService;
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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.Optional;

@RestController
@RequestMapping("/super-admin")
@Slf4j
@SecurityRequirement(name = "Bearer Authentication")
public class SuperAdminController {

    private final UserSecurityService userSecurityService;
    private final UserSecurityRepository userSecurityRepository;
    private final UserService userService;

    @Autowired
    public SuperAdminController(UserSecurityService userSecurityService, UserSecurityRepository userSecurityRepository,
                                UserService userService) {
        this.userSecurityService = userSecurityService;
        this.userSecurityRepository = userSecurityRepository;
        this.userService = userService;
    }


    @Operation(summary = "Выдача админа")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Админка выдана"),
            @ApiResponse(responseCode = "403", description = "Отказано в доспупе"),
            @ApiResponse(responseCode = "400", description = "Ошибка в запросе"),
    })
    @IsBlocked
    @Transactional(rollbackFor = Exception.class)
    @PreAuthorize("hasRole('SUPERADMIN')")
    @PostMapping("/give-admin")
    ResponseEntity<HttpStatus> giveAdmin(@RequestBody GiveAdminDTO user, Principal principal) {
        Optional<UserSecurity> result = userSecurityRepository.findByUserLogin(principal.getName());
        Optional<UserSecurity> newAdmin = userSecurityRepository.findByUserId(user.getUserId());

        if (result.isPresent()) {
            if (newAdmin.isPresent()) {
                if (result.get().getUserId() != user.getUserId()) {
                    if (newAdmin.get().getRole() != Roles.SUPERADMIN) {
                        if (!newAdmin.get().getIsBlocked()) {
                            if (newAdmin.get().getRole() == Roles.USER) {
                                if (userSecurityService.updateUserRole(newAdmin.get().getUserId(), Roles.ADMIN) > 0) {
                                    log.info("User with ID: " + newAdmin.get().getUserId() + " change ROLE (ADMIN) ");
                                    return new ResponseEntity<>(HttpStatus.CREATED);
                                }
                                log.error("Something wrong with DB!");
                            }
                            throw new ForbiddenException("This user is already ADMIN, ID: " + newAdmin.get().getUserId());
                        }
                        throw new ForbiddenException("New potential ADMIN is blocked, ID: " + newAdmin.get().getUserId());
                    }
                    throw new ForbiddenException("Error! You trying to downgrade another SUPER ADMIN!");
                }
                throw new ForbiddenException("Error! You trying to change your role");
            }
            log.error("Error! User with this id don't exist! ID: " + user.getUserId());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        log.error("You aren't authorized!");
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

    @Operation(summary = "Отмена админа")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Админка выдана"),
            @ApiResponse(responseCode = "403", description = "Отказано в доспупе"),
            @ApiResponse(responseCode = "400", description = "Ошибка в запросе"),
    })
    @IsBlocked
    @Transactional(rollbackFor = Exception.class)
    @PreAuthorize("hasRole('SUPERADMIN')")
    @PostMapping("/downgrade-admin")
    ResponseEntity<HttpStatus> downgradeAdmin(@RequestBody GiveAdminDTO user, Principal principal) {
        Optional<UserSecurity> result = userSecurityRepository.findByUserLogin(principal.getName());
        Optional<UserSecurity> oldAdmin = userSecurityRepository.findByUserId(user.getUserId());

        if (result.isPresent()) {
            if (oldAdmin.isPresent()) {
                if (result.get().getUserId() != user.getUserId()) {
                    if (oldAdmin.get().getRole() != Roles.SUPERADMIN) {
                        if (!oldAdmin.get().getIsBlocked()) {
                            if (oldAdmin.get().getRole() == Roles.ADMIN) {
                                if (userSecurityService.updateUserRole(oldAdmin.get().getUserId(), Roles.USER) > 0) {
                                    log.info("User with ID: " + oldAdmin.get().getUserId() + " change ROLE (USER) ");
                                    return new ResponseEntity<>(HttpStatus.CREATED);
                                }
                                log.error("Something wrong with DB!");
                            }
                            throw new ForbiddenException("This user is already USER, ID: " + oldAdmin.get().getUserId());
                        }
                        throw new ForbiddenException("New potential USER is blocked, ID: " + oldAdmin.get().getUserId());
                    }
                    throw new ForbiddenException("Error! You trying to downgrade another SUPER ADMIN!");
                }
                throw new ForbiddenException("Error! You trying to change your role");
            }
            log.error("Error! User with this id don't exist! ID: " + user.getUserId());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        log.error("You aren't authorized!");
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

    @Operation(summary = "Блокировка пользователя")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Админка выдана"),
            @ApiResponse(responseCode = "403", description = "Отказано в доспупе"),
            @ApiResponse(responseCode = "400", description = "Ошибка в запросе"),
    })
    @IsBlocked
    @Transactional(rollbackFor = Exception.class)
    @PreAuthorize("hasRole('SUPERADMIN')")
    @PostMapping("/block")
    ResponseEntity<HttpStatus> block(@RequestBody GiveAdminDTO user, Principal principal) {
        Optional<UserSecurity> result = userSecurityRepository.findByUserLogin(principal.getName());
        Optional<UserSecurity> potentialBlock = userSecurityRepository.findByUserId(user.getUserId());

        if (result.isPresent()) {
            if (potentialBlock.isPresent()) {
                if (result.get().getUserId() != user.getUserId()) {
                    if (potentialBlock.get().getRole() != Roles.SUPERADMIN) {
                        if (!potentialBlock.get().getIsBlocked()) {
                            if (userSecurityService.updateUserBlock(potentialBlock.get().getUserId(), true) > 0) {
                                log.info("User with ID: " + potentialBlock.get().getUserId() + " blocked!");
                                return new ResponseEntity<>(HttpStatus.CREATED);
                            }
                            log.error("Something wrong with DB!");
                        }
                        throw new ForbiddenException("This user is already blocked, ID: " + potentialBlock.get().getUserId());
                    }
                    throw new ForbiddenException("Error! You trying to block another SUPER ADMIN!");
                }
                throw new ForbiddenException("Error! You trying to block yourself ");
            }
            log.error("Error! User with this id don't exist! ID: " + user.getUserId());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        log.error("You aren't authorized!");
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

    @Operation(summary = "Удаление пользователя")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Админка выдана"),
            @ApiResponse(responseCode = "403", description = "Отказано в доспупе"),
            @ApiResponse(responseCode = "400", description = "Ошибка в запросе"),
    })
    @IsBlocked
    @Transactional(rollbackFor = Exception.class)
    @PreAuthorize("hasRole('SUPERADMIN')")
    @DeleteMapping("/delete-user")
    ResponseEntity<HttpStatus> deleteUser(@RequestBody GiveAdminDTO user, Principal principal) {
        Optional<UserSecurity> result = userSecurityRepository.findByUserLogin(principal.getName());
        Optional<UserSecurity> potentialDelete = userSecurityRepository.findByUserId(user.getUserId());

        if (result.isPresent()) {
            if (potentialDelete.isPresent()) {
                if (result.get().getUserId() != user.getUserId()) {
                    if (potentialDelete.get().getRole() != Roles.SUPERADMIN) {
                        if (userService.deleteUser(potentialDelete.get().getUserId())) {
                            log.info("User with ID: " + potentialDelete.get().getUserId() + " deleted!");
                            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
                        }
                        log.error("Something wrong with DB!");
                        return new ResponseEntity<>(HttpStatus.CONFLICT);
                    }
                    throw new ForbiddenException("Error! You trying to delete another SUPER ADMIN!");
                }
                throw new ForbiddenException("Error! You trying to delete yourself ");
            }
            log.error("Error! User with this id don't exist! ID: " + user.getUserId());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        log.error("You aren't authorized!");
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }
}