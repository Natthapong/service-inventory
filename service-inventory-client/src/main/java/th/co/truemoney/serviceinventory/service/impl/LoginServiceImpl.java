package th.co.truemoney.serviceinventory.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import net.minidev.json.JSONObject;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import th.co.truemoney.serviceinventory.domain.DevConfigBean;
import th.co.truemoney.serviceinventory.domain.RequestBean;
import th.co.truemoney.serviceinventory.domain.ResponseBean;
import th.co.truemoney.serviceinventory.domain.SigninBean;
import th.co.truemoney.serviceinventory.domain.proxy.BaseServiceRequestBean;
import th.co.truemoney.serviceinventory.domain.proxy.BaseServiceResponseBean;
import th.co.truemoney.serviceinventory.domain.proxy.SigninResponseBean;
import th.co.truemoney.serviceinventory.ewallet.domain.TmnProfile;
import th.co.truemoney.serviceinventory.service.LoginService;

public class LoginServiceImpl implements LoginService {
	@Autowired
	private RestTemplate restTemplate;
	
	public RestTemplate getRestTemplate() {
		return restTemplate;
	}

	public void setRestTemplate(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}

	private String loginPath = "/login";
	
	private LinkedHashMap<String, String> signinValue;
	
	public DevConfigBean configBean = new DevConfigBean();
	
	@Override
	@SuppressWarnings({ "rawtypes", "unchecked", "unused" })
	public String login(SigninBean signinBean){
		
		String url_login = configBean.getPathDevUrl(loginPath);
		
		try {

			List<MediaType> acceptableMediaTypes = new ArrayList<MediaType>();
			acceptableMediaTypes.add(MediaType.APPLICATION_JSON);

			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(acceptableMediaTypes);

			BaseServiceRequestBean<SigninResponseBean> baseServiceRequestBean = new BaseServiceRequestBean<SigninResponseBean>();
			
			HttpEntity<BaseServiceRequestBean> entity = new HttpEntity<BaseServiceRequestBean>(
					baseServiceRequestBean, headers);

			ResponseEntity<BaseServiceResponseBean> result = restTemplate.exchange(
					url_login, HttpMethod.POST, entity,
					BaseServiceResponseBean.class);
			
			BaseServiceResponseBean bodyResponse = result.getBody();
			
			return bodyResponse.getAccessToken();
			
		} catch (HttpClientErrorException e) {
			return null;
		}
	}

	@Override
	public ResponseBean<TmnProfile> getProfile(RequestBean requestBean) {
		
		return null;
	}

	
}
