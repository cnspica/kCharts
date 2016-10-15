package exe1;

import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.Hashtable;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class MainJframe extends JFrame {
	private String myName = null;
	private DatagramSocket socket = null;

	private JPanel jp1;
	private JButton jb1;
	private Hashtable<String, JButton> friends = new Hashtable<String, JButton>();
	public static Hashtable<String, ChatJframe> chatWindows = new Hashtable<String, ChatJframe>();


	public MainJframe(String myName, DatagramSocket socket) {
		this.myName = myName;
		this.socket = socket;

		jp1 = new JPanel();

		// String[] fri = this.receiveFriList();
		// jp1.setLayout(new GridLayout(fri.length, 1));
		// int i = 1;
		// for (String s : fri) {
		// i = (int) (Math.random() * 20) + 1;
		// if (i <= 9) {
		// jb1 = new JButton(s, new ImageIcon("images/p_0" + i + ".png"));
		// } else {
		// jb1 = new JButton(s, new ImageIcon("images/p_" + i + ".png"));
		//
		// // 添加背景图片的另一种方式
		// // jb1 = new JButton(s, new ImageIcon(this.getClass()
		// // .getClassLoader().getResource("images/p_01.png")));
		// }
		// jp1.add(jb1);
		// }

		Thread thread = new ClientReceiveMessageThread(this, this.socket);
		thread.start();

		this.add(jp1);
		this.setSize(200, 600);
		this.setTitle("Q聊" + myName);
		this.setVisible(true);

		this.addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosing(WindowEvent e) {
				DatagramSocket sendSocket = null;
				DatagramPacket packet = null;
				byte[] bytes = new byte[1024];
				bytes = String.format("%s%s%s", MessageType.C_EXIT_MSG,
						MessageType.SPERATOR, MainJframe.this.myName)
						.getBytes();

				try {
					sendSocket = new DatagramSocket();
					packet = new DatagramPacket(bytes, 0, bytes.length, Util
							.getInetSocketAddress());
					sendSocket.send(packet);

				} catch (SocketException e1) {
					e1.printStackTrace();
				} catch (IOException e2) {
					e2.printStackTrace();
				} finally {
					if (sendSocket != null) {
						sendSocket.close();
					}
				}

				// MainJframe.this.socket.close();
				System.exit(0);
			}
		});

	}

	// public String[] receiveFriList() {
	// String[] tmp = null;
	// DatagramPacket packet = null;
	// byte[] bytes = new byte[1024];
	// packet = new DatagramPacket(bytes, 0, bytes.length);
	// try {
	// do {
	// socket.receive(packet);
	// String receiveFriMeg = new String(bytes, 0, packet.getLength());
	// if (receiveFriMeg.startsWith(MessageType.S_Fri_LIST_MSG)) {
	// String[] fri = receiveFriMeg.split(MessageType.SPERATOR);
	// tmp = new String[fri.length - 1];
	// System.arraycopy(fri, 1, tmp, 0, fri.length - 1);
	// break;
	//
	// }
	// } while (true);
	// } catch (IOException e) {
	//
	// e.printStackTrace();
	// }
	// return tmp;
	// }

	public void updateFriList(String[] fri) {
		jp1.setLayout(new GridLayout(fri.length, 1));
		int i = 1;
		for (String s : fri) {
			final String toUser = s;
			if (!this.friends.containsKey(s)) {
				i = (int) (Math.random() * 20) + 1;
				if (i <= 9) {
					jb1 = new JButton(s, new ImageIcon("images/p_0" + i
							+ ".png"));
				} else {
					jb1 = new JButton(s,
							new ImageIcon("images/p_" + i + ".png"));

					// 添加背景图片的另一种方式
					// jb1 = new JButton(s, new ImageIcon(this.getClass()
					// .getClassLoader().getResource("images/p_01.png")));
				}
				jb1.addMouseListener(new MouseAdapter() {

					@Override
					public void mouseClicked(MouseEvent e) {
						// 必须双击
						if (e.getClickCount() == 2) {
							// 当双击自己的按钮时，不能弹出聊天窗口
							if (!myName.equals(toUser)) {
								if (!chatWindows.containsKey(toUser)) {
									// 没有改窗体
									ChatJframe chat = new ChatJframe(
											MainJframe.this.myName, toUser);
									chatWindows.put(toUser, chat);
								} else {
									// 有该窗体，则取出
									ChatJframe chat = chatWindows.get(toUser);
									// 将最小化的窗口重新显示到桌面
									chat.setState(JFrame.NORMAL);
									chat.show();
								}
							}
						}

					}
				});
				jp1.add(jb1);
				this.friends.put(s, jb1);
			}
		}
		this.setVisible(true);
		this.repaint();
	}
}
