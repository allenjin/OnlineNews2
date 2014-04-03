package online.news;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.MenuItem;
import online.news.R;
import com.slidingmenu.lib.SlidingMenu;
import com.slidingmenu.lib.app.SlidingFragmentActivity;
/**
 * 该Activity继承开源包的SlidingFragmentActivity以实现侧拉功能
 * @author AllenJin
 *
 */
public class BaseActivity extends SlidingFragmentActivity {
	private SlidingMenu sm;
	protected TextView notificationTv,newsTv,searchTv,shoucangTv,appsTv,settingsTv,exitTv;
	protected ActionBar actionbar;
	protected TextView abDropdownTv;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
				// set the Behind View
				setBehindContentView(R.layout.menu_layout);
				findViews();
				//customize actionbar 
				actionbar=getSupportActionBar();
				actionbar.setDisplayHomeAsUpEnabled(true);
				actionbar.setIcon(R.drawable.sm_notification);
				actionbar.setDisplayShowTitleEnabled(false);
				actionbar.setHomeButtonEnabled(true);
				actionbar.setBackgroundDrawable(getResources().getDrawable(R.drawable.actionbar_bg));
				abDropdownTv=(TextView)LayoutInflater.from(this).inflate(R.layout.ab_spinner, null);
				actionbar.setCustomView(abDropdownTv);
				actionbar.setDisplayShowCustomEnabled(true);
				// customize the SlidingMenu
				sm = getSlidingMenu();
				sm.setShadowWidthRes(R.dimen.shadow_width);
				sm.setShadowDrawable(R.drawable.shadow);
				sm.setBehindOffsetRes(R.dimen.slidingmenu_offset);
				sm.setFadeDegree(0.35f);
				sm.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
	}
	/**
	 * 初始化侧拉菜单界面
	 */
	private void findViews(){
		actionbar=getSupportActionBar();
		notificationTv=(TextView)findViewById(R.id.sm_item_notification);
		notificationTv.setTextColor(getResources().getColor(R.color.sm_item_color_red));
		Drawable rDrawable=getResources().getDrawable(R.drawable.sm_notification_red);
		rDrawable.setBounds(0, 0, 36, 36);
		notificationTv.setCompoundDrawables(rDrawable, null, null, null);
		newsTv=(TextView)findViewById(R.id.sm_item_news);
		searchTv=(TextView)findViewById(R.id.sm_item_search);
		shoucangTv=(TextView)findViewById(R.id.sm_item_shoucang);
		appsTv=(TextView)findViewById(R.id.sm_item_apps);
		settingsTv=(TextView)findViewById(R.id.sm_item_settings);
		settingsTv.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent=new Intent(BaseActivity.this,SettingsActivity.class);
				startActivity(intent);
				overridePendingTransition(R.anim.push_right_in, R.anim.push_left_out);
			}
		});
		exitTv=(TextView)findViewById(R.id.sm_item_exit);
		exitTv.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
					finish();
				}
		});
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()){
		case android.R.id.home:
			Log.v("-------------------------","click home");
			sm.toggle();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if(keyCode==KeyEvent.KEYCODE_BACK&&sm.isMenuShowing()){
			Builder builder=new AlertDialog.Builder(this);
			builder.setMessage(R.string.dialog_exit_info)
			.setPositiveButton(R.string.dialog_confirm, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					BaseActivity.this.finish();
				}
			}).setNegativeButton(R.string.dialog_cancel, null).show();
			return true;
		}else if(keyCode==KeyEvent.KEYCODE_BACK&&!sm.isMenuShowing()){
			sm.showMenu();
			return true;
		}else if(keyCode==KeyEvent.KEYCODE_MENU){
			sm.toggle();
			return true;
		}
		return super.onKeyUp(keyCode, event);
	}
}
