package online.news.adapter;
import java.util.ArrayList;
import java.util.List;
import online.news.beans.NewestBean;
import online.news.beans.OnlineListBean;
import online.news.entity.Passage;
import online.news.util.CallBack;
import online.news.widget.NewsPageListView;
import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import online.news.R;

public class NewsPagerAdapter extends PagerAdapter{
    private String[] labels;
    private List<NewsPageListView> viewlist;
    private Activity context;
	private OnlineListBean passageList;
	private NewestBean newestList;
    private List<Passage> plist;
    private boolean[] positionValue=new boolean[6];
    private static final String TAG=NewsPagerAdapter.class.getSimpleName();
	private static final int MESSAGE_INIT_LIST=1;
	private static final int MESSAGE_EXCEPTION=2;
	private static final int MESSAGE_NULL=3;
	private static final int[] BIDS = {0,2,3,4,5,6};
	private Handler handler=new Handler(){
		@Override
		public void handleMessage(Message msg) {
			switch(msg.what){
			case MESSAGE_INIT_LIST:
				viewlist.get(msg.arg1).setAdapter(plist);
				break;
			case MESSAGE_EXCEPTION:
				Toast.makeText(context, "网络不给力", Toast.LENGTH_SHORT).show();
				viewlist.get(msg.arg1).showLoadFailView();
				break;
			case MESSAGE_NULL:
				Toast.makeText(context, "服务器无响应", Toast.LENGTH_SHORT).show();
				viewlist.get(msg.arg1).showLoadFailView();
				Log.v(TAG, "null--->"+msg.arg1);
				break;
			}
		}
	};
	public NewsPagerAdapter(Activity activity){
		this.context=activity;
		labels=context.getResources().getStringArray(R.array.online_news_labels);
		viewlist=new ArrayList<NewsPageListView>();
		for(int i=0;i<labels.length;i++){
			viewlist.add(new NewsPageListView(context, BIDS[i]));
			positionValue[i]=false;
		}	
	}

	@Override
	public void setPrimaryItem(ViewGroup container, int position, Object object) {
		if(positionValue[position]==false){
			Log.v(TAG, "primary------------------"+position);
			getNewsData(position);		
			positionValue[position] = true;
		}
	}

	@Override
	public int getCount() {
		return viewlist.size();
	}
	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView(viewlist.get(position));
	}
	
	@Override
	public CharSequence getPageTitle(int position) {
		 return labels[position % labels.length];
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		NewsPageListView view=viewlist.get(position);
		container.addView(view);
		return view;
	}
	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return arg0==arg1;
	}	
	/**
	 * 
	 * @param id	对应viewpager的position
	 */
	private void getNewsData(final int id){
		passageList=new OnlineListBean();
		newestList=new NewestBean();	
		//判断是否为头版头条页面
		if(id==0){
			newestList.getPassageListAsync(new CallBack<List<Passage>>() {
				@Override
				public void onFinish(List<Passage> param) {
					Message msg = Message.obtain();
					msg.arg1=0;
					if(param!=null){
						msg.what=MESSAGE_INIT_LIST;
						plist=param;
						handler.sendMessage(msg);
					}else{
						msg.what=MESSAGE_NULL;
						handler.sendMessage(msg);
					}
				}

				@Override
				public void onException(Exception e) {		
					Message msg = Message.obtain();
				  	msg.arg1=id;
					msg.what = MESSAGE_EXCEPTION;
					handler.sendMessage(msg);
				}
				
			});
		}else{
			passageList.getPassageListAsync(BIDS[id],1, new CallBack<List<Passage>>() {
				@Override
				public void onFinish(List<Passage> param) {
					Message msg = Message.obtain();
					msg.arg1=id;
					if(param!=null){
						msg.what=MESSAGE_INIT_LIST;
						plist=param;
						handler.sendMessage(msg);
					}else{
						msg.what=MESSAGE_NULL;
						handler.sendMessage(msg);
					}
				}
				@Override
				public void onException(Exception e) {
				  	Message msg = Message.obtain();
				  	msg.arg1=id;
					msg.what = MESSAGE_EXCEPTION;
					handler.sendMessage(msg);
				}
			});
		}
	}
}
