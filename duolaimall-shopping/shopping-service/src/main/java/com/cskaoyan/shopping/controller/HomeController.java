package com.cskaoyan.shopping.controller;

import com.cskaoyan.mall.commons.result.ResponseData;
import com.cskaoyan.mall.commons.result.ResponseUtil;
import com.cskaoyan.mall.constant.ShoppingRetCode;
import com.cskaoyan.shopping.dto.NavListResponse;
import com.cskaoyan.shopping.service.IContentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Jingdian Li
 * @since 2022/05/27 07:39
 */

@RestController
@RequestMapping("/shopping")
public class HomeController {
    @Autowired
    IContentService contentService;

    /**
     * 导航栏显示
     *
     * @return com.cskaoyan.mall.commons.result.ResponseData
     * @author Jingdian Li
     * @since 2022/05/27 7:43
     */
    @GetMapping("/navigation")
    public ResponseData navigation() {
        NavListResponse response = contentService.queryNavList();
        if (response.getCode().equals(ShoppingRetCode.SUCCESS.getCode())) {
            return new ResponseUtil().setData(response.getPannelContentDtos());
        }
        return new ResponseUtil().setErrorMsg(response.getMsg());
    }

    /**
     * 主页显示
     *
     * @return com.cskaoyan.mall.commons.result.ResponseData
     * @author Jingdian Li
     * @since 2022/05/27 7:44
     */
    @GetMapping("/homepage")
    public ResponseData homepage() {
        return null;
    }
}
