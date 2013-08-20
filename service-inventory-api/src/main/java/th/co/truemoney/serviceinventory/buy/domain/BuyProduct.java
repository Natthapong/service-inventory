package th.co.truemoney.serviceinventory.buy.domain;

import java.io.Serializable;
import java.math.BigDecimal;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
public class BuyProduct implements Serializable {

	private static final long serialVersionUID = -290742801797853300L;
    private String target;
    private BigDecimal amount = BigDecimal.ZERO;
    
	private String ID;
	private String transRelation;
    
    public BuyProduct() {
        super();
    }
    
    public BuyProduct(String target, BigDecimal amount) {
		super();
		this.target = target;
		this.amount = amount;
	}
    
    public BuyProduct(String ID, String transRelation, BigDecimal amount) {
		super();
		this.ID = ID;
		this.transRelation = transRelation;
		this.amount = amount;
	}
    
	public String getID() {
		return ID;
	}

	public void setID(String iD) {
		ID = iD;
	}

	public String getTransRelation() {
		return transRelation;
	}

	public void setTransRelation(String transRelation) {
		this.transRelation = transRelation;
	}

	public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    @JsonIgnore
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
				.append("ID: ", this.getID())
				.append("transRelation: ", this.getTransRelation())
				.append("target: ", this.getTarget())
				.append("amount: ", this.getAmount())
				.toString();
	}
    
}
