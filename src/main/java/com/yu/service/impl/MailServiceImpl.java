package com.yu.service.impl;

import com.yu.service.MailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 邮件服务
 *
 * @Author niujinpeng
 * @Date 2019/3/10 21:45
 */
@Service
@Slf4j
public class MailServiceImpl implements MailService {

    @Value("${spring.mail.username}")
    private String from;

    @Autowired
    private JavaMailSender mailSender;

    /**
     * 发送简单文本邮件
     *
     * @param to
     * @param content
     */
    public void sendSimpleTextMail(String to, String content) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("余的项目验证码");
        String c="[服务]尊敬的用户,您好:\n本次请求的验证码为:"+content+"，验证码只有3分钟有效时间，请妥善保管\n如非本人操作，请忽略该邮件。\n(这是一封自动发送的邮件，请不要直接回复）";
        message.setText(c);
        message.setFrom(from+"(外卖项目)");
        mailSender.send(message);
        log.info("【文本邮件】成功发送！to={}", to);
    }
}