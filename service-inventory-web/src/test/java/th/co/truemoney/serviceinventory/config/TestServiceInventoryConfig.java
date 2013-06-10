package th.co.truemoney.serviceinventory.config;

import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import th.co.truemoney.serviceinventory.authen.TransactionAuthenService;
import th.co.truemoney.serviceinventory.bill.BillPaymentService;
import th.co.truemoney.serviceinventory.bill.BillRetriever;
import th.co.truemoney.serviceinventory.ewallet.ActivityService;
import th.co.truemoney.serviceinventory.ewallet.EnhancedDirectDebitSourceOfFundService;
import th.co.truemoney.serviceinventory.ewallet.FavoriteService;
import th.co.truemoney.serviceinventory.ewallet.TmnProfileService;
import th.co.truemoney.serviceinventory.ewallet.TopUpService;
import th.co.truemoney.serviceinventory.ewallet.impl.ExtendAccessTokenAsynService;
import th.co.truemoney.serviceinventory.sms.OTPGenerator;
import th.co.truemoney.serviceinventory.sms.UnSecureOTPGenerator;
import th.co.truemoney.serviceinventory.topup.TopUpMobileService;
import th.co.truemoney.serviceinventory.transfer.P2PTransferService;


@Configuration
public class TestServiceInventoryConfig {

    @Bean @Scope("singleton")
    public BillPaymentService billPaymentServiceMock(){
        return Mockito.mock(BillPaymentService.class);
    }

    @Bean @Scope("singleton")
    public BillRetriever billRetrieverMock(){
        return Mockito.mock(BillRetriever.class);
    }

    @Bean @Scope("singleton")
    public TmnProfileService tmnProfileServiceMock() {
        return Mockito.mock(TmnProfileService.class);
    }

    @Bean @Scope("singleton")
    public EnhancedDirectDebitSourceOfFundService getSourceOfFundService() {
        return Mockito.mock(EnhancedDirectDebitSourceOfFundService.class);
    }

    @Bean @Scope("singleton")
    public TopUpService mockTopUpService() {
        return Mockito.mock(TopUpService.class);
    }

    @Bean @Scope("singleton")
    public P2PTransferService mockP2PTransferService() {
        return Mockito.mock(P2PTransferService.class);
    }

    @Bean @Scope("singleton")
    public TopUpMobileService mockTopUpMobileService() {
        return Mockito.mock(TopUpMobileService.class);
    }

    @Bean @Scope("singleton")
    public ActivityService mockActivityService() {
        return Mockito.mock(ActivityService.class);
    }

    @Bean @Scope("singleton")
    public FavoriteService mockFavoriteService() {
        return Mockito.mock(FavoriteService.class);
    }

    @Bean @Scope("singleton")
    public TransactionAuthenService mockTransactionAuthenService() {
        return Mockito.mock(TransactionAuthenService.class);
    }

    @Bean
    public ExtendAccessTokenAsynService mockExtendAccessTokenAsynService() {
        return Mockito.mock(ExtendAccessTokenAsynService.class);
    }

    @Bean
    public OTPGenerator otpGenerator() {
        return new UnSecureOTPGenerator();
    }
}
