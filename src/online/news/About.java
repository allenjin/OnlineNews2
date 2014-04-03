package online.news;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;
import com.umeng.analytics.MobclickAgent;

import android.os.Bundle;
import android.webkit.WebView;
/**
 * 关于界面
 * @author AllenJin
 *
 */
public class About extends SherlockActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.actionbar_bg));
		getSupportActionBar().setTitle("关于");
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		WebView webView = new WebView(this);
		setContentView(webView);
		webView.loadUrl("file:///android_asset/about.html");
	}
	public void onResume() {
	    super.onResume();
	    MobclickAgent.onResume(this);
	}
	public void onPause() {
	    super.onPause();
	    MobclickAgent.onPause(this);
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
