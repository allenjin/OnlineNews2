package online.news.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.util.HashMap;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ImageView;

public class AsyncBitmapLoader {
	 /** 
     * 内存图片软引用缓冲 
     */  
	  

    private HashMap<String, SoftReference<Bitmap>> imageCache = null;  
    public AsyncBitmapLoader(){  
        imageCache = new HashMap<String, SoftReference<Bitmap>>();
    }   
    public void loadBitmap(final ImageView imageView, final String imageURL, final ImageCallBack imageCallBack){  
    	   final Handler handler = new Handler(){  
               @Override  
               public void handleMessage(Message msg){  
                   imageCallBack.imageLoad(imageView, (Bitmap)msg.obj);  
               }  
           };  
        //在内存缓存中，则返回Bitmap对象  
        if(imageCache.containsKey(imageURL)){  
            SoftReference<Bitmap> reference = imageCache.get(imageURL);  
            Bitmap bitmap = reference.get();  
            if(bitmap != null){  
            	 Message msg = handler.obtainMessage();
            	 msg.obj=bitmap;
                 handler.sendMessage(msg); 
            	}  
        }else {  
        	boolean isExist=false;
        	//在SD卡中进行查找
        	if(FileManager.existSDCard()){
        		String bitmapName = imageURL.substring(imageURL.lastIndexOf("/") + 1);  
                Log.v(FileManager.PHOTOS_PATH, bitmapName);
                File cacheDir = new File(FileManager.PHOTOS_PATH);  
                File[] cacheFiles = cacheDir.listFiles(); 
                for(int i=0;i<cacheFiles.length;i++){
                	if(bitmapName.equals(cacheFiles[i].getName())){  
                		isExist=true;
                		Log.v("asynbitmapLoader",cacheFiles[i].getName());
                		Bitmap bitmap=BitmapFactory.decodeFile(FileManager.PHOTOS_PATH + bitmapName); 
                        Message msg = handler.obtainMessage();
                        msg.obj=bitmap;
                        handler.sendMessage(msg); 
                        break;  
                    }  
                }
        	}
        	//如果不在内存缓存中，也不在本地（被jvm回收掉），则开启线程下载图片  
        	if(!isExist){
        		new Thread(){  
        	        @Override  
        	        public void run(){  
        	           InputStream bitmapIs = null;
        	           try {
        					bitmapIs = HttpUtils.getStreamFromURL(imageURL); 
        					Bitmap bitmap = BitmapFactory.decodeStream(bitmapIs);  
         	                imageCache.put(imageURL, new SoftReference<Bitmap>(bitmap));  
         	                Message msg = handler.obtainMessage(); 
         	                msg.obj=bitmap;
         	                handler.sendMessage(msg);  
         	                if(FileManager.existSDCard()){
         	                	File dir = new File(FileManager.PHOTOS_PATH);  
         	                	if(!dir.exists()){  
         	                		dir.mkdirs();  
         	                	   }  
         	                		File bitmapFile = new File(FileManager.PHOTOS_PATH +   
        	                        imageURL.substring(imageURL.lastIndexOf("/") + 1));  
        	                if(!bitmapFile.exists()){  
        	                    try{  
        	                        bitmapFile.createNewFile();  
        	                      }catch (IOException e){  
        	                        e.printStackTrace();  
        	                      }  
        	                	}  
        	                	FileOutputStream fos;  
        	                	try{  
        	                		fos = new FileOutputStream(bitmapFile);  
        	                		bitmap.compress(Bitmap.CompressFormat.JPEG,   
        	                						100, fos);  
        	                		fos.close();  
        	                		}catch(FileNotFoundException e){  
        	                			e.printStackTrace();  
        	                		}catch (IOException e){  
        	                			e.printStackTrace();  
        	                		}  
         	                } 
        	           }catch (Exception e) {
        	        	   e.printStackTrace();
        	           }
					}
        	    }.start();  
        	}  
        }
    }
    /** 
     * 回调接口 
     * @author onerain 
     * 
     */  
    public interface ImageCallBack  
    {  
        public void imageLoad(ImageView imageView, Bitmap bitmap);  
    }  
}  
