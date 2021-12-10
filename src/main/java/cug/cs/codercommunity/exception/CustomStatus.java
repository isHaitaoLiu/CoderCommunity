package cug.cs.codercommunity.exception;

public enum CustomStatus {
    SUCCESS(200, "请求成功！"),

    //数据库问题
    QUESTION_NOT_FOUND(2001, "您找的问题已经删除或不存在，要不换个试试？"),

    TARGET_PARAM_WRONG(2002, "没有选中任何评论或回复！"),
    NOT_LOGIN(2003, "当前操作需要登录，请登录后重试！"),
    TYPE_PARAM_WRONG(2004, "评论的类型错误或不存在！"),
    COMMENT_NOT_FOUND(2005, "您找到评论已经删除或不存在，要不换个试试？"),
    CONTENT_IS_EMPTY(2006, "内容不能为空！"),
    READ_NOTIFICATION_FILE(2007, "无法读取他人通知！"),
    NOTIFICATION_NOT_FOUND(2008, "通知不翼而飞了！"),
    LOGIN_FAILED(2009, "登录失败");

    private final int code;
    private final String message;

    CustomStatus(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer getCode(){
        return code;
    }

    public String getMessage(){
        return message;
    }
}
