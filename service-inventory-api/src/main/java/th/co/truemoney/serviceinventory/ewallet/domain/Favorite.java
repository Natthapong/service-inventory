package th.co.truemoney.serviceinventory.ewallet.domain;

import java.math.BigDecimal;

public class Favorite {
	private Long favoriteID;
	private String serviceType;
	private String serviceCode;
	private String ref1;
	private BigDecimal amount;

	public Favorite() {
	}
	
	public Favorite(Long favoriteID, String serviceType, String serviceCode,
			String ref1, BigDecimal amount) {
		super();
		this.favoriteID = favoriteID;
		this.serviceType = serviceType;
		this.serviceCode = serviceCode;
		this.ref1 = ref1;
		this.amount = amount;
	}
	
	public Long getFavoriteID() {
		return favoriteID;
	}

	public void setFavoriteID(Long favoriteID) {
		this.favoriteID = favoriteID;
	}

	public String getServiceType() {
		return serviceType;
	}

	public void setServiceType(String serviceType) {
		this.serviceType = serviceType;
	}

	public String getServiceCode() {
		return serviceCode;
	}

	public void setServiceCode(String serviceCode) {
		this.serviceCode = serviceCode;
	}

	public String getRef1() {
		return ref1;
	}

	public void setRef1(String ref1) {
		this.ref1 = ref1;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

}
