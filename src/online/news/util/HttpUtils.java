package online.news.util;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpUtils {

	public static InputStream getStreamFromURL(String imageURL) throws Exception {
			URL url=new URL(imageURL);
			HttpURLConnection conn=(HttpURLConnection)url.openConnection();
			conn.setConnectTimeout(5*1000);
			conn.setRequestMethod("GET");	
		return conn.getInputStream();
	}
	
}
