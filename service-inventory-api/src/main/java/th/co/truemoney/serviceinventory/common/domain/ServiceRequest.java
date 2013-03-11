package th.co.truemoney.serviceinventory.common.domain;

@SuppressWarnings("serial")
public class ServiceRequest<T> implements java.io.Serializable {

	private String requestTransactionID;
	private String requestChannelID;
	private String checkSum;
	private T body;
	
	public String getRequestTransactionID() {
		return requestTransactionID;
	}
	public void setRequestTransactionID(String requestTransactionID) {
		this.requestTransactionID = requestTransactionID;
	}	
	public String getRequestChannelID() {
		return requestChannelID;
	}
	public void setRequestChannelID(String requestChannelID) {
		this.requestChannelID = requestChannelID;
	}
	public String getCheckSum() {
		return checkSum;
	}
	public void setCheckSum(String checkSum) {
		this.checkSum = checkSum;
	}
	public T getBody() {
		return body;
	}
	public void setBody(T body) {
		this.body = body;
	}
		
}
