package com.iscas.lndicatormonitor.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.iscas.lndicatormonitor.annotation.OperationLog;
import com.iscas.lndicatormonitor.common.CustomResult;
import com.iscas.lndicatormonitor.domain.HistoryPwd;
import com.iscas.lndicatormonitor.domain.Loginattempt;
import com.iscas.lndicatormonitor.domain.Role;
import com.iscas.lndicatormonitor.domain.Users;
import com.iscas.lndicatormonitor.dto.LoginRes;
import com.iscas.lndicatormonitor.dto.UsersDTO;
import com.iscas.lndicatormonitor.dto.UserQueryCriteria;
import com.iscas.lndicatormonitor.service.*;
import com.iscas.lndicatormonitor.utils.JwtTokenUtil;
import com.iscas.lndicatormonitor.utils.LoginAttemptUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import javax.servlet.http.HttpServletRequest;
import java.util.Random;
import java.util.UUID;
import java.util.HashMap;
import java.util.Map;
import com.iscas.lndicatormonitor.utils.MD5Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RequestMapping("/user")
@RestController
public class UsersController {
    private static final Logger log = LoggerFactory.getLogger(UsersController.class);

    @Autowired
    UsersService usersService;

    @Autowired
    RoleService roleService;

    @Autowired
    HistoryPwdService historyPwdService;

    @Autowired
    LoginAttemptUtil loginAttemptUtil;
    @Autowired
    LoginattemptService loginattemptService;

    @Autowired
    TokenBlacklistService tokenBlacklistService;

    @Autowired
    JwtTokenUtil jwtTokenUtil;  

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    // 验证码可用字符
    private static final String CAPTCHA_CHARS = "1234567890abcdefghijklmnopqrstuvwxyz";
    // 验证码长度
    private static final int CAPTCHA_LENGTH = 4;
    // 验证码过期时间（秒）
    private static final int CAPTCHA_EXPIRE_SECONDS = 300;
    
    @PostMapping("/loginnew")
    @OperationLog("用户登录")
    public CustomResult userLoginNew(@RequestBody UsersDTO usersDTO, HttpServletRequest request) throws Exception {
        JwtTokenUtil jwtTokenUtil = new JwtTokenUtil();
        try {
            // 验证码校验
            String captchaId = request.getHeader("Captcha-Id"); // 从请求头获取验证码ID
            if (captchaId == null || usersDTO.getCaptcha() == null) {
                return new CustomResult(40000, "验证码不能为空", null);
            }
            
            // 从Redis获取验证码
            String storedCaptcha = (String) redisTemplate.opsForValue().get("captcha:" + captchaId);
            
            // 验证码不存在或已过期
            if (storedCaptcha == null) {
                return new CustomResult(40000, "验证码已过期或不存在", null);
            }
            
            // 验证码校验（不区分大小写）
            if (!storedCaptcha.equalsIgnoreCase(usersDTO.getCaptcha())) {
                log.warn("验证码错误，输入：{}，正确：{}", usersDTO.getCaptcha(), storedCaptcha);
                return new CustomResult(40000, "验证码错误", null);
            }
            
            // 验证通过后，删除Redis中的验证码，防止重复使用
            redisTemplate.delete("captcha:" + captchaId);
            
            // 继续执行原来的登录逻辑
            Users loginResult = usersService.userLogin(usersDTO);
            if (loginResult != null && loginResult.getId() > 0) {
                // 检查 Redis 中当前用户的活跃会话数
                Long currentSessions = redisTemplate.opsForList().size("active_sessions:" + loginResult.getId());
                System.out.println(currentSessions);
                if (currentSessions >= 5) { // 假设最大会话数为 5
                    return new CustomResult(40000, "达到最大会话数", null);
                }

                // 生成 Token
                String token = jwtTokenUtil.generateToken(usersDTO.getUsername(), usersDTO.getId());

                // 将 Token 添加到 Redis
                redisTemplate.opsForList().leftPush("active_sessions:" + loginResult.getId(), token);
                redisTemplate.expire("active_sessions:" + loginResult.getId(), 1, TimeUnit.HOURS); // 设置过期时间为1小时
                BeanUtils.copyProperties(loginResult,usersDTO);
                Role role =  roleService.selectByUserId(loginResult.getId());
                usersDTO.setReal_name(loginResult.getRealName());
                usersDTO.setPassword("xxxxxx");
                usersDTO.setRole(role.getRoleType());
                LoginRes loginRes = new LoginRes();
                loginRes.setUsersDTO(usersDTO);
                loginRes.setToken(token);
                return new CustomResult(20000,"登录成功",loginRes);
            } else {
                Users user =  usersService.selectByUserName(usersDTO.getUsername());
                // 处理登录失败的情况
                if (loginResult != null && loginAttemptUtil.isLocked(user.getId())){
                    // 处于被锁定的状态
                   Loginattempt loginattempt =  loginattemptService.selectByUserId(user.getId());
                    // 获取当前时间
                    Date now = new Date();
                    // 获取 LockUntil 时间
                    Date lockUntil = loginattempt.getLockUntil();
                    // 计算时间差异（以毫秒为单位）
                    long diffInMillies = Math.abs(lockUntil.getTime() - now.getTime());
                    return new CustomResult(40000,"账户已被锁定，请于"+TimeUnit.MINUTES.convert(diffInMillies, TimeUnit.MILLISECONDS)+"分钟后再试",5);
                }else {
                    if (loginResult == null){
                        // 用户名不正确
                        return new CustomResult(40000,"登录失败",null);
                    }else {
                        // 密码不正确
                        loginAttemptUtil.handleLoginError(user.getId());
                        Loginattempt loginattempt =  loginattemptService.selectByUserId(user.getId());
                        return new CustomResult(40000,"登录失败",loginattempt.getLoginAttempts());
                    }
                }
            }
        }catch (Exception e){
            return new CustomResult(40000,e.toString(),null);
        }
    }


