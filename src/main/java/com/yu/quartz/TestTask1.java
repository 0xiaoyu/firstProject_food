package com.yu.quartz;

import com.qiniu.common.QiniuException;
import com.yu.service.qiniu.QiniuService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;

@Component
@EnableScheduling
@Slf4j
public class TestTask1 {

    @Qualifier("redisTemplate")
    @Autowired
    private RedisTemplate redis;

    @Autowired
    private QiniuService qiniuService;

    //@Scheduled(cron = "0/5 * * * * ?")
    protected void run() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        System.out.println("TestQuartz01----" + sdf.format(new Date()));
    }

    @Scheduled(cron = "0 0 0/1 * * ?")
    protected void cleanImages() {
        Set<String> set = redis.opsForSet().difference("dishImageResourceCache","dishImageCache");
        int i=1;
        for (String o : set) {
            try {
                qiniuService.delete(o);
                redis.opsForSet().remove("dishImageResourceCache", o);
                log.info("定时清理了"+i+"个残留图片");
                i++;
            } catch (QiniuException e) {
                log.error("Error deleting");
            }
        }
        log.info("本次定时清理完成");
    }
}
