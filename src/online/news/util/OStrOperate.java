package online.news.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * 字符串处理相关的工具类
 */
public class OStrOperate {

    // 禁止调用构造方法
    private OStrOperate() {
    }

    /**
     * 检查字符串是否存在值
     * 
     * @param str 待检验的字符串
     * @return 当 str 不为 null 或 "" 就返回 true
     */
    public static boolean hasValue(String str) {
        return (str != null && !"".equals(str));
    }
    
    /**
     * 得到string的值
     * 
     * @param str 待检验的字符串
     * @param d 默认值
     * @return 当 str 为null时返回默认值,否则返回str本身
     */
    public static String getValue(String str,String d){
    	return str == null ? d:str;
    }

    
    /**
     * 根据 timestamp 生成各类时间状态串
     * 
     * @param timestamp 距1970 00:00:00 GMT的秒数
     * @return 时间状态串(如：刚刚5分钟前)
     */
    public static String getTimeState(String timestamp) {
        if (timestamp == null || "".equals(timestamp)) {
            return "";
        }

        try {
            long _timestamp = Long.parseLong(timestamp) * 1000;
            if (System.currentTimeMillis() - _timestamp < 1 * 60 * 1000) {
                return "刚刚";
            } else if (System.currentTimeMillis() - _timestamp < 30 * 60 * 1000) {
                return ((System.currentTimeMillis() - _timestamp) / 1000 / 60)
                        + "分钟前";
            } else {
                Calendar now = Calendar.getInstance();
                Calendar c = Calendar.getInstance();
                c.setTimeInMillis(_timestamp);
                if (c.get(Calendar.YEAR) == now.get(Calendar.YEAR)
                        && c.get(Calendar.MONTH) == now.get(Calendar.MONTH)
                        && c.get(Calendar.DATE) == now.get(Calendar.DATE)) {
                    SimpleDateFormat sdf = new SimpleDateFormat("今天 HH:mm");
                    return sdf.format(c.getTime());
                }
                if (c.get(Calendar.YEAR) == now.get(Calendar.YEAR)
                        && c.get(Calendar.MONTH) == now.get(Calendar.MONTH)
                        && c.get(Calendar.DATE) == now.get(Calendar.DATE) - 1) {
                    SimpleDateFormat sdf = new SimpleDateFormat("昨天 HH:mm");
                    return sdf.format(c.getTime());
                } else if (c.get(Calendar.YEAR) == now.get(Calendar.YEAR)) {
                    SimpleDateFormat sdf = new SimpleDateFormat("M月d日 HH:mm");
                    return sdf.format(c.getTime());
                } else {
                    SimpleDateFormat sdf = new SimpleDateFormat(
                            "yyyy年M月d日 HH:mm");
                    return sdf.format(c.getTime());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
    
    /**turn the long size to string with K(KB) or M(MB)
     * @param size the size in bytes*/
    public static String TurnByteSizeToString(long size){
		long mb,kb;
    	kb = size / 1024;
    	mb = kb / 1024;
    	return mb == 0 ? kb +"K" : mb +"M";
    }
}
