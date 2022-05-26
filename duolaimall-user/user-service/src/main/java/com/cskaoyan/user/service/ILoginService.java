package com.cskaoyan.user.service;

import com.cskaoyan.user.dto.UserLoginRequest;
import com.cskaoyan.user.dto.UserLoginResponse;

public interface ILoginService {
    UserLoginResponse login(UserLoginRequest loginRequest);
}
