package cug.cs.codercommunity.dto;


import cug.cs.codercommunity.exception.CustomStatus;
import lombok.Data;

@Data
public class ResultDto<T> {
    private Integer code;
    private String message;
    private T data;

    public static ResultDto errorOf(Integer code, String message){
        ResultDto resultDto = new ResultDto();
        resultDto.setCode(code);
        resultDto.setMessage(message);
        return resultDto;
    }

    public static ResultDto errorOf(CustomStatus customStatus){
        ResultDto resultDto = new ResultDto();
        resultDto.setCode(customStatus.getCode());
        resultDto.setMessage(customStatus.getMessage());
        return resultDto;
    }

    public static <T> ResultDto okOf(T t) {
        ResultDto resultDTO = new ResultDto();
        resultDTO.setCode(200);
        resultDTO.setMessage("请求成功");
        resultDTO.setData(t);
        return resultDTO;
    }

}
