package online.news.entity;

public class Photo {
	/*
	 * Editor :xsailor 2013-1-20
	 * @entity
	 */
	public String imageURL;//the image url
	public int pid;//the passage id that is related to the picture 
	public String title;//the title of the passage
	public String subtitle;//the subtitle of the passage
	public String editTime;//编辑发布时间
	public String getImageURL() {
		return imageURL;
	}
	public void setImageURL(String imageURL) {
		this.imageURL = imageURL;
	}
	public int getPid() {
		return pid;
	}
	public void setPid(int pid) {
		this.pid = pid;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getSubtitle() {
		return subtitle;
	}
	public void setSubtitle(String subtitle) {
		this.subtitle = subtitle;
	}
	public String getEditTime() {
		return editTime;
	}
	public void setEditTime(String editTime) {
		this.editTime = editTime;
	}
	@Override
	public String toString() {
		return "Photo [imageURL=" + imageURL + ", pid=" + pid + ", title="
				+ title + ", subtitle=" + subtitle + ", editTime=" + editTime
				+ "]";
	}
	
	
}
