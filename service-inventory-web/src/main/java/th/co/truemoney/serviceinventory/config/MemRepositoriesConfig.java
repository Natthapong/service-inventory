package th.co.truemoney.serviceinventory.config;

import java.math.BigDecimal;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import th.co.truemoney.serviceinventory.ewallet.domain.DraftTransaction;
import th.co.truemoney.serviceinventory.ewallet.domain.TopUpQuote;
import th.co.truemoney.serviceinventory.ewallet.repositories.AccessTokenRepository;
import th.co.truemoney.serviceinventory.ewallet.repositories.OTPRepository;
import th.co.truemoney.serviceinventory.ewallet.repositories.RegisteringProfileRepository;
import th.co.truemoney.serviceinventory.ewallet.repositories.TransactionRepository;
import th.co.truemoney.serviceinventory.ewallet.repositories.impl.AccessTokenMemoryRepository;
import th.co.truemoney.serviceinventory.ewallet.repositories.impl.OTPMemoryRepository;
import th.co.truemoney.serviceinventory.ewallet.repositories.impl.ProfileMemoryRepository;
import th.co.truemoney.serviceinventory.ewallet.repositories.impl.TransactionMemoryRepository;
import th.co.truemoney.serviceinventory.transfer.domain.P2PDraftTransaction;
import th.co.truemoney.serviceinventory.transfer.domain.P2PTransaction;

@Configuration
@Profile("mem")
public class MemRepositoriesConfig {

	@Bean
	public AccessTokenRepository memAccessTokenRepository() {
		return new AccessTokenMemoryRepository();
	}

	@Bean
	public TransactionRepository memOrderRepository() {
		TransactionRepository transactionRepository = new TransactionMemoryRepository();

		P2PDraftTransaction p2pDraftTransaction = new P2PDraftTransaction("0868185055", new BigDecimal("20.00"));
		p2pDraftTransaction.setID("xxxx");
		p2pDraftTransaction.setAccessTokenID("12345");
		transactionRepository.saveP2PDraftTransaction(p2pDraftTransaction, p2pDraftTransaction.getAccessTokenID());
		p2pDraftTransaction.setStatus(DraftTransaction.Status.OTP_CONFIRMED);

		P2PTransaction p2pTransaction = new P2PTransaction(p2pDraftTransaction);
		transactionRepository.saveP2PTransaction(p2pTransaction, p2pDraftTransaction.getAccessTokenID());

		TopUpQuote topUpQuote =  new TopUpQuote();
		topUpQuote.setID("xxxx");
		topUpQuote.setAccessTokenID("12345");
		transactionRepository.saveTopUpEwalletDraftTransaction(topUpQuote, topUpQuote.getAccessTokenID());

		return transactionRepository;
	}

	@Bean
	public OTPRepository memOTPRepository() {
		return new OTPMemoryRepository();
	}

	@Bean
	public RegisteringProfileRepository memoProfileRepository() {
		ProfileMemoryRepository profile = new ProfileMemoryRepository();
		return profile;
	}

}
