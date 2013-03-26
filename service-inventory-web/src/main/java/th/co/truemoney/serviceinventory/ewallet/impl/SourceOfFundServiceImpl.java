package th.co.truemoney.serviceinventory.ewallet.impl;

import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

	private static Logger logger = LoggerFactory.getLogger(SourceOfFundServiceImpl.class);

	@Autowired @Qualifier("accessTokenRedisRepository")
	private AccessTokenRepository accessTokenRepo;

	@Autowired
	private SourceOfFundRepository sofRepo;

	@Override
	public List<DirectDebit> getUserDirectDebitSources(String username, String accessTokenID)
			throws ServiceInventoryException {
		try {
			AccessToken accessToken = accessTokenRepo.getAccessToken(accessTokenID);
			logger.debug("retrieve access Token: "+accessToken.toString());
			
			if (!username.equals(accessToken.getUsername())) {
				throw new ServiceInventoryException(BaseException.Code.GENERAL_ERROR, "Invalid user name.");
			}

			List<DirectDebit> userDirectDebitSources = sofRepo
					.getUserDirectDebitSources(accessToken.getTruemoneyID(),
							accessToken.getChannelID(),
							accessToken.getSessionID());

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
