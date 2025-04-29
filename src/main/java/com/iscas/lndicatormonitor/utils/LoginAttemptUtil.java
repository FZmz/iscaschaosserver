package com.iscas.lndicatormonitor.utils;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Component
public class LoginAttemptUtil {
    private static final int MAX_ATTEMPT = 5;
    private static final long BLOCK_DURATION_MINUTES = 30;
    
    // 用户名 -> 失败次数的映射
    private static final Map<String, Integer> attemptsCache = new HashMap<>();
    
    // 用户名 -> 锁定截止时间的映射
    private static final Map<String, Long> blockCache = new HashMap<>();
    
    /**
     * 检查用户是否被锁定
     * @param username 用户名
     * @return 是否被锁定
     */
    public static boolean isBlocked(String username) {
        Long blockedUntil = blockCache.get(username);
        if (blockedUntil == null) {
            return false;
        }
        
        boolean stillBlocked = System.currentTimeMillis() < blockedUntil;
        
        // 如果锁定时间已过，清除锁定记录
        if (!stillBlocked) {
            blockCache.remove(username);
            attemptsCache.remove(username);
        }
        
        return stillBlocked;
    }
    
    /**
     * 记录登录失败
     * @param username 用户名
     */
    public static void loginFailed(String username) {
        int attempts = attemptsCache.getOrDefault(username, 0);
        attempts++;
        attemptsCache.put(username, attempts);
        
        if (attempts >= MAX_ATTEMPT) {
            // 锁定用户
            long blockUntil = System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(BLOCK_DURATION_MINUTES);
            blockCache.put(username, blockUntil);
        }
    }
    
    /**
     * 登录成功，重置计数
     * @param username 用户名
     */
    public static void loginSucceeded(String username) {
        attemptsCache.remove(username);
        blockCache.remove(username);
    }
    
    /**
     * 获取用户剩余的登录尝试次数
     * @param username 用户名
     * @return 剩余尝试次数
     */
    public static int getRemainingAttempts(String username) {
        int attempts = attemptsCache.getOrDefault(username, 0);
        return Math.max(0, MAX_ATTEMPT - attempts);
    }
    
    /**
     * 检查用户ID是否被锁定
     * @param userId 用户ID
     * @return 是否被锁定
     */
    public boolean isLocked(Integer userId) {
        // 这里需要通过userId查找对应的username
        // 简化实现，实际应该通过数据库查询
        return false;
    }
    
    /**
     * 处理登录错误
     * @param userId 用户ID
     */
    public void handleLoginError(Integer userId) {
        // 这里需要通过userId查找对应的username，然后调用loginFailed
        // 简化实现，实际应该通过数据库查询
    }
}
