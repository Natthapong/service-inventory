package th.co.truemoney.serviceinventory.transfer.domain;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
public class P2PTransfer implements Serializable {

	private static final long serialVersionUID = 6509557907381958820L;
	
	private String recipientName;
	private String recipientImageFileName;
	
	public P2PTransfer() {
		super();
	}
	
	public P2PTransfer(String recipientName, String recipientImageFileName) {
		super();
		this.recipientName = recipientName;
		this.recipientImageFileName = recipientImageFileName;
	}

	public String getRecipientName() {
		return recipientName;
	}

	public void setRecipientName(String recipientName) {
		this.recipientName = recipientName;
	}

	public String getRecipientImageFileName() {
		return recipientImageFileName;
	}

	public void setRecipientImageFileName(String recipientImageFileName) {
		this.recipientImageFileName = recipientImageFileName;
	}
		
}
