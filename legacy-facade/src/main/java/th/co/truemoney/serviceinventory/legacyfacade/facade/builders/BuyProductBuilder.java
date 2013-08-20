package th.co.truemoney.serviceinventory.legacyfacade.facade.builders;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;

import org.apache.commons.lang.Validate;
import org.springframework.beans.factory.annotation.Autowired;

import th.co.truemoney.serviceinventory.buy.domain.BuyProduct;
import th.co.truemoney.serviceinventory.engine.client.domain.services.VerifyBuyRequest;
import th.co.truemoney.serviceinventory.engine.client.exception.SIEngineException;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;
import th.co.truemoney.serviceinventory.legacyfacade.handlers.BuyProductHandler;

public class BuyProductBuilder {

	private BuyProductHandler buyProductFacade;
	
	private String channel;
	private String channelDetail;
	
	private String appUser;
	private String appPassword;
	private String appKey;
	
	private String sessionID;
	private String tmnID;
	private String commandAction;
	
	private String recipientMobileNumber;
	private String ref1;
	private BigDecimal amount;
	private String targetProduct;
	private BigDecimal serviceFee;
	private BigDecimal sourceOfFundFee;
	private String sourceOfFundSourceType;
	
	@Autowired(required = false)
	public BuyProductBuilder(BuyProductHandler buyProductFacade) {
		this.buyProductFacade = buyProductFacade;
	}

	public BuyProductBuilder fromChannel(String channel, String channelDetail) {
		this.channel = channel;
		this.channelDetail = channelDetail;
		return this;
	}

	public BuyProductBuilder fromApp(String appUser, String appPassword, String appKey) {
		this.appUser = appUser;
		this.appPassword = appPassword;
		this.appKey = appKey;
		return this;
	}
	
	public BuyProductBuilder fromUser(String sessionID, String tmnID) {
		this.sessionID = sessionID;
		this.tmnID = tmnID;
		return this;
	}
	
	public BuyProductBuilder toRecipientMobileNumber(String recipientMobileNumber) {
		this.recipientMobileNumber = recipientMobileNumber;
		this.ref1 = recipientMobileNumber;
		return this;
	}
	
	public BuyProductBuilder usingSourceOfFund(String sourceOfFundSourceType) {
		this.commandAction = sourceOfFundSourceType;
		this.sourceOfFundSourceType = sourceOfFundSourceType;
		this.sourceOfFundFee = BigDecimal.ZERO;
		this.serviceFee = BigDecimal.ZERO;
		return this;
	}

	public BuyProductBuilder withAmount(BigDecimal amount) {
		this.amount = amount;
		return this;
	}
	
	public BuyProductBuilder andFee(BigDecimal serviceFee, BigDecimal sourceOfFundFee) {
		this.serviceFee = serviceFee;
		this.sourceOfFundFee = sourceOfFundFee;
		return this;
	}
	
	public BuyProductBuilder withTargetProduct(String target) {
		this.targetProduct = target;
		return this;
	} 
	
	public BuyProduct verifyBuyProduct() {
		Validate.notNull(amount, "data missing. how much to buy ?");
		Validate.notNull(serviceFee, "data missing. missing service fee value");
		Validate.notNull(sourceOfFundFee, "data missing. missing source of fund fee value");
		Validate.notNull(targetProduct, "data missing. target missing?");

		Validate.notNull(tmnID, "data missing. missing ewallet source of fund user detail?");
		Validate.notNull(sessionID, "data missing. missing ewallet source of fund user detail?");

		Validate.notNull(appUser, "data missing.verify topping from which source?");
		Validate.notNull(appPassword, "data missing. verify topping from which source?");
		Validate.notNull(appKey, "data missing. verify topping from which source?");
		Validate.notNull(channel, "data missing. verify topping from which channel?");
		Validate.notNull(channelDetail, "data missing. verify topping from which channel detail.");
		Validate.notNull(commandAction, "data missing. verify topping from which command action.");
		
		VerifyBuyRequest verifyRequest = new VerifyBuyRequest();

		verifyRequest.setAppUser(appUser);
		verifyRequest.setAppPassword(appPassword);
		verifyRequest.setAppKey(appKey);
		
		verifyRequest.setChannel(channel);
		verifyRequest.setChannelDetail(channelDetail);
		verifyRequest.setCommandAction(commandAction);
		
		verifyRequest.setTmnID(tmnID);
		verifyRequest.setSession(sessionID);

		verifyRequest.setAmount(convertMoney(amount));
		verifyRequest.setSource(sourceOfFundSourceType);
		verifyRequest.setSourceFeeType("THB");
		verifyRequest.setTotalSourceFee(convertMoney(sourceOfFundFee));
		verifyRequest.setServiceFeeType("THB");
		verifyRequest.setTotalServiceFee(convertMoney(serviceFee));
		verifyRequest.setTarget(targetProduct);
		
		return buyProductFacade.verifyBuyProduct(verifyRequest);
	}

	private String convertMoney(BigDecimal value) {
		BigDecimal scaled = value.setScale(2, RoundingMode.HALF_UP);
		String formatedString = new DecimalFormat("#0.00").format(scaled);
		int lastIndex = formatedString.lastIndexOf('.');
		return formatedString.substring(0, lastIndex) + formatedString.substring(lastIndex + 1);
	}
	
    public static class VerifyBuyProductFailException extends ServiceInventoryException{
		private static final long serialVersionUID = 2783748973750123315L;

		public VerifyBuyProductFailException(SIEngineException ex) {
            super(500,ex.getCode(),"Verify Buy product fail with code: " + ex.getCode(),ex.getNamespace(),ex.getMessage());
        }
    }
	
}
