package com.bobo.controller;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bobo.util.DingTalkMsgTemplate;
import com.bobo.util.MarkdownMessage;
import com.bobo.util.MessageHelper;
import com.xiaoleilu.hutool.http.HttpUtil;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;

import static com.bobo.util.JiraMessageUtil.*;

/**
 *
 * @author huabo
 * @date 2018/8/18
 */
@RestController
public class TestController {
    public static final String JIRA_ISSUE_CREATED = "jira:issue_created";

    public static final String JIRA_ISSUE_UPDATED = "jira:issue_updated";



    @PostMapping("/webHook")
    public String webhook(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String data = IOUtils.toString(request.getInputStream(), StandardCharsets.UTF_8);
        if(!StringUtils.isEmpty(data)){
            JSONObject dataObject = JSON.parseObject(data);
            String webhookEvent = dataObject.getString("webhookEvent");
            if(JIRA_ISSUE_CREATED.equals(webhookEvent) || JIRA_ISSUE_UPDATED.equals(webhookEvent)){
                String issueEventTypeName = dataObject.getString("issue_event_type_name");
                JSONObject issueObject = dataObject.getJSONObject("issue");
                JSONObject issueFields = issueObject.getJSONObject("fields");
                //创建人
                JSONObject creatorObject = issueFields.getJSONObject("creator");
                String creatorName = creatorObject.getString("displayName");
                //issue名称
                String issueName = issueObject.getString("key");
                String issueUrl = BASE_URL+"/"+issueName;
                //issue类型
                JSONObject issueTypeObject = issueFields.getJSONObject("issuetype");
                String issueType = issueTypeObject.getString("name");

                //需求标题
                String summary = issueFields.getString("summary");
                //到期日
                String dueDate = issueFields.getString("duedate");
                //剩余工期
                String remainTime = issueFields.getString("customfield_10605");
                //优先级
                JSONObject priorityObject = issueFields.getJSONObject("priority");
                String priorityName = priorityObject.getString("name");
                String priorityIconUrl = priorityObject.getString("iconUrl");
                //状态
                JSONObject statusObject = issueFields.getJSONObject("status");
                String statusName = statusObject.getString("name");
                //需求内容
                String description = issueFields.getString("description");

                //需求提出人
                String requestMan = issueFields.getJSONObject("customfield_10202") == null ? "" : issueFields.getJSONObject("customfield_10202").getString("displayName");

                //开发负责人
                String developer = issueFields.getJSONObject("customfield_10300") == null ? "" : issueFields.getJSONObject("customfield_10300").getString("displayName");

                //上线审核人
                String check = issueFields.getJSONObject("customfield_10301")==null ? "" : issueFields.getJSONObject("customfield_10301").getString("displayName");

                //测试负责人
                String test =  issueFields.getJSONObject("customfield_10302") == null ? "": issueFields.getJSONObject("customfield_10302").getString("displayName");

                //测试干系人customfield_10700
                JSONArray testLinks = issueFields.getJSONArray("customfield_10700");

                //产品负责人customfield_10303
                String productOwner = issueFields.getJSONObject("customfield_10303") == null ? "" : issueFields.getJSONObject("customfield_10303").getString("displayName");
                //经办人
                JSONObject assigneeObject = issueFields.getJSONObject("assignee");
                String assignee = assigneeObject.getString("displayName");

                String roles = getRoles(assignee,requestMan,developer,check,test,productOwner,testLinks);

                //操作人
                String userName = dataObject.getJSONObject("user").getString("displayName");

                //获取评论内容
                JSONObject commentObject = dataObject.getJSONObject("comment");
                String commentUser = "";
                String commentBody = "";
                if(commentObject != null){
                    commentUser = commentObject.getJSONObject("author").getString("displayName");
                    commentBody = commentObject.getString("body");
                }
                String markdownText = "";


                if("issue_created".equals(issueEventTypeName)){
                    markdownText = CREATE_TEMPLATE.replaceAll("#creatorName",creatorName)
                            .replaceAll("#issueName",issueName).replaceAll("#issueType",issueType).replaceAll("#issueUrl",issueUrl)
                            .replaceAll("#duedate",dueDate).replaceAll("#remaintime",remainTime)
                            .replaceAll("#statusName",statusName).replaceAll("#priorityName",priorityName)
                            .replaceAll("#assignee","@"+assignee).replaceAll("#description",description)
                            .replaceAll("#productOwner","@"+productOwner).replaceAll("#roles",roles);
                }else if("issue_commented".equals(issueEventTypeName)){
                    //添加评论
                    markdownText = ADD_COMMENT_TEMPLATE.replaceAll("#commentName",commentUser)
                            .replaceAll("#issueName",issueName).replaceAll("#issueType",issueType).replaceAll("#issueUrl",issueUrl)
                            .replaceAll("#duedate",dueDate).replaceAll("#remaintime",remainTime)
                            .replaceAll("#statusName",statusName).replaceAll("#priorityName",priorityName)
                            .replaceAll("#assignee","@"+assignee).replaceAll("#description",description)
                            .replaceAll("#productOwner","@"+productOwner).replaceAll("#commentContent",commentBody)
                            .replaceAll("#roles",roles);
                }else if("issue_updated".equals(issueEventTypeName)){
                    //修改需求
                    JSONObject changelogObject = dataObject.getJSONObject("changelog");
                    JSONArray items = changelogObject.getJSONArray("items");
                    String updateContent = "";
                    for(int i=0;i<items.size();i++){
                        JSONObject item = items.getJSONObject(i);
                        String field = item.getString("field");
                        String fieldName = getFiledName(field);
                        String fromString = item.getString("fromString");
                        if(fromString != null && fromString.length()>15){
                            fromString = fromString.substring(0,12)+"...";
                        }
                        String from = item.getString("from");
                        String toString = item.getString("toString");
                        if(toString != null && toString.length()>15){
                            toString = toString.substring(0,12)+"...";
                        }
                        String to = item.getString("to");
                        if(!StringUtils.isEmpty(fieldName)){
                            updateContent += fieldName+":";
                        }
                        updateContent += (StringUtils.isEmpty(fromString) ? "空" : fromString+(StringUtils.isEmpty(from) ? "" : "["+from+"]"));
                        updateContent += ">";
                        updateContent += (StringUtils.isEmpty(toString) ? "空" : toString+(StringUtils.isEmpty(to) ? "" : "["+to+"]"));
                        updateContent+="  \n";
                    }
                    markdownText = ISSUE_UPDATED_TEMPLATE.replaceAll("#updateName",userName)
                            .replaceAll("#issueName",issueName).replaceAll("#issueType",issueType).replaceAll("#issueUrl",issueUrl)
                            .replaceAll("#duedate",dueDate).replaceAll("#remaintime",remainTime)
                            .replaceAll("#statusName",statusName).replaceAll("#priorityName",priorityName)
                            .replaceAll("#assignee","@"+assignee).replaceAll("#description",description)
                            .replaceAll("#productOwner","@"+productOwner).replaceAll("#updateContent",updateContent)
                            .replaceAll("#roles",roles);

                }else if("issue_generic".equals(issueEventTypeName)){
                    JSONObject changelogObject = dataObject.getJSONObject("changelog");
                    JSONArray items = changelogObject.getJSONArray("items");
                    String fromStatus = "";
                    String toStatus = "";
                    for(int i=0;i<items.size();i++){
                        JSONObject item = items.getJSONObject(i);
                        String field = item.getString("field");
                        String fieldName = getFiledName(field);
                        String fromString = item.getString("fromString");
                        String from = item.getString("from");
                        String toString = item.getString("toString");
                        String to = item.getString("to");
                        if("status".equals(field)){
                            fromStatus = fromString;
                            toStatus = toString;
                            break;
                        }
                    }
                    markdownText = ISSUE_GENERIC_TEMPLATE.replaceAll("#userName",userName)
                            .replaceAll("#issueName",issueName).replaceAll("#issueType",issueType).replaceAll("#issueUrl",issueUrl)
                            .replaceAll("#duedate",dueDate).replaceAll("#remaintime",remainTime)
                            .replaceAll("#statusName",statusName).replaceAll("#priorityName",priorityName)
                            .replaceAll("#assignee","@"+assignee).replaceAll("#description",description)
                            .replaceAll("#productOwner","@"+productOwner).replaceAll("#commentContent",commentBody)
                            .replaceAll("#fromStatus",fromStatus).replaceAll("#toStatus",toStatus)
                            .replaceAll("#roles",roles);;
                }else if("issue_assigned".equals(issueEventTypeName)){
                    //分配需求
                    markdownText = ISSUE_ASSIGNED_TEMPLATE.replaceAll("#userName",userName)
                            .replaceAll("#issueName",issueName).replaceAll("#issueType",issueType).replaceAll("#issueUrl",issueUrl)
                            .replaceAll("#duedate",dueDate).replaceAll("#remaintime",remainTime)
                            .replaceAll("#statusName",statusName).replaceAll("#priorityName",priorityName)
                            .replaceAll("#assignee",assignee).replaceAll("#description",description)
                            .replaceAll("#productOwner","@"+productOwner).replaceAll("#commentContent",commentBody)
                            .replaceAll("#roles",roles);;
                }
                MarkdownMessage markdownMessage = new MarkdownMessage();
                MarkdownMessage.TextBean textBean = new MarkdownMessage.TextBean();
                textBean.setTitle("jira:[分享]");
                textBean.setText(markdownText);
                markdownMessage.setMarkdown(textBean);
                MessageHelper.sendCorpMsg("6ba0b4d140473a1e9c09857cdbdd9da0",markdownMessage,"0240461122682444");
            }
        }

        System.out.println("data:["+data+"]");
        return "webHook success";
    }

