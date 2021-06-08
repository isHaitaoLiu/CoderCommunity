package cug.cs.codercommunity.advice;


import cug.cs.codercommunity.exception.CustomException;
import cug.cs.codercommunity.exception.CustomExceptionToJson;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class CustomExceptionHandler {

    @ExceptionHandler(value = {CustomException.class})
    public ModelAndView handleCustomException(CustomException exception){
        ModelAndView mv = new ModelAndView();
        mv.addObject("code", exception.getCode());
        mv.addObject("message", exception.getMessage());
        mv.setViewName("error");
        return mv;
    }

    @ExceptionHandler(value = {CustomExceptionToJson.class})
    public Map<String, Object> handleCustomExceptionToJson(CustomExceptionToJson exception){
        Map<String, Object> res = new HashMap<>();
        res.put("code", exception.getCode());
        res.put("message", exception.getMessage());
        return res;
    }
}
