package org.zframework.core.web.resolver;

import java.io.PrintWriter;
import java.sql.SQLException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.ConversionNotSupportedException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.NoSuchRequestHandlingMethodException;
import org.zframework.core.support.ApplicationCommon;
import org.zframework.core.util.HttpServletUtil;
import org.zframework.core.util.ObjectUtil;
import org.zframework.core.web.support.WebContextHelper;
import org.zframework.core.web.support.WebResult;
import org.zframework.web.entity.system.User;
import org.zframework.web.service.admin.system.LogService;

public class ExceptionResolver implements HandlerExceptionResolver{
	private final Logger logger = Logger.getLogger(this.getClass());
	@Autowired
	private LogService logService;
	@Override
	public ModelAndView resolveException(HttpServletRequest request,
			HttpServletResponse response, Object obj, Exception e) {
		e.printStackTrace();
		Object user = WebContextHelper.getSession().getAttribute(ApplicationCommon.SESSION_ADMIN);
		String userName = "SYSTEM";
		if(ObjectUtil.isNotNull(user)){
			userName = ((User)user).getLoginName();
		}
		try {
			int errorCode = 500;
			String errorMsg = "????????????????????????????????????";
			if(e instanceof ConversionNotSupportedException){//Web?????????????????????
				errorMsg = "????????????????????????";
				errorCode = 406;
			}else if(e instanceof HttpMediaTypeNotAcceptableException){//????????????Accept?????????MIME??????
				errorMsg = "????????????Accept?????????MIME?????????";
				errorCode = 415;
			}else if(e instanceof HttpMediaTypeNotSupportedException){//????????????MIME??????
				errorMsg = "????????????MIME?????????";
				errorCode = 400;
			}else if(e instanceof HttpMessageNotReadableException){//Bad Request
				errorCode = 500;
			}else if(e instanceof HttpMessageNotWritableException){//406
				errorMsg = "?????????????????????";
				errorCode = 405;
			}else if(e instanceof HttpRequestMethodNotSupportedException){//????????????????????????
				errorMsg = "????????????????????????:"+request.getMethod();
				errorCode = 400;
			}else if(e instanceof MissingServletRequestParameterException){//400
				errorMsg = "???????????????";
				errorCode = 400;
			}else if(e instanceof NoSuchRequestHandlingMethodException){//?????????????????????
				errorMsg = "???????????????????????????";
				errorCode = 404;
			}else if(e instanceof TypeMismatchException){//400
				errorMsg = "??????????????????";
				errorCode = 400;
			}else if(e instanceof SQLException){
				errorMsg = "????????????????????????";
				errorCode = 500;
			}
			//???????????????ajax?????????????????????ajax????????????????????????
			if(HttpServletUtil.isAjaxWithRequest(request) && request.getMethod().equals("GET"))
			{
				response.setContentType("text/html");
				PrintWriter out = response.getWriter();
				out.println("<script type=\"text/javascript\">");
				out.println("top.Dialog.alert(\""+errorMsg+"\",function(){");
				out.println("window.location.href='"+request.getContextPath()+"/signup'");
				out.println("});");
				out.println("</script>");
				out.close();
			}else if(HttpServletUtil.isAjaxWithRequest(request) && request.getMethod().equals("POST")){
				//??????
				response.setContentType("text/json");
				response.getWriter().print(WebResult.error(errorMsg));
				response.getWriter().close();
			}else if(ObjectUtil.isNull(request.getHeader("x-requested-with")))
			{
				response.sendRedirect("/"+ApplicationCommon.BASEPATH+"/error/e/"+errorCode);
				return new ModelAndView("_error/"+errorCode);
			}
			int index = 0;
			for(int i=0;i<e.getStackTrace().length;i++){
				StackTraceElement element= e.getStackTrace()[i];
				if(element.isNativeMethod()){
					index = i-1;
					break;
				}
			}
			logger.error(e.getMessage());
			logService.recordError("Servlet??????","????????????:"+errorCode+"\nException:"+e+"\n At:"+e.getStackTrace()[index].getClassName()+",Line:"+e.getStackTrace()[index].getLineNumber()+"\nMessage:"+e.getMessage() ,userName, request.getRemoteAddr());
		} catch (Exception e2) {
			int index = 0;
			for(int i=0;i<e2.getStackTrace().length;i++){
				StackTraceElement element= e2.getStackTrace()[i];
				if(element.isNativeMethod()){
					index = i-1;
					break;
				}
			}
			e2.printStackTrace();
			logger.error(e.getMessage());
			logService.recordError("Servlet??????","????????????:500\nException:"+e+"\n At:"+e2.getStackTrace()[index].getClassName()+",Line:"+e2.getStackTrace()[index].getLineNumber()+"\nMessage:"+e.getMessage(),userName , request.getRemoteAddr());
		}
		return new ModelAndView("_error/500");
	}

}
