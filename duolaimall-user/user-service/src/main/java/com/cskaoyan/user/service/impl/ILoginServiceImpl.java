package com.cskaoyan.user.service.impl;

import com.alibaba.fastjson.JSON;
import com.cskaoyan.mall.commons.constant.SysRetCodeConstants;
import com.cskaoyan.mall.commons.util.jwt.JwtTokenUtils;
import com.cskaoyan.user.constants.UserRetCode;
import com.cskaoyan.user.converter.UserConverterMapper;
import com.cskaoyan.user.dal.entitys.Member;
import com.cskaoyan.user.dal.persistence.MemberMapper;
import com.cskaoyan.user.dto.UserLoginRequest;
import com.cskaoyan.user.dto.UserLoginResponse;
import com.cskaoyan.user.service.ILoginService;
import com.cskaoyan.user.utils.ExceptionProcessorUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Jingdian Li
 * @since 2022/05/27 05:18
 */

@Service
@Slf4j
public class ILoginServiceImpl implements ILoginService {
    @Autowired
    MemberMapper memberMapper;

    @Override
    public UserLoginResponse login(UserLoginRequest request) {
        log.info("Begin UserLoginServiceImpl.login: request:" + request);
        UserLoginResponse response = new UserLoginResponse();
        try {
            request.requestCheck();
            Example example = new Example(Member.class);
            example.createCriteria().andEqualTo("state", 1).andEqualTo("username", request.getUserName());

            List<Member> member = memberMapper.selectByExample(example);
            if (member == null || member.size() == 0) {
                response.setCode(UserRetCode.USERORPASSWORD_ERRROR.getCode());
                response.setMsg(UserRetCode.USERORPASSWORD_ERRROR.getMessage());
                return response;
            }
            // 验证是否已经激活
            if ("N".equals(member.get(0).getIsVerified())) {
                response.setCode(UserRetCode.USERORPASSWORD_ERRROR.getCode());
                response.setMsg(UserRetCode.USERORPASSWORD_ERRROR.getMessage());
                return response;
            }
            if (!DigestUtils.md5DigestAsHex(request.getPassword().getBytes()).equals(member.get(0).getPassword())) {
                response.setCode(UserRetCode.USERORPASSWORD_ERRROR.getCode());
                response.setMsg(UserRetCode.USERORPASSWORD_ERRROR.getMessage());
                return response;
            }
            Map<String, Object> map = new HashMap<>();
            map.put("uid", member.get(0).getId());
            map.put("file", member.get(0).getFile());

            String token = JwtTokenUtils.builder().msg(JSON.toJSON(map).toString()).build().creatJwtToken();
            response = UserConverterMapper.INSTANCE.converter(member.get(0));
            response.setToken(token);
            response.setCode(SysRetCodeConstants.SUCCESS.getCode());
            response.setMsg(SysRetCodeConstants.SUCCESS.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            log.error("UserLoginServiceImpl.login Occur Exception :" + e);
            ExceptionProcessorUtils.wrapperHandlerException(response, e);
        }
        return response;
    }
}
