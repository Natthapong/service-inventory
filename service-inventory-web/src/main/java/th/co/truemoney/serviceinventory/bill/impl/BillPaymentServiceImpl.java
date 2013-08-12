package th.co.truemoney.serviceinventory.bill.impl;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import th.co.truemoney.serviceinventory.bill.BillPaymentService;
import th.co.truemoney.serviceinventory.bill.domain.Bill;
import th.co.truemoney.serviceinventory.bill.domain.BillPaymentDraft;
import th.co.truemoney.serviceinventory.bill.domain.BillPaymentTransaction;
import th.co.truemoney.serviceinventory.bill.domain.InquiryOutstandingBillType;
import th.co.truemoney.serviceinventory.bill.domain.OutStandingBill;
import th.co.truemoney.serviceinventory.bill.validation.BillValidator;
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

    private static final String PAYWITH_FAVORITE = "favorite";

    private static final String PAYWITH_BARCODE = "barcode";

    private static final String PAYWITH_KEYIN = "keyin";

    @Autowired
    private AsyncBillPayProcessor asyncBillPayProcessor;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private BillInformationRepository billInfoRepo;

    @Autowired
    private BillValidator billValidator;

    @Autowired
    private AccessTokenRepository accessTokenRepo;

    @Autowired
    private LegacyFacade legacyFacade;

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
    public BillPaymentDraft getBillPaymentDraftDetail(String invoiceID, String accessTokenID) 
    		throws ServiceInventoryException {
    	
		AccessToken accessToken = accessTokenRepo.findAccessToken(accessTokenID);
		
		BillPaymentDraft billPaymentDraft = transactionRepository.findDraftTransaction(invoiceID, accessToken.getAccessTokenID(), BillPaymentDraft.class);
        
		return billPaymentDraft;
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
            ServiceInventoryException failCause = billPayment.getFailCause();
            
            if (failCause != null) {
            	throw new ServiceInventoryException(
            			HttpStatus.BAD_REQUEST.value(), 
            			failCause.getErrorCode(), 
            			failCause.getDeveloperMessage(), 
            			failCause.getErrorNamespace());
            }
            throw new ServiceInventoryWebException(Code.CONFIRM_FAILED, "confirmation processing fail.");
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

    @Override
    public Bill retrieveBillInformationWithBarcode(String barcode, String accessTokenID)
            throws ServiceInventoryException {

        AccessToken token = accessTokenRepo.findAccessToken(accessTokenID);
        ClientCredential appData = token.getClientCredential();


        Bill bill = legacyFacade.billing()
                                .readBillInfoWithBarcode(barcode)
                                .fromApp(appData.getAppUser(), appData.getAppPassword(), appData.getAppKey())
                                .fromBillChannel(appData.getChannel(), appData.getChannelDetail())
                                .read();

        billValidator.validateOverDue(bill);
        billValidator.validateDebtStatus(bill);
        bill.setID(UUID.randomUUID().toString());
        bill.setPayWith(PAYWITH_BARCODE);
        billInfoRepo.saveBill(bill, accessTokenID);

        return bill;
    }

    @Override
    public Bill retrieveBillInformationWithUserFavorite(String billCode,
            String ref1, String ref2, BigDecimal amount, InquiryOutstandingBillType inquiryType,
            String accessTokenID)
            throws ServiceInventoryException {
    	
        Bill billInfo = this.retrieveBillInformationWithFavorite(billCode, ref1, ref2, amount, accessTokenID);

        if (inquiryType == InquiryOutstandingBillType.ONLINE) {
            OutStandingBill outstanding = retrieveBillOutStandingOnline(billCode, ref1, ref2, accessTokenID);

            String newRef1 = StringUtils.hasText(outstanding.getRef1()) ? outstanding.getRef1() : ref1;
            String newRef2 = StringUtils.hasText(outstanding.getRef2()) ? outstanding.getRef2() : ref2;

            billInfo.setRef1(newRef1);
            billInfo.setRef2(newRef2);
            billInfo.setAmount(outstanding.getOutStandingBalance());
            billInfo.setDueDate(outstanding.getDueDate());

        }
        
        billValidator.validateOverDue(billInfo);
        
        billInfo.setPayWith(PAYWITH_FAVORITE);
        billInfoRepo.saveBill(billInfo, accessTokenID);

        return billInfo;
    }

    @Override
    public Bill retrieveBillInformationWithKeyin(String billCode,
            String ref1, String ref2, BigDecimal amount, InquiryOutstandingBillType inquiryType,
            String accessTokenID)
            throws ServiceInventoryException {
    	
    	Bill billInfo = null;
    	
        if (inquiryType == InquiryOutstandingBillType.ONLINE) {
        	OutStandingBill outstanding = this.retrieveBillOutStandingOnline(billCode, ref1, ref2, accessTokenID);
        	
        	String outstandingBillCode = outstanding.getBillCode();
            billInfo = this.retrieveBillInformationWithKeyin(outstandingBillCode, ref1, ref2, amount, accessTokenID);

            String newRef1 = StringUtils.hasText(outstanding.getRef1()) ? outstanding.getRef1() : ref1;
            String newRef2 = StringUtils.hasText(outstanding.getRef2()) ? outstanding.getRef2() : ref2;
            
            billInfo.setTarget(outstandingBillCode);
            billInfo.setRef1(newRef1);
            billInfo.setRef2(newRef2);
            billInfo.setAmount(outstanding.getOutStandingBalance());
            billInfo.setDueDate(outstanding.getDueDate());
        } else {
        	billInfo = this.retrieveBillInformationWithKeyin(billCode, ref1, ref2, amount, accessTokenID);
        }
        
        billValidator.validateOverDue(billInfo);
        
        billInfo.setPayWith(PAYWITH_KEYIN);
        billInfoRepo.saveBill(billInfo, accessTokenID);

        return billInfo;
    }
    
    private ClientCredential getClientCredential(String accessTokenID) {
        AccessToken token = accessTokenRepo.findAccessToken(accessTokenID);
        return token.getClientCredential();
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
                                 .fromOperateByStaff(token.getMobileNumber())
                                 .fromApp(appData.getAppUser(), appData.getAppPassword(), appData.getAppKey())
                                 .fromBillChannel(appData.getChannel(), appData.getChannelDetail())
                                 .getBillOutStandingOnline();

        return outStandingBill;
    }
    
    private Bill retrieveBillInformationWithBillCode(String billCode, 
    		String ref1, String ref2, BigDecimal amount, String accessTokenID) throws ServiceInventoryException {

    	ClientCredential appData = getClientCredential(accessTokenID);

        Bill bill =  legacyFacade.billing().readBillInfoWithBillCode(billCode)
                                 .fromBillChannel(appData.getChannel(), appData.getChannelDetail())
                                 .fromApp(appData.getAppUser(), appData.getAppPassword(), appData.getAppKey())
                                 .read();
        String generatedBillID = UUID.randomUUID().toString();
        bill.setID(generatedBillID);
        bill.setAmount(amount);
        return bill;
    }

    private Bill retrieveBillInformationWithKeyin(String billCode, 
    		String ref1, String ref2, BigDecimal amount, String accessTokenID) throws ServiceInventoryException {
    	
    	Bill bill = retrieveBillInformationWithBillCode(billCode, ref1, ref2, amount, accessTokenID);
    	String newRef1 = StringUtils.hasText(bill.getRef1()) ? bill.getRef1() : ref1;
        String newRef2 = StringUtils.hasText(bill.getRef2()) ? bill.getRef2() : ref2;
        bill.setRef1(newRef1);
        bill.setRef2(newRef2);
    	return bill;
    }

    private Bill retrieveBillInformationWithFavorite(String billCode,
            String ref1, String ref2, BigDecimal amount, String accessTokenID) throws ServiceInventoryException {
    	
    	Bill bill = retrieveBillInformationWithBillCode(billCode, ref1, ref2, amount, accessTokenID);
    	bill.setRef1(ref1);
    	bill.setRef2(ref2);
    	return bill;
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

    private Boolean isFavorited(AccessToken accessToken, Bill bill) {
        return legacyFacade.userProfile(accessToken.getSessionID(), accessToken.getTruemoneyID())
        .fromChannel(accessToken.getChannelID())
        .withServiceCode(bill.getTarget())
        .withRefernce1(bill.getRef1())
        .isFavorited();
    }

    public void setAccessTokenRepo(AccessTokenRepository accessTokenRepo) {
        this.accessTokenRepo = accessTokenRepo;
    }

    public void setTransactionRepository(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public void setLegacyFacade(LegacyFacade legacyFacade) {
        this.legacyFacade = legacyFacade;
    }

}
