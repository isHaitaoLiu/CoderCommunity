package cug.cs.codercommunity.dto;


import cug.cs.codercommunity.exception.CustomStatus;
import lombok.Data;

@Data
public class JsonResult<T> {
    private Integer code;
    private String message;
    private T data;
/*
    public JsonResult<T> errorOf(Integer code, String message){
        JsonResult<T> jsonResult = new JsonResult<>();
        jsonResult.setCode(code);
        jsonResult.setMessage(message);
        return jsonResult;
    }

    public JsonResult<T> errorOf(CustomStatus customStatus){
        JsonResult<T> jsonResult = new JsonResult<>();
        jsonResult.setCode(customStatus.getCode());
        jsonResult.setMessage(customStatus.getMessage());
        return jsonResult;
    }
*/
    public JsonResult(CustomStatus customStatus){
        this.code = customStatus.getCode();
        this.message = customStatus.getMessage();
        this.data = null;
    }

    public JsonResult(CustomStatus customStatus, T data){
        this.code = customStatus.getCode();
        this.message = customStatus.getMessage();
        this.data = data;
    }


}
