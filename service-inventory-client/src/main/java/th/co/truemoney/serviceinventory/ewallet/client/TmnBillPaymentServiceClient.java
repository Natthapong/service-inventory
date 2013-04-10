package th.co.truemoney.serviceinventory.ewallet.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import th.co.truemoney.serviceinventory.bill.BillPaymentService;
import th.co.truemoney.serviceinventory.bill.domain.BillInvoice;
import th.co.truemoney.serviceinventory.bill.domain.BillPayment;
import th.co.truemoney.serviceinventory.bill.domain.BillPaymentInfo;
import th.co.truemoney.serviceinventory.ewallet.client.config.EndPoints;
import th.co.truemoney.serviceinventory.ewallet.domain.DraftTransaction.Status;
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
	public BillPaymentInfo getBillInformation(String barcode,
			String accessTokenID) throws ServiceInventoryException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BillInvoice createBillInvoice(BillPaymentInfo billpayInfo,
			String accessTokenID) throws ServiceInventoryException {

		HttpEntity<BillPaymentInfo> requestEntity = new HttpEntity<BillPaymentInfo>(billpayInfo,headers);

		ResponseEntity<BillInvoice> responseEntity = restTemplate.exchange(
				endPoints.getCreateBillInvoiceUrl(), HttpMethod.POST, requestEntity,
				BillInvoice.class, accessTokenID);

		BillInvoice billInvoice = responseEntity.getBody();

		return billInvoice;
	}

	@Override
	public BillInvoice getBillInvoiceDetail(String invoiceID,
			String accessTokenID) throws ServiceInventoryException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public OTP sendOTP(String invoiceID, String accessTokenID)
			throws ServiceInventoryException {
		return new OTP("0861234567", "abcd", "******");
	}

	@Override
	public Status confirmBillInvoice(String invoiceID, OTP otp,
			String accessTokenID) throws ServiceInventoryException {

		HttpEntity<OTP> requestEntity = new HttpEntity<OTP>(otp, headers);

		ResponseEntity<Status> responseEntity = restTemplate.exchange(
				endPoints.getBillPayInvoiceOTPConfirmURL(), HttpMethod.PUT,
				requestEntity, Status.class,
				invoiceID, otp.getReferenceCode(), accessTokenID);

		return responseEntity.getBody();
	}

	@Override
	public th.co.truemoney.serviceinventory.ewallet.domain.Transaction.Status getBillPaymentStatus(
			String billPaymentID, String accessTokenID)
			throws ServiceInventoryException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BillPayment getBillPaymentResult(String billPaymentID,
			String accessTokenID) throws ServiceInventoryException {
		// TODO Auto-generated method stub
		return null;
	}

}
