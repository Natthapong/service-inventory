package th.co.truemoney.serviceinventory.ewallet.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
public class ActivityDetail extends Activity {

	private static final long serialVersionUID = -7169633329969307484L;

	protected String transactionID;
	protected String ref2;
	protected String personalMessage;
	
	public ActivityDetail() {
		super();
	}

	public String getRef2() {
		return ref2;
	}

	public void setRef2(String ref2) {
		this.ref2 = ref2;
	}

	public String getTransactionID() {
		return transactionID;
	}

	public void setTransactionID(String transactionID) {
		this.transactionID = transactionID;
	}
	
	public String getPersonalMessage() {
		return personalMessage;
	}

	public void setPersonalMessage(String personalMessage) {
		this.personalMessage = personalMessage;
	}
		
}
