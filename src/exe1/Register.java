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
	// 声明组件
	JLabel jlab1, jlab2, jlab3;
	JTextField jtf;
	JPasswordField jpf, jpf1;
	JButton jbu1, jbu2;

	public static void main(String[] args) {
		Register l = new Register();
	}

	public Register() {

		jlab1 = new JLabel("用户名：");
		jlab2 = new JLabel("密    码：");
		jlab3 = new JLabel("确认密码");
		jtf = new JTextField(15);
		jpf = new JPasswordField(15);
		jpf1 = new JPasswordField(15);

		jbu1 = new JButton("注册");
		jbu2 = new JButton("重置");

		// 设定布局.

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

		// 设置窗体属性

		this.setSize(280, 150);

		this.setResizable(false);
		int width = (Toolkit.getDefaultToolkit().getScreenSize().width - 280) / 2;
		int height = (Toolkit.getDefaultToolkit().getScreenSize().height - 200) / 2;
		this.setLocation(width, height);
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.setVisible(true);

	}

	/**
	 * 判断用户信息是否合法
	 */
	public boolean validateUser() {
		boolean flag = true;
		String info = null;
		// 用户名不能为空
		if (jtf.getText().length() == 0) {
			info = "用户名不能为空";
			flag = false;
			// 用户名的长度应在3-10位
		} else if (jtf.getText().length() < 3 || jtf.getText().length() > 10) {
			info = "用户名的长度应在3-10位";
			flag = false;
			// 用户名不能包含特殊字符#@%&*$
		} else if (jtf.getText().indexOf("#") >= 0
				|| jtf.getText().indexOf("$") >= 0
				|| jtf.getText().indexOf("@") >= 0
				|| jtf.getText().indexOf("%") >= 0
				|| jtf.getText().indexOf("&") >= 0
				|| jtf.getText().indexOf("*") >= 0) {
			info = "用户名不能包含特殊字符#@%&*";
			flag = false;
			// 密码不能为空
		} else if (jpf.getText().length() == 0) {
			info = "密码不能为空";
			flag = false;
			// 密码至少6位
		} else if (jpf.getText().length() < 6) {
			info = "密码至少6位";
			flag = false;
			// 两次输入的密码应该一致
		} else if (!jpf.getText().equals(jpf1.getText())) {
			info = "两次输入的密码应该一致";
			flag = false;
		}

		if (info != null)
			JOptionPane.showMessageDialog(null, info, "注册提示",
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
						JOptionPane.showMessageDialog(null, "注册成功", "注册提示",
								JOptionPane.INFORMATION_MESSAGE);
						this.setVisible(false);
					} else {
						JOptionPane.showMessageDialog(null, str[1], "注册提示",
								JOptionPane.ERROR_MESSAGE);
					}
				} else {
					JOptionPane.showMessageDialog(null, "服务器忙，请稍后再试", "注册提示",
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
	 * 发送和接受注册消息
	 * 
	 * @return 注册结果字符串
	 */
	public String sendRegisterMessage() {
		// 发送注册信息
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
		// 接受服务器发来的注册结果信息
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
