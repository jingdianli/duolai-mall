package com.cskaoyan.order.dal.persistence;


import com.cskaoyan.order.dal.entitys.OrderShipping;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

@Repository
public interface OrderShippingMapper extends Mapper<OrderShipping> {
}