package com.cskaoyan.shopping.converter;

import com.cskaoyan.shopping.dal.entitys.Item;
import com.cskaoyan.shopping.dto.ITestProductDetailDto;
import javax.annotation.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2022-05-28T09:41:27+0800",
    comments = "version: 1.3.0.Final, compiler: javac, environment: Java 1.8.0_333 (Oracle Corporation)"
)
@Component
public class ITestProductConverterImpl implements ITestProductConverter {

    @Override
    public ITestProductDetailDto testProductDoToProductDto(Item item) {
        if ( item == null ) {
            return null;
        }

        ITestProductDetailDto iTestProductDetailDto = new ITestProductDetailDto();

        iTestProductDetailDto.setImgUrls( item.getImage() );
        iTestProductDetailDto.setProductName( item.getTitle() );
        iTestProductDetailDto.setPrice( item.getPrice() );

        return iTestProductDetailDto;
    }
}
