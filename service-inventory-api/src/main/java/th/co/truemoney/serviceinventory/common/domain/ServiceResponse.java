package th.co.truemoney.serviceinventory.common.domain;

@SuppressWarnings("serial")
public class ServiceResponse<T> implements java.io.Serializable {

	private String transactionID;
	private String requestTransactionID;
	private String responseNamespace;
	private String responseCode;
	private String responseDesc;
	private T body;

	public ServiceResponse() {
		
	}

	public ServiceResponse(String responseNamespace, String responseCode, String responseDesc) {
		super();
		this.responseNamespace = responseNamespace;
		this.responseCode = responseCode;
		this.responseDesc = responseDesc;
	}

	public String getTransactionID() {
		return transactionID;
	}

	public void setTransactionID(String transactionID) {
		this.transactionID = transactionID;
	}

	public String getRequestTransactionID() {
		return requestTransactionID;
	}

	public void setRequestTransactionID(String requestTransactionID) {
		this.requestTransactionID = requestTransactionID;
	}

	public String getResponseNamespace() {
		return responseNamespace;
	}

	public void setResponseNamespace(String responseNamespace) {
		this.responseNamespace = responseNamespace;
	}

	public String getResponseCode() {
		return responseCode;
	}

	public void setResponseCode(String responseCode) {
		this.responseCode = responseCode;
	}

	public String getResponseDesc() {
		return responseDesc;
	}

	public void setResponseDesc(String responseDesc) {
		this.responseDesc = responseDesc;
	}

	public T getBody() {
		return body;
	}

	public void setBody(T body) {
		this.body = body;
	}

}
