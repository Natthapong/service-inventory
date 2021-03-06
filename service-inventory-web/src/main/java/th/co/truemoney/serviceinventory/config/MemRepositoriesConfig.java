package th.co.truemoney.serviceinventory.config;

import java.math.BigDecimal;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import th.co.truemoney.serviceinventory.dao.ExpirableMap;
import th.co.truemoney.serviceinventory.dao.impl.MemoryExpirableMap;
import th.co.truemoney.serviceinventory.ewallet.domain.TopUpQuote;
import th.co.truemoney.serviceinventory.ewallet.repositories.AccessTokenRepository;
import th.co.truemoney.serviceinventory.ewallet.repositories.BillInformationRepository;
import th.co.truemoney.serviceinventory.ewallet.repositories.ForgotPasswordRepository;
import th.co.truemoney.serviceinventory.ewallet.repositories.OTPRepository;
import th.co.truemoney.serviceinventory.ewallet.repositories.RegisteringProfileRepository;
import th.co.truemoney.serviceinventory.ewallet.repositories.TransactionRepository;
import th.co.truemoney.serviceinventory.ewallet.repositories.impl.AccessTokenMemoryRepository;
import th.co.truemoney.serviceinventory.ewallet.repositories.impl.BillInformationMemoryRepository;
import th.co.truemoney.serviceinventory.ewallet.repositories.impl.ForgotPasswordMemoryRepository;
import th.co.truemoney.serviceinventory.ewallet.repositories.impl.OTPMemoryRepository;
import th.co.truemoney.serviceinventory.ewallet.repositories.impl.ProfileMemoryRepository;
import th.co.truemoney.serviceinventory.ewallet.repositories.impl.TransactionRepositoryImpl;
import th.co.truemoney.serviceinventory.transfer.domain.P2PTransferDraft;
import th.co.truemoney.serviceinventory.transfer.domain.P2PTransferTransaction;

@Configuration
@Profile("mem")
public class MemRepositoriesConfig {

	@Bean
	public AccessTokenRepository memAccessTokenRepository() {
		return new AccessTokenMemoryRepository();
	}

	@Bean
	public TransactionRepository memOrderRepository() {
		TransactionRepositoryImpl transactionRepository = new TransactionRepositoryImpl(memoryExpirableMap());

		P2PTransferDraft p2pTransferDraft = new P2PTransferDraft("0868185055", new BigDecimal("20.00"));
		p2pTransferDraft.setID("xxxx");
		p2pTransferDraft.setAccessTokenID("12345");
		transactionRepository.saveDraftTransaction(p2pTransferDraft, p2pTransferDraft.getAccessTokenID());
		p2pTransferDraft.setStatus(P2PTransferDraft.Status.OTP_CONFIRMED);

		P2PTransferTransaction p2pTransaction = new P2PTransferTransaction(p2pTransferDraft);
		transactionRepository.saveTransaction(p2pTransaction, p2pTransferDraft.getAccessTokenID());

		TopUpQuote topUpQuote =  new TopUpQuote();
		topUpQuote.setID("xxxx");
		topUpQuote.setAccessTokenID("12345");
		transactionRepository.saveDraftTransaction(topUpQuote, topUpQuote.getAccessTokenID());

		return transactionRepository;
	}

	@Bean
	public ExpirableMap memoryExpirableMap() {
		return new MemoryExpirableMap();
	}

	@Bean
	public OTPRepository memOTPRepository() {
		return new OTPMemoryRepository();
	}

	@Bean
	public RegisteringProfileRepository memProfileRepository() {
		return new ProfileMemoryRepository();
	}

	@Bean
	public BillInformationRepository memBillInfoRepository() {
		return new BillInformationMemoryRepository();
	}
	
	@Bean
	public ForgotPasswordRepository memForgotPasswordRepository() {
		return new ForgotPasswordMemoryRepository();
	}

}
