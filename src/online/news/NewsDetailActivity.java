package online.news;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import online.news.beans.OnlineContentBean;
import online.news.beans.OtherContentBean;
import online.news.entity.Passage;
import online.news.sql.DataBaseHelper;
import online.news.sql.PassageDAO;
import online.news.util.CallBack;
import online.news.util.OStrOperate;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.MailTo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.widget.ShareActionProvider;
import com.umeng.analytics.MobclickAgent;

import online.news.R;
/**
 * 此类为新闻内容显示的Activity
 * @author AllenJin
 *
 */
public class NewsDetailActivity extends SherlockActivity {
	private DataBaseHelper helper;
	private PassageDAO pDao ;
	private TextView titleTv,authorTv,reporterTv,timeTv;
	private WebView contentView;
	private LinearLayout attachmentsLayout;
	private ScrollView mainNewsLayout;
	private LinearLayout mainLoadingLayout;
	private RelativeLayout mainLoadAgainLayout;
	private ImageView mainLoadAgainView;
	private Passage passage;
	private OnlineContentBean onlineBean;
	private OtherContentBean otherBean;
	private String link = "http://www.online.sdu.edu.cn/news/article.php?pid=";
	private static final int MESSAGE_CONTENT_LOAD_FINISHED = 1;
	private static final int MESSAGE_EXCEPTION = 9;
	private ActionBar actionbar;
	private MenuItem shareItem;	//分享按钮
	private MenuItem favoriteItem;//收藏按钮
	private boolean isOtherNews;	//判断是否为otherNews调用
	/**if the network is wifi*/ 
	private boolean isWifi = false;
	private boolean ifLoadImg = false;
	private SharedPreferences sp;
	/**handle event */
	private float x_start=0,x_end=0;
	private float y_start=0,y_end=0;
	private static final String TAG = NewsDetailActivity.class.getSimpleName();
	private Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			switch(msg.what){
			case MESSAGE_CONTENT_LOAD_FINISHED:
				setValues();
				break;
			case MESSAGE_EXCEPTION:
				mainLoadingLayout.setVisibility(View.GONE);
				mainLoadAgainLayout.setVisibility(View.VISIBLE);
				Toast.makeText(NewsDetailActivity.this, "网络不给力", Toast.LENGTH_SHORT).show();
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTheme(R.style.Theme_Sherlock_Light);
		actionbar=getSupportActionBar();
		actionbar.setDisplayShowTitleEnabled(false);
		actionbar.setHomeButtonEnabled(true);
		actionbar.setBackgroundDrawable(getResources().getDrawable(R.drawable.news_detail_actionbar_bg));
		actionbar.setLogo(getResources().getDrawable(R.drawable.news_detail_ab_back));
		setContentView(R.layout.activity_news_detail);
		sp = PreferenceManager.getDefaultSharedPreferences(this);
		  findViews();
	      init();
	      addListeners();
	}
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch(item.getItemId()){
		case android.R.id.home:
			this.finish();
			return true;
		case R.id.news_detail_activity_favorites:
			if(pDao.find(passage.getPid())){
				pDao.delete(passage.getPid());
				favoriteItem.setIcon(R.drawable.news_detail_ab_favor);
			}else{
				pDao.add(passage);
				favoriteItem.setIcon(R.drawable.news_detail_ab_favor_selected);
			}
		}
		return super.onMenuItemSelected(featureId, item);
	}
	/*
	 * (non-Javadoc)
	 * actionBar 分享组件
	 * @see com.actionbarsherlock.app.SherlockActivity#onCreateOptionsMenu(android.view.Menu)
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
			getSupportMenuInflater().inflate(R.menu.share_action_provider, menu);
			favoriteItem=menu.findItem(R.id.news_detail_activity_favorites);
		  // Set file with share history to the provider and set the share intent.
			shareItem = menu.findItem(R.id.menu_item_share_action_provider_action_bar);
	        ShareActionProvider actionProvider = (ShareActionProvider) shareItem.getActionProvider();
	        actionProvider.setShareIntent(createShareIntent());
	      
	      //若为其他新闻，不支持收藏
	        if(isOtherNews){	
	        	favoriteItem.setVisible(false);
	        }
	      //set the favorite icon status
			favoriteItem.setIcon(pDao.find(passage.getPid()) ? R.drawable.news_detail_ab_favor_selected : R.drawable.news_detail_ab_favor);
		return true;
	}
    /**
     * Creates a sharing {@link Intent}.
     *
     * @return The sharing intent.
     */
    private Intent createShareIntent() {
    	Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, getTweetFromPassage(passage));
         return shareIntent;
    }

	/**get description in 140 words from given passage*/
	private String getTweetFromPassage(Passage p){
		return "#掌中山大#["+passage.title+"] "+link;
	}
	/**
	 * 初始化
	 */
	private void init() {
		helper = new DataBaseHelper(this);
		pDao = new PassageDAO(this, helper);
		passage = (Passage) getIntent().getSerializableExtra("passage");
		isOtherNews=getIntent().getBooleanExtra("isOtherNews", false);
		if(isOtherNews){//判断是否为其他站点的新闻
			otherBean=new OtherContentBean();
			if(getIntent().getStringExtra("urlLink")!=null){
				link=getIntent().getStringExtra("urlLink");
			}
			otherBean.getContentByIdAsync(passage.getPid(), new CallBack<String>() {
				@Override
				public void onFinish(String param) {
					passage.content = param;
					handler.sendEmptyMessage(MESSAGE_CONTENT_LOAD_FINISHED);
					Log.v(TAG, "content:"+param);
				}
				@Override
				public void onException(Exception e) {	
					handler.sendEmptyMessage(MESSAGE_EXCEPTION);
				}
				
			});
		}else{//若不是，则为学线新闻
			onlineBean = new OnlineContentBean();
			//init the link address
			link += passage.getPid();
			onlineBean.getContentByIdAsync(passage.getPid(), new CallBack<String>() {
				@Override
				public void onFinish(String param) {
					passage.content = param;
					handler.sendEmptyMessage(MESSAGE_CONTENT_LOAD_FINISHED);
					Log.v(TAG, "content:"+param);
				}
				
				@Override
				public void onException(Exception e) {
					handler.sendEmptyMessage(MESSAGE_EXCEPTION);
				}
			});

	   }
		//get the wifi status 
		//true := wifi is on ,false otherwise
		ConnectivityManager con = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE); 
		isWifi = con.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnected(); 
		//get preferences
		String imgS = sp.getString("img_load",getString(R.string.settings_img_load_1));
		if(imgS.equals(getString(R.string.settings_img_load_1))){
			ifLoadImg = isWifi;
		}else if(imgS.equals(getString(R.string.settings_img_load_2))){
			ifLoadImg = true;
		}else {
			ifLoadImg = false;
		}
	}
	private void findViews() {
		mainLoadAgainLayout=(RelativeLayout)findViewById(R.id.main_news_load_again);
		mainLoadingLayout=(LinearLayout)findViewById(R.id.main_news_loading);
		mainNewsLayout=(ScrollView)findViewById(R.id.main_news_layout);
		mainLoadAgainView=(ImageView)mainLoadAgainLayout.findViewById(R.id.news_load_again_iv);
		titleTv = (TextView) findViewById(R.id.activity_news_detail_title);
		authorTv = (TextView) findViewById(R.id.activity_news_detail_author);
		reporterTv = (TextView)findViewById(R.id.activity_news_detail_reporter);
		timeTv = (TextView) findViewById(R.id.activity_news_detail_time);
		contentView = (WebView) findViewById(R.id.activity_news_detail_content);
		attachmentsLayout = (LinearLayout) findViewById(R.id.activity_news_detail_attachments);
	}
	private void addListeners() {
		attachmentsLayout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				browserOutSide(link);
			}
		});
		mainLoadAgainView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mainLoadingLayout.setVisibility(View.VISIBLE);
				mainLoadAgainLayout.setVisibility(View.GONE);
				if(isOtherNews){//判断是否为其他站点的新闻
					otherBean.getContentByIdAsync(passage.getPid(), new CallBack<String>() {
						@Override
						public void onFinish(String param) {
							passage.content = param;
							handler.sendEmptyMessage(MESSAGE_CONTENT_LOAD_FINISHED);
							Log.v(TAG, "content:"+param);
						}
						@Override
						public void onException(Exception e) {	
							handler.sendEmptyMessage(MESSAGE_EXCEPTION);
						}
					});
				}else{//若不是，则为学线新闻
					onlineBean.getContentByIdAsync(passage.getPid(), new CallBack<String>() {
						@Override
						public void onFinish(String param) {
							passage.content = param;
							handler.sendEmptyMessage(MESSAGE_CONTENT_LOAD_FINISHED);
							Log.v(TAG, "content:"+param);
						}
						
						@Override
						public void onException(Exception e) {
							handler.sendEmptyMessage(MESSAGE_EXCEPTION);
						}
					});
				}
			}
		});
	}
	/**
	 * 初始化页面的信息
	 */
	private void setValues() {
		String fontSize=sp.getString("font_list", "小");
		if(fontSize.equals("小")){
			contentView.getSettings().setDefaultFontSize(16);
		}else if(fontSize.equals("中")){
			contentView.getSettings().setDefaultFontSize(18);
		}else if(fontSize.equals("大")){
			contentView.getSettings().setDefaultFontSize(20);
		}
		titleTv.setText(passage.getTitle());
		reporterTv.setText("记者："+OStrOperate.getValue(passage.getReporter(),"佚名"));
		authorTv.setText("编辑："+OStrOperate.getValue(passage.getEditor(),"无"));
		SimpleDateFormat ff = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		SimpleDateFormat ff2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss 学生在线");
		Date d = null;
		if(passage.getEditTime()!=null){
			try {
				d= ff.parse(passage.getEditTime());
				timeTv.setText(ff2.format(d));
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		contentView.getSettings().setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);
		contentView.getSettings().setDefaultTextEncodingName("utf-8");
		contentView.getSettings().setLoadsImagesAutomatically(ifLoadImg);
		// passage.content load
		contentView.loadDataWithBaseURL("http://www.online.sdu.edu.cn/",passage.getContent(),"text/html","utf-8",null);
		contentView.setWebViewClient(new WebViewClient(){
			@Override
			public void onPageFinished(WebView view, String url) {
				super.onPageFinished(view, url);
				//dismiss the dialog when page load finished;
				mainLoadingLayout.setVisibility(View.GONE);
				mainLoadAgainLayout.setVisibility(View.GONE);
				mainNewsLayout.setVisibility(View.VISIBLE);
			}
			@Override 
		    public boolean shouldOverrideUrlLoading(WebView view, String url) { 
				Log.v(TAG, url);
		        if (url.startsWith("mailto:")) { 
		            MailTo mt=MailTo.parse(url);
		        	Intent i = new Intent(android.content.Intent.ACTION_SENDTO);
		        	i.setData(Uri.parse(url));
		            i.putExtra(Intent.EXTRA_EMAIL, new String[]{mt.getTo()});
		            startActivity(Intent.createChooser(i, "发送邮件"));
		        }else{
		        	try{
		        		browserOutSide(url);
		        	}catch (Exception e) {
		        		e.printStackTrace();
		        	}
				}
		        return true; 
		    }
//			@Override
//			public void onLoadResource(WebView view, String url) {
////				String ur = url.toLowerCase();
////				if(!isWifi){
////					if(ur.endsWith(".img") || ur.endsWith(".png")||ur.endsWith(".gif") || ur.endsWith(".jpg") || ur.endsWith("jpge")){
////						Toast.makeText(NewsDetailActivity.this, "建议关闭图片加载以节省流量",Toast.LENGTH_SHORT).show();
////					}
////				}
//				super.onLoadResource(view, url);
//			}
			
		});
	}
	/**
	 * 点击图片或其他链接，触发外带浏览器浏览
	 * @param url
	 */
	private void browserOutSide(String url) {
		Uri uri = Uri.parse(url);
		Intent intent = new Intent(Intent.ACTION_VIEW,uri);
        startActivity(intent);
	}
	/**
	 * 用于监听手势右滑，使得退出该activity
	 */
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		switch(ev.getAction()){
		case MotionEvent.ACTION_DOWN:
			x_start=ev.getX();
			y_start=ev.getY();
			break;
		case MotionEvent.ACTION_UP:
			x_end=ev.getX();
			y_end=ev.getY();
			Log.v(TAG, "xs:"+x_start+",xe:"+x_end+",ys:"+y_start+",ye:"+y_end);
			if((x_start+35)<x_end&&(y_end-y_start<25&&y_end-y_start>-25)){
				this.finish();
				return true;
			}
			break;
		}
		return super.dispatchTouchEvent(ev);
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
	public void finish() {
		super.finish();
		//退出动画
		this.overridePendingTransition(R.anim.push_left_in,R.anim.push_right_out);
	}

}
