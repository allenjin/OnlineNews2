package online.news.adapter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import online.news.entity.Passage;
import online.news.util.OStrOperate;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import online.news.R;

public class NewsInfoListAdapter extends BaseAdapter {
	private LayoutInflater mInflater;
	private List<Passage> datalist;
	private SimpleDateFormat ff;
	public NewsInfoListAdapter(Context context,List<Passage> datalist){
		this.mInflater=LayoutInflater.from(context);
		this.datalist=datalist;
		ff = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
	}
	@Override
	public int getCount() {
		return datalist.size();
	}

	@Override
	public Object getItem(int position) {
		return datalist.get(position);
	}

	@Override
	public long getItemId(int arg0) {
		return 0;
	}
	private class ViewHolder{
		//public ImageView headIcon;
		public TextView title;
		public TextView pubtime;
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final ViewHolder holder;
		if(convertView==null){
			holder=new ViewHolder();
			convertView=mInflater.inflate(R.layout.news_listview_item, null);
			//holder.headIcon=(ImageView) convertView.findViewById(R.id.news_item_img);
			holder.title=(TextView)convertView.findViewById(R.id.news_vlist_title);
			holder.pubtime=(TextView)convertView.findViewById(R.id.news_vlist_pubtime);
			convertView.setTag(holder);
		}else{
			holder=(ViewHolder) convertView.getTag();
		}
		//set data
		Passage p = datalist.get(position);
		holder.title.setText(p.getTitle());
		if(p.getEditTime()!=null){
		Date d = null;
			try {
				d= ff.parse(p.getEditTime());
				holder.pubtime.setText(OStrOperate.getTimeState(d.getTime()/1000+""));
			} catch (ParseException e) {
				holder.pubtime.setText(p.getEditTime());
				e.printStackTrace();
			}
		}
		return convertView;
	}

}
