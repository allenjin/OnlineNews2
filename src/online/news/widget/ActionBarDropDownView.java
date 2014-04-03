package online.news.widget;

import java.util.ArrayList;
import online.news.R;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

public class ActionBarDropDownView extends PopupWindow {
	
	private Context context;
	private ListView listView;
	private float abh;
	private int curPos=0;
	private ItemAdapter iadapter;
	private ArrayList<ActionBarDropDownItem> itemList=new ArrayList<ActionBarDropDownItem>();
	public ActionBarDropDownView(Context context){
		super(context);
		this.context=context;
		View v=(View)LayoutInflater.from(context).inflate(R.layout.ab_dropdown_glist, null);
		listView=(ListView)v.findViewById(R.id.ab_dropdown_list);
		setAdapter();
		this.setWidth(LayoutParams.MATCH_PARENT);
	    float scale= context.getResources().getDisplayMetrics().density;
	    abh=70*scale+0.5f;
	    float screenh=context.getResources().getDisplayMetrics().heightPixels;
	    int height=(int)(screenh-abh);
		this.setHeight(height);
		this.setContentView(v);
        setFocusable(true);
        setTouchable(true);
        setOutsideTouchable(true);
        setAnimationStyle(R.style.ab_dropdown_anim_style);
        setBackgroundDrawable(context.getResources().getDrawable(R.drawable.ab_dropdown_bg));
	}
	public void setOnItemClickListener(OnItemClickListener onItemClickListener){
		listView.setOnItemClickListener(onItemClickListener);
	}
	public void addItem(ActionBarDropDownItem aItem){
		itemList.add(aItem);
	}
	public void show(View anchor){
		 final View contentView = getContentView();
	       contentView.setOnTouchListener(new View.OnTouchListener() {
				@Override
				public boolean onTouch(View v, MotionEvent event) {
		            final int x = (int) event.getX();
		            final int y = (int) event.getY();
		            
		            if ((event.getAction() == MotionEvent.ACTION_DOWN)
		                    && ((x < 0) || (x >= getWidth()) || (y < 0) || (y >= getHeight()))) {
		                dismiss();
		                return true;
		            } else if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
		                dismiss();
		                return true;
		            } else {
		                return contentView.onTouchEvent(event);
		            }
				}
			});
	       this.showAtLocation(anchor, Gravity.CENTER, 0,(int)abh);
	}
	public class ViewHolder{
		public TextView tx;
		public ImageView icon;
		public ImageView checked;
	}
	public void notifyChanged(int position){
		curPos=position;
		iadapter.notifyDataSetChanged();
	}
	private class ItemAdapter extends BaseAdapter{
		@Override
		public View getView(int position, View view, ViewGroup parent) {
			final ViewHolder viewHolder;
			if(view==null){
				viewHolder=new ViewHolder();
				view=(View) LayoutInflater.from(context).inflate(R.layout.ab_dropdown_item, null);
				viewHolder.tx=(TextView)view.findViewById(R.id.ab_dropdown_item_tx);
				viewHolder.icon=(ImageView)view.findViewById(R.id.ab_dropdown_item_icon);
				viewHolder.checked=(ImageView)view.findViewById(R.id.ab_dropdown_item_checked);
				view.setTag(viewHolder);
			}else{
				viewHolder=(ViewHolder)view.getTag();
			}
			if(position==curPos){
				viewHolder.icon.setImageResource(itemList.get(position).getIconPressedRes());
				viewHolder.tx.setTextColor(context.getResources().getColor(R.color.ab_dropdown_item_red));
				viewHolder.checked.setVisibility(View.VISIBLE);
			}else{
				viewHolder.icon.setImageResource(itemList.get(position).getIconDefaultRes());
				viewHolder.tx.setTextColor(context.getResources().getColor(R.color.ab_dropdown_item_defalut));
				viewHolder.checked.setVisibility(View.INVISIBLE);
			}
			viewHolder.tx.setText(itemList.get(position).getTitle());
			return view;
		}
		
		@Override
		public long getItemId(int position) {
			return position;
		}
		
		@Override
		public Object getItem(int position) {
			return null;
		}
		
		@Override
		public int getCount() {
			return itemList.size();
		}
	}
	private void setAdapter(){
		iadapter=new ItemAdapter();
 		listView.setAdapter(iadapter);

	}
}
