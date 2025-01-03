package com.laioffer.twitch.db;

import com.laioffer.twitch.db.entity.UserEntity;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.ListCrudRepository;

import java.util.List;

public interface UserRepository extends ListCrudRepository<UserEntity, Long> {

    List<UserEntity> findByLastName(String lastName);

    List<UserEntity> findByFirstName(String firstName);

    // username是不重复的，所以可以不用list
    UserEntity findByUsername(String username);

    // 改名
    @Modifying
    @Query("UPDATE users SET first_name = :firstName, last_name = :lastName WHERE username = :username")
    void updateNameByUsername(String username, String firstName, String lastName);
}
