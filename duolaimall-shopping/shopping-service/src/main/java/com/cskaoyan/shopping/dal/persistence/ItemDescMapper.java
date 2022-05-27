package com.cskaoyan.shopping.dal.persistence;


import com.cskaoyan.shopping.dal.entitys.ItemDesc;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

@Repository
public interface ItemDescMapper extends Mapper<ItemDesc> {
}