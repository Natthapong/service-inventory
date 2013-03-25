package th.co.truemoney.serviceinventory.ewallet.client.config;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatus.Series;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.UnknownHttpStatusCodeException;

import com.fasterxml.jackson.databind.ObjectMapper;

import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;

public class ServiceInventoryExceptionResponseErrorHandler extends DefaultResponseErrorHandler {

	public static final ServiceInventoryException SERVICE_NOT_AVAILABLE = new ServiceInventoryException("503", "Service Not Available", "TMN-PRODUCT");

	public void handleError(ClientHttpResponse response) throws IOException {
		HttpStatus statusCode = getHttpStatusCode(response);

		if (statusCode.series() == Series.CLIENT_ERROR || statusCode.series() == Series.SERVER_ERROR) {

			try {
				ObjectMapper objectMapper = new ObjectMapper();
				throw objectMapper.readValue(getResponseBody(response), ServiceInventoryException.class);
			} catch (ServiceInventoryException e1) {
				throw e1;
			} catch (IOException e2) {
				e2.printStackTrace();
				throw SERVICE_NOT_AVAILABLE;
			}
		} else {
			throw new RestClientException("Unknown status code [" + statusCode + "]");
		}
	}

	private HttpStatus getHttpStatusCode(ClientHttpResponse response) throws IOException {
		HttpStatus statusCode;
		try {
			statusCode = response.getStatusCode();
		}
		catch (IllegalArgumentException ex) {
			throw new UnknownHttpStatusCodeException(response.getRawStatusCode(),
					response.getStatusText(), response.getHeaders(), getResponseBody(response), getCharset(response));
		}
		return statusCode;
	}

	private byte[] getResponseBody(ClientHttpResponse response) {
		try {
			InputStream responseBody = response.getBody();
			if (responseBody != null) {
				return FileCopyUtils.copyToByteArray(responseBody);
			}
		}
		catch (IOException ex) {
			// ignore
		}
		return new byte[0];
	}

	private Charset getCharset(ClientHttpResponse response) {
		HttpHeaders headers = response.getHeaders();
		MediaType contentType = headers.getContentType();
		return contentType != null ? contentType.getCharSet() : null;
	}

}
