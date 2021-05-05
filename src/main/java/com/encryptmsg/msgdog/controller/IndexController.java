package com.encryptmsg.msgdog.controller;

import com.encryptmsg.msgdog.bo.*;
import com.encryptmsg.msgdog.constants.Constant;
import com.encryptmsg.msgdog.entity.Topic;
import com.encryptmsg.msgdog.service.TopicService;
import com.encryptmsg.msgdog.utils.HTMLFilter;
import com.encryptmsg.msgdog.utils.HmacShaUtils;
import com.encryptmsg.msgdog.utils.TinkUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.Instant;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Controller
public class IndexController {

    @Autowired
    private TopicService topicService;

    @Value("${topic.secretKey.value}")
    private String secretKey;

    @Value("${topic.pathKey}")
    private String pathKey;

    @Value("${topic.secondPwdKey}")
    private String secondPwdKey;


    @Value("${topic.expireDay}")
    private Long expireDay;

    @Value("${topic.readSecond}")
    private Long readSecond;

    @Value("${topic.tokenSecond}")
    private Long tokenSecond;

    @Value("${topic.secondPwdLimitLength}")
    private int secondPwdLimitLength;


    @Autowired
    private RedisTemplate redisTemplate;

    public static int count = 1;


    @RequestMapping("/getToken")
    @CrossOrigin
    @ResponseBody
    public HashMap getToken() throws IOException {
        HashMap resultMap = new HashMap();
        String path = UUID.randomUUID().toString().replaceAll("-", "").toUpperCase();
        redisTemplate.opsForValue().set(path, "1");
        redisTemplate.expire(path, tokenSecond, TimeUnit.SECONDS);
        resultMap.put("state", "T00000");
        resultMap.put("data", path);
        return resultMap;
    }


    @RequestMapping("/deleteInfo")
    @CrossOrigin
    @ResponseBody
    public HashMap deleteInfo(@RequestBody DeleteInfoRequest deleteInfoRequest) throws IOException {
        HashMap resultMap = new HashMap();
        String deleteToken = deleteInfoRequest.getDeleteToken();
        String path = deleteInfoRequest.getPath();
        String encryptPath = HmacShaUtils.encryptPath(path, pathKey);
        if (StringUtils.isAnyBlank(path, deleteToken)) {
            resultMap.put("state", "T00403");
            resultMap.put("data", "params error");
            return resultMap;
        }

        //delete db
        topicService.deleteTopicByPath(encryptPath);
        redisTemplate.delete(encryptPath);
        //delete cache
        resultMap.put("state", "T00000");
        resultMap.put("data", "ok");
        return resultMap;
    }

    @PostMapping("/encryptInfo")
    @CrossOrigin
    @ResponseBody
    public HashMap encryptInfo(@RequestBody EncryptInfoRequest encryptInfoRequest) throws IOException {
        HashMap resultMap = new HashMap();
        if (encryptInfoRequest == null) {
            resultMap.put("state", "T00403");
            resultMap.put("data", "params error");
            return resultMap;
        }
        String path = encryptInfoRequest.getPath();
        String userContent = encryptInfoRequest.getContent();
        //xss attack
        String content = new HTMLFilter().filter(userContent);
        Object tokenObject = redisTemplate.opsForValue().get(path);
        if (tokenObject == null) {
            resultMap.put("state", "T00401");
            resultMap.put("data", "expire token");
            return resultMap;
        }
        String salt = UUID.randomUUID().toString().replaceAll("-", "");
        String associatedData = HmacShaUtils.getAssociatedData(salt, secretKey);
        Instant instant = Instant.now();
        long timeStampMillis = instant.toEpochMilli();
        Topic topic = new Topic();
        CiphertextBase64Bo ciphertextBase64Bo = TinkUtil.encrypt(content, associatedData);
        topic.setCipherText(ciphertextBase64Bo.getCipherText());
        topic.setEnable(1);
        topic.setKeyStoreText(ciphertextBase64Bo.getKeyStoreText());
        topic.setCreateTime(timeStampMillis);
        //encrypt path
        String encryptPath = HmacShaUtils.encryptPath(path, pathKey);
        topic.setPath(encryptPath);
        topic.setSalt(salt);

        if (encryptInfoRequest.getExpire() != null && encryptInfoRequest.getExpire() > 0) {
            Long expireParam = encryptInfoRequest.getExpire();
            //604800 7days
            if (expireParam > 604800) {
                resultMap.put("state", "T00404");
                resultMap.put("data", "error param");
                return resultMap;
            }
            topic.setExpire(expireParam);
        } else {
            //default read time
            topic.setExpire(readSecond);
        }
        String secondPasswordParam = encryptInfoRequest.getSecondPassword();
        if (StringUtils.isBlank(secondPasswordParam)) {
            topic.setSecondPassword(Constant.DEFAULT_SECOND_PASSWORD);
        } else {
            if(secondPasswordParam.length() > secondPwdLimitLength){
                resultMap.put("state", "T00403");
                resultMap.put("data", "error param");
                return resultMap;
            }
            topic.setSecondPassword(HmacShaUtils.encryptSecondPwd(secondPasswordParam, secondPwdKey));
        }
        String emailParam = encryptInfoRequest.getEmail();
        if (StringUtils.isBlank(emailParam)) {
            topic.setEmail(Constant.DEFAULT_EMAIL);
        } else {
            topic.setEmail(emailParam);
        }
        //save to db
        topicService.insertTopic(topic);
        ObjectMapper objectMapper = new ObjectMapper();
        String topicCache = objectMapper.writeValueAsString(topic);
        //add Cache
        redisTemplate.opsForValue().set(encryptPath, topicCache);
        redisTemplate.expire(encryptPath, expireDay, TimeUnit.DAYS);

        resultMap.put("state", "T00000");
        resultMap.put("data", path);
        return resultMap;
    }


