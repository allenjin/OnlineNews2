package online.news;
import java.io.IOException;
import java.util.List;

import org.apache.http.client.ClientProtocolException;

import online.news.adapter.NewsInfoListAdapter;
import online.news.beans.OtherListBean;
import online.news.entity.Passage;
import online.news.util.CallBack;
import online.news.util.UsefulStaticValues;
import online.news.widget.NewsDataToast;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnLastItemVisibleListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.umeng.analytics.MobclickAgent;

public class OtherNewsListActivity extends SherlockActivity {
	private OtherListBean otherBean;
	private List<Passage> plist;
	private int otherBlockId;
	private int page=1;
	private ActionBar actionbar;
	private LinearLayout mainLayout;
	private ProgressBar loadingPb;
	private ListView actualListView;
	private NewsInfoListAdapter newsAdapter;
	private View footerView;
	private TextView load_more_tv;
	private ProgressBar load_more_pb;
	private ImageView load_again_iv;
	private boolean isLoadingMore=false;
	private PullToRefreshListView prlv;
	private static final int MESSAGE_LOAD_MORE=1;
	private static final int MESSAGE_LOAD_FAIL=2;
	private static final int MESSAGE_INIT_LIST=3;
	private static final int MESSAGE_EXCEPTION=4;
	private static final int MESSAGE_NULL=5;
	private Handler handler =new Handler(){
		@Override
		public void handleMessage(Message msg) {
			switch(msg.what){
			case MESSAGE_LOAD_MORE:
				isLoadingMore=false;
				newsAdapter.notifyDataSetChanged();
				break;
			case MESSAGE_LOAD_FAIL:
				isLoadingMore=false;
				load_more_pb.setVisibility(View.GONE);
				load_more_tv.setText(R.string.load_more_fail);
				load_more_tv.setClickable(true);
				break;
			case MESSAGE_INIT_LIST:
				newsAdapter=new NewsInfoListAdapter(OtherNewsListActivity.this, plist);
				actualListView.setAdapter(newsAdapter);
				mainLayout.removeAllViews();
				mainLayout.addView(prlv);
				break;
			case MESSAGE_EXCEPTION:
				Toast.makeText(OtherNewsListActivity.this, "网络不给力", Toast.LENGTH_SHORT).show();
				showLoadFailView();
				break;
			case MESSAGE_NULL:
				Toast.makeText(OtherNewsListActivity.this, "服务器无响应", Toast.LENGTH_SHORT).show();
				showLoadFailView();
				break;
			}
		}
	};
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.other_news_list_layout);
		actionbar=getSupportActionBar();
		actionbar.setBackgroundDrawable(getResources().getDrawable(R.drawable.actionbar_bg));
		actionbar.setDisplayHomeAsUpEnabled(true);
		findViews();
		setListeners();
		init();
	}
	private void init(){
		Intent intent=getIntent();
		otherBlockId=intent.getIntExtra("obid", 0);
		actionbar.setIcon(getIntent().getIntExtra("iconRes", R.drawable.sm_news));
		actionbar.setTitle(UsefulStaticValues.otherBlockTitle[otherBlockId]);
		otherBean=new OtherListBean();
		otherBean.getPassageListAsync(otherBlockId, 1, new CallBack<List<Passage>>() {
			@Override
			public void onFinish(List<Passage> param) {
				if(param!=null){
					Log.v("list", param.toString());
					plist=param;
					handler.sendEmptyMessage(MESSAGE_INIT_LIST);
				}else{
					handler.sendEmptyMessage(MESSAGE_NULL);
				}
			}
			@Override
			public void onException(Exception e) {
				handler.sendEmptyMessage(MESSAGE_EXCEPTION);
			}
		});
	}
	private void findViews() {
		mainLayout=(LinearLayout)findViewById(R.id.other_news_list_layout);
		loadingPb=(ProgressBar)findViewById(R.id.other_list_progressBar);
		prlv=new PullToRefreshListView(this);
		actualListView=prlv.getRefreshableView();
		footerView=getLayoutInflater().inflate(R.layout.footer_load_more, null);
		actualListView.addFooterView(footerView);
		footerView.setClickable(false);
		load_more_tv=(TextView)footerView.findViewById(R.id.load_moer_tv);
		load_more_pb=(ProgressBar)footerView.findViewById(R.id.load_more_pb);
	}
	private void setListeners(){
		//item点击事件
		prlv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View v, int position,
					long id) {
				Log.v("othernews", "curPos:"+position);
				Intent intent=new Intent(OtherNewsListActivity.this,NewsDetailActivity.class);
				intent.putExtra("passage", plist.get(position-1));
				intent.putExtra("urlLink", UsefulStaticValues.otherBlockUrl[otherBlockId]);
				intent.putExtra("isOtherNews", true);
				startActivity(intent);
				overridePendingTransition(R.anim.push_right_in,R.anim.push_left_out);
			}
		});
		//下拉刷新事件监听
		prlv.setOnRefreshListener(new OnRefreshListener<ListView>() {
			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
				new GetDataTask().execute();
			}
		});
		//最后item显示触发事件监听
		prlv.setOnLastItemVisibleListener(new OnLastItemVisibleListener() {
			@Override
			public void onLastItemVisible() {
				isLoadingMore=true;
				otherBean.getPassageListAsync(otherBlockId, ++page, new CallBack<List<Passage>>() {
					@Override
					public void onFinish(List<Passage> param) {
						plist.addAll(param);
						handler.sendEmptyMessage(MESSAGE_LOAD_MORE);
					}
					@Override
					public void onException(Exception e) {
						handler.sendEmptyMessage(MESSAGE_LOAD_FAIL);
					}
				});
			}
		});
		load_more_tv.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(!isLoadingMore){
					load_more_pb.setVisibility(View.VISIBLE);
					load_more_tv.setText(R.string.load_more_tv);
					otherBean.getPassageListAsync(otherBlockId, ++page, new CallBack<List<Passage>>() {
						@Override
						public void onFinish(List<Passage> param) {
							plist.addAll(param);
							handler.sendEmptyMessage(MESSAGE_LOAD_MORE);
						}
						@Override
						public void onException(Exception e) {
							handler.sendEmptyMessage(MESSAGE_LOAD_FAIL);
						}
					});
			   }
			}
		});
	}
	
	/**
	 * 加载失败调用的方法
	 * 
	 */
	private void showLoadFailView() {
		mainLayout.removeAllViews();
		View v=getLayoutInflater().inflate(R.layout.news_load_again_layout, null);
		load_again_iv=(ImageView)v.findViewById(R.id.news_load_again_iv);
		mainLayout.addView(v);
		load_again_iv.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mainLayout.removeAllViews();
				mainLayout.addView(loadingPb);
				otherBean.getPassageListAsync(otherBlockId, 1, new CallBack<List<Passage>>() {
					@Override
					public void onFinish(List<Passage> param) {
						if(param!=null){
							plist=param;
							handler.sendEmptyMessage(MESSAGE_INIT_LIST);
						}else{
							handler.sendEmptyMessage(MESSAGE_NULL);
						}
					}
					@Override
					public void onException(Exception e) {
						handler.sendEmptyMessage(MESSAGE_EXCEPTION);
					}
				});
			}
		});
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()){
		case android.R.id.home:
			this.finish();
			
			return true;
		}
		return super.onOptionsItemSelected(item);
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
		this.overridePendingTransition(R.anim.push_left_in, R.anim.push_right_out);
	}

	private class GetDataTask extends AsyncTask<Void, Void, List<Passage>> {
        @Override
        protected List<Passage> doInBackground(Void... params) {
        	List<Passage> list=null;
			try {
				list=otherBean.getPassageList(otherBlockId, 1);
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
            return list;
        }

        @Override
        protected void onPostExecute(List<Passage> result) {
            // Call onRefreshComplete when the list has been refreshed.
        	if(result!=null){
        		int i=0;
        		for(;i<result.size();i++){
        			if(result.get(i).getPid()==plist.get(0).getPid()){
        				break;
        			}
        		}
        		if(i==0){
        			NewsDataToast.makeText(OtherNewsListActivity.this, getString(R.string.new_data_toast_none),50).show();
        		}else {
        			NewsDataToast.makeText(OtherNewsListActivity.this,getString(R.string.new_data_toast_message,i),50).show();
        		}
	        	plist.clear();
	        	plist.addAll(result);
	        	newsAdapter.notifyDataSetChanged();
        	}else{
        		NewsDataToast.makeText(OtherNewsListActivity.this,getString(R.string.new_data_toast_exception),50).show();
        	}
        	prlv.onRefreshComplete();
            super.onPostExecute(result);
        }
	}
}
