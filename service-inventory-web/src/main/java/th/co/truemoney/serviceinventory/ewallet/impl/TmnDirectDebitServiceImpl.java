package th.co.truemoney.serviceinventory.ewallet.impl;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;

import th.co.truemoney.serviceinventory.ewallet.TmnDirectDebitService;
import th.co.truemoney.serviceinventory.ewallet.domain.AccessToken;
import th.co.truemoney.serviceinventory.ewallet.domain.DirectDebit;
import th.co.truemoney.serviceinventory.ewallet.domain.DirectDebitOrder;
import th.co.truemoney.serviceinventory.ewallet.domain.DirectDebitOrderResult;
import th.co.truemoney.serviceinventory.ewallet.repositories.AccessTokenRepository;
import th.co.truemoney.serviceinventory.ewallet.repositories.DirectDebitConfig;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;
import th.co.truemoney.serviceinventory.util.FeeUtil;

public class TmnDirectDebitServiceImpl implements TmnDirectDebitService {

	@Autowired
	private AccessTokenRepository accessTokenRepo;
	
	@Autowired 
	private DirectDebitConfig directDebitConfig;
	
	@Override
	public DirectDebitOrderResult verify(Integer channelId, String accessTokenID, DirectDebitOrder directDebitOrder) 
			throws ServiceInventoryException{

		DirectDebitOrderResult orderResult = new DirectDebitOrderResult();
		
		//--- Get Account Detail from accessToken ---//
		AccessToken accessToken = accessTokenRepo.getAccessToken(accessTokenID);
		if (accessToken == null)
			throw new ServiceInventoryException("90001", "AccessToken not found");
		
		//--- Connect to Ewallet Client to verify amount on this ewallet-account ---//
		
		//--- Success : Generate OrderID and store on redis ---//
		
		//--- calculate total FEE ---//
		//DirectDebit directDebitInfo = directDebitConfig.getBankDetail(directDebitOrder.getBankCode());
		
		FeeUtil feeUtil = new FeeUtil();
		//BigDecimal totalFee = feeUtil.calculateFee(directDebitOrder.getAmount(), feeValue, feeType, minTotalFee, maxTotalFee);
		
		
		
		return orderResult;
	}

}
