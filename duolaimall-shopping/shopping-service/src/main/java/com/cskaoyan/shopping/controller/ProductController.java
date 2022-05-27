package com.cskaoyan.shopping.controller;

import com.cskaoyan.mall.commons.result.ResponseData;
import com.cskaoyan.mall.commons.result.ResponseUtil;
import com.cskaoyan.mall.constant.ShoppingRetCode;
import com.cskaoyan.mall.dto.ProductDetailRequest;
import com.cskaoyan.mall.dto.ProductDetailResponse;
import com.cskaoyan.shopping.form.PageInfo;
import com.cskaoyan.shopping.service.IProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Jingdian Li
 * @since 2022/05/27 07:45
 */

@RestController
@RequestMapping("/shopping")
public class ProductController {
    @Autowired
    IProductService productService;

    /**
     * 查看商品详情
     *
     * @param id
     * @return com.cskaoyan.mall.commons.result.ResponseData
     * @author Jingdian Li
     * @since 2022/05/27 7:47
     */
    @GetMapping("/product/{id}")
    public ResponseData product(@PathVariable long id) {
        ProductDetailRequest request = new ProductDetailRequest();
        request.setId(id);
        ProductDetailResponse response = productService.getProductDetail(request);

        if (response.getCode().equals(ShoppingRetCode.SUCCESS.getCode())) {
            return new ResponseUtil().setData(response.getProductDetailDto());
        }
        return new ResponseUtil().setErrorMsg(response.getMsg());
    }

    /**
     * 分⻚页查询商品列列表
     *
     * @param pageInfo
     * @return com.cskaoyan.mall.commons.result.ResponseData
     * @author Jingdian Li
     * @since 2022/05/27 7:47
     */
    @GetMapping("/goods")
    public ResponseData goods(PageInfo pageInfo) {
        return null;
    }

    /**
     * 查询推荐商品
     *
     * @return com.cskaoyan.mall.commons.result.ResponseData
     * @author Jingdian Li
     * @since 2022/05/27 7:47
     */
    @GetMapping("/recommend")
    public ResponseData recommend() {
        return null;
    }
}
