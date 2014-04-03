package online.news.beans;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.util.ArrayList;
import java.util.List;
import online.news.util.CallBack;
import online.news.util.FileManager;
import online.news.util.UsefulStaticValues;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

public class OnlineContentBean {
	public static final String HTTPCustomer =
			UsefulStaticValues.getIpAddress()+":8080/News2/servlet/PassageContentServlet";
	   String content;
      public String getContentById(int pid)throws Exception{
   	   List<NameValuePair> params=new ArrayList<NameValuePair>();  
          params.add(new BasicNameValuePair("pid", ""+pid));  
   	   HttpPost httpRequest =new HttpPost (HTTPCustomer);
 				httpRequest.setEntity(new UrlEncodedFormEntity(params,HTTP.UTF_8)); 
 				HttpResponse httpResponse=new DefaultHttpClient().execute(httpRequest);
 				if(httpResponse.getStatusLine().getStatusCode()==200)  
              {  
                  byte[] data = new byte[2048];  
                  data =EntityUtils.toByteArray((HttpEntity)httpResponse.getEntity());  
                  ByteArrayInputStream bais = new ByteArrayInputStream(data);      
                  DataInputStream dis = new DataInputStream(bais);
                  content = new String(dis.readUTF());
                  //System.out.println("data:"+jsonData);
              }  
   	   return content;
      }
      public void getContentByIdAsync(final int pid,final CallBack<String> callback){
   	   Thread t = new Thread(){
			@Override
			public void run() {
					super.run();
					try {
						//first search the file ,if not exists,then request the web and store it.
						if(FileManager.existSDCard()){
  							String path = FileManager.exists(pid+"");
  	  						if(path == null){
  	  							String c= getContentById(pid);
  	  							FileManager.createFile(pid+"",c);
  	  						}
  	  						callback.onFinish(FileManager.readFile(pid+""));
  						}else{
  							String content= getContentById(pid);
  							callback.onFinish(content);
  						}
					}  catch (Exception e) {
						callback.onException(e);
					}
				}
   	   };
   	   t.start();
      }
}
