package com.cskaoyan.user.service;

import com.cskaoyan.user.dto.QueryMemberRequest;
import com.cskaoyan.user.dto.QueryMemberResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

public interface UserService {
    /**
     * queryUserById 
     * @param request 
     * @return com.cskaoyan.user.dto.QueryMemberResponse
     * @author Jingdian Li
     * @since 2022/05/28 5:31
    */
    @PostMapping(value = "/rpc/user")
    QueryMemberResponse queryMemberById(@RequestBody QueryMemberRequest request);
}
