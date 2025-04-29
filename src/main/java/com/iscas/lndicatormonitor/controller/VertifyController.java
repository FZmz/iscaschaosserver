package com.iscas.lndicatormonitor.controller;

import com.iscas.lndicatormonitor.annotation.OperationLog;
import com.iscas.lndicatormonitor.common.CustomResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Random;

@RestController
@RequestMapping("/vertify")
public class VertifyController {

    /*
     * 随机生成4位数验证码 包含数字和字母
     */
    @GetMapping("generateCode")
    @OperationLog("生成验证码")
    public CustomResult generateCode() {
        // 定义验证码字符空间
        String charSpace = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder code = new StringBuilder();
        Random random = new Random();

        // 生成4位验证码
        for (int i = 0; i < 4; i++) {
            int index = random.nextInt(charSpace.length()); // 生成一个随机索引
            code.append(charSpace.charAt(index)); // 根据索引在字符空间中选择一个字符
        }

        // 返回包含验证码的CustomResult对象
        return new CustomResult(20000, "success", code.toString());
    }
}
