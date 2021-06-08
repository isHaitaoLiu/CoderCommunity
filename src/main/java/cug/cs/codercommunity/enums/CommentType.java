package cug.cs.codercommunity.enums;

public enum CommentType {
    QUESTION(1),
    COMMENT(2);

    private final Integer type;

    CommentType(Integer type) {
        this.type = type;
    }

    public static boolean isExist(Integer type) {
        for (CommentType c : CommentType.values()) {
            if (c.getType().equals(type)){
                return true;
            }
        }
        return false;
    }

    public Integer getType() {
        return type;
    }
}
