package th.co.truemoney.serviceinventory.ewallet.repositories.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import th.co.truemoney.serviceinventory.dao.ExpirableMap;
import th.co.truemoney.serviceinventory.ewallet.domain.OTP;
import th.co.truemoney.serviceinventory.ewallet.repositories.OTPRepository;
import th.co.truemoney.serviceinventory.exception.InternalServerErrorException;
import th.co.truemoney.serviceinventory.exception.ResourceNotFoundException;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryWebException.Code;

import com.fasterxml.jackson.databind.ObjectMapper;

public class OTPRedisRepository implements OTPRepository {

	ObjectMapper mapper = new ObjectMapper();

	@Autowired
	private ExpirableMap redisLoggingDao;

	@Override
	public void save(OTP otp) {
		try {
			redisLoggingDao.addData(createKey(otp.getMobileNumber(), otp.getReferenceCode()), mapper.writeValueAsString(otp), 15L);
		} catch (Exception e) {
			throw new InternalServerErrorException(Code.GENERAL_ERROR, "Can not store data in repository.", e);
		}

	}

	@Override
	public OTP findOTPByRefCode(String mobileNumber, String refCode) {
		OTP otp = null;
		String result = null;
		try {
			result = redisLoggingDao.getData(createKey(mobileNumber, refCode));
		} catch (Exception e) {
			e.printStackTrace();
			throw new InternalServerErrorException(Code.GENERAL_ERROR, "Can not read data in repository.", e);
		}
		if(StringUtils.isEmpty(result)) {
			throw new ResourceNotFoundException(Code.OTP_NOT_FOUND, "OTP not found.");
		}
		try {
			otp = mapper.readValue(result, OTP.class);
		} catch (Exception e) {
			e.printStackTrace();
			throw new InternalServerErrorException(Code.GENERAL_ERROR, "Can not map data from repository.", e);
		}
		return otp;
	}

	private String createKey(String mobileNumber, String referenceCode) {
		return "otp:" + mobileNumber + ":" + referenceCode;
	}

}
