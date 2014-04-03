package online.news;

import java.util.LinkedList;
import java.util.List;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.actionbarsherlock.app.SherlockActivity;
/**
 * 新手引导界面
 * @author AllenJin
 *
 */
public class GuideActivity extends SherlockActivity implements ViewPager.OnPageChangeListener{
	private ViewPager mPager;
	private LinearLayout mTipLayout;
	private ImageView lastTipImg;
	private Button lauchBtn;
	private boolean isFirst;	//用于区分是由哪个Activity调用的
	//新手引导界面图片资源数组
	private int[] guidePicRes={R.drawable.guide_pic_1,R.drawable.guide_pic_2,R.drawable.guide_pic_3,
							  R.drawable.guide_pic_4,R.drawable.guide_pic_5,R.drawable.guide_pic_6};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_guide_layout);
		isFirst=getIntent().getBooleanExtra("call_mode", true);
		findViews();
		init();
	}
	private void findViews(){
		lauchBtn=new Button(this);
		lauchBtn.setBackgroundResource(R.drawable.search_btn_bg);
		lauchBtn.setText("立即体验");
		lauchBtn.setTextColor(getResources().getColor(R.color.sm_item_color_default));
		lauchBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(isFirst){	//若是由第一次安装时调用，点击按钮则进入主界面
					Intent intent=new Intent(GuideActivity.this,MainActivity.class);
					startActivity(intent);
	         		finish();
	         		overridePendingTransition(R.anim.push_right_in, R.anim.push_left_out);
				}else{			//若由设置界面中新手引导调用，则直接finish
					finish();
				}

			}
		});
		mPager=(ViewPager)findViewById(R.id.guide_vp);
		mPager.setOnPageChangeListener(this);
		mTipLayout=(LinearLayout)findViewById(R.id.guide_tip);
	}
	private void init(){
		mPager.setAdapter(new GuidePicAdapter(this));
		for(int i=0;i<guidePicRes.length;i++){
			ImageView timg=new ImageView(this);
			if(i==0){
				lastTipImg=timg;
				timg.setImageResource(R.drawable.guide_indication_focused);
			}else{
				timg.setImageResource(R.drawable.guide_indication_normal);
			}
			LinearLayout.LayoutParams tl=new LinearLayout.LayoutParams(14,14);
			tl.setMargins(0, 0, 5, 0);
			timg.setLayoutParams(tl);
			mTipLayout.setGravity(Gravity.CENTER);
			mTipLayout.addView(timg, i);
		}
	}
	private class GuidePicAdapter extends PagerAdapter{
		private List<LinearLayout> vlist;
		public GuidePicAdapter(Context context){
			vlist=new LinkedList<LinearLayout>();
			for(int i=0;i<guidePicRes.length;i++){
				LinearLayout imgLayout=new LinearLayout(context);
				imgLayout.setBackgroundResource(guidePicRes[i]);
				if(i==guidePicRes.length-1){
					LinearLayout.LayoutParams tl=new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
					imgLayout.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.BOTTOM);
					tl.setMargins(0, 0, 0, 25);
					imgLayout.addView(lauchBtn,tl);
				}
				vlist.add(imgLayout);
			}
		}
		@Override
		public int getCount() {
			return vlist.size();
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0==arg1;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView(vlist.get(position));
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			container.addView(vlist.get(position));
			return vlist.get(position);
		}
		
	}
	@Override
	public void onPageScrollStateChanged(int position) {
		
	}
	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
		
	}
	@Override
	public void onPageSelected(int position) {
		lastTipImg.setImageResource(R.drawable.guide_indication_normal);
		ImageView curTimg=	(ImageView) mTipLayout.getChildAt(position);
		curTimg.setImageResource(R.drawable.guide_indication_focused);
		lastTipImg=curTimg;
	}
}
