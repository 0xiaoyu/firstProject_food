package com.yu;

import com.qiniu.common.QiniuException;
import com.yu.domain.qiniu.Qiniuyun;
import com.yu.service.qiniu.QiniuService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Slf4j
public class QiniuyunTest {

    @Autowired
    private QiniuService service;

    @Autowired
    private Qiniuyun qiniuyun;

    @Test
    public void upload(){
        try {
            String s = service.uploadFile("D:\\study\\外卖项目\\image\\1405081e-f545-42e1-86a2-f7559ae2e276.jpeg", "d.jpeg");
            log.info(s);
        } catch (QiniuException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void delete(){
        try {
            service.delete("d.jpeg");
        } catch (QiniuException e) {
            throw new RuntimeException(e);
        }
    }


    @Test
    public void download(){

    }

}
