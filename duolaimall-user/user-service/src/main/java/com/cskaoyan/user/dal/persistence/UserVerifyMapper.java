package com.cskaoyan.user.dal.persistence;


import com.cskaoyan.user.dal.entitys.UserVerify;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

@Repository
public interface UserVerifyMapper extends Mapper<UserVerify> {

}