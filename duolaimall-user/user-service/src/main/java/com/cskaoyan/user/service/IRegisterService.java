package com.cskaoyan.user.service;

import com.cskaoyan.user.dto.UserRegisterRequest;
import com.cskaoyan.user.dto.UserRegisterResponse;
import com.cskaoyan.user.dto.UserVerifyRequest;
import com.cskaoyan.user.dto.UserVerifyResponse;

public interface IRegisterService {
    UserRegisterResponse register(UserRegisterRequest userRegisterRequest);

    UserVerifyResponse verifyMemer(UserVerifyRequest userVerifyRequest);
}
