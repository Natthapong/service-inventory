package th.co.truemoney.serviceinventory.domain;

public class ResponseBean<T> {
	
	private String code;
	private String messageTh;
	private String messageEn;
	private T data;		
	
	public ResponseBean() {
		
	}
	
	public ResponseBean(String code, String messageEn) {
		super();
		this.code = code;
		this.messageEn = messageEn;
	}
	
	public ResponseBean(String code, String messageTh, String messageEn) {
		super();
		this.code = code;
		this.messageTh = messageTh;
		this.messageEn = messageEn;
	}
	
	public ResponseBean(String code, String messageTh, String messageEn, T data) {
		super();
		this.code = code;
		this.messageTh = messageTh;
		this.messageEn = messageEn;
		this.data = data;
	}
	
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getMessageTh() {
		return messageTh;
	}
	public void setMessageTh(String messageTh) {
		this.messageTh = messageTh;
	}
	public String getMessageEn() {
		return messageEn;
	}
	public void setMessageEn(String messageEn) {
		this.messageEn = messageEn;
	}
	public T getData() {
		return data;
	}
	public void setData(T data) {
		this.data = data;
	}
}
