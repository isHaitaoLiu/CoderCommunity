package cug.cs.codercommunity.dto;


import cug.cs.codercommunity.exception.CustomStatus;
import lombok.Data;

@Data
public class ResultDto {
    private Integer code;
    private String message;

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
}
