package th.co.truemoney.serviceinventory.controller;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import th.co.truemoney.serviceinventory.bill.BillRetriever;
import th.co.truemoney.serviceinventory.bill.domain.Bill;
import th.co.truemoney.serviceinventory.ewallet.impl.ExtendAccessTokenAsynService;

@Controller
@RequestMapping(value = "/bill-payment/bill")
public class BillRetrieverController {

    @Autowired
    private BillRetriever retriever;

    @Autowired
    private ExtendAccessTokenAsynService extendAccessTokenAsynService;

    @RequestMapping(value = "/information", method = RequestMethod.GET, params="barcode")
    public @ResponseBody Bill getBillInfoFromBarcode(
            @RequestParam(value = "barcode", defaultValue = "") String barcode,
            @RequestParam(value = "accessTokenID", defaultValue = "") String accessTokenID) {

        extendExpireAccessToken(accessTokenID);
        return retriever.getOfflineBillInfoByScanningBarcode(barcode, accessTokenID);
    }

    @RequestMapping(value = "/information", method = RequestMethod.GET, params={"billCode", "ref1", "ref2", "amount"})
    public @ResponseBody Bill getBillInfoByBillCodeOffline(
            @RequestParam(value = "billCode") String billCode,
            @RequestParam(value = "ref1") String ref1,
            @RequestParam(value = "ref2") String ref2,
            @RequestParam(value = "amount") BigDecimal amount,
            @RequestParam(value = "accessTokenID", defaultValue = "") String accessTokenID) {

        extendExpireAccessToken(accessTokenID);
        return retriever.getOfflineBillInfoByKeyInBillCode(billCode, ref1, ref2, amount, accessTokenID);
    }

    @RequestMapping(value = "/information", method = RequestMethod.GET, params={"billCode", "ref1", "ref2", "!amount", "inquiry=online"})
    public @ResponseBody Bill getBillInfoByBillCodeOnline(
            @RequestParam(value = "billCode") String billCode,
            @RequestParam(value = "ref1") String ref1,
            @RequestParam(value = "ref2") String ref2,
            @RequestParam(value = "accessTokenID", defaultValue = "") String accessTokenID) {

        extendExpireAccessToken(accessTokenID);
        return retriever.getOnlineBillInfoByKeyInBillCode(billCode, ref1, ref2, accessTokenID);
    }

    @RequestMapping(value = "/information", method = RequestMethod.GET, params={"favoriteID", "amount"})
    public @ResponseBody Bill getBillInfoFromUserFavoriteOffline(
            @RequestParam(value = "favoriteID", defaultValue = "") Long favoriteID,
            @RequestParam(value = "amount", defaultValue = "") BigDecimal amount,
            @RequestParam(value = "accessTokenID", defaultValue = "") String accessTokenID) {

        extendExpireAccessToken(accessTokenID);
        return retriever.getOfflineBillInfoFromUserFavorited(favoriteID, amount, accessTokenID);
    }

    @RequestMapping(value = "/information", method = RequestMethod.GET, params={"favoriteID", "inquiry=online"})
    public @ResponseBody Bill getBillInfoFromUserFavoriteOnline(
            @RequestParam(value = "favoriteID", defaultValue = "") Long favoriteID,
            @RequestParam(value = "accessTokenID", defaultValue = "") String accessTokenID) {

        extendExpireAccessToken(accessTokenID);
        return retriever.getOnlineBillInfoFromUserFavorited(favoriteID, accessTokenID);
    }

    private void extendExpireAccessToken(String accessTokenID) {
        extendAccessTokenAsynService.setExpire(accessTokenID);
    }


}
