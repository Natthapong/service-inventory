package th.co.truemoney.serviceinventory.ewallet.domain;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
public class ActivityDetail extends Activity {

	private static final long serialVersionUID = -7169633329969307484L;

	protected String transactionID;
	protected String ref2;
	protected String personalMessage;
	protected Boolean favoritable = Boolean.FALSE;
	protected Boolean favorited = Boolean.FALSE;
	protected String additionalData;
	
	public ActivityDetail() {
		super();
	}

	public String getRef2() {
		return ref2;
	}

	public void setRef2(String ref2) {
		this.ref2 = ref2;
	}

	public String getTransactionID() {
		return transactionID;
	}

	public void setTransactionID(String transactionID) {
		this.transactionID = transactionID;
	}
	public String getPersonalMessage() {
		return personalMessage;
	}

	public void setPersonalMessage(String personalMessage) {
		this.personalMessage = personalMessage;
	}
	
	public Boolean isFavoritable() {
		return favoritable;
	}

	public void setFavoritable(Boolean favoritable) {
		this.favoritable = favoritable;
	}
	
	public Boolean isFavorited() {
		return favorited;
	}

	public void setFavorited(Boolean favorited) {
		this.favorited = favorited;
	}

	public String getAdditionalData() {
		return additionalData;
	}

	public void setAdditionalData(String additionalData) {
		this.additionalData = additionalData;
	}

	@JsonIgnore
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
			.appendSuper(super.toString())
			.append("transactionID: ", this.getTransactionID())
			.append("ref2: ", this.getRef2())
			.toString();
	}

}
