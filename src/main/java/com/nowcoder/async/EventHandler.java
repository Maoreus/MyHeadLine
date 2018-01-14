package com.nowcoder.async;

import com.nowcoder.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * producer和consumer之间的接口,每个handler都不一样
 */
@Component //在spring加载的时候生成
public interface EventHandler {
    void doHandle(EventModel model);

    //关注的Event
    List<EventType> getSupportEventTypes();
}
