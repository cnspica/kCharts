package exe1;

import java.io.Serializable;

public class Users implements Serializable {

	// �����û�������
	private String userName;
	// �����û�������
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

	// �ض���toString()����
	public String toString() {
//		if (this.isUserAble() == true)
//			return this.getUserName() + "\t\t" + "*********" + "\t\t" + "����";
//		else
//			return this.getUserName() + "\t\t" + "*********" + "\t\t" + "����";
		return "";
	}
}
