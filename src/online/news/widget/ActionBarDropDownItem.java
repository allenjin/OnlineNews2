package online.news.widget;

public class ActionBarDropDownItem {
	public String title;
	public int iconDefaultRes;
	public int iconPressedRes;
	public ActionBarDropDownItem(String title,int iconDefaultRes,int iconPressedRes){
		this.title=title;
		this.iconDefaultRes=iconDefaultRes;
		this.iconPressedRes=iconPressedRes;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public int getIconDefaultRes() {
		return iconDefaultRes;
	}
	public void setIconDefaultRes(int iconDefaultRes) {
		this.iconDefaultRes = iconDefaultRes;
	}
	public int getIconPressedRes() {
		return iconPressedRes;
	}
	public void setIconPressedRes(int iconPressedRes) {
		this.iconPressedRes = iconPressedRes;
	}
}
