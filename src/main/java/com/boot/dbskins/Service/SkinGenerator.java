package com.boot.dbskins.Service;

import com.boot.dbskins.Model.NameOfSkins;
import com.boot.dbskins.Model.Skins;
import com.boot.dbskins.Model.TypeOfSkins;
import com.boot.dbskins.Repository.SkinRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.random.RandomGenerator;

@Component
@Slf4j
public class SkinGenerator {
    private final RandomGenerator random = RandomGenerator.getDefault();
    private final SkinRepository skinRepository;

    public SkinGenerator(SkinRepository skinRepository) {
        this.skinRepository = skinRepository;
    }

    public Skins generateRandomSkin() {
        Skins skins = new Skins();
        skins.setSkinType(getRandomSkinType());
        skins.setCost(getRandomCost());
        skins.setFloatOfSkin(getRandomFloat());
        skins.setSkinName(getRandomSkinName());
        skinRepository.save(skins);
        log.info("New skin create!");
        return skins;
    }

    private Integer getRandomCost() {
        return random.nextInt(1, 20);
    }

    private Float getRandomFloat() {
        return random.nextFloat() * 1;
    }

    private TypeOfSkins getRandomSkinType() {
        TypeOfSkins[] skinTypes = TypeOfSkins.values();
        return skinTypes[random.nextInt(0, skinTypes.length)];
    }

    private NameOfSkins getRandomSkinName() {
        NameOfSkins[] skinName = NameOfSkins.values();
        return skinName[random.nextInt(0, skinName.length)];
    }
}
