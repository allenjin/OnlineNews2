package online.news.util;

/**
 * the CallBack interface<br/>
 * P: the object to handle when onFinish() method invoked
 * @author sheling
 */
public interface CallBack <P>{
	/**
	 * when the job finished
	 * @param param the param to handle*/
	public void onFinish(P param);
	/**
	 * when exception accrued
	 * @param e exception type*/
	public void onException(Exception e);
}
