package th.co.truemoney.serviceinventory.ewallet.repositories;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;

import th.co.truemoney.serviceinventory.bean.DirectDebitConfigBean;
import th.co.truemoney.serviceinventory.ewallet.domain.DirectDebit;
import th.co.truemoney.serviceinventory.ewallet.exception.EwalletException;
import th.co.truemoney.serviceinventory.ewallet.exception.ServiceUnavailableException;
import th.co.truemoney.serviceinventory.ewallet.proxy.tmnprofile.TmnProfileProxy;
import th.co.truemoney.serviceinventory.ewallet.proxy.tmnprofile.message.ListSourceRequest;
import th.co.truemoney.serviceinventory.ewallet.proxy.tmnprofile.message.ListSourceResponse;
import th.co.truemoney.serviceinventory.ewallet.proxy.tmnprofile.message.SecurityContext;
import th.co.truemoney.serviceinventory.ewallet.proxy.tmnprofile.message.SourceContext;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;

public class SourceOfFundRepository {
	
	@Autowired
	private TmnProfileProxy tmnProfileProxy;
	
	@Autowired
	private DirectDebitConfig directDebitConfig;
	
	public DirectDebit getUserDirectDebitSourceById(String sourceOfFundId, String truemoneyId, Integer channelId, String sessionId) {
		List<DirectDebit> directDebitSources = getUserDirectDebitSources(truemoneyId, channelId, sessionId);
		
		for (DirectDebit dd : directDebitSources) {
			if (dd.getSourceOfFundId().equals(sourceOfFundId)) {
				return dd;
			}
		}
		
		throw new ServiceInventoryException("404", "source of fund not found : " + sourceOfFundId);
	}
	
	public List<DirectDebit> getUserDirectDebitSources(String truemoneyId, Integer channelId, String sessionId)
			throws ServiceInventoryException {
		try {


			List<DirectDebit> directDebitList = null;
			ListSourceRequest listSourceRequest = createListSourceRequest(channelId, truemoneyId, sessionId);
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
