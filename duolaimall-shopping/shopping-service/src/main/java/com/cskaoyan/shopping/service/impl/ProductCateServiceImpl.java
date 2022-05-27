package com.cskaoyan.shopping.service.impl;

import com.alibaba.fastjson.JSON;
import com.cskaoyan.mall.constant.ShoppingRetCode;
import com.cskaoyan.shopping.constants.GlobalConstants;
import com.cskaoyan.shopping.converter.ProductCateConverter;
import com.cskaoyan.shopping.dal.entitys.ItemCat;
import com.cskaoyan.shopping.dal.persistence.ItemCatMapper;
import com.cskaoyan.shopping.dto.AllProductCateRequest;
import com.cskaoyan.shopping.dto.AllProductCateResponse;
import com.cskaoyan.shopping.dto.ProductCateDto;
import com.cskaoyan.shopping.service.IProductCateService;
import com.cskaoyan.shopping.service.cache.CacheManager;
import com.cskaoyan.shopping.utils.ExceptionProcessorUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

/**
 * @author Jingdian Li
 * @since 2022/05/27 09:28
 */

@Service
@Slf4j
public class ProductCateServiceImpl implements IProductCateService {
    @Autowired
    ItemCatMapper itemCatMapper;
    @Autowired
    CacheManager cacheManager;
    @Autowired
    ProductCateConverter productCateConverter;

    @Override
    public AllProductCateResponse getAllProductCate(AllProductCateRequest request) {
        AllProductCateResponse response = new AllProductCateResponse();
        response.setCode(ShoppingRetCode.SUCCESS.getCode());
        response.setMsg(ShoppingRetCode.SUCCESS.getMessage());

        try{
            String json=cacheManager.checkCache(GlobalConstants.PRODUCT_CATE_CACHE_KEY);
            if(StringUtils.isNotBlank(json)){
                List<ProductCateDto> productCateDtos = JSON.parseArray(json,ProductCateDto.class);
                response.setProductCateDtoList(productCateDtos);
                return response;
            }
            Example itemCatExample = new Example(ItemCat.class);
//            ItemCatExample itemCatExample = new ItemCatExample();
//            ItemCatExample.Criteria criteria = itemCatExample.createCriteria();
            String orderCol = "sort_order";
            String orderDir = "asc";
            if(request.getSort().equals("1")){
                orderCol="sort_order";
                orderDir="asc";
            }else if(request.getSort().equals("-1")){
                orderCol="sort_order";
                orderDir="desc";
            }
            itemCatExample.setOrderByClause(orderCol + " " + orderDir);

            List<ItemCat> itemCats = itemCatMapper.selectByExample(itemCatExample);
            List<ProductCateDto> productCateDtoList = productCateConverter.items2Dto(itemCats);
            response.setProductCateDtoList(productCateDtoList);
            //设置缓存
            cacheManager.setCache(
                    genProductCateCacheKey(request),
                    JSON.toJSON(productCateDtoList).toString(),
                    GlobalConstants.PRODUCT_CATE_EXPIRE_TIME);
        }catch (Exception e){
            log.error("ProductCateServiceImpl.getAllProductCate Occur Exception :"+e);
            ExceptionProcessorUtils.wrapperHandlerException(response,e);
        }
        return response;
    }

    private String genProductCateCacheKey(AllProductCateRequest request) {
        return GlobalConstants.PRODUCT_CATE_CACHE_KEY;
    }
}
