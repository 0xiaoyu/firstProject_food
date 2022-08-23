package com.yu.service.qiniu;

import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;
import com.qiniu.util.StringMap;
import com.yu.domain.qiniu.Qiniuyun;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.InputStream;

@Service
public class QiniuServiceImpl implements QiniuService{
    @Autowired
    private UploadManager uploadManager;

    @Autowired
    private BucketManager bucketManager;

    @Autowired
    private Auth auth;

    @Autowired
    private Qiniuyun qiniuyun;

    /**
     * 定义七牛云上传的相关策略
     */
    private StringMap putPolicy;


    @Override
    public String uploadFile(String file, String fileName) throws QiniuException {
        File f=new File(file);
        return this.uploadFile(f,fileName);
    }

    @Override
    public String uploadFile(byte[] file, String fileName) throws QiniuException {
        Response response = this.uploadManager.put(file, fileName, getUploadToken());
        int retry = 0;
        while (response.needRetry() && retry < 3) {
            response = this.uploadManager.put(file, fileName, getUploadToken());
            retry++;
        }
        if (response.statusCode == 200) {
            return "http://" + qiniuyun.getDomain() + "/" + fileName;
        }
        return "上传失败!";
    }

    @Override
    public String uploadFile(File file, String fileName) throws QiniuException {
        Response response = this.uploadManager.put(file, fileName, getUploadToken());
        int retry = 0;
        while (response.needRetry() && retry < 3) {
            response = this.uploadManager.put(file, fileName, getUploadToken());
            retry++;
        }
        if (response.statusCode == 200) {
            return "http://" + qiniuyun.getDomain() + "/" + fileName;
        }
        return "上传失败!";
    }


    @Override
    public String uploadFile(InputStream inputStream, String fileName) throws QiniuException {
        Response response = this.uploadManager.put(inputStream, fileName, getUploadToken(), null, null);
        int retry = 0;
        while (response.needRetry() && retry < 3) {
            response = this.uploadManager.put(inputStream, fileName, getUploadToken(), null, null);
            retry++;
        }
        if (response.statusCode == 200) {
            return "http://" + qiniuyun.getDomain() + "/" + fileName;
        }
        return "上传失败!";
    }


    @Override
    public String delete(String key) throws QiniuException {
        Response response = bucketManager.delete(qiniuyun.getBucket(), key);
        int retry = 0;
        while (response.needRetry() && retry++ < 3) {
            response = bucketManager.delete(qiniuyun.getBucket(), key);
        }
        return response.statusCode == 200 ? "删除成功!" : "删除失败!";
    }

    public void afterPropertiesSet() throws Exception {
        this.putPolicy = new StringMap();
        putPolicy.put("returnBody", "{\"key\":\"$(key)\",\"hash\":\"$(etag)\",\"bucket\":\"$(bucket)\",\"width\":$(imageInfo.width), \"height\":${imageInfo.height}}");
    }

    /**
     * 获取上传凭证
     */
    private String getUploadToken() {
        return this.auth.uploadToken(qiniuyun.getBucket(), null, 3600, putPolicy);
    }
}
