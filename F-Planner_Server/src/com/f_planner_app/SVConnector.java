package com.f_planner_app;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

/*Server ���� �׽�Ʈ���� ����*/
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
	/*������ ����*/
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
	/*�α���*/
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
	/*����� �߰�*/
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
	/*���̵� �ߺ� üũ*/
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
	/*���ο� ������ �ۼ�*/
	public boolean addSchedule(Schedule schedule)
	{
		boolean result = false;
		
		try
		{
			oos.writeUTF("[AddSchedule]");//�÷��� ���� ����
			oos.flush();
			
			sPacket packet = new sPacket(schedule);
			oos.writeObject(packet);//���� ������ ���� ����
			oos.flush();
			
			//�������� �߰��� ��, �����κ��� ����� ����
			result = ois.readBoolean();
			
		}catch(Exception ex){
			System.out.println("[SVConnector] addSchedule error " + ex);
		}
		return result;
	}
	/*���� �������� ����*/
	public Schedule getSchedule(int wNum)//���н� null ����
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
	/*��� �������� ����*/
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
	/*���� ������ ����*/
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
	/*��� ������ ����*/
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
	/*���� ������ ����*/
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
	/*���ο� �׷� ���� �޽����� ����*/
	public boolean requestGroupTime(String Gname, ArrayList<String> phones,String content, String sDate, String eDate, String aTime ,String type)
	{
		boolean result = false;
		try{
			
			oos.writeUTF("[RequestGroupTime]");
			oos.flush();
			
			mPacket packet = new mPacket(Gname, phones, content, sDate, eDate,aTime, type);
			oos.writeObject(packet);
			oos.flush();
			//���..��ٸ���..
			result = ois.readBoolean();
			
		}catch(Exception ex){
			System.out.println("[SVConnector]sendGroupMessage error " + ex);
		}
		
		return result;
	}
	/*�������� ������ ����*/
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
	/*����ڰ� �ٸ�������� ��û�� ���� �ǰ��� ��*/
	public boolean sendOpinion(int Gid, String opinion)
	{
		boolean result = false;
		try{
			
			oos.writeUTF("[SendOpinion]"+Gid+"/"+opinion);
			oos.flush();
			////////����� ��ٸ�
			result = ois.readBoolean();
			
		}catch(Exception ex){
			System.out.println("[SVConnector] sendOpinion error " + ex);
		}
		
		
		return result;
		
	}
	/*������ ���۵� �޽����� ��� ����*/
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
			t.start();	t.join(10000);//�ִ� 10�� ���
			
			message = t.getResult();
			
		}catch(Exception ex){
			System.out.println("[SVConnector] getAllMessage error " + ex);
		}
		
		return message;
	}
	/*�ڽ��� ���ԵǾ��ִ� ��� �׷� ������ ����*/
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
