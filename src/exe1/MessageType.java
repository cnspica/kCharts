package exe1;

public class MessageType {
	
	/**
	 * 消息分隔符
	 */
	 public static final String SPERATOR="￥@d:";
	 
	
	/**
	 * 客户端发送的登录请求
	 */
	public static final String C_LOGIN_MSG="9001";
	
	/**
	 * 客户端发送的注册请求
	 */
	public static final String C_REG_MSG="9003";
	
	/**
	 * 客户端发送的普通消息
	 */
	public static final String C_SEND_MSG="9002";
	
	/**
	 * 客户端发送的退出消息
	 */
	public static final String C_EXIT_MSG="9004";
	
    /**
     * 通知客户端是否登录成功的消息
     */
	public static final String S_LOGIN_MSG="1001";
  
	/**
	 * 通知客户端是否注册成功的消息
	 */
	public static final String S_REG_MSG="1002";
	
	/**
	 * 普通好友消息
	 */
	public static final String S_Fri_MSG="1003";
	
	/**
	 * 发送好友列表的消息
	 */
	public static final String S_Fri_LIST_MSG="1004";
}
