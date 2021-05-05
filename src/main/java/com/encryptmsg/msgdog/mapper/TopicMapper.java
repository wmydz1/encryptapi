package com.encryptmsg.msgdog.mapper;

import com.encryptmsg.msgdog.entity.Topic;

public interface TopicMapper {

    public int insertTopic(Topic topic);

    public Topic queryTopicByPath(String path);

    public int deleteTopic(Integer id);

    public int deleteTopicByPath(String path);

    public String querySecondPasswordByPath(String path);
}
