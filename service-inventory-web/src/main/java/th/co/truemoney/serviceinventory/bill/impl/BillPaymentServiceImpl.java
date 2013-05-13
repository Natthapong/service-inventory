package th.co.truemoney.serviceinventory.bill.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import th.co.truemoney.serviceinventory.bill.BillPaymentService;
import th.co.truemoney.serviceinventory.bill.domain.Bill;
import th.co.truemoney.serviceinventory.bill.domain.BillPaymentDraft;
import th.co.truemoney.serviceinventory.bill.domain.BillPaymentTransaction;
import th.co.truemoney.serviceinventory.bill.domain.BillPaymentTransaction.FailStatus;
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
import th.co.truemoney.serviceinventory.exception.UnVerifiedOwnerTransactionException;
import th.co.truemoney.serviceinventory.legacyfacade.facade.LegacyFacade;

@Service
public class BillPaymentServiceImpl implements  BillPaymentService {

    @Autowired
    private AccessTokenRepository accessTokenRepo;

    @Autowired
    private BillInformationRepository billInfoRepo;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private LegacyFacade legacyFacade;

    @Autowired
    AsyncBillPayProcessor asyncBillPayProcessor;

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

		validateOverdue(bill.getTarget(), bill.getDueDate());
		bill.setID(UUID.randomUUID().toString());
		bill.setPayWith("barcode");
		billInfoRepo.saveBill(bill, accessTokenID);
		
		return bill;

    }
    
	@Override
	public Bill retrieveBillInformationWithBillCode(String billCode, String ref1, BigDecimal amount, String accessTokenID) 
			throws ServiceInventoryException {
		AccessToken token = accessTokenRepo.findAccessToken(accessTokenID);
		ClientCredential appData = token.getClientCredential();
		
		Bill bill =  legacyFacade.billing()
								 .readBillInfoWithBillCode(billCode)
								 .fromApp(appData.getAppUser(), appData.getAppPassword(), appData.getAppKey())
								 .fromBillChannel(appData.getChannel(), appData.getChannelDetail())
								 .getInformationWithBillCode();
		
		validateOverdue(bill.getTarget(), bill.getDueDate());
		bill.setID(UUID.randomUUID().toString());
		bill.setPayWith("favorite");
		bill.setRef1(ref1);
		bill.setAmount(amount);
		billInfoRepo.saveBill(bill, accessTokenID);
		
		return bill;
	}

    private void validateOverdue(String billerCode, Date duedate) {
        BillPaymentValidation billPaymentValidation = validationConfig.getBillValidation(billerCode);
        if (billPaymentValidation != null && "TRUE".equals(billPaymentValidation.getValidateDuedate())) {
            if (isOverdue(duedate)) {
                throw new ServiceInventoryWebException(Code.BILL_OVER_DUE, "bill over due date.");
            }
        }
    }

    public boolean isOverdue(Date duedate) {
        DateTime dateTime = new DateTime(duedate);
        return dateTime.plusDays(1).isBeforeNow();
    }

    @Override
    public BillPaymentDraft verifyPaymentAbility(String billID, BigDecimal amount, String accessTokenID)
            throws ServiceInventoryException {

		Bill billInfo = billInfoRepo.findBill(billID, accessTokenID);
		
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

        BillPaymentDraft billDraft = new BillPaymentDraft(UUID.randomUUID().toString(), billInfo, amount, verificationID, Status.CREATED);
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
        return transactionRepository.findTransaction(billPaymentID, accessTokenID, BillPaymentTransaction.class);
    }

    public void setAsyncBillPayProcessor(AsyncBillPayProcessor asyncBillPayProcessor) {
        this.asyncBillPayProcessor = asyncBillPayProcessor;
    }

}
