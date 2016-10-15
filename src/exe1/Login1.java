package exe1;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

import javax.swing.*;

public class Login1 extends JFrame implements ActionListener {
	// �������
	JLabel jlab1, jlab2, jlab3, jlab4;
	JTextField jtf;
	JPasswordField jpf;
	JButton jbu1, jbu2;
	private DatagramSocket socket = null;

	public static void main(String[] args) {
		Login1 login1 = new Login1();
	}

	public Login1() {

		jlab1 = new JLabel("�û�����", JLabel.CENTER);
		jlab2 = new JLabel("��   �룺", JLabel.CENTER);
		jlab3 = new JLabel("ע���û�", JLabel.CENTER);
		jlab3.setForeground(Color.blue);
		// jlab3.setFont(new Font())
		jlab4 = new JLabel("��������", JLabel.CENTER);
		jlab4.setForeground(Color.blue);
		jtf = new JTextField(10);
		jpf = new JPasswordField(10);
		jbu1 = new JButton("��½");
		jbu2 = new JButton("ȡ��");

		// �趨����.

		this.setLayout(new FlowLayout());
		this.add(jlab1);
		this.add(jtf);
		this.add(jlab3);
		jlab3.addMouseListener(new MouseListener() {

			@Override
			public void mouseReleased(MouseEvent e) {

			}

			@Override
			public void mousePressed(MouseEvent e) {

			}

			@Override
			public void mouseExited(MouseEvent e) {
				jlab3.setForeground(Color.blue);

			}

			@Override
			public void mouseEntered(MouseEvent e) {
				jlab3.setForeground(Color.cyan);

			}

			@Override
			public void mouseClicked(MouseEvent e) {
                 
				Register register = new Register();

			}
		});
		this.add(jlab2);
		this.add(jpf);
		this.add(jlab4);
		this.add(jbu1);
		jbu1.addActionListener(this);
		this.add(jbu2);
		jbu2.addActionListener(this);

		// ���ô�������

		this.setSize(260, 130);

		this.setResizable(false);
		int width = (Toolkit.getDefaultToolkit().getScreenSize().width - 280) / 2;
		int height = (Toolkit.getDefaultToolkit().getScreenSize().height - 200) / 2;
		this.setLocation(width, height);
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.setVisible(true);

	}

	@Override
	public void actionPerformed(ActionEvent e) {

		if (e.getSource() == this.jbu1) {
			if (this.validateUser()) {
				String receiveMeg = this.sendLoginMessage();
				if (receiveMeg.startsWith(MessageType.S_LOGIN_MSG)) {
					String[] str = receiveMeg.split(MessageType.SPERATOR);

					if ("true".equals(str[1])) {
						this.setVisible(false);
						MainJframe mainJframe=new MainJframe(this.jtf.getText(), socket); 
						
						
					} else {
						JOptionPane.showMessageDialog(null, str[1], "��½��ʾ",
								JOptionPane.ERROR_MESSAGE);
					}
				} else {
					JOptionPane.showMessageDialog(null, "������æ�����Ժ�����", "��½��ʾ",
							JOptionPane.ERROR_MESSAGE);
				}
			}
		} else if (e.getSource() == this.jbu2) {

			jtf.setText("");
			jpf.setText("");
		}
	}

	/**
	 * ��֤����ĵ�¼�û����������Ƿ�Ϸ�
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
		}

		if (info != null)
			JOptionPane.showMessageDialog(null, info, "��½��ʾ",
					JOptionPane.ERROR_MESSAGE);
		return flag;
	}

	/**
	 * ���ͺͽ��ܵ�½��Ϣ
	 * 
	 * @return ��½����ַ���
	 */
	public String sendLoginMessage() {

		// ���������½��Ϣ
		String sendMeg = MessageType.C_LOGIN_MSG + MessageType.SPERATOR
				+ this.jtf.getText() + MessageType.SPERATOR + jpf.getText();
		byte[] bytes = new byte[100];
		bytes = sendMeg.getBytes();
		
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
		// ���ܷ����������ĵ�½�����Ϣ
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
		} 
		return receiveMeg;

	}

}
