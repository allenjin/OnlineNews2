package online.news.beans;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import online.news.entity.Passage;
import online.news.util.CallBack;
import online.news.util.UsefulStaticValues;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class OnlineListBean {
	public static final String HTTPCustomer =
			UsefulStaticValues.getIpAddress()+":8080/News2/servlet/PassageListServlet";
	public List<Passage> getPassageList(int id,int page) throws ClientProtocolException,IOException{
		 List<Passage> list =null;
		 String jsonData=null;
		 List<NameValuePair> params=new ArrayList<NameValuePair>();  
        params.add(new BasicNameValuePair("id", ""+id));
        params.add(new BasicNameValuePair("page", ""+page));
		 HttpPost httpRequest =new HttpPost (HTTPCustomer);
//		 try {
				httpRequest.setEntity(new UrlEncodedFormEntity(params,HTTP.UTF_8)); 
				HttpResponse httpResponse=new DefaultHttpClient().execute(httpRequest);
				if(httpResponse.getStatusLine().getStatusCode()==200)  
            {  
                byte[] data = new byte[2048];  
                //String qq=new String();
               // EntityUtils.toString((HttpEntity)httpResponse.getEntity(), qq);
                data =EntityUtils.toByteArray((HttpEntity)httpResponse.getEntity());  
                ByteArrayInputStream bais = new ByteArrayInputStream(data);      
                DataInputStream dis = new DataInputStream(bais);
                jsonData = new String(dis.readUTF());
                //System.out.println("data:"+qq);
            }  
///			} catch (ClientProtocolException e) {
//				e.printStackTrace();
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
		 Type listType = new TypeToken<List<Passage>>() {
	        }.getType();
		 Gson gson = new Gson();
		 list = gson.fromJson(jsonData, listType);
		 return list;
		 
	 }
	/**
	 * get passage list asynchronous
	 * @param blockId block id
	 * @param callback the Callback class*/
	public void getPassageListAsync(final int blockId,final int page,final CallBack<List<Passage>> callback){
		Thread t = new Thread(){
			@Override
			public void run() {
				super.run();
				try {
					callback.onFinish(getPassageList(blockId,page));
				} catch (Exception e) {
					callback.onException(e);
				}
			}
		};
		t.start();
	}
}
