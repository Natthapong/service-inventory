package th.co.truemoney.serviceinventory.config;

import java.util.concurrent.Executor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import th.co.truemoney.serviceinventory.email.EmailService;
import th.co.truemoney.serviceinventory.ewallet.P2PTransferService;
import th.co.truemoney.serviceinventory.ewallet.TmnProfileService;
import th.co.truemoney.serviceinventory.ewallet.TopUpService;
import th.co.truemoney.serviceinventory.ewallet.impl.AsyncService;
import th.co.truemoney.serviceinventory.ewallet.impl.DirectDebitSourceOfFundServiceImpl;
import th.co.truemoney.serviceinventory.ewallet.impl.EnhancedDirectDebitSourceOfFundService;
import th.co.truemoney.serviceinventory.ewallet.impl.ExtendAccessTokenAsynService;
import th.co.truemoney.serviceinventory.ewallet.impl.P2PTransferServiceImpl;
import th.co.truemoney.serviceinventory.ewallet.impl.TmnProfileServiceImpl;
import th.co.truemoney.serviceinventory.ewallet.impl.TopUpServiceImpl;
import th.co.truemoney.serviceinventory.ewallet.repositories.DirectDebitConfig;
import th.co.truemoney.serviceinventory.ewallet.repositories.impl.DirectDebitConfigImpl;
import th.co.truemoney.serviceinventory.sms.OTPService;

@Configuration
@EnableAsync
@ComponentScan("th.co.truemoney.serviceinventory.dao")
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
    public DirectDebitConfig getDirectDebitConfig() {
    	return new DirectDebitConfigImpl();
    }

    @Bean
    public AsyncService getAsyncService() {
    	return new AsyncService();
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
	public EmailService getEmailService() {
		return new EmailService();
	}

}
