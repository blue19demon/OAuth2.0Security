package com.funtl.oauth2.resource.controller;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.funtl.oauth2.resource.domain.TbContentCategory;
import com.funtl.oauth2.resource.dto.ResponseResult;
import com.funtl.oauth2.resource.service.TbContentCategoryService;

@RestController
public class TbContentCategoryController {

    @Autowired
    private TbContentCategoryService tbContentCategoryService;

    /**
     * 获取全部资源
     *
     * @return
     */
    @GetMapping("/category/list")
    public ResponseResult<List<TbContentCategory>> selectAll() {
        return new ResponseResult<>(Integer.valueOf(HttpStatus.OK.value()), HttpStatus.OK.toString(), tbContentCategoryService.selectAll());
    }

}