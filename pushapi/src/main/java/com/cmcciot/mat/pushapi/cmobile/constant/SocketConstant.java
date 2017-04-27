package com.cmcciot.mat.pushapi.cmobile.constant;

/**
 * @author PTERO Socket常量
 */
public final class SocketConstant {

	/**
	 * Private Constructor
	 */
	private SocketConstant() {
	}

	/**
	 * 配置文件存放路径
	 */
	public static String CONFIG_FILE_PATH = null;

	/**
	 * 短信协议类型
	 */
	public static final String SGIP = "0";

	/**
	 * 短信协议类型
	 */
	public static final String SMPP = "1";

	/**
	 * 短信协议类型
	 */
	public static final String ISAG = "2";

	/**
	 * True
	 */
	public static final boolean TRUE_FLAG = true;
	/**
	 * FALSE
	 */
	public static final boolean FALSE_FLAG = false;

	/**
	 * MsgId初始值
	 */
	public static final String NULL_FLAG = null;

	/*
	 * 报文符号
	 */
	/**
	 * ZERO
	 */
	public static final int ZERO = 0;
	/**
	 * ONE
	 */
	public static final int ONE = 1;
	/**
	 * TWO
	 */
	public static final int TWO = 2;
	/**
	 * THREE
	 */
	public static final int THREE = 3;
	/**
	 * FOUR
	 */
	public static final int FOUR = 4;
	/**
	 * FIVE
	 */
	public static final int FIVE = 5;
	/**
	 * SIX
	 */
	public static final int SIX = 6;
	/**
	 * SEVEN
	 */
	public static final int SEVEN = 7;
	/**
	 * EIGHT
	 */
	public static final int EIGHT = 8;
	/**
	 * NINE
	 */
	public static final int NINE = 9;
	/**
	 * TEN
	 */
	public static final int TEN = 10;
	/**
	 * ELEVEN
	 */
	public static final int ELEVEN = 11;
	/**
	 * TWELVE
	 */
	public static final int TWELVE = 12;

	/**
	 * 小于号
	 */
	public static final String SIGN_LESS = "<";
	/**
	 * 大于号
	 */
	public static final String SIGN_MORE = ">";
	/**
	 * 竖线分隔符
	 */
	public static final String SIGN_ERECTION = "|";
	/**
	 * 星号
	 */
	public static final String SIGN_STAR = "#";

	/**
	 * 下划线
	 */
	public static final String SIGN_DOWN = "_";

	/**
	 * Socket 读取长度
	 */
	public static final int READ_LENGTH = 256 * 256;

	/**
	 * 发送队列最大长度
	 */
	public static final int MAX_SEND_LIST_LENGTH = 1024;

	/**
	 * 发送线程等待时间
	 */
	public static final int SEND_THREAD_WAIT_TIME = 2000;

	/*
	 * 消息类型说明
	 */
	/**
	 * 不合法的消息数据
	 */
	public static final String ERROR_MSG_LENGTH = "ERROR MESSAGE LENGTH!";

	/**
	 * 单次定位指令
	 */
	public final static String ONE_TIME_POSITION_COMMAND = "PDW";

	/**
	 * 短信更新指令
	 */
	public final static String BATTERY = "BATTERY";

	/**
	 * 短信更新指令
	 */
	public final static String UPDATE = "UPDATE";

	/**
	 * SMPP_PROTOCOL
	 */
	public static final String CMPP_PROTOCOL = "cmpp";

	/**
	 * SMGP_PROTOCOL
	 */
	public static final String SMGP_PROTOCOL = "smgp";

	/**
	 *ISAG_PROTOCOL
	 */
	public static final String ISAG_PROTOCOL = "isag";

	/**
	 * 企信通
	 */
	public static final String NJ_ABLE_PROTOCOL = "njable";

	/**
	 * 企信通
	 */
	public static final String HZ_ABLE_PROTOCOL = "hzable";

	/**
	 * 链路检测消息类型
	 */
	public static final int CHECK_LINK = 99;

	/**
	 * 手机初始化消息
	 */
	public static final int PHONE_INIT = 15;

