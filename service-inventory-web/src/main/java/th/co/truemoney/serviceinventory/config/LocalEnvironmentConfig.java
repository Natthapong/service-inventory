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
import th.co.truemoney.serviceinventory.engine.client.proxy.impl.BuyProxy;
import th.co.truemoney.serviceinventory.engine.client.proxy.impl.TopUpMobileProxy;
import th.co.truemoney.serviceinventory.ewallet.proxy.TmnProfileAdminProxyClient;
import th.co.truemoney.serviceinventory.ewallet.proxy.TmnProfileProxyClient;
import th.co.truemoney.serviceinventory.ewallet.proxy.TmnSecurityProxyClient;
import th.co.truemoney.serviceinventory.ewallet.proxy.WalletProxyClient;
import th.co.truemoney.serviceinventory.firsthop.proxy.SmsProxy;
import th.co.truemoney.serviceinventory.persona.LocalBillsPersona;
import th.co.truemoney.serviceinventory.persona.proxies.LocalBuyProductProxy;
import th.co.truemoney.serviceinventory.persona.proxies.LocalProfileAdminProxyClient;
import th.co.truemoney.serviceinventory.persona.proxies.LocalSecurityProxyClient;
import th.co.truemoney.serviceinventory.persona.proxies.LocalSmsProxy;
import th.co.truemoney.serviceinventory.persona.proxies.LocalTmnProfileProxyClient;
import th.co.truemoney.serviceinventory.persona.proxies.LocalWalletProxyClient;
import th.co.truemoney.serviceinventory.persona.proxies.TopUpTruemoneyProxy;
import th.co.truemoney.serviceinventory.sms.OTPGenerator;
import th.co.truemoney.serviceinventory.sms.UnSecureOTPGenerator;

@Configuration
@Profile("local")
public class LocalEnvironmentConfig {

    @Bean @Qualifier("core.report.endpoint.host") 
    @Primary
    public String coreReportWebHost() {
        return "http://localhost:8787";
    }
    
    @Bean @Qualifier("endpoint.host") 
    @Primary
    public String coreServiceWebHost() {
        return "http://127.0.0.1:8585";
    }

    @Bean
    @Primary
    public TmnProfileProxyClient stubTmnProfileProxyClient() {
        return new LocalTmnProfileProxyClient();
    }

    @Bean
    @Primary
    public TmnSecurityProxyClient stubTmnSecurityProxyClient() {
        return new LocalSecurityProxyClient();
    }
    
    @Bean
    public TmnProfileAdminProxyClient stubTmnProfileAdminProxyClient() {
        return new LocalProfileAdminProxyClient();
    }
    
    @Bean
    @Primary
    public WalletProxyClient stubWalletProxyClient() {
        return new LocalWalletProxyClient();
    }
    
    @Bean
    @Primary
    public SmsProxy stubSmsProxy() {
        return new LocalSmsProxy();
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
    public BuyProxy buyProxy() {
        return new LocalBuyProductProxy();
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