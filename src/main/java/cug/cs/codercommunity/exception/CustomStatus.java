package cug.cs.codercommunity.exception;

public enum CustomStatus {
    QUESTION_NOT_FOUND(100, "您找的问题已经删除或不存在！");

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
