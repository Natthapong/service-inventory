package th.co.truemoney.serviceinventory.ewallet.impl;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import th.co.truemoney.serviceinventory.bean.DirectDebitConfigBean;
import th.co.truemoney.serviceinventory.ewallet.SourceOfFundService;
import th.co.truemoney.serviceinventory.ewallet.domain.AccessToken;
import th.co.truemoney.serviceinventory.ewallet.domain.DirectDebit;
import th.co.truemoney.serviceinventory.ewallet.exception.EwalletException;
import th.co.truemoney.serviceinventory.ewallet.exception.ServiceUnavailableException;
import th.co.truemoney.serviceinventory.ewallet.proxy.tmnprofile.TmnProfileProxy;
import th.co.truemoney.serviceinventory.ewallet.proxy.tmnprofile.message.ListSourceRequest;
import th.co.truemoney.serviceinventory.ewallet.proxy.tmnprofile.message.ListSourceResponse;
import th.co.truemoney.serviceinventory.ewallet.proxy.tmnprofile.message.SecurityContext;
import th.co.truemoney.serviceinventory.ewallet.proxy.tmnprofile.message.SourceContext;
import th.co.truemoney.serviceinventory.ewallet.repositories.AccessTokenRepository;
import th.co.truemoney.serviceinventory.ewallet.repositories.DirectDebitConfig;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;

public class SourceOfFundServiceImpl implements SourceOfFundService {

	private static Logger logger = Logger.getLogger(SourceOfFundServiceImpl.class);

	@Autowired @Qualifier("accessTokenMemoryRepository")
	private AccessTokenRepository accessTokenRepo;

	@Autowired
	private DirectDebitConfig directDebitConfig;

	@Autowired
	private TmnProfileProxy tmnProfileProxy;

	@Override
	public List<DirectDebit> getUserDirectDebitSources(String username, String accessTokenId)
			throws ServiceInventoryException {
		try {
			AccessToken accessToken = accessTokenRepo.getAccessToken(accessTokenId);
			logger.debug("retrieve access Token: "+accessToken.toString());

			Integer channelId = accessToken.getChannelId();

			List<DirectDebit> directDebitList = null;
			ListSourceRequest listSourceRequest = createListSourceRequest(channelId, accessToken.getTruemoneyId(), accessToken.getSessionId());
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
							directDebit.setSourceOfFundId(sourceContext.getSourceId());
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
		} catch (EwalletException e) {
			throw new ServiceInventoryException(e.getCode(),
				"tmnSecurityProxy.listSource response: " + e.getCode(), e.getNamespace());
		} catch (ServiceUnavailableException e) {
			throw new ServiceInventoryException(Integer.toString(HttpServletResponse.SC_SERVICE_UNAVAILABLE),
				e.getMessage(), e.getNamespace());
		}
	}

	public void setTmnProfileProxy(TmnProfileProxy tmnProfileProxy) {
		this.tmnProfileProxy = tmnProfileProxy;
	}

	private ListSourceRequest createListSourceRequest(Integer channelId, String truemoneyId, String sessionId) {
		ListSourceRequest listSourceRequest = new ListSourceRequest();
		listSourceRequest.setChannelId(channelId);
		SecurityContext securityContext = new SecurityContext(sessionId, truemoneyId);
		listSourceRequest.setSecurityContext(securityContext);

		return listSourceRequest;
	}

}
