package com.example.formlogin.module.system.service;

import com.example.formlogin.module.system.dao.UserDao;
import com.example.formlogin.module.system.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserService implements UserDetailsService {


    @Autowired
    UserDao userDao;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User userByUsername = userDao.findUserByUsername(username);
        if (userByUsername == null) {
            throw new UsernameNotFoundException("用户不存在");
        }
        return userByUsername;
    }
}
