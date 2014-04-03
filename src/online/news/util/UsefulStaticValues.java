package online.news.util;

import online.news.R;

public class UsefulStaticValues {
	public static String[] ips={"10.0.2.2","121.250.221.23","202.194.15.193","202.194.14.194"};
	public static final int LOCAL_VIRTUAL_MACHINE_IP = 0;
	public static final int LOCAL_IP = 1;
	public static final int ONLINE_IP_01 = 2;
	public static final int ONLINE_IP_02 = 3;
	public static String getIpAddress() {
		return "http://"+ips[ONLINE_IP_01];
	}
	public static int[] otherBlockTitle={0,R.string.other_logo_benke,R.string.other_logo_yanjiusheng,
										R.string.other_logo_guoji,R.string.other_logo_houqin,
										R.string.other_logo_xiaoyi,R.string.other_logo_jiuye_yugao,
										R.string.other_logo_jiuye_tongzhi,R.string.other_logo_jiuye_gonggao,
										R.string.other_logo_gongxun,R.string.other_logo_guofang,
										R.string.other_logo_qingchun,R.string.other_logo_zizhu};
	public static String[] otherBlockUrl={"",
											"http://www.bkjx.sdu.edu.cn/Typeall.asp?typeid=15",
											"http://www.grad.sdu.edu.cn/ListShowNote.asp",
											"http://www.ipo.sdu.edu.cn/index.php?m=content&c=index&a=lists&catid=119",
											"http://www.hqc.sdu.edu.cn/n1/plus/list.php?tid=2",
											"http://www.xyy.sdu.edu.cn/more.php?blockid=5",
											"http://job.sdu.edu.cn/html/list/8890/list_1.html",
											"http://job.sdu.edu.cn/html/list/8014/list_1.html",
											"http://job.sdu.edu.cn/html/list/1001/list_1.html",
											"http://www.xlzx.sdu.edu.cn/new/cxyd/jstz/",
											"http://www.plaoffice.sdu.edu.cn/new/a/zhongyaotongzhi/index.html",
											"http://www.youth.sdu.edu.cn/listNews.jsp?id=7",
											"http://www.zzzx.sdu.edu.cn/more.php?blockid=15"
											};
}
