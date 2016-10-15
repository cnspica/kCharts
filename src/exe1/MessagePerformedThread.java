package exe1;

import java.io.IOException;
import java.net.*;

import java.util.Collection;
import java.util.Set;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class MessagePerformedThread extends Thread {

	byte[] bytes = null;
	DatagramPacket packet = null;
	// 将传来的消息转化为字符串的形式
	private String message = null;

	Lock lock = new ReentrantLock();
	Condition condition = lock.newCondition();

	public MessagePerformedThread(byte[] bytes, DatagramPacket packet) {

		this.bytes = bytes;
		this.packet = packet;
		message = new String(bytes, 0, packet.getLength());

	}

	public void run() {
		int i = 0;

		// 传来登陆的消息
		if (message.startsWith(MessageType.C_LOGIN_MSG)) {
			// 9001+用户名+密码
			if (this.userLogin()) {
				do {
					i++;
					this.sendFriList();
				} while (i < 3);

				try {
					Thread.sleep(1500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				// 发送该登陆用户的离线消息
				this.sendIsNotReceiveMeg();
			}

		}
		// 传来退出消息
		else if (message.startsWith(MessageType.C_EXIT_MSG)) {
			// 9002+用户名
			this.userExit();
		}
		// 传来普通消息
		else if (message.startsWith(MessageType.C_SEND_MSG)) {
			// 9003+发送者名+接受者名+消息内容
			this.sendUserMeg();
		}
		// 传来注册消息
		else if (message.startsWith(MessageType.C_REG_MSG)) {
			// 9004+用户名+密码
			if (this.userRegister()) {
				// 当有用户注册成功后，发送新的好友列表
				// 给所有的用户
				this.sendFriListToAll();
			}
		}

	}

	/**
	 * 发送好友列表给所有用户
	 */
	public void sendFriListToAll() {

		StringBuilder builder = new StringBuilder();
		builder.append(MessageType.S_Fri_LIST_MSG);
		Set<String> set = UserManager.getInstance().userMap.keySet();
		for (String s : set) {
			builder.append(MessageType.SPERATOR).append(s);
		}
		DatagramSocket sendSocket = null;
		DatagramPacket paper = null;
		byte[] bytes = null;
		Collection<DatagramPacket> con = UdpServer.userList.values();
		for (DatagramPacket packet : con) {
			bytes = new byte[1024];
			bytes = builder.toString().getBytes();
			try {
				sendSocket = new DatagramSocket();
				paper = new DatagramPacket(bytes, 0, bytes.length, packet
						.getSocketAddress());
				sendSocket.send(paper);
			} catch (SocketException e) {
				e.printStackTrace();
			} catch (IOException e1) {
				e1.printStackTrace();
			}

		}
		if (sendSocket != null) {
			sendSocket.close();
		}
	}

	/**
	 * 发送离线消息
	 */
	public void sendIsNotReceiveMeg() {

		String[] str = message.split(MessageType.SPERATOR);
		String userName = str[1];
		for (int i = 0; i < UdpServer.isNotSendMessage.size(); i++) {
			Message message = UdpServer.isNotSendMessage.get(i);
			if (userName.equals(message.getToWho())) {
				String sendMeg = MessageType.S_Fri_MSG + MessageType.SPERATOR
						+ message.getFrom() + MessageType.SPERATOR + userName
						+ MessageType.SPERATOR + message.getMessage();
				this.sendimidateMeg(sendMeg);
				UdpServer.isNotSendMessage.remove(i);
				i--;
			}
		}
	}

	/**
	 * 发送普通消息
	 */
	public void sendUserMeg() {
		String[] str = message.split(MessageType.SPERATOR);

		if (UdpServer.userList.containsKey(str[2])) {
			// 接收方在线
			String sendMeg = MessageType.S_Fri_MSG + MessageType.SPERATOR
					+ str[1] + MessageType.SPERATOR + str[2]
					+ MessageType.SPERATOR + str[3];
			DatagramSocket socket = null;
			DatagramPacket paper = null;
			byte[] bytes = new byte[1024];
			try {
				bytes = sendMeg.getBytes();
				socket = new DatagramSocket();
				paper = new DatagramPacket(bytes, 0, bytes.length,
						UdpServer.userList.get(str[2]).getSocketAddress());
				socket.send(paper);
			} catch (SocketException e) {
				e.printStackTrace();
			} catch (IOException e1) {
				e1.printStackTrace();
			} finally {
				if (socket != null) {
					socket.close();
				}
			}
		} else {
			// 接收方不在线
			UdpServer.isNotSendMessage.add(new Message(str[1], str[2], str[3]));

		}

	}

	/**
	 * 发送好友列表
	 */
	public void sendFriList() {
		StringBuilder builder = new StringBuilder();
		builder.append(MessageType.S_Fri_LIST_MSG);
		Set<String> set = UserManager.getInstance().userMap.keySet();
		for (String s : set) {
			builder.append(MessageType.SPERATOR).append(s);
		}
		this.sendimidateMeg(builder.toString());
	}

	/**
	 * 用户退出
	 */
	public void userExit() {
		String[] str = message.split(MessageType.SPERATOR);
		String userName = str[1];
		if (UdpServer.userList.containsKey(userName))
			UdpServer.userList.remove(str[1]);

	}

	/**
	 * 用户登录
	 */

	public boolean userLogin() {

		// byte[] buf = null;
		// DatagramSocket socket = null;
		// try {
		// socket = new DatagramSocket();
		// } catch (SocketException e1) {
		// e1.printStackTrace();
		// }
		// DatagramPacket paper = null;

		String[] str = message.split(MessageType.SPERATOR);
		Users users = null;
		String s = null;
		boolean flag = false;

		// //通过同步代码块保证数据读取的安全

		synchronized (UserManager.getInstance()) {
			users = UserManager.getInstance().getSaleUsersByName(str[1]);
		}

		if (users != null) {
			// 通过锁保证数据读取的安全
			lock.lock();
			if (UdpServer.userList.containsKey(str[1])) {
				// 用户已在线
				lock.unlock();
				s = MessageType.S_LOGIN_MSG + MessageType.SPERATOR + "用户已在线";

				// buf = s.getBytes();
				// paper = new DatagramPacket(buf, 0, buf.length,
				// packet.getSocketAddress());
				// socket.send(paper);
			} else {
				// 用户不在线
				if (users.getUserPassword().equals(str[2])) {
					// 将登陆的用户添加到在线用户集合中
					// UdpServer.userList.put()
					// 密码正确
					s = MessageType.S_LOGIN_MSG + MessageType.SPERATOR + "true";
					flag = true;
					UdpServer.userList.put(str[1], this.packet);

					// buf = s.getBytes();
					// paper = new DatagramPacket(buf, 0, buf.length,
					// packet.getSocketAddress());
					// socket.send(paper);
				} else {
					// 密码不正确
					s = MessageType.S_LOGIN_MSG + MessageType.SPERATOR
							+ "用户密码不正确";

					// buf = s.getBytes();
					// paper = new DatagramPacket(buf, 0, buf.length,
					// packet.getSocketAddress());
					// socket.send(paper);
				}
			}
		} else {
			// 用户不存在
			s = MessageType.S_LOGIN_MSG + MessageType.SPERATOR + "用户不存在";

			// buf = s.getBytes();
			// paper = new DatagramPacket(buf, 0, buf.length,
			// packet.getSocketAddress());
			// socket.send(paper);
		}

		this.sendimidateMeg(s);

		return flag;
	}

	/**
	 * 用户注册
	 */
	public boolean userRegister() {

		// byte[] buf = null;
		// DatagramSocket socket = null;
		// try {
		// socket = new DatagramSocket();
		// } catch (SocketException e) {
		//
		// e.printStackTrace();
		// }
		// DatagramPacket paper = null;
		String[] str = message.split(MessageType.SPERATOR);
		Users users = null;
		String s = null;
		boolean flag = false;

		synchronized (UserManager.getInstance()) {
			users = UserManager.getInstance().getSaleUsersByName(str[1]);
		}
		if (users == null) {
			// 用户不存在
			Users user = new Users(str[1], str[2]);
			// 注册成功后将用户添加到用户集合中
			if (UserManager.getInstance().addUser(user)) {
				UserManager.getInstance().saveData();
				s = MessageType.S_REG_MSG + MessageType.SPERATOR + "true";
				flag = true;
				// buf = s.getBytes();
				// paper = new DatagramPacket(buf, 0, buf.length,
				// packet.getSocketAddress());
				// socket.send(paper);
			} else {
				s = MessageType.S_REG_MSG + MessageType.SPERATOR + "注册失败";

			}
		} else {
			// 用户已存在
			s = MessageType.S_REG_MSG + MessageType.SPERATOR + "用户已被注册";

			// buf = s.getBytes();
			// paper = new DatagramPacket(buf, 0, buf.length,
			// packet.getSocketAddress());
			// socket.send(paper);
		}

		this.sendimidateMeg(s);
		return flag;
	}

	/**
	 * 发送服务器端的反馈消息
	 * 
	 * @param serverMeg
	 */
	public void sendimidateMeg(String serverMeg) {

		byte[] buf = null;
		DatagramSocket socket = null;
		try {
			socket = new DatagramSocket();
		} catch (SocketException e1) {
			e1.printStackTrace();
		}

		DatagramPacket paper = null;
		buf = serverMeg.getBytes();
		try {
			paper = new DatagramPacket(buf, 0, buf.length, this.packet
					.getSocketAddress());
			socket.send(paper);
		} catch (SocketException e) {

			e.printStackTrace();
		} catch (IOException e) {

			e.printStackTrace();
		} finally {
			if (socket != null) {
				socket.close();
			}
		}
	}
}
