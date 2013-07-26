package th.co.truemoney.serviceinventory.ewallet.repositories.impl;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;

import th.co.truemoney.serviceinventory.dao.ExpirableMap;
import th.co.truemoney.serviceinventory.ewallet.domain.OTP;
import th.co.truemoney.serviceinventory.ewallet.repositories.OTPRepository;
import th.co.truemoney.serviceinventory.exception.InternalServerErrorException;
import th.co.truemoney.serviceinventory.exception.ResourceNotFoundException;

import static th.co.truemoney.serviceinventory.exception.ServiceInventoryWebException.Code.GENERAL_ERROR;
import static th.co.truemoney.serviceinventory.exception.ServiceInventoryWebException.Code.OTP_NOT_FOUND;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class OTPRedisRepository implements OTPRepository {
	
	private static final Long OTP_TTL = 15L; //Time-To-Live in minutes
	
	private ObjectMapper mapper = new ObjectMapper();
	
	@Autowired
	private ExpirableMap repository;

	@Override
	public void save(OTP otp) {
		String value;
		try {
			value = mapper.writeValueAsString(otp);
		} catch (JsonProcessingException e) {
			throw new InternalServerErrorException(GENERAL_ERROR, e.getMessage(), e);
		}
		repository.addData(createKey(otp), value, OTP_TTL);
	}

	@Override
	public OTP findOTPByRefCode(String mobileNumber, String refCode) {
		String value = repository.getData(createKey(mobileNumber, refCode));
		if (value == null) {
			throw new ResourceNotFoundException(OTP_NOT_FOUND, "OTP not found.");
		} else {
			try {
				return mapper.readValue(value, OTP.class);
			} catch (JsonProcessingException je) {
				throw new InternalServerErrorException(GENERAL_ERROR, je.getMessage(), je);
			} catch (IOException ie) {
				throw new InternalServerErrorException(GENERAL_ERROR, ie.getMessage(), ie);
			}
		}
	}
	
	private String createKey(OTP otp) {
		return createKey(otp.getMobileNumber(), otp.getReferenceCode());
	}
	
	private String createKey(String mobileNumber, String referenceCode) {
		return "otp:" + mobileNumber + ":" + referenceCode;
	}

}
