package com.funtl.oauth2.resource.service.impl;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.funtl.oauth2.resource.domain.TbContentCategory;
import com.funtl.oauth2.resource.mapper.TbContentCategoryMapper;
import com.funtl.oauth2.resource.service.TbContentCategoryService;
@Service
public class TbContentCategoryServiceImpl implements TbContentCategoryService{

    @Resource
    private TbContentCategoryMapper tbContentCategoryMapper;

	@Override
	public List<TbContentCategory> selectAll() {
		return tbContentCategoryMapper.selectAll();
	}

}