package th.co.truemoney.serviceinventory.domain;

public class ResponseBean<T> {

	private String accessToken;
	private T data;

	public ResponseBean() {

	}

	public ResponseBean(String accessToken) {
		super();
		this.accessToken = accessToken;
	}

	public ResponseBean(String accessToken, T data) {
		super();
		this.accessToken = accessToken;
		this.data = data;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String code) {
		this.accessToken = code;
	}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}
}
