package com.cskaoyan.user.controller;

import com.cskaoyan.mall.commons.constant.SysRetCodeConstants;
import com.cskaoyan.mall.commons.result.ResponseData;
import com.cskaoyan.mall.commons.result.ResponseUtil;
import com.cskaoyan.mall.commons.util.CookieUtil;
import com.cskaoyan.user.dto.KaptchaCodeRequest;
import com.cskaoyan.user.dto.KaptchaCodeResponse;
import com.cskaoyan.user.dto.UserRegisterRequest;
import com.cskaoyan.user.dto.UserRegisterResponse;
import com.cskaoyan.user.service.IKaptchaService;
import com.cskaoyan.user.service.IRegisterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
