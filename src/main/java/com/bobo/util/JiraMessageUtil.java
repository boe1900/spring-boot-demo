package com.bobo.util;

public class JiraMessageUtil {

    public static final String BASE_URL = "http://mis.rongcard.com/jira/browse";

    public static final String CREATE_TEMPLATE = "#### [#creatorName create #issueType #issueName](#issueUrl)  \n" +
            "##### 测试  \n" +
            "###### 到期日：#duedate  \n" +
            "###### 剩余工期：#remaintime  \n" +
            "###### 优先级：#priorityName  \n" +
            "###### 状态：#statusName  \n" +
            "###### 当前经办人：#assignee（#roles）  \n" +
            "> #description";


    public static final String ADD_COMMENT_TEMPLATE = "#### [#commentName给#issueType #issueName添加备注](#issueUrl)  \n" +
            "##### 测试  \n" +
            "###### 到期日：#duedate  \n" +
            "###### 剩余工期：#remaintime  \n" +
            "###### 优先级：#priorityName  \n" +
            "###### 状态：#statusName  \n" +
            "###### 当前经办人：#assignee（#roles）  \n" +
            "> #commentContent";


    public static final String ISSUE_UPDATED_TEMPLATE = "#### [#updateName update #issueType #issueName](#issueUrl)  \n" +
            "##### 测试  \n" +
            "###### 到期日：#duedate  \n" +
            "###### 剩余工期：#remaintime  \n" +
            "###### 优先级：#priorityName  \n" +
            "###### 状态：#statusName  \n" +
            "###### 当前经办人：#assignee（#roles）  \n" +
            "> #updateContent";


    public static final String ISSUE_GENERIC_TEMPLATE = "#### [#userName changed #issueType #issueName](#issueUrl) from #fromStatus to #toStatus  \n" +
            "##### 测试  \n" +
            "###### 到期日：#duedate  \n" +
            "###### 剩余工期：#remaintime  \n" +
            "###### 优先级：#priorityName  \n" +
            "###### 状态：#statusName  \n" +
            "###### 当前经办人：#assignee（#roles）  \n" +
            "> #commentContent";

    public static final String ISSUE_ASSIGNED_TEMPLATE = "#### [#userName 将 #issueType #issueName 分配给#assignee](#issueUrl)  \n" +
            "##### 测试  \n" +
            "###### 到期日：#duedate  \n" +
            "###### 剩余工期：#remaintime  \n" +
            "###### 优先级：#priorityName  \n" +
            "###### 状态：#statusName  \n" +
            "###### 当前经办人：@#assignee（#roles）  \n" +
            "> #description  \n"+
            "###### 备注:  \n"+
            "> #commentContent";






}
