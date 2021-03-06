package com.nowcoder.util;

import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.MessageDigest;
import java.util.Map;

public class ToutiaoUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(ToutiaoUtil.class);

    public static String[] Image_fileName = new String[] {"png", "jpg", "bmp", "jpeg"};

    public static String ImageDir = "D:/";

    /**
     * 七牛外链域名
     */
    public static String QiuNiu_Domain_Prefix = "";

    /**
     * 头条域名
     */
    public static String Toutiao_Domain = "http://127.0.0.1:8080/";

    public static boolean isFileAllowed(String file_ext){
        for (String ext : Image_fileName){
            if (ext.equals(file_ext)){
                return true;
            }
        }
        return false;
    }

    public static String MD5(String key) {
        char hexDigits[] = {
                '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'
        };
        try {
            byte[] btInput = key.getBytes();
            // 获得MD5摘要算法的 MessageDigest 对象
            MessageDigest mdInst = MessageDigest.getInstance("MD5");
            // 使用指定的字节更新摘要
            mdInst.update(btInput);
            // 获得密文
            byte[] md = mdInst.digest();
            // 把密文转换成十六进制的字符串形式
            int j = md.length;
            char str[] = new char[j * 2];
            int k = 0;
            for (int i = 0; i < j; i++) {
                byte byte0 = md[i];
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(str);
        } catch (Exception e) {
            LOGGER.error("生成MD5失败", e);
            return null;
        }
    }

    public static String getJSONString(int code){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("code", code);
        return jsonObject.toJSONString();
    }

    public static String getJSONString(int code, Map<String, Object> map){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("code", code);
        for (Map.Entry<String, Object> entry: map.entrySet()){
            map.put(entry.getKey(), entry.getValue());
        }
        return jsonObject.toJSONString();
    }

    public static String getJSONString(int code, String message){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("code", code);
        jsonObject.put("message", message);
        return jsonObject.toJSONString();
    }
}
