package com.encryptmsg.msgdog.service.impl;

import com.encryptmsg.msgdog.entity.Topic;
import com.encryptmsg.msgdog.mapper.TopicMapper;
import com.encryptmsg.msgdog.service.TopicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TopicServiceImpl implements TopicService {

    @Autowired
    private TopicMapper topicMapper;

    @Override
    public int insertTopic(Topic topic) {
        return topicMapper.insertTopic(topic);
    }

    @Override
    public Topic queryTopicByPath(String path) {
        return topicMapper.queryTopicByPath(path);
    }

    @Override
    public int deleteTopic(Integer id) {
        return topicMapper.deleteTopic(id);
    }

    @Override
    public int deleteTopicByPath(String path) {
        return topicMapper.deleteTopicByPath(path);
    }

    @Override
    public String querySecondPasswordByPath(String path) {
        return topicMapper.querySecondPasswordByPath(path);
    }
}
