package th.co.truemoney.serviceinventory.ewallet.domain;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@class")
@JsonSubTypes(value = {@Type(value = DirectDebit.class, name="directDebit")})
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class SourceOfFund implements Serializable {

	private static final long serialVersionUID = 9157552125582604595L;
	private String sourceOfFundID;
	private String sourceOfFundType;

	public SourceOfFund() {
		super();
	}

	public String getSourceOfFundID() {
		return sourceOfFundID;
	}

	public void setSourceOfFundID(String sourceOfFundID) {
		this.sourceOfFundID = sourceOfFundID;
	}

	public String getSourceOfFundType() {
		return sourceOfFundType;
	}

	public void setSourceOfFundType(String sourceOfFundType) {
		this.sourceOfFundType = sourceOfFundType;
	}
}
