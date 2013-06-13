package th.co.truemoney.serviceinventory.interceptor;

import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jboss.logging.MDC;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.fasterxml.jackson.databind.ObjectMapper;

public class ActivityInterceptor extends HandlerInterceptorAdapter {

    ObjectMapper mapper = new ObjectMapper();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

	String trackingID = UUID.randomUUID().toString();
	MDC.put("trackingID", trackingID);
	MDC.remove("draftTransactionID");
	MDC.remove("transactionID");

	String accessTokenID = null;

	if(request.getParameter("accessTokenID") != null) {
	    accessTokenID = request.getParameter("accessTokenID");
	} else {
	    accessTokenID = getAccessTokenID(request);
	}

	if(accessTokenID!=null) {
	    MDC.put("accessTokenID", accessTokenID);
	}

	return true;
    }

    @SuppressWarnings("rawtypes")
    private String getAccessTokenID(HttpServletRequest request) {
	Map pathValiable = (Map)request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
	return (String)pathValiable.get("accessTokenID");
    }
}
