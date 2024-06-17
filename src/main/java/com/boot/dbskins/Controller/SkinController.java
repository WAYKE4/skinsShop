package com.boot.dbskins.Controller;

import com.boot.dbskins.Annotation.IsBlocked;
import com.boot.dbskins.Exception.ForbiddenException;
import com.boot.dbskins.Model.Skins;
import com.boot.dbskins.Model.dto.BuyOrSellSkinsDTO;
import com.boot.dbskins.Model.dto.TradeSkinDTO;
import com.boot.dbskins.Security.Model.UserSecurity;
import com.boot.dbskins.Security.Repository.UserSecurityRepository;
import com.boot.dbskins.Service.SkinGenerator;
import com.boot.dbskins.Service.SkinService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@SecurityRequirement(name = "Bearer Authentication")
public class SkinController {
    private final SkinService skinService;
    private final SkinGenerator skinGenerator;
    private final UserSecurityRepository userSecurityRepository;

    @Autowired
    public SkinController(SkinService skinService, SkinGenerator skinGenerator, UserSecurityRepository userSecurityRepository) {
        this.skinService = skinService;
        this.skinGenerator = skinGenerator;
        this.userSecurityRepository = userSecurityRepository;
    }

    @Operation(summary = "Актуальная информация о скинах в магазине")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Мазагин пустой"),
            @ApiResponse(responseCode = "200", description = "Информация о доспупных скинах"),
            @ApiResponse(responseCode = "403", description = "Отказано в доспупе"),
    })
    @GetMapping("/marketplace/AvailableSkins")
    ResponseEntity<List<Skins>> getAllSkins() {
        Optional<List<Skins>> allSkins = skinService.getALLSkins();
        if (allSkins.get().isEmpty()) {
            log.info("Marketplace empty");
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        log.info("Info about available skins");
        return new ResponseEntity<>(allSkins.get(), HttpStatus.OK);
    }

    @Operation(summary = "Генератор скинов")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Скин создан"),
            @ApiResponse(responseCode = "403", description = "Отказано в доспупе"),
    })
    @IsBlocked
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN')")
    @GetMapping("/skins/generator")
    ResponseEntity<Skins> skinsGenerator() {
        return new ResponseEntity<>(skinGenerator.generateRandomSkin(), HttpStatus.CREATED);
    }

    @Operation(summary = "Покупка скина")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Покупка прошла успешна"),
            @ApiResponse(responseCode = "403", description = "Отказано в доспупе"),
            @ApiResponse(responseCode = "400", description = "Ошибка в запросе"),
    })
    @IsBlocked
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN', 'USER')")
    @PutMapping("/buySkin")
    public ResponseEntity<HttpStatus> buySkins(@RequestBody @Valid BuyOrSellSkinsDTO user, Principal principal) {
        Optional<UserSecurity> result = userSecurityRepository.findByUserLogin(principal.getName());
        if (result.isPresent()) {
            if (result.get().getUserId() == user.getUserId()) {
                return skinService.buySkins(user.getSkinId(), user.getUserId());
            }
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

    @Operation(summary = "Продажа скина")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Продажа прошла успешна"),
            @ApiResponse(responseCode = "403", description = "Отказано в доспупе"),
            @ApiResponse(responseCode = "400", description = "Ошибка в запросе"),
    })
    @IsBlocked
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN', 'USER')")
    @PutMapping("/sellSkin")
    public ResponseEntity<HttpStatus> sellSkins(@RequestBody @Valid BuyOrSellSkinsDTO user, Principal principal) {
        Optional<UserSecurity> result = userSecurityRepository.findByUserLogin(principal.getName());
        if (result.isPresent()) {
            if (result.get().getUserId() == user.getUserId()) {
                return skinService.sellSkins(user.getSkinId(), user.getUserId());
            }
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

    @Operation(summary = "Обмен скинами")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Обмен произошел успешно"),
            @ApiResponse(responseCode = "403", description = "Отказано в доспупе"),
            @ApiResponse(responseCode = "400", description = "Ошибка в запросе"),
    })
    @Transactional
    @IsBlocked
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN', 'USER')")
    @PutMapping("/tradeSkin")
    public ResponseEntity<HttpStatus> tradeSkins(@RequestBody @Valid TradeSkinDTO user, Principal principal) {
        Optional<UserSecurity> result = userSecurityRepository.findByUserLogin(principal.getName());
        if (result.isPresent()) {
            if (result.get().getUserId() != user.getUserIdTo()) {
                return skinService.tradeSkins(user, result);
            }
            throw new ForbiddenException("You trying to trade yourself!");
        }
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }
}
