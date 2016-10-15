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
	// �������

	// ��������
	JLabel jl1;
	// �ϲ�����
	JPanel jp1;
	JButton jb1, jb2, jb3;
	// �в�����
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

	// ���캯��
	public Login() {
		// �������

		// ��������
		jl1 = new JLabel(new ImageIcon("images/tou.gif"));

		// �ϲ�����
		jp1 = new JPanel();
		jb1 = new JButton(new ImageIcon("images/denglu.gif"));
		jb1.addActionListener(this);
		jb2 = new JButton(new ImageIcon("images/quxiao.gif"));
		jb2.addActionListener(this);
		jb3 = new JButton(new ImageIcon("images/xiangdao.gif"));
		jb3.addActionListener(this);

		// �в�����
		jp2 = new JPanel();
		jp3 = new JPanel();
		jp4 = new JPanel();

		jl2 = new JLabel("QQ����", JLabel.CENTER);
		jl3 = new JLabel("QQ����", JLabel.CENTER);
		jl4 = new JLabel("��������", JLabel.CENTER);
		jl4.setFont(new Font("����", Font.PLAIN, 16));
		jl4.setForeground(Color.BLUE);
		jl5 = new JLabel("<html><a href='www.qq.com'>�������뱣��</a></html>");
		jl5.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

		jtf = new JTextField(15);
		jpf = new JPasswordField(15);

		jb4 = new JButton(new ImageIcon("images/clear.gif"));

		jcb1 = new JCheckBox("�����½");
		jcb2 = new JCheckBox("��ס����");

		jtb = new JTabbedPane();

		// ������
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

		jtb.add("QQ����", jp2);
		jtb.add("�ֻ�����", jp3);
		jtb.add("��������", jp4);

		this.add(jl1, BorderLayout.NORTH);
		this.add(jp1, BorderLayout.SOUTH);
		this.add(jtb, BorderLayout.CENTER);

		// ���ô�������
		this.setIconImage((new ImageIcon("images/qq.gif")).getImage());
		this.setTitle("��ѶQQ");
		this.setSize(350, 230);
		this.setResizable(false);
		int w = (Toolkit.getDefaultToolkit().getScreenSize().width - 350) / 2;
		int h = (Toolkit.getDefaultToolkit().getScreenSize().height - 230) / 2;
		this.setLocation(w, h);
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.setVisible(true);

	}

	/**
	 * ʵ�ְ�ť�����¼�
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
						JOptionPane.showMessageDialog(null, str[1], "��½��ʾ",
								JOptionPane.ERROR_MESSAGE);
					}
				} else {
					JOptionPane.showMessageDialog(null, "������æ�����Ժ�����", "��½��ʾ",
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
