package cn.duxl.exception;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;


public class CustomExceptionResolver implements HandlerExceptionResolver {

	@Override
	public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler,
			Exception ex) {
		CustomException customException = null;
		if(ex instanceof CustomException) {
			customException = (CustomException) ex;
		}else {
			customException = new CustomException("未知错误！");
		}
		
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.addObject("errMessage",customException.getMessage());
		modelAndView.setViewName("index");
		
		return modelAndView;
	}


}
