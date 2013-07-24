package th.co.truemoney.serviceinventory.ewallet.repositories.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import th.co.truemoney.serviceinventory.dao.ExpirableMap;
import th.co.truemoney.serviceinventory.ewallet.domain.ResetPassword;
import th.co.truemoney.serviceinventory.ewallet.repositories.ForgotPasswordRepository;
import th.co.truemoney.serviceinventory.exception.InternalServerErrorException;
import th.co.truemoney.serviceinventory.exception.ResourceNotFoundException;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryWebException.Code;

import com.fasterxml.jackson.databind.ObjectMapper;

public class ForgotPasswordRedisRepository implements ForgotPasswordRepository {

	private static Logger logger = LoggerFactory.getLogger(ForgotPasswordRedisRepository.class);

	private ObjectMapper mapper = new ObjectMapper();

	@Autowired
	private ExpirableMap redisLoggingDao;

	@Override
	public ResetPassword findResetPassword(String resetPasswordToken) {

		try {
			String result = redisLoggingDao.getData(resetPasswordToken);
			if(result == null) {
				throw new ResourceNotFoundException(Code.RESET_PASSWORD_TOKEN_NOT_FOUND, "reset password token not found.");
			}
			return mapper.readValue(result, ResetPassword.class);
		} catch (ResourceNotFoundException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new InternalServerErrorException(Code.GENERAL_ERROR, "Can not store data in repository.", e);
		}
	}

	@Override
	public void saveResetPassword(String resetPasswordToken, ResetPassword resetPassword) {
		if (resetPassword != null) {
			try {
				redisLoggingDao.addData(resetPasswordToken, mapper.writeValueAsString(resetPassword), 1440L);
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
				throw new InternalServerErrorException(Code.GENERAL_ERROR, "Can not store data in repository.", e);
			}
		}
	}

}
