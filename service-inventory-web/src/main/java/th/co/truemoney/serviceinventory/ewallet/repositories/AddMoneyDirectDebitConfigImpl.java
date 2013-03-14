package th.co.truemoney.serviceinventory.ewallet.repositories;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

import th.co.truemoney.serviceinventory.bean.AddMoneyBankDetail;

public class AddMoneyDirectDebitConfigImpl implements AddMoneyDirectDebitConfig {

	private HashMap<String, AddMoneyBankDetail> bankConfigList;

	public AddMoneyDirectDebitConfigImpl() {
		try {
			JsonFactory factory = new JsonFactory();
			ObjectMapper m = new ObjectMapper(factory);

			TypeReference<HashMap<String, AddMoneyBankDetail>> typeRef;
			typeRef = new TypeReference<HashMap<String, AddMoneyBankDetail>>() {
			};

			bankConfigList = m.readValue(new File(
					"src/main/resources/addmoney/directdebit.json"), typeRef);
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
	public AddMoneyBankDetail getBankDetail(String bankCode) {
		return bankConfigList.get(bankCode);
	}

}
