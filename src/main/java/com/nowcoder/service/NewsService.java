package com.nowcoder.service;

import com.nowcoder.dao.NewsDAO;
import com.nowcoder.model.News;
import com.nowcoder.util.ToutiaoUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

@Service
public class NewsService {
    @Autowired
    private NewsDAO newsDAO;

    public List<News> getLatestNews(int userId, int offset, int limit){
        return newsDAO.selectByUserIdAndOffset(userId, offset, limit);
    }

    public int addNews(News news) {
        newsDAO.addNews(news);
        return news.getId();
    }

    public News getById(int newsId) {
        return newsDAO.getById(newsId);
    }

    /**
     * 保存图片
     * @param file
     * @return
     * @throws IOException
     */
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
        Files.copy(file.getInputStream(),
                new File(ToutiaoUtil.ImageDir + fileName).toPath(),
                StandardCopyOption.REPLACE_EXISTING);

        return ToutiaoUtil.Toutiao_Domain + "image/name=" + fileName;

    }

    /**
     * 更新评论数
     * @param id
     * @param count
     * @return
     */
    public int updateCommentCount(int id, int count) {
        return newsDAO.updateCommentCount(id, count);
    }
}
