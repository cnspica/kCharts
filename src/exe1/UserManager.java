package exe1;

import java.util.*;
import java.io.*;

public class UserManager implements Manager {

	private static UserManager manager = new UserManager();
	HashMap<String, Users> userMap = new HashMap<String, Users>();
	private Scanner input = new Scanner(System.in);

	// ����һ���û�����
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
	 * ���Ǽ����û����ϵĺ���
	 */
	public void loadData() {
		FileInputStream fis = null;
		ObjectInputStream ois = null;
		File file = new File("users.txt");
		if (file.exists()) {
			try {
				fis = new FileInputStream(file);
				ois = new ObjectInputStream(fis);
				// ע�������ǽ�HashMap������Ϊһ������ȡ����
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
	 * ���Ǳ����û����ϵĺ���
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
	 * ��������û��ĺ���
	 */
	public boolean addUser(Users users) {
		if (!this.hasUser(users.getUserName())) {
			userMap.put(users.getUserName(), users);
			return true;
		}
		return false;
	}

	/**
	 * ����ɾ���û��ĺ���
	 */
	public boolean deleteUser(String name) {
		if (this.hasUser(name)) {
			userMap.remove(name);
			return true;
		}
		return false;
	}

	/**
	 * ���Ǹ����û��ĺ���
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
	 * �жϼ������Ƿ��и��û�
	 * 
	 * @param name
	 *            ����Ա���ƣ�����ֵ��
	 * @return �Ƿ��и��û�
	 */
	public boolean hasUser(String name) {
		return userMap.containsKey(name);

	}

	/**
	 * ���Ǹ����û�����ȡ����Ӧ���û�����
	 * 
	 * @param name
	 *            �û�����
	 * @return ȡ�����û�����
	 */
	public Users getSaleUsersByName(String name) {
		return userMap.get(name);
	}

	/**
	 * ʵ��ģ����ѯ��Ч��
	 * 
	 * @param key
	 *            ����Ĺؼ���
	 * @return �����ؼ��ֵ��û��ļ���
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
	 * ������е��û� ��ʵ�������ֵ���Ȼ˳�������Ч��
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
// * ������֤��ĺ���
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
// * ����Ա��½
// */
// public boolean saleUsersLogin() {
// int count=0;
// do {
// if(count==3)
// {
// System.out.println("���Ѿ�ʹ����3�ε�½����");
// System.out.println("ϵͳ���˳�����Ա��½����");
// break;
// }
// System.out.println("����������Ա����");
// String name = input.next();
// System.out.println("����������Ա����");
// String password = input.next();
// do {
// System.out.print("��֤�룺");
// String checkCode = this.getCheckCode();
// System.out.println("��������֤��");
// String getCode = input.next();
// if (!checkCode.equalsIgnoreCase(getCode)) {
// System.out.println("��֤�벻��ȷ������������");
// } else {
// break;
// }
// } while (true);
//
// SaleUsers saleUsers = this.getSaleUsersByName(name);
// if (saleUsers == null) {
// count++;
// System.out.println("δ�ҵ���Ϊ" + name + "������Ա");
// System.out.println("�Ƿ������½(y/n)");
// if (!"y".equals(input.next())) {
// break;
// }
// } else {
// if (saleUsers.isUserAble()) {
// if (saleUsers.getUserPassword().equals(password)) {
// System.out.println(name + "�ѳɹ���½");
// saleUsers.setUserName(name);
// saleUsers.setUserPassword(password);
// new SaleUsersMainMenu(saleUsers);
// return true;
// } else {
// count++;
// System.out.println("����Ա���벻��ȷ");
// System.out.println("�Ƿ������½(y/n)");
// if (!"y".equals(input.next())) {
// break;
// }
// }
// } else {
// count++;
// System.out.println("������Ա�û��ѽ���");
// System.out.println("�Ƿ������½(y/n)");
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
