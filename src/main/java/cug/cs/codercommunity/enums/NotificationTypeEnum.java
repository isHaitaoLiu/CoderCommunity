package cug.cs.codercommunity.enums;

public enum NotificationTypeEnum {
    REPLY_QUESTION(1, "回复了问题"),
    REPLY_COMMENT(2, "回复了评论");

    private final Integer type;
    private final String name;

    NotificationTypeEnum(Integer type, String name) {
        this.type = type;
        this.name = name;
    }

    public Integer getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public static String nameOfType(Integer type){
        for (NotificationTypeEnum typeEnum : values()) {
            if (typeEnum.getType().equals(type)){
                return typeEnum.getName();
            }
        }
        return "";
    }
}
