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
	/*생성자*/
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
	/*서버 프레임 초기화*/
	public void Frameinit()
	{
		setLayout(new BorderLayout());
		setSize(500,400);
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	/*서버 객체들 초기화*/
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
	/*서버 동작*/
	public void serverStart()
	{
		try{
			while(true)
			{
				display.append("접속 대기중..\n");
				Socket sock = server.accept();
				//최초 소켓으로 부터 br 과 pw 객체를 얻는다.
					
				ClientRequest cr = new ClientRequest(sock);
				cr.start();
			}
		}catch(Exception e){System.out.println("Server run error");}
	}
	/*main method*/
	public static void main(String[] args) {
		new Server();
	}
	/*사용자 질의를 처리하는 클래스*/
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
				if(dc.DBConnection(con)) display.append("DB 연결 성공!\n");
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
					else if(msg.indexOf("[GetAllGroupInfo]") != -1)
					{
						try{
							
							gPacket packet = new gPacket(dc.getAllGroupInfo());
							oos.writeObject(packet);
							oos.flush();
							
						}catch(Exception ex){
							System.out.println("[Server] GetAllGroupInfo error " + ex);
						}
					}
					else if(msg.indexOf("[GetAllMessages]") != -1) GetAllMessages();
					else if(msg.indexOf("[SendOpinion]") != -1) SendOpinion(msg);
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
				System.out.println("[Server->ClientRuequst] run error " + ex);
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
		/*사용자에게 전송된 모든 메시지를 받음*/
		public void GetAllMessages()
		{
			try{
				mPacket packet = new mPacket(dc.getAllMessages());
				oos.writeObject(packet);
				oos.flush();
				}catch(Exception ex){
					System.out.println("[Server] GetAllMessage error " +ex);
				}
		}
		/*개인 메시지 응답*/
		public void SendOpinion(String msg)
		{
			try{
				String[] temp = msg.substring("[SendOpinion]".length()).split("/");
				//temp[0]에는 Gid 가, temp[1]에는 사용자의견, temp[2]에는 시간이 들어간다.
				oos.writeBoolean(dc.sendOpinion(temp[0], temp[1], temp[2]));
				oos.flush();
				}catch(Exception ex){
					System.out.println("[Server] SendOpinion error " + ex);
				}
		}
		/*단체 시간 요청 및 메시지 발송 메서드*/
		public void RequestGroupTime()
		{
			try{
				mPacket packet = (mPacket)ois.readObject();
				oos.writeBoolean(dc.requestGroupTime(packet));
				oos.flush();
				
				}catch(Exception ex){
					System.out.println("[Server] RequestGourpTime error "+ex );
				}
		}
		/*Loing 메서드*/
		public void Login(String msg)
		{
			try{
			// '/' 로 연결된 사용자 아이디와 패스워드를 분리
			String[] temp = msg.substring("[Login]".length()).split("/");
			this.Id = temp[0];
			display.append("사용자 로그인 -> ID : "+this.Id+ " PW : " + temp[1]+"\n");
			
			//로그인 성공-> SUCCESS, 실패-> FAIL
			//비밀번호 불일치-> DISMATCH , 없는 계정-> JOIN
			String result = dc.Login(this.Id, temp[1]);
			display.append("로그인 결과 : "+result+" \n");
			
			//결과 통보
			oos.writeUTF(result);
			oos.flush();
			
			if(null == ois || null == oos)
				display.append("서버 연결 끊김..\n");
			
			if("SUCCESS".equals(result))///결과가 성공일때만,
			{	
				//해시맵에 저장
				synchronized(hm)
				{
					hm.put(this.Id, oos);
				}
			}
			}catch(Exception ex){
				System.out.println("[Server] Login error " + ex);
			}
		}
		/*Schedule 추가 메서드*/
		public void AddSchedule()
		{
			try{
			//해당 플래그를 읽어들였다면, 다음으로 스케줄 객체를 받는다.
			sPacket packet = (sPacket)ois.readObject();
			
			//DB에 스케줄을 더하고, 그 결과를 클라이언트에게 반환
			oos.writeBoolean(dc.addSchedule(packet.getSchedule()));
			oos.flush();
			}catch(Exception ex){
				System.out.println("[Server] addSchedule error " + ex);
			}
		}
		/*사용자 아이디 중복체크 메서드*/
		public void CheckId(String msg)
		{
			try{
				
				String temp = msg.substring("[CheckId]".length());
				//temp에는 중복검사 대상 ID 가 저장됨.
				this.Id = temp;
				boolean result = dc.checkID(this.Id);
				oos.writeBoolean(result);
				oos.flush();
				
			}catch(Exception ex){
				System.out.println("[Server] CheckId error " + ex);
			}
		}
		/*사용자 추가 메서드*/
		public void AddUser(String msg)
		{
			try{
				String[] temp = msg.substring("[AddUser]".length()).split("/");
				//temp[0]은 ID ,  temp[1]은 PW 가 저장됨.
				this.Id = temp[0];
				display.append("사용자 추가-> ID : " + temp[0]+" PW : "+temp[1]+"\n");
				boolean result = dc.addUser(temp[0], temp[1], temp[2], temp[3]);
				
				oos.writeBoolean(result);
				oos.flush();
					
				if(result)//결과가 성공일때만,
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
		/*단일 스케줄 삭제 메서드*/
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
		/*모든 스케줄 삭제 메서드*/
		public void DeleteAllSchedules()
		{
			try{
				oos.writeBoolean(dc.deleteAllSchedules());
				oos.flush();
			}catch(Exception ex){
				System.out.println("[Server] DeleteAllSchedules error " +ex);
			}
		}
		/*스케줄 한 행을 얻는 메서드*/
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
		/*스케줄 변경*/
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
		/*모든 스케줄을 얻는 메서드*/
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
		/*접속 종료*/
		public void EXIT()
		{
			if(null != this.Id)
			{
				synchronized(hm)
				{
					hm.remove(this.Id);
				}
				display.append(this.Id + " 접속 종료\n");
				display.append("현재 접속 인원 : " + hm.size()+"\n");
			}
			this.dc.Disconnection();
		}
		
	}//end of ClientRequest Thread
	
}//end of server




