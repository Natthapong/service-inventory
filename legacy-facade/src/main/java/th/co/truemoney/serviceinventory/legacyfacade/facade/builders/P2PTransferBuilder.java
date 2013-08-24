package th.co.truemoney.serviceinventory.legacyfacade.facade.builders;

import java.math.BigDecimal;

import org.apache.commons.lang.Validate;
import org.springframework.beans.factory.annotation.Autowired;

import th.co.truemoney.serviceinventory.legacyfacade.handlers.EwalletBalanceHandler;
import th.co.truemoney.serviceinventory.transfer.domain.P2PTransactionConfirmationInfo;
import th.co.truemoney.serviceinventory.transfer.domain.P2PTransfer;

public class P2PTransferBuilder {

    private Integer channelID;

    private String sessionID;
    
    private String tmnID;

    private BigDecimal amount;

    private String targetMobileNumber;

    private EwalletBalanceHandler balanceFacade;

    private String personalMessage;

    @Autowired(required = false)
    public P2PTransferBuilder(EwalletBalanceHandler balanceFacade) {
        this.balanceFacade = balanceFacade;
    }

    public P2PTransferBuilder fromChannelID(Integer channelID) {
        this.channelID = channelID;
        return this;
    }

    public P2PTransferBuilder fromUser(String sessionID, String tmnID) {
        this.sessionID = sessionID;
        this.tmnID = tmnID;
        return this;
    }

    public P2PTransferBuilder toTargetUser(String targetMobileNumber) {
        this.targetMobileNumber = targetMobileNumber;
        return this;
    }

    public P2PTransferBuilder withAmount(BigDecimal amount) {
        this.amount = amount;
        return this;
    }

    /**
     * set personal message that user want to send to receiver
     *
     */
    public P2PTransferBuilder setPersonalMessage(String message) {
        this.personalMessage = message;
        return this;
    }

    public P2PTransfer verify() {

        Validate.notNull(tmnID, "data missing. transfer money from whom?");
        Validate.notNull(sessionID, "data missing. transfer money from whom?");
        Validate.notNull(channelID, "data missing. transfer money from which channel?");
        Validate.notNull(amount, "data missing. how much to transfer?");
        Validate.notNull(targetMobileNumber, "data missing. whom to transfer money to?");

        return balanceFacade.verifyTransferFromPersonToPerson(amount, targetMobileNumber, channelID, sessionID, tmnID);
    }

    public P2PTransactionConfirmationInfo performTransfer() {

        Validate.notNull(tmnID, "data missing. transfer money from whom?");
        Validate.notNull(sessionID, "data missing. transfer money from whom?");
        Validate.notNull(channelID, "data missing. transfer money from which channel?");
        Validate.notNull(amount, "data missing. how much to transfer?");
        Validate.notNull(targetMobileNumber, "data missing. whom to transfer money to?");

        return balanceFacade.transferFromPersonToPerson(amount, targetMobileNumber, channelID, sessionID, tmnID, personalMessage);
    }
    
}