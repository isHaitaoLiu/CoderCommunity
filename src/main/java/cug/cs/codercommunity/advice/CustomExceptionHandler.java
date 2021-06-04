package cug.cs.codercommunity.advice;


import cug.cs.codercommunity.exception.CustomException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

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
}
