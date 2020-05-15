package com.example.formlogin;

import com.example.formlogin.module.system.dao.UserDao;
import com.example.formlogin.module.system.model.Role;
import com.example.formlogin.module.system.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
class FormloginApplicationTests {

    @Autowired
    UserDao userDao;

    @Test
    void contextLoads() {
        User user1 = new User();
        user1.setUsername("ppp");
        user1.setPassword("123");
        List<Role> roles1 = new ArrayList<>();
        Role role1 = new Role();
        role1.setName("ROLE_admin");
        role1.setNameZh("管理员");
        roles1.add(role1);
        user1.setRoles(roles1);
        user1.setAccountNonExpired(true);
        user1.setAccountNonLocked(true);
        user1.setCredentialsNonExpired(true);
        user1.setEnabled(true);
        userDao.save(user1);

        User user2 = new User();
        user2.setUsername("ppp");
        user2.setPassword("123");
        user2.setAccountNonExpired(true);
        user2.setAccountNonLocked(true);
        user2.setCredentialsNonExpired(true);
        user2.setEnabled(true);
        List<Role> roles2 = new ArrayList<>();
        Role role2 = new Role();
        role2.setName("ROLE_user");
        role2.setNameZh("用户");
        roles2.add(role2);
        user2.setRoles(roles2);
        userDao.save(user2);
    }

}
