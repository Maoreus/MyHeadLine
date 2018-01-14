package com.nowcoder.controller;

import com.nowcoder.async.EventModel;
import com.nowcoder.async.EventProducer;
import com.nowcoder.async.EventType;
import com.nowcoder.model.EntityType;
import com.nowcoder.service.UserService;
import com.nowcoder.util.ToutiaoUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@Controller
public class LoginController {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    UserService userService;

    @Autowired
    EventProducer eventProducer;

    @RequestMapping(path = {"/login/"}, method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public String login(Model model, @RequestParam("username") String username,
                           @RequestParam("password") String pwd,
                           @RequestParam(value = "rember", defaultValue = "0") int remember,
                           HttpServletResponse response)
    {
        try {
            Map<String, Object> map = userService.register(username, pwd);
            if (map.containsKey("ticket")){
                Cookie cookie = new Cookie("ticket", map.get("ticket").toString());
                response.addCookie(cookie);

                //登录异常校验
                eventProducer.fireEvent(new EventModel(
                        EventType.LOGIN).setActorId((int) map.get("userId"))
                        //扩展信息
                        .setExt("username", username)
                        .setExt("email", "xxx@qq.com"));
                //设置cookie路径为全量有效
                cookie.setPath("/");
                //用户保持登陆的话就给他记住五天
                if (remember > 0){
                    cookie.setMaxAge(3600*24*5);
                }
                return ToutiaoUtil.getJSONString(0, "注册成功");
            }
            else {
                return ToutiaoUtil.getJSONString(1, map);
            }
        }
        catch (Exception e){
            LOGGER.error("注册异常", e);
            return ToutiaoUtil.getJSONString(1, "注册异常");
        }
    }

    /**
     * 登录
     * @param model
     * @param username
     * @param pwd
     * @param remember
     * @param response
     * @return
     */
    @RequestMapping(path = {"/reg/"}, method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public String register(Model model, @RequestParam("username") String username,
                           @RequestParam("password") String pwd,
                           @RequestParam(value = "rember", defaultValue = "0") int remember,
                           HttpServletResponse response)
    {
        try {
            Map<String, Object> map = userService.register(username, pwd);
            if (map.containsKey("ticket")){
                return ToutiaoUtil.getJSONString(0, "注册成功");
            }
            else {
                return ToutiaoUtil.getJSONString(1, map);
            }
        }
        catch (Exception e){
            LOGGER.error("注册异常", e);
            return ToutiaoUtil.getJSONString(1, "注册异常");
        }
    }

    /**
     * 退出登录后返回首页
     * @param ticket
     * @return
     */
    @RequestMapping(path = {"/logout/"}, method = {RequestMethod.GET, RequestMethod.POST})
    //@ResponseBody 使用它会被当成字符串处理
    public String logout(@CookieValue("ticket") String ticket){
        userService.logout(ticket);
        return "redirect:/";
    }

}
