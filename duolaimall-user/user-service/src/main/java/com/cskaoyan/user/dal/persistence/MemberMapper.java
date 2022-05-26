package com.cskaoyan.user.dal.persistence;


import com.cskaoyan.user.dal.entitys.Member;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

@Repository
public interface MemberMapper extends Mapper<Member> {

}