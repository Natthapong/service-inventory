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

	@RequestMapping(value = "/postGetBill", method = RequestMethod.POST)
	public @ResponseBody ModelAndView getBill(
		   @RequestBody String post) {

		System.out.println("post is : " + post);

		return new ModelAndView("forward:/WEB-INF/test-xml/bill_response.xml");
	}

}
