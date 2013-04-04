package th.co.truemoney.serviceinventory.ewallet.impl;

import java.math.BigDecimal;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import th.co.truemoney.serviceinventory.ewallet.EnhancedDirectDebitSourceOfFundService;
import th.co.truemoney.serviceinventory.ewallet.domain.AccessToken;
import th.co.truemoney.serviceinventory.ewallet.domain.DirectDebit;
import th.co.truemoney.serviceinventory.ewallet.repositories.AccessTokenRepository;
import th.co.truemoney.serviceinventory.ewallet.repositories.impl.DirectDebitPreference;
import th.co.truemoney.serviceinventory.ewallet.repositories.impl.SourceOfFundPreference;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryWebException;
import th.co.truemoney.serviceinventory.legacyfacade.ewallet.LegacyFacade;

@Service
public class DirectDebitSourceOfFundServiceImpl implements EnhancedDirectDebitSourceOfFundService {

	private static Logger logger = LoggerFactory.getLogger(DirectDebitSourceOfFundServiceImpl.class);

	@Autowired
	private LegacyFacade legacyFacade;

	@Autowired
	private SourceOfFundPreference preference;

	@Autowired
	private AccessTokenRepository accessTokenRepo;

	@Override
	public List<DirectDebit> getUserDirectDebitSources(String username, String accessTokenID)
			throws ServiceInventoryException {

		AccessToken accessToken = accessTokenRepo.getAccessToken(accessTokenID);
		logger.debug("retrieve access Token: " + accessToken.toString());

		if (!accessToken.getUsername().equals(username)) {
			throw new ServiceInventoryWebException("401", "unauthorized access");
		}

		String sessionID = accessToken.getSessionID();
		String truemoneyID = accessToken.getTruemoneyID();
		Integer channelID = accessToken.getChannelID();

		List<DirectDebit> userDirectDebitSources = legacyFacade
					.userProfile(sessionID, truemoneyID)
					.fromChannel(channelID)
					.getDirectDebitSourceOfFundList();

		for (DirectDebit directDebit : userDirectDebitSources) {
			updateDirectDebitDataWithBankPreference(directDebit);
		}

		return userDirectDebitSources;
	}

	@Override
	public DirectDebit getUserDirectDebitSource(String sourceOfFundID, String accessTokenID) throws ServiceInventoryException {

		AccessToken accessToken = accessTokenRepo.getAccessToken(accessTokenID);

		String sessionID = accessToken.getSessionID();
		String truemoneyID = accessToken.getTruemoneyID();
		Integer channelID = accessToken.getChannelID();

		List<DirectDebit> directDebitSources = legacyFacade
					.userProfile(sessionID, truemoneyID)
					.fromChannel(channelID)
					.getDirectDebitSourceOfFundList();

		for (DirectDebit directDebit : directDebitSources) {

			if (directDebit.getSourceOfFundID().equals(sourceOfFundID)) {
				updateDirectDebitDataWithBankPreference(directDebit);
				return directDebit;
			}
		}
		throw new ServiceInventoryWebException("404", "source of fund not found : " + sourceOfFundID);
	}

	@Override
	public BigDecimal calculateTopUpFee(BigDecimal amount, DirectDebit sofDetail) throws ServiceInventoryException {
		 DirectDebitPreference bankConfig = preference.getBankPreference(sofDetail.getBankCode());
		return (bankConfig != null) ? bankConfig.calculateTotalFee(amount) : BigDecimal.ZERO;
	}

	private void updateDirectDebitDataWithBankPreference(DirectDebit directDebit) {
		DirectDebitPreference bankPreference = preference.getBankPreference(directDebit.getBankCode());

		if (bankPreference != null) {
			directDebit.setBankNameEn(bankPreference.getBankNameEn());
			directDebit.setBankNameTh(bankPreference.getBankNameTh());
			directDebit.setMaxAmount(bankPreference.getMaxAmount());
			directDebit.setMinAmount(bankPreference.getMinAmount());
		}
	}

	public void setLegacyFacade(LegacyFacade legacyFacade) {
		this.legacyFacade = legacyFacade;
	}
}
