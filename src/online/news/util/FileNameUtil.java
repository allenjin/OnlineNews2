package online.news.util;

public class FileNameUtil {
	

	public static String getFileName(String fileURL) {
		int length = fileURL.length();
		String fileName;
		fileName = fileURL.substring(length-16, length);
		return fileName;
	}
	public static String getNewFileURL(String originalURL){
		String fileName = getFileName(originalURL);
		String newURL = UsefulStaticValues.getIpAddress()+":8080/News2/images/"+fileName;
		return newURL;
	}
}
