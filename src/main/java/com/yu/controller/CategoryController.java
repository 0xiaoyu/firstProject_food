package com.yu.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yu.common.R;
import com.yu.domain.Category;
import com.yu.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/category")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    /**
     * 添加菜单
     * @param category
     * @return
     */
    @PostMapping
    public R<String> addCategory(@RequestBody Category category){
        categoryService.save(category);
        return R.success("添加成功");
    }

    /**
     * 分页查询
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/page")
    public R<Page> getAll(int page, int pageSize){
        Page p=new Page(page,pageSize);
        LambdaQueryWrapper<Category> lqw=new LambdaQueryWrapper<>();
        lqw.orderByAsc(Category::getSort);
        categoryService.page(p,lqw);
        return R.success(p);
    }

    /**
     * 根据id删除
     * @param id
     * @return
     */
    @DeleteMapping("/{id}")
    public R<String> deleteById(@PathVariable Long id){
        categoryService.remove(id);
        return R.success("删除"+id);
    }

    /**
     * 更新数据
     * @param category
     * @return
     */
    @PutMapping
    public R<String> updateCategory(@RequestBody Category category){
        categoryService.updateById(category);
        return R.success("修改成功");
    }

    /**
     * 根据条件查询分类数据
     * @param category
     * @return
     */
    @GetMapping("/list")
    public R<List<Category>> getList(Category category){
        LambdaQueryWrapper<Category> lqw=new LambdaQueryWrapper<>();
        lqw.eq(category.getType()!=null,Category::getType,category.getType());
        lqw.orderByAsc(Category::getSort).orderByDesc(Category::getUpdateTime);
        List<Category> categories = categoryService.list(lqw);
        return R.success(categories);
    }
}
