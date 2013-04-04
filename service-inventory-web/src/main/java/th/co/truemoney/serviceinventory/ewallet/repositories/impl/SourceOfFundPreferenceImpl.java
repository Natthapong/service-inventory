package th.co.truemoney.serviceinventory.ewallet.repositories.impl;

import java.io.IOException;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class SourceOfFundPreferenceImpl implements SourceOfFundPreference {

	private static Logger logger = LoggerFactory.getLogger(SourceOfFundPreferenceImpl.class);

	private HashMap<String, DirectDebitPreference> bankConfigList;

	public SourceOfFundPreferenceImpl() {
		try {
			JsonFactory factory = new JsonFactory();
			ObjectMapper m = new ObjectMapper(factory);

			TypeReference<HashMap<String, DirectDebitPreference>> typeRef;
			typeRef = new TypeReference<HashMap<String, DirectDebitPreference>>() {
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
	public DirectDebitPreference getBankPreference(String bankCode) {
		logger.debug("direct debit config size: " + bankConfigList.size());
		return bankConfigList.get(bankCode);
	}

}