package com.cskaoyan.order.dal.entitys;

import lombok.Data;

import javax.persistence.Table;

@Data
@Table(name = "tb_stock")
public class Stock {
	//商品id
private Long itemId;
//库存可用数量
private Long stockCount;
//锁定数量
private Integer lockCount;
//限购数量
private Integer restrictCount;
//售卖ID(放在不同区域售卖的id)
private Integer sellId;
}