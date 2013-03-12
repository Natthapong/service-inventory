package th.co.truemoney.serviceinventory.domain.proxy;

public class BaseServiceRequestBean<T> {
	private String tmnAppID;
	private String tmnAppPassword;
	private String requestTransactionID;
	private String groupTransactionID;
	private String checkSum;
	private T body;

	public String getTmnAppID() {
		return tmnAppID;
	}

	public void setTmnAppID(String tmnAppID) {
		this.tmnAppID = tmnAppID;
	}

	public String getTmnAppPassword() {
		return tmnAppPassword;
	}

	public void setTmnAppPassword(String tmnAppPassword) {
		this.tmnAppPassword = tmnAppPassword;
	}

	public String getRequestTransactionID() {
		return requestTransactionID;
	}

	public void setRequestTransactionID(String requestTransactionID) {
		this.requestTransactionID = requestTransactionID;
	}

	public String getGroupTransactionID() {
		return groupTransactionID;
	}

	public void setGroupTransactionID(String groupTransactionID) {
		this.groupTransactionID = groupTransactionID;
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
