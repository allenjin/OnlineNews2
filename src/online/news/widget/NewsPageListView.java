package online.news.widget;
import java.io.IOException;
import java.util.List;
import online.news.MainActivity;
import online.news.NewsDetailActivity;
import online.news.adapter.NewsInfoListAdapter;
import online.news.beans.NewestBean;
import online.news.beans.OnlineListBean;
import online.news.entity.Passage;
import online.news.util.CallBack;
import org.apache.http.client.ClientProtocolException;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnLastItemVisibleListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import online.news.R;
/**
 * 此类为学线新闻列表的ListView
 * @author Allen
 *
 */
public class NewsPageListView extends LinearLayout {
	private LayoutInflater mInflater;
	private int bid;
	private Context context;
	private PullToRefreshListView prlv;
	private ListView actualListView;
	private NewsInfoListAdapter newsAdapter;
	private List<Passage> plist;
	private OnlineListBean passageList;
	private NewestBean newestBean;
	private int page=1;
	private ProgressBar pb;
	private View footerView;
	private TextView load_more_tv;
	private ProgressBar load_more_pb;
	private ImageView load_again_iv;
	private boolean isLoadingMore=false;
	private static final int MESSAGE_LOAD_MORE=1;
	private static final int MESSAGE_LOAD_FAIL=2;
	private static final int MESSAGE_INIT_LIST=3;
	private static final int MESSAGE_EXCEPTION=4;
	private static final int MESSAGE_NULL=5;
	private static final String TAG=NewsPageListView.class.getSimpleName();
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
				load_more_pb.setVisibility(GONE);
				load_more_tv.setText(R.string.load_more_fail);
				load_more_tv.setClickable(true);
				break;
			case MESSAGE_INIT_LIST:
				setView();
				newsAdapter=new NewsInfoListAdapter(context, plist);
				actualListView.setAdapter(newsAdapter);
				break;
			case MESSAGE_EXCEPTION:
				Toast.makeText(context, "网络不给力", Toast.LENGTH_SHORT).show();
				showLoadFailView();
				break;
			case MESSAGE_NULL:
				Toast.makeText(context, "服务器无响应", Toast.LENGTH_SHORT).show();
				showLoadFailView();
				break;
			}
		}
	};
	public NewsPageListView(Context context,int bid) {
		super(context);
		this.context=context;
		mInflater=LayoutInflater.from(context);
		passageList=new OnlineListBean();
		newestBean=new NewestBean();
		this.bid=bid;
		this.setGravity(Gravity.CENTER);
		pb=new ProgressBar(context);
		pb.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));
		this.addView(pb);
	}
	//if network poor,load fail,show this view
	public void showLoadFailView(){
		this.removeAllViews();
		View v=mInflater.inflate(R.layout.news_load_again_layout, null);
		load_again_iv=(ImageView)v.findViewById(R.id.news_load_again_iv);
		load_again_iv.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
					removeAllViews();
					addView(pb);
				if(bid!=0){
				passageList.getPassageListAsync(bid,1, new CallBack<List<Passage>>() {
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
			  }else{
				  newestBean.getPassageListAsync(new CallBack<List<Passage>>() {
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
			}
		});
		this.addView(v);
	}
	// init
	private void setView(){
		this.removeAllViews();
		View v=mInflater.inflate(R.layout.news_pager_layout, null);
		prlv=(PullToRefreshListView)v.findViewById(R.id.pull_refresh_list);
		actualListView=prlv.getRefreshableView();
		footerView=mInflater.inflate(R.layout.footer_load_more, null);
		footerView.setClickable(false);
		load_more_tv=(TextView)footerView.findViewById(R.id.load_moer_tv);
		load_more_pb=(ProgressBar)footerView.findViewById(R.id.load_more_pb);
		if(bid==0){
			actualListView.addHeaderView(new NewsBannerView(context));
		}else{
			actualListView.addFooterView(footerView);
		}
		setListeners();
		this.addView(v);	
	}
	//setListener
	private void setListeners(){
		actualListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> root, View view, int position,
					long id) {
				
				Log.v(TAG, "cur item="+position);
				//item position change in pulltorefreshListView
				int targetPos;
				if(bid==0){	//have headview
					targetPos=position-2;
				}else{
					targetPos=position-1;
				}
				if(targetPos>=plist.size()){
					return;
				}
				Intent intent=new Intent(context,NewsDetailActivity.class);
				intent.putExtra("passage", plist.get(targetPos));
				context.startActivity(intent);
				((MainActivity) context).overridePendingTransition(R.anim.push_right_in,R.anim.push_left_out);
			}
		});
		prlv.setOnRefreshListener(new OnRefreshListener<ListView>() {
			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
//				String label = DateUtils.formatDateTime(context.getApplicationContext(), System.currentTimeMillis(),
//						DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);
//				// Update the LastUpdatedLabel
//				refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
				new GetDataTask().execute();
			}
		});
		if(bid!=0){
			prlv.setOnLastItemVisibleListener(new OnLastItemVisibleListener() {
				@Override
				public void onLastItemVisible() {	
					isLoadingMore=true;
					passageList.getPassageListAsync(bid, ++page, new CallBack<List<Passage>>() {
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
		}
		load_more_tv.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(!isLoadingMore){
					load_more_pb.setVisibility(VISIBLE);
					load_more_tv.setText(R.string.load_more_tv);
					passageList.getPassageListAsync(bid, ++page, new CallBack<List<Passage>>() {
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
	//set listview adapter
	public void setAdapter(List<Passage> list){
		setView();
		plist=list;
		newsAdapter=new NewsInfoListAdapter(context, list);
		actualListView.setAdapter(newsAdapter);
	}
	//do refresh
	private class GetDataTask extends AsyncTask<Void, Void, List<Passage>> {
        @Override
        protected List<Passage> doInBackground(Void... params) {
        	List<Passage> list=null;
			try {
				if(bid!=0){
					list = passageList.getPassageList(bid, 1);
				  }else{
					list=newestBean.getPassageList();
				  }
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			Log.v(TAG, list.toString());
            return list;
        }

        @Override
        protected void onPostExecute(List<Passage> result) {
            // Call onRefreshComplete when the list has been refreshed.
        	if(result!=null){
        		Log.v(TAG, "---------------------refresh");
        		int i=0;
        		for(;i<result.size();i++){
        			if(result.get(i).getPid()==plist.get(0).getPid()){
        				break;
        			}
        		}
        		if(i==0){
        			NewsDataToast.makeText(context, context.getString(R.string.new_data_toast_none),78).show();
        		}else {
        			NewsDataToast.makeText(context, context.getString(R.string.new_data_toast_message,i),78).show();
        		}
	        	plist.clear();
	        	plist.addAll(result);
	        	newsAdapter.notifyDataSetChanged();
        	}else{
        		NewsDataToast.makeText(context, context.getString(R.string.new_data_toast_exception),78).show();
        	}
        	prlv.onRefreshComplete();
            super.onPostExecute(result);
        }
	}
}
