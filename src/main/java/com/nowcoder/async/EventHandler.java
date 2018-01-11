package com.nowcoder.async;

import java.util.List;

/**
 * producer和consumer之间的接口,每个handler都不一样
 */
public interface EventHandler {
    void doHandle(EventModel model);

    //关注的Event
    List<EventType> getSupportEventTypes();
}
