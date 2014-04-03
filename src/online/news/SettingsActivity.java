package online.news;
import java.util.HashMap;
import java.util.Map;

import online.news.util.FileManager;
import online.news.util.OStrOperate;

import com.actionbarsherlock.app.SherlockPreferenceActivity;
import com.actionbarsherlock.view.MenuItem;
import com.umeng.analytics.MobclickAgent;
import com.umeng.fb.UMFeedbackService;
import com.umeng.fb.util.FeedBackListener;
import com.umeng.update.UmengUpdateAgent;
import com.umeng.update.UmengUpdateListener;
import com.umeng.update.UpdateResponse;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceScreen;
import android.widget.EditText;
import android.widget.Toast;

@SuppressLint("ParserError")
public class SettingsActivity extends SherlockPreferenceActivity {
	private Preference checkNewVersion,clearTmp;
	private ListPreference fontList,imgLoad;
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.actionbar_bg));
		getSupportActionBar().setIcon(R.drawable.sm_settings);
		getSupportActionBar().setTitle(R.string.sm_item_settings);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		addPreferencesFromResource(R.xml.preference);
		init();
	}
	
	public void onResume() {
	    super.onResume();
	    //refresh the values
	    setValues();
	    MobclickAgent.onResume(this);
	}
	public void onPause() {
	    super.onPause();
	    MobclickAgent.onPause(this);
	}
	
	private void init(){
		findPreference();
		setValues();
	}
	
	@SuppressWarnings("deprecation")
	private void findPreference(){
		imgLoad = (ListPreference)findPreference("img_load");
		clearTmp = findPreference("clear_tmp");
		checkNewVersion=findPreference("check_new_version");
		fontList=(ListPreference)findPreference("font_list");
		fontList.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				preference.setSummary("当前大小:"+newValue);
				return true;
			}
		});
		imgLoad.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				preference.setSummary("当前模式:"+newValue);
				return true;
			}
		});
	}
	/**set the pre-values*/
	private void setValues(){
		imgLoad.setSummary("当前模式:"+imgLoad.getEntry());
		String sum = null; 
		try{
			sum = OStrOperate.TurnByteSizeToString(FileManager.getTmpFilesSize());
		}catch(Exception e){
			//exception means that sd card is not at place
			sum = "未知";
			Toast.makeText(this, "SD卡未挂载,无法获取缓存信息", Toast.LENGTH_LONG).show();
			clearTmp.setEnabled(false);
		}
		clearTmp.setSummary("已缓存大小:"+sum);
		fontList.setSummary("当前大小:"+fontList.getEntry());
		
		//get the app version name
		PackageManager packageManager = getPackageManager();
        PackageInfo packInfo;
		try {
			packInfo = packageManager.getPackageInfo(getPackageName(),0);
	        String version = packInfo.versionName;
			checkNewVersion.setSummary("当前版本: "+version);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen,
			Preference preference) {
		if(preference.getKey().equals("about_us")){
			//start about activity
			startActivity(new Intent(this,About.class));
			this.overridePendingTransition(R.anim.push_right_in,R.anim.push_left_out);
		}else if(preference.getKey().equals("feedback")){
			startFeedBackActivity();
		}else if(preference.getKey().equals("check_new_version")){
			checkUpdate();
		}else if(preference.getKey().equals("clear_tmp")){
			showClearTmpDialog();
		}else if(preference.getKey().equals("help")){
			//start help activity
			startActivity(new Intent(this,Help.class));
			this.overridePendingTransition(R.anim.push_right_in,R.anim.push_left_out);
		}else if(preference.getKey().equals("guide")){
			Intent intent=new Intent(this,GuideActivity.class);
			intent.putExtra("call_mode", false);
			startActivity(intent);
			this.overridePendingTransition(R.anim.push_right_in,R.anim.push_left_out);
		}
		return super.onPreferenceTreeClick(preferenceScreen, preference);
	}
	
	/**mofify the infos to collect by our self and show activity*/
	private void startFeedBackActivity(){
		FeedBackListener listener = new FeedBackListener() {
		    @Override
		    public void onSubmitFB(Activity activity) {
		        EditText marjorText = (EditText) activity
		                .findViewById(R.id.feedback_marjor);
		        EditText gradeText = (EditText) activity
		                .findViewById(R.id.feedback_grade);
		        EditText emailText = (EditText) activity
		                .findViewById(R.id.feedback_email);
		        EditText phoneText = (EditText) activity
		                .findViewById(R.id.feedback_phone);
		        Map<String, String> contactMap = new HashMap<String, String>();
		        contactMap.put("marjor", marjorText.getText()
		                .toString());
		        contactMap.put("grade", gradeText.getText()
		                .toString());
		        contactMap.put("email", emailText
		                .getText().toString());
		        contactMap.put("phone", phoneText
		                .getText().toString());
		        UMFeedbackService.setContactMap(contactMap);
		    }
		    @Override
		    public void onResetFB(Activity activity,
		            Map<String, String> contactMap,
		            Map<String, String> remarkMap) {
		        // FB initialize itself,load other attribute
		        // from local storage and set them
		    	 EditText marjorText = (EditText) activity
			                .findViewById(R.id.feedback_marjor);
			        EditText gradeText = (EditText) activity
			                .findViewById(R.id.feedback_grade);
			        EditText emailText = (EditText) activity
			                .findViewById(R.id.feedback_email);
			        EditText phoneText = (EditText) activity
			                .findViewById(R.id.feedback_phone);
		        if (remarkMap != null) {
		            marjorText.setText(remarkMap.get("marjor"));
		            gradeText.setText(remarkMap.get("grade"));
		            emailText.setText(remarkMap.get("email"));
		            phoneText.setText(remarkMap.get("phone"));
		        }
		    }
		};
		UMFeedbackService.setFeedBackListener(listener); 
		UMFeedbackService.openUmengFeedbackSDK(this);
	}
	
	
	/**check update infos and notify user*/
	private void checkUpdate(){
		UmengUpdateAgent.update(this);
		UmengUpdateAgent.setUpdateAutoPopup(false);
		UmengUpdateAgent.setUpdateListener(new UmengUpdateListener() {
	        @Override
	        public void onUpdateReturned(int updateStatus,UpdateResponse updateInfo) {
	            switch (updateStatus) {
	            case 0: // has update
	                UmengUpdateAgent.showUpdateDialog(SettingsActivity.this, updateInfo);
	                break;
	            case 1: // has no update
	                Toast.makeText(SettingsActivity.this, "当前版本已为最新", Toast.LENGTH_SHORT)
	                        .show();
	                break;
	            case 2: // none wifi
	                Toast.makeText(SettingsActivity.this, "当前无wifi连接， 建议在wifi下更新", Toast.LENGTH_SHORT)
	                        .show();
	                break;
	            case 3: // time out
	                Toast.makeText(SettingsActivity.this, "连接超时，请检查网络或稍后再试", Toast.LENGTH_SHORT)
	                        .show();
	                break;
	            }
	        }
		});
	}
	
	/**show clear tmp warning*/
	private void showClearTmpDialog(){
		Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("提示")
			.setMessage("缓存可有效节省流量及提示二次载入速度，确定清除？")
			.setNegativeButton("取消", null)
			.setPositiveButton("确定", new DialogInterface.OnClickListener(){
				@Override
				public void onClick(DialogInterface dialog, int which) {
					if(FileManager.clearTmp()){
						//success
						Toast.makeText(SettingsActivity.this, "清除成功！", Toast.LENGTH_LONG).show();
					}else{
						//false
						Toast.makeText(SettingsActivity.this, 
								"清楚遇到问题,若请手动删除文件夹 sdcard/online/news/下的.tmp文件", Toast.LENGTH_LONG).show();
					}
					setValues();
				}}).show();
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()){
		case android.R.id.home:
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	@Override
	public void finish() {
		super.finish();
		//退出动画
		this.overridePendingTransition(R.anim.push_left_in,R.anim.push_right_out);
	}
}
