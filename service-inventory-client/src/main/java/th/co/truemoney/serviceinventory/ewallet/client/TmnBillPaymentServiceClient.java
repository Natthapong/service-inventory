package th.co.truemoney.serviceinventory.ewallet.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import th.co.truemoney.serviceinventory.bill.BillPaymentService;
import th.co.truemoney.serviceinventory.bill.domain.BillPaymentDraft;
import th.co.truemoney.serviceinventory.bill.domain.Bill;
import th.co.truemoney.serviceinventory.bill.domain.BillPaymentTransaction;
import th.co.truemoney.serviceinventory.ewallet.client.config.EndPoints;
import th.co.truemoney.serviceinventory.ewallet.domain.OTP;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;

@Service
public class TmnBillPaymentServiceClient implements BillPaymentService {

	@Autowired
	private RestTemplate restTemplate;

	public void setRestTemplate(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}

	@Autowired
	private EndPoints endPoints;

	@Autowired
	private HttpHeaders headers;

	@Override
	public Bill getBillInformation(String barcode, String accessTokenID)
			throws ServiceInventoryException {
		HttpEntity<String> requestEntity = new HttpEntity<String>(headers);

		ResponseEntity<Bill> responseEntity = restTemplate.exchange(
				endPoints.getScanBarcodeServiceURL(), HttpMethod.GET, requestEntity,
				Bill.class, barcode, accessTokenID);

		Bill billPaymentInfo = responseEntity.getBody();

		return billPaymentInfo;
	}

	@Override
	public BillPaymentDraft createBill(Bill billpayInfo,
			String accessTokenID) throws ServiceInventoryException {

		HttpEntity<Bill> requestEntity = new HttpEntity<Bill>(billpayInfo,headers);

		ResponseEntity<BillPaymentDraft> responseEntity = restTemplate.exchange(
				endPoints.getCreateBillInvoiceURL(), HttpMethod.POST, requestEntity,
				BillPaymentDraft.class, accessTokenID);

		BillPaymentDraft billInvoice = responseEntity.getBody();

		return billInvoice;
	}

	@Override
	public BillPaymentDraft getBillDetail(String invoiceID,
			String accessTokenID) throws ServiceInventoryException {

		HttpEntity<Bill> requestEntity = new HttpEntity<Bill>(headers);

		ResponseEntity<BillPaymentDraft> responseEntity = restTemplate.exchange(
				endPoints.getBillInvoiceDetailURL(), HttpMethod.GET, requestEntity,
				BillPaymentDraft.class,invoiceID, accessTokenID);

		BillPaymentDraft billInvoice = responseEntity.getBody();

		return billInvoice;
	}

	@Override
	public OTP sendOTP(String invoiceID, String accessTokenID)
			throws ServiceInventoryException {

		HttpEntity<Bill> requestEntity = new HttpEntity<Bill>(headers);

		ResponseEntity<OTP> responseEntity = restTemplate.exchange(
				endPoints.getBillPaymentSendOTPConfirmURL(), HttpMethod.POST, requestEntity,
				OTP.class, invoiceID, accessTokenID);

		return responseEntity.getBody();
	}

	@Override
	public BillPaymentDraft.Status confirmBill(String invoiceID, OTP otp,
			String accessTokenID) throws ServiceInventoryException {

		HttpEntity<OTP> requestEntity = new HttpEntity<OTP>(otp, headers);

		ResponseEntity<BillPaymentDraft.Status> responseEntity = restTemplate.exchange(
				endPoints.getBillPayInvoiceOTPConfirmURL(), HttpMethod.PUT,
				requestEntity, BillPaymentDraft.Status.class,
				invoiceID, otp.getReferenceCode(), accessTokenID);

		return responseEntity.getBody();
	}

	@Override
	public BillPaymentTransaction.Status getBillPaymentStatus(
			String billPaymentID, String accessTokenID)
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

}
