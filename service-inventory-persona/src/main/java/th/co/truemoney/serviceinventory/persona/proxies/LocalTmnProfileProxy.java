package th.co.truemoney.serviceinventory.persona.proxies;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import th.co.truemoney.serviceinventory.ewallet.exception.EwalletException;
import th.co.truemoney.serviceinventory.ewallet.exception.FailResultCodeException;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.AddFavoriteRequest;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.AddFavoriteResponse;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.CreateTmnProfileRequest;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.CreateTmnProfileResponse;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.DeleteFavoriteRequest;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.FavoriteContext;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.GetBasicProfileResponse;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.IsFavoritableRequest;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.IsFavoritedRequest;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.ListFavoriteRequest;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.ListFavoriteResponse;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.ListSourceRequest;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.ListSourceResponse;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.SourceContext;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.StandardBizRequest;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.StandardBizResponse;
import th.co.truemoney.serviceinventory.ewallet.proxy.tmnprofile.TmnProfileProxy;
import th.co.truemoney.serviceinventory.persona.Adam;
import th.co.truemoney.serviceinventory.persona.Eve;
import th.co.truemoney.serviceinventory.persona.Simpsons;

public class LocalTmnProfileProxy implements TmnProfileProxy {

    private Adam adam = new Adam();
    private Eve eve = new Eve();
    private Simpsons simpsons = new Simpsons();

    private String AdamTmnMoneyID = "AdamTmnMoneyId";
    private String EveTmnMoneyID = "EveTmnMoneyId";
    private String SimpsonsTmnMoneyID = "SimpsonsTmnMoneyId";

    @Override
    public GetBasicProfileResponse getBasicProfile(
            StandardBizRequest standardBizRequest) throws EwalletException {
        if (standardBizRequest.getSecurityContext().getTmnId()
                .equals(AdamTmnMoneyID)) {
            return adam.getTmnProfile().getBasicProfile(standardBizRequest);
        } else if (standardBizRequest.getSecurityContext().getTmnId()
                .equals(EveTmnMoneyID)) {
            return eve.getTmnProfile().getBasicProfile(standardBizRequest);
        } else if (standardBizRequest.getSecurityContext().getTmnId()
                .equals(SimpsonsTmnMoneyID)) {
            return simpsons.getTmnProfile().getBasicProfile(standardBizRequest);
        } else {
            return new GetBasicProfileResponse("1", "0", "namespace",
                    new String[] { "key" }, new String[] { "value" },
                    "username", "local@tmn.com", "0891231234", new BigDecimal(
                            50.0d), "C", 3);
        }
    }

    @Override
    public CreateTmnProfileResponse createTmnProfile(
            CreateTmnProfileRequest createTmnProfileRequest)
            throws EwalletException {
        return new CreateTmnProfileResponse("1", "0", "namespace",
                new String[] { "key" }, new String[] { "value" }, "123123");
    }

    @Override
    public ListSourceResponse listSource(ListSourceRequest listSourceRequest)
            throws EwalletException {

        SourceContext[] sourceContext = new SourceContext[3];
        sourceContext[0] = new SourceContext("1", "type", new String[] { "SCB",
                "xxxx1234" });
        sourceContext[1] = new SourceContext("2", "type", new String[] { "KTB",
                "xxxx5678" });
        sourceContext[2] = new SourceContext("3", "type", new String[] { "BBL",
                "xxxx9101" });

        return new ListSourceResponse("1", "2", "namespace",
                new String[] { "key" }, new String[] { "value" }, sourceContext);
    }

    @Override
    public StandardBizResponse isFavoritable(
            IsFavoritableRequest isIsFavoritableRequest)
            throws EwalletException {
        List<String> serviceCodes = Arrays.asList("tr", "trmv", "tmvh", "tlp",
                "tic", "ti", "tcg", "mea", "tli");
        if (!serviceCodes.contains(isIsFavoritableRequest.getServiceCode())) {
            throw new FailResultCodeException("2013",
                    "stub ADD_FAVORITE_DENIED");
        }
        if (isIsFavoritableRequest.getSecurityContext().getTmnId()
                .equals(AdamTmnMoneyID)) {
            return adam.getTmnProfile().isFavoritable(isIsFavoritableRequest);
        } else if (isIsFavoritableRequest.getSecurityContext().getTmnId()
                .equals(EveTmnMoneyID)) {
            return eve.getTmnProfile().isFavoritable(isIsFavoritableRequest);
        } else if (isIsFavoritableRequest.getSecurityContext().getTmnId()
                .equals(SimpsonsTmnMoneyID)) {
            return simpsons.getTmnProfile().isFavoritable(
                    isIsFavoritableRequest);
        } else {
            return new StandardBizResponse("1", "0", "namespace",
                    new String[] { "key" }, new String[] { "value" });
        }
    }

