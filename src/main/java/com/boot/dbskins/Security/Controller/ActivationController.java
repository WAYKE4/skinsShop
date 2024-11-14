package com.boot.dbskins.Security.Controller;

import com.boot.dbskins.Security.Service.UserSecurityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class ActivationController {

    private final UserSecurityService userSecurityService;

    @Autowired
    public ActivationController(UserSecurityService userSecurityService) {
        this.userSecurityService = userSecurityService;
    }


    @Operation(summary = "Активация аккаунта")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Аккаунт успешно активирован"),
            @ApiResponse(responseCode = "403", description = "Отказано в доступе или ошибка активации. Пожалуйста, проверьте ссылку"),
    })
    @GetMapping("/activate")
    public ResponseEntity<HttpStatus> activateAccount(@RequestParam Long userId, @RequestParam String token) {
        boolean isActivated = userSecurityService.activateUser(userId, token);

        if (isActivated) {
            log.info(" Аккаунт c ID: " + userId + " успешно активирован!");
            return new ResponseEntity<>(HttpStatus.CREATED);
        } else {
            log.error("Ошибка активации. Пожалуйста, проверьте ссылку.");
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }
}