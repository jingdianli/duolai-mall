package com.cskaoyan.order.dal.persistence;


import com.cskaoyan.order.dal.entitys.Order;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

@Repository
public interface OrderMapper extends Mapper<Order> {
    Long countAll();
}