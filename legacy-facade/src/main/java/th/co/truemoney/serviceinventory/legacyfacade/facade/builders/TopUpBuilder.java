package th.co.truemoney.serviceinventory.legacyfacade.facade.builders;

import java.math.BigDecimal;

import org.apache.commons.lang.Validate;
import org.springframework.beans.factory.annotation.Autowired;

import th.co.truemoney.serviceinventory.ewallet.domain.TopUpConfirmationInfo;
import th.co.truemoney.serviceinventory.legacyfacade.handlers.EwalletBalanceHandler;

public class TopUpBuilder {

	private Integer channelID;

	private String sessionID;
	
	private String tmnID;

	private BigDecimal amount;

	private String sourceOfFundID;
	
	private String sourceOfFundType;

	private EwalletBalanceHandler balanceFacade;

	@Autowired(required = false)
	public TopUpBuilder(EwalletBalanceHandler balanceFacade) {
		this.balanceFacade = balanceFacade;
	}

	public TopUpBuilder fromChannelID(Integer channelID) {
		this.channelID = channelID;
		return this;
	}

	public TopUpBuilder fromUser(String sessionID, String tmnID) {
		this.sessionID = sessionID;
		this.tmnID = tmnID;
		return this;
	}

	public TopUpBuilder withAmount(BigDecimal amount) {
		this.amount = amount;
		return this;
	}

	public TopUpBuilder usingSourceOFFund(String sourceOfFundID, String sourceOfFundType) {
		this.sourceOfFundID = sourceOfFundID;
		this.sourceOfFundType = sourceOfFundType;
		return this;
	}

	public void verify() {

		Validate.notNull(tmnID, "data missing. topup from whom?");
		Validate.notNull(sessionID, "data missing. topup from whom?");
		Validate.notNull(channelID, "data missing. topup from which channel?");
		Validate.notNull(amount, "data missing. how much to top up?");
		Validate.notNull(sourceOfFundID, "data missing. using which source of fund to top up?");
		Validate.notNull(sourceOfFundType, "data missing. using which source of fund to top up?");

		balanceFacade.verifyTopupToMyWallet(amount, sourceOfFundID, sourceOfFundType, channelID, sessionID, tmnID);
	}

	public TopUpConfirmationInfo performTopUp() {

		Validate.notNull(tmnID, "data missing. topup from whom?");
		Validate.notNull(sessionID, "data missing. topup from whom?");
		Validate.notNull(channelID, "data missing. topup from which channel?");
		Validate.notNull(amount, "data missing. how much to top up?");
		Validate.notNull(sourceOfFundID, "data missing. using withc source of fund to top up?");
		Validate.notNull(sourceOfFundType, "data missing. using withc source of fund to top up?");

		return balanceFacade.topupToMyWallet(amount, sourceOfFundID, sourceOfFundType, channelID, sessionID, tmnID);
	}
}