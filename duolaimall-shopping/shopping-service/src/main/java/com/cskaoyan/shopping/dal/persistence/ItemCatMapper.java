package com.cskaoyan.shopping.dal.persistence;


import com.cskaoyan.shopping.dal.entitys.ItemCat;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

@Repository
public interface ItemCatMapper extends Mapper<ItemCat> {
}