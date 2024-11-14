package com.boot.dbskins.Repository;

import com.boot.dbskins.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findUserById(Long id);

    Optional<User> findUserByEmail(String email);

    @Modifying
    @Query(nativeQuery = true, value = "UPDATE users SET balance=:balance WHERE id =:userId")
    int updateInfoAboutUser(@Param("userId") Long userId, @Param("balance") Integer balance);

    @Modifying
    @Query(nativeQuery = true, value = "UPDATE users SET email =:newEmail WHERE id =:userId")
    int updateInfoAboutUserEmail(@Param("userId") Long userId, @Param("newEmail") String newEmail);
}