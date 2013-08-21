package th.co.truemoney.serviceinventory.controller;

import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import th.co.truemoney.serviceinventory.buy.BuyProductService;
import th.co.truemoney.serviceinventory.buy.domain.BuyProduct;
import th.co.truemoney.serviceinventory.buy.domain.BuyProductDraft;
import th.co.truemoney.serviceinventory.buy.domain.BuyProductTransaction;
import th.co.truemoney.serviceinventory.ewallet.impl.ExtendAccessTokenAsynService;

@Controller
@RequestMapping(value="/buy/product")
public class BuyProductController {
	
    private static final String MDC_TRANSACTION_ID = "transactionID";

    private static final String MDC_DRAFT_TRANSACTION_ID = "draftTransactionID";
    
    @Autowired
    private BuyProductService buyProductService;

    @Autowired
    private ExtendAccessTokenAsynService extendAccessTokenAsynService;
    
    @RequestMapping(value = "/draft", method = RequestMethod.POST)
    public @ResponseBody BuyProductDraft createBuyProductDraft(
            @RequestParam(value = "accessTokenID", defaultValue = "") String accessTokenID,
            @RequestBody BuyProductDraft draft) {

        extendExpireAccessToken(accessTokenID);

        BuyProduct buyProduct = draft.getBuyProductInfo();
        
        BuyProductDraft buyProductDraft = buyProductService.createAndVerifyBuyProductDraft(buyProduct.getTarget(), draft.getRecipientMobileNumber(), buyProduct.getAmount(), accessTokenID);

        MDC.put(MDC_DRAFT_TRANSACTION_ID, buyProductDraft.getID());

        return buyProductDraft;
    }
    
    @RequestMapping(value = "/draft/{buyProductDraftID}", method = RequestMethod.GET)
    public @ResponseBody BuyProductDraft getBuyProductDraftDetail(
            @PathVariable String buyProductDraftID,
            @RequestParam(value = "accessTokenID", defaultValue = "") String accessTokenID) {

        extendExpireAccessToken(accessTokenID);

        MDC.put(MDC_DRAFT_TRANSACTION_ID, buyProductDraftID);

        return buyProductService.getBuyProductDraftDetails(buyProductDraftID, accessTokenID);
    }
    
    @RequestMapping(value = "/transaction/{buyProductDraftID}", method = RequestMethod.PUT)
    public @ResponseBody BuyProductTransaction.Status performBuyProduct(
            @PathVariable String buyProductDraftID,
            @RequestParam(value = "accessTokenID", defaultValue = "") String accessTokenID) {

        extendExpireAccessToken(accessTokenID);

        MDC.put(MDC_DRAFT_TRANSACTION_ID, buyProductDraftID);

        return buyProductService.performBuyProduct(buyProductDraftID, accessTokenID);
    }
    
    @RequestMapping(value = "/transaction/{transactionID}/status", method = RequestMethod.GET)
    public @ResponseBody BuyProductTransaction.Status getBuyProductStatus(
            @PathVariable String transactionID,
            @RequestParam(value = "accessTokenID", defaultValue = "") String accessTokenID) {

        extendExpireAccessToken(accessTokenID);

        MDC.put(MDC_TRANSACTION_ID, transactionID);

        return buyProductService.getBuyProductStatus(transactionID, accessTokenID);
    }
    
    @RequestMapping(value = "/transaction/{transactionID}", method = RequestMethod.GET)
    public @ResponseBody BuyProductTransaction getBuyProductResult(
            @PathVariable String transactionID,
            @RequestParam(value = "accessTokenID", defaultValue = "") String accessTokenID) {

        extendExpireAccessToken(accessTokenID);

        MDC.put(MDC_TRANSACTION_ID, transactionID);

        return buyProductService.getBuyProductResult(transactionID, accessTokenID);
    }
    
    private void extendExpireAccessToken(String accessTokenID) {
        extendAccessTokenAsynService.setExpire(accessTokenID);
    }
    
}
