package th.co.truemoney.serviceinventory.persona.proxies;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import th.co.truemoney.serviceinventory.ewallet.exception.EwalletException;
import th.co.truemoney.serviceinventory.ewallet.exception.FailResultCodeException;
import th.co.truemoney.serviceinventory.ewallet.proxy.TmnProfileProxyClient;
import th.co.truemoney.serviceinventory.persona.Adam;
import th.co.truemoney.serviceinventory.persona.Eve;
import th.co.truemoney.serviceinventory.persona.Simpsons;

import com.tmn.core.api.message.AddFavoriteRequest;
import com.tmn.core.api.message.AddFavoriteResponse;
import com.tmn.core.api.message.ChangePasswordRequest;
import com.tmn.core.api.message.ChangePinRequest;
import com.tmn.core.api.message.CreateTmnProfileRequest;
import com.tmn.core.api.message.CreateTmnProfileResponse;
import com.tmn.core.api.message.DeleteFavoriteRequest;
import com.tmn.core.api.message.FavoriteContext;
import com.tmn.core.api.message.GetBasicProfileResponse;
import com.tmn.core.api.message.GetProfileRequest;
import com.tmn.core.api.message.GetProfileResponse;
import com.tmn.core.api.message.IsFavoritableRequest;
import com.tmn.core.api.message.IsFavoritedRequest;
import com.tmn.core.api.message.ListFavoriteRequest;
import com.tmn.core.api.message.ListFavoriteResponse;
import com.tmn.core.api.message.ListSourceRequest;
import com.tmn.core.api.message.ListSourceResponse;
import com.tmn.core.api.message.SourceContext;
import com.tmn.core.api.message.StandardBizRequest;
import com.tmn.core.api.message.StandardBizResponse;
import com.tmn.core.api.message.UpdateProfileRequest;

public class LocalTmnProfileProxyClient implements TmnProfileProxyClient {

    private Adam adam = new Adam();
    private Eve eve = new Eve();
    private Simpsons simpsons = new Simpsons();

    private String AdamTmnMoneyID = "AdamTmnMoneyId";
    private String EveTmnMoneyID = "EveTmnMoneyId";
    private String SimpsonsTmnMoneyID = "SimpsonsTmnMoneyId";
    
	@Override
	public StandardBizResponse changePassword(
			ChangePasswordRequest changePasswordRequest)
			throws EwalletException {
        if (changePasswordRequest.getSecurityContext().getTmnId().equals(AdamTmnMoneyID)) {
            return adam.getTmnProfileClient().changePassword(changePasswordRequest);
        } else if (changePasswordRequest.getSecurityContext().getTmnId().equals(EveTmnMoneyID)) {
            return eve.getTmnProfileClient().changePassword(changePasswordRequest);
        } else if (changePasswordRequest.getSecurityContext().getTmnId().equals(SimpsonsTmnMoneyID)) {
            return simpsons.getTmnProfileClient().changePassword(changePasswordRequest);
        } else {
			StandardBizResponse standardBizResponse = new StandardBizResponse();
			standardBizResponse.setResultCode("0");	
			standardBizResponse.setResultNamespace("core");	
			return standardBizResponse;
        }
	}

	@Override
	public StandardBizResponse changePin(
			ChangePinRequest changePinRequest) throws EwalletException {
        if (changePinRequest.getSecurityContext().getTmnId().equals(AdamTmnMoneyID)) {
            return adam.getTmnProfileClient().changePin(changePinRequest);
        } else if (changePinRequest.getSecurityContext().getTmnId().equals(EveTmnMoneyID)) {
            return eve.getTmnProfileClient().changePin(changePinRequest);
        } else if (changePinRequest.getSecurityContext().getTmnId().equals(SimpsonsTmnMoneyID)) {
            return simpsons.getTmnProfileClient().changePin(changePinRequest);
        } else {
			StandardBizResponse standardBizResponse = new StandardBizResponse();
			standardBizResponse.setResultCode("0");	
			standardBizResponse.setResultNamespace("core");	
			return standardBizResponse;
        }
	}

	@Override
	public GetProfileResponse getProfile(GetProfileRequest getProfileRequest) throws EwalletException {
        if (getProfileRequest.getSecurityContext().getTmnId().equals(AdamTmnMoneyID)) {
            return adam.getTmnProfileClient().getProfile(getProfileRequest);
        } else if (getProfileRequest.getSecurityContext().getTmnId().equals(EveTmnMoneyID)) {
            return eve.getTmnProfileClient().getProfile(getProfileRequest);
        } else if (getProfileRequest.getSecurityContext().getTmnId().equals(SimpsonsTmnMoneyID)) {
            return simpsons.getTmnProfileClient().getProfile(getProfileRequest);
        } else {
        	GetProfileResponse profileResponse = new GetProfileResponse();
        	profileResponse.setTransactionId("1");
        	profileResponse.setResultCode("0");
        	profileResponse.setResultNamespace("core");
        	profileResponse.setFullName("local");
        	profileResponse.setMobile("0891234567");
        	profileResponse.setEmail("local@tmn.com");
        	return profileResponse;
        }
	}

