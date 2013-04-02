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

	public static final ServiceInventoryException SERVICE_NOT_AVAILABLE = new ServiceInventoryException(503, "503", "Service Not Available", "TMN-PRODUCT");

	public void handleError(ClientHttpResponse response) throws IOException {
		HttpStatus statusCode = getHttpStatusCode(response);

		if (statusCode.series() == Series.CLIENT_ERROR || statusCode.series() == Series.SERVER_ERROR) {

			try {
				ObjectMapper objectMapper = new ObjectMapper();
				throw objectMapper.readValue(getResponseBody(response), ServiceInventoryException.class);
			} catch (IOException ex) {
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

		InputStream responseBody = null;
		try {
			responseBody = response.getBody();
			if (responseBody != null) {
				return FileCopyUtils.copyToByteArray(responseBody);
			}
		}
		catch (IOException ex) {
			// ignore
		} finally {
			if (responseBody != null) {
				try {
					responseBody.close();
				} catch (IOException e) {
					// ignore
				}
			}
		}
		return new byte[0];
	}

	private Charset getCharset(ClientHttpResponse response) {
		HttpHeaders headers = response.getHeaders();
		MediaType contentType = headers.getContentType();
		return contentType != null ? contentType.getCharSet() : null;
	}

}
