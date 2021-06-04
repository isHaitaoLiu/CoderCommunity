package cug.cs.codercommunity.exception;

public class CustomException extends RuntimeException{
    private final int code;
    private final String message;

    public CustomException(CustomStatus customStatus){
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
