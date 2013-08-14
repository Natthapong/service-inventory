package th.co.truemoney.serviceinventory.legacyfacade.facade.builders;

import java.math.BigDecimal;
import java.util.List;

import org.apache.commons.lang.Validate;
import org.springframework.beans.factory.annotation.Autowired;

import th.co.truemoney.serviceinventory.ewallet.domain.DirectDebit;
import th.co.truemoney.serviceinventory.ewallet.domain.Favorite;
import th.co.truemoney.serviceinventory.ewallet.domain.TmnProfile;
import th.co.truemoney.serviceinventory.legacyfacade.handlers.EwalletBalanceHandler;
import th.co.truemoney.serviceinventory.legacyfacade.handlers.TopUpSourceOfFundHandler;
import th.co.truemoney.serviceinventory.legacyfacade.handlers.UserProfileHandler;

public class UserProfileBuilder {

    private Integer channelID;    
    private String sessionID;    
    private String tmnID;
    private String serviceType;    
    private String serviceCode;    
    private String reference1;
    private String oldPin;
    private String pin;
    private String oldPassword;
    private String password;
    private String fullname;
    private String profileImage;
    private String loginID;
    private Favorite favorite;
    
    private EwalletBalanceHandler balanceFacade;    
    private UserProfileHandler profileFacade;    
    private TopUpSourceOfFundHandler sourceOfFundFacade;
    
    public UserProfileBuilder() {
    	
    }
    
    @Autowired(required = false)
    public UserProfileBuilder(EwalletBalanceHandler balanceFacade, UserProfileHandler profileFacade, TopUpSourceOfFundHandler sourceOfFundFacade) {
        this.balanceFacade = balanceFacade;
        this.profileFacade = profileFacade;
        this.sourceOfFundFacade = sourceOfFundFacade;
    }

    public UserProfileBuilder aUser(String sessionID, String tmnID) {
        this.sessionID = sessionID;
        this.tmnID = tmnID;
        return this;
    }

    public UserProfileBuilder fromChannel(Integer channelID) {
        this.channelID = channelID;
        return this;
    }

    public UserProfileBuilder withServiceType(String serviceType){
        this.serviceType = serviceType;
        return this;
    }

    public UserProfileBuilder withServiceCode(String serviceCode){
        this.serviceCode = serviceCode;
        return this;
    }

    public UserProfileBuilder withRefernce1(String reference1){
        this.reference1 = reference1;
        return this;
    }

    public UserProfileBuilder withFavorite(Favorite favorite) {
        this.favorite = favorite;
        return this;
    }
    
    public UserProfileBuilder withPin(String oldPin, String pin) {
    	this.oldPin = oldPin;
    	this.pin = pin;
    	return this;
    }
    
    public UserProfileBuilder withPassword(String oldPassword, String password) {
    	this.oldPassword = oldPassword;
    	this.password = password;
    	return this;
    }
    
	public UserProfileBuilder withFullname(String fullname) {
		this.fullname = fullname;
		return this;
	}
	
	public UserProfileBuilder withImageName(String imageFileName) {
		this.profileImage = imageFileName;
		return this;
	}
	
	public UserProfileBuilder withLoginID(String loginID) {
		this.loginID = loginID;
		return this;
	}
	
    public TmnProfile getProfile() {
        Validate.notNull(channelID, "from which channel");
        Validate.notNull(sessionID, "missing sessionID");
        Validate.notNull(tmnID, "missing TruemoneyID");

        return profileFacade.getProfile(this.channelID, this.sessionID, this.tmnID);
    }

    public BigDecimal getCurrentBalance() {
        Validate.notNull(tmnID, "data missing. get balance of whom?");
        Validate.notNull(sessionID, "data missing. get balance of whom?");
        Validate.notNull(channelID, "data missing. get balance from which channel?");

        return balanceFacade.getCurrentBalance(this.channelID, this.sessionID, this.tmnID);
    }

    public List<DirectDebit> getDirectDebitSourceOfFundList() {
        Validate.notNull(tmnID, "data missing. get direct debit list of whom?");
        Validate.notNull(sessionID, "data missing. get direct debit list of whom?");
        Validate.notNull(channelID, "data missing. get direct debit list from which channel?");

        return sourceOfFundFacade.getAllDirectDebitSourceOfFunds(this.channelID, this.sessionID, this.tmnID);
    }