    @Override
    public AddFavoriteResponse addFavorite(AddFavoriteRequest addFavoriteRequest)
            throws EwalletException {

        FavoriteContext favoriteContext = new FavoriteContext();
        if (addFavoriteRequest.getSecurityContext().getTmnId()
                .equals(AdamTmnMoneyID)) {
            favoriteContext.setFavoriteId("1001");
            return new AddFavoriteResponse("1", "0", "namespace",
                    new String[] { "key" }, new String[] { "value" },
                    favoriteContext);
        } else if (addFavoriteRequest.getSecurityContext().getTmnId()
                .equals(EveTmnMoneyID)) {
            favoriteContext.setFavoriteId("1002");
            return new AddFavoriteResponse("1", "0", "namespace",
                    new String[] { "key" }, new String[] { "value" },
                    favoriteContext);
        } else if (addFavoriteRequest.getSecurityContext().getTmnId()
                .equals(SimpsonsTmnMoneyID)) {
            favoriteContext.setFavoriteId("1003");
            return new AddFavoriteResponse("1", "0", "namespace",
                    new String[] { "key" }, new String[] { "value" },
                    favoriteContext);
        } else {
            throw new FailResultCodeException("500", "stub namespace.");
        }
    }

    @Override
    public ListFavoriteResponse listFavorite(
            ListFavoriteRequest listFavoriteRequest) throws EwalletException {

        FavoriteContext[] favoriteContexts = new FavoriteContext[4];
        favoriteContexts[0] = new FavoriteContext("1", "billpay", "trmv", "Truemove",
                new BigDecimal("13.00"), "20000211101010");
        favoriteContexts[1] = new FavoriteContext("2", "billpay", "tmvh", "Truemove H",
                new BigDecimal("19.00"), "20000212101010");
        favoriteContexts[2] = new FavoriteContext("3", "billpay", "tlp", "Truelife plus",
                new BigDecimal("18.00"), "20000210101010");
        favoriteContexts[3] = new FavoriteContext("3", "billpay", "mea", "Metro Electric",
                new BigDecimal("20.00"), "20000210101010");


        return new ListFavoriteResponse("1", "0", "namespace",
                new String[] { "key" }, new String[] { "value" },
                favoriteContexts);
    }

    @Override
    public StandardBizResponse isFavorited(IsFavoritedRequest isFavoritedRequest)
            throws EwalletException {
        if (isFavoritedRequest.getSecurityContext().getTmnId()
                .equals(AdamTmnMoneyID)) {
            return adam.getTmnProfile().isFavorited(isFavoritedRequest);
        } else if (isFavoritedRequest.getSecurityContext().getTmnId()
                .equals(EveTmnMoneyID)) {
            return eve.getTmnProfile().isFavorited(isFavoritedRequest);
        } else if (isFavoritedRequest.getSecurityContext().getTmnId()
                .equals(SimpsonsTmnMoneyID)) {
            return simpsons.getTmnProfile().isFavorited(isFavoritedRequest);
        } else {
            return new StandardBizResponse("1", "0", "namespace",
                    new String[] { "key" }, new String[] { "value" });
        }
    }

    @Override
    public StandardBizResponse removeFavorite(
            DeleteFavoriteRequest removeFavoriteRequest)
            throws EwalletException {
        if (removeFavoriteRequest.getSecurityContext().getTmnId()
                .equals(AdamTmnMoneyID)) {
            return adam.getTmnProfile().removeFavorite(removeFavoriteRequest);
        } else if (removeFavoriteRequest.getSecurityContext().getTmnId()
                .equals(EveTmnMoneyID)) {
            return eve.getTmnProfile().removeFavorite(removeFavoriteRequest);
        } else if (removeFavoriteRequest.getSecurityContext().getTmnId()
                .equals(SimpsonsTmnMoneyID)) {
            return simpsons.getTmnProfile().removeFavorite(
                    removeFavoriteRequest);
        } else {
            return new StandardBizResponse("1", "0", "namespace",
                    new String[] { "key" }, new String[] { "value" });
        }
    }

}
