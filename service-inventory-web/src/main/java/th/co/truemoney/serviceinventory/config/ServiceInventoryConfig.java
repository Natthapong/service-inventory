package th.co.truemoney.serviceinventory.config;

import java.util.concurrent.Executor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import th.co.truemoney.serviceinventory.ewallet.EnhancedDirectDebitSourceOfFundService;
import th.co.truemoney.serviceinventory.ewallet.TmnProfileService;
import th.co.truemoney.serviceinventory.ewallet.TopUpService;
import th.co.truemoney.serviceinventory.ewallet.impl.AsyncP2PTransferProcessor;
import th.co.truemoney.serviceinventory.ewallet.impl.AsyncTopUpEwalletProcessor;
import th.co.truemoney.serviceinventory.ewallet.impl.DirectDebitSourceOfFundServiceImpl;
import th.co.truemoney.serviceinventory.ewallet.impl.ExtendAccessTokenAsynService;
import th.co.truemoney.serviceinventory.ewallet.impl.P2PTransferServiceImpl;
import th.co.truemoney.serviceinventory.ewallet.impl.TmnProfileServiceImpl;
import th.co.truemoney.serviceinventory.ewallet.impl.TopUpServiceImpl;
import th.co.truemoney.serviceinventory.ewallet.repositories.impl.SourceOfFundPreference;
import th.co.truemoney.serviceinventory.ewallet.repositories.impl.SourceOfFundPreferenceImpl;
import th.co.truemoney.serviceinventory.sms.OTPService;
import th.co.truemoney.serviceinventory.transfer.P2PTransferService;

@Configuration
@EnableAsync
@EnableAspectJAutoProxy
@ComponentScan({"th.co.truemoney.serviceinventory.dao", "th.co.truemoney.serviceinventory.aop"})
@Import({SmsConfig.class, TmnProfileConfig.class, EmailConfig.class, LegacyFacadeConfig.class })
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
    public SourceOfFundPreference getDirectDebitConfig() {
    	return new SourceOfFundPreferenceImpl();
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

}
