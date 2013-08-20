package th.co.truemoney.serviceinventory.legacyfacade.facade.builders;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;

import th.co.truemoney.serviceinventory.buy.domain.BuyProduct;
import th.co.truemoney.serviceinventory.legacyfacade.handlers.BuyProductHandler;

public class BuyProductBuilder {

	private BuyProductHandler buyProductHandler;
	
	private String channel;
	private String channelDetail;
	
	private String appUser;
	private String appPassword;
	private String appKey;
	
	private String sessionID;
	private String tmnID;
	private String commandAction;
	
	private String targetMobileNumber;
	private String ref1;
	private BigDecimal amount;
	private BigDecimal serviceFee;
	private BigDecimal sourceOfFundFee;
	private String sourceOfFundSourceType;
	
	@Autowired(required = false)
	public BuyProductBuilder(BuyProductHandler buyProductHandler) {
		this.buyProductHandler = buyProductHandler;
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
	
	public BuyProductBuilder toMobileNumber(String targetMobileNumber) {
		this.targetMobileNumber = targetMobileNumber;
		this.ref1 = targetMobileNumber;
		return this;
	}
	
	public BuyProductBuilder usingSourceOfFund(String sourceOfFundSourceType) {
		this.commandAction = sourceOfFundSourceType;
		this.sourceOfFundSourceType = sourceOfFundSourceType;
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
	
	public BuyProduct verifyBuyProduct() {
		return null;
	}

}
