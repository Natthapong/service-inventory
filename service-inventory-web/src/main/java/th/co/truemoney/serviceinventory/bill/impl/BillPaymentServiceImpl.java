package th.co.truemoney.serviceinventory.bill.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import th.co.truemoney.serviceinventory.bill.BillPaymentService;
import th.co.truemoney.serviceinventory.bill.domain.Bill;
import th.co.truemoney.serviceinventory.bill.domain.BillPaymentDraft;
import th.co.truemoney.serviceinventory.bill.domain.BillPaymentTransaction;
import th.co.truemoney.serviceinventory.bill.domain.BillPaymentTransaction.FailStatus;
import th.co.truemoney.serviceinventory.bill.domain.OutStandingBill;
import th.co.truemoney.serviceinventory.ewallet.domain.AccessToken;
import th.co.truemoney.serviceinventory.ewallet.domain.ClientCredential;
import th.co.truemoney.serviceinventory.ewallet.domain.DraftTransaction.Status;
import th.co.truemoney.serviceinventory.ewallet.domain.Transaction;
import th.co.truemoney.serviceinventory.ewallet.repositories.AccessTokenRepository;
import th.co.truemoney.serviceinventory.ewallet.repositories.BillInformationRepository;
import th.co.truemoney.serviceinventory.ewallet.repositories.TransactionRepository;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryWebException;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryWebException.Code;
import th.co.truemoney.serviceinventory.exception.UnVerifiedFavoritePaymentException;
import th.co.truemoney.serviceinventory.exception.UnVerifiedOwnerTransactionException;
import th.co.truemoney.serviceinventory.legacyfacade.LegacyFacade;

@Service
public class BillPaymentServiceImpl implements  BillPaymentService {
	
	private static final Logger logger = LoggerFactory.getLogger(BillPaymentServiceImpl.class);

    private static final String PAYWITH_FAVORITE = "favorite";

	private static final String PAYWITH_BARCODE = "barcode";
	
	private static final String PAYWITH_KEYIN = "keyin";

	@Autowired
    private AccessTokenRepository accessTokenRepo;

    @Autowired
    private BillInformationRepository billInfoRepo;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private LegacyFacade legacyFacade;

    @Autowired
    private AsyncBillPayProcessor asyncBillPayProcessor;

    @Autowired
    private BillPaymentValidationConfig validationConfig;

    public void setAccessTokenRepo(AccessTokenRepository accessTokenRepo) {
        this.accessTokenRepo = accessTokenRepo;
    }

