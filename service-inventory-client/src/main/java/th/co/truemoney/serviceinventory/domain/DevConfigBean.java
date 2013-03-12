package th.co.truemoney.serviceinventory.domain;

public class DevConfigBean {
	private String dev_url = "https://dev.truemoney.co.th";
	//private String dev_url = "https://10.98.49.18/";
	
	public String getPathDevUrl(String path){
		return dev_url + path;
	}
	
}