    private String getRoles(String assignee, String requestMan, String developer, String check, String test, String productOwner, JSONArray testLinks) {
        List<String> roles = new ArrayList<>();
        if(assignee.equals(requestMan)){
            roles.add("需求提出人");
        }
        if(assignee.equals(developer)){
            roles.add("开发负责人");
        }
        if(assignee.equals(check)){
            roles.add("上线审核人");
        }
        if(assignee.equals(test)){
            roles.add("测试负责人");
        }
        if(assignee.equals(productOwner)){
            roles.add("产品/项目负责人");
        }
        if(testLinks != null && testLinks.size()>0){
            for(int i=0 ;i<testLinks.size();i++){
                JSONObject object = testLinks.getJSONObject(i);
                if(assignee.equals(object.getString("displayName"))){
                    roles.add("测试干系人");
                    break;
                }
            }
        }
        return String.join(",",roles);
    }

    private String getFiledName(String field) {
        switch (field){
            case "Component":
                return "模块";
            case "description":
                return "描述";
            case "labels":
                return "标签";
            case "priority":
                return "优先级";
            default:
                return "";
        }
    }

    private void createIssue(String creatorName, String issueName, String issueUrl, String issueType, String dueDate, String remainTime, String priorityName, String statusName, String description, String productOwner, String assignee) {
        String markdownText = CREATE_TEMPLATE.replaceAll("#creatorName",creatorName)
                .replaceAll("#issueName",issueName).replaceAll("#issueType",issueType).replaceAll("#issueUrl",issueUrl)
                .replaceAll("#duedate",dueDate).replaceAll("#remaintime",remainTime)
                .replaceAll("#statusName",statusName).replaceAll("#priorityName",priorityName)
                .replaceAll("#assignee","@"+assignee).replaceAll("#description",description)
                .replaceAll("#productOwner","@"+productOwner);
//                System.out.println(markdownText);
        MarkdownMessage markdownMessage = new MarkdownMessage();
        MarkdownMessage.TextBean textBean = new MarkdownMessage.TextBean();
        textBean.setTitle("jira:[分享]");
        textBean.setText(markdownText);
        markdownMessage.setMarkdown(textBean);
        MessageHelper.sendCorpMsg("f62c0ee76fa63ff39ff382f2219ee416",markdownMessage,"0240461122682444");
    }

    public static void main(String[] args) {
        System.out.println(CREATE_TEMPLATE);
    }

}
