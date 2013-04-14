package th.co.truemoney.serviceinventory.ewallet.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import th.co.truemoney.serviceinventory.bill.BillPaymentService;
import th.co.truemoney.serviceinventory.bill.domain.Bill;
import th.co.truemoney.serviceinventory.bill.domain.BillInfo;
import th.co.truemoney.serviceinventory.bill.domain.BillPayment;
import th.co.truemoney.serviceinventory.ewallet.client.config.EndPoints;
import th.co.truemoney.serviceinventory.ewallet.domain.OTP;
import th.co.truemoney.serviceinventory.ewallet.domain.TopUpOrder;
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
	public BillInfo getBillInformation(String barcode, String accessTokenID)
			throws ServiceInventoryException {
		HttpEntity<String> requestEntity = new HttpEntity<String>(headers);

		ResponseEntity<BillInfo> responseEntity = restTemplate.exchange(
				endPoints.getScanBarcodeServiceURL(), HttpMethod.GET, requestEntity,
				BillInfo.class, barcode, accessTokenID);

		BillInfo billPaymentInfo = responseEntity.getBody();

		return billPaymentInfo;
	}

	@Override
	public Bill createBill(BillInfo billpayInfo,
			String accessTokenID) throws ServiceInventoryException {

		HttpEntity<BillInfo> requestEntity = new HttpEntity<BillInfo>(billpayInfo,headers);

		ResponseEntity<Bill> responseEntity = restTemplate.exchange(
				endPoints.getCreateBillInvoiceURL(), HttpMethod.POST, requestEntity,
				Bill.class, accessTokenID);

		Bill billInvoice = responseEntity.getBody();

		return billInvoice;
	}

	@Override
	public Bill getBillDetail(String invoiceID,
			String accessTokenID) throws ServiceInventoryException {

		HttpEntity<BillInfo> requestEntity = new HttpEntity<BillInfo>(headers);

		ResponseEntity<Bill> responseEntity = restTemplate.exchange(
				endPoints.getBillInvoiceDetailURL(), HttpMethod.GET, requestEntity,
				Bill.class,invoiceID, accessTokenID);

		Bill billInvoice = responseEntity.getBody();

		return billInvoice;
	}

	@Override
	public OTP sendOTP(String invoiceID, String accessTokenID)
			throws ServiceInventoryException {

		HttpEntity<BillInfo> requestEntity = new HttpEntity<BillInfo>(headers);

		ResponseEntity<OTP> responseEntity = restTemplate.exchange(
				endPoints.getBillPaymentSendOTPConfirmURL(), HttpMethod.POST, requestEntity,
				OTP.class, invoiceID, accessTokenID);

		return responseEntity.getBody();
	}

	@Override
	public Bill.Status confirmBill(String invoiceID, OTP otp,
			String accessTokenID) throws ServiceInventoryException {

		HttpEntity<OTP> requestEntity = new HttpEntity<OTP>(otp, headers);

		ResponseEntity<Bill.Status> responseEntity = restTemplate.exchange(
				endPoints.getBillPayInvoiceOTPConfirmURL(), HttpMethod.PUT,
				requestEntity, Bill.Status.class,
				invoiceID, otp.getReferenceCode(), accessTokenID);

		return responseEntity.getBody();
	}

	@Override
	public BillPayment.Status getBillPaymentStatus(
			String billPaymentID, String accessTokenID)
			throws ServiceInventoryException {

		HttpEntity<BillPayment.Status> requestEntity = new HttpEntity<BillPayment.Status>(headers);

		ResponseEntity<BillPayment.Status> responseEntity = restTemplate.exchange(
				endPoints.getBillPaymentStatusURL(), HttpMethod.GET,
				requestEntity, BillPayment.Status.class,
				billPaymentID , accessTokenID);

		return responseEntity.getBody();
	}

	@Override
	public BillPayment getBillPaymentResult(String billPaymentID,
			String accessTokenID) throws ServiceInventoryException {

		HttpEntity<BillPayment> requestEntity = new HttpEntity<BillPayment>(headers);

		ResponseEntity<BillPayment> responseEntity = restTemplate.exchange(
				endPoints.getBillPaymentInfoURL(), HttpMethod.GET,
				requestEntity, BillPayment.class, billPaymentID, accessTokenID);

		return responseEntity.getBody();
	}

}
