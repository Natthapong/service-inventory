package th.co.truemoney.serviceinventory.interceptor;

import java.sql.Timestamp;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jboss.logging.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import th.co.truemoney.serviceinventory.ewallet.domain.AccessToken;
import th.co.truemoney.serviceinventory.ewallet.repositories.AccessTokenRepository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ActivityInterceptor extends HandlerInterceptorAdapter {
	
	ObjectMapper mapper = new ObjectMapper();

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		
		HandlerMethod handlerMethod = (HandlerMethod)handler;
		String trackingID = UUID.randomUUID().toString();
		MDC.put("trackingID", trackingID);
		
		String accessTokenID = null;
		
		if(request.getParameter("accessTokenID") != null) {
			accessTokenID = request.getParameter("accessTokenID");
		} else {
			accessTokenID = getAccessTokenID(request);
		}
		
		System.out.println("accessTokenID: "+accessTokenID);
		
		if(accessTokenID!=null) {
			MDC.put("accessTokenID", accessTokenID);
		}
		
		Timestamp createdDate = new Timestamp(System.currentTimeMillis());
		
		System.out.println("handler class: "+handlerMethod.getBeanType().getSimpleName());
		System.out.println("handler method: "+handlerMethod.getMethod().getName());
		System.out.println("createdDate: " + createdDate);
		
		/*System.out.println("-----------------------------");
        System.out.println("trackingID: " + trackingID);
        System.out.println("createdDate: " + createdDate);
        System.out.println("responseDate: " + responseDate);
        System.out.println("loginID: " + loginID);
        System.out.println("truemoneyID: " + truemoneyID);
        System.out.println("httpStatus: " + httpStatus);
        System.out.println("resultCode: " + resultCode);
        System.out.println("resultNamespace: " + resultNamespace);
        System.out.println("transactionID: " + transactionID);
        System.out.println("processState: " + processState);
        System.out.println("referenceTransactionID: " + referenceTransactionID);
        System.out.println("durationTime: " + durationTime);
        System.out.println("WorkerName: " + workerName);
        System.out.println("ActivityName: " + activityName);
        System.out.println("Details: " + details);
        System.out.println("-----------------------------");*/
		
		return true;
	}
	
	@Override
	public void postHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		
//		try {
//			Timestamp responseDate = new Timestamp(System.currentTimeMillis());
//			AccessToken accessToken = accessTokenRepository.findAccessToken((String)MDC.get("accessTokenID"));
//			String loginID = accessToken==null?null:(accessToken.getEmail()==null?accessToken.getMobileNumber():accessToken.getEmail());
//			String truemoneyID = accessToken==null?null:(accessToken.getTruemoneyID());
//			
//			System.out.println("loginID: " + loginID);
//		    System.out.println("truemoneyID: " + truemoneyID);
//			System.out.println("responseDate: " + responseDate);
//			System.out.println("getClass: "+response.getClass().getName());
//			System.out.println("getContentType: "+response.getContentType());
//			System.out.println("getStatus: "+response.getStatus());
//			System.out.println("trackingID: " + MDC.get("trackingID"));
//		} catch(Exception e) {
//			e.printStackTrace();
//		}
		
	}
	
	/*@Override
	public void afterCompletion(HttpServletRequest request,
			HttpServletResponse response, Object handler, Exception ex)
			 {
		ServiceInventoryException se = (ServiceInventoryException)ex;
		
		System.out.println("MMMMMMMMMMMMMMMM getStatus: "+response.getStatus());
		System.out.println("-----------------------------------------");
		System.out.println("MMMMMMMMMMMMMMMM getHeaderNames: "+response.getHeaders("Content-Type"));
		System.out.println("-----------------------------------------");
		if(ex != null) {
			System.out.println("MMMMMMMMMMMMMMMM getErrorCode: "+ex.getMessage());
			System.out.println("-----------------------------------------");
		} else {
			System.out.println("MMMMMMMMMMMMMMMM getErrorCode isNull");
		}
		
	}*/

	@SuppressWarnings("rawtypes")
	private String getAccessTokenID(HttpServletRequest request) {
		Map pathValiable = (Map)request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
		return (String)pathValiable.get("accessTokenID");
	}



	
	

}
