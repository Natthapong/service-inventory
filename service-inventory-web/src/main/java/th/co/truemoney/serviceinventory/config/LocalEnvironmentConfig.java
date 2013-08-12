package th.co.truemoney.serviceinventory.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import th.co.truemoney.serviceinventory.engine.client.proxy.impl.BillProxy;
import th.co.truemoney.serviceinventory.engine.client.proxy.impl.TopUpMobileProxy;
import th.co.truemoney.serviceinventory.ewallet.proxy.TmnProfileProxyClient;
import th.co.truemoney.serviceinventory.ewallet.proxy.TmnSecurityProxyClient;
import th.co.truemoney.serviceinventory.ewallet.proxy.ewalletsoap.EwalletSoapProxy;
import th.co.truemoney.serviceinventory.ewallet.proxy.tmnprofile.TmnProfileProxy;
import th.co.truemoney.serviceinventory.ewallet.proxy.tmnprofile.admin.TmnProfileAdminProxy;
import th.co.truemoney.serviceinventory.firsthop.proxy.SmsProxy;
import th.co.truemoney.serviceinventory.persona.LocalBillsPersona;
import th.co.truemoney.serviceinventory.persona.proxies.LocalEwalletSoapProxy;
import th.co.truemoney.serviceinventory.persona.proxies.LocalProfileAdminProxy;
import th.co.truemoney.serviceinventory.persona.proxies.LocalSecurityProxy;
import th.co.truemoney.serviceinventory.persona.proxies.LocalSmsProxy;
import th.co.truemoney.serviceinventory.persona.proxies.LocalTmnProfileProxy;
import th.co.truemoney.serviceinventory.persona.proxies.LocalTmnProfileProxyClient;
import th.co.truemoney.serviceinventory.persona.proxies.TopUpTruemoneyProxy;
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
    public TmnSecurityProxyClient stubTmnSecurityProxy() {
        return new LocalSecurityProxy();
    }
    
    @Bean
    @Primary
    public TmnProfileProxyClient stubTmnProfileProxyClient() {
        return new LocalTmnProfileProxyClient();
    }

    @Bean
    @Primary
    public EwalletSoapProxy stubEWalletSoapProxy() {
        return new LocalEwalletSoapProxy();
    }

    @Bean
    @Primary
    public SmsProxy stubSmsProxy() {
        return new LocalSmsProxy();
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
        return new LocalBillsPersona().getBillPayProxy();
    }

    @Bean
    public TopUpMobileProxy topUpMobileProxy() {
        return new TopUpTruemoneyProxy();
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