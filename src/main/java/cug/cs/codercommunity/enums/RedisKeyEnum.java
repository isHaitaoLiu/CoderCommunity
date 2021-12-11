package cug.cs.codercommunity.enums;

/**
 * @program: codercommunity
 * @description: redis键的枚举类
 * @author: Sakura
 * @create: 2021-12-09 15:05
 **/

public enum RedisKeyEnum {
    MAP_QUESTION_LIKE("MAP_QUESTION_LIKE", "问题点赞详情，key=userId:questionId，value=status"),
    MAP_QUESTION_LIKE_COUNT("MAP_QUESTION_LIKE_COUNT", "问题点赞数, key=questionId，value=count"),
    MAP_COMMENT_LIKE("MAP_COMMENT_LIKE", "评论点赞详情, key=userId:commentId，value=status"),
    MAP_COMMENT_LIKE_COUNT("MAP_COMMENT_LIKE_COUNT", "评论点赞数, key=commentId，value=count"),
    ZSET_QUESTION_SCORE("ZSET_QUESTION_SCORE", "问题热度评分排行, key=questionId，score=view_count + like_count * 2 + comment_count * 3"),
    MAP_QUESTION_TITLE("MAP_QUESTION_ID_TITLE", "缓存问题名称, key=questionId, value=title"),
    MAP_QUESTION_VIEW_COUNT("MAP_QUESTION_VIEW_COUNT", "缓存问题浏览数，key=questionId, value=view_count");

    private final String key;
    private final String description;

    RedisKeyEnum(String key, String description) {
        this.key = key;
        this.description = description;
    }

    public String getKey() {
        return key;
    }

    public String getDescription() {
        return description;
    }
}
