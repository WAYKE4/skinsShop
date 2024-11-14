package com.boot.dbskins.Repository;

import com.boot.dbskins.Model.Skins;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SkinRepository extends JpaRepository<Skins, Long> {

    @Query(nativeQuery = true, value = "SELECT * FROM skins WHERE user_id = :userId")
    Optional<List<Skins>> customGetAllSkinsByUserId(@Param("userId") Long userId);

    @Query(nativeQuery = true, value = "SELECT * FROM skins WHERE user_id IS NULL ORDER BY id")
    Optional<List<Skins>> customGetAllSkinsAndSortById();

    @Query(nativeQuery = true, value = "SELECT * FROM skins WHERE user_id IS NULL AND id = :skinId")
    Optional<Skins> findSkinForBuy(@Param("skinId") Long id);

    @Modifying
    @Query(nativeQuery = true, value = "UPDATE skins SET user_id=:userId WHERE id=:skinId")
    void updateInfoAboutUserSkinsForBuy(@Param("userId") Long userId, @Param("skinId") Long skinId);

    @Query(nativeQuery = true, value = "SELECT * FROM skins WHERE user_id IS NOT NULL AND id = :skinId")
    Optional<Skins> findSkinForSell(@Param("skinId") Long id);

    @Modifying
    @Query(nativeQuery = true, value = "UPDATE skins SET user_id = NULL WHERE id=:skinId")
    void updateInfoAboutUserSkinsForSell(@Param("skinId") Long skinId);

    @Modifying
    @Query(nativeQuery = true, value = "UPDATE skins SET user_id = :userId WHERE id=:skinId")
    void updateInfoAboutUserSkinsForTrade(@Param("skinId") Long skinId, @Param("userId") Long userId);
}