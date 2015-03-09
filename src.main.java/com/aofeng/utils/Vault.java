package com.aofeng.utils;

public class Vault {
	/**
	 * 登录用户名
	 */
	public static String USER_NAME = "USER_NAME";
	/**
	 * 登录密码
	 */
	public static String PASSWORD ="ENCRYPT";	
	//部门
	public static String department = "department";
	/**
	 * 安检人员名字,取登陆信息的用户名
	 */
	public  static String CHECKER_NAME ="CHECKER_NAME";
	/**
	 * 用户ID
	 */
	public static String USER_ID = "USER_ID";
	
	
	/**
	 * 上传标记1 0000 0001
	 */
	public static int UPLOAD_FLAG = 1;
	
	/**
	 * 已检标记2 0000 0010
	 */
	public static int INSPECT_FLAG = 2;
	
	/**
	 * 新增标记4 0000 0100
	 */
	public static int NEW_FLAG = 4;
	
	/**
	 * 删除标记8 0000 1000
	 */
	public static int DELETE_FLAG = 8;
	
	/**
	 * 维修标记16 0001 0000
	 */
	public static int REPAIR_FLAG = 16;
	
	/**
	 * 拒检标记32 0010 0000
	 */
	public static int DENIED_FLAG = 32;
	
	/**
	 * 无人标记64 0100 0000
	 */
	public static int NOANSWER_FLAG = 64;
	

	public static String REPAIRED_NOT="未维修";
	public static String REPAIRED_UNUPLOADED="未上传";
	public static String REPAIRED_UPLOADED="已上传";
	
	public static  String packageName= "com.aofeng.safecheck";
//	// 数据服务地址
//		public static String  DB_URL = "http://113.140.20.122:9990/rs/db/";
//	//入户安检服务
//		public static String  IIS_URL = "http://113.140.20.122:9990/rs/iis/";
//	//认证服务地址
//		public static String AUTH_URL = "http://113.140.20.122:9992/rs/user/";
//	public static String TUNNEL_URL = "http://113.140.20.122:9990/rs/tunnel/http%3A%7C%7C192.168.2.41%7Crs%7Cdb%7C";
//	public static  String downloadURL = "http://113.140.20.122:9990/changansafecheck.apk";
//	public static  String checkVersionURL = "http://113.140.20.122:9990/rs/db/one/from%20t_singlevalue%20where%20name='safecheck版本号'";
//	// 数据服务地址
//	public static String  DB_URL = "http://192.168.2.45:8080/rs/db/";
//	//入户安检服务
//	public static String  IIS_URL = "http://192.168.2.45:8080/rs/iis/";
//	//认证服务地址
//	public static String AUTH_URL = "http://192.168.2.45:83/rs/user/";
//	public static  String downloadURL = "http://192.168.2.45:8080/changansafecheck.apk";
//	public static  String checkVersionURL = "http://192.168.2.45:8080/rs/db/one/from%20t_singlevalue%20where%20name='safecheck版本号'";
	
//	// 数据服务地址
//	public static String  DB_URL = "http://125.76.225.223:3000/alashansafecheckDB/rs/db/";
//	//入户安检服务
//	public static String  IIS_URL = "http://125.76.225.223:3000/alashansafecheckDB/rs/iis/";
//	//认证服务地址
//	public static String AUTH_URL = "http://125.76.225.223:3000/alashansafecheckres/rs/user/";
//	public static String TUNNEL_URL = "http://125.76.225.223:3000/alashansafecheckDB/rs/tunnel/http%3A%7C%7C125.76.225.223%7Crs%7Cdb%7C";
//	public static  String downloadURL = "http://125.76.225.223:3000/alashansafecheckDB/alashansafecheck.apk";
//	public static  String checkVersionURL = "http://125.76.225.223:3000/alashansafecheckDB/rs/db/one/from%20t_singlevalue%20where%20name='safecheck版本号'";

	//数据服务地址
	public static String  DB_URL = "http://192.168.1.222:8082/rs/db/";
	//入户安检服务
	public static String  IIS_URL = "http://192.168.1.222:8082/rs/iis/";
	//认证服务地址
	public static String AUTH_URL = "http://192.168.1.222:82/rs/user/";
//	public static String TUNNEL_URL = "http://192.168.1.222:8080/rs/tunnel/http%3A%7C%7C192.168.1.222%3A8080%7Crs%7Cdb%7C";
	public static String TUNNEL_URL = "http://192.168.1.222:8082/rs/db/";
	public static  String downloadURL = "http://192.168.1.222:8082/SafeCheck.apk";
	public static  String checkVersionURL = "http://192.168.1.222:8082/rs/db/one/from%20t_singlevalue%20where%20name='safecheck版本号'";
	
	public static  String apkName ="download.apk";
	public static String appID="rcghsafecheck";
}
