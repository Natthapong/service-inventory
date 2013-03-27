package th.co.truemoney.serviceinventory.ewallet.client;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

import th.co.truemoney.serviceinventory.ewallet.SourceOfFundService;
import th.co.truemoney.serviceinventory.ewallet.client.config.EndPoints;
import th.co.truemoney.serviceinventory.ewallet.domain.DirectDebit;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;

@Service
public class TmnSourceOfFundServiceClient implements SourceOfFundService {

	@Autowired
	RestTemplate restTemplate;

	@Autowired
	private EndPoints environmentConfig;

	@Autowired
	private HttpHeaders headers;
	
	private ObjectMapper mapper = new ObjectMapper();
	private List listFromMapper = null;
	private List<DirectDebit> responseList = new ArrayList<DirectDebit>();
	
	@SuppressWarnings({ "rawtypes" })
	@Override
	public List<DirectDebit> getUserDirectDebitSources(String username,
			String accessTokenID) throws ServiceInventoryException {
		HttpEntity<String> requestEntity = new HttpEntity<String>(headers);

		ResponseEntity<String> responseEntity = restTemplate.exchange(
				environmentConfig.getUserDirectDebitSourceOfFundsUrl(),
					HttpMethod.GET, requestEntity, String.class, username, accessTokenID);

		String directDebitsJson = responseEntity.getBody();
		
		try {
			listFromMapper = mapper.readValue(directDebitsJson, List.class);
		} catch (IOException e) {
			throw new ServiceInventoryException("500","Cannot Parse Json back","TMN-SI-CLIENT");
		}
		
		//work around fix...
		for(int i = 0; i < listFromMapper.size() ; i++){
			Map map = (Map) (listFromMapper.get(i));
			DirectDebit debit = new DirectDebit();
			debit.setBankAccountNumber(map.get("bankAccountNumber").toString());
			debit.setBankCode(map.get("bankCode").toString());
			debit.setBankNameEn(map.get("bankNameEn").toString());
			debit.setBankNameTh(map.get("bankNameTh").toString());
			debit.setMaxAmount(new BigDecimal(map.get("maxAmount").toString()));
			debit.setMinAmount(new BigDecimal(map.get("minAmount").toString()));
			responseList.add(debit);
		}
		
		return responseList;
	}

}
