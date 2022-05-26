package com.cskaoyan.user.service;

import com.cskaoyan.user.dto.UserRegisterRequest;
import com.cskaoyan.user.dto.UserRegisterResponse;

public interface IRegisterService {
    UserRegisterResponse register(UserRegisterRequest userRegisterRequest);
}
