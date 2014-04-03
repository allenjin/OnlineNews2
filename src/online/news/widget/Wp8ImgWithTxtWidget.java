package online.news.widget;

import online.news.R;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class Wp8ImgWithTxtWidget extends LinearLayout {
	private ImageView blockIcon;
	private TextView blockText;
	public Wp8ImgWithTxtWidget(Context context) {
		super(context);
		init(context,null);
	}

	public Wp8ImgWithTxtWidget(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context,attrs);
	}

	public Wp8ImgWithTxtWidget(Context context, AttributeSet attrs, int defStyle) {
		this(context, attrs);
	}
	
	private void init(Context context,AttributeSet attrs){
		blockIcon=new ImageView(context);
		blockIcon.setLayoutParams(new LinearLayout.LayoutParams(80, 80));
		blockText=new TextView(context);
		TypedArray ta = context.obtainStyledAttributes(attrs,R.styleable.otherNewsBlock);
		String text=ta.getString(R.styleable.otherNewsBlock_blockText);
		Drawable icon=ta.getDrawable(R.styleable.otherNewsBlock_blockDrawable);
		blockIcon.setImageDrawable(icon);
		blockText.setText(text);
		blockText.setTextSize(20);
		blockText.setTextColor(context.getResources().getColor(R.color.sm_item_color_default));
		blockText.setGravity(Gravity.CENTER);
		this.setGravity(Gravity.CENTER);
		this.setOrientation(LinearLayout.VERTICAL);
		this.addView(blockIcon, 0);
		this.addView(blockText, 1);
	}

}
