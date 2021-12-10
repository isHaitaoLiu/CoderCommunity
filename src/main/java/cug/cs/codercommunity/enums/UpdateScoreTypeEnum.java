package cug.cs.codercommunity.enums;

/**
 * @program: codercommunity
 * @description: 更新热度的类型枚举
 * @author: Sakura
 * @create: 2021-12-09 20:29
 **/

public enum UpdateScoreTypeEnum {
    UNLIKE(-3, "消赞"),
    VIEW(1, "浏览"),
    LIKE(2, "点赞"),
    COMMENT(3, "评论");

    private final Integer type;
    private final String description;

    UpdateScoreTypeEnum(Integer type, String description) {
        this.type = type;
        this.description = description;
    }

    public Integer getType() {
        return type;
    }

    public String getDescription() {
        return description;
    }
}
