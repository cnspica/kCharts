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
	 * 接受好友信息，并显示
	 * 
	 * @param receiveMeg
	 *            接受的消息
	 */
	public void receiveFriMeg(String receiveMeg) {
		AePlayWave wave=new AePlayWave("1141.wav");
		wave.start();
		String[] str = receiveMeg.split(MessageType.SPERATOR);
		// 这里是接受好友发来的信息，所以str[1]指的是好友
		String from = str[1];
		String to = str[2];
		String meg = str[3];
		if (!MainJframe.chatWindows.containsKey(from)) {
			// 不存在，创建一个新聊天窗口，并储存
			ChatJframe chat = new ChatJframe(to, from);
			MainJframe.chatWindows.put(from, chat);
		}
		// 存在该聊天窗口，取出，并将内容追加显示到其中
		ChatJframe chat = MainJframe.chatWindows.get(from);
		String showMeg = String.format("%s%s\n%s\n", from, "说:", meg);
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			
			e.printStackTrace();
		}
		chat.appendMessae(showMeg);
		
	}

	/**
	 * 添加或更新好友列表
	 * 
	 * @param receiveMeg
	 *            接受的消息
	 */
	public void updateFri(String receiveMeg) {
		String[] fri = receiveMeg.split(MessageType.SPERATOR);
		String[] tmp = new String[fri.length - 1];
		System.arraycopy(fri, 1, tmp, 0, fri.length - 1);
		this.mainJframe.updateFriList(tmp);

	}
}
