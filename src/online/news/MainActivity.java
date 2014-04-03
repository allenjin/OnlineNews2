package online.news;
import java.util.LinkedList;
import java.util.List;
import online.news.adapter.AppListAdapter;
import online.news.adapter.NewsInfoListAdapter;
import online.news.adapter.NewsPagerAdapter;
import online.news.beans.SearchListBean;
import online.news.entity.AppInfo;
import online.news.entity.Passage;
import online.news.sql.DataBaseHelper;
import online.news.sql.PassageDAO;
import online.news.util.CallBack;
import online.news.widget.ActionBarDropDownItem;
import online.news.widget.ActionBarDropDownView;
import online.news.widget.NotificationListView;
import online.news.widget.TitlePageIndicator;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnCreateContextMenuListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.actionbarsherlock.app.ActionBar;
import com.slidingmenu.lib.SlidingMenu;
import com.umeng.analytics.MobclickAgent;
/**
 * 应用的主Activity
 * @author AllenJin
 *
 */
public class MainActivity extends BaseActivity implements 
			ActionBar.OnNavigationListener,ViewPager.OnPageChangeListener{
	
	private View onlineNewsView;//学习新闻界面
	private View searchView;//搜索界面
	private View favoriteView;//收藏界面
	private View appCenterView;//应用中心界面
	private View notificationView;//通知界面
	//侧拉界面
	private TextView clickedTv;	//当前选中按钮
	private int clickedIconRes;//当前选中按钮图片资源
	private View curShowView;//当前显示的界面
	private int onlineNewsPos=0;
	//学线新闻模块初始变量
	private ViewPager mPager;
	private TitlePageIndicator mIndicator;
	private NewsPagerAdapter mAdapter;
	//actionbar
	private ArrayAdapter<CharSequence> onlineList;
	private int curOnlinePos=0;
	
	
	//通知界面
	private ActionBarDropDownView abDropDownView;
	private  String[] noLabels;
	private int linearPos=0;
	private LinearLayout noLayout;
	private NotificationListView onlineNoView;
	private NotificationListView curNoView;
	private boolean isInitView[]=new boolean[8];
	private NotificationListView noliviews[]=new NotificationListView[8];
	private int AbItemsDeRes[]={R.drawable.ab_item_online,R.drawable.ab_item_qingchun,R.drawable.ab_item_yanjiusheng,
							R.drawable.ab_item_yanjiusheng,R.drawable.ab_item_guoji,R.drawable.ab_item_tongzhi,
							R.drawable.ab_item_tongzhi,R.drawable.ab_item_tongzhi};
	private int AbItemsPrRes[]={R.drawable.ab_item_online_red,R.drawable.ab_item_qingchun_red,R.drawable.ab_item_yanjiusheng_red,
							R.drawable.ab_item_yanjiusheng_red,R.drawable.ab_item_guoji_red,R.drawable.ab_item_tongzhi_red,
							R.drawable.ab_item_tongzhi_red,R.drawable.ab_item_tongzhi_red};
	//搜索界面初始变量
	private EditText searchBar;
	private LinearLayout resultLayout;
	private Button searchButton;
	private ImageView delBtn;
	private LayoutInflater mInflater;
	private View loadingView;
	private ListView resultListView;
	private SearchListBean searchBean;
	private List<Passage> slist;
	private NewsInfoListAdapter adapter;
	private int type=1;//调用搜索文章时,设置类型为1。
	private static final int SEARCH_MESSAGE_INIT=1;
	private static final int SEARCH_MESSAGE_EXCEPTION=2;
	private Handler searchHandler=new Handler(){
		@Override
		public void handleMessage(Message msg) {
			switch(msg.what){
			case SEARCH_MESSAGE_INIT:
				if(slist.size()!=0){//若有搜索结果，则显示
					adapter=new NewsInfoListAdapter(MainActivity.this, slist);
					resultListView.setAdapter(adapter);
					resultLayout.removeAllViews();
					resultLayout.setGravity(Gravity.TOP);
					resultLayout.addView(resultListView);
				}else{//无结果则显示提示信息
					resultLayout.removeAllViews();
					resultLayout.setGravity(Gravity.CENTER);
					TextView tx=new TextView(MainActivity.this);
					tx.setGravity(Gravity.CENTER);
					tx.setText("未找到相应文章");
					resultLayout.addView(tx);
				}
				break;
			case SEARCH_MESSAGE_EXCEPTION:
				Toast.makeText(MainActivity.this,"exception",Toast.LENGTH_SHORT).show();
				break;
			}
		}	
	};
	//收藏新闻初始变量
	private List<Passage> pList;
	private PassageDAO pDao;
	private DataBaseHelper helper ;
	/*the menu on which position*/
	private int menuPosition;
	private NewsInfoListAdapter lAdapter;
	private ListView listview;
	private LinearLayout mainLayout;
	
	//应用中心初始变量
	private ListView applist;
	private String bus_url="http://www.online.sdu.edu.cn/app/app_an_quer.html";
	//其他站点初始变量
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_frame);
		findViews();
		init();
		setListeners();
	}
	/**
	 * 初始化默认显示界面
	 */
	private void init(){
		//初始化默认为学线新闻界面
		clickedIconRes=R.drawable.sm_notification;
		clickedTv=notificationTv;
		onlineNoView=new NotificationListView(this,0);
		isInitView[0]=true;
		noliviews[0]=onlineNoView;
		curNoView=onlineNoView;
		noLayout.removeAllViews();
		noLayout.addView(onlineNoView,linearPos++);
		curShowView=notificationView;
	}
	private void findViews(){
		//学线下拉菜单
		onlineList= ArrayAdapter.createFromResource(
        		actionbar.getThemedContext(),
        		R.array.online_news_labels, 
        		R.layout.sherlock_spinner_item);
		onlineList.setDropDownViewResource(R.layout.news_spinner_dropdown_item);
		//各版块的view
        notificationView=(View)findViewById(R.id.main_notification_view);
        noLayout=(LinearLayout)findViewById(R.id.notification_content_layout);
        abDropDownView=new ActionBarDropDownView(this);
        noLabels=getResources().getStringArray(R.array.notification_labels);
        for(int i=0;i<noLabels.length;i++){
        	abDropDownView.addItem(new ActionBarDropDownItem(noLabels[i],AbItemsDeRes[i],AbItemsPrRes[i]));
        }
		onlineNewsView=(View)findViewById(R.id.main_online_news_view);
		searchView=(View)findViewById(R.id.main_search_view);
		favoriteView=(View)findViewById(R.id.main_favorite_view);
		appCenterView=(View)findViewById(R.id.main_app_view);
		
		//onlineNews布局
		mAdapter=new NewsPagerAdapter(this);
		mPager=(ViewPager)findViewById(R.id.pager);
		mIndicator=(TitlePageIndicator)findViewById(R.id.indicator);
		mPager.setAdapter(mAdapter);
		mIndicator.setViewPager(mPager);
		mIndicator.setOnPageChangeListener(this);
		
		//搜索界面布局
		searchBar=(EditText)findViewById(R.id.search_edit_text);
		resultLayout=(LinearLayout)findViewById(R.id.search_result_layout);
		searchButton=(Button)findViewById(R.id.search_button);
		delBtn=(ImageView)findViewById(R.id.search_del);
		delBtn.setVisibility(View.INVISIBLE);
		mInflater=LayoutInflater.from(this);
		loadingView=mInflater.inflate(R.layout.new_detail_loading, null);
		resultListView=new ListView(this);
		searchBean=new SearchListBean();
		//收藏界面布局
		mainLayout=(LinearLayout)findViewById(R.id.favorite_main);
		listview=new ListView(this);
		helper = new DataBaseHelper(this);
		pDao = new PassageDAO(this, helper);
		
		//应用中心布局
		applist=(ListView)findViewById(R.id.app_listview);
		applist.setAdapter(new AppListAdapter(this,initData()));
		//其他站点布局	
	}
	public void onResume() {
	    super.onResume();
	    showFavorites();//用于显示收藏列表
	    MobclickAgent.onResume(this);
	}
	public void onPause() {
	    super.onPause();
	    MobclickAgent.onPause(this);
	}
	@Override
	public void onPageScrollStateChanged(int arg0) {
		
	}
	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
		
	}
	@Override
	public void onPageSelected(int position) {
		onlineNewsPos=position;
		if(position!=0){
			getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
		}else{
			getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		}
		actionbar.setSelectedNavigationItem(position);
		curOnlinePos=position;
	}
	
	/**
	 * 学线新闻中的ActionBar中下拉列表选择监听；
	 */
	@Override
	public boolean onNavigationItemSelected(int itemPosition, long itemId) {
			mPager.setCurrentItem(itemPosition);
		return true;
	}
	
	/**
	 * 界面事件监听
	 */
	private void setListeners(){
		/******************通知界面***********************/
		abDropdownTv.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				abDropDownView.show(noLayout);
			}
		});
		
		abDropDownView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapter, View view, int position,
					long id) {
				abDropDownView.notifyChanged(position);
				abDropdownTv.setText(noLabels[position]);
				curNoView.setVisibility(View.GONE);
				if(!isInitView[position]){
					NotificationListView noli=new NotificationListView(MainActivity.this, position);
					noLayout.addView(noli,linearPos++);
					curNoView=noli;
					isInitView[position]=true;
					noliviews[position]=noli;
				}else{
					noliviews[position].setVisibility(View.VISIBLE);
					curNoView=noliviews[position];
				}
				abDropDownView.dismiss();
			}
		});
		/*****************侧拉栏事件监听********************/
		newsTv.setOnClickListener(new SlibingMenuChangedListener());
		searchTv.setOnClickListener(new SlibingMenuChangedListener());
		shoucangTv.setOnClickListener(new SlibingMenuChangedListener());
		appsTv.setOnClickListener(new SlibingMenuChangedListener());
		notificationTv.setOnClickListener(new SlibingMenuChangedListener());
		/******************搜索界面事件监听******************/
		searchBar.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if(s.length()==0){	//若输入框无字符,内容为空
					delBtn.setVisibility(View.INVISIBLE);
					resultLayout.removeAllViews();
				}else{				//否则显示删除键
					delBtn.setVisibility(View.VISIBLE);
				}	
			}
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}
			@Override
			public void afterTextChanged(Editable s) {
			}
		});
		delBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				searchBar.setText("");
			}
		});
		resultListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> root, View view, int position,
					long id) {
				Intent intent=new Intent(MainActivity.this,NewsDetailActivity.class);
				intent.putExtra("passage", slist.get(position));
				startActivity(intent);
				overridePendingTransition(R.anim.push_right_in,R.anim.push_left_out);
			}
		});
		searchButton.setOnClickListener(new OnClickListener(
				) {
			@Override
			public void onClick(View v) {
				if(searchBar.getText()==null||searchBar.getText().toString().equals("")){
					Toast.makeText(MainActivity.this,"输入不能为空!",Toast.LENGTH_SHORT).show();
				}else{		
					String keywords=searchBar.getText().toString();
					//异步调用文章搜索方法
					searchBean.getPassageListAsync(type, keywords, new CallBack<List<Passage>>() {
						@Override
						public void onFinish(List<Passage> param) {
							slist=param;
							searchHandler.sendEmptyMessage(SEARCH_MESSAGE_INIT);
						}

						@Override
						public void onException(Exception e) {
							searchHandler.sendEmptyMessage(SEARCH_MESSAGE_EXCEPTION);
						}
					});
					resultLayout.removeAllViews();
					//隐藏软键盘 
					((InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE))
					.hideSoftInputFromWindow(getCurrentFocus().getWindowToken()
							, InputMethodManager.HIDE_NOT_ALWAYS);
					resultLayout.setGravity(Gravity.CENTER);
					resultLayout.addView(loadingView);
				}	
			}
		});
		
		/******************收藏界面事件监听******************/
		listview.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> root, View view, int position,
					long id) {
				//item position change in pulltorefreshListView
				Intent intent=new Intent(MainActivity.this,NewsDetailActivity.class);
				intent.putExtra("passage", pList.get(position));
				startActivity(intent);
				overridePendingTransition(R.anim.push_right_in,R.anim.push_left_out);
			}
		});
		
		listview.setOnCreateContextMenuListener(new OnCreateContextMenuListener() {
			@Override
			public void onCreateContextMenu(ContextMenu menu, View v,
					ContextMenuInfo menuInfo) {
				AdapterView.AdapterContextMenuInfo menuinfo = (AdapterView.AdapterContextMenuInfo)menuInfo;
				menuPosition=menuinfo.position;
				menu.add(R.string.menu_favorite_delete);
				menu.setHeaderTitle("选择操作");
			}
		});
	}
	/************************---收藏界面----*******************/
	private void showFavorites(){
		pList = pDao.list(); 
		if(pList.size()==0){
			favoritesZero();
		}else{
			lAdapter = new NewsInfoListAdapter(this, pList);
			listview.setAdapter(lAdapter);
			mainLayout.setGravity(Gravity.TOP);
			mainLayout.removeAllViews();
			mainLayout.addView(listview);
		}
	}
	/**
	 * 若无收藏则调用该方法，显示提示信息
	 */
	private void favoritesZero(){
		TextView tx=new TextView(this);
		tx.setText("尚无收藏");
		tx.setGravity(Gravity.CENTER);
		mainLayout.setGravity(Gravity.CENTER);
		mainLayout.removeAllViews();
		mainLayout.addView(tx);
	}
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		/*上下文菜单，用于删除收藏*/
		if(item.getTitle().equals(getResources().getString(R.string.menu_favorite_delete))){
			pDao.delete(pList.get(menuPosition).getPid());
			pList.remove(menuPosition);
			lAdapter.notifyDataSetChanged();
			if(pList.size()==0){
				favoritesZero();
			}
		}
		return super.onContextItemSelected(item);
	}
	
	
	/************************----------应用中心-----------*******************/
	/**
	 * 此方法为初始化应用信息数据的方法
	 * 注：若添加一个新的应用，则按照如下的方法在appDataList中添加AppInfo的实例即可；
	 */
	private LinkedList<AppInfo> initData() {
		LinkedList<AppInfo> appDataList=new LinkedList<AppInfo>();
		//android应用添加如下
		appDataList.add(new AppInfo(AppInfo.APP_TYPE_ANDROID,bus_url,"com.sdu.online.schoolbus", 
				"com.sdu.online.schoolbus.SplashActivity", "校车查询",
				R.drawable.app_schoolbus_bg, R.drawable.ic_app_bus));
		//HTML5应用添加如下
		/**
		appDataList.add(new AppInfo(AppInfo.APP_TYPE_HTML5,//此为类型
				"http://html5应用地址",//HTML5应用地址
				null,
				null,
				"HTML5应用名称",				//应用名称
				R.drawable.app_schoolbus_bg,//图片背景资源
				R.drawable.ic_app_bus));	//应用图标资源
		**/
		return appDataList;
	}
	
	/***********************-----------侧拉栏界面切换事件监听**************************/
	private class SlibingMenuChangedListener implements View.OnClickListener{
		@Override
		public void onClick(View v) {
			TextView cv=(TextView)v;
			if(cv.equals(clickedTv)){//若点击当前界面按钮,默认无响应
				return;
			}
			//设置选中按钮颜色
			cv.setTextColor(getResources().getColor(R.color.sm_item_color_red));
			if(clickedTv!=null&&curShowView!=null){
				curShowView.setVisibility(View.GONE);
				clickedTv.setTextColor(getResources().getColor(R.color.sm_item_color_default));
				Drawable rDrawable=getResources().getDrawable(clickedIconRes);
				rDrawable.setBounds(0, 0, 36, 36);
				clickedTv.setCompoundDrawables(rDrawable, null, null, null);
			}
			int IconRes = 0;//当前选中按钮的图片资源
			switch(cv.getId()){
			case R.id.sm_item_news:
				//actionbar change
				actionbar.setDisplayShowCustomEnabled(false);
				actionbar.setDisplayShowTitleEnabled(false);
				actionbar.setIcon(R.drawable.sm_news);		
				actionbar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
		        actionbar.setListNavigationCallbacks(onlineList, MainActivity.this);
		        actionbar.setSelectedNavigationItem(curOnlinePos);
				curShowView=onlineNewsView;
				onlineNewsView.setVisibility(View.VISIBLE);
				clickedIconRes=R.drawable.sm_news;
				IconRes=R.drawable.sm_news_red;
				if(onlineNewsPos==0){
					getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
				}else{
					getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
				}
				break;
			case R.id.sm_item_search:
				curShowView=searchView;
				searchView.setVisibility(View.VISIBLE);
				clickedIconRes=R.drawable.sm_search;
				IconRes=R.drawable.sm_search_red;
				changeActionBar(R.string.sm_item_search,R.drawable.sm_search);
				break;
			case R.id.sm_item_shoucang:
				curShowView=favoriteView;
				favoriteView.setVisibility(View.VISIBLE);
				clickedIconRes=R.drawable.sm_shoucang;
				IconRes=R.drawable.sm_shoucang_red;
				changeActionBar(R.string.sm_item_shoucang,R.drawable.sm_shoucang);
				break;
			case R.id.sm_item_apps:
				curShowView=appCenterView;
				appCenterView.setVisibility(View.VISIBLE);
				clickedIconRes=R.drawable.sm_apps_center;
				IconRes=R.drawable.sm_apps_center_red;
				changeActionBar(R.string.sm_item_apps,R.drawable.sm_apps_center);
				break;
			case R.id.sm_item_notification:
				getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
				actionbar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
				actionbar.setDisplayHomeAsUpEnabled(true);
				actionbar.setDisplayShowTitleEnabled(false);
				actionbar.setIcon(R.drawable.sm_notification);		
				actionbar.setDisplayShowCustomEnabled(true);
				curShowView=notificationView;
				notificationView.setVisibility(View.VISIBLE);
				clickedIconRes=R.drawable.sm_notification;
				IconRes=R.drawable.sm_notification_red;
				break;
			}
			Drawable cDrawable=getResources().getDrawable(IconRes);
			cDrawable.setBounds(0, 0, 36, 36);
			cv.setCompoundDrawables(cDrawable, null, null, null);
			clickedTv=cv;
			getSlidingMenu().showContent();
		} 
		//切换时,ActionBar对应改变
		private void changeActionBar(int titleRes,int IconRes){
			getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
			actionbar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
			actionbar.setDisplayShowCustomEnabled(false);
			actionbar.setDisplayHomeAsUpEnabled(true);
			actionbar.setDisplayShowTitleEnabled(true);
			actionbar.setTitle(titleRes);
			actionbar.setIcon(IconRes);
		}
	}
}
