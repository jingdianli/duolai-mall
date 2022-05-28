package com.cskaoyan.shopping.service.remote;

import com.cskaoyan.mall.api.CartService;
import com.cskaoyan.mall.dto.ClearCartItemRequest;
import com.cskaoyan.mall.dto.ClearCartItemResponse;
import com.cskaoyan.shopping.service.ICartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Jingdian Li
 * @since 2022/05/28 08:23
 */

@RestController
public class CartServiceImpl implements CartService {
    @Autowired
    ICartService cartService;
    @Override
    public ClearCartItemResponse clearCartItemByUserID(ClearCartItemRequest request) {
        return cartService.clearCartItemByUserID(request);
    }
}
