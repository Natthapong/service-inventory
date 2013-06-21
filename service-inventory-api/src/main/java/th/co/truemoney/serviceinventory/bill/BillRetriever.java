package th.co.truemoney.serviceinventory.bill;

import java.math.BigDecimal;

import th.co.truemoney.serviceinventory.bill.domain.Bill;


public interface BillRetriever {

    public Bill getOfflineBillInfoByScanningBarcode(String barcode, String accessTokenID);

    public Bill getOfflineBillInfoByKeyInBillCode(String billCode, String ref1, String ref2, BigDecimal amount, String accessTokenID);
    public Bill getOnlineBillInfoByKeyInBillCode(String billCode, String ref1, String ref2, String accessTokenID);

    public Bill getOfflineBillInfoFromUserFavorited(Long favoriteID, BigDecimal amount, String accessTokenID);
    public Bill getOnlineBillInfoFromUserFavorited(Long favoriteID, String accessTokenID);
}
