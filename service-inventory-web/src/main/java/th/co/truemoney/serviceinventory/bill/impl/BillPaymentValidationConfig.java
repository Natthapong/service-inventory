package th.co.truemoney.serviceinventory.bill.impl;

import java.io.IOException;
import java.util.HashMap;

import org.springframework.core.io.ClassPathResource;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class BillPaymentValidationConfig {

	private HashMap<String, BillPaymentValidation> validation;
	
	public BillPaymentValidationConfig() {
		JsonFactory factory = new JsonFactory();
		ObjectMapper m = new ObjectMapper(factory);

		TypeReference<HashMap<String, BillPaymentValidation>> typeRef;
			typeRef = new TypeReference<HashMap<String, BillPaymentValidation>>() {
		};

		ClassPathResource resource = new ClassPathResource("bill/bill-validation.json");
		try {
			validation = m.readValue(resource.getFile(), typeRef);
		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public BillPaymentValidation getBillValidation(String billerCode) {
		return validation.get(billerCode);
	}
	
}
