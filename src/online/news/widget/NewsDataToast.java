package online.news.widget;
import online.news.R;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class NewsDataToast extends Toast {
	public NewsDataToast(Context context) {
		super(context);
	}
	/**
	 * 获取控件实例
	 * @param context
	 * @param text 提示消息
	 * @return
	 */
	public static NewsDataToast makeText(Context context, CharSequence text,int yoffset) {
		NewsDataToast result = new NewsDataToast(context);
		
        LayoutInflater inflate = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        
        View v = inflate.inflate(R.layout.new_data_toast, null);
        v.setMinimumWidth(dm.widthPixels);//设置控件最小宽度为手机屏幕宽度
        TextView tv = (TextView)v.findViewById(R.id.new_data_toast_message);
        tv.setText(text);
        
        result.setView(v);
        result.setDuration(600);
        result.setGravity(Gravity.TOP, 0, (int)(dm.density*yoffset));

        return result;
    }
}
