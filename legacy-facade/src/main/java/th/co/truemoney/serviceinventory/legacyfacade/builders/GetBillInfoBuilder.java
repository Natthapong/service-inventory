package th.co.truemoney.serviceinventory.legacyfacade.builders;

import org.apache.commons.lang.Validate;
import org.springframework.beans.factory.annotation.Autowired;

import th.co.truemoney.serviceinventory.bill.domain.BillInfo;
import th.co.truemoney.serviceinventory.bill.domain.services.GetBarcodeRequest;
import th.co.truemoney.serviceinventory.legacyfacade.ewallet.BillPaymentFacade;

public class GetBillInfoBuilder {

	private String channel;
	private String channelDetail;
	private String appUser;
	private String appPassword;
	private String appKey;
	private String barcode;

	private BillPaymentFacade billPaymentFacade;

	@Autowired(required = false)
	public GetBillInfoBuilder(BillPaymentFacade billPaymentFacade) {
		this.billPaymentFacade = billPaymentFacade;
	}

	public GetBillInfoBuilder withBarcode(String barcode) {
		this.barcode = barcode;
		return this;
	}

	public GetBillInfoBuilder fromBillChannel(String channel, String channelDetail) {
		this.channel = channel;
		this.channelDetail = channelDetail;
		return this;
	}

	public GetBillInfoBuilder fromApp(String appUser, String appPassword, String appKey) {
		this.appUser = appUser;
		this.appPassword = appPassword;
		this.appKey = appKey;

		return this;
	}

	public BillInfo getInformation() {
		Validate.notNull(channel, "data missing. get barcode information from which channel?");
		Validate.notNull(channelDetail, "missing channel detail.");
		Validate.notNull(appUser, "data missing. from which app user?");
		Validate.notNull(appPassword, "data missing. missing app password.");
		Validate.notNull(appKey, "data missing. missing app key.");
		Validate.notNull(barcode, "data missing. barcode missing?");

		GetBarcodeRequest billRequest = new GetBarcodeRequest();

		billRequest.setChannel(channel);
		billRequest.setChannelDetail(channelDetail);

		billRequest.setAppUser(appUser);
		billRequest.setAppPassword(appPassword);
		billRequest.setAppKey(appKey);

		billRequest.setBarcode(barcode);

		return billPaymentFacade.getBarcodeInformation(billRequest);
	}

}
