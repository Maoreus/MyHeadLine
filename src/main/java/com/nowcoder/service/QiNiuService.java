package com.nowcoder.service;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.nowcoder.util.ToutiaoUtil;
import com.qiniu.common.QiniuException;
import com.qiniu.common.Zone;
import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.util.Auth;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
public class QiNiuService {

    private static final Logger LOGGER = LoggerFactory.getLogger(QiNiuService.class);

    //构造一个带指定Zone对象的配置类
    Configuration cfg = new Configuration(Zone.zone0());
//...其他参数参考类注释

    UploadManager uploadManager = new UploadManager(cfg);
    //...生成上传凭证，然后准备上传
    String accessKey = "your access key";
    String secretKey = "your secret key";
    String bucket = "your bucket name";
    //如果是Windows情况下，格式是 D:\\qiniu\\test.png
    String localFilePath = "/home/qiniu/test.png";
    //默认不指定key的情况下，以文件内容的hash值作为文件名
    String key = null;

    Auth auth = Auth.create(accessKey, secretKey);
    String upToken = auth.uploadToken(bucket);

    public String saveImage(MultipartFile file) throws IOException{
        int dotPos = file.getOriginalFilename().lastIndexOf(".");
        if (dotPos < 0){
            return null;
        }
        String fileExt = file.getOriginalFilename().substring(dotPos + 1);
        if (!ToutiaoUtil.isFileAllowed(fileExt.toLowerCase())){
            return null;
        }

        String fileName = UUID.randomUUID().toString().replaceAll("-", "") + "." + fileExt;
        //七牛的回显
        Response response = uploadManager.put(file.getBytes(), fileName, upToken);
        if (response.isOK() && response.isJson()){
            String key = JSONObject.parseObject(response.bodyString()).get("key").toString();
            return ToutiaoUtil.QiuNiu_Domain_Prefix;
        }
        else {
            LOGGER.error("七牛异常", response.bodyString());
            return null;
        }

    }


}
