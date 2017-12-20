package com.nowcoder.controller;

import com.nowcoder.model.HostHolder;
import com.nowcoder.model.News;
import com.nowcoder.service.NewsService;
import com.nowcoder.service.QiNiuService;
import com.nowcoder.util.ToutiaoUtil;
import org.apache.tomcat.util.http.fileupload.util.Streams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Date;

@Controller
public class NewsController {

    @Autowired
    private NewsService newsService;

    @Autowired
    QiNiuService qiNiuService;

    @Autowired
    HostHolder hostHolder;

    private static final Logger LOGGER = LoggerFactory.getLogger(NewsController.class);

    @RequestMapping(path = {"/image"}, method = RequestMethod.GET)
    @ResponseBody
    public void getImage(@RequestParam("filename") String fileName,
                           HttpServletResponse response){
        response.setContentType("image/jpeg");
        try {
            //这是已经保存过的图片，从目录中把二进制流读取出来，之后直接写到response里面
            StreamUtils.copy(new FileInputStream(new File(
                    ToutiaoUtil.ImageDir + fileName)), response.getOutputStream());
        } catch (IOException e) {
            LOGGER.error("读取图片失败", e);
        }
    }

    @RequestMapping(path = {"/user/addNews/"}, method = RequestMethod.GET)
    @ResponseBody
    public String addNews(@RequestParam("image") String image,
                        @RequestParam("title") String title,
                        @RequestParam("link") String link){
        try{
            News news = new News();
            //判断用户是否登录
            if (hostHolder.getUser() != null){
                news.setUserId(hostHolder.getUser().getId());
            }
            else {
                //匿名用户
                news.setUserId(3);
            }
            news.setImage(image);
            news.setCreatedDate(new Date());
            news.setTitle(title);
            news.setLink(link);
            newsService.addNews(news);
            return ToutiaoUtil.getJSONString(0);
        }catch (Exception e){
            LOGGER.error("添加资讯失败", e);
            return ToutiaoUtil.getJSONString(1, "发布失败");
        }


    }


    /**
     * 上传图片
     * @param file
     * @return
     */
    @RequestMapping(path = {"/uploadImage/"}, method = RequestMethod.POST)
    public String uploadImage(@RequestParam("file") MultipartFile file){
        try {
            String fileUrl = qiNiuService.saveImage(file);
            if (fileUrl == null){
                return ToutiaoUtil.getJSONString(1, "上传失败");
            }
            return ToutiaoUtil.getJSONString(0, fileUrl);
        }
        catch (Exception e){
            LOGGER.error("上传图片失败", e );
            return ToutiaoUtil.getJSONString(1, "上传失败");
        }
    }


}
