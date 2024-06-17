package com.boot.dbskins.Service;

import com.boot.dbskins.Exception.ForbiddenException;
import com.boot.dbskins.Exception.InsufficientFunds;
import com.boot.dbskins.Exception.UserOrSkinNotExist;
import com.boot.dbskins.Model.Skins;
import com.boot.dbskins.Model.User;
import com.boot.dbskins.Model.dto.TradeSkinDTO;
import com.boot.dbskins.Repository.SkinRepository;
import com.boot.dbskins.Repository.UserRepository;
import com.boot.dbskins.Security.Model.UserSecurity;
import com.boot.dbskins.Security.Repository.UserSecurityRepository;
import com.boot.dbskins.Security.Service.UserSecurityService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class SkinService {

    private final SkinRepository skinRepository;
    private final UserRepository userRepository;
    private final UserSecurityRepository userSecurityRepository;

    @Autowired
    public SkinService(SkinRepository skinRepository, UserRepository userRepository, UserSecurityRepository userSecurityRepository) {
        this.skinRepository = skinRepository;
        this.userRepository = userRepository;
        this.userSecurityRepository = userSecurityRepository;
    }

    public Optional<List<Skins>> getALLSkins() {
        return skinRepository.customGetAllSkinsAndSortById();
    }

    public Optional<List<Skins>> getUserSkinsByUserId(Long id) {
        return skinRepository.customGetAllSkinsByUserId(id);
    }

    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity<HttpStatus> buySkins(Long skinId, Long userId) {
        log.info("Skin purchase processing has started .");
        Optional<Skins> skinById = skinRepository.findSkinForBuy(skinId);
        Optional<User> userById = userRepository.findUserById(userId);
        int newBalance;

        if (skinById.isPresent() && userById.isPresent()) {
            if (skinById.get().getCost() <= userById.get().getBalance()) {
                newBalance = userById.get().getBalance() - skinById.get().getCost();
                skinRepository.updateInfoAboutUserSkinsForBuy(userId, skinId);
                userRepository.updateInfoAboutUser(userId, newBalance);
                log.info("The skin purchase was successful!: UserID: " + userById.get().getId() + " SkinID: " + skinById.get().getId());
                return new ResponseEntity<>(HttpStatus.CREATED);
            }
            throw new InsufficientFunds("UserID: " + userById.get().getId() + " SkinID: " + skinById.get().getId());
        }
        throw new UserOrSkinNotExist("There is no user, or this skin doesn't belong to him. UserID: " + userId + " SkinID: " + skinId);
    }

    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity<HttpStatus> sellSkins(Long skinId, Long userId) {
        log.info("Skin sell processing has started .");
        Optional<Skins> skinById = skinRepository.findSkinForSell(skinId);
        Optional<User> userById = userRepository.findUserById(userId);
        int newBalance;

        if (skinById.isPresent() && userById.isPresent()) {
            if (skinById.get().getUserId() == userById.get().getId()) {
                newBalance = userById.get().getBalance() + skinById.get().getCost();
                skinRepository.updateInfoAboutUserSkinsForSell(skinId);
                userRepository.updateInfoAboutUser(userId, newBalance);
                log.info("The skin sell was successful!: UserID: " + userById.get().getId() + " SkinID: " + skinById.get().getId());
                return new ResponseEntity<>(HttpStatus.CREATED);
            }
            throw new ForbiddenException("Incorrect userID or userFromSkinID input" + "UserID: "
                    + userById.get().getId() + " UserID: "
                    + skinById.get().getUserId());
        }
        throw new UserOrSkinNotExist("There is no user, or this skin doesn't belong to him. UserID: " + userId + " SkinID: " + skinId);
    }


    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity<HttpStatus> tradeSkins(TradeSkinDTO user, Optional<UserSecurity> result) {
        log.info("Skin trade processing has started .");
        Optional<Skins> skinFrom = skinRepository.findSkinForSell(user.getSkinIdFrom());
        Optional<Skins> skinTo = skinRepository.findSkinForSell(user.getSkinIdTo());
        Optional<UserSecurity> userTo = userSecurityRepository.findByUserId(user.getUserIdTo());

        if (skinTo.isPresent() && skinFrom.isPresent()) {
            if (userTo.isPresent()) {
                if (!userTo.get().getIsBlocked()) {
                    if (skinFrom.get().getCost() == skinTo.get().getCost()) {
                        if (skinFrom.get().getId() != skinTo.get().getId()) {
                            if (userTo.get().getUserId() == skinTo.get().getUserId()
                                    && result.get().getUserId() == skinFrom.get().getUserId()) {
                                skinRepository.updateInfoAboutUserSkinsForTrade(skinFrom.get().getId(), skinTo.get().getUserId());
                                skinRepository.updateInfoAboutUserSkinsForTrade(skinTo.get().getId(), skinFrom.get().getUserId());
                                log.info("Successfully trade!");
                                return new ResponseEntity<>(HttpStatus.CREATED);
                            }
                            throw new ForbiddenException("Skin doesn't belong to this user, ID: " + userTo.get().getUserId());
                        }
                        log.error("You are trying to teade the same skin");
                        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                    }
                    throw new ForbiddenException("Different cost!, ID: " + userTo.get().getUserId());
                }
                throw new ForbiddenException("UserTo is blocked, ID: " + userTo.get().getUserId());
            }
            log.error("Invalid userIdTO, ID: " + user.getUserIdTo());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        log.error("Invalid skinID , ID 1: " + user.getSkinIdTo() + ", ID 2: " + user.getSkinIdFrom());
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
}