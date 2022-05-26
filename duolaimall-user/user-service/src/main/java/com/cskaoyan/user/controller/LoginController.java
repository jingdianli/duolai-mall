package com.cskaoyan.user.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.cskaoyan.mall.commons.constant.SysRetCodeConstants;
import com.cskaoyan.mall.commons.result.ResponseData;
import com.cskaoyan.mall.commons.result.ResponseUtil;
import com.cskaoyan.mall.commons.util.CookieUtil;
import com.cskaoyan.user.constants.UserRetCode;
import com.cskaoyan.user.dto.KaptchaCodeRequest;
import com.cskaoyan.user.dto.KaptchaCodeResponse;
import com.cskaoyan.user.dto.UserLoginRequest;
import com.cskaoyan.user.dto.UserLoginResponse;
import com.cskaoyan.user.service.IKaptchaService;
import com.cskaoyan.user.service.ILoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * @author Jingdian Li
 * @since 2022/05/27 05:11
 */

@RestController
@RequestMapping("/user")
public class LoginController {
    @Autowired
    ILoginService iLoginService;

    @Autowired
    IKaptchaService kaptchaService;

    @Value("${captchaFlag}")
    private boolean captchaFlag;

    @PostMapping(value = "/login")
    public ResponseData login(@RequestBody Map<String, String> map, HttpServletRequest request,
        HttpServletResponse response) {
        UserLoginRequest loginRequest = new UserLoginRequest();
        loginRequest.setPassword(map.get("userPwd"));
        loginRequest.setUserName(map.get("userName"));
        String captcha = map.get("captcha");

        if (captchaFlag) {
            KaptchaCodeRequest kaptchaCodeRequest = new KaptchaCodeRequest();
            String uuid = CookieUtil.getCookieValue(request, "kaptcha_uuid");
            kaptchaCodeRequest.setCode(captcha);
            kaptchaCodeRequest.setUuid(uuid);
            KaptchaCodeResponse kaptchaCodeResponse = kaptchaService.validateKaptchaCode(kaptchaCodeRequest);
            if (!kaptchaCodeResponse.getCode().equals(SysRetCodeConstants.SUCCESS.getCode())) {
                return new ResponseUtil<>().setErrorMsg(kaptchaCodeResponse.getMsg());
            }
        }
        UserLoginResponse userLoginResponse = iLoginService.login(loginRequest);
        if (userLoginResponse.getCode().equals(SysRetCodeConstants.SUCCESS.getCode())) {
            Cookie cookie = CookieUtil.genCookie("access_token", userLoginResponse.getToken(), "/", 24 * 60 * 60);
            cookie.setHttpOnly(true);
            response.addCookie(cookie);
            return new ResponseUtil().setData(userLoginResponse);
        }
        return new ResponseUtil().setErrorMsg(userLoginResponse.getMsg());
    }

    @GetMapping("/login")
    public ResponseData checkLogin(HttpServletRequest request) {
        String userInfo = (String)request.getAttribute("access_token");
        Object object = JSON.parse(userInfo);
        return new ResponseUtil().setData(object);
    }

}
