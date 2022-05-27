package com.cskaoyan.order.biz.handler;

import com.alibaba.fastjson.JSON;
import com.cskaoyan.mall.commons.exception.BaseBusinessException;
import com.cskaoyan.order.biz.context.CreateOrderContext;
import com.cskaoyan.order.biz.context.TransHandlerContext;
import com.cskaoyan.order.dal.entitys.Stock;
import com.cskaoyan.order.dal.persistence.StockMapper;
import com.cskaoyan.order.dto.CartProductDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @Description: 扣减库存处理器
 **/
@Component
@Slf4j
public class SubStockHandler extends AbstractTransHandler {
    @Autowired
    private StockMapper stockMapper;

    @Override
    public boolean isAsync() {
        return false;
    }

    @Override
    @Transactional
    public boolean handle(TransHandlerContext context) {
        log.info("begin SubStockHandler :context:" + context);
        CreateOrderContext createOrderContext = (CreateOrderContext) context;
        List<CartProductDto> cartProductDtoList = createOrderContext.getCartProductDtoList();

        List<Long> buyProductIds = createOrderContext.getBuyProductIds();
        if (CollectionUtils.isEmpty(buyProductIds)) {
            buyProductIds = cartProductDtoList.stream().map(u -> u.getProductId()).collect(Collectors.toList());
        }
        //排序
        buyProductIds.sort(Long::compareTo);

        List<Stock> stocksForUpdate = stockMapper.findStocksForUpdate(buyProductIds);
        log.info("购物车列表 list:{}", JSON.toJSONString(stocksForUpdate));
        if (CollectionUtils.isEmpty(stocksForUpdate)) {
            throw new BaseBusinessException("库存未初始化");
        }

        if (stocksForUpdate.size() != buyProductIds.size()) {
            throw new BaseBusinessException("有商品未初始化库存,请在如下商品id中检查库存状态：" + buyProductIds.toString());
        }

        stocksForUpdate.stream().forEach(stock -> {

            Optional<CartProductDto> targetCartItemOptional = cartProductDtoList.stream()
                    .filter(cartProductDto -> cartProductDto.getProductId().equals(stock.getItemId()))
                    .findAny();

            targetCartItemOptional
                    .ifPresent(cartItem -> {
                        if (cartItem.getProductNum() > stock.getStockCount()) {
                            throw new BaseBusinessException(stock.getItemId() + "库存不足");
                        }

                        if (stock.getRestrictCount() < cartItem.getProductNum()) {
                            log.info("商品{}超出限购数量，限购:{},购买:{}",
                                    cartItem.getProductName(), stock.getRestrictCount(), cartItem.getProductNum());
                            throw new BaseBusinessException("超出限购数量");
                        }

                        // 锁定库存
                        stock.setLockCount(cartItem.getProductNum().intValue());
                        stock.setStockCount(-cartItem.getProductNum());
                        // 更新库存
                        stockMapper.updateStock(stock);
                    });
        });


        return true;
    }
}