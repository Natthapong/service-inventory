package th.co.truemoney.serviceinventory.config;

import java.math.BigDecimal;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import th.co.truemoney.serviceinventory.ewallet.domain.TopUpQuote;
import th.co.truemoney.serviceinventory.ewallet.repositories.AccessTokenRepository;
import th.co.truemoney.serviceinventory.ewallet.repositories.OTPRepository;
import th.co.truemoney.serviceinventory.ewallet.repositories.RegisteringProfileRepository;
import th.co.truemoney.serviceinventory.ewallet.repositories.TransactionRepository;
import th.co.truemoney.serviceinventory.ewallet.repositories.impl.AccessTokenMemoryRepository;
import th.co.truemoney.serviceinventory.ewallet.repositories.impl.OTPMemoryRepository;
import th.co.truemoney.serviceinventory.ewallet.repositories.impl.ProfileMemoryRepository;
import th.co.truemoney.serviceinventory.ewallet.repositories.impl.TransactionMemoryRepository;
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
		TransactionRepository transactionRepository = new TransactionMemoryRepository();

		P2PTransferDraft p2pTransferDraft = new P2PTransferDraft("0868185055", new BigDecimal("20.00"));
		p2pTransferDraft.setID("xxxx");
		p2pTransferDraft.setAccessTokenID("12345");
		transactionRepository.saveP2PTransferDraft(p2pTransferDraft, p2pTransferDraft.getAccessTokenID());
		p2pTransferDraft.setStatus(P2PTransferDraft.Status.OTP_CONFIRMED);

		P2PTransferTransaction p2pTransaction = new P2PTransferTransaction(p2pTransferDraft);
		transactionRepository.saveP2PTransferTransaction(p2pTransaction, p2pTransferDraft.getAccessTokenID());

		TopUpQuote topUpQuote =  new TopUpQuote();
		topUpQuote.setID("xxxx");
		topUpQuote.setAccessTokenID("12345");
		transactionRepository.saveTopUpQuote(topUpQuote, topUpQuote.getAccessTokenID());

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