    @PostMapping("/decryptInfo")
    @CrossOrigin
    @ResponseBody
    public HashMap decryptInfo(@RequestBody DecryptInfoRequest decryptInfoRequest) throws Exception {
        HashMap resultMap = new HashMap();
        if (decryptInfoRequest == null) {
            resultMap.put("state", "T00403");
            resultMap.put("data", "params error");
            return resultMap;
        }
        String path = decryptInfoRequest.getPath();
        String encryptPath = HmacShaUtils.encryptPath(path, pathKey);
        //get topic from cache
        Object topicCacheObject = redisTemplate.opsForValue().get(encryptPath);
        ObjectMapper objectMapper = new ObjectMapper();
        if (topicCacheObject != null) {
            String topicCache = topicCacheObject.toString();
            Topic topic = objectMapper.readValue(topicCache, Topic.class);
            //check secondPassword
            String secondPwd = topic.getSecondPassword();
            if (StringUtils.isNotBlank(secondPwd)) {
                resultMap.put("state", "T00301");
                resultMap.put("data", "need password page");
                return resultMap;
            }
            String cipherText = topic.getCipherText();
            String keyStoreText = topic.getKeyStoreText();
            String salt = topic.getSalt();
            String associatedData = HmacShaUtils.getAssociatedData(salt, secretKey);
            String plainText = TinkUtil.decrypt(cipherText, keyStoreText, associatedData);
            //get topic ttl in cache
            Long ttl = redisTemplate.getExpire(encryptPath);
            Long userReadSecond = topic.getExpire();
            if (ttl > userReadSecond) {
                redisTemplate.expire(encryptPath, userReadSecond, TimeUnit.SECONDS);
            }
            Long currentTtl = redisTemplate.getExpire(encryptPath);
            //remove db topic
            Integer topicId = topic.getId();
            topicService.deleteTopic(topicId);
            if (ttl > 0) {
                resultMap.put("state", "T00000");
                resultMap.put("data", plainText);
                resultMap.put("expireTime", currentTtl);
                resultMap.put("deleteToken", "0");
            } else {
                resultMap.put("state", "T00404");
                resultMap.put("data", "404");
            }
            return resultMap;
        } else {
            //query db
            Topic topic = topicService.queryTopicByPath(encryptPath);
            if (topic == null) {
                resultMap.put("state", "T00404");
                resultMap.put("data", "404");
                return resultMap;
            }
            //check secondPassword
            String secondPwd = topic.getSecondPassword();
            if (StringUtils.isNotBlank(secondPwd)) {
                resultMap.put("state", "T00301");
                resultMap.put("data", "need password page");
                return resultMap;
            }
            String cipherText = topic.getCipherText();
            String keyStoreText = topic.getKeyStoreText();
            String salt = topic.getSalt();
            String associatedData = HmacShaUtils.getAssociatedData(salt, secretKey);
            String plainText = TinkUtil.decrypt(cipherText, keyStoreText, associatedData);
            String topicCache = objectMapper.writeValueAsString(topic);
            redisTemplate.opsForValue().set(encryptPath, topicCache);
            Long userReadSecond = topic.getExpire();
            //user custom read time
            redisTemplate.expire(encryptPath, userReadSecond, TimeUnit.SECONDS);
            //remove db topic
            Integer topicId = topic.getId();
            topicService.deleteTopic(topicId);
            resultMap.put("state", "T00000");
            resultMap.put("data", plainText);
            resultMap.put("expireTime", readSecond);
            resultMap.put("deleteToken", "0");
            return resultMap;
        }

    }


