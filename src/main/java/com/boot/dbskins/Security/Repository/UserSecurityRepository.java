package com.boot.dbskins.Security.Repository;


import com.boot.dbskins.Security.Model.UserSecurity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserSecurityRepository extends JpaRepository<UserSecurity, Long> {

    Optional<UserSecurity> findByUserLogin(String userLogin);

    Optional<UserSecurity> findByUserId(Long id);

    @Modifying
    @Query(nativeQuery = true, value = "UPDATE user_security SET user_password =:newPassword WHERE user_id =:userId")
    int updateInfoAboutUserPassword(@Param("userId") Long userId, @Param("newPassword") String newPassword);

    @Modifying
    @Query(nativeQuery = true, value = "UPDATE user_security SET user_login =:newUserLogin WHERE user_id =:userId")
    int updateInfoAboutUserLogin(@Param("userId") Long userId, @Param("newUserLogin") String newUserLogin);

    @Modifying
    @Query(nativeQuery = true, value = "UPDATE user_security SET role =:newRole WHERE user_id =:userId")
    int updateInfoAboutUserRole(@Param("userId") Long userId, @Param("newRole") String role);

    @Modifying
    @Query(nativeQuery = true, value = "UPDATE user_security SET is_blocked =:newBlock WHERE user_id =:userId")
    int updateInfoAboutUserBlock(@Param("userId") Long userId, @Param("newBlock") Boolean newBlock);
}