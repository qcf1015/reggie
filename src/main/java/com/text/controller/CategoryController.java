package com.text.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.text.common.R;
import com.text.entity.Category;
import com.text.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/category")
@Slf4j
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    /**
     * 新增分类
     * @param category
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody Category category){
        log.info("新增成功:{}",category);
        categoryService.save(category);
        return R.success("新增成功");
    }

    /**
     * 分页查询
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page,int pageSize){
//        分页构造器
        Page page1 = new Page(page,pageSize);
//        条件构造器
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
//        进行排序
        queryWrapper.orderByDesc(Category::getSort);
        categoryService.page(page1,queryWrapper);
        return R.success(page1);
    }

    /**
     * 删除分类
     * @param ids
     * @return
     */
    @DeleteMapping
//相同点:
//这两个都是用来处理前端传递过来的请求参数
//不同点:
//不同的是RequestParam处理的是请求参数，而PathVariable处理的是路径变量
    public R<String> delete(Long ids){
        log.info("删除分类：{}",ids);
//        categoryService.removeById(id);
        categoryService.remove(ids);
        return R.success("分类信息删除成功");
    }

    /**
     * 修改分类
     * @param category
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody Category category){
        log.info("修改分类信息：{}",category);
        categoryService.updateById(category);
        return R.success("修改信息成功");
    }

    /**
     * 根据条件查询
     * @param category
     * @return
     */
    @GetMapping("/list")
    public R<List<Category>> list(Category category){
//        条件构造器
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
//        添加条件
        queryWrapper.eq(category.getType() != null,Category::getType,category.getType());
//        进行排序
        queryWrapper.orderByDesc(Category::getSort);
        List<Category> list = categoryService.list(queryWrapper);
        return R.success(list);
    }
}
