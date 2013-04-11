package th.co.truemoney.serviceinventory.lagacyfacade.client;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class XMLOverHTTPUtil {

	private static Logger logger = LoggerFactory.getLogger(XMLOverHTTPUtil.class);

	private XMLOverHTTPUtil() {
		super();
	}
	
	public static String postData(String target, String content) throws IOException {

		logger.debug("\n\nAbout to post\n\nURL: "+target+ "\n\ncontent: \n\n" + content + "\n\n");
		String response		= "";
		URL url				= new URL(target);
		URLConnection conn = url.openConnection();

		// Set connection parameters.
		conn.setDoInput (true);
		conn.setDoOutput (true);
		conn.setUseCaches (false);
		// Make server believe we are form data...
		conn.setRequestProperty("Content-Type", "text/xml");

		DataOutputStream out	= new DataOutputStream (conn.getOutputStream ());
		// Write out the bytes of the content string to the stream.
		out.writeBytes(content);
		out.flush ();
		out.close ();
		// Read response from the input stream.
		BufferedReader in		= new BufferedReader (new InputStreamReader(conn.getInputStream ()));
		String temp;
		while ((temp = in.readLine()) != null){
			response += temp + "\n";
		}
		temp = null;
		in.close ();
		logger.debug("Server response:\n\n" + response + "\n");
		return response;
	}
	
}
