[1mdiff --git a/service-inventory-client/src/main/java/th/co/truemoney/serviceinventory/ewallet/client/config/EndPoints.java b/service-inventory-client/src/main/java/th/co/truemoney/serviceinventory/ewallet/client/config/EndPoints.java[m
[1mindex be1448a..be9458a 100644[m
[1m--- a/service-inventory-client/src/main/java/th/co/truemoney/serviceinventory/ewallet/client/config/EndPoints.java[m
[1m+++ b/service-inventory-client/src/main/java/th/co/truemoney/serviceinventory/ewallet/client/config/EndPoints.java[m
[36m@@ -8,7 +8,8 @@[m [mimport org.springframework.beans.factory.annotation.Qualifier;[m
 public class EndPoints {[m
 [m
 	@Autowired @Qualifier("endpoint.host")[m
[31m-	private String host = "https://dev.truemoney.co.th";[m
[32m+[m[32m//	private String host = "https://dev.truemoney.co.th";[m
[32m+[m	[32mprivate String host = "https://10.221.6.239";[m
 [m
 	public String getLoginURL() {[m
 		return host + "/service-inventory-web/v1/ewallet/login";[m