    // @PostMapping("/login")
    // @OperationLog("用户登录")
    // public CustomResult userLogin(@RequestBody UsersDTO usersDTO, HttpServletRequest request) throws Exception {
    //     JwtTokenUtil jwtTokenUtil = new JwtTokenUtil();
    //     try {
    //         // 继续执行原来的登录逻辑
    //         Users loginResult = usersService.userLogin(usersDTO);
    //         if (loginResult != null && loginResult.getId() > 0) {
    //             // 检查 Redis 中当前用户的活跃会话数
    //             Long currentSessions = redisTemplate.opsForList().size("active_sessions:" + loginResult.getId());
    //             System.out.println(currentSessions);
    //             if (currentSessions >= 5) { // 假设最大会话数为 5
    //                 return new CustomResult(40000, "达到最大会话数", null);
    //             }
    //             // 生成 Token
    //             String token = jwtTokenUtil.generateToken(usersDTO.getUsername(), usersDTO.getId());

    //             // 将 Token 添加到 Redis
    //             redisTemplate.opsForList().leftPush("active_sessions:" + loginResult.getId(), token);
    //             redisTemplate.expire("active_sessions:" + loginResult.getId(), 1, TimeUnit.HOURS); // 设置过期时间为1小时
    //             BeanUtils.copyProperties(loginResult,usersDTO);
    //             Role role =  roleService.selectByUserId(loginResult.getId());
    //             usersDTO.setReal_name(loginResult.getRealName());
    //             usersDTO.setPassword("xxxxxx");
    //             usersDTO.setRole(role.getRoleType());
    //             LoginRes loginRes = new LoginRes();
    //             loginRes.setUsersDTO(usersDTO);
    //             loginRes.setToken(token);
    //             return new CustomResult(20000,"登录成功",loginRes);
    //         } else {
    //             Users user =  usersService.selectByUserName(usersDTO.getUsername());
    //             // 处理登录失败的情况
    //             if (loginResult != null && loginAttemptUtil.isLocked(user.getId())){
    //                 // 处于被锁定的状态
    //                Loginattempt loginattempt =  loginattemptService.selectByUserId(user.getId());
    //                 // 获取当前时间
    //                 Date now = new Date();
    //                 // 获取 LockUntil 时间
    //                 Date lockUntil = loginattempt.getLockUntil();
    //                 // 计算时间差异（以毫秒为单位）
    //                 long diffInMillies = Math.abs(lockUntil.getTime() - now.getTime());
    //                 return new CustomResult(40000,"账户已被锁定，请于"+TimeUnit.MINUTES.convert(diffInMillies, TimeUnit.MILLISECONDS)+"分钟后再试",5);
    //             }else {
    //                 if (loginResult == null){
    //                     // 用户名不正确
    //                     return new CustomResult(40000,"登录失败",null);
    //                 }else {
    //                     // 密码不正确
    //                     loginAttemptUtil.handleLoginError(user.getId());
    //                     Loginattempt loginattempt =  loginattemptService.selectByUserId(user.getId());
    //                     return new CustomResult(40000,"密码错误",loginattempt.getLoginAttempts());
    //                 }
    //             }
    //         }
    //     }catch (Exception e){
    //         return new CustomResult(40000,e.toString(),null);
    //     }
    // }

