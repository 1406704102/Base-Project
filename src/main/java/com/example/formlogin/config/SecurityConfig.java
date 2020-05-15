package com.example.formlogin.config;

import com.example.formlogin.module.system.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.io.PrintWriter;

@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    UserService userService;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userService);
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        //不加密
        return NoOpPasswordEncoder.getInstance();
    }

    /*    //在内存中设置用户
        @Override
        protected void configure(AuthenticationManagerBuilder auth) throws Exception {
            auth.inMemoryAuthentication()
                    .withUser("pangjie")//用户名
                    .password("333")//密码
                    .roles("admin")
                    .and()
                    .withUser("jjj")
                    .password("444")
                    .roles("user");
        }*/

/*    @Autowired
    DataSource dataSource;
    //在内存中设置用户
    @Override
    @Bean
    protected UserDetailsService userDetailsService() {
//        内存
//        InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();
//        manager.createUser(User.withUsername("ppp").password("333").roles("admin").build());
//        manager.createUser(User.withUsername("jjj").password("444").roles("user").build());
//        数据库
        JdbcUserDetailsManager manager = new JdbcUserDetailsManager(dataSource);
        if (!manager.userExists("ppp")) {
            manager.createUser(User.withUsername("ppp").password("333").roles("admin").build());
        }
        if (!manager.userExists("jjj")) {
            manager.createUser(User.withUsername("jjj").password("444").roles("admin").build());
        }
        return manager;
    }*/

    //静态资源不过滤在这里配置
    @Override
    public void configure(WebSecurity web) throws Exception {
        super.configure(web);
    }

    //admin角色可以访问user角色的资源
    @Bean
    RoleHierarchy roleHierarchy(){
        RoleHierarchyImpl roleHierarchy = new RoleHierarchyImpl();
        roleHierarchy.setHierarchy("ROLE_admin > ROLE_user");
        return roleHierarchy;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/admin/**").hasRole("admin")
                .antMatchers("/user/**").hasRole("user")
                .anyRequest().authenticated() //只能在最后
                .and()
                .formLogin()
//                .loginPage("/login.html")
                .loginProcessingUrl("/doLogin")//不写 就是默认的("/login.html")和loginPage同名
//                .usernameParameter("name")//参数的名称 默认是username
//                .passwordParameter("pass")//参数的名称 默认是password\
                // --- 前后不分离
//                .successForwardUrl("/successLogin")//登陆成功的服务端跳转 默认是"/" 前后端部分
//                .defaultSuccessUrl("/successLogin",true)//同successForwardUrl
//                .defaultSuccessUrl("/successLogin")//重定向 到之前被拦截 的页面
//                .failureForwardUrl("")//登录失败服务端跳转
//                .failureUrl("")//登录失败重定向
                // --- 前后分离返回json数据
                // 登陆回调成功返回登录用户信息
                .successHandler((req, resp, authentication) -> {
                    resp.setContentType("application/json;charset=utf-8");
                    PrintWriter writer = resp.getWriter();
                    writer.write(new ObjectMapper().writeValueAsString(authentication.getPrincipal()));
                    writer.flush();
                    writer.close();
                })
                //失败回调
                .failureHandler((req, resp, exception) -> {
                    resp.setContentType("application/json;charset=utf-8");
                    PrintWriter writer = resp.getWriter();
                    writer.write(new ObjectMapper().writeValueAsString(exception.getMessage()));
                    writer.flush();
                    writer.close();
                })
                .permitAll()
                .and()
                .logout()
//                .logoutUrl("/logout")//get请求登录 的注销地址
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout", "POST"))
//                .logoutSuccessUrl("/login.html")//注销成功后去哪个 页面 前后不分
                //前后分离
                .logoutSuccessHandler((req, resp, authentication) -> {
                    resp.setContentType("application/json;charset=utf-8");
                    PrintWriter writer = resp.getWriter();
                    writer.write(new ObjectMapper().writeValueAsString(authentication.getPrincipal() + "注销成功"));
                    writer.flush();
                    writer.close();
                })
                .invalidateHttpSession(true)//清除session 默认就是true
                .clearAuthentication(true)//清除认证信息 默认就是true
                .permitAll()
                .and()
                .csrf().disable()
                // --- 用户未认证
                .exceptionHandling()
                .authenticationEntryPoint((req, resp, exception) -> {
                    resp.setContentType("application/json;charset=utf-8");
                    PrintWriter writer = resp.getWriter();
                    writer.write(new ObjectMapper().writeValueAsString("未登录!"));
                    writer.flush();
                    writer.close();
                });
    }
}
