package cug.cs.codercommunity.enums;

/**
 * @program: codercommunity
 * @description: 点赞状态
 * @author: Sakura
 * @create: 2021-11-28 19:29
 **/

public enum LikeStatusEnum {
    LIKE(1, "已点赞"),
    UNLIKE(0, "未点赞/取消点赞");

    private final Integer status;
    private final String message;

    LikeStatusEnum(Integer status, String message) {
        this.status = status;
        this.message = message;
    }


    public Integer getStatus() {
        return status;
    }

    public String getMessage(){
        return message;
    }
}
