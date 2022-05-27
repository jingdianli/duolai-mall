package com.cskaoyan.user.service.remote;

import com.cskaoyan.mall.commons.constant.SysRetCodeConstants;
import com.cskaoyan.user.converter.MemberConverter;
import com.cskaoyan.user.dal.entitys.Member;
import com.cskaoyan.user.dal.persistence.MemberMapper;
import com.cskaoyan.user.dto.QueryMemberRequest;
import com.cskaoyan.user.dto.QueryMemberResponse;
import com.cskaoyan.user.service.UserService;
import com.cskaoyan.user.utils.ExceptionProcessorUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Jingdian Li
 * @since 2022/05/28 05:51
 */

@RestController
@Slf4j
public class UserServiceImpl implements UserService {
    @Autowired
    MemberMapper memberMapper;

    @Autowired
    MemberConverter memberConverter;
    @Override
    public QueryMemberResponse queryMemberById(QueryMemberRequest request) {
        QueryMemberResponse queryMemberResponse = new QueryMemberResponse();
        try{
            request.requestCheck();
            Member member = memberMapper.selectByPrimaryKey(request.getUserId());
            if(member==null){
                queryMemberResponse.setCode(SysRetCodeConstants.DATA_NOT_EXIST.getCode());
                queryMemberResponse.setMsg(SysRetCodeConstants.DATA_NOT_EXIST.getMessage());
            }
            queryMemberResponse=memberConverter.member2Res(member);
            queryMemberResponse.setCode(SysRetCodeConstants.SUCCESS.getCode());
            queryMemberResponse.setMsg(SysRetCodeConstants.SUCCESS.getMessage());
        }catch (Exception e){
            log.error("MemberServiceImpl.queryMemberById Occur Exception :"+e);
            ExceptionProcessorUtils.wrapperHandlerException(queryMemberResponse,e);
        }
        return queryMemberResponse;
    }
}
