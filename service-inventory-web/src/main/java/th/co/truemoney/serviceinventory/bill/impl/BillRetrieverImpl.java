package th.co.truemoney.serviceinventory.bill.impl;


import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import th.co.truemoney.serviceinventory.bill.BillRetriever;
import th.co.truemoney.serviceinventory.bill.domain.Bill;
import th.co.truemoney.serviceinventory.bill.domain.OutStandingBill;
import th.co.truemoney.serviceinventory.bill.validation.BillValidator;
import th.co.truemoney.serviceinventory.ewallet.FavoriteService;
import th.co.truemoney.serviceinventory.ewallet.domain.AccessToken;
import th.co.truemoney.serviceinventory.ewallet.domain.ClientCredential;
import th.co.truemoney.serviceinventory.ewallet.domain.Favorite;
import th.co.truemoney.serviceinventory.ewallet.repositories.AccessTokenRepository;
import th.co.truemoney.serviceinventory.ewallet.repositories.BillInformationRepository;
import th.co.truemoney.serviceinventory.exception.ResourceNotFoundException;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryWebException.Code;
import th.co.truemoney.serviceinventory.legacyfacade.LegacyFacade;

public class BillRetrieverImpl implements BillRetriever {

    private static final String PAYWITH_FAVORITE = "favorite";

    private static final String PAYWITH_BARCODE = "barcode";

    private static final String PAYWITH_KEYIN = "keyin";

    @Autowired
    private AccessTokenRepository accessTokenRepo;

    @Autowired
    private BillInformationRepository billInfoRepo;

    @Autowired
    private LegacyFacade legacyFacade;

    @Autowired
    private BillValidator billValidator;

    @Autowired
    private FavoriteService favoriteService;

    @Override
    public Bill getOfflineBillInfoByScanningBarcode(String barcode, String accessTokenID) {

        AccessToken token = accessTokenRepo.findAccessToken(accessTokenID);

        Bill bill = retrieveBillByBarcode(barcode, token);
        bill.setPayWith(PAYWITH_BARCODE);

        return saveBill(bill, accessTokenID);
    }

    @Override
    public Bill getOfflineBillInfoByKeyInBillCode(String billCode, String ref1, String ref2, BigDecimal amount, String accessTokenID) {

        AccessToken token = accessTokenRepo.findAccessToken(accessTokenID);

        Bill bill = retrieveBillByBillCodeOffline(billCode, ref1, ref2, amount, token);
        bill.setPayWith(PAYWITH_KEYIN);

        return saveBill(bill, accessTokenID);
    }

    @Override
    public Bill getOnlineBillInfoByKeyInBillCode(String billCode, String ref1, String ref2, String accessTokenID) {
        AccessToken token = accessTokenRepo.findAccessToken(accessTokenID);

        Bill bill = retrieveBillByBillCodeOnline(billCode, ref1, ref2, token);
        bill.setPayWith(PAYWITH_KEYIN);

        return saveBill(bill, accessTokenID);
    }

    @Override
    public Bill getOfflineBillInfoFromUserFavorited(Long favoriteID, BigDecimal amount, String accessTokenID) {

        AccessToken token = accessTokenRepo.findAccessToken(accessTokenID);

        Favorite favBill = findFavoriteBill(favoriteID, accessTokenID);

        String billCode = favBill.getServiceCode();
        String ref1 = favBill.getRef1();
        String ref2 = favBill.getRef2();

        Bill bill = retrieveBillByBillCodeOffline(billCode, ref1, ref2, amount, token);
        bill.setPayWith(PAYWITH_FAVORITE);

        return saveBill(bill, accessTokenID);
    }

    @Override
    public Bill getOnlineBillInfoFromUserFavorited(Long favoriteID, String accessTokenID) {

        AccessToken token = accessTokenRepo.findAccessToken(accessTokenID);

        Favorite favBill = findFavoriteBill(favoriteID, accessTokenID);

        String billCode = favBill.getServiceCode();
        String ref1 = favBill.getRef1();
        String ref2 = favBill.getRef2();

        Bill bill = retrieveBillByBillCodeOnline(billCode, ref1, ref2, token);
        bill.setPayWith(PAYWITH_FAVORITE);

        return saveBill(bill, accessTokenID);
    }

    public void setFavoriteService(FavoriteService favoriteService) {
        this.favoriteService = favoriteService;
    }

    public void setValidator(BillValidator billValidator) {
        this.billValidator = billValidator;
    }

    private void validateOverdue(Bill bill) {
        billValidator.validateOverDue(bill);
    }

    private Bill saveBill(Bill bill, String accessTokenID) {
        bill.setID(UUID.randomUUID().toString());
        billInfoRepo.saveBill(bill, accessTokenID);

        return bill;
    }

    private Bill retrieveBillByBillCodeOffline(String billCode, String ref1, String ref2, BigDecimal amount, AccessToken accessToken) {

        ClientCredential appData = accessToken.getClientCredential();

        Bill bill = legacyFacade.billing()
                                .readBillInfoWithBillCode(billCode)
                                .fromApp(appData.getAppUser(), appData.getAppPassword(), appData.getAppKey())
                                .fromBillChannel(appData.getChannel(), appData.getChannelDetail())
                                .read();

        bill.setRef1(ref1);
        bill.setRef2(ref2);
        bill.setAmount(amount);

        validateOverdue(bill);

        return bill;
    }

    private Bill retrieveBillByBarcode(String barcode, AccessToken accessToken) {

        ClientCredential appData = accessToken.getClientCredential();

        Bill bill = legacyFacade.billing()
                                .readBillInfoWithBarcode(barcode)
                                .fromApp(appData.getAppUser(), appData.getAppPassword(), appData.getAppKey())
                                .fromBillChannel(appData.getChannel(), appData.getChannelDetail())
                                .read();

        validateOverdue(bill);

        return bill;
    }

    private Bill retrieveBillByBillCodeOnline(String billCode, String ref1, String ref2, AccessToken accessToken)
            throws ServiceInventoryException {

        ClientCredential appData = accessToken.getClientCredential();

        OutStandingBill outStandingBill =  legacyFacade.billing()
                                 .readBillOutStandingOnlineWithBillCode(billCode)
                                 .fromRef1(ref1)
                                 .fromOperateByStaff(accessToken.getMobileNumber())
                                 .fromApp(appData.getAppUser(), appData.getAppPassword(), appData.getAppKey())
                                 .fromBillChannel(appData.getChannel(), appData.getChannelDetail())
                                 .getBillOutStandingOnline();

        Bill bill = new Bill();
        String newRef1 = StringUtils.hasText(outStandingBill.getRef1()) ? outStandingBill.getRef1() : ref1;
        String newRef2 = StringUtils.hasText(outStandingBill.getRef2()) ? outStandingBill.getRef2() : ref2;

        bill.setRef1(newRef1);
        bill.setRef2(newRef2);
        bill.setAmount(outStandingBill.getOutStandingBalance());
        bill.setDueDate(outStandingBill.getDueDate());

        validateOverdue(bill);

        return bill;
    }

    private Favorite findFavoriteBill(Long favoriteID, String accessTokenID) {
        List<Favorite> favorites = favoriteService.getFavorites(accessTokenID);
        if (favorites != null) {
            for (Favorite fav : favorites) {
                if (fav.getFavoriteID().equals(favoriteID) && "billpay".equals(fav.getServiceType())) {
                    return fav;
                }
            }
        }

        throw new ResourceNotFoundException(Code.FAVORITE_SERVICE_CODE_NOT_INLIST, "favorite bill not found: " + favoriteID);
    }

}
