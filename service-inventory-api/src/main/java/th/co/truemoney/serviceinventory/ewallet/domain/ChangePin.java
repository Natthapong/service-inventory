package th.co.truemoney.serviceinventory.ewallet.domain;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
public class ChangePin implements Serializable {

	private static final long serialVersionUID = 8640195157876093995L;
	private String oldPin;
	private String pin;
	
	private ChangePin() {
		super();
	}
	
	private ChangePin(String oldPin, String pin) {
		super();
		this.oldPin = oldPin;
		this.pin = pin;
	}

	public String getOldPin() {
		return oldPin;
	}
	
	public void setOldPin(String oldPin) {
		this.oldPin = oldPin;
	}
	public String getPin() {
		return pin;
	}
	public void setPin(String pin) {
		this.pin = pin;
	}
	
}
