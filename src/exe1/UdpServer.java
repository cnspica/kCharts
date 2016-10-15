package exe1;

import java.util.*;
import java.io.IOException;
import java.net.*;

public class UdpServer {
	// 这是用来存储接收方不在线的时候，发来的消息

	public static List<Message> isNotSendMessage = new Vector<Message>();
	/*
	 * 这是用来存储在线用户及其相关信息 (通过DatagramPacket封装了客户端的ip地址以及端口)
	 */
	public static Hashtable<String, DatagramPacket> userList = new Hashtable<String, DatagramPacket>();

	public static List<Manager> list = new ArrayList<Manager>();

	public static void main(String[] args) {

		DatagramSocket socket = null;
		try {
			socket = new DatagramSocket(8000);
		} catch (SocketException e) {
			e.printStackTrace();
		}
		System.out.println("服务器已启动..........");

		Manager manager = UserManager.getInstance();
		list.add(manager);
		Thread thread = new LoadDataThread(list);
		thread.start();

		Set<String> set = UserManager.getInstance().userMap.keySet();
		for (String s : set) {
			System.out.println(s);
		}
		do {
			System.out.println("正在接受来自客户端的消息");
			byte[] bytes = new byte[1024];
			DatagramPacket packet = new DatagramPacket(bytes, 0, bytes.length);
			try {
				socket.receive(packet);
			} catch (IOException e) {
				e.printStackTrace();
			}
			String s = "收到来自客户端：" + packet.getAddress().getHostAddress()
					+ "  端口为：" + packet.getPort() + "的消息";
			System.out.println(s);
			// 启动线程
			MessagePerformedThread thread1 = new MessagePerformedThread(bytes,
					packet);
			thread1.start();

		} while (true);
	}

}
