package exe1;

import java.util.*;
import java.io.IOException;
import java.net.*;

public class UdpServer {
	// ���������洢���շ������ߵ�ʱ�򣬷�������Ϣ

	public static List<Message> isNotSendMessage = new Vector<Message>();
	/*
	 * ���������洢�����û����������Ϣ (ͨ��DatagramPacket��װ�˿ͻ��˵�ip��ַ�Լ��˿�)
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
		System.out.println("������������..........");

		Manager manager = UserManager.getInstance();
		list.add(manager);
		Thread thread = new LoadDataThread(list);
		thread.start();

		Set<String> set = UserManager.getInstance().userMap.keySet();
		for (String s : set) {
			System.out.println(s);
		}
		do {
			System.out.println("���ڽ������Կͻ��˵���Ϣ");
			byte[] bytes = new byte[1024];
			DatagramPacket packet = new DatagramPacket(bytes, 0, bytes.length);
			try {
				socket.receive(packet);
			} catch (IOException e) {
				e.printStackTrace();
			}
			String s = "�յ����Կͻ��ˣ�" + packet.getAddress().getHostAddress()
					+ "  �˿�Ϊ��" + packet.getPort() + "����Ϣ";
			System.out.println(s);
			// �����߳�
			MessagePerformedThread thread1 = new MessagePerformedThread(bytes,
					packet);
			thread1.start();

		} while (true);
	}

}
