package com.yu.controller;

import com.yu.common.R;
import com.yu.service.qiniu.QiniuService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/common")
public class CommonController {

    @Value("${Image.path}")
    private String path;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private QiniuService service;

    @PostMapping("/upload")
    public R<String> upload(MultipartFile file){
        String originalFilename = file.getOriginalFilename();
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        String fileName = UUID.randomUUID()+suffix;
        File dir=new File(path);
        try {
            String s = service.uploadFile(file.getBytes(), fileName);
            redisTemplate.opsForSet().add("dishImageResourceCache",fileName);
            return R.success(fileName);
        } catch (IOException e) {
            log.error(originalFilename+"文件上传失败");
            return R.error("上传失败");
        }
    }


    @GetMapping("/download")
    public void download(String name, HttpServletResponse response){
        URL s=null;
        try {
            s=new URL("http://rh0mlncw3.bkt.clouddn.com/"+name);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
        try(
                BufferedInputStream bis = new BufferedInputStream(s.openStream());
                ServletOutputStream outputStream = response.getOutputStream();
        ) {
            byte[] bytes=new byte[1024];
            int len=0;
            while ((len=bis.read(bytes))!=-1){
                outputStream.write(bytes,0,len);
                outputStream.flush();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    /*@GetMapping("/download")
    public void download(String name, HttpServletResponse response){
        try(
                FileInputStream fis=new FileInputStream(path+name);
                BufferedInputStream bis=new BufferedInputStream(fis);
                ServletOutputStream outputStream = response.getOutputStream();
                ) {
            byte[] bytes=new byte[1024];
            int len=0;
            while ((len=bis.read(bytes))!=-1){
                outputStream.write(bytes,0,len);
                outputStream.flush();
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }*/
    /*@PostMapping("/upload")
    public R<String> upload(MultipartFile file){

        String originalFilename = file.getOriginalFilename();
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));

        String fileName = UUID.randomUUID()+suffix;
        File dir=new File(path);
        if (!dir.exists())
            dir.mkdirs();
        try {
            file.transferTo(new File(path+fileName));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return R.success(fileName);
    }*/
}
