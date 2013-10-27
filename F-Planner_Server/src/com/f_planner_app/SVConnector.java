package com.f_planner_app;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

/*Server 에서 테스트중인 원본*/
public class SVConnector{

	private ObjectOutputStream oos = null;
	private ObjectInputStream ois = null;
	private Socket sock;
	private String Id;
	private final String SERVER_IP = "192.168.0.29";
	private final int SERVER_PORT = 12012;
	
	public SVConnector()
	{
		oos = null;
		ois = null;
		sock = null;
	}
	/*서버에 접속*/
	public boolean connectServer()
	{
		boolean result = false;
		
			try
			{
				sock = new Socket(SERVER_IP,SERVER_PORT);
				oos = new ObjectOutputStream(sock.getOutputStream());
				result = true;
			}
			catch(Exception ex)
			{
				System.out.println("[SVConnector] connectServer error " +ex);
			}
			
		return result;
	}
	public void setId(String id)
	{
		this.Id = id;
	}
	public Socket getSocket()
	{
		return this.sock;
	}
	/*로그인*/
	public String Login(final String id, final String pw)
	{
		
		String result = "FAIL";
		try
		{
			oos.writeUTF("[Login]"+id+"/"+pw);
			oos.flush();
			if(null==ois) ois = new ObjectInputStream(this.sock.getInputStream());
			result = ois.readUTF();
			this.Id = id;
		}
		catch(Exception ex)
		{
			System.out.println("[SVConnector] Login error " +ex);
		}
			
		return result;
	}
	/*사용자 추가*/
	public boolean addUser(String id, String pw, String phone, String name)
	{
		boolean result = false;
		try
		{
			oos.writeUTF("[AddUser]"+id+"/"+pw+"/"+phone+"/"+name);
			oos.flush();
			if(null == ois) ois = new ObjectInputStream(sock.getInputStream());
			result = ois.readBoolean();
			if(result) this.Id = id;
		}
		catch(Exception ex)
		{
			System.out.println("[SVConnector] addUser erorr " + ex);
		}
		
		return result;
	}
	/*아이디 중복 체크*/
	public boolean checkID(String id)
	{
		if(null==id || "".equals(id)) return true;
		boolean result = true;
		try
		{
			oos.writeUTF("[CheckId]"+id);
			oos.flush();
			if(null == ois) ois = new ObjectInputStream(sock.getInputStream());
			result = ois.readBoolean();
			
		}catch(Exception ex)
		{
			System.out.println("[SVConnector] checkID error " +ex );
		}
		
		return result;
		
	}
	/*새로운 스케줄 작성*/
	public boolean addSchedule(Schedule schedule)
	{
		boolean result = false;
		
		try
		{
			oos.writeUTF("[AddSchedule]");//플래그 먼저 전송
			oos.flush();
			
			sPacket packet = new sPacket(schedule);
			oos.writeObject(packet);//실제 스케줄 정보 전송
			oos.flush();
			
			//스케줄을 추가한 후, 서버로부터 결과를 받음
			result = ois.readBoolean();
			
		}catch(Exception ex){
			System.out.println("[SVConnector] addSchedule error " + ex);
		}
		return result;
	}
	/*단일 스케줄을 얻음*/
	public Schedule getSchedule(int wNum)//실패시 null 리턴
	{
		Schedule schedule = null;
		
		try{
			oos.writeUTF("[GetSchedule]"+wNum);
			oos.flush();
			
			sPacket packet = (sPacket)ois.readObject();
			schedule = packet.getSchedule();
		}catch(Exception e){
			System.out.println("[SVConnector] getScheduel error " + e);
		}
		
		return schedule;
	}
	/*모든 스케줄을 얻음*/
	public Schedule[] getAllSchedules()
	{
		Schedule[] schedules = null;
		try
		{
			oos.writeUTF("[GetSchedules]");
			oos.flush();
			
			sPacket packet = (sPacket)ois.readObject();
			schedules = packet.getSchedules();
			
		}catch(Exception ex)
		{
			System.out.println("[SVConnector] getSchedules error " + ex );
		}
		
		return schedules;
	}
	/*단일 스케줄 삭제*/
	public boolean deleteSchedule(int wNum)
	{
		boolean result = false;
		try
		{
			oos.writeUTF("[DeleteSchedule]"+wNum);
			oos.flush();
			result = ois.readBoolean();
			
		}catch(Exception ex){
			System.out.println("[SVConnector] deleteSchedule error " + ex);
		}
		
		return result;
	}
	/*모든 스케줄 삭제*/
	public boolean deleteAllSchedules()
	{
		boolean result = false;
		try{
			
			oos.writeUTF("[DeleteAllSchedules]");
			oos.flush();
			result = ois.readBoolean();
			
		}catch(Exception ex){
			System.out.println("[SVConnector] deleteAllSchedules error " + ex);
		}
		
		return result;
	}
	/*단일 스케줄 수정*/
	public boolean modifySchedule(int wNum, Schedule schedule)
	{
		boolean result = false;
		try{
			
			oos.writeUTF("[ModifySchedule]");
			oos.flush();
			
			schedule.wNum = wNum;
			sPacket packet = new sPacket(schedule);
			
			oos.writeObject(packet);
			oos.flush();
			
			result = ois.readBoolean();
			
		}catch(Exception ex){
			System.out.println("[SVConnector] modifySchedule error " + ex);
		}
		
		return result;
	}
	/*새로운 그룹 생성 메시지를 보냄*/
	public boolean requestGroupTime(String Gname, ArrayList<String> phones,String content, String sDate, String eDate, String aTime ,String type)
	{
		boolean result = false;
		try{
			
			oos.writeUTF("[RequestGroupTime]");
			oos.flush();
			
			mPacket packet = new mPacket(Gname, phones, content, sDate, eDate,aTime, type);
			oos.writeObject(packet);
			oos.flush();
			//잠시..기다리고..
			result = ois.readBoolean();
			
		}catch(Exception ex){
			System.out.println("[SVConnector]sendGroupMessage error " + ex);
		}
		
		return result;
	}
	/*서버와의 연결을 종료*/
	public boolean EXIT()
	{
		boolean result = false;
		try
		{
			oos.writeUTF("[EXIT]");
			oos.flush();
			
			this.Id = null;
			result = true;
		}
		catch(Exception ex)
		{
			System.out.println("[SVConnector] EXIT error " + ex);
		}
		
		return result;
	}
	/*사용자가 다른사용자의 요청에 대한 의견을 냄*/
	public boolean sendOpinion(int Gid, String opinion)
	{
		boolean result = false;
		try{
			
			oos.writeUTF("[SendOpinion]"+Gid+"/"+opinion);
			oos.flush();
			////////결과를 기다림
			result = ois.readBoolean();
			
		}catch(Exception ex){
			System.out.println("[SVConnector] sendOpinion error " + ex);
		}
		
		
		return result;
		
	}
	/*나에게 전송된 메시지를 모두 받음*/
	public Message[] getAllMessages()
	{
		Message[] message = null;
		
		try{
			
			class MessageThread extends Thread
			{
				private Message[] messages;
				
				@Override
				public void run()
				{
					try{
						
						oos.writeUTF("[GetAllMessages]");
						oos.flush();
						
						mPacket packet = (mPacket)ois.readObject();
						this.messages = packet.getAllMessage();
						
						
					}catch(Exception ex){
						System.out.println("[SVConnector] getAllMessage Thread error " + ex);
					}
				}
				
				public Message[] getResult()
				{
					return this.messages;
				}
			}
			
			MessageThread t = new MessageThread();
			t.start();	t.join(10000);//최대 10초 대기
			
			message = t.getResult();
			
		}catch(Exception ex){
			System.out.println("[SVConnector] getAllMessage error " + ex);
		}
		
		return message;
	}
	/*자신이 포함되어있는 모든 그룹 정보를 얻음*/
	public Group[] getAllGroupInfo()
	{
		Group[] group = null;
		try{
			
			oos.writeUTF("[GetAllGroupInfo]");
			oos.flush();
			
			gPacket packet = (gPacket)ois.readObject();
			group = packet.getAllGroup();
			
		}catch(Exception ex){
			System.out.println("[SVConnector] getAllGroupInfo error " +ex);
		}
		
		return group;
	}
}
