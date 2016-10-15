package exe1;

import java.util.*;
import java.io.*;

public class UserManager implements Manager {

	private static UserManager manager = new UserManager();
	HashMap<String, Users> userMap = new HashMap<String, Users>();
	private Scanner input = new Scanner(System.in);

	// 声明一个用户对象
	Users users = new Users();

	private UserManager() {

	}

	public static UserManager getInstance() {
		if (manager == null) {
			manager = new UserManager();
		}
		return manager;
	}

	/**
	 * 这是加载用户集合的函数
	 */
	public void loadData() {
		FileInputStream fis = null;
		ObjectInputStream ois = null;
		File file = new File("users.txt");
		if (file.exists()) {
			try {
				fis = new FileInputStream(file);
				ois = new ObjectInputStream(fis);
				// 注意这里是将HashMap集合作为一个对象取出来
				try {
					userMap = (HashMap<String, Users>) ois.readObject();
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			} catch (FileNotFoundException e) {

				e.printStackTrace();
			} catch (IOException e) {

				e.printStackTrace();
			} finally {
				try {
					if (ois != null)
						ois.close();
					if (fis != null)
						fis.close();
				} catch (IOException e) {

					e.printStackTrace();
				}

			}
		}
	}

	/**
	 * 这是保存用户集合的函数
	 */
	public void saveData() {
		FileOutputStream fos = null;
		ObjectOutputStream oos = null;
		try {
			fos = new FileOutputStream("users.txt");
			oos = new ObjectOutputStream(fos);
			oos.writeObject(userMap);

		} catch (FileNotFoundException e) {

			e.printStackTrace();
		} catch (IOException e) {

			e.printStackTrace();
		} finally {
			try {
				if (oos != null)
					oos.close();
				if (fos != null)
					fos.close();
			} catch (IOException e) {

				e.printStackTrace();
			}
		}
	}

	/**
	 * 这是添加用户的函数
	 */
	public boolean addUser(Users users) {
		if (!this.hasUser(users.getUserName())) {
			userMap.put(users.getUserName(), users);
			return true;
		}
		return false;
	}

	/**
	 * 这是删除用户的函数
	 */
	public boolean deleteUser(String name) {
		if (this.hasUser(name)) {
			userMap.remove(name);
			return true;
		}
		return false;
	}

	/**
	 * 这是更改用户的函数
	 */
	public void updateUser(String name, Users users) {
		if (name.equals(users.getUserName())) {
			this.userMap.put(name, users);
		} else {
			this.deleteUser(name);
			this.addUser(users);
		}
	}

	/**
	 * 判断集合中是否有该用户
	 * 
	 * @param name
	 *            销售员名称（即键值）
	 * @return 是否有该用户
	 */
	public boolean hasUser(String name) {
		return userMap.containsKey(name);

	}

	/**
	 * 这是根据用户名称取出相应的用户对象
	 * 
	 * @param name
	 *            用户名称
	 * @return 取出的用户对象
	 */
	public Users getSaleUsersByName(String name) {
		return userMap.get(name);
	}

	/**
	 * 实现模糊查询的效果
	 * 
	 * @param key
	 *            输入的关键字
	 * @return 包含关键字的用户的集合
	 */
	public List<Users> getSaleUsersByKey(String key) {
		List<Users> list = new ArrayList<Users>();
		Collection<Users> collection = userMap.values();
		for (Users saleUsers : collection) {
			if (saleUsers.getUserName().indexOf(key) >= 0) {
				list.add(saleUsers);
			}
		}
		return list;
	}

	/**
	 * 输出所有的用户 并实现以名字的自然顺序排序的效果
	 */

	public List<Users> showAllSaleUsers() {
		List<Users> list = new ArrayList<Users>();
		Collection<Users> collection = userMap.values();
		// list.addAll(collection);
		// Collections.sort(list);
		return list;
	}
}

// /**
// * 产生验证码的函数
// */
//
// public String getCheckCode() {
//
// StringBuilder builder = new StringBuilder();
// for (int i = 0; i < 5; i++) {
// int n = (int) (Math.random() * 10);
// if (n > 6) {
// int m = (int) (Math.random() * 10);
// builder.append(m);
//
// } else {
// char c = (char) (int) (Math.random() * 27 + 65);
// builder.append(c);
// }
// }
// System.out.println(builder.toString());
// return builder.toString();
//
// }
//
// /**
// * 销售员登陆
// */
// public boolean saleUsersLogin() {
// int count=0;
// do {
// if(count==3)
// {
// System.out.println("您已经使用了3次登陆机会");
// System.out.println("系统将退出销售员登陆界面");
// break;
// }
// System.out.println("请输入销售员名称");
// String name = input.next();
// System.out.println("请输入销售员密码");
// String password = input.next();
// do {
// System.out.print("验证码：");
// String checkCode = this.getCheckCode();
// System.out.println("请输入验证码");
// String getCode = input.next();
// if (!checkCode.equalsIgnoreCase(getCode)) {
// System.out.println("验证码不正确，请重新输入");
// } else {
// break;
// }
// } while (true);
//
// SaleUsers saleUsers = this.getSaleUsersByName(name);
// if (saleUsers == null) {
// count++;
// System.out.println("未找到名为" + name + "的销售员");
// System.out.println("是否继续登陆(y/n)");
// if (!"y".equals(input.next())) {
// break;
// }
// } else {
// if (saleUsers.isUserAble()) {
// if (saleUsers.getUserPassword().equals(password)) {
// System.out.println(name + "已成功登陆");
// saleUsers.setUserName(name);
// saleUsers.setUserPassword(password);
// new SaleUsersMainMenu(saleUsers);
// return true;
// } else {
// count++;
// System.out.println("销售员密码不正确");
// System.out.println("是否继续登陆(y/n)");
// if (!"y".equals(input.next())) {
// break;
// }
// }
// } else {
// count++;
// System.out.println("该销售员用户已禁用");
// System.out.println("是否继续登陆(y/n)");
// if (!"y".equals(input.next())) {
// break;
// }
// }
// }
// } while (true);
//
// return false;
// }
// }
