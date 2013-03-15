package th.co.truemoney.serviceinventory.ewallet.repositories.impl;

import java.io.IOException;
import java.util.HashMap;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.springframework.core.io.ClassPathResource;

import th.co.truemoney.serviceinventory.ewallet.domain.DirectDebit;
import th.co.truemoney.serviceinventory.ewallet.repositories.DirectDebitConfig;

public class DirectDebitConfigImpl implements DirectDebitConfig {

	private HashMap<String, DirectDebit> bankConfigList;

	public DirectDebitConfigImpl() {
		try {
			JsonFactory factory = new JsonFactory();
			ObjectMapper m = new ObjectMapper(factory);

			TypeReference<HashMap<String, DirectDebit>> typeRef;
			typeRef = new TypeReference<HashMap<String, DirectDebit>>() {
			};
			ClassPathResource resource = new ClassPathResource("addmoney/directdebit.json");
			bankConfigList = m.readValue(resource.getFile(), typeRef);
		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public DirectDebit getBankDetail(String bankCode) {
		return bankConfigList.get(bankCode);
	}

}
