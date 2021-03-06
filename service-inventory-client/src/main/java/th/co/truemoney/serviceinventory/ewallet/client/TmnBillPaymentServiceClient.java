package th.co.truemoney.serviceinventory.ewallet.client;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import th.co.truemoney.serviceinventory.bill.BillPaymentService;
import th.co.truemoney.serviceinventory.bill.domain.Bill;
import th.co.truemoney.serviceinventory.bill.domain.BillPaymentDraft;
import th.co.truemoney.serviceinventory.bill.domain.BillPaymentTransaction;
import th.co.truemoney.serviceinventory.bill.domain.InquiryOutstandingBillType;
import th.co.truemoney.serviceinventory.bill.domain.OutStandingBill;
import th.co.truemoney.serviceinventory.ewallet.client.config.EndPoints;
import th.co.truemoney.serviceinventory.ewallet.domain.Transaction.Status;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;

@Service
public class TmnBillPaymentServiceClient implements BillPaymentService {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private EndPoints endPoints;

    @Autowired
    private HttpHeaders headers;

    @Override
    public Bill retrieveBillInformationWithBarcode(String barcode, String accessTokenID)
            throws ServiceInventoryException {
        HttpEntity<String> requestEntity = new HttpEntity<String>(headers);

        ResponseEntity<Bill> responseEntity = restTemplate.exchange(
                endPoints.getScanBarcodeServiceURL(), HttpMethod.GET, requestEntity,
                Bill.class, barcode, accessTokenID);

        return responseEntity.getBody();
    }

    @Override
    public BillPaymentDraft verifyPaymentAbility(String billID, BigDecimal amount, String accessTokenID) throws ServiceInventoryException {

        BillPaymentDraft draft = new BillPaymentDraft(billID, null, amount);
        HttpEntity<BillPaymentDraft> requestEntity = new HttpEntity<BillPaymentDraft>(draft, headers);

        ResponseEntity<BillPaymentDraft> responseEntity = restTemplate.exchange(
                endPoints.getCreateBillInvoiceURL(), HttpMethod.POST, requestEntity,
                BillPaymentDraft.class, billID, accessTokenID);

        BillPaymentDraft billInvoice = responseEntity.getBody();

        return billInvoice;
    }

    @Override
    public BillPaymentDraft getBillPaymentDraftDetail(String invoiceID,
            String accessTokenID) throws ServiceInventoryException {

        HttpEntity<Bill> requestEntity = new HttpEntity<Bill>(headers);

        ResponseEntity<BillPaymentDraft> responseEntity = restTemplate.exchange(
                endPoints.getBillInvoiceDetailURL(), HttpMethod.GET, requestEntity,
                BillPaymentDraft.class,invoiceID, accessTokenID);

        BillPaymentDraft billInvoice = responseEntity.getBody();

        return billInvoice;
    }

    @Override
    public Status performPayment(String invoiceID, String accessTokenID)
            throws ServiceInventoryException {

        HttpEntity<String> requestEntity = new HttpEntity<String>(headers);

        ResponseEntity<BillPaymentTransaction.Status> responseEntity = restTemplate.exchange(
                endPoints.getBillPaymentPerformURL(), HttpMethod.PUT,
                requestEntity, BillPaymentTransaction.Status.class,
                invoiceID, accessTokenID);

        return responseEntity.getBody();

    }

    @Override
    public BillPaymentTransaction.Status getBillPaymentStatus(String billPaymentID, String accessTokenID)
            throws ServiceInventoryException {

        HttpEntity<BillPaymentTransaction.Status> requestEntity = new HttpEntity<BillPaymentTransaction.Status>(headers);

        ResponseEntity<BillPaymentTransaction.Status> responseEntity = restTemplate.exchange(
                endPoints.getBillPaymentStatusURL(), HttpMethod.GET,
                requestEntity, BillPaymentTransaction.Status.class,
                billPaymentID , accessTokenID);

        return responseEntity.getBody();
    }

    @Override
    public BillPaymentTransaction getBillPaymentResult(String billPaymentID,
            String accessTokenID) throws ServiceInventoryException {

        HttpEntity<BillPaymentTransaction> requestEntity = new HttpEntity<BillPaymentTransaction>(headers);

        ResponseEntity<BillPaymentTransaction> responseEntity = restTemplate.exchange(
                endPoints.getBillPaymentInfoURL(), HttpMethod.GET,
                requestEntity, BillPaymentTransaction.class, billPaymentID, accessTokenID);

        return responseEntity.getBody();
    }

    @Override
    public Bill retrieveBillInformationWithKeyin(String billCode,
            String ref1, String ref2, BigDecimal amount, InquiryOutstandingBillType inquiryType,
            String accessTokenID)
            throws ServiceInventoryException {
        HttpEntity<String> requestEntity = new HttpEntity<String>(headers);

        String inquiry = (InquiryOutstandingBillType.ONLINE == inquiryType) ? "online" : "offline";
        String isFavorite = "false";

        ResponseEntity<Bill> responseEntity = restTemplate.exchange(
                endPoints.getKeyInBillURL(), HttpMethod.GET, requestEntity,
                Bill.class, billCode, ref1, ref2, amount, inquiry, isFavorite, accessTokenID);

        return responseEntity.getBody();
    }

    @Override
    public OutStandingBill retrieveBillOutStandingOnline(String billCode,
            String ref1, String ref2, String accessTokenID)
            throws ServiceInventoryException {
        HttpEntity<String> requestEntity = new HttpEntity<String>(headers);

        ResponseEntity<OutStandingBill> responseEntity = restTemplate.exchange(
                endPoints.getBillOutStandingOnlineURL(), HttpMethod.GET, requestEntity,
                OutStandingBill.class, billCode, ref1, ref2, accessTokenID);

        return responseEntity.getBody();
    }

    public void setEndPoints(EndPoints endPoints) {
        this.endPoints = endPoints;
    }

    public void setRestTemplate(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public void setHeaders(HttpHeaders headers) {
        this.headers = headers;
    }

    @Override
    public Bill retrieveBillInformationWithUserFavorite(String billCode,
            String ref1, String ref2, BigDecimal amount, InquiryOutstandingBillType inquiryType,
            String accessTokenID) throws ServiceInventoryException {

        HttpEntity<String> requestEntity = new HttpEntity<String>(headers);

        String inquiry = InquiryOutstandingBillType.ONLINE == inquiryType ? "online": "offline";
        String isFavorite = "true";

        ResponseEntity<Bill> responseEntity = restTemplate.exchange(
                endPoints.getKeyInBillURL(), HttpMethod.GET, requestEntity,
                Bill.class, billCode, ref1, ref2, amount, inquiry, isFavorite, accessTokenID);

        return responseEntity.getBody();

    }

}
