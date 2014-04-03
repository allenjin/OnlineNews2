package online.news.entity;

public class AppInfo {
	public static final int APP_TYPE_ANDROID=1;
	public static final int APP_TYPE_HTML5=2;
	public String httpUrl;	
	public String packageInfo; 
	public String activityInfo;
	public String appName;	
	public int appBgRes;	
	public int appLogoRes;	
	public int appType;		
	/**
	 * 
	 * @param appType	应用类型:学线android应用类型为1,HTML5应用类型为2;
	 * @param httpUrl 网页应用的下载地址或HTML5应用地址,必须
	 * @param packageInfo	应用的包名,若其他应该则为null
	 * @param activityInfo	应用开始界面Activity的路径,若其他应该则为null
	 * @param appName	应用名称,必须
	 * @param bgRes		应用展示的背景图片，可以为欢迎界面的截图资源,必须
	 * @param logoRes	应用的Logo资源,必须
	 */
	public AppInfo(int appType,String httpUrl,String packageInfo,String activityInfo,String appName,int bgRes,int logoRes){
		this.appType=appType;
		this.httpUrl=httpUrl;
		this.packageInfo=packageInfo;
		this.activityInfo=activityInfo;
		this.appName=appName;
		this.appBgRes=bgRes;
		this.appLogoRes=logoRes;
	}
}
