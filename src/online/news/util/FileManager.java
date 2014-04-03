package online.news.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import org.apache.http.util.EncodingUtils;
import android.os.Environment;

/**manage list and content caches*/
public class FileManager {
	public static final String path = Environment.getExternalStorageDirectory()+"/online/news/";
	public final static String PHOTOS_PATH= Environment.getExternalStorageDirectory() + "/online/pics/";  
	public static final String suffix = ".tmp";
	private FileManager(){}
	
	/**prepare the dirs that will be used*/
	public static void initDirs(){
		File photoPath=new File(FileManager.PHOTOS_PATH);
		File f = new File(path);
		if(!f.exists()){
			f.mkdirs();
		}
		if(!photoPath.exists()){
	        photoPath.mkdirs();
	    }
	}
	/**
	 * 判断是否加载SD卡
	 */
	public static boolean existSDCard() {  
		 if (android.os.Environment.getExternalStorageState().equals(  
		    android.os.Environment.MEDIA_MOUNTED)) {  
		   return true;  
		  } else  
		   return false;  
		 }  
	/**create file with pre-given path and suffix
	 * @param fileName the name without prefix and suffix
	 * @param content the file content
	 * @return the file absolute path*/
	public static String createFile(String fileName,String content) throws IOException{
		if(content!=null){
			File f = new File(path+fileName+suffix);
			if(!f.getParentFile().isDirectory())
				initDirs();
			FileOutputStream fops = new FileOutputStream(f);
			fops.write(content.getBytes());
			fops.flush();
			fops.close();
			return f.getAbsolutePath();
		}
		return null;
	}
	
	/**read file which was not be used by now*/
	public static String readFile(String fileName) throws IOException{
		String content = null;
		File f = new File(path+fileName+suffix);
		if(!f.exists()) return null;
		FileInputStream fin = new FileInputStream(f);
	    int length = fin.available(); 
	    byte [] buffer = new byte[length]; 
	    fin.read(buffer);     
	    content = EncodingUtils.getString(buffer, "UTF-8"); 
	    fin.close();     
		return content;
	}
	
	/**if the tmp file exists
	 * @return null if not exists
	 * <br/>else return the file path*/
	public static String exists(String fileName){
		File f = new File(path+fileName+suffix);
		if(f.exists()) return f.getAbsolutePath();
		return null;
	}
	
	/**clear the tmp files
	 * @return true if every file is deleted successfully
	 * <br/> false otherwise*/
	public static boolean clearTmp(){
		File f = new File(path);
		File[] fs= f.listFiles();
		boolean re = true;
		for(File fil :fs){
			if(fil.isFile())
				re = re && fil.delete();
		}
		//delete photos
		File photoFile=new File(PHOTOS_PATH);
		File[] pfs=photoFile.listFiles();
		for(File pf:pfs){
			pf.delete();
		}
		return re;
	}
	
	/**get the tmp files total size
	 * @return long the total size in bytes*/
	public static long getTmpFilesSize(){
		File f = new File(path);
		File[] fs = f.listFiles();
		long re = 0;
		for(File fil : fs){
			re +=fil.length();
		}
		return re;
	}
}
