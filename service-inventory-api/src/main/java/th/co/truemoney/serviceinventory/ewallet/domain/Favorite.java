package th.co.truemoney.serviceinventory.ewallet.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Favorite implements Serializable {
	
	private static final long serialVersionUID = 6081844741612178872L;
	
	private Long favoriteID;
	private String serviceType;
	private String serviceCode;
	private String ref1;
	private String ref2;
	private BigDecimal amount;
	private Date date;

	public Favorite() {
	}
	
	public Favorite(Long favoriteID, String serviceType, String serviceCode,
			String ref1, String ref2, BigDecimal amount) {
		super();
		this.favoriteID = favoriteID;
		this.serviceType = serviceType;
		this.serviceCode = serviceCode;
		this.ref1 = ref1;
		this.ref2 = ref2;
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
	
	public String getRef2() {
		return ref2;
	}

	public void setRef2(String ref2) {
		this.ref2 = ref2;
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
	
	@JsonIgnore
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
				.append("favoriteID: ", this.getFavoriteID())
				.append("serviceType: ", this.getServiceType())
				.append("serviceCode: ", this.getServiceCode())
				.append("ref1: ", this.getRef1())
				.append("ref2: ", this.getRef2())
				.append("amount: ", this.getAmount())
				.append("date: ", this.getDate())
				.toString();
	}

}
