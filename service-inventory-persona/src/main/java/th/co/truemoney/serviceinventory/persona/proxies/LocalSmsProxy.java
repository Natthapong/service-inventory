package th.co.truemoney.serviceinventory.persona.proxies;

import th.co.truemoney.serviceinventory.firsthop.message.SmsRequest;
import th.co.truemoney.serviceinventory.firsthop.message.SmsResponse;
import th.co.truemoney.serviceinventory.firsthop.proxy.SmsProxy;
import th.co.truemoney.serviceinventory.firsthop.proxy.impl.SmsProxyImpl;


public class LocalSmsProxy implements SmsProxy {

	 @Override
	 public SmsResponse send(SmsRequest request) {
         StringBuilder xmlResponse = new StringBuilder();
         xmlResponse.append("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>");
         xmlResponse.append("<message id=\"13619484376108002\">");
         xmlResponse.append("<rsr type=\"ack\">");
         xmlResponse.append("<service-id/>");
         xmlResponse.append("<destination messageid=\"13619484376108002\">");
         xmlResponse.append("<address>");
         xmlResponse.append("<number type=\"international\">66891267026</number>");
         xmlResponse.append("</address>");
         xmlResponse.append("</destination>");
         xmlResponse.append("<source>");
         xmlResponse.append("<address>");
         xmlResponse.append("<number type=\"\">TMN-ID</number>");
         xmlResponse.append("</address>");
         xmlResponse.append("</source>");
         xmlResponse.append("<rsr_detail status=\"success\">");
         xmlResponse.append("<code>000</code>");
         xmlResponse.append("<description>success</description>");
         xmlResponse.append("</rsr_detail>");
         xmlResponse.append("</rsr>");
         xmlResponse.append("</message>");

         return new SmsProxyImpl().readXMLResponse(xmlResponse
                 .toString());
     }

}