    @PostMapping("/decryptInfoWithPassword")
    @CrossOrigin
    @ResponseBody
    public HashMap decryptInfoWithPassword(@RequestBody DecryptInfoPwdRequest decryptInfoPwdRequest) throws Exception {
        HashMap resultMap = new HashMap();
        if (decryptInfoPwdRequest == null) {
            resultMap.put("state", "T00403");
            resultMap.put("data", "params error");
            return resultMap;
        }
        String path = decryptInfoPwdRequest.getPath();
        String encryptPath = HmacShaUtils.encryptPath(path, pathKey);
        String secondPwdParam = decryptInfoPwdRequest.getSecondPassword();
        String lang = decryptInfoPwdRequest.getLang();
        if (StringUtils.isAnyBlank(secondPwdParam, path) || secondPwdParam.length() > secondPwdLimitLength) {
            resultMap.put("state", "T00403");
            resultMap.put("data", "params error");
            return resultMap;
        }
        Object topicCacheObject = redisTemplate.opsForValue().get(encryptPath);
        ObjectMapper objectMapper = new ObjectMapper();
        if (topicCacheObject != null) {
            String topicCache = topicCacheObject.toString();
            Topic topic = objectMapper.readValue(topicCache, Topic.class);
            //check second password
            String secondPwd = topic.getSecondPassword();
            if (StringUtils.isBlank(secondPwd)) {
                resultMap.put("state", "T00406");
                resultMap.put("data", "request api error");
                return resultMap;
            } else {
                //not equal password
                String secondPwdEncrypt = HmacShaUtils.encryptSecondPwd(secondPwdParam, secondPwdKey);
                if (!secondPwd.equals(secondPwdEncrypt)) {
                    resultMap.put("state", "T00407");
                    resultMap.put("data", "without permission");
                    return resultMap;
                }
            }
            String cipherText = topic.getCipherText();
            String email = topic.getEmail();
            String keyStoreText = topic.getKeyStoreText();
            String salt = topic.getSalt();
            String associatedData = HmacShaUtils.getAssociatedData(salt, secretKey);
            String plainText = TinkUtil.decrypt(cipherText, keyStoreText, associatedData);
            Long userReadSecond = topic.getExpire();
            Long ttl = redisTemplate.getExpire(encryptPath);
            if (ttl > userReadSecond) {
                //user custom read time
                redisTemplate.expire(encryptPath, userReadSecond, TimeUnit.SECONDS);
            }
            Long currentTtl = redisTemplate.getExpire(encryptPath);
            //remove db topic
            Integer topicId = topic.getId();
            topicService.deleteTopic(topicId);
            if (ttl > 0) {
                resultMap.put("state", "T00000");
                resultMap.put("data", plainText);
                resultMap.put("expireTime", currentTtl);
                resultMap.put("deleteToken", "0");
            } else {
                resultMap.put("state", "T00404");
                resultMap.put("data", "404");
            }
            return resultMap;
        } else {
            //query db
            Topic topic = topicService.queryTopicByPath(encryptPath);
            if (topic == null) {
                resultMap.put("state", "T00404");
                resultMap.put("data", "404");
                return resultMap;
            }
            //check second password
            String secondPwd = topic.getSecondPassword();
            if (StringUtils.isBlank(secondPwd)) {
                resultMap.put("state", "T00406");
                resultMap.put("data", "request error");
                return resultMap;
            } else {
                String secondPwdEncrypt = HmacShaUtils.encryptSecondPwd(secondPwdParam, secondPwdKey);
                //not equal password
                if (!secondPwd.equals(secondPwdEncrypt)) {
                    resultMap.put("state", "T00407");
                    resultMap.put("data", "without permission");
                    return resultMap;
                }
            }
            String cipherText = topic.getCipherText();
            String keyStoreText = topic.getKeyStoreText();
            String email = topic.getEmail();
            String salt = topic.getSalt();
            String associatedData = HmacShaUtils.getAssociatedData(salt, secretKey);
            String plainText = TinkUtil.decrypt(cipherText, keyStoreText, associatedData);
            String topicCache = objectMapper.writeValueAsString(topic);
            redisTemplate.opsForValue().set(encryptPath, topicCache);
            Long userReadSecond = topic.getExpire();
            //user custom read time
            redisTemplate.expire(encryptPath, userReadSecond, TimeUnit.SECONDS);
            //remove db topic
            Integer topicId = topic.getId();
            topicService.deleteTopic(topicId);
            resultMap.put("state", "T00000");
            resultMap.put("data", plainText);
            resultMap.put("expireTime", userReadSecond);
            resultMap.put("deleteToken", "0");
            return resultMap;
        }

    }

}