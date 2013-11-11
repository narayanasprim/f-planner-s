package com.f_planner_app;
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
					
				ClientRequest cr = new ClientRequest(sock);
				cr.start();
			}
		}catch(Exception e){display.append("Server run error " +e +"\n");}
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
			dc = new DBConnector(display);
			try {
				ois = new ObjectInputStream(sock.getInputStream());
				oos = new ObjectOutputStream(sock.getOutputStream());
				if(dc.DBConnection(con)) display.append("DB ���� ����!\n");
			} catch (IOException e) {
				display.append("[Server->ClientRequest] Object create error " + e+"\n");
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
					else if(msg.indexOf("[GetAllMessages]") != -1) GetAllMessages();
					else if(msg.indexOf("[GetGroupPeopleOpinion]") != -1) GetGroupPeopleOpinion(msg);
 					else if(msg.indexOf("[GetRecentMessageDate]") != -1) GetRecentMessageDate();
					else if(msg.indexOf("[DeleteMessage]") != -1) DeleteMessage();
					else if(msg.indexOf("[SendMessage]") != -1) SendMessage();
					else if(msg.indexOf("[SendOpinion]") != -1) SendOpinion();
					else if(msg.indexOf("[GetAllGroupInfo]") != -1) GetAllGroupInfo();
					else if(msg.indexOf("[RequestGroupTime]") != -1) RequestGroupTime();
					else if(msg.indexOf("[AddSchedule]") != -1) AddSchedule();
					else if(msg.indexOf("[GetSchedule]") != -1) GetSchedule(msg);
					else if(msg.indexOf("[GetSchedules]") != -1) GetSchedules();
					else if(msg.indexOf("[ModifySchedule]") != -1) ModifySchedule();
					else if(msg.indexOf("[DeleteSchedule]") != -1) DeleteSchedule(msg);
					else if(msg.indexOf("[DeleteAllSchedules]") != -1) DeleteAllSchedules();
					else if(msg.indexOf("[CheckId]") != -1) CheckId(msg);
					else if(msg.indexOf("[AddUser]") != -1) AddUser(msg);
					else if(msg.indexOf("[EXIT]") != -1)  break;

					display.setSelectionStart(display.getText().length());
				}
				
			}catch(Exception ex){
				display.append("[Server->ClientRuequst] run error " + ex);
			}
			finally
			{
				try{
					EXIT();
					ois.close();
					oos.close();
				}catch(Exception e){}
			}
		}//end of run
		
		public void GetGroupPeopleOpinion(String msg)
		{
			int Gid = Integer.parseInt(msg.substring("[GetGroupPeopleOpinion]".length()));
			
			try{
				
				oos.writeObject(new mPacket(dc.getGroupPeopleOpinion(Gid)));
				oos.flush();
				
			}catch(Exception ex){
				display.append("[Server] GetGroupPeopleOpinion error " + ex+"\n");
			}
		}
		
		public void GetRecentMessageDate()
		{
			try{
			oos.writeUTF(dc.getRecentMessageDate());
			oos.flush();
			}catch(Exception ex){
				display.append("[Server] GetRecentMessageDate error " + ex+"\n");
			}
		}
		/*�޽����� ����*/
		public void DeleteMessage()
		{
			try{
				display.append(this.Id+ " �� �޽����� ����\n");
				
				mPacket packet = (mPacket)ois.readObject();
				oos.writeBoolean(dc.deleteMessage(packet.getList()));
				oos.flush();
				
			}catch(Exception ex){
				display.append("[Server] DeleteMessage error " + ex+"\n");
			}
		}
		/*�޽����� ����*/
		public void SendMessage()
		{
			try{
				
				mPacket packet = (mPacket)ois.readObject();
				
				oos.writeBoolean(dc.sendMessage(packet));
				oos.flush();
				
			}catch(Exception ex){
				display.append("[Server] SendMessage error " + ex+"\n");
			}
		}
		/*����ڰ� ���Ե� ��� �׷� ������ ����*/
		public void GetAllGroupInfo()
		{
			try{
				
				gPacket packet = new gPacket(dc.getAllGroupInfo());
				oos.writeObject(packet);
				oos.flush();
				
			}catch(Exception ex){
				display.append("[Server] GetAllGroupInfo error " + ex+"\n");
			}
		}
		/*����ڿ��� ���۵� ��� �޽����� ����*/
		public void GetAllMessages()
		{
			try{
				display.append(this.Id+" �� �޽��� ������ ��û\n");
				
				mPacket packet = new mPacket(dc.getAllMessages());
				oos.writeObject(packet);
				oos.flush();
				}catch(Exception ex){
					display.append("[Server] GetAllMessage error " +ex+"\n");
				}
		}
		/*���� �޽��� ����*/
		public void SendOpinion()
		{
			try{
				
				oos.writeBoolean(dc.sendOpinion((Message)ois.readObject()));
				oos.flush();
				
				}catch(Exception ex){
					display.append("[Server] SendOpinion error " + ex);
				}
		}
		/*��ü �ð� ��û �� �޽��� �߼� �޼���*/
		public void RequestGroupTime()
		{
			try{
				mPacket packet = (mPacket)ois.readObject();
				oos.writeBoolean(dc.requestGroupTime(packet));
				oos.flush();
				
				}catch(Exception ex){
					display.append("[Server] RequestGourpTime error "+ex );
				}
		}
		/*Loing �޼���*/
		public void Login(String msg)
		{
			try{
			// '/' �� ����� ����� ���̵�� �н����带 �и�
			String[] temp = msg.substring("[Login]".length()).split("/");
			
			display.append("�α��� �õ� -> ID : "+temp[0]+ " PW : " + temp[1]+"\n");
			
			//�α��� ����-> SUCCESS, ����-> FAIL
			//��й�ȣ ����ġ-> DISMATCH , ���� ����-> JOIN
			String result = dc.Login(temp[0], temp[1]);
			display.append("�α��� ��� : "+result+" \n");
			
			//��� �뺸
			oos.writeUTF(result);
			oos.flush();
			
			if("SUCCESS".equals(result))///����� �����϶���,
			{	
				this.Id = temp[0];
				//�ؽøʿ� ����
				synchronized(hm)
				{
					hm.put(this.Id, oos);
				}
			}
			}catch(Exception ex){
				display.append("[Server] Login error " + ex);
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
				display.append("[Server] addSchedule error " + ex);
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
				display.append("[Server] CheckId error " + ex);
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
				display.append("[Server] Adduser error" + ex);
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
					display.append("[Server] DeleteSchedule error " + ex);
				}
		}
		/*��� ������ ���� �޼���*/
		public void DeleteAllSchedules()
		{
			try{
				oos.writeBoolean(dc.deleteAllSchedules());
				oos.flush();
			}catch(Exception ex){
				display.append("[Server] DeleteAllSchedules error " +ex);
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
				display.append("[Server] getSchedule error "+ ex);
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
					display.append("[Server] ModifySchedule error " + ex);
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
				display.append("[Server] GetSchedules error  " +ex);
			}
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
				display.append("���� ���� �ο� : " + hm.size()+"\n");
			}
			this.dc.Disconnection();
			
		}
		
	}//end of ClientRequest Thread
}//end of server




