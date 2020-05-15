package com.example.formlogin.module.system.dao;

import com.example.formlogin.module.system.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserDao extends JpaRepository<User, Long> {
    User findUserByUsername(String username);
}
