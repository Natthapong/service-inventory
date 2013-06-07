package th.co.truemoney.serviceinventory.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;


public class EndPoints {

	@Autowired @Qualifier("endpoint.host")
	private String host = "https://127.0.0.1:9443";

	public String getListAllReport() {
		return host + "/core-report-web/transaction/history/{tmnID}";
	}

	public String getReportDetail() {
		System.out.println("**********************getReportDetail URL !!!!!*****************");
		return host + "/core-report-web/transaction/history/{trueMoneyID}/detail/{reportID}";
	}

}
