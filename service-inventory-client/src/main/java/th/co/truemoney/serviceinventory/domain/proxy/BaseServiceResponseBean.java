package th.co.truemoney.serviceinventory.domain.proxy;

public class BaseServiceResponseBean<T> {
	
	private String accessToken;
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

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String token) {
		this.accessToken = token;
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
