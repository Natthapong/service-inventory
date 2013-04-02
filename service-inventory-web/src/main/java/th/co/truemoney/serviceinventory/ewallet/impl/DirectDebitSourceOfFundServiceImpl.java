package th.co.truemoney.serviceinventory.ewallet.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import th.co.truemoney.serviceinventory.bean.DirectDebitConfigBean;
import th.co.truemoney.serviceinventory.ewallet.domain.AccessToken;
import th.co.truemoney.serviceinventory.ewallet.domain.DirectDebit;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.ListSourceRequest;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.ListSourceResponse;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.SecurityContext;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.SourceContext;
import th.co.truemoney.serviceinventory.ewallet.proxy.tmnprofile.TmnProfileProxy;
import th.co.truemoney.serviceinventory.ewallet.repositories.AccessTokenRepository;
import th.co.truemoney.serviceinventory.ewallet.repositories.DirectDebitConfig;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryWebException;

@Service
public class DirectDebitSourceOfFundServiceImpl implements EnhancedDirectDebitSourceOfFundService {

	private static Logger logger = LoggerFactory.getLogger(DirectDebitSourceOfFundServiceImpl.class);

	@Autowired
	private AccessTokenRepository accessTokenRepo;

	@Autowired
	private TmnProfileProxy tmnProfileProxy;

	@Autowired
	private DirectDebitConfig directDebitConfig;

	@Override
	public List<DirectDebit> getUserDirectDebitSources(String username, String accessTokenID)
			throws ServiceInventoryException {

		AccessToken accessToken = accessTokenRepo.getAccessToken(accessTokenID);
		logger.debug("retrieve access Token: "+accessToken.toString());

		if (!accessToken.getUsername().equals(username)) {
			throw new ServiceInventoryWebException("401", "unauthorized access");
		}

		List<DirectDebit> userDirectDebitSources = getUserDirectDebitSources(accessToken.getTruemoneyID(), accessToken.getChannelID(), accessToken.getSessionID());

		return userDirectDebitSources;

	}

	@Override
	public DirectDebit getUserDirectDebitSource(String sourceOfFundID, String accessTokenID) throws ServiceInventoryException {

		AccessToken accessToken = accessTokenRepo.getAccessToken(accessTokenID);

		List<DirectDebit> directDebitSources = getUserDirectDebitSources(accessToken.getTruemoneyID(), accessToken.getChannelID(), accessToken.getSessionID());
		for (DirectDebit dd : directDebitSources) {
			if (dd.getSourceOfFundID().equals(sourceOfFundID)) {
				return dd;
			}
		}
		throw new ServiceInventoryWebException("404", "source of fund not found : " + sourceOfFundID);
	}

	@Override
	public BigDecimal calculateTopUpFee(BigDecimal amount, DirectDebit sofDetail) throws ServiceInventoryException {
		DirectDebitConfigBean bankConfig = directDebitConfig.getBankDetail(sofDetail.getBankCode());
		return (bankConfig != null) ? bankConfig.calculateTotalFee(amount) : BigDecimal.ZERO;
	}


	private List<DirectDebit> getUserDirectDebitSources(String truemoneyID, Integer channelID, String sessionID)
			throws ServiceInventoryException {

		List<DirectDebit> directDebitList = new ArrayList<DirectDebit>();
		ListSourceRequest listSourceRequest = createListSourceRequest(channelID, truemoneyID, sessionID);
		listSourceRequest.setSourceType("debit");
		ListSourceResponse listSourceResponse = this.tmnProfileProxy.listSource(listSourceRequest);
		SourceContext[] sourceContexts = listSourceResponse.getSourceList();
		if (sourceContexts != null && sourceContexts.length > 0) {
			directDebitList = new ArrayList<DirectDebit>();
			for (int i=0; i<sourceContexts.length; i++) {
				SourceContext sourceContext = sourceContexts[i];
				String[] sourceDetail = sourceContext.getSourceDetail();
				DirectDebit directDebit = new DirectDebit();
				if (sourceDetail != null && sourceDetail.length > 0) {
					DirectDebitConfigBean directDebitConfigBean = directDebitConfig.getBankDetail(sourceDetail[0] != null ? sourceDetail[0].trim() : "");
					if (directDebitConfigBean != null) {
						directDebit.setSourceOfFundID(sourceContext.getSourceId());
						directDebit.setSourceOfFundType(sourceContext.getSourceType());
						directDebit.setBankCode(sourceDetail[0] != null ? sourceDetail[0].trim() : "");
						directDebit.setBankAccountNumber(sourceDetail[0] != null ? sourceDetail[1].trim() : "");
						directDebit.setBankNameEn(directDebitConfigBean.getBankNameEn());
						directDebit.setBankNameTh(directDebitConfigBean.getBankNameTh());
						directDebit.setMinAmount(directDebitConfigBean.getMinAmount());
						directDebit.setMaxAmount(directDebitConfigBean.getMaxAmount());
					}
				}
				directDebitList.add(directDebit);
			}
		}
		return directDebitList;
	}

	private ListSourceRequest createListSourceRequest(Integer channelID, String truemoneyID, String sessionID) {
		ListSourceRequest listSourceRequest = new ListSourceRequest();
		listSourceRequest.setChannelId(channelID);
		SecurityContext securityContext = new SecurityContext(sessionID, truemoneyID);
		listSourceRequest.setSecurityContext(securityContext);

		return listSourceRequest;
	}

}
