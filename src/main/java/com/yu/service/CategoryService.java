package com.yu.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yu.domain.Category;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface CategoryService extends IService<Category> {

    public void remove(Long id);
}
