package th.co.truemoney.serviceinventory.util;

import static org.mockito.Matchers.*;

import java.math.BigDecimal;
import java.util.List;

import org.mockito.Mockito;
import org.mockito.stubbing.Stubber;

import th.co.truemoney.serviceinventory.ewallet.domain.DirectDebit;
import th.co.truemoney.serviceinventory.ewallet.domain.TopUpConfirmationInfo;
import th.co.truemoney.serviceinventory.legacyfacade.ewallet.BalanceFacade;
import th.co.truemoney.serviceinventory.legacyfacade.ewallet.SourceOfFundFacade;
import th.co.truemoney.serviceinventory.transfer.domain.P2PTransactionConfirmationInfo;

public class TestData {

	public static class SourceOfFundFacadeMocker {

		private SourceOfFundFacade sofFacadeMock;

		public SourceOfFundFacadeMocker() {
			this(Mockito.mock(SourceOfFundFacade.class));
		}

		public SourceOfFundFacadeMocker(SourceOfFundFacade facade) {
			this.sofFacadeMock = facade;
		}

		public SourceOfFundFacade getMockObject() {
			return sofFacadeMock;
		}

		public List<DirectDebit> getAllDirectDebitSourceOfFunds() {
			return sofFacadeMock.getAllDirectDebitSourceOfFunds(anyInt(), anyString(), anyString());
		}
	}

	public static class BalanceFacadeMocker {

		private BalanceFacade balanceFacadeMock;

		public BalanceFacadeMocker() {
			this(Mockito.mock(BalanceFacade.class));
		}

		public BalanceFacadeMocker(BalanceFacade facade) {
			this.balanceFacadeMock = facade;
		}

		public BalanceFacade getMockObject() {
			return balanceFacadeMock;
		}

		public BigDecimal getCurrentBalance() {
			return balanceFacadeMock.getCurrentBalance(anyInt(), anyString(), anyString());
		}

		public void verifyP2PTransfer(Stubber stubber) {
			stubber.when(balanceFacadeMock).verifyP2PTransfer(any(BigDecimal.class), anyString(), anyInt(), anyString(), anyString());
		}

		public P2PTransactionConfirmationInfo transferEwallet() {
			return balanceFacadeMock.transferEwallet(any(BigDecimal.class), anyString(), anyInt(), anyString(), anyString());
		}

		public void verifyToppingUpCapability(Stubber stubber) {
			stubber.when(balanceFacadeMock).verifyToppingUpCapability(any(BigDecimal.class), anyString(), anyString(), anyInt(), anyString(), anyString());
		}

		public TopUpConfirmationInfo topUpMoney() {
			return balanceFacadeMock.topUpMoney(any(BigDecimal.class), anyString(), anyString(), anyInt(), anyString(), anyString());
		}




	}

}
