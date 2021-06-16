package cug.cs.codercommunity.enums;

public enum NotificationStatusEnum {
    UNREAD(0),
    READ(1);

    private final Integer status;

    NotificationStatusEnum(Integer status) {
        this.status = status;
    }

    public Integer getStatus() {
        return status;
    }
}
