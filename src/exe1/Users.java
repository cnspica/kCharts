package exe1;

import java.io.Serializable;

public class Users implements Serializable {

	// 定义用户的姓名
	private String userName;
	// 定义用户的密码
	private String userPassword;

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserPassword() {
		return userPassword;
	}

	public void setUserPassword(String userPassword) {
		this.userPassword = userPassword;
	}

	public Users() {

	}

	public Users(String userName, String userPassword) {
		this.userName = userName;
		this.userPassword = userPassword;

	}

//	public Users clone() {
//		return new SaleUsers(this.getUserName(), this.getUserPassword(),
//				this.isUserAble);
//	}

	// 重定义toString()方法
	public String toString() {
//		if (this.isUserAble() == true)
//			return this.getUserName() + "\t\t" + "*********" + "\t\t" + "可用";
//		else
//			return this.getUserName() + "\t\t" + "*********" + "\t\t" + "禁用";
		return "";
	}
}
