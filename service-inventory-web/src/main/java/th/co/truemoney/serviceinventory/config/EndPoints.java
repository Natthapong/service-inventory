package th.co.truemoney.serviceinventory.config;


public class EndPoints {

	private String host = "http://127.0.0.1:9443";

	public String getListAllReport() {
		return host + "/core-report-web/transaction/history/{tmnID}";
	}

	public String getReportDetail() {
		return host + "/core-report-web/transaction/history/detail/{reportID}";
	}

}
