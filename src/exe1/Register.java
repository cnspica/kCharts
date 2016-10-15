package exe1;

import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

import javax.swing.*;

public class Register extends JFrame implements ActionListener {
	// �������
	JLabel jlab1, jlab2, jlab3;
	JTextField jtf;
	JPasswordField jpf, jpf1;
	JButton jbu1, jbu2;

	public static void main(String[] args) {
		Register l = new Register();
	}

	public Register() {

		jlab1 = new JLabel("�û�����");
		jlab2 = new JLabel("��    �룺");
		jlab3 = new JLabel("ȷ������");
		jtf = new JTextField(15);
		jpf = new JPasswordField(15);
		jpf1 = new JPasswordField(15);

		jbu1 = new JButton("ע��");
		jbu2 = new JButton("����");

		// �趨����.

		this.setLayout(new FlowLayout());
		this.add(jlab1);
		this.add(jtf);
		this.add(jlab2);
		this.add(jpf);
		this.add(jlab3);
		this.add(jpf1);
		this.add(jbu1);
		jbu1.addActionListener(this);
		this.add(jbu2);
		jbu2.addActionListener(this);

		// ���ô�������

		this.setSize(280, 150);

		this.setResizable(false);
		int width = (Toolkit.getDefaultToolkit().getScreenSize().width - 280) / 2;
		int height = (Toolkit.getDefaultToolkit().getScreenSize().height - 200) / 2;
		this.setLocation(width, height);
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.setVisible(true);

	}

	/**
	 * �ж��û���Ϣ�Ƿ�Ϸ�
	 */
	public boolean validateUser() {
		boolean flag = true;
		String info = null;
		// �û�������Ϊ��
		if (jtf.getText().length() == 0) {
			info = "�û�������Ϊ��";
			flag = false;
			// �û����ĳ���Ӧ��3-10λ
		} else if (jtf.getText().length() < 3 || jtf.getText().length() > 10) {
			info = "�û����ĳ���Ӧ��3-10λ";
			flag = false;
			// �û������ܰ��������ַ�#@%&*$
		} else if (jtf.getText().indexOf("#") >= 0
				|| jtf.getText().indexOf("$") >= 0
				|| jtf.getText().indexOf("@") >= 0
				|| jtf.getText().indexOf("%") >= 0
				|| jtf.getText().indexOf("&") >= 0
				|| jtf.getText().indexOf("*") >= 0) {
			info = "�û������ܰ��������ַ�#@%&*";
			flag = false;
			// ���벻��Ϊ��
		} else if (jpf.getText().length() == 0) {
			info = "���벻��Ϊ��";
			flag = false;
			// ��������6λ
		} else if (jpf.getText().length() < 6) {
			info = "��������6λ";
			flag = false;
			// �������������Ӧ��һ��
		} else if (!jpf.getText().equals(jpf1.getText())) {
			info = "�������������Ӧ��һ��";
			flag = false;
		}

		if (info != null)
			JOptionPane.showMessageDialog(null, info, "ע����ʾ",
					JOptionPane.ERROR_MESSAGE);
		return flag;
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		if (e.getSource() == this.jbu1) {
			if (this.validateUser()) {
				String receiveMeg = this.sendRegisterMessage();
				if (receiveMeg.startsWith(MessageType.S_REG_MSG)) {
					String[] str = receiveMeg.split(MessageType.SPERATOR);
					if ("true".equals(str[1])) {
						JOptionPane.showMessageDialog(null, "ע��ɹ�", "ע����ʾ",
								JOptionPane.INFORMATION_MESSAGE);
						this.setVisible(false);
					} else {
						JOptionPane.showMessageDialog(null, str[1], "ע����ʾ",
								JOptionPane.ERROR_MESSAGE);
					}
				} else {
					JOptionPane.showMessageDialog(null, "������æ�����Ժ�����", "ע����ʾ",
							JOptionPane.ERROR_MESSAGE);
				}
			}
		} else if (e.getSource() == this.jbu2) {
			jtf.setText("");
			jpf.setText("");
			jpf1.setText("");
		}
	}

	/**
	 * ���ͺͽ���ע����Ϣ
	 * 
	 * @return ע�����ַ���
	 */
	public String sendRegisterMessage() {
		// ����ע����Ϣ
		String sendMeg = MessageType.C_REG_MSG + MessageType.SPERATOR
				+ this.jtf.getText() + MessageType.SPERATOR + jpf.getText();
		byte[] bytes = new byte[100];
		bytes = sendMeg.getBytes();
		DatagramSocket socket = null;
		DatagramPacket packet = null;
		try {
			socket = new DatagramSocket();
			packet = new DatagramPacket(bytes, 0, bytes.length,
					Util.getInetSocketAddress());
			socket.send(packet);

		} catch (SocketException e) {

			e.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		// ���ܷ�����������ע������Ϣ
		byte[] buf = new byte[100];
		DatagramPacket paper = null;

		String receiveMeg = null;
		try {

			paper = new DatagramPacket(buf, 0, buf.length);
			socket.receive(paper);
			receiveMeg = new String(buf, 0, paper.getLength());

		} catch (SocketException e) {

			e.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		} finally {
			if (socket != null) {
				socket.close();
			}
		}
		return receiveMeg;

	}
}
