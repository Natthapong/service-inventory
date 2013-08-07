package th.co.truemoney.serviceinventory.ewallet.repository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import th.co.truemoney.serviceinventory.dao.ExpirableMap;
import th.co.truemoney.serviceinventory.ewallet.domain.AccessToken;
import th.co.truemoney.serviceinventory.ewallet.repositories.impl.AccessTokenRedisRepository;
import th.co.truemoney.serviceinventory.exception.InternalServerErrorException;
import th.co.truemoney.serviceinventory.exception.ResourceNotFoundException;

import com.fasterxml.jackson.databind.ObjectMapper;

public class AccessTokenRedisRepositoryUnitTest {
	
	private ExpirableMap mockRedisLoggingDao;
	
	private AccessTokenRedisRepository accessTokenRedisRepository;

	@Before
	public void setUp() {
		mockRedisLoggingDao = Mockito.mock(ExpirableMap.class);
		accessTokenRedisRepository = new AccessTokenRedisRepository();
		accessTokenRedisRepository.setExpirableMap(mockRedisLoggingDao);
	}
	
	@Test
	public void shouldSaveAccessTokenSuccess() {
		//given
		doNothing().when(mockRedisLoggingDao).addData(anyString(), anyString(), anyLong());
		
		//when
		AccessToken stubbedAccessToken = new AccessToken();
		accessTokenRedisRepository.save(stubbedAccessToken);
		
		//then
		verify(mockRedisLoggingDao).addData(anyString(), anyString(), anyLong());
	}
	
	@Test(expected=InternalServerErrorException.class) 
	public void shouldSaveAccessTokenFail() throws Exception {
		//given
		doThrow(new InternalServerErrorException("Error Code", "Error Description", new Exception())).when(mockRedisLoggingDao).addData(anyString(), anyString(), anyLong());
		
		//when
		AccessToken stubbedAccessToken = new AccessToken();
		accessTokenRedisRepository.save(stubbedAccessToken);
		
		//then
		verify(mockRedisLoggingDao).addData(anyString(), anyString(), anyLong());
	}
	
	@Test
	public void shouldFindAccessTokenSuccess() throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		String accessTokenJsonString = mapper.writeValueAsString(new AccessToken("token", "loginID", "sessionID", "tmnID", 41));
		
		//given
		when(mockRedisLoggingDao.getData(anyString())).thenReturn(accessTokenJsonString);
		
		//when
		String stubbedKey = "key";
		AccessToken accessToken = accessTokenRedisRepository.findAccessToken(stubbedKey);
		
		//then
		assertNotNull(accessToken);
		assertEquals("token", accessToken.getAccessTokenID());
		verify(mockRedisLoggingDao).getData(anyString());
	}
	
	@Test(expected=ResourceNotFoundException.class) 
	public void shouldFindAccessTokenFail() throws Exception {
		//given
		when(mockRedisLoggingDao.getData(anyString())).thenReturn(null);
		
		//when
		String stubbedKey = "key";
		accessTokenRedisRepository.findAccessToken(stubbedKey);
		
		//then
		verify(mockRedisLoggingDao).getData(anyString());
	}
	
	@Test(expected=InternalServerErrorException.class) 
	public void shouldFindAccessTokenFailException() throws Exception {
		//given
		when(mockRedisLoggingDao.getData(anyString())).thenThrow(new InternalServerErrorException("Error Code", "Error Description", new Exception()));
		
		//when
		String stubbedKey = "key";
		accessTokenRedisRepository.findAccessToken(stubbedKey);
		
		//then
		verify(mockRedisLoggingDao).getData(anyString());
	}
	
	@Test
	public void shouldRemoveAccessTokenSuccess() {
		//given
		doNothing().when(mockRedisLoggingDao).delete(anyString());
		
		//when
		String stubbedKey = "key";
		accessTokenRedisRepository.remove(stubbedKey);
		
		//then
		verify(mockRedisLoggingDao).delete(anyString());
	}
	
	@Test(expected=InternalServerErrorException.class) 
	public void shouldRemoveAccessTokenFail() throws Exception {
		//given
		doThrow(new InternalServerErrorException("Error Code", "Error Description", new Exception())).when(mockRedisLoggingDao).delete(anyString());
		
		//when
		String stubbedKey = "key";
		accessTokenRedisRepository.remove(stubbedKey);
		
		//then
		verify(mockRedisLoggingDao).delete(anyString());
	}

	@Test
	public void shouldExtendAccessTokenSuccess() {
		//given
		doNothing().when(mockRedisLoggingDao).setExpire(anyString(), anyLong());
		
		//when
		String stubbedAccessTokenID = "accessTokenID";
		accessTokenRedisRepository.extendAccessToken(stubbedAccessTokenID);
		
		//then
		verify(mockRedisLoggingDao).setExpire(anyString(), anyLong());
	}
	
	@Test(expected=InternalServerErrorException.class) 
	public void shouldExtendAccessTokenFail() {
		//given
		doThrow(new InternalServerErrorException("Error Code", "Error Description", new Exception())).when(mockRedisLoggingDao).setExpire(anyString(), anyLong());
		
		//when
		String stubbedAccessTokenID = "accessTokenID";
		accessTokenRedisRepository.extendAccessToken(stubbedAccessTokenID);
		
		//then
		verify(mockRedisLoggingDao).setExpire(anyString(), anyLong());
	}
	
}
