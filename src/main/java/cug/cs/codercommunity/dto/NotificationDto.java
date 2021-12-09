package cug.cs.codercommunity.dto;

import lombok.Data;

import java.util.Date;

@Data
public class NotificationDto {
    private Integer id;
    private Date gmtCreate;
    private Integer status;
    //通知者id
    private Integer notifier;
    //通知者名字
    private String notifierName;
    //
    private Integer outerId;
    //通知内容的标题
    private String outerTitle;
    //显示在前端的文案，例如：回复了问题，回复了评论
    private Integer type;
    private String typeName;
}
