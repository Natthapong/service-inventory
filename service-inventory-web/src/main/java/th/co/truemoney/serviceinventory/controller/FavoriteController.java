package th.co.truemoney.serviceinventory.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import th.co.truemoney.serviceinventory.ewallet.FavoriteService;
import th.co.truemoney.serviceinventory.ewallet.domain.Favorite;
import th.co.truemoney.serviceinventory.ewallet.impl.ExtendAccessTokenAsynService;

@Controller
@RequestMapping(value="/ewallet")
public class FavoriteController {

	@Autowired
	private FavoriteService favoriteService;

	@Autowired
	private ExtendAccessTokenAsynService extendAccessTokenAsynService;

	
	@RequestMapping(value = "/favorites/{accessTokenID}" , method = RequestMethod.POST)
	public @ResponseBody Favorite addFavorite(
			@RequestBody Favorite favorite,
			@PathVariable String accessTokenID) {
		
		extendExpireAccessToken(accessTokenID);

		Favorite favoriteResponse = favoriteService.addFavorite(favorite, accessTokenID);
		
		return favoriteResponse;
	}
	
	@RequestMapping(value = "/favorites" , method = RequestMethod.DELETE)
	public @ResponseBody Boolean removeFavorite(
			@RequestParam(value = "serviceCode", defaultValue="") String serviceCode,
			@RequestParam(value = "ref1", defaultValue="") String ref1,
			@RequestParam(value = "accessTokenID", defaultValue="") String accessTokenID) {
		
		extendExpireAccessToken(accessTokenID);

		return favoriteService.deleteFavorite(serviceCode, ref1, accessTokenID);
	}
	
	@RequestMapping(value = "/favorites" , method = RequestMethod.GET)
	public @ResponseBody List<Favorite> getFavorites(
			@RequestParam(value = "accessTokenID", defaultValue="") String accessTokenID) {

		extendExpireAccessToken(accessTokenID);
		
		List<Favorite> favorites = favoriteService.getFavorites(accessTokenID);
		
		return favorites;
	}
	
	private void extendExpireAccessToken(String accessTokenID) {
		extendAccessTokenAsynService.setExpire(accessTokenID);
	}

}
