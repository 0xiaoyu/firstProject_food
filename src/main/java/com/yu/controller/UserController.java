package com.yu.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yu.common.R;
import com.yu.domain.Euser;
import com.yu.service.EuserService;
import com.yu.service.impl.MailServiceImpl;
import com.yu.utils.ValidateCodeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private EuserService service;

    @Autowired
    private MailServiceImpl mailService;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 发送验证码
     * @param user
     * @return
     */
    @PostMapping("/sendMsg")
    public R<String> sendMsg(@RequestBody Euser user) {
        String email = user.getEmail();
        if (email != null && !email.isEmpty()) {
            String s = ValidateCodeUtils.generateValidateCode(6).toString();
            System.out.println("code=" + s);
            try {
                mailService.sendSimpleTextMail(email, s);
            }catch (Exception e){
                return R.error("邮箱不存在，发送失败");
            }

            //session.setAttribute("email", s);
            redisTemplate.opsForValue().set(email,s,3, TimeUnit.MINUTES);

            return R.success("验证码发送成功");
        }
        return R.error("发送失败");
    }

    @PostMapping("/login")
    public R<Euser> login(@RequestBody Map map, HttpSession session) {
        //System.out.println(map);
        //String code2 = (String) session.getAttribute("email");
        String email = (String) map.get("email");
        String code = (String) map.get("code");
        String code2 = (String) redisTemplate.opsForValue().get(email);
        if (code2 == null) {
            return R.error("请获取验证码");
        }
        if (code2.equals(code)) {
            LambdaQueryWrapper<Euser> lqw = new LambdaQueryWrapper<>();
            lqw.eq(Euser::getEmail, email);
            Euser one = service.getOne(lqw);
            if (one == null) {
                one = new Euser();
                one.setEmail(email);
                service.save(one);
            }
            session.setAttribute("user", one.getId());
            redisTemplate.delete(email);
            return R.success(one);
        }

        return R.error("登录失败");
    }

    @PostMapping("/loginout")
    public R<String> loginout(HttpSession session) {
        session.removeAttribute("email");
        return R.success("退出成功");
    }
}
