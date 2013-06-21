package th.co.truemoney.serviceinventory.config;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import th.co.truemoney.serviceinventory.authen.TransactionAuthenService;
import th.co.truemoney.serviceinventory.authen.impl.TransactionAuthenServiceImpl;
import th.co.truemoney.serviceinventory.bill.BillPaymentService;
import th.co.truemoney.serviceinventory.bill.BillRetriever;
import th.co.truemoney.serviceinventory.bill.impl.AsyncBillPayProcessor;
import th.co.truemoney.serviceinventory.bill.impl.BillPaymentServiceImpl;
import th.co.truemoney.serviceinventory.bill.impl.BillPaymentValidationConfig;
import th.co.truemoney.serviceinventory.bill.impl.BillRetrieverImpl;
import th.co.truemoney.serviceinventory.bill.validation.BillOverDueValidator;
import th.co.truemoney.serviceinventory.email.EmailService;
import th.co.truemoney.serviceinventory.engine.client.config.SIEngineConfig;
import th.co.truemoney.serviceinventory.ewallet.ActivityService;
import th.co.truemoney.serviceinventory.ewallet.EnhancedDirectDebitSourceOfFundService;
import th.co.truemoney.serviceinventory.ewallet.FavoriteService;
import th.co.truemoney.serviceinventory.ewallet.TmnProfileService;
import th.co.truemoney.serviceinventory.ewallet.TopUpService;
import th.co.truemoney.serviceinventory.ewallet.config.EwalletConfig;
import th.co.truemoney.serviceinventory.ewallet.config.TmnProfileConfig;
import th.co.truemoney.serviceinventory.ewallet.impl.ActivityServiceImpl;
import th.co.truemoney.serviceinventory.ewallet.impl.AsyncP2PTransferProcessor;
import th.co.truemoney.serviceinventory.ewallet.impl.AsyncTopUpEwalletProcessor;
import th.co.truemoney.serviceinventory.ewallet.impl.AsyncTopUpMobileProcessor;
import th.co.truemoney.serviceinventory.ewallet.impl.DirectDebitSourceOfFundServiceImpl;
import th.co.truemoney.serviceinventory.ewallet.impl.ExtendAccessTokenAsynService;
import th.co.truemoney.serviceinventory.ewallet.impl.FavoriteServiceImpl;
import th.co.truemoney.serviceinventory.ewallet.impl.P2PTransferServiceImpl;
import th.co.truemoney.serviceinventory.ewallet.impl.TmnProfileServiceImpl;
import th.co.truemoney.serviceinventory.ewallet.impl.TopUpMobileServiceImpl;
import th.co.truemoney.serviceinventory.ewallet.impl.TopUpServiceImpl;
import th.co.truemoney.serviceinventory.ewallet.repositories.impl.SourceOfFundPreference;
import th.co.truemoney.serviceinventory.ewallet.repositories.impl.SourceOfFundPreferenceImpl;
import th.co.truemoney.serviceinventory.firsthop.config.SmsConfig;
import th.co.truemoney.serviceinventory.sms.OTPService;
import th.co.truemoney.serviceinventory.topup.TopUpMobileService;
import th.co.truemoney.serviceinventory.transfer.P2PTransferService;

@Configuration
@EnableAsync(proxyTargetClass = true)
@EnableAspectJAutoProxy(proxyTargetClass = true)
@ComponentScan({"th.co.truemoney.serviceinventory.dao", "th.co.truemoney.serviceinventory.aop","th.co.truemoney.serviceinventory.log"})
@Import({EwalletConfig.class, SmsConfig.class, TmnProfileConfig.class, SIEngineConfig.class, EmailConfig.class, LegacyFacadeConfig.class, RedisRepositoriesConfig.class, ProdEnvironmentConfig.class, DevEnvironmentConfig.class })
public class ServiceInventoryConfig {
	
    @Bean
    public TmnProfileService getTmnProfileService() {
        return new TmnProfileServiceImpl();
    }

    @Bean
    public EnhancedDirectDebitSourceOfFundService directDebitSourceService() {
        return new DirectDebitSourceOfFundServiceImpl();
    }

    @Bean
    public TopUpService getTopUpService() {
        return new TopUpServiceImpl();
    }

    @Bean
    public OTPService getOtpService() {
        return new OTPService();
    }

    @Bean
    public TransactionAuthenService getTransactionAuthenService() {
        return new TransactionAuthenServiceImpl();
    }

    @Bean
    public BillPaymentService getBillPaymentService(){
        return new BillPaymentServiceImpl();
    }

    @Bean
    public BillRetriever billRetriever() {
        return new BillRetrieverImpl();
    }

    @Bean
    public TopUpMobileService getTopUpMobileService() {
        return new TopUpMobileServiceImpl();
    }

    @Bean ActivityService getActivityService() {
        return new ActivityServiceImpl();
    }

    @Bean
    public FavoriteService getFavoriteService() {
        return new FavoriteServiceImpl();
    }

    @Bean
    public AsyncBillPayProcessor getAsyncBillPayProcessor() {
        return new AsyncBillPayProcessor();
    }

    @Bean
    public SourceOfFundPreference getDirectDebitConfig() {
        return new SourceOfFundPreferenceImpl();
    }

    @Bean
    public BillPaymentValidationConfig getBillPaymentValidationConfig() {
        return new BillPaymentValidationConfig();
    }

    @Bean
    public AsyncTopUpEwalletProcessor getAsyncTopUpEwalletProcessor() {
        return new AsyncTopUpEwalletProcessor();
    }

    @Bean
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(7);
        executor.setMaxPoolSize(56);
        executor.setQueueCapacity(11);
        executor.setThreadNamePrefix("asyncExecutor-");
        executor.initialize();
        return executor;
    }

    @Bean
    public ExtendAccessTokenAsynService getExtendAccessTokenAsynService() {
        return new ExtendAccessTokenAsynService();
    }

    @Bean
    public P2PTransferService getP2pTransferService() {
        return new P2PTransferServiceImpl();
    }

    @Bean
    public AsyncP2PTransferProcessor getAsyncP2PTransferProcessor() {
        return new AsyncP2PTransferProcessor();
    }

    @Bean
    public EmailService emailService() {
        return new EmailService();
    }

    @Bean
    public AsyncTopUpMobileProcessor getAsyncTopUpMobileProcessor() {
        return new AsyncTopUpMobileProcessor();
    }

    @Bean
    public BillOverDueValidator billOverDueValidator() {
        return new BillOverDueValidator();
    }

    @Bean
    public EndPoints endPoints() {
        return new EndPoints();
    }

    @Bean
    @Qualifier("jsonHttpHeader")
    public HttpHeaders defaultHttpHeaders() {
        HttpHeaders headers = new HttpHeaders();

        List<MediaType> acceptableMediaTypes = new ArrayList<MediaType>();
        acceptableMediaTypes.add(MediaType.APPLICATION_JSON);
        headers.setAccept(acceptableMediaTypes);
        headers.setContentType(MediaType.APPLICATION_JSON);

        return headers;
    }

}
