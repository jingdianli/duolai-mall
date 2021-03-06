package com.cskaoyan.user.service.impl;

import com.alibaba.fastjson.JSON;
import com.cskaoyan.mall.commons.constant.SysRetCodeConstants;
import com.cskaoyan.mall.commons.exception.ValidateException;
import com.cskaoyan.user.constants.UserRetCode;
import com.cskaoyan.user.dal.entitys.Member;
import com.cskaoyan.user.dal.entitys.UserVerify;
import com.cskaoyan.user.dal.persistence.MemberMapper;
import com.cskaoyan.user.dal.persistence.UserVerifyMapper;
import com.cskaoyan.user.dto.UserRegisterRequest;
import com.cskaoyan.user.dto.UserRegisterResponse;
import com.cskaoyan.user.dto.UserVerifyRequest;
import com.cskaoyan.user.dto.UserVerifyResponse;
import com.cskaoyan.user.service.IRegisterService;
import com.cskaoyan.user.utils.ExceptionProcessorUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.DigestUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class RegisterServiceImpl implements IRegisterService {
    @Autowired
    MemberMapper memberMapper;
    @Autowired
    UserVerifyMapper userVerifyMapper;

    @Autowired
    JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String from;
    @Override
    public UserRegisterResponse register(UserRegisterRequest registerRequest) {
        UserRegisterResponse registerResponse = new UserRegisterResponse();
        // 判空验证
        registerRequest.requestCheck();
        // 验证用户名是否重复
        volidUserNameRepeat(registerRequest);
        // 1.向用户表中插入一条记录
        Member member = new Member();
        member.setUsername(registerRequest.getUserName());
        member.setEmail(registerRequest.getEmail());
        String userPwd = DigestUtils.md5DigestAsHex(registerRequest.getUserPwd().getBytes());
        member.setPassword(userPwd);
        member.setCreated(new Date());
        member.setUpdated(new Date());
        member.setIsVerified("N");
        member.setState(1);
        int insert = memberMapper.insert(member);
        if (insert != 1){
            registerResponse.setCode(UserRetCode.USER_REGISTER_FAILED.getCode());
            registerResponse.setMsg(UserRetCode.USER_REGISTER_FAILED.getMessage());
            return registerResponse;
        }
        // 2.向用户验证表中插入一条记录
        UserVerify userVerify = new UserVerify();
        userVerify.setUsername(member.getUsername());
        String key = member.getUsername() + member.getPassword() + UUID.randomUUID().toString();
        String uuid = DigestUtils.md5DigestAsHex(key.getBytes());
        userVerify.setUuid(uuid);
        userVerify.setRegisterDate(new Date());
        userVerify.setIsExpire("N");
        userVerify.setIsVerify("N");
        int row = userVerifyMapper.insert(userVerify);
        if (row != 1){
            registerResponse.setCode(UserRetCode.USER_REGISTER_VERIFY_FAILED.getCode());
            registerResponse.setMsg(UserRetCode.USER_REGISTER_VERIFY_FAILED.getMessage());
            return registerResponse;
        }
        // 3.向用户发送激活邮件
        sendEmail(uuid, registerRequest);
        // 打印日志
        log.info("注册成功，注册参数request:{},{}", JSON.toJSONString(registerRequest),"xxx");
        registerResponse.setCode(UserRetCode.SUCCESS.getCode());
        registerResponse.setMsg(UserRetCode.SUCCESS.getMessage());
        return registerResponse;
    }

    @Override
    public UserVerifyResponse verifyMemer(UserVerifyRequest request) {
        UserVerifyResponse response = new UserVerifyResponse();
        try {
            request.requestCheck();
            Example example = new Example(Member.class);
            example.createCriteria().andEqualTo("state", 1).andEqualTo("username", request.getUserName());

            List<Member> member = memberMapper.selectByExample(example);
            if (member == null || member.size() == 0) {
                response.setCode(UserRetCode.USER_INFOR_INVALID.getCode());
                response.setMsg(UserRetCode.USER_INFOR_INVALID.getMessage());
                return response;
            }
            // 是否存在注册激活信息
            example.clear();
            example = new Example(UserVerify.class);
            example.createCriteria().andEqualTo("uuid", request.getUuid());
            List<UserVerify> userVerifys = userVerifyMapper.selectByExample(example);
            if (userVerifys == null || userVerifys.size() == 0) {
                response.setCode(UserRetCode.USERVERIFY_INFOR_INVALID.getCode());
                response.setMsg(UserRetCode.USERVERIFY_INFOR_INVALID.getMessage());
                return response;
            }
            example.clear();
            example.createCriteria().andEqualTo("uuid", request.getUuid());
            UserVerify userVerify = userVerifys.get(0);
            userVerify.setIsVerify("Y");
            // 激活用户，修改tb_user_verify的信息 is_verify
            userVerifyMapper.updateByExample(userVerify, example);
            // 更新Member 表的is_verify
            example.clear();
            example.createCriteria().andEqualTo("username", request.getUserName());
            Member member1 = member.get(0);
            member1.setIsVerified("Y");
            memberMapper.updateByExample(member1, example);

            response.setCode(SysRetCodeConstants.SUCCESS.getCode());
            response.setCode(SysRetCodeConstants.SUCCESS.getMessage());
            return response;
        } catch (Exception e) {
            log.error(e.getMessage());
            e.printStackTrace();
            ExceptionProcessorUtils.wrapperHandlerException(response, e);
            return response;
        }
    }

    private void volidUserNameRepeat(UserRegisterRequest registerRequest) {
        Example example = new Example(Member.class);
        example.createCriteria().andEqualTo("username",registerRequest.getUserName());
        // select * from tb_member username = #{username}
        List<Member> members = memberMapper.selectByExample(example);
        if (!CollectionUtils.isEmpty(members)){
            throw  new ValidateException(UserRetCode.USERNAME_ALREADY_EXISTS.getCode(), UserRetCode.USERNAME_ALREADY_EXISTS.getMessage());
        }
    }

    private void sendEmail(String uuid, UserRegisterRequest registerRequest) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setSubject("MALL注册激活");
        message.setFrom(from);
        message.setTo(registerRequest.getEmail());
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("http://localhost:8099/user/verify?uuid=").append(uuid).append("&username=").append(registerRequest.getUserName());
        message.setText(stringBuilder.toString());
        mailSender.send(message);
    }
}
