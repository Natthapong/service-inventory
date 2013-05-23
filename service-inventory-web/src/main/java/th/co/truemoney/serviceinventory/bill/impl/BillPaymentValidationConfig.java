package th.co.truemoney.serviceinventory.bill.impl;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class BillPaymentValidationConfig {
	
	private static Logger logger = LoggerFactory.getLogger(BillPaymentValidationConfig.class);
	
	private Map<String, BillPaymentValidation> validation;
	
	public BillPaymentValidationConfig() {
		JsonFactory factory = new JsonFactory();
		ObjectMapper m = new ObjectMapper(factory);

		TypeReference<HashMap<String, BillPaymentValidation>> typeRef;
			typeRef = new TypeReference<HashMap<String, BillPaymentValidation>>() {
		};

		ClassPathResource resource = new ClassPathResource("bill/bill-validation.json");
		try {
			validation = m.readValue(resource.getFile(), typeRef);
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
	}
	
	public BillPaymentValidation getBillValidation(String billerCode) {
		return validation.get(billerCode);
	}
	
}