    public List<Favorite> getListFavorite(){
        Validate.notNull(tmnID, "data missing. get direct debit list of whom?");
        Validate.notNull(sessionID, "data missing. get direct debit list of whom?");
        Validate.notNull(channelID, "data missing. get direct debit list from which channel?");

        return profileFacade.getListFavorite(this.channelID, this.sessionID, this.tmnID, this.serviceType);
    }

    public Boolean isFavoritable(){
        Validate.notNull(tmnID, "data missing. get direct debit list of whom?");
        Validate.notNull(sessionID, "data missing. get direct debit list of whom?");
        Validate.notNull(channelID, "data missing. get direct debit list from which channel?");
        Validate.notNull(serviceCode, "data missing. get serviceCode?");
        Validate.notNull(reference1, "data missing. get reference1?");

        return profileFacade.isFavoritable(this.channelID, this.sessionID, this.tmnID,this.serviceType, this.serviceCode, this.reference1);
    }

    public Boolean isFavorited(){
        Validate.notNull(tmnID, "data missing. get direct debit list of whom?");
        Validate.notNull(sessionID, "data missing. get direct debit list of whom?");
        Validate.notNull(channelID, "data missing. get direct debit list from which channel?");
        Validate.notNull(serviceCode, "data missing. get serviceCode?");
        Validate.notNull(reference1, "data missing. get reference1?");

        return profileFacade.isFavorited(this.channelID, this.sessionID, this.tmnID,this.serviceType, this.serviceCode, this.reference1);
    }

    public Favorite addFavorite(){
        Validate.notNull(tmnID, "data missing. get direct debit list of whom?");
        Validate.notNull(sessionID, "data missing. get direct debit list of whom?");
        Validate.notNull(channelID, "data missing. get direct debit list from which channel?");

        return profileFacade.addFavorite(this.channelID, this.sessionID, this.tmnID, this.favorite);
    }

    public void logout() {
        Validate.notNull(channelID, "from which channel");
        Validate.notNull(sessionID, "missing sessionID");
        Validate.notNull(tmnID, "missing TruemoneyID");

        profileFacade.logout(channelID, sessionID, tmnID);
    }

    public Boolean removeFavorite() {
        Validate.notNull(tmnID, "data missing. get direct debit list of whom?");
        Validate.notNull(sessionID, "data missing. get direct debit list of whom?");
        Validate.notNull(channelID, "data missing. get direct debit list from which channel?");
        Validate.notNull(serviceCode, "data missing. get serviceCode?");
        Validate.notNull(reference1, "data missing. get reference1?");

        return profileFacade.removeFavorite(this.channelID, this.sessionID, this.tmnID, this.serviceCode, this.reference1);
    }

    public void changePin() {
        Validate.notNull(tmnID, "data missing. change PIN of whom?");
        Validate.notNull(sessionID, "data missing. change PIN of whom?");
        Validate.notNull(channelID, "data missing. change PIN from which channel?");
        Validate.notNull(oldPin, "data missing. old PIN ?");
        Validate.notNull(pin, "data missing. pin ?");
        Validate.notNull(loginID, "data missing. loginID ?");

        profileFacade.changePin(this.channelID, this.sessionID, this.tmnID, this.oldPin, this.pin, this.loginID);
    }
    
    public void changePassword() {
        Validate.notNull(tmnID, "data missing. change password of whom?");
        Validate.notNull(sessionID, "data missing. change password of whom?");
        Validate.notNull(channelID, "data missing. change password from which channel?");
        Validate.notNull(oldPassword, "data missing. old password ?");
        Validate.notNull(password, "data missing. password ?");
        Validate.notNull(loginID, "data missing. loginID ?");
        
        profileFacade.changePassword(this.channelID, this.sessionID, this.tmnID, this.oldPassword, this.password, this.loginID);
    }

	public void changeFullName() {
        Validate.notNull(tmnID, "data missing. change PIN of whom?");
        Validate.notNull(sessionID, "data missing. change PIN of whom?");
        Validate.notNull(channelID, "data missing. change PIN from which channel?");
        Validate.notNull(fullname, "data missing. fullname ?");
        
        profileFacade.changeFullname(this.channelID, this.sessionID, this.tmnID, this.fullname);
	}

	public void changeProfileImage() {
		Validate.notNull(tmnID, "data missing. change PIN of whom?");
        Validate.notNull(sessionID, "data missing. change PIN of whom?");
        Validate.notNull(channelID, "data missing. change PIN from which channel?");
        Validate.notNull(profileImage, "data missing. profile image path ?");
		
        profileFacade.changeProfileImage(this.channelID, this.sessionID, this.tmnID, this.profileImage);
	}
    
}
