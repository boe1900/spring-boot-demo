package com.bobo.util;


import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 *  消息帮助
 * </p>
 *
 * @author yuys
 * @since 2018-08-21
 */
public class MessageHelper {

    private static RestTemplate restTemplate = new RestTemplate();
    public static final Long RC_AGENT_ID = 184673853L;

    /**
     * 发送企业消息
     * @param accessToken
     * @param message
     * @param userids
     * @param <T>
     */
    public static <T> void sendCorpMsg(String accessToken,T message,String userids){
        String url = "https://oapi.dingtalk.com/topapi/message/corpconversation/asyncsend_v2?access_token="+accessToken;
        Map<String,Object> map = new HashMap<>(3);
        map.put("agent_id", RC_AGENT_ID);
        map.put("userid_list",userids);
        map.put("msg",message);
        ResponseEntity<String> response = restTemplate.postForEntity(url, map, String.class);
    }

}