	/**
	 * 手机初始化消息报文长度
	 */
	public static final int PHONE_INIT_LENGTH = 5;

	/**
	 * 单次定位消息
	 */
	public static final int SINGLE_TRACK = 20;

	/**
	 * 单次定位消息长度
	 */
	public static final int SINGLE_TRACK_LENGTH = 5;

	/**
	 * 向主用户发送具体的位置信息消息
	 */
	public static final int SINGLE_SEND_LOCATION = 23;

	/**
	 * 向主用户发送具体的位置信息消息
	 */
	public static final int LAST_SEND_MAIN_USER_LOCATION = 33;

	/**
	 * 向主用户发送具体的位置信息消息
	 */
	public static final int AREA_SEND_MAIN_USER_LOCATION = 47;

	/**
	 * 向主用户发送具体的位置信息消息
	 */
	public static final int SOS_SEND_MAIN_USER_LOCATION = 53;

	/**
	 * 向主用户发送具体的位置信息消息长度
	 */
	public static final int SEND_MAIN_USER_LOCATION_LENGTH = 5;

	/**
	 * 持续定位命令发送消息
	 */
	public static final int LAST_TRACK_SET = 30;

	/**
	 * 持续定位命令发送消息长度
	 */
	public static final int LAST_TRACK_SET_LENGTH = 7;

	/**
	 * 持续定位取消消息
	 */
	public static final int LAST_TRACK_CANCEL = 32;

	/**
	 * 区域告警全部取消消息
	 */
	public static final int AREA_TRACK_CANCEL = 43;

	/**
	 * 持续定位取消消息长度
	 */
	public static final int LAST_TRACK_CANCEL_LENGTH = 5;

	/**
	 * 区域告警设置消息
	 */
	public static final int AREA_TRACK_SET = 40;

	/**
	 * 区域告警设置消息长度
	 */
	public static final int AREA_TRACK_SET_LENGTH = 13;

	/**
	 * 区域告警取消一个区域消息
	 */
	public static final int AREA_TRACK_CANCEL_ONE = 45;

	/**
	 * GIS检测报文
	 */
	public static final int CHECK_GIS_MESSAGE = 98;

	/**
	 * GIS检测报文长度
	 */
	public static final int GIS_CHECK_MESSAGE_LENGTH = 2;

	/**
	 * 区域告警取消一个区域消息长度
	 */
	public static final int AREA_TRACK_CANCEL_ONE_LENGTH = 9;

	/**
	 * 信息报告
	 */
	public static final String REPORT_MESSAGE = "21";

	/**
	 * 手机按键设置结果
	 */
	public static final String KEY_SET_MESSAGE = "17";

	/**
	 * 单次定位位置上报
	 */
	public static final String LOCATION_SINGLE = "22";

	/**
	 * 新单次定位位置上报
	 */
	public static final String LOCATION_SINGLE2 = "27";

	/**
	 * 持续定位位置上报
	 */
	public static final String LOCATION_LAST = "31";

	/**
	 * 区域告警位置上报
	 */
	public static final String LOCATION_AREA = "42";

	/**
	 * SOS位置上报
	 */
	public static final String LOCATION_SOS = "52";

	/**
	 * GIS检测报文
	 */
	public static final String CHECK_GIS_MESSAGE_STRING = "98";

	/**
	 * 链路检测消息内容
	 */
	public static final String CHECK_LINK_MESSAGE = "<99|How are you?>";

	/**
	 * 用户手机号
	 */
	public static final String USER_PHONE = "userPhone";

	/**
	 * 消息发送序列号
	 */
	public static final String SEQUENCE_NO = "sequenceNo";

	/**
	 * Submit Response 消息
	 */
	public static final String SUBMIT_RESPONSE = "20";

	/**
	 * 成功状态
	 */
	public static final String OK = "ok";

	/**
	 * 错误状态
	 */
	public static final String ERROR = "error";

	/**
	 * 链路检测时间
	 */
	public static int LINK_TEST_TIME = 0;
	
	

}
