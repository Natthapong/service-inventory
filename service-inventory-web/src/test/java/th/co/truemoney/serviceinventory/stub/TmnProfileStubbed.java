package th.co.truemoney.serviceinventory.stub;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.rmi.RemoteException;

import th.co.truemoney.serviceinventory.ewallet.exception.EwalletException;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.AuthenticateResponse;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.CreateSessionResponse;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.GetBasicProfileResponse;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.SignonResponse;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.StandardBizResponse;

public class TmnProfileStubbed {
	
	public static SignonResponse createSuccessStubbedSignonResponse() {
		SignonResponse stubbedSignonResponse = new SignonResponse();
		stubbedSignonResponse.setResultCode("0");
		stubbedSignonResponse.setSessionId("SjdfgkIDF");
		stubbedSignonResponse.setTmnId("tmnid0001");		
		return stubbedSignonResponse;
	}
	
	public static SignonResponse createFailedStubbedSignonResponse() {
		SignonResponse stubbedSignonResponse = new SignonResponse();
		stubbedSignonResponse.setResultCode("1");
		stubbedSignonResponse.setSessionId(null);
		stubbedSignonResponse.setTmnId(null);		
		return stubbedSignonResponse;
	}
	
	public static GetBasicProfileResponse createSuccessStubbedProfileResponse() {
		GetBasicProfileResponse stubbedProfileResponse = new GetBasicProfileResponse();
		stubbedProfileResponse.setResultCode("0");
		stubbedProfileResponse.setFullName("Mali Colt");
		stubbedProfileResponse.setEwalletBalance(new BigDecimal("30000.00").setScale(2, RoundingMode.HALF_UP));
		stubbedProfileResponse.setProfileType("C");		
		return stubbedProfileResponse;
	}
	
	public static GetBasicProfileResponse createFailedStubbedProfileResponse() {
		GetBasicProfileResponse basicProfileResponse = new GetBasicProfileResponse();
		basicProfileResponse.setResultCode("1");
		basicProfileResponse.setFullName(null);
		basicProfileResponse.setEwalletBalance(null);
		return basicProfileResponse;
	}
	
	public static GetBasicProfileResponse createFailedNotCustomerProfile() {
		GetBasicProfileResponse basicProfileResponse = new GetBasicProfileResponse();
		basicProfileResponse.setResultCode("0");
		basicProfileResponse.setFullName("Mali Colt");
		basicProfileResponse.setEwalletBalance(new BigDecimal("30000.00").setScale(2, RoundingMode.HALF_UP));
		basicProfileResponse.setProfileType("D");	
		return basicProfileResponse;
	}
	
	public static StandardBizResponse createSuccessStubbedStandardBizResponse() {
		StandardBizResponse standardBizResponse = new StandardBizResponse();
		standardBizResponse.setResultCode("0");		
		return standardBizResponse;
	}
	
	public static StandardBizResponse createFailedStubbedStandardBizResponse() {
		StandardBizResponse standardBizResponse = new StandardBizResponse();
		standardBizResponse.setResultCode("1");
		return standardBizResponse;
	}
	
	public static CreateSessionResponse createSuccessStubbedCreateSessionResponse() {
		CreateSessionResponse createSessionResponse = new CreateSessionResponse();
		createSessionResponse.setResultCode("0");
		createSessionResponse.setSessionId("SjdfgkIDF");
		return createSessionResponse;
	}
	
	public static CreateSessionResponse createFailedStubbedCreateSessionResponse() {
		CreateSessionResponse createSessionResponse = new CreateSessionResponse();
		createSessionResponse.setResultCode("1");
		createSessionResponse.setSessionId(null);
		return createSessionResponse;
	}
	
	public static AuthenticateResponse createSuccessStubbedAuthenticateResponse() {
		AuthenticateResponse authenticateResponse = new AuthenticateResponse();
		authenticateResponse.setResultCode("0");
		authenticateResponse.setTmnId("tmnid0001");
		return authenticateResponse;
	}
	
	public static AuthenticateResponse createFailedStubbedAuthenticateResponse() {
		AuthenticateResponse authenticateResponse = new AuthenticateResponse();
		authenticateResponse.setResultCode("1");
		authenticateResponse.setTmnId(null);
		return authenticateResponse;
	}
	
	public static Exception createFailedThrowRemoteException() {
		return new RemoteException();
	}

	public static Exception createFailedThrowEwalletException() {
		EwalletException e = new EwalletException("error code", "error namespace");
		return e;
	}

}

