package th.co.truemoney.serviceinventory.ewallet.domain;

import java.math.BigDecimal;
import java.util.Date;

public class Favorite {
	private Long favoriteID;
	private String serviceType;
	private String serviceCode;
	private String ref1;
	private BigDecimal amount;
	private Date date;

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

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

}
