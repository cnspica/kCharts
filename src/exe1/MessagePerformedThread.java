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
	// ����������Ϣת��Ϊ�ַ�������ʽ
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

		// ������½����Ϣ
		if (message.startsWith(MessageType.C_LOGIN_MSG)) {
			// 9001+�û���+����
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
				// ���͸õ�½�û���������Ϣ
				this.sendIsNotReceiveMeg();
			}

		}
		// �����˳���Ϣ
		else if (message.startsWith(MessageType.C_EXIT_MSG)) {
			// 9002+�û���
			this.userExit();
		}
		// ������ͨ��Ϣ
		else if (message.startsWith(MessageType.C_SEND_MSG)) {
			// 9003+��������+��������+��Ϣ����
			this.sendUserMeg();
		}
		// ����ע����Ϣ
		else if (message.startsWith(MessageType.C_REG_MSG)) {
			// 9004+�û���+����
			if (this.userRegister()) {
				// �����û�ע��ɹ��󣬷����µĺ����б�
				// �����е��û�
				this.sendFriListToAll();
			}
		}

	}

	/**
	 * ���ͺ����б�������û�
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
	 * ����������Ϣ
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
	 * ������ͨ��Ϣ
	 */
	public void sendUserMeg() {
		String[] str = message.split(MessageType.SPERATOR);

		if (UdpServer.userList.containsKey(str[2])) {
			// ���շ�����
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
			// ���շ�������
			UdpServer.isNotSendMessage.add(new Message(str[1], str[2], str[3]));

		}

	}

	/**
	 * ���ͺ����б�
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
	 * �û��˳�
	 */
	public void userExit() {
		String[] str = message.split(MessageType.SPERATOR);
		String userName = str[1];
		if (UdpServer.userList.containsKey(userName))
			UdpServer.userList.remove(str[1]);

	}

	/**
	 * �û���¼
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

		// //ͨ��ͬ������鱣֤���ݶ�ȡ�İ�ȫ

		synchronized (UserManager.getInstance()) {
			users = UserManager.getInstance().getSaleUsersByName(str[1]);
		}

		if (users != null) {
			// ͨ������֤���ݶ�ȡ�İ�ȫ
			lock.lock();
			if (UdpServer.userList.containsKey(str[1])) {
				// �û�������
				lock.unlock();
				s = MessageType.S_LOGIN_MSG + MessageType.SPERATOR + "�û�������";

				// buf = s.getBytes();
				// paper = new DatagramPacket(buf, 0, buf.length,
				// packet.getSocketAddress());
				// socket.send(paper);
			} else {
				// �û�������
				if (users.getUserPassword().equals(str[2])) {
					// ����½���û���ӵ������û�������
					// UdpServer.userList.put()
					// ������ȷ
					s = MessageType.S_LOGIN_MSG + MessageType.SPERATOR + "true";
					flag = true;
					UdpServer.userList.put(str[1], this.packet);

					// buf = s.getBytes();
					// paper = new DatagramPacket(buf, 0, buf.length,
					// packet.getSocketAddress());
					// socket.send(paper);
				} else {
					// ���벻��ȷ
					s = MessageType.S_LOGIN_MSG + MessageType.SPERATOR
							+ "�û����벻��ȷ";

					// buf = s.getBytes();
					// paper = new DatagramPacket(buf, 0, buf.length,
					// packet.getSocketAddress());
					// socket.send(paper);
				}
			}
		} else {
			// �û�������
			s = MessageType.S_LOGIN_MSG + MessageType.SPERATOR + "�û�������";

			// buf = s.getBytes();
			// paper = new DatagramPacket(buf, 0, buf.length,
			// packet.getSocketAddress());
			// socket.send(paper);
		}

		this.sendimidateMeg(s);

		return flag;
	}

	/**
	 * �û�ע��
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
			// �û�������
			Users user = new Users(str[1], str[2]);
			// ע��ɹ����û���ӵ��û�������
			if (UserManager.getInstance().addUser(user)) {
				UserManager.getInstance().saveData();
				s = MessageType.S_REG_MSG + MessageType.SPERATOR + "true";
				flag = true;
				// buf = s.getBytes();
				// paper = new DatagramPacket(buf, 0, buf.length,
				// packet.getSocketAddress());
				// socket.send(paper);
			} else {
				s = MessageType.S_REG_MSG + MessageType.SPERATOR + "ע��ʧ��";

			}
		} else {
			// �û��Ѵ���
			s = MessageType.S_REG_MSG + MessageType.SPERATOR + "�û��ѱ�ע��";

			// buf = s.getBytes();
			// paper = new DatagramPacket(buf, 0, buf.length,
			// packet.getSocketAddress());
			// socket.send(paper);
		}

		this.sendimidateMeg(s);
		return flag;
	}

	/**
	 * ���ͷ������˵ķ�����Ϣ
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
