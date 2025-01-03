package com.laioffer.twitch.user;

import com.laioffer.twitch.db.UserRepository;
import com.laioffer.twitch.db.entity.UserEntity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    // 三个dependencies
    private final UserDetailsManager userDetailsManager;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    public UserService(UserDetailsManager userDetailsManager, PasswordEncoder passwordEncoder, UserRepository userRepository) {
        this.userDetailsManager = userDetailsManager;
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
    }

    @Transactional // 有多个写操作，中间出错还可以撤回
    public void register(String username, String password, String firstName, String lastName) {
        UserDetails user = User.builder() // 它自己的api，builder pattern。为什么不直接new？因为看不出来顺序对不对（比如有30个string的参数），可读性差，维护性差
                .username(username)
                .password(passwordEncoder.encode(password))
                .roles("USER") // authentication table
                .build();
        userDetailsManager.createUser(user); // 创建user
        userRepository.updateNameByUsername(username, firstName, lastName); // 让Spring Boot更新一下
    }

    public UserEntity findByUsername(String username) { // 通过username查找userentity
        return userRepository.findByUsername(username);
    }
}