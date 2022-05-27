package com.cskaoyan.shopping.service.impl;

import com.alibaba.fastjson.JSON;
import com.cskaoyan.mall.constant.ShoppingRetCode;
import com.cskaoyan.mall.dto.ClearCartItemRequest;
import com.cskaoyan.mall.dto.ClearCartItemResponse;
import com.cskaoyan.shopping.constants.GlobalConstants;
import com.cskaoyan.shopping.converter.CartItemConverter;
import com.cskaoyan.shopping.dal.entitys.Item;
import com.cskaoyan.shopping.dal.persistence.ItemMapper;
import com.cskaoyan.shopping.dto.*;
import com.cskaoyan.shopping.service.ICartService;
import com.cskaoyan.shopping.utils.ExceptionProcessorUtils;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author Jingdian Li
 * @since 2022/05/27 10:14
 */

@Service
@Slf4j
public class CartServiceImpl implements ICartService {
    @Autowired
    RedissonClient redissonClient;

    @Autowired
    ItemMapper itemMapper;

    @Override
    public CartListByIdResponse getCartListById(CartListByIdRequest request) {
        Long userId = request.getUserId();
        CartListByIdResponse cartListByIdResponse = new CartListByIdResponse();
        try {
            RMap<String, CartProductDto> map = redissonClient.getMap(userId.toString());
            List<CartProductDto> cartProductDtoList = new ArrayList<>();
            if (map == null) {
                // 若购物车为空
                cartProductDtoList = null;
                cartListByIdResponse.setCartProductDtos(cartProductDtoList);
            } else {
                // 若购物车不为空，拿到购物车中所有产品的productId,即key的集合。
                Set<String> keySet = map.keySet();
                // 根据所有的key值，得到所有对应的产品对象集合
                for (String item : keySet) {
                    CartProductDto cartProductDto = map.get(item);
                    cartProductDtoList.add(cartProductDto);
                }
                cartListByIdResponse.setCartProductDtos(cartProductDtoList);
            }
            cartListByIdResponse.setCode(ShoppingRetCode.SUCCESS.getCode());
            cartListByIdResponse.setMsg(ShoppingRetCode.SUCCESS.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            cartListByIdResponse.setCode(ShoppingRetCode.DB_EXCEPTION.getCode());
            cartListByIdResponse.setMsg(ShoppingRetCode.DB_EXCEPTION.getMessage());
        }
        return cartListByIdResponse;
    }

    @Override
    public AddCartResponse addToCart(AddCartRequest request) {
        AddCartResponse response = new AddCartResponse();
        response.setCode(ShoppingRetCode.SUCCESS.getCode());
        response.setMsg(ShoppingRetCode.SUCCESS.getMessage());
        try {
            request.requestCheck();
            boolean exists =
                redissonClient.getMap(generatorCartItemKey(request.getUserId())).containsKey(request.getItemId());
            if (exists) {
                String cartItemJson = redissonClient.getMap(generatorCartItemKey(request.getUserId()))
                    .get(request.getItemId()).toString();
                CartProductDto cartProductDto = JSON.parseObject(cartItemJson, CartProductDto.class);
                cartProductDto.setProductNum(cartProductDto.getProductNum().longValue() + request.getNum().longValue());
                redissonClient.getMap(generatorCartItemKey(request.getUserId())).put(request.getItemId(),
                    JSON.toJSON(cartProductDto).toString());
                return response;
            }
            Item item = itemMapper.selectByPrimaryKey(request.getItemId().longValue());
            if (item != null) {
                CartProductDto cartProductDto = CartItemConverter.item2Dto(item);
                cartProductDto.setChecked("true");
                cartProductDto.setProductNum(request.getNum().longValue());
                redissonClient.getMap(generatorCartItemKey(request.getUserId())).put(request.getItemId(),
                    JSON.toJSON(cartProductDto).toString());
                return response;
            }
            response.setCode(ShoppingRetCode.SYSTEM_ERROR.getCode());
            response.setMsg(ShoppingRetCode.SYSTEM_ERROR.getMessage());
        } catch (Exception e) {
            log.error("CartServiceImpl.addToCart Occur Exception :" + e);
            ExceptionProcessorUtils.wrapperHandlerException(response, e);
        }
        return response;
    }

    @Override
    public UpdateCartNumResponse updateCartNum(UpdateCartNumRequest request) {
        UpdateCartNumResponse response = new UpdateCartNumResponse();
        response.setCode(ShoppingRetCode.SUCCESS.getCode());
        response.setMsg(ShoppingRetCode.SUCCESS.getMessage());
        try {
            RMap itemMap = redissonClient.getMap(generatorCartItemKey(request.getUserId()));
            Object item = itemMap.get(request.getItemId());
            if (item != null) {
                CartProductDto cartProductDto = JSON.parseObject(item.toString(), CartProductDto.class);
                cartProductDto.setChecked(request.getChecked());
                cartProductDto.setProductNum(request.getNum().longValue());
                itemMap.put(request.getItemId(), JSON.toJSON(cartProductDto));
            }
        } catch (Exception e) {
            log.error("CartServiceImpl.updateCartNum Occur Exception :" + e);
            ExceptionProcessorUtils.wrapperHandlerException(response, e);
        }
        return response;
    }

    @Override
    public CheckAllItemResponse checkAllCartItem(CheckAllItemRequest request) {
        CheckAllItemResponse response = new CheckAllItemResponse();
        try {
            RMap items = redissonClient.getMap(generatorCartItemKey(request.getUserId()));
            items.values().forEach(obj -> {
                CartProductDto cartProductDto = (CartProductDto)obj;
                cartProductDto.setChecked(request.getChecked());// true / false
                items.put(cartProductDto.getProductId(), cartProductDto);
            });
            response.setCode(ShoppingRetCode.SUCCESS.getCode());
            response.setMsg(ShoppingRetCode.SUCCESS.getMessage());
        } catch (Exception e) {
            log.error("CartServiceImpl.checkAllCartItem Occur Exception :" + e);
            ExceptionProcessorUtils.wrapperHandlerException(response, e);
        }
        return response;
    }

    @Override
    public DeleteCartItemResponse deleteCartItem(DeleteCartItemRequest request) {
        DeleteCartItemResponse response = new DeleteCartItemResponse();
        try {
            RMap rMap = redissonClient.getMap(generatorCartItemKey(request.getUserId()));
            rMap.remove(request.getItemId());
            response.setCode(ShoppingRetCode.SUCCESS.getCode());
            response.setMsg(ShoppingRetCode.SUCCESS.getMessage());
        } catch (Exception e) {
            log.error("CartServiceImpl.deleteCartItem Occur Exception :" + e);
            ExceptionProcessorUtils.wrapperHandlerException(response, e);
        }
        return response;
    }

    @Override
    public DeleteCheckedItemResposne deleteCheckedItem(DeleteCheckedItemRequest request) {
        DeleteCheckedItemResposne response = new DeleteCheckedItemResposne();
        try {
            RMap itemMap = redissonClient.getMap(generatorCartItemKey(request.getUserId()));
            itemMap.values().forEach(obj -> {
                CartProductDto cartProductDto = JSON.parseObject(obj.toString(), CartProductDto.class);
                if ("true".equals(cartProductDto.getChecked())) {
                    itemMap.remove(cartProductDto.getProductId());
                }
            });
            response.setCode(ShoppingRetCode.SUCCESS.getCode());
            response.setMsg(ShoppingRetCode.SUCCESS.getMessage());
        } catch (Exception e) {
            log.error("CartServiceImpl.deleteCheckedItem Occur Exception :" + e);
            ExceptionProcessorUtils.wrapperHandlerException(response, e);
        }
        return response;
    }

    @Override
    public ClearCartItemResponse clearCartItemByUserID(ClearCartItemRequest request) {
        return null;
    }

    private String generatorCartItemKey(long userId) {
        StringBuilder sb = new StringBuilder(GlobalConstants.CART_ITEM_CACHE_PREFIX);
        sb.append(":").append(userId);
        return sb.toString();
    }
}
