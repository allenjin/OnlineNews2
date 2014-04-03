package online.news.widget;

import java.util.LinkedList;
import java.util.List;
import online.news.NewsDetailActivity;
import online.news.R;
import online.news.adapter.BannerPagerAdapter;
import online.news.beans.PhotoListBean;
import online.news.entity.Passage;
import online.news.entity.Photo;
import online.news.util.AsyncBitmapLoader;
import online.news.util.CallBack;
import online.news.util.FileNameUtil;
import online.news.util.AsyncBitmapLoader.ImageCallBack;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 此类为头版头条页面的图片banner的初始化类
 * @author Allen
 *
 */
public class NewsBannerView extends LinearLayout implements ViewPager.OnPageChangeListener{
	private Context context;
	private FrameLayout mFrameLayout;
	private TextView mHintTv;
	private LinearLayout mTipLinearLayout;
	private ViewPager mViewPager;
	private ImageView lastTipImageView;
	private int frameheight=180;
	private View view;
	private PhotoListBean photoBean;
	private AsyncBitmapLoader abLoader;
	private List<Photo> photoList;
	private int photoSize=0;
	private static final int MESSAGE_PHOTOLIST_DONE=1;
	private static final int MESSAGE_PHOTOLIST_EXCEPTION=2;
	private static final String TAG=NewsBannerView.class.getSimpleName();
	private Handler handler=new Handler(){
		@Override
		public void handleMessage(Message msg) {
			switch(msg.what){
			case MESSAGE_PHOTOLIST_DONE:
				setView();
				Log.v(TAG, photoList.toString());
				break;
			case MESSAGE_PHOTOLIST_EXCEPTION:
				Toast.makeText(context, "exception", Toast.LENGTH_SHORT).show();
				break;
			}
		}
		
	};
	public NewsBannerView(Context context) {
		super(context);
		this.context=context;
		this.setOrientation(LinearLayout.VERTICAL);
		getPhotoListFromNet();
	}
	private void getPhotoListFromNet(){
		photoBean=new PhotoListBean();
		photoBean.getPassageListAsync(new CallBack<List<Photo>>() {
			@Override
			public void onFinish(List<Photo> param) {
				photoList=param;
				handler.sendEmptyMessage(MESSAGE_PHOTOLIST_DONE);
			}
			@Override
			public void onException(Exception e) {
				handler.sendEmptyMessage(MESSAGE_PHOTOLIST_EXCEPTION);
			}
		});
	}
	private void setView(){
		abLoader=new AsyncBitmapLoader();
		LinkedList<View> vlist=new LinkedList<View>();
		final int screenWidth = getResources().getDisplayMetrics().widthPixels;  
		view=LayoutInflater.from(context).inflate(R.layout.news_head_layout, null);
		mViewPager=(ViewPager) view.findViewById(R.id.hb_vf);
		mHintTv=(TextView)view.findViewById(R.id.hb_tx);
		mHintTv.setText(photoList.get(0).title);
		mFrameLayout=(FrameLayout)view.findViewById(R.id.hb_main);
		mTipLinearLayout=(LinearLayout)view.findViewById(R.id.hb_tip);
		//init view
		LayoutParams llayoutParams=new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT);
		photoSize=photoList.size();
		for(int i=0;i<photoSize;i++){
			final Photo photo=photoList.get(i);
			//add pic
			ImageView image=new ImageView(context);
			image.setScaleType(ScaleType.FIT_XY);
			Bitmap bitmap=BitmapFactory.decodeResource(getResources(), R.drawable.online_banner_default);
			Bitmap bitmap2=getBitmap(bitmap,screenWidth);
			frameheight=bitmap2.getHeight();
			image.setImageBitmap(bitmap);
			try{
				abLoader.loadBitmap(image, FileNameUtil.getNewFileURL(photo.imageURL), new ImageCallBack() {
					@Override
					public void imageLoad(ImageView imageView, Bitmap bitmap) {
						if(bitmap!=null){
							Bitmap bitmap2=getBitmap(bitmap,screenWidth);
							frameheight=bitmap2.getHeight();
							imageView.setImageBitmap(bitmap);
							android.view.ViewGroup.LayoutParams layoutParams=mFrameLayout.getLayoutParams();
							layoutParams.height=frameheight;
							mFrameLayout.setLayoutParams(layoutParams);
						}
					}
				});
			}catch (Exception e) {
				
			}
			android.view.ViewGroup.LayoutParams layoutParams=mFrameLayout.getLayoutParams();
			layoutParams.height=frameheight;
			mFrameLayout.setLayoutParams(layoutParams);
				LinearLayout ll=new LinearLayout(context);
				ll.setLayoutParams(llayoutParams);
				ll.setOrientation(LinearLayout.VERTICAL);
				ll.addView(image,llayoutParams);
				ll.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						Passage p=new Passage();
						p.pid=photo.getPid();
						p.title=photo.getTitle();
						p.editTime=photo.getEditTime();
						p.editor="学生在线";
						p.reporter="学生在线";
						Intent intent=new Intent(context,NewsDetailActivity.class);
						intent.putExtra("passage", p);
						context.startActivity(intent);
					}
				});
				vlist.add(ll);
				//init tipview
				ImageView tipImg=new ImageView(context);
				tipImg.setLayoutParams(new LayoutParams((screenWidth/photoList.size()),5));
				tipImg.setBackgroundColor(getResources().getColor(R.color.banner_tip_bg));
				mTipLinearLayout.addView(tipImg);	
		}
		mViewPager.setAdapter(new BannerPagerAdapter(vlist));
		mViewPager.setOnPageChangeListener(this);
		//图片自动循环
		//add textHint
			removeAllViews();
			addView(view);
			//frame
			if(mTipLinearLayout.getChildCount()!=0){
				lastTipImageView=(ImageView) mTipLinearLayout.getChildAt(0);
				lastTipImageView.setBackgroundColor(getResources().getColor(R.color.banner_tip_focused));
			}
	}	
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		getParent().requestDisallowInterceptTouchEvent(true);
		super.dispatchTouchEvent(ev);
		return true;
	}
	private Bitmap getBitmap(Bitmap bitmap, int width) {
		int w=bitmap.getWidth();
		int h=bitmap.getHeight();
		Matrix matrix=new Matrix();
		float scale=(float)width/w;
		matrix.postScale(scale, scale);
		return Bitmap.createBitmap(bitmap, 0, 0, w, h, matrix, true);
	}
	@Override
	public void onPageScrollStateChanged(int position) {
		
	}
	@Override
	public void onPageScrolled(int position, float arg1, int arg2) {
		
	}
	@Override
	public void onPageSelected(int position) {
		mHintTv.setText(photoList.get(position).title);
		ImageView curTipImg=(ImageView) mTipLinearLayout.getChildAt(position);
		curTipImg.setBackgroundColor(getResources().getColor(R.color.banner_tip_focused));
		lastTipImageView.setBackgroundColor(getResources().getColor(R.color.banner_tip_bg));
		lastTipImageView=curTipImg;
	}
}