    public void setTransactionRepository(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public void setLegacyFacade(LegacyFacade legacyFacade) {
        this.legacyFacade = legacyFacade;
    }

    @Override
    public Bill retrieveBillInformationWithBarcode(String barcode, String accessTokenID)
            throws ServiceInventoryException {

        AccessToken token = accessTokenRepo.findAccessToken(accessTokenID);
        ClientCredential appData = token.getClientCredential();


        Bill bill = legacyFacade.billing()
                                .readBillInfoWithBarcode(barcode)
                                .fromApp(appData.getAppUser(), appData.getAppPassword(), appData.getAppKey())
                                .fromBillChannel(appData.getChannel(), appData.getChannelDetail())
                                .getInformationWithBarcode();

		validateOverdue(bill.getTarget(), bill.getDueDate(), bill.getAmount());
		bill.setID(UUID.randomUUID().toString());
		bill.setPayWith(PAYWITH_BARCODE);
		billInfoRepo.saveBill(bill, accessTokenID);

		return bill;

    }

	@Override
	public Bill retrieveBillInformationWithBillCode(String billCode,
			String ref1, BigDecimal amount, String accessTokenID) throws ServiceInventoryException {
		AccessToken token = accessTokenRepo.findAccessToken(accessTokenID);
		ClientCredential appData = token.getClientCredential();

		Bill bill =  legacyFacade.billing()
								 .readBillInfoWithBillCode(billCode)
								 .fromApp(appData.getAppUser(), appData.getAppPassword(), appData.getAppKey())
								 .fromBillChannel(appData.getChannel(), appData.getChannelDetail())
								 .getInformationWithBillCode();

		validateOverdue(bill.getTarget(), bill.getDueDate(), bill.getAmount());
		bill.setID(UUID.randomUUID().toString());
		bill.setPayWith(PAYWITH_FAVORITE);
		bill.setRef1(ref1);
		bill.setAmount(amount);
		billInfoRepo.saveBill(bill, accessTokenID);

		return bill;
	}

    private void validateOverdue(String billerCode, Date duedate, BigDecimal amount) {
        BillPaymentValidation billPaymentValidation = validationConfig.getBillValidation(billerCode);
        if (billPaymentValidation != null && "TRUE".equals(billPaymentValidation.getValidateDuedate())) {
            if (isOverdue(duedate)) {
        		HashMap<String, Object> mapData = new HashMap<String, Object>();
        		mapData.put("dueDate", duedate);
        		mapData.put("amount", amount);
        		mapData.put("target", billerCode);
        		
        		ServiceInventoryWebException se = new ServiceInventoryWebException(Code.BILL_OVER_DUE, "bill over due date.");
        		se.setData(mapData);
        		throw se;
            }
        }
    }
    
    private Boolean isFavorited(AccessToken accessToken, Bill bill) {
	    	return legacyFacade.userProfile(accessToken.getSessionID(), accessToken.getTruemoneyID())
	    	.fromChannel(accessToken.getChannelID())
	    	.withServiceCode(bill.getTarget())
	    	.withRefernce1(bill.getRef1())
	    	.isFavorited();
    }

    public boolean isOverdue(Date duedate) {
        DateTime dateTime = new DateTime(duedate);
        return dateTime.plusDays(1).isBeforeNow();
    }

    @Override
    public BillPaymentDraft verifyPaymentAbility(String billID, BigDecimal amount, String accessTokenID)
            throws ServiceInventoryException {

		Bill billInfo = billInfoRepo.findBill(billID, accessTokenID);

		validateMinMaxAmount(amount, billInfo.getMinAmount(), billInfo.getMaxAmount());
		
		AccessToken accessToken = accessTokenRepo.findAccessToken(accessTokenID);
		ClientCredential appData = accessToken.getClientCredential();

        BigDecimal sofFee = billInfo.getEwalletSourceOfFund() != null ? billInfo.getEwalletSourceOfFund().calculateFee(amount) : BigDecimal.ZERO;

        //verify bill.
        String verificationID = legacyFacade.billing()
                        .fromBill(billInfo.getRef1(), billInfo.getRef2(), billInfo.getTarget())
                            .aUser(accessToken.getSessionID(), accessToken.getTruemoneyID())
                            .usingMobilePayPoint(accessToken.getMobileNumber())
                            .fromApp(appData.getAppUser(), appData.getAppPassword(), appData.getAppKey())
                            .fromBillChannel(appData.getChannel(), appData.getChannelDetail())
                            .paying(amount, billInfo.getServiceFee().calculateFee(amount), sofFee)
                            .verifyPayment();

        BillPaymentDraft.Status billStatus = PAYWITH_FAVORITE.equals(billInfo.getPayWith()) ? Status.OTP_CONFIRMED : Status.CREATED;

        BillPaymentDraft billDraft = new BillPaymentDraft(UUID.randomUUID().toString(), billInfo, amount, verificationID, billStatus);
        transactionRepository.saveDraftTransaction(billDraft, accessTokenID);
        return billDraft;
    }

    @Override
    public BillPaymentDraft getBillPaymentDraftDetail(String invoiceID, String accessTokenID) throws ServiceInventoryException {
        return transactionRepository.findDraftTransaction(invoiceID, accessTokenID, BillPaymentDraft.class);
    }

    @Override
    public BillPaymentTransaction.Status performPayment(String invoiceID, String accessTokenID)
            throws ServiceInventoryException {

        AccessToken accessToken = accessTokenRepo.findAccessToken(accessTokenID);
        BillPaymentDraft invoiceDetails = getBillPaymentDraftDetail(invoiceID, accessTokenID);

        if (BillPaymentDraft.Status.OTP_CONFIRMED != invoiceDetails.getStatus()) {
            throw new UnVerifiedOwnerTransactionException();
        }
        
        Bill bill = invoiceDetails.getBillInfo();
        
        if(PAYWITH_FAVORITE.equals(bill.getPayWith()) &&
        		!isFavorited(accessToken, bill)) {
        	throw new UnVerifiedFavoritePaymentException();        	
        }
        
        BillPaymentTransaction billPaymentReceipt = new BillPaymentTransaction(invoiceDetails);
        billPaymentReceipt.setStatus(BillPaymentTransaction.Status.VERIFIED);
        transactionRepository.saveTransaction(billPaymentReceipt, accessTokenID);

        performBillPay(billPaymentReceipt, accessToken);

        return billPaymentReceipt.getStatus();
    }

    private void performBillPay(BillPaymentTransaction billPaymentReceipt, AccessToken accessToken) {
        asyncBillPayProcessor.payBill(billPaymentReceipt, accessToken);
    }

    @Override
    public BillPaymentTransaction.Status getBillPaymentStatus(String billPaymentID, String accessTokenID)
            throws ServiceInventoryException {
        BillPaymentTransaction billPayment = getBillPaymentResult(billPaymentID, accessTokenID);

        if (Transaction.Status.FAILED == billPayment.getStatus()) {
        	FailStatus failSts = billPayment.getFailStatus();
        	if (FailStatus.PCS_FAILED == failSts) {
        		throw new ServiceInventoryWebException(Code.CONFIRM_PCS_FAILED, "pcs confirmation processing fail.");
        	} else if (FailStatus.TPP_FAILED == failSts) {
        		throw new ServiceInventoryWebException(Code.CONFIRM_TPP_FAILED, "tpp confirmation processing fail.");
        	} else if (FailStatus.UMARKET_FAILED == failSts) {
        		throw new ServiceInventoryWebException(Code.CONFIRM_UMARKET_FAILED, "u-market confirmation processing fail.");
        	} else { //UNKNOWN FAIL
        		throw new ServiceInventoryWebException(Code.CONFIRM_FAILED, "confirmation processing fail.");
        	}
        }
        return billPayment.getStatus();
    }

    @Override
    public BillPaymentTransaction getBillPaymentResult(String billPaymentID, String accessTokenID)
            throws ServiceInventoryException {
    	
        AccessToken accessToken = accessTokenRepo.findAccessToken(accessTokenID);
    	
        BillPaymentTransaction billPaymentTransaction = 
        		transactionRepository.findTransaction(billPaymentID, accessTokenID, BillPaymentTransaction.class);
        
        Bill billInfo = billPaymentTransaction.getDraftTransaction().getBillInfo();
        
		
		Boolean isFavoritable = isAddFavoritable(accessToken, billInfo.getTarget(), billInfo.getRef1());
		Boolean isFavorited = isFavorited(accessToken, billInfo);
		
		billPaymentTransaction.getDraftTransaction().getBillInfo().setFavoritable(isFavoritable);
		billPaymentTransaction.getDraftTransaction().getBillInfo().setFavorited(isFavorited);			
		      
        
        return billPaymentTransaction;
    }

	private Boolean isAddFavoritable(AccessToken accessToken,String serviceCode,String ref1){
		return legacyFacade.userProfile(accessToken.getSessionID(), accessToken.getTruemoneyID())
				.fromChannel(accessToken.getChannelID())
				.withServiceCode(serviceCode)
				.withRefernce1(ref1)
				.isFavoritable();
	}
	
    public void setAsyncBillPayProcessor(AsyncBillPayProcessor asyncBillPayProcessor) {
        this.asyncBillPayProcessor = asyncBillPayProcessor;
    }
    
	private void validateMinMaxAmount(BigDecimal amount, BigDecimal minAmount, BigDecimal maxAmount) {
		HashMap<String, BigDecimal> mapData = new HashMap<String, BigDecimal>();
		mapData.put("minAmount", minAmount);
		mapData.put("maxAmount", maxAmount);
		if (minAmount != null && amount.compareTo(minAmount) < 0) {
			ServiceInventoryWebException se = new ServiceInventoryWebException(Code.INVALID_BILL_PAYMENT_AMOUNT, "amount less than min amount.");
			se.marshallToData(mapData);
			throw se;
		}
		if (maxAmount != null && amount.compareTo(maxAmount) > 0) {
			ServiceInventoryWebException se = new ServiceInventoryWebException(Code.INVALID_BILL_PAYMENT_AMOUNT, "amount more than max amount.");
			se.marshallToData(mapData);
			throw se;
		}
	}

	@Override
	public Bill retrieveBillInformationWithKeyin(String billCode, String accessTokenID)
			throws ServiceInventoryException {
		AccessToken token = accessTokenRepo.findAccessToken(accessTokenID);
		ClientCredential appData = token.getClientCredential();

		Bill bill =  legacyFacade.billing()
								 .readBillInfoWithBillCode(billCode)
								 .fromApp(appData.getAppUser(), appData.getAppPassword(), appData.getAppKey())
								 .fromBillChannel(appData.getChannel(), appData.getChannelDetail())
								 .getInformationWithBillCode();

		validateOverdue(bill.getTarget(), bill.getDueDate(), bill.getAmount());
		bill.setID(UUID.randomUUID().toString());
		bill.setPayWith(PAYWITH_KEYIN);
		billInfoRepo.saveBill(bill, accessTokenID);

		return bill;
	}

	@Override
	public Bill updateBillInformation(String billID, String ref1, String ref2, BigDecimal amount,
			String accessTokenID) throws ServiceInventoryException {
		
		logger.debug("billID : "+billID);
		logger.debug("ref1 : "+ref1);
		logger.debug("ref2 : "+ref2);
		logger.debug("amount : "+amount);
		logger.debug("accessTokenID : "+accessTokenID);
		
		AccessToken token = accessTokenRepo.findAccessToken(accessTokenID);
		
		logger.debug("token.getAccessTokenID() : "+token.getAccessTokenID());
		
		Bill bill = billInfoRepo.findBill(billID, token.getAccessTokenID());
		
		logger.debug("bill target : "+bill.getTarget());
		
		bill.setRef1(ref1);
		bill.setRef2(ref2);
		bill.setAmount(amount);
		
		billInfoRepo.saveBill(bill, accessTokenID);
		
		return bill;
	}

	@Override
	public OutStandingBill retrieveBillOutStandingOnline(String billCode,
			String ref1, String ref2, String accessTokenID)
			throws ServiceInventoryException {
		AccessToken token = accessTokenRepo.findAccessToken(accessTokenID);
		ClientCredential appData = token.getClientCredential();

		OutStandingBill outStandingBill =  legacyFacade.billing()
								 .readBillOutStandingOnlineWithBillCode(billCode)
								 .fromRef1(ref1)
								 .fromApp(appData.getAppUser(), appData.getAppPassword(), appData.getAppKey())
								 .fromBillChannel(appData.getChannel(), appData.getChannelDetail())
								 .getBillOutStandingOnline();
		
		validateOverdue(outStandingBill.getBillCode(), outStandingBill.getDueDate(), outStandingBill.getOutStandingBalance());

		return outStandingBill;
	}

}
