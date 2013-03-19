package th.co.truemoney.serviceinventory.ewallet.impl;

import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import th.co.truemoney.serviceinventory.ewallet.SourceOfFundService;
import th.co.truemoney.serviceinventory.ewallet.domain.AccessToken;
import th.co.truemoney.serviceinventory.ewallet.domain.DirectDebit;
import th.co.truemoney.serviceinventory.ewallet.exception.EwalletException;
import th.co.truemoney.serviceinventory.ewallet.exception.ServiceUnavailableException;
import th.co.truemoney.serviceinventory.ewallet.repositories.AccessTokenRepository;
import th.co.truemoney.serviceinventory.ewallet.repositories.SourceOfFundRepository;
import th.co.truemoney.serviceinventory.exception.BaseException;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;

public class SourceOfFundServiceImpl implements SourceOfFundService {

	private static Logger logger = Logger.getLogger(SourceOfFundServiceImpl.class);

	@Autowired @Qualifier("accessTokenMemoryRepository")
	private AccessTokenRepository accessTokenRepo;

	@Autowired
	private SourceOfFundRepository sofRepo;

	@Override
	public List<DirectDebit> getUserDirectDebitSources(String username, String accessTokenID)
			throws ServiceInventoryException {
		try {
			AccessToken accessToken = accessTokenRepo.getAccessToken(accessTokenID);
			if (accessToken == null) {
				throw new ServiceInventoryException(BaseException.Code.ACCESS_TOKEN_NOT_FOUND, "AccessTokenID is expired or not found.");
			}
			logger.debug("retrieve access Token: "+accessToken.toString());

			String truemoneyId = accessToken.getTruemoneyID();
			Integer channelId = accessToken.getChannelID();
			String sessionId = accessToken.getSessionID();

			List<DirectDebit> userDirectDebitSources = sofRepo.getUserDirectDebitSources(truemoneyId, channelId, sessionId);
			
			return userDirectDebitSources;
			
		} catch (EwalletException e) {
			throw new ServiceInventoryException(e.getCode(),
				"tmnSecurityProxy.listSource response: " + e.getCode(), e.getNamespace());
		} catch (ServiceUnavailableException e) {
			throw new ServiceInventoryException(Integer.toString(HttpServletResponse.SC_SERVICE_UNAVAILABLE),
				e.getMessage(), e.getNamespace());
		}
	}

}
