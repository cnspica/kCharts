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
	// 声明组件
	JLabel jlab1, jlab2, jlab3, jlab4;
	JTextField jtf;
	JPasswordField jpf;
	JButton jbu1, jbu2;
	private DatagramSocket socket = null;

	public static void main(String[] args) {
		Login1 login1 = new Login1();
	}

	public Login1() {

		jlab1 = new JLabel("用户名：", JLabel.CENTER);
		jlab2 = new JLabel("密   码：", JLabel.CENTER);
		jlab3 = new JLabel("注册用户", JLabel.CENTER);
		jlab3.setForeground(Color.blue);
		// jlab3.setFont(new Font())
		jlab4 = new JLabel("忘记密码", JLabel.CENTER);
		jlab4.setForeground(Color.blue);
		jtf = new JTextField(10);
		jpf = new JPasswordField(10);
		jbu1 = new JButton("登陆");
		jbu2 = new JButton("取消");

		// 设定布局.

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

		// 设置窗体属性

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
						JOptionPane.showMessageDialog(null, str[1], "登陆提示",
								JOptionPane.ERROR_MESSAGE);
					}
				} else {
					JOptionPane.showMessageDialog(null, "服务器忙，请稍后再试", "登陆提示",
							JOptionPane.ERROR_MESSAGE);
				}
			}
		} else if (e.getSource() == this.jbu2) {

			jtf.setText("");
			jpf.setText("");
		}
	}

	/**
	 * 验证输入的登录用户名和密码是否合法
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
		}

		if (info != null)
			JOptionPane.showMessageDialog(null, info, "登陆提示",
					JOptionPane.ERROR_MESSAGE);
		return flag;
	}

	/**
	 * 发送和接受登陆消息
	 * 
	 * @return 登陆结果字符串
	 */
	public String sendLoginMessage() {

		// 发送请求登陆信息
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
		// 接受服务器发来的登陆结果信息
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