    @GetMapping("/logout")
    @OperationLog("用户登出")
    public CustomResult logout(String token) throws Exception {
//        if (token != null && token.startsWith("Bearer ")) {
//            token = token.substring(7);
//        }
        JwtTokenUtil jwtTokenUtil = new JwtTokenUtil();
        String username = jwtTokenUtil.getUsernameFromToken(token);
        Users user = usersService.selectByUserName(username);
        System.out.println(user);
        if (user != null) {
            // 从 Redis 中移除 Token
            redisTemplate.opsForList().remove("active_sessions:" + user.getId(), 0, token);
        }
        try{
            tokenBlacklistService.blacklistToken(token);
            return new CustomResult(20000,"注销成功！",null);
        }catch (Exception e){
            return new CustomResult(40000,"注销失败！",null);
        }
    }
    @PostMapping("/add")
    @OperationLog("添加用户")
    public CustomResult addUser(@RequestBody UsersDTO usersDTO) {
        try {
            // 检查用户名是否已存在
            if (usersService.isHaveCommonUser(usersDTO.getUsername())) {
                return new CustomResult(40000, "用户名已存在", null);
            }
            
            // 对默认密码"123456"进行处理
            String defaultPassword = "123456";
            String transformedPassword = transformPassword(defaultPassword);

            // 使用MD5加密
            MD5Utils md5Utils = new MD5Utils();
            String encryptedPassword = md5Utils.encrypted(transformedPassword);

            // 打印加密前后的密码，用于调试
            log.info("原始密码: {}", defaultPassword);
            log.info("变换后密码: {}", transformedPassword);
            log.info("加密后密码: {}", encryptedPassword);

            // 设置加密后的密码
            usersDTO.setPassword(encryptedPassword);
            
            // 创建新用户
            int userId = usersService.usersInsert(usersDTO);
            
            if (userId > 0) {
                // 为新用户分配角色
                Role role = new Role();
                role.setUserid(userId);
                role.setRoleType(usersDTO.getRole());
                roleService.insert(role);
                return new CustomResult(20000, "添加成功", null);
            } else {
                return new CustomResult(40000, "添加失败", null);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new CustomResult(40000, e.toString(), null);
        }
    }

    /**
     * 对密码进行与前端相同的字符变换
     * @param password 原始密码
     * @return 变换后的密码
     */
    private String transformPassword(String password) {
        StringBuilder transformedPassword = new StringBuilder();
        for (int i = 0; i < password.length(); i++) {
            char c = password.charAt(i);
            
            if (c == 'Z') {
                transformedPassword.append('A');
            } else if (c == 'z') {
                transformedPassword.append('a');
            } else if (c == '9') {
                transformedPassword.append('0');
            } else if ((c >= '0' && c <= '9') || 
                      (c >= 'A' && c <= 'Z') || 
                      (c >= 'a' && c <= 'z')) {
                // 数字和字母ASCII码+1
                transformedPassword.append((char)(c + 1));
            } else {
                // 其他字符不变
                transformedPassword.append(c);
            }
        }
        return transformedPassword.toString();
    }

    @GetMapping("/getAllUser")
    @OperationLog("获取用户列表")
    public CustomResult getAllUser() throws Exception {
        try {
            List<UsersDTO> usersDTOS = usersService.getAllUsersDto();
            return new CustomResult(20000,"获取成功",usersDTOS);
        }catch (Exception e){
            return new CustomResult(40000,"获取失败",null);
        }
    }
    @GetMapping("/deleteUser")
    @OperationLog("删除用户")
    public CustomResult deleteUser(@RequestParam(value = "userId", required = false) Integer userId) throws Exception {
        // 检查 userId 是否为 null
        if (userId == null) {
            return new CustomResult(40000, "用户ID未提供", null);
        }

        System.out.println(userId);
        int result = usersService.deleteById(userId);
        if (result == 1) {
            return new CustomResult(20000, "删除成功！", null);
        } else {
            return new CustomResult(40000, "删除失败！", null);
        }
    }

    @PostMapping("/alterUser")
    @OperationLog("更新用户信息")
    public CustomResult alterUser(@RequestBody UsersDTO usersDTO) throws Exception {
        int result =  usersService.updateUser(usersDTO);
        if (result == 1){
            return new CustomResult(20000,"更新成功！",null);
        }else {
            return new CustomResult(40000,"更新失败！",null);
        }
    }

    // 判断是否是第一次登录
    @GetMapping("/isFirstLogin")
    @OperationLog("判断是否是第一次登录")
    public CustomResult isFirstLogin(int userId){
       List<HistoryPwd> historyPwdList = historyPwdService.selectLastFiveByUserId(userId);
       if (historyPwdList.size() == 0){
           return new CustomResult(20000,"第一次登录",null);
       }else {
           return new CustomResult(30000,"第n次登录",null);
       }
    }

    // 判断当前密码是否超过180d
    @GetMapping("/isPwdPastDue")
    @OperationLog("判断当前密码是否超过180d")
    public CustomResult isPwdPastDue(int userId){
        HistoryPwd lastHistoryPwd = historyPwdService.selectLastFiveByUserId(userId).get(0);
        boolean isExpired = isPasswordExpired(lastHistoryPwd);
        if (isExpired){
            return new CustomResult(40000,"已过期",null);
        }else {
            return new CustomResult(20000,"未过期",null);
        }
    }

    // 修改密码
    @PostMapping("/updatePwd")
    @OperationLog("修改密码")
    public CustomResult updatePwd(@RequestBody UsersDTO usersDTO, HttpServletRequest request) throws Exception {
        // 获取当前登录用户的ID
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        
        String username = jwtTokenUtil.getUsernameFromToken(token);
        Users currentUser = usersService.selectByUserName(username);
        
        // 确保用户只能修改自己的密码
        if (currentUser == null || !currentUser.getId().equals(usersDTO.getId())) {
            return new CustomResult(40000, "只能修改自己的密码", null);
        }
        
        System.out.println(usersDTO);
        Integer result = usersService.updatePwd(usersDTO);
        if (result == 1){
            return new CustomResult(20000, "修改成功", null);
        } else if (result == 2) {
            return new CustomResult(40000, "不能与当前密码重复", null);
        } else if (result == 3) {
            return new CustomResult(40000, "不能与最近历史修改记录重复", null);
        }
        return new CustomResult(40000, "修改失败", null);
    }
    public boolean isPasswordExpired(HistoryPwd lastHistoryPwd) {
        // 将 Date 类型的 changedate 转换为 LocalDate
        LocalDate changeDate = lastHistoryPwd.getChangedate().toInstant()
                .atZone(ZoneId.systemDefault()).toLocalDate();

        // 获取当前日期
        LocalDate currentDate = LocalDate.now();

        // 计算两个日期之间的天数差异
        long daysBetween = java.time.temporal.ChronoUnit.DAYS.between(changeDate, currentDate);

        // 检查是否超过了180天
        return daysBetween > 180;
    }

    @PostMapping("/listByPage")
    @OperationLog("分页查询用户")
    public CustomResult listUsersByPage(@RequestBody UserQueryCriteria criteria) {
        try {
            IPage<UsersDTO> userPage = usersService.getUsersByPage(criteria);
            return new CustomResult(20000, "查询成功", userPage);
        } catch (Exception e) {
            e.printStackTrace();
            return new CustomResult(40000, "查询失败: " + e.getMessage(), null);
        }
    }

    /**
     * 生成验证码
     * @return 包含验证码字符串和验证码ID的响应
     */
    @GetMapping("/generateCaptcha")
    @OperationLog("生成验证码")
    public CustomResult generateCaptcha() {
        try {
            // 生成随机验证码
            String captchaCode = generateRandomCode(CAPTCHA_LENGTH);
            
            // 生成唯一ID用于后续验证
            String captchaId = UUID.randomUUID().toString();
            
            // 将验证码存入Redis，设置过期时间
            redisTemplate.opsForValue().set("captcha:" + captchaId, captchaCode, CAPTCHA_EXPIRE_SECONDS, TimeUnit.SECONDS);
            
            // 构建返回结果
            Map<String, String> result = new HashMap<>();
            result.put("captchaId", captchaId);
            result.put("captchaCode", captchaCode);
            
            return new CustomResult(20000, "验证码生成成功", result);
        } catch (Exception e) {
            e.printStackTrace();
            return new CustomResult(40000, "验证码生成失败", null);
        }
    }
    
    /**
     * 校验验证码
     * @param captchaId 验证码ID
     * @param captchaCode 用户输入的验证码
     * @return 校验结果
     */
    @GetMapping("/verifyCaptcha")
    @OperationLog("校验验证码")
    public CustomResult verifyCaptcha(@RequestParam String captchaId, @RequestParam String captchaCode) {
        try {
            // 从Redis获取验证码
            String storedCaptcha = (String) redisTemplate.opsForValue().get("captcha:" + captchaId);
            
            // 验证码不存在或已过期
            if (storedCaptcha == null) {
                return new CustomResult(40000, "验证码已过期或不存在", false);
            }
            
            // 验证码校验（不区分大小写）
            boolean isValid = storedCaptcha.equalsIgnoreCase(captchaCode);
            
            // 无论验证是否成功，都删除已使用的验证码
            redisTemplate.delete("captcha:" + captchaId);
            
            if (isValid) {
                return new CustomResult(20000, "验证码校验成功", true);
            } else {
                return new CustomResult(40000, "验证码错误", false);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new CustomResult(40000, "验证码校验失败", false);
        }
    }
    
    /**
     * 生成随机验证码
     * @param length 验证码长度
     * @return 随机验证码
     */
    private String generateRandomCode(int length) {
        String chars = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(chars.length());
            sb.append(chars.charAt(index));
        }
        
        return sb.toString();
    }
}