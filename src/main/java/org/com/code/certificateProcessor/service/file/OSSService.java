package org.com.code.certificateProcessor.service.file;

import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.CannedAccessControlList;
import com.aliyun.oss.model.CreateBucketRequest;
import org.com.code.certificateProcessor.exeption.OSSException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Component
public class OSSService {
    //避免硬编码
    @Value("${aliyun.oss.endpoint}")
    private String ENDPOINT;
    @Value("${aliyun.oss.accessKeyId}")
    private String ACCESS_KEY_ID;
    @Value("${aliyun.oss.accessKeySecret}")
    private String ACCESS_KEY_SECRET;
    @Value("${aliyun.oss.bucketName}")
    private String BUCKET_NAME;
    @Value("${aliyun.oss.bucketDomain}")
    private String BUCKET_DOMAIN;

    public OSSClient getOSSClient(){
        OSSClient ossClient = new OSSClient(ENDPOINT, ACCESS_KEY_ID, ACCESS_KEY_SECRET);

        if(!ossClient.doesBucketExist(BUCKET_NAME)){
            System.out.println("Bucket 不存在,重新创建...." + BUCKET_NAME);
            CreateBucketRequest createBucketRequest = new CreateBucketRequest(BUCKET_NAME);
            createBucketRequest.setCannedACL(CannedAccessControlList.PublicReadWrite);
            ossClient.createBucket(createBucketRequest);
            System.out.println("Bucket 创建成功...." + BUCKET_NAME);
        }
        return ossClient;
    }

    // 获取OSS文件路径
    public String getFilePath(String fileName, String mimeType){
        StringBuilder url = new StringBuilder();

        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        String formattedDate = now.format(formatter);
        String uuid = UUID.randomUUID().toString().replace("-", "");
        return  url.append(mimeType).append("/").append(formattedDate).append("/")
                .append(SecurityContextHolder.getContext().getAuthentication().getName()).append("/")
                .append(uuid).append("-").append(fileName).toString();
    }

    public void deleteFile(String URL){
        OSSClient ossClient= getOSSClient();
        try {
            /**
             *  URL = "http://sk8erboi.oss-cn-fuzhou.aliyuncs.com/video/20250525/19233392121872384/94c0d50c88524b0494f76ae8112b1bf1-肯德基疯狂星期四.mp4"
             * BUCKET_DOMAIN = "http://sk8erboi.oss-cn-fuzhou.aliyuncs.com"
             *
             * => key = "video/20250525/19233392121872384/94c0d50c88524b0494f76ae8112b1bf1-肯德基疯狂星期四.mp4"
             */
            String key = URL.replace(BUCKET_DOMAIN+"/", "");
            ossClient.deleteObject(BUCKET_NAME, key);
        } catch (Exception e) {
            throw new OSSException("删除文件失败",e);
        }
        ossClient.shutdown();
    }

    public String getBucketName() {
        return this.BUCKET_NAME;
    }

    public String getBucketDomain() {
        return this.BUCKET_DOMAIN;
    }
}
