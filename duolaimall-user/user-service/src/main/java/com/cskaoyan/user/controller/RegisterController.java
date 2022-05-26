package com.cskaoyan.user.controller;

import com.cskaoyan.mall.commons.constant.SysRetCodeConstants;
import com.cskaoyan.mall.commons.result.ResponseData;
import com.cskaoyan.mall.commons.result.ResponseUtil;
import com.cskaoyan.mall.commons.util.CookieUtil;
import com.cskaoyan.user.dto.*;
import com.cskaoyan.user.service.IKaptchaService;
import com.cskaoyan.user.service.IRegisterService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class RegisterController {
    @Autowired
    private IKaptchaService iKaptchaService;

    @Autowired
    private IRegisterService iRegisterService;

    @PostMapping("/register")
    public ResponseData register(@RequestBody Map<String, String> map, HttpServletRequest request) {
        String userName = map.get("userName");
        String userPwd = map.get("userPwd");
        String captcha = map.get("captcha");
        String email = map.get("email");
        // 验证验证码
        String uuid = CookieUtil.getCookieValue(request, "kaptcha_uuid");
        KaptchaCodeRequest kaptchaCodeRequest = new KaptchaCodeRequest();
        kaptchaCodeRequest.setUuid(uuid);
        kaptchaCodeRequest.setCode(captcha);
        KaptchaCodeResponse response = iKaptchaService.validateKaptchaCode(kaptchaCodeRequest);
        if (!response.getCode().equals(SysRetCodeConstants.SUCCESS.getCode())){
            return  new ResponseUtil<>().setErrorMsg(response.getMsg());
        }
        // 向用户表插入记录
        UserRegisterRequest registerRequest = new UserRegisterRequest();
        registerRequest.setUserName(userName);
        registerRequest.setEmail(email);
        registerRequest.setUserPwd(userPwd);
        UserRegisterResponse registerResponse = iRegisterService.register(registerRequest);

        if (registerResponse.getCode().equals(SysRetCodeConstants.SUCCESS.getCode())) {
            return new ResponseUtil<>().setData(null);
        }
        return new ResponseUtil<>().setErrorMsg(registerResponse.getMsg());
    }

    @GetMapping("/verify")
    public ResponseData register(@RequestParam String uuid, @RequestParam String username, HttpServletRequest request){
        if(!(StringUtils.isNotBlank(uuid) &&  StringUtils.isNotBlank(username))){
            return new ResponseUtil<>().setErrorMsg("注册序号/用户名不允许为空");
        }
        UserVerifyRequest userVerifyRequest = new UserVerifyRequest();
        userVerifyRequest.setUserName(username);
        userVerifyRequest.setUuid(uuid);
        UserVerifyResponse userVerifyResponse = iRegisterService.verifyMemer(userVerifyRequest);
        if(userVerifyResponse.getCode().equals(SysRetCodeConstants.SUCCESS.getCode())) {
            return new ResponseUtil().setData(null);
        }else{
            return new ResponseUtil().setData(userVerifyResponse.getMsg());
        }
    }
}
