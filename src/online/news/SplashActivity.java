package online.news;

import com.actionbarsherlock.app.SherlockActivity;
import com.umeng.analytics.MobclickAgent;
import com.umeng.fb.NotificationType;
import com.umeng.fb.UMFeedbackService;
import online.news.util.FileManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
/**
 * 应用启动时欢迎界面
 * @author AllenJin
 *
 */
public class SplashActivity extends SherlockActivity {
	SharedPreferences sp;
	private static final String PREFS_NAME="MyPrefsFile";
	private boolean isFirst;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		MobclickAgent.onError(this);
		setContentView(R.layout.splash);
		sp=getSharedPreferences(PREFS_NAME,0);
		//判断是否为第一次启动
		isFirst=sp.getBoolean("launchMode", true);
			//利用线程使欢迎界面停留一段时间
			   new Handler().postDelayed(new Runnable() {
		             public void run() {
		                 /* Create an Intent that will start the Main WordPress Activity. */
		            	 Intent intent=null;
		            	 if(isFirst){//若为第一次启动，则显示引导界面
		            		intent=new Intent(SplashActivity.this,GuideActivity.class);
		            		intent.putExtra("call_mode", true);//与设置界面的调用区分
		            		SharedPreferences.Editor editor=sp.edit();
		            		editor.putBoolean("launchMode", false);
		            		editor.commit();
		            	}else{
		            		intent=new Intent(SplashActivity.this,MainActivity.class);
		            	}
		         		startActivity(intent);
		         		finish();
		             }
		         }, 1250);
			   //create the dirs
			   FileManager.initDirs();
				//umeng feedback alert
				UMFeedbackService.enableNewReplyNotification(this, NotificationType.NotificationBar);
	}
	public void onResume() {
	    super.onResume();
	    MobclickAgent.onResume(this);
	}
	public void onPause() {
	    super.onPause();
	   MobclickAgent.onPause(this);
	}
	
}
