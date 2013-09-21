package th.co.truemoney.serviceinventory.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;


public class CoreReportWebEndPoints {

	@Autowired @Qualifier("core.report.endpoint.host")
	private String coreReportWebHost = "https://127.0.0.1";

	public String getListAllReport() {
		return coreReportWebHost + "/core-report-web/transaction/history/{tmnID}";
	}

	public String getReportDetail() {
		return coreReportWebHost + "/core-report-web/transaction/history/{trueMoneyID}/detail/{reportID}";
	}

}
