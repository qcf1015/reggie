package com.text.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.text.common.R;
import com.text.entity.User;
import com.text.service.UserService;
import com.text.util.SMSUtils;
import com.text.util.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Map;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 发送验证码
     * @param user
     * @param session
     * @return
     */
    @PostMapping("/sendMsg")
    public R<String> sendMsg(@RequestBody User user, HttpSession session){
//        获取手机号
        String phone = user.getPhone();
//        尽心判断
        if (StringUtils.isNotEmpty(phone)){
 //        生成验证码
            String code = ValidateCodeUtils.generateValidateCode(6).toString();
            log.info("code:{}",code);
//        发送验证码
            SMSUtils.sendMessage("reggie","code",phone,code);
//        保存
            session.setAttribute(phone,code);
            return R.success("发送成功");
        }
        return R.error("发送失败");
    }

    @PostMapping("/login")
    public R<User> login(@RequestBody Map map,HttpSession session){
//        获取手机号
        String phone = map.get("phone").toString();
//        获取验证码
        String code = map.get("code").toString();
//        验证码比对
        Object codeSession = session.getAttribute(phone);
        if (codeSession != null && codeSession.equals(code)){
            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(User::getPhone,phone);
            User user = userService.getOne(queryWrapper);
            if (user == null){
                user = new User();
                user.setPhone(phone);
                user.setStatus(1);
                userService.save(user);
            }
            return R.success(user);
        }
        return R.error("登陆失败");
    }
}
