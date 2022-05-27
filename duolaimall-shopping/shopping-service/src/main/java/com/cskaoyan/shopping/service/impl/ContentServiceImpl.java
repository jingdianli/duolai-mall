package com.cskaoyan.shopping.service.impl;

import com.cskaoyan.mall.constant.ShoppingRetCode;
import com.cskaoyan.shopping.constants.GlobalConstants;
import com.cskaoyan.shopping.converter.ContentConverter;
import com.cskaoyan.shopping.dal.entitys.PanelContent;
import com.cskaoyan.shopping.dal.persistence.PanelContentMapper;
import com.cskaoyan.shopping.dto.NavListResponse;
import com.cskaoyan.shopping.service.IContentService;
import com.cskaoyan.shopping.utils.ExceptionProcessorUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

/**
 * @author Jingdian Li
 * @since 2022/05/27 08:13
 */

@Service
@Slf4j
public class ContentServiceImpl implements IContentService {
    @Autowired
    PanelContentMapper panelContentMapper;

    @Autowired
    ContentConverter contentConverter;

    /**
     * 
     * @return com.cskaoyan.shopping.dto.NavListResponse
     * @author Jingdian Li
     * @since 2022/05/27 8:19
     */
    @Override
    public NavListResponse queryNavList() {
        NavListResponse response = new NavListResponse();
        try {
            Example exampleContent = new Example(PanelContent.class);
            exampleContent.setOrderByClause("sort_order");
            Example.Criteria criteriaContent = exampleContent.createCriteria();
            criteriaContent.andEqualTo("panelId", GlobalConstants.HEADER_PANEL_ID);
            List<PanelContent> pannelContents = panelContentMapper.selectByExample(exampleContent);
            // TODO 添加缓存操作
            response.setPannelContentDtos(contentConverter.panelContents2Dto(pannelContents));
            response.setCode(ShoppingRetCode.SUCCESS.getCode());
            response.setMsg(ShoppingRetCode.SUCCESS.getMessage());
        } catch (Exception e) {
            log.error("ContentServiceImpl.queryNavList Occur Exception :" + e);
            ExceptionProcessorUtils.wrapperHandlerException(response, e);
        }
        return response;
    }
}
