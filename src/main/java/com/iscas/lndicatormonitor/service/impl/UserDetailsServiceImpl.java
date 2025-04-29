package com.iscas.lndicatormonitor.service.impl;

import com.iscas.lndicatormonitor.domain.Users;
import com.iscas.lndicatormonitor.service.UsersService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(UserDetailsServiceImpl.class);

    @Autowired
    private UsersService usersService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        logger.info("Loading user by username: {}", username);
        
        try {
            Users user = usersService.selectByUserName(username);
            
            if (user == null) {
                logger.warn("User not found with username: {}", username);
                throw new UsernameNotFoundException("User not found with username: " + username);
            }
            
            logger.info("User found: {}", user.getUsername());
            
            // 创建一个Spring Security的UserDetails对象
            return new User(
                user.getUsername(),
                user.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
            );
        } catch (Exception e) {
            logger.error("Error loading user: {}", e.getMessage(), e);
            throw new UsernameNotFoundException("Error loading user: " + e.getMessage(), e);
        }
    }
} 