package exe1;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

import javax.swing.*;

public class ChatJframe extends JFrame implements ActionListener, KeyListener {

	// �������
	JTextArea jta1, jta2;
	JButton jbu1, jbu2;
	JScrollPane jsp1, jsp2;
	private String me;
	private String friend;
	String s = null;
	
	
	public ChatJframe(String fromUser, String toUser) {
		this.me = fromUser;
		this.friend = toUser;
		jta1 = new JTextArea(15, 30);
		// ʹ���ղ���ʾ������Ϣ���ı��򲻿ɱ�
		// �༭
		jta1.setEditable(false);

		jsp1 = new JScrollPane(jta1);
		
		//�����ı��򲻳���ˮƽ������
		jsp1
				.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		jta2 = new JTextArea(5, 30);
		jta2.addKeyListener(this);
		jsp2 = new JScrollPane(jta2);
	
		//�����ı��򲻳���ˮƽ������
		jsp2
				.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		jbu1 = new JButton("����");
//		jbu1.setFont(new Font("����", Font.BOLD, 16));
//		jbu1.setForeground(Color.DARK_GRAY);
		jbu1.addActionListener(this);
		jbu2 = new JButton("ȡ��");
		jbu2.addActionListener(this);

		// �趨����

		this.setLayout(new FlowLayout());
		this.add(jsp1);
		this.add(jsp2);
		this.add(jbu1);
		this.add(jbu2);

		// ���ô�������

		this.setSize(350, 450);
		this.setResizable(false);
		this.setTitle(String.format("��%s������...", this.friend));
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.setVisible(true);

		this.addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosing(WindowEvent e) {
				MainJframe.chatWindows.remove(ChatJframe.this.friend);

			}
		});

	}

	/**
	 * ����Ϣ׷�ӵ�jta1�ı����У�����ʾ����
	 * 
	 * @param receiveMeg
	 *            ׷�ӵ���Ϣ
	 */
	public void appendMessae(String receiveMeg) {
		this.jta1.append(receiveMeg);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == this.jbu1) {

			this.sendToFriMeg();

		} else if (e.getSource() == this.jbu2) {

			this.jta2.setText("");

		}

	}

	/**
	 * ������ϼ�ctrl+alt+enterʱ������Ϣ
	 */
	@Override
	public void keyPressed(KeyEvent e) {
		if (e.isControlDown() && e.isAltDown()
				&& e.getKeyCode() == KeyEvent.VK_ENTER) {

			this.sendToFriMeg();

		}
	}

	@Override
	public void keyReleased(KeyEvent e) {

	}

	@Override
	public void keyTyped(KeyEvent e) {

	}

	public void sendToFriMeg() {
		String text = this.jta2.getText();
		if (text.length() != 0) {
			String sendMeg = MessageType.C_SEND_MSG + MessageType.SPERATOR + me
					+ MessageType.SPERATOR + friend + MessageType.SPERATOR
					+ text;

			DatagramSocket socket = null;
			DatagramPacket paper = null;
			byte[] bytes = new byte[1024];
			try {
				bytes = sendMeg.getBytes();
				socket = new DatagramSocket();
				paper = new DatagramPacket(bytes, 0, bytes.length, Util
						.getInetSocketAddress());
				socket.send(paper);
			} catch (SocketException e1) {
				e1.printStackTrace();
			} catch (IOException e2) {
				e2.printStackTrace();
			} finally {
				// �ر�
				if (socket != null) {
					socket.close();
				}
			}
			String s = String.format("%s\n%s\n", "��˵��", this.jta2.getText());
			this.appendMessae(s);
			this.jta2.setText("");
		} else {
			JOptionPane.showMessageDialog(null, "���Ͳ���Ϊ��", "������ʾ",
					JOptionPane.ERROR_MESSAGE);

		}
	}
}
