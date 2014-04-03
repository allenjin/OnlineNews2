package online.news.adapter;

import java.util.LinkedList;
import online.news.R;
import online.news.entity.AppInfo;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class AppListAdapter extends BaseAdapter {
	private Context context;
	private LinkedList<AppInfo> appDataList;
	private PackageInfo packageinfo;
	private LayoutInflater mInflater;
	public AppListAdapter(Context context,LinkedList<AppInfo> appDataList){
		this.context=context;
		this.appDataList=appDataList;
		mInflater=LayoutInflater.from(context);
	}
	@Override
	public int getCount() {
		return appDataList.size();
	}

	@Override
	public Object getItem(int position) {
		return appDataList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}
	private class ViewHolder{
		public ImageView appBg;
		public ImageView appLogo;
		public TextView appText;
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final AppInfo appInfo=appDataList.get(position);
		ViewHolder holder=null;
		if(convertView==null){
			holder=new ViewHolder();
			convertView=mInflater.inflate(R.layout.app_list_item_layout, null);
			holder.appBg=(ImageView)convertView.findViewById(R.id.app_img);
			holder.appLogo=(ImageView)convertView.findViewById(R.id.app_logo);
			holder.appText=(TextView)convertView.findViewById(R.id.app_title);
			convertView.setTag(holder);
		}else{
			holder=(ViewHolder)convertView.getTag();
		}
		holder.appBg.setImageResource(appInfo.appBgRes);
		holder.appLogo.setImageResource(appInfo.appLogoRes);
		holder.appText.setText(appInfo.appName);
		holder.appLogo.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
			if(appInfo.appType==AppInfo.APP_TYPE_ANDROID){	//学线Android应用
				try{
					packageinfo=context.getPackageManager().getPackageInfo(appInfo.packageInfo, 0);
				}catch (NameNotFoundException e) {
					packageinfo=null;
					
				}
				if(packageinfo!=null){
					ComponentName comp=new ComponentName(appInfo.packageInfo,appInfo.activityInfo);
					Intent intent=new Intent();
					intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					intent.setComponent(comp);
					context.startActivity(intent);
				}else{
					AlertDialog.Builder dialog=new AlertDialog.Builder(context);
					dialog.setMessage("您当前未添加该应用，是否下载？")
					.setNegativeButton("取消", null)
					.setPositiveButton("确定", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							Intent intent=new Intent(Intent.ACTION_VIEW, Uri.parse(appInfo.httpUrl));
							context.startActivity(intent);
						}
					}).show();
				}
			  }else if(appInfo.appType==AppInfo.APP_TYPE_HTML5){	//HTML5应用
				  Intent intent=new Intent(Intent.ACTION_VIEW, Uri.parse(appInfo.httpUrl));
				  context.startActivity(intent);
			  }
			}
		});
		return convertView;
	}

}
