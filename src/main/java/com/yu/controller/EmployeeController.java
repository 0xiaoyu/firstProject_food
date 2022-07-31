package com.yu.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yu.common.R;
import com.yu.domain.Employee;
import com.yu.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    /**
     * 员工登录
     *
     * @param request
     * @param employee
     * @return
     */
    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee) {

        //查询用户名是否存在
        LambdaQueryWrapper<Employee> lqw = new LambdaQueryWrapper<>();
        lqw.eq(Employee::getUsername, employee.getUsername());
        Employee one = employeeService.getOne(lqw);
        if (one == null) {
            //不存在返回失败
            return R.error("用户名不存在");
        }

        //用md5加密password
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());

        //比对密码是否正确
        if (!one.getPassword().equals(password)) {
            return R.error("密码错误");
        }

        //比对员工状态
        if (one.getStatus() == 0)
            return R.error("该用户被禁用");

        //登录成功，将员工id返回到session
        request.getSession().setAttribute("employee",one.getId());
        return R.success(one);
    }

    /**
     * 员工退出
     * @param request
     * @return
     */
    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request){
        request.getSession().removeAttribute("employee");
        return R.success("退出成功");

    }

    /**
     * 分页查询员工
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name){
        Page iPage=new Page(page,pageSize);

        LambdaQueryWrapper<Employee> lqw=new LambdaQueryWrapper();
        lqw.like(!StringUtils.isEmpty(name),Employee::getName,name==null? null :name.trim());
        lqw.orderByDesc(Employee::getUpdateTime);

        employeeService.page(iPage,lqw);

        return R.success(iPage);
    }

    /**
     * 添加员工
     * @param employee
     * @return
     */
    @PostMapping
    public R<String> add(HttpServletRequest request,@RequestBody Employee employee){
        /*employee.setCreateTime(LocalDateTime.now());
        employee.setUpdateTime(LocalDateTime.now());
        Long id = (Long) request.getSession().getAttribute("employee");
        employee.setCreateUser(id);
        employee.setUpdateUser(id);*/
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));

        boolean b = employeeService.save(employee);
        if (b){
            return R.success("添加成功");
        }else {
            return R.error("添加失败");
        }

    }

    /**
     * 更新数据
     * @param employee
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody Employee employee){
        /*Long upId = (Long) request.getSession().getAttribute("employee");
        employee.setUpdateTime(LocalDateTime.now());
        employee.setUpdateUser(upId);*/
        if (employee.getId()==1)
            return R.error("不能修改管理员用户");
        employeeService.updateById(employee);
        return R.success("修改成功");
    }

    /**
     * 根据id查询
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<Employee> selectById(@PathVariable Long id){
        Employee employee = employeeService.getById(id);
        return R.success(employee);
    }


}
