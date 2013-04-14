package th.co.truemoney.serviceinventory.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;



@Controller
@RequestMapping(value="/test")
public class PostController {

	@RequestMapping(value = "/postGetBill", method = RequestMethod.POST, consumes="application/xml")
	public @ResponseBody ModelAndView getBill(
		   @RequestBody String post) {

		System.out.println("get bill : request is : " + post);

		return new ModelAndView("forward:/WEB-INF/test-xml/bill_response.xml");
	}

	@RequestMapping(value = "/postVerifyBill", method = RequestMethod.POST, consumes="application/xml")
	public @ResponseBody ModelAndView verifyBill(
		   @RequestBody String post) {

		System.out.println("verify: request is : " + post);

		return new ModelAndView("forward:/WEB-INF/test-xml/verify_bill_response.xml");
	}

	@RequestMapping(value = "/postConfirmBill", method = RequestMethod.POST)
	public @ResponseBody ModelAndView confirmBill(
		   @RequestBody String post) {

		System.out.println("confirm: request is : " + post);

		return new ModelAndView("forward:/WEB-INF/test-xml/confirm_bill_response.xml");
	}

}
