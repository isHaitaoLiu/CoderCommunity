package cug.cs.codercommunity.exception;

public class CustomExceptionToJson extends RuntimeException{
    private final int code;
    private final String message;

    public CustomExceptionToJson(CustomStatus customStatus){
        this.code = customStatus.getCode();
        this.message = customStatus.getMessage();
    }

    @Override
    public String getMessage() {
        return message;
    }

    public int getCode() {
        return code;
    }
}