	@Override
	public StandardBizResponse updateProfile(UpdateProfileRequest updateProfileRequest) throws EwalletException {
        if (updateProfileRequest.getSecurityContext().getTmnId().equals(AdamTmnMoneyID)) {
            return adam.getTmnProfileClient().updateProfile(updateProfileRequest);
        } else if (updateProfileRequest.getSecurityContext().getTmnId().equals(EveTmnMoneyID)) {
            return eve.getTmnProfileClient().updateProfile(updateProfileRequest);
        } else if (updateProfileRequest.getSecurityContext().getTmnId().equals(SimpsonsTmnMoneyID)) {
            return simpsons.getTmnProfileClient().updateProfile(updateProfileRequest);
        } else {
			StandardBizResponse standardBizResponse = new StandardBizResponse();
			standardBizResponse.setResultCode("0");	
			standardBizResponse.setResultNamespace("core");	
			return standardBizResponse;
        }
	}

	@Override
	public CreateTmnProfileResponse createTmnProfile(CreateTmnProfileRequest createTmnProfileRequest)
			throws EwalletException {
		CreateTmnProfileResponse response = new CreateTmnProfileResponse();
    	response.setTransactionId("1");
    	response.setResultCode("0");
    	response.setResultNamespace("namespace");
    	response.setTmnId("123123");
		return response;
	}

	@Override
	public GetBasicProfileResponse getBasicProfile(StandardBizRequest standardBizRequest) 
			throws EwalletException {
        if (standardBizRequest.getSecurityContext().getTmnId()
                .equals(AdamTmnMoneyID)) {
            return adam.getTmnProfileClient().getBasicProfile(standardBizRequest);
        } else if (standardBizRequest.getSecurityContext().getTmnId()
                .equals(EveTmnMoneyID)) {
            return eve.getTmnProfileClient().getBasicProfile(standardBizRequest);
        } else if (standardBizRequest.getSecurityContext().getTmnId()
                .equals(SimpsonsTmnMoneyID)) {
            return simpsons.getTmnProfileClient().getBasicProfile(standardBizRequest);
        } else {
        	GetBasicProfileResponse response = new GetBasicProfileResponse();
        	response.setTransactionId("1");
        	response.setResultCode("0");
        	response.setResultNamespace("namespace");
        	response.setFullName("fullname");
        	response.setMobile("0891231234");
        	response.setEmail("local@tmn.com");
        	response.setEwalletBalance(new BigDecimal(50.0d));
        	response.setStatusId(3);
        	response.setProfileType("C");
        	return response;
        }
	}

	@Override
	public ListSourceResponse listSource(ListSourceRequest listSourceRequest)
			throws EwalletException {
        SourceContext[] sourceContext = createSourceContexts();
        ListSourceResponse listSourceResponse = new ListSourceResponse();
        listSourceResponse.setTransactionId("1");
        listSourceResponse.setResultCode("0");
        listSourceResponse.setResultNamespace("namespace");
        listSourceResponse.setSourceList(sourceContext);
        return listSourceResponse;
	}

	private SourceContext[] createSourceContexts() {
		SourceContext[] sourceContext = new SourceContext[3];
		sourceContext[0] = createSourceContext("1", "type", new String[] { "SCB", "xxxx1234" });
		sourceContext[1] = createSourceContext("2", "type", new String[] { "KTB", "xxxx5678" });
        sourceContext[2] = createSourceContext("3", "type", new String[] { "BBL", "xxxx9101" });
		return sourceContext;
	}
	
	private SourceContext createSourceContext(String sourceId, String sourceType, String[] sourceDetail) {
		SourceContext sourceContext = new SourceContext();
		sourceContext.setSourceId(sourceId);
		sourceContext.setSourceType(sourceType);
		sourceContext.setSourceDetail(sourceDetail);
		return sourceContext;
	}

	@Override
	public StandardBizResponse isFavoritable(IsFavoritableRequest isFavoritableRequest)
			throws EwalletException {
        List<String> serviceCodes = Arrays.asList("tr", "trmv", "tmvh", "tlp", "tic", "ti", "tcg", "mea", "glc");
        if (!serviceCodes.contains(isFavoritableRequest.getServiceCode())) {
            throw new FailResultCodeException("2013","stub ADD_FAVORITE_DENIED");
        }
        if (isFavoritableRequest.getSecurityContext().getTmnId().equals(AdamTmnMoneyID)) {
            return adam.getTmnProfileClient().isFavoritable(isFavoritableRequest);
        } else if (isFavoritableRequest.getSecurityContext().getTmnId().equals(EveTmnMoneyID)) {
            return eve.getTmnProfileClient().isFavoritable(isFavoritableRequest);
        } else if (isFavoritableRequest.getSecurityContext().getTmnId().equals(SimpsonsTmnMoneyID)) {
            return simpsons.getTmnProfileClient().isFavoritable(isFavoritableRequest);
        } else {
			StandardBizResponse standardBizResponse = new StandardBizResponse();
			standardBizResponse.setTransactionId("1");
			standardBizResponse.setResultCode("0");	
			standardBizResponse.setResultNamespace("namespace");	
			return standardBizResponse;
        }
	}

