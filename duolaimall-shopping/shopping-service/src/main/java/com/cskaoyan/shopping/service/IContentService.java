package com.cskaoyan.shopping.service;

import com.cskaoyan.shopping.dto.NavListResponse;

public interface IContentService {
    /**
     * 查询导航栏商品种类列表
     * 
     * @return com.cskaoyan.shopping.dto.NavListResponse
     * @author Jingdian Li
     * @since 2022/05/27 8:18
     */
    NavListResponse queryNavList();
}
