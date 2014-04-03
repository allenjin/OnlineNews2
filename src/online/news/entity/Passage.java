package online.news.entity;

import java.io.Serializable;

public class Passage implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -6661892682944712235L;
	/*
	 * by xsailor 2012.9.20
	 *  实体类Passage.java,整个应用的中�?
	 *  �?��的文章，通知，评论等学线主页上所能看到短文都包含在内
	 */
	public int blockId;//板块id，用于区分是评论还是通知等类�?
	public int pid;//每篇文章在数据库有一个单独的id
	public int priorty;//等级
	public String title;//标题
	public String content;//内容
//	@Expose
//	public ArrayList<String> attachments;//附件
	public String addTime;//添加时间
	public String editTime;//编辑发布时间
    public String subTitle;//副标�?
    public String editor;//作�?
    public String reporter;//记�?
    public String source;//来源
    public String keyWord;//关键�?
    public Passage(){
    	
    }
    public Passage(int blockId,int passageId,String passageName,String passageContent){
    	
    }
	public int getBlockId() {
		return blockId;
	}


	public void setBlockId(int blockId) {
		this.blockId = blockId;
	}

	

//	public ArrayList<String> getAttachments() {
//		return attachments;
//	}
//
//	public void setAttachments(ArrayList<String> attachments) {
//		this.attachments = attachments;
//	}
	public int getPid() {
		return pid;
	}
	public void setPid(int pid) {
		this.pid = pid;
	}
	public int getPriorty() {
		return priorty;
	}
	public void setPriorty(int priorty) {
		this.priorty = priorty;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getAddTime() {
		return addTime;
	}
	public void setAddTime(String addTime) {
		this.addTime = addTime;
	}
	public String getEditTime() {
		return editTime;
	}
	public void setEditTime(String editTime) {
		this.editTime = editTime;
	}
	public String getSubTitle() {
		return subTitle;
	}
	public void setSubTitle(String subTitle) {
		this.subTitle = subTitle;
	}
	public String getEditor() {
		return editor;
	}
	public void setEditor(String editor) {
		this.editor = editor;
	}
	public String getReporter() {
		return reporter;
	}
	public void setReporter(String reporter) {
		this.reporter = reporter;
	}
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	public String getKeyWord() {
		return keyWord;
	}
	public void setKeyWord(String keyWord) {
		this.keyWord = keyWord;
	}
	@Override
	public String toString() {
		return "Passage [blockId=" + blockId + ", pid=" + pid + ", priorty="
				+ priorty + ", title=" + title + ", content=" + content
				+ ", addTime=" + addTime + ", editTime=" + editTime
				+ ", subTitle=" + subTitle + ", editor=" + editor
				+ ", reporter=" + reporter + ", source=" + source
				+ ", keyWord=" + keyWord + "]";
	}

}
