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
		setSize(700,400);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	/*서버 객체들 초기화*/
	public void init()
	{
		hm = new HashMap<String,ObjectOutputStream>();
		userList = new List();
		display = new JTextArea();
		display.setEditable(false);
		scroll = new JScrollPane(display);
		scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		
		add("Center",scroll);
		
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
					
				ClientRequest cr = new ClientRequest(sock);
				cr.start();
			}
		}catch(Exception e){display.append("Server run error " +e +"\n");}
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
					
					else if(msg.indexOf("[FindFreeTime]") != -1) FindFreeTime(msg);
					else if(msg.indexOf("[GetGroupPeopleLocation]") != -1) GetGroupPeopleLocation(msg);
					else if(msg.indexOf("[SendLocation]") != -1) SendLocation(msg);
					else if(msg.indexOf("[GetDestination]") != -1) GetDestination(msg);
					else if(msg.indexOf("[getGroupArriveTime]") != -1) GetGroupArriveTime();
					else if(msg.indexOf("[CancelGroup]") != -1) CancelGroup(msg);
					else if(msg.indexOf("[SetGroupPeopleOpinion]") != -1) SetGroupPeopleOpinion(msg);
					else if(msg.indexOf("[GetDaySchedule]") != -1) GetDaySchedule(msg);
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
				Log("[Server->ClientRuequst] run error " + ex);
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
		
		public void FindFreeTime(String msg)
		{
			try{
				String Gid = msg.substring("[FindFreeTime]".length());
				oos.writeObject(new sPacket(dc.findFreeTime(Gid)));
				oos.flush();
				
			}catch(Exception ex){
				Log("[Server]FindFreeTime error " + ex);
			}
		}
		
		public void GetGroupArriveTime()
		{
			try{
				
				oos.writeObject(new mPacket(dc.getGroupArriveTime()));
				oos.flush();
				
			}catch(Exception ex){
				System.out.println("[Server] GetGroupArriveTime error "  + ex);
			}
		}
		
		public void GetDestination(String msg)
		{
			String Gid = msg.substring("[GetDestination]".length());
			try{
				oos.writeObject(new sPacket(dc.getDestination(Gid)));
				oos.flush();
				
			}catch(Exception ex){
				Log("[Server] GetDestination error " + ex);
			}
		}
		
		public void GetGroupPeopleLocation(String msg)
		{
			try{
			String gid = msg.substring("[GetGroupPeopleLocation]".length());	
			oos.writeObject(new mPacket(dc.getGroupPeopleLocation(gid)));
			oos.flush();
			}catch(Exception ex){
				Log("[Server] GetGroupPeopleLocation error " + ex);
			}
		}
		
		public void SendLocation(String msg)
		{
			try{
				String[] temp = msg.substring("[SendLocation]".length()).split("/");
				//temp[0] 은 위도(X) , temp[1] 은 경도(Y) , temp[2] 는 남은시간 , temp[3] 은 도착여부
				
				oos.writeBoolean(dc.sendLocation(temp[0],temp[1],temp[2],temp[3]));
				oos.flush();
				
				
			}catch(Exception ex){
				Log("[Server] sendLocation error " + ex);
			}
		}
		
		public void CancelGroup(String msg)
		{
			try{
				String gid = msg.substring("[CancelGroup]".length());
				oos.writeBoolean(dc.CancelGroup(gid));
				oos.flush();
			}catch(Exception ex){
				Log("[Server] CancelGroup error " + ex);
			}
		}
		
		public void SetGroupPeopleOpinion(String msg)
		{
			try{
				String[] temp = msg.substring("[SetGroupPeopleOpinion]".length()).split("/");
				
				//temp[0]은 Gid, temp[1]은 opinion
				oos.writeBoolean(dc.SetGroupPeopleOpinion(temp[0],temp[1]));
				oos.flush();
				
			}catch(Exception ex){
				Log("[Server] SetGroupPeopleOpinion error " + ex);
			}
		}
		
		public void GetDaySchedule(String msg)
		{
			String YMD = msg.substring("[GetDaySchedule]".length());
			try{
				
				oos.writeObject(new sPacket(dc.getDaySchedule(YMD)));
				oos.flush();
				
			}catch(Exception ex){
				display.append("GetDaySchedule error" + ex + "\n");
			}
		}
		
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
		/*메시지를 지움*/
		public void DeleteMessage()
		{
			try{
				display.append(this.Id+ " 가 메시지를 삭제\n");
				
				mPacket packet = (mPacket)ois.readObject();
				oos.writeBoolean(dc.deleteMessage(packet.getList()));
				oos.flush();
				
			}catch(Exception ex){
				display.append("[Server] DeleteMessage error " + ex+"\n");
			}
		}
		/*메시지를 보냄*/
		public void SendMessage()
		{
			try{
				
				mPacket packet = (mPacket)ois.readObject();
				display.append(this.Id+"가 메시지를 보냄\n");
				display.append("제목 : " + packet.getTitle()+" 내용 : "+packet.getContent()+"\n");
				display.append(""+packet.getList().size()+"\n");
				oos.writeBoolean(dc.sendMessage(packet));
				oos.flush();
				
			}catch(Exception ex){
				display.append("[Server] SendMessage error " + ex+"\n");
			}
		}
		/*사용자가 포함된 모든 그룹 정보를 얻음*/
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
		/*사용자에게 전송된 모든 메시지를 받음*/
		public void GetAllMessages()
		{
			try{
				display.append(this.Id+" 가 메시지 정보를 요청\n");
				
				mPacket packet = new mPacket(dc.getAllMessages());
				oos.writeObject(packet);
				oos.flush();
				}catch(Exception ex){
					display.append("[Server] GetAllMessage error " +ex+"\n");
				}
		}
		/*개인 메시지 응답*/
		public void SendOpinion()
		{
			try{
				
				oos.writeBoolean(dc.sendOpinion((Message)ois.readObject()));
				oos.flush();
				
				}catch(Exception ex){
					display.append("[Server] SendOpinion error " + ex);
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
					display.append("[Server] RequestGourpTime error "+ex );
				}
		}
		/*Loing 메서드*/
		public void Login(String msg)
		{
			try{
			// '/' 로 연결된 사용자 아이디와 패스워드를 분리
			String[] temp = msg.substring("[Login]".length()).split("/");
			
			display.append("로그인 시도 -> ID : "+temp[0]+ " PW : " + temp[1]+"\n");
			
			//로그인 성공-> SUCCESS, 실패-> FAIL
			//비밀번호 불일치-> DISMATCH , 없는 계정-> JOIN
			String result = dc.Login(temp[0], temp[1]);
			display.append("로그인 결과 : "+result+" \n");
			
			//결과 통보
			oos.writeUTF(result);
			oos.flush();
			
			if("SUCCESS".equals(result))///결과가 성공일때만,
			{	
				this.Id = temp[0];
				//해시맵에 저장
				synchronized(hm)
				{
					hm.put(this.Id, oos);
				}
			}
			}catch(Exception ex){
				display.append("[Server] Login error " + ex);
			}
		}
		/*Schedule 추가 메서드*/
		public void AddSchedule()
		{
			try{
			//해당 플래그를 읽어들였다면, 다음으로 스케줄 객체를 받는다.
			sPacket packet = (sPacket)ois.readObject();
			
			display.append("시작시간 : " + packet.getSchedule().sDate + " \n");
			display.append("종료시간 : " + packet.getSchedule().eDate + " \n");
			
			//DB에 스케줄을 더하고, 그 결과를 클라이언트에게 반환
			oos.writeBoolean(dc.addSchedule(packet.getSchedule()));
			oos.flush();
			}catch(Exception ex){
				display.append("[Server] addSchedule error " + ex + "\n");
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
				display.append("[Server] CheckId error " + ex+"\n");
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
				display.append("[Server] Adduser error" + ex + "\n");
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
					display.append("[Server] DeleteSchedule error " + ex + "\n");
				}
		}
		/*모든 스케줄 삭제 메서드*/
		public void DeleteAllSchedules()
		{
			try{
				oos.writeBoolean(dc.deleteAllSchedules());
				oos.flush();
			}catch(Exception ex){
				display.append("[Server] DeleteAllSchedules error " +ex + "\n");
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
				display.append("[Server] getSchedule error "+ ex + "\n");
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
					display.append("[Server] ModifySchedule error " + ex + "\n");
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
				display.append("[Server] GetSchedules error  " +ex + "\n");
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
		
		public void Log(String error)
		{
			display.append(error+"\n");
		}
	}//end of ClientRequest Thread
}//end of server




