package th.co.truemoney.serviceinventory.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import th.co.truemoney.serviceinventory.engine.client.proxy.impl.BillProxy;
import th.co.truemoney.serviceinventory.engine.client.proxy.impl.TopUpMobileProxy;
import th.co.truemoney.serviceinventory.ewallet.proxy.ewalletsoap.EwalletSoapProxy;
import th.co.truemoney.serviceinventory.ewallet.proxy.tmnprofile.TmnProfileProxy;
import th.co.truemoney.serviceinventory.ewallet.proxy.tmnprofile.admin.TmnProfileAdminProxy;
import th.co.truemoney.serviceinventory.ewallet.proxy.tmnsecurity.TmnSecurityProxy;
import th.co.truemoney.serviceinventory.firsthop.message.SmsRequest;
import th.co.truemoney.serviceinventory.firsthop.message.SmsResponse;
import th.co.truemoney.serviceinventory.firsthop.proxy.SmsProxy;
import th.co.truemoney.serviceinventory.firsthop.proxy.impl.SmsProxyImpl;
import th.co.truemoney.serviceinventory.persona.TrueConvergenceOneBillPersona;
import th.co.truemoney.serviceinventory.persona.proxies.LocalEwalletSoapProxy;
import th.co.truemoney.serviceinventory.persona.proxies.LocalProfileAdminProxy;
import th.co.truemoney.serviceinventory.persona.proxies.LocalSecurityProxy;
import th.co.truemoney.serviceinventory.persona.proxies.LocalTmnProfileProxy;
import th.co.truemoney.serviceinventory.sms.OTPGenerator;
import th.co.truemoney.serviceinventory.sms.UnSecureOTPGenerator;

@Configuration
@Profile("local")
public class LocalEnvironmentConfig {

    @Bean @Qualifier("endpoint.host") @Primary
    public String host() {
        return "http://127.0.0.1:8787";
    }

    @Bean
    @Primary
    public TmnProfileProxy stubTmnProfileProxy() {
        return new LocalTmnProfileProxy();
    }

    @Bean
    @Primary
    public TmnSecurityProxy stubTmnSecurityProxy() {
        return new LocalSecurityProxy();
    }

    @Bean
    @Primary
    public EwalletSoapProxy stubEWalletSoapProxy() {
        return new LocalEwalletSoapProxy();
    }

    @Bean
    @Primary
    public SmsProxy stubSmsProxy() {
        return new SmsProxy() {

            @Override
            public SmsResponse send(SmsRequest request) {
                StringBuilder xmlResponse = new StringBuilder();
                xmlResponse
                        .append("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>");
                xmlResponse.append("<message id=\"13619484376108002\">");
                xmlResponse.append("<rsr type=\"ack\">");
                xmlResponse.append("<service-id/>");
                xmlResponse
                        .append("<destination messageid=\"13619484376108002\">");
                xmlResponse.append("<address>");
                xmlResponse
                        .append("<number type=\"international\">66891267026</number>");
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
        };
    }

    @Bean
    public TmnProfileAdminProxy stubTmnProfileAdminProxy() {
        return new LocalProfileAdminProxy();
    }

    @Bean
    public OTPGenerator otpGenerator() {
        return new UnSecureOTPGenerator();
    }

    @Bean
    public BillProxy billPayProxy() {
        return new TrueConvergenceOneBillPersona().getBillPayProxy();
    }

    @Bean
    public TopUpMobileProxy topUpMobileProxy() {
        return new TrueConvergenceOneBillPersona().getTopUpMobileProxy();
    }
    
    @Bean
	public DataSource dataSource() {
		EmbeddedDatabaseBuilder builder = new EmbeddedDatabaseBuilder();
		EmbeddedDatabase database = (EmbeddedDatabase)builder.setType(EmbeddedDatabaseType.H2)
				.addScript("dataset/schema.sql")
				.build();
		return database;
	}
}