package exe1;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class ClientReceiveMessageThread extends Thread {

	private MainJframe mainJframe = null;
	private DatagramSocket socket = null;

	public ClientReceiveMessageThread(MainJframe mainJframe,
			DatagramSocket socket) {
		super();
		this.mainJframe = mainJframe;
		this.socket = socket;
	}

	public void run() {

		DatagramPacket packet = null;
		String receiveMeg = null;
		do {
			byte[] bytes = new byte[1024];
			packet = new DatagramPacket(bytes, 0, bytes.length);
			try {
				socket.receive(packet);
				receiveMeg = new String(bytes, 0, packet.getLength());
				if (receiveMeg.startsWith(MessageType.S_Fri_MSG)) {
					this.receiveFriMeg(receiveMeg);
				} else if (receiveMeg.startsWith(MessageType.S_Fri_LIST_MSG)) {
					this.updateFri(receiveMeg);
				}
			} catch (IOException e) {

				e.printStackTrace();
			}
		} while (true);
	}

	/**
	 * ���ܺ�����Ϣ������ʾ
	 * 
	 * @param receiveMeg
	 *            ���ܵ���Ϣ
	 */
	public void receiveFriMeg(String receiveMeg) {
		AePlayWave wave=new AePlayWave("1141.wav");
		wave.start();
		String[] str = receiveMeg.split(MessageType.SPERATOR);
		// �����ǽ��ܺ��ѷ�������Ϣ������str[1]ָ���Ǻ���
		String from = str[1];
		String to = str[2];
		String meg = str[3];
		if (!MainJframe.chatWindows.containsKey(from)) {
			// �����ڣ�����һ�������촰�ڣ�������
			ChatJframe chat = new ChatJframe(to, from);
			MainJframe.chatWindows.put(from, chat);
		}
		// ���ڸ����촰�ڣ�ȡ������������׷����ʾ������
		ChatJframe chat = MainJframe.chatWindows.get(from);
		String showMeg = String.format("%s%s\n%s\n", from, "˵:", meg);
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			
			e.printStackTrace();
		}
		chat.appendMessae(showMeg);
		
	}

	/**
	 * ��ӻ���º����б�
	 * 
	 * @param receiveMeg
	 *            ���ܵ���Ϣ
	 */
	public void updateFri(String receiveMeg) {
		String[] fri = receiveMeg.split(MessageType.SPERATOR);
		String[] tmp = new String[fri.length - 1];
		System.arraycopy(fri, 1, tmp, 0, fri.length - 1);
		this.mainJframe.updateFriList(tmp);

	}
}
