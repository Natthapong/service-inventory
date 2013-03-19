package th.co.truemoney.serviceinventory.domain.proxy;

public class BaseServiceResponseBean<T> {
	
	private String accessTokenID;
	private String fullname;
	private Double currentBalance;
	private T body;

	public BaseServiceResponseBean() {
	}
	
	public T getBody() {
		return body;
	}

	public void setBody(T body) {
		this.body = body;
	}

	public String getAccessTokenID() {
		return accessTokenID;
	}

	public void setAccessTokenID(String token) {
		this.accessTokenID = token;
	}

	public String getFullname() {
		return fullname;
	}

	public void setFullname(String fullname) {
		this.fullname = fullname;
	}

	public Double getCurrentBalance() {
		return currentBalance;
	}

	public void setCurrentBalance(Double currentBalance) {
		this.currentBalance = currentBalance;
	}
	
}
