package th.co.truemoney.serviceinventory.domain;

public class ResponseBean<T> {

	private String accessTokenID;
	private T data;

	public ResponseBean() {

	}

	public ResponseBean(String accessTokenID) {
		super();
		this.accessTokenID = accessTokenID;
	}

	public ResponseBean(String accessTokenID, T data) {
		super();
		this.accessTokenID = accessTokenID;
		this.data = data;
	}

	public String getAccessTokenID() {
		return accessTokenID;
	}

	public void setAccessTokenID(String code) {
		this.accessTokenID = code;
	}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}
}
