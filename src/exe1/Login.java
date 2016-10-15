package exe1;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

import javax.swing.*;

public class Login extends JFrame implements ActionListener {
	// 声明组件

	// 北部区域
	JLabel jl1;
	// 南部区域
	JPanel jp1;
	JButton jb1, jb2, jb3;
	// 中部区域
	JPanel jp2, jp3, jp4;
	JLabel jl2, jl3, jl4, jl5;
	JTextField jtf;
	JPasswordField jpf;
	JButton jb4;
	JCheckBox jcb1, jcb2;
	JTabbedPane jtb;
	private DatagramSocket socket = null;

	public static void main(String[] args) {

		Login login = new Login();
	}

	// 构造函数
	public Login() {
		// 创建组件

		// 北部区域
		jl1 = new JLabel(new ImageIcon("images/tou.gif"));

		// 南部区域
		jp1 = new JPanel();
		jb1 = new JButton(new ImageIcon("images/denglu.gif"));
		jb1.addActionListener(this);
		jb2 = new JButton(new ImageIcon("images/quxiao.gif"));
		jb2.addActionListener(this);
		jb3 = new JButton(new ImageIcon("images/xiangdao.gif"));
		jb3.addActionListener(this);

		// 中部区域
		jp2 = new JPanel();
		jp3 = new JPanel();
		jp4 = new JPanel();

		jl2 = new JLabel("QQ号码", JLabel.CENTER);
		jl3 = new JLabel("QQ密码", JLabel.CENTER);
		jl4 = new JLabel("忘记密码", JLabel.CENTER);
		jl4.setFont(new Font("宋体", Font.PLAIN, 16));
		jl4.setForeground(Color.BLUE);
		jl5 = new JLabel("<html><a href='www.qq.com'>申请密码保护</a></html>");
		jl5.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

		jtf = new JTextField(15);
		jpf = new JPasswordField(15);

		jb4 = new JButton(new ImageIcon("images/clear.gif"));

		jcb1 = new JCheckBox("隐身登陆");
		jcb2 = new JCheckBox("记住密码");

		jtb = new JTabbedPane();

		// 添加组件
		jp1.add(jb1);
		jp1.add(jb2);
		jp1.add(jb3);

		jp2.setLayout(new GridLayout(3, 3));
		jp2.add(jl2);
		jp2.add(jtf);
		jp2.add(jb4);
		jp2.add(jl3);
		jp2.add(jpf);
		jp2.add(jl4);
		jp2.add(jcb1);
		jp2.add(jcb2);
		jp2.add(jl5);

		jtb.add("QQ号码", jp2);
		jtb.add("手机号码", jp3);
		jtb.add("电子邮箱", jp4);

		this.add(jl1, BorderLayout.NORTH);
		this.add(jp1, BorderLayout.SOUTH);
		this.add(jtb, BorderLayout.CENTER);

		// 设置窗体属性
		this.setIconImage((new ImageIcon("images/qq.gif")).getImage());
		this.setTitle("腾讯QQ");
		this.setSize(350, 230);
		this.setResizable(false);
		int w = (Toolkit.getDefaultToolkit().getScreenSize().width - 350) / 2;
		int h = (Toolkit.getDefaultToolkit().getScreenSize().height - 230) / 2;
		this.setLocation(w, h);
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.setVisible(true);

	}

	/**
	 * 实现按钮单击事件
	 */
	@Override
	public void actionPerformed(ActionEvent e) {

		if (e.getSource() == this.jb1) {
			if (this.validateUser()) {
				String receiveMeg = this.sendLoginMessage();
				if (receiveMeg.startsWith(MessageType.S_LOGIN_MSG)) {
					String[] str = receiveMeg.split(MessageType.SPERATOR);

					if ("true".equals(str[1])) {
						this.setVisible(false);
						MainJframe mainJframe = new MainJframe(this.jtf
								.getText(), socket);

					} else {
						JOptionPane.showMessageDialog(null, str[1], "登陆提示",
								JOptionPane.ERROR_MESSAGE);
					}
				} else {
					JOptionPane.showMessageDialog(null, "服务器忙，请稍后再试", "登陆提示",
							JOptionPane.ERROR_MESSAGE);
				}
			}
		} else if (e.getSource() == this.jb2) {
            jtf.setText("");
			jpf.setText("");
		} else if (e.getSource() == this.jb3) {
			Register register = new Register();
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
