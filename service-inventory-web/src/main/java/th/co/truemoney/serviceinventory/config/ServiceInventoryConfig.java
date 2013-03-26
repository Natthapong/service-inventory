package th.co.truemoney.serviceinventory.config;

import java.util.concurrent.Executor;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import th.co.truemoney.serviceinventory.bean.OTPBean;
import th.co.truemoney.serviceinventory.ewallet.P2PTransferService;
import th.co.truemoney.serviceinventory.ewallet.SourceOfFundService;
import th.co.truemoney.serviceinventory.ewallet.TmnProfileService;
import th.co.truemoney.serviceinventory.ewallet.TopUpService;
import th.co.truemoney.serviceinventory.ewallet.domain.AccessToken;
import th.co.truemoney.serviceinventory.ewallet.domain.DirectDebit;
import th.co.truemoney.serviceinventory.ewallet.domain.TopUpConfirmationInfo;
import th.co.truemoney.serviceinventory.ewallet.domain.TopUpOrder;
import th.co.truemoney.serviceinventory.ewallet.impl.AsyncService;
import th.co.truemoney.serviceinventory.ewallet.impl.ExtendAccessTokenAsynService;
import th.co.truemoney.serviceinventory.ewallet.impl.P2PTransferServiceImpl;
import th.co.truemoney.serviceinventory.ewallet.impl.SourceOfFundServiceImpl;
import th.co.truemoney.serviceinventory.ewallet.impl.TmnProfileServiceImpl;
import th.co.truemoney.serviceinventory.ewallet.impl.TopUpServiceImpl;
import th.co.truemoney.serviceinventory.ewallet.repositories.AccessTokenRepository;
import th.co.truemoney.serviceinventory.ewallet.repositories.DirectDebitConfig;
import th.co.truemoney.serviceinventory.ewallet.repositories.OTPRepository;
import th.co.truemoney.serviceinventory.ewallet.repositories.OrderRepository;
import th.co.truemoney.serviceinventory.ewallet.repositories.SourceOfFundRepository;
import th.co.truemoney.serviceinventory.ewallet.repositories.impl.AccessTokenMemoryRepository;
import th.co.truemoney.serviceinventory.ewallet.repositories.impl.AccessTokenRedisRepository;
import th.co.truemoney.serviceinventory.ewallet.repositories.impl.DirectDebitConfigImpl;
import th.co.truemoney.serviceinventory.ewallet.repositories.impl.OTPMemoryRepository;
import th.co.truemoney.serviceinventory.ewallet.repositories.impl.OTPRedisRepository;
import th.co.truemoney.serviceinventory.ewallet.repositories.impl.OrderMemoryRepository;
import th.co.truemoney.serviceinventory.ewallet.repositories.impl.OrderRedisRepository;
import th.co.truemoney.serviceinventory.sms.OTPService;
import th.co.truemoney.serviceinventory.sms.impl.OTPServiceImpl;

@Configuration
@EnableAsync
@ComponentScan("th.co.truemoney.serviceinventory.dao")
public class ServiceInventoryConfig {

	@Bean
	public TmnProfileService getTmnProfileService() {
		return new TmnProfileServiceImpl();
	}

	@Bean
	public SourceOfFundService getSourceOfFundService() {
		return new SourceOfFundServiceImpl();
	}
	
	@Bean
    public TopUpService getTopUpService() {
    	return new TopUpServiceImpl();
    }

	@Bean
    public OTPService getOtpService() {
    	return new OTPServiceImpl();
    }
	
    @Bean @Qualifier("accessTokenMemoryRepository")
    public AccessTokenRepository getAccessTokenMemoryRepository() {
    	AccessTokenMemoryRepository accessTokenMemoryRepository = new AccessTokenMemoryRepository();
    	accessTokenMemoryRepository.save(new AccessToken("12345", "6789", "555", "username", "0861234567", "local@tmn.com", 41));
        return accessTokenMemoryRepository;
    }

    @Bean @Qualifier("accessTokenRedisRepository")
    public AccessTokenRepository getAccessTokenRedisRepository() {
        return new AccessTokenRedisRepository();
    }
    
    @Bean @Qualifier("orderMemoryRepository")
    public OrderRepository getOrderMemoryRepository() {
    	OrderRepository repository = new OrderMemoryRepository();
    	TopUpConfirmationInfo confirmationInfo = new TopUpConfirmationInfo();
    	confirmationInfo.setTransactionID("1");
    	confirmationInfo.setTransactionDate("03-21-2013 16:45");
    	DirectDebit directDebit = new DirectDebit();
    	directDebit.setSourceOfFundID("123");
    	directDebit.setSourceOfFundType("direc-debit");
    	TopUpOrder topupOrder = new TopUpOrder();
    	topupOrder.setID("1");
    	topupOrder.setConfirmationInfo(confirmationInfo);
    	topupOrder.setSourceOfFund(directDebit);
    	repository.saveTopUpOrder(topupOrder);
    	return repository;
    }
    
    @Bean @Qualifier("orderRedisRepository")
    public OrderRepository getOrderRedisRepository() {
    	return new OrderRedisRepository();
    }
    
    @Bean
    public SourceOfFundRepository sourceOfFundRepo() {
    	return new SourceOfFundRepository();
    }
    
    @Bean 
    public DirectDebitConfig getDirectDebitConfig() {
    	return new DirectDebitConfigImpl();
    }

    @Bean @Qualifier("otpMemoryRepository")
    public OTPRepository getOTPMemoryRepository() {
    	OTPRepository otpRepository = new OTPMemoryRepository();
    	OTPBean otpBean = new OTPBean();
    	otpBean.setMobileno("0861234567");
    	otpBean.setOtpReferenceCode("Code");
    	otpBean.setOtpString("112233");
    	otpRepository.saveOTP(otpBean);
    	return otpRepository;
    }
    
    @Bean @Qualifier("otpRedisRepository")
    public OTPRepository getOTPRedisRepository() {
    	return new OTPRedisRepository();
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
    
}
