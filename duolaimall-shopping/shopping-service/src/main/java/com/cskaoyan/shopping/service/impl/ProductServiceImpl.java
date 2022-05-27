package com.cskaoyan.shopping.service.impl;

import com.alibaba.fastjson.JSON;
import com.cskaoyan.mall.constant.ShoppingRetCode;
import com.cskaoyan.mall.dto.*;
import com.cskaoyan.shopping.constants.GlobalConstants;
import com.cskaoyan.shopping.converter.ProductConverter;
import com.cskaoyan.shopping.dal.entitys.Item;
import com.cskaoyan.shopping.dal.entitys.ItemDesc;
import com.cskaoyan.shopping.dal.persistence.ItemDescMapper;
import com.cskaoyan.shopping.dal.persistence.ItemMapper;
import com.cskaoyan.shopping.dto.AllProductRequest;
import com.cskaoyan.shopping.dto.AllProductResponse;
import com.cskaoyan.shopping.dto.RecommendResponse;
import com.cskaoyan.shopping.service.IProductService;
import com.cskaoyan.shopping.service.cache.CacheManager;
import com.cskaoyan.shopping.utils.ExceptionProcessorUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Jingdian Li
 * @since 2022/05/27 09:41
 */

@Service
@Slf4j
public class ProductServiceImpl implements IProductService {
    @Autowired
    CacheManager cacheManager;

    @Autowired
    ItemMapper itemMapper;

    @Autowired
    ItemDescMapper itemDescMapper;

    @Autowired
    ProductConverter productConverter;

    @Override
    public ProductDetailResponse getProductDetail(ProductDetailRequest request) {
        ProductDetailResponse response = new ProductDetailResponse();
        response.setCode(ShoppingRetCode.SUCCESS.getCode());
        response.setMsg(ShoppingRetCode.SUCCESS.getMessage());
        try {
            // 查询缓存
            String json = cacheManager.checkCache(generatorProduceCacheKey(request));
            if (StringUtils.isNotBlank(json)) {
                ProductDetailDto productDetailDto = JSON.parseObject(json, ProductDetailDto.class);
                cacheManager.expire(generatorProduceCacheKey(request), GlobalConstants.PRODUCT_ITEM_EXPIRE_TIME);
                response.setProductDetailDto(productDetailDto);
                return response;
            }
            Item item = itemMapper.selectByPrimaryKey(request.getId().longValue());
            ProductDetailDto productDetailDto = new ProductDetailDto();
            productDetailDto.setProductId(request.getId().longValue());
            productDetailDto.setProductName(item.getTitle());
            productDetailDto.setSubTitle(item.getSellPoint());
            productDetailDto
                .setLimitNum(item.getLimitNum() == null ? item.getNum().longValue() : item.getLimitNum().longValue());
            productDetailDto.setSalePrice(item.getPrice());

            ItemDesc itemDesc = itemDescMapper.selectByPrimaryKey(request.getId().longValue());
            productDetailDto.setDetail(itemDesc.getItemDesc());
            if (StringUtils.isNotBlank(item.getImage())) {
                String images[] = item.getImage().split(",");
                productDetailDto.setProductImageBig(images[0]);
                productDetailDto.setProductImageSmall(Arrays.asList(images));
            }
            response.setProductDetailDto(productDetailDto);
            // 设置缓存
            cacheManager.setCache(generatorProduceCacheKey(request), JSON.toJSON(productDetailDto).toString(),
                GlobalConstants.PRODUCT_ITEM_EXPIRE_TIME);
        } catch (Exception e) {
            log.error("ProductServiceImpl.getProductDetail Occur Exception :" + e);
            ExceptionProcessorUtils.wrapperHandlerException(response, e);
        }
        return response;
    }

    @Override
    public AllProductResponse getAllProduct(AllProductRequest request) {
        AllProductResponse response = new AllProductResponse();
        response.setCode(ShoppingRetCode.SUCCESS.getCode());
        response.setMsg(ShoppingRetCode.SUCCESS.getMessage());
        try {
            List<ProductDto> productDtos = new ArrayList<>();
            PageHelper.startPage(request.getPage(), request.getSize());
            String orderCol = "created";
            String orderDir = "desc";
            if (request.getSort().equals("1")) {
                orderCol = "price";
                orderDir = "asc";
            } else if (request.getSort().equals("-1")) {
                orderCol = "price";
                orderDir = "desc";
            }
            List<Item> items = itemMapper.selectItemFront(request.getCid(), orderCol, orderDir, request.getPriceGt(),
                request.getPriceLte());
            PageInfo<Item> pageInfo = new PageInfo<>(items);
            List<ProductDto> productDtosList = productConverter.items2Dto(items);
            response.setProductDtoList(productDtosList);
            response.setTotal(pageInfo.getTotal());
        } catch (Exception e) {
            log.error("ProductServiceImpl.getAllProduct Occur Exception :" + e);
            ExceptionProcessorUtils.wrapperHandlerException(response, e);
        }
        return response;
    }

    @Override
    public RecommendResponse getRecommendGoods() {
        return null;
    }

    @Override
    public AllItemResponse getAllItems() {
        return null;
    }

    private String generatorProduceCacheKey(ProductDetailRequest request) {
        StringBuilder stringBuilder = new StringBuilder(GlobalConstants.PRODUCT_ITEM_CACHE_KEY);
        stringBuilder.append(":").append(request.getId());
        return stringBuilder.toString();
    }
}
