package com.nowcoder.controller;

import com.nowcoder.model.*;
import com.nowcoder.service.CommentService;
import com.nowcoder.service.NewsService;
import com.nowcoder.service.QiNiuService;
import com.nowcoder.service.UserService;
import com.nowcoder.util.ToutiaoUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
public class NewsController {

    @Autowired
    UserService userService;

    @Autowired
    private NewsService newsService;

    @Autowired
    QiNiuService qiNiuService;

    @Autowired
    HostHolder hostHolder;

    @Autowired
    CommentService commentService;
    private static final Logger LOGGER = LoggerFactory.getLogger(NewsController.class);


    /**
     * 返回资讯detail的页面
     * @param newsId
     * @param model
     * @return
     */
    @RequestMapping(path = {"/news/{newsId}"}, method = RequestMethod.GET)
    public String newsDetail(@PathVariable("newsId") int newsId, Model model){
        News news = newsService.getById(newsId);
        if (news != null){
            List<Comment> comments = commentService.getComment(news.getId(), EntityType.ENTITY_COMMENT);
            List<ViewObject> commentvo = new ArrayList<>();
            for (Comment comment : comments){
                ViewObject vo = new ViewObject();
                vo.set("comment", comment);
                vo.set("user", news.getUserId());
                commentvo.add(vo);
            }
            model.addAttribute("comments", commentvo);
        }

        //视图
        model.addAttribute("news", news);
        model.addAttribute("owner", userService.getUser(news.getUserId()));
        return "detail";
    }

    /**
     * 添加资讯
     * @param newsId
     * @param content
     * @return
     */
    @RequestMapping(path = {"/addComment"}, method = RequestMethod.POST)
    public String addComment(@RequestParam("newsId") int newsId,
                             @RequestParam("content") String content){
        try {
            Comment comment = new Comment();
            comment.setUserId(hostHolder.getUser().getId());
            comment.setContent(content);
            comment.setEntityId(newsId);
            comment.setEntityType(EntityType.ENTITY_NEWS);
            comment.setCreatedDate(new Date());
            comment.setStatus(0);

            commentService.addComment(comment);
            //更新news中的评论数量
            int count = commentService.getCommentCount(comment.getEntityId(), comment.getEntityType());
            newsService.updateCommentCount(comment.getEntityId(), count);

            //异步化
        }catch (Exception e){
            LOGGER.error("提交评论错误", e);
        }

        return "redirect:/news/" + String.valueOf(newsId);
    }


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
