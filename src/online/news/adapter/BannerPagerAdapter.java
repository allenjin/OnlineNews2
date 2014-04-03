package online.news.adapter;

import java.util.LinkedList;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

public class BannerPagerAdapter extends PagerAdapter {

	LinkedList<View> vlist;
	public BannerPagerAdapter(LinkedList<View> vlist){
		this.vlist=vlist;
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