	@Override
	public AddFavoriteResponse addFavorite(AddFavoriteRequest addFavoriteRequest)
			throws EwalletException {
        if (addFavoriteRequest.getSecurityContext().getTmnId().equals(AdamTmnMoneyID)) {
        	 return adam.getTmnProfileClient().addFavorite(addFavoriteRequest);
        } else if (addFavoriteRequest.getSecurityContext().getTmnId().equals(EveTmnMoneyID)) {
        	return eve.getTmnProfileClient().addFavorite(addFavoriteRequest);
        } else if (addFavoriteRequest.getSecurityContext().getTmnId().equals(SimpsonsTmnMoneyID)) {
       	 	return simpsons.getTmnProfileClient().addFavorite(addFavoriteRequest);
        } else {
            throw new FailResultCodeException("500", "stub namespace.");
        }
	}

	@Override
	public StandardBizResponse removeFavorite(DeleteFavoriteRequest deleteFavoriteRequest)
			throws EwalletException {
        if (deleteFavoriteRequest.getSecurityContext().getTmnId().equals(AdamTmnMoneyID)) {
            return adam.getTmnProfileClient().removeFavorite(deleteFavoriteRequest);
        } else if (deleteFavoriteRequest.getSecurityContext().getTmnId().equals(EveTmnMoneyID)) {
            return eve.getTmnProfileClient().removeFavorite(deleteFavoriteRequest);
        } else if (deleteFavoriteRequest.getSecurityContext().getTmnId().equals(SimpsonsTmnMoneyID)) {
            return simpsons.getTmnProfileClient().removeFavorite(deleteFavoriteRequest);
        } else {
			StandardBizResponse standardBizResponse = new StandardBizResponse();
			standardBizResponse.setTransactionId("1");
			standardBizResponse.setResultCode("0");	
			standardBizResponse.setResultNamespace("namespace");	
			return standardBizResponse;
        }
	}

	@Override
	public ListFavoriteResponse listFavorite(ListFavoriteRequest listFavoriteRequest) 
			throws EwalletException {
        FavoriteContext[] favoriteContexts = createFavoriteContexts();
        ListFavoriteResponse listFavoriteResponse = new ListFavoriteResponse();
        listFavoriteResponse.setTransactionId("1");
        listFavoriteResponse.setResultCode("0");	
        listFavoriteResponse.setResultNamespace("namespace");	
        listFavoriteResponse.setFavoriteList(favoriteContexts);
		return listFavoriteResponse;
	}

	private FavoriteContext[] createFavoriteContexts() {
		FavoriteContext[] favoriteContexts = new FavoriteContext[4];
		favoriteContexts[0] = createFavoriteContext("1", "billpay", "trmv", "Truemove", new BigDecimal("13.00"), "20000211101010");
        favoriteContexts[1] = createFavoriteContext("2", "billpay", "tmvh", "Truemove H", new BigDecimal("19.00"), "20000212101010");
        favoriteContexts[2] = createFavoriteContext("3", "billpay", "tlp", "Truelife plus", new BigDecimal("18.00"), "20000210101010");
        favoriteContexts[3] = createFavoriteContext("4", "billpay", "mea", "Metro Electric", new BigDecimal("20.00"), "20000210101010");
		return favoriteContexts;
	}
	
	private FavoriteContext createFavoriteContext(String favoriteId, String serviceType, String serviceCode, String reference1, BigDecimal amount, String createdDate) {
		FavoriteContext favoriteContext = new FavoriteContext();
		favoriteContext.setFavoriteId(favoriteId);
		favoriteContext.setServiceType(serviceType);
		favoriteContext.setServiceCode(serviceCode);
		favoriteContext.setAmount(amount);
		favoriteContext.setReference1(reference1);
		favoriteContext.setCreatedDate(createdDate);
		return favoriteContext;
	}

	@Override
	public StandardBizResponse isFavorited(IsFavoritedRequest isFavoritedRequest)
			throws EwalletException {
        if (isFavoritedRequest.getSecurityContext().getTmnId().equals(AdamTmnMoneyID)) {
            return adam.getTmnProfileClient().isFavorited(isFavoritedRequest);
        } else if (isFavoritedRequest.getSecurityContext().getTmnId().equals(EveTmnMoneyID)) {
            return eve.getTmnProfileClient().isFavorited(isFavoritedRequest);
        } else if (isFavoritedRequest.getSecurityContext().getTmnId().equals(SimpsonsTmnMoneyID)) {
            return simpsons.getTmnProfileClient().isFavorited(isFavoritedRequest);
        } else {
			StandardBizResponse standardBizResponse = new StandardBizResponse();
			standardBizResponse.setTransactionId("1");
			standardBizResponse.setResultCode("0");	
			standardBizResponse.setResultNamespace("namespace");	
			return standardBizResponse;
        }
	}

}
