import java.awt.BorderLayout;
import java.awt.List;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;


@SuppressWarnings("serial")
public class Server extends JFrame{
	
	private Connection con; 
	private String url = "jdbc:mysql://127.0.0.1:3306/scheduler?useUnicode=yes&characterEncoding=utf8";
	private String user = "root",pass = "secufact";
	private static final int SERVER_PORT = 12012;
	private ServerSocket server;
	private HashMap<String,ObjectOutputStream> hm;
	private List userList;
	private JTextArea display;
	private JScrollPane scroll;
	/*������*/
	public Server()
	{
		super("Schedule Server");
		
		try{
			server = new ServerSocket(SERVER_PORT);
		}catch(Exception e){}
		Frameinit();
		init();
		setVisible(true);
		serverStart();
		
	}
	/*���� ������ �ʱ�ȭ*/
	public void Frameinit()
	{
		setLayout(new BorderLayout());
		setSize(500,400);
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	/*���� ��ü�� �ʱ�ȭ*/
	public void init()
	{
		
		hm = new HashMap<String,ObjectOutputStream>();
		userList = new List();
		userList.add("test");
		display = new JTextArea();
		display.setEditable(false);
		scroll = new JScrollPane(display);
		scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		
		add("Center",scroll);
		add("East",userList);
		
		try {
			con = DriverManager.getConnection(url,user,pass);
		} catch (SQLException e) {
			display.append("DB connect error " + e + "\n");
		}
	}
	/*���� ����*/
	public void serverStart()
	{
		try{
			while(true)
			{
				display.append("���� �����..\n");
				Socket sock = server.accept();
				//���� �������� ���� br �� pw ��ü�� ��´�.
					
				ClientRequest cr = new ClientRequest(sock);
				cr.start();
			}
		}catch(Exception e){System.out.println("Server run error");}
	}
	/*main method*/
	public static void main(String[] args) {
		new Server();
	}
	/*����� ���Ǹ� ó���ϴ� Ŭ����*/
	class ClientRequest extends Thread
	{
		private String Id;
		private DBConnector dc;
		private ObjectOutputStream oos;
		private ObjectInputStream ois;
		
		ClientRequest(Socket sock)
		{
			dc = new DBConnector();
			try {
				oos = new ObjectOutputStream(sock.getOutputStream());
				ois = new ObjectInputStream(sock.getInputStream());
				if(dc.DBConnection(con)) display.append("DB ���� ����!\n");
			} catch (IOException e) {
				System.out.println("[Server->ClientRequest] Object create error " + e);
			}
		}
		/*Run*/
		public void run()
		{
			try{
				String msg = null;
				
				while((msg = ois.readUTF()) !=null)
				{
					if(msg.indexOf("[Login]") != -1) Login(msg);
					else if(msg.indexOf("[GetMessages]") != -1) GetMessages();
					else if(msg.indexOf("[Notification]") != -1) NotificationMessage();
					else if(msg.indexOf("[AddSchedule]") != -1) AddSchedule();
					else if(msg.indexOf("[GetSchedule]") != -1) GetSchedule(msg);
					else if(msg.indexOf("[GetSchedules]") != -1) GetSchedules();
					else if(msg.indexOf("[ModifySchedule]") != -1) ModifySchedule();
					else if(msg.indexOf("[DeleteSchedule]") != -1) DeleteSchedule(msg);
					else if(msg.indexOf("[DeleteAllSchedules]") != -1) DeleteAllSchedules();
					else if(msg.indexOf("[CheckId]") != -1) CheckId(msg);
					else if(msg.indexOf("[AddUser]") != -1) AddUser(msg);
					else if(msg.indexOf("[EXIT]") != -1) {EXIT(); break;}

					display.setSelectionStart(display.getText().length());
				}
				
			}catch(Exception ex){
				System.out.println("[Server->ClientRuequst] run error " + ex);
			}
			finally
			{
				try{
					ois.close();
					oos.close();
				}catch(Exception e){}
			}
		}//end of run
		/*Loing �޼���*/
		public void Login(String msg)
		{
			try{
			// '/' �� ����� ����� ���̵�� �н����带 �и�
			String[] temp = msg.substring("[Login]".length()).split("/");
			this.Id = temp[0];
			display.append("����� �α��� -> ID : "+this.Id+ " PW : " + temp[1]+"\n");
			
			//�α��� ����-> SUCCESS, ����-> FAIL
			//��й�ȣ ����ġ-> DISMATCH , ���� ����-> JOIN
			String result = dc.Login(this.Id, temp[1]);
			
			//��� �뺸
			oos.writeUTF(result);
			oos.flush();
			
			if("SUCCESS".equals(result))///����� �����϶���,
			{	
				//�ؽøʿ� ����
				synchronized(hm)
				{
					hm.put(this.Id, oos);
				}
			}
			}catch(Exception ex){
				System.out.println("[Server] Login error " + ex);
			}
		}
		/*Schedule �߰� �޼���*/
		public void AddSchedule()
		{
			try{
			//�ش� �÷��׸� �о�鿴�ٸ�, �������� ������ ��ü�� �޴´�.
			sPacket packet = (sPacket)ois.readObject();
			
			//DB�� �������� ���ϰ�, �� ����� Ŭ���̾�Ʈ���� ��ȯ
			oos.writeBoolean(dc.addSchedule(packet.getSchedule()));
			oos.flush();
			}catch(Exception ex){
				System.out.println("[Server] addSchedule error " + ex);
			}
		}
		/*����� ���̵� �ߺ�üũ �޼���*/
		public void CheckId(String msg)
		{
			try{
				
				String temp = msg.substring("[CheckId]".length());
				//temp���� �ߺ��˻� ��� ID �� �����.
				this.Id = temp;
				boolean result = dc.checkID(this.Id);
				oos.writeBoolean(result);
				oos.flush();
				
			}catch(Exception ex){
				System.out.println("[Server] CheckId error " + ex);
			}
		}
		/*����� �߰� �޼���*/
		public void AddUser(String msg)
		{
			try{
				String[] temp = msg.substring("[AddUser]".length()).split("/");
				//temp[0]�� ID ,  temp[1]�� PW �� �����.
				this.Id = temp[0];
				display.append("����� �߰�-> ID : " + temp[0]+" PW : "+temp[1]+"\n");
				boolean result = dc.addUser(temp[0], temp[1], temp[2], temp[3]);
				
				oos.writeBoolean(result);
				oos.flush();
					
				if(result)//����� �����϶���,
				{
					synchronized(hm)
					{
						hm.put(this.Id, oos);
					}
				}
			}catch(Exception ex){
				System.out.println("[Server] Adduser error" + ex);
			}
		}
		/*���� ������ ���� �޼���*/
		public void DeleteSchedule(String msg)
		{
			try{
				int wNum = Integer.parseInt(msg.substring("[DeleteSchedule]".length()));
				oos.writeBoolean(dc.deleteSchedule(wNum));
				oos.flush();
				}catch(Exception ex){
					System.out.println("[Server] DeleteSchedule error " + ex);
				}
		}
		/*��� ������ ���� �޼���*/
		public void DeleteAllSchedules()
		{
			try{
				oos.writeBoolean(dc.deleteAllSchedules());
				oos.flush();
			}catch(Exception ex){
				System.out.println("[Server] DeleteAllSchedules error " +ex);
			}
		}
		/*������ �� ���� ��� �޼���*/
		public void GetSchedule(String msg)
		{
			try{
				
				int wNum = Integer.parseInt(msg.substring("[GetSchedule]".length()));
				sPacket packet = new sPacket(dc.getSchedule(wNum));
				oos.writeObject(packet);
				oos.flush();
			
			}catch(Exception ex){
				System.out.println("[Server] getSchedule error "+ ex);
			}
		}
		/*������ ����*/
		public void ModifySchedule()
		{
			try{
				sPacket packet = (sPacket)ois.readObject();
				oos.writeBoolean(dc.modifySchedule(packet.getSchedule()));
				oos.flush();
				}catch(Exception ex){
					System.out.println("[Server] ModifySchedule error " + ex);
				}
		}
		/*��� �������� ��� �޼���*/
		public void GetSchedules()
		{
			try{
				
				sPacket packet = new sPacket(dc.getAllSchedules());
				oos.writeObject(packet);
				oos.flush();
				
			}catch(Exception ex){
				System.out.println("[Server] GetSchedules error  " +ex);
			}
		}
		/*Ư�� ����ڿ��� �޽����� �����ϴ� �޼���*/
		public void NotificationMessage()
		{
			try{
				
				mPacket packet = (mPacket)ois.readObject();
				oos.writeBoolean(dc.notificationMessage(packet.getAllPhones(), packet.getMessage()));
				oos.flush();
				
			}catch(Exception ex){
				System.out.println("[Server] Notification error " + ex);
			}
		}
		/*����ڿ��� ���޵� �޽����� ��� �޼���*/
		public void GetMessages()
		{
			try{
				mPacket packet = new mPacket(dc.getAllMessages());
				oos.writeObject(packet);
				oos.flush();
				
			}catch(Exception ex){
				System.out.println("[Server] GetMessage error " + ex);}
		}
		/*���� ����*/
		public void EXIT()
		{
			if(null != this.Id)
			{
				synchronized(hm)
				{
					hm.remove(this.Id);
				}
				display.append(this.Id + " ���� ����\n");
			}
			this.dc.Disconnection();
		}
		
		
		
		
	}//end of ClientRequest Thread
	
}//end of server



