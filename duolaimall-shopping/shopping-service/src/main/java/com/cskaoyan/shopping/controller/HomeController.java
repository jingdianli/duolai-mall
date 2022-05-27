package com.cskaoyan.shopping.controller;

import com.cskaoyan.mall.commons.result.ResponseData;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Jingdian Li
 * @since 2022/05/27 07:39
 */

@RestController
@RequestMapping("/shopping")
public class HomeController {
    /**
     * 导航栏显示
     *
     * @return com.cskaoyan.mall.commons.result.ResponseData
     * @author Jingdian Li
     * @since 2022/05/27 7:43 
     */
    @GetMapping("/navigation")
    public ResponseData navigation(){
        return null;
    }

    /**
     * 主页显示
     *
     * @return com.cskaoyan.mall.commons.result.ResponseData
     * @author Jingdian Li
     * @since 2022/05/27 7:44
     */
    @GetMapping("/homepage")
    public ResponseData homepage(){
        return null;
    }
}
