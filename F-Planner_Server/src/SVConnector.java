import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;


public class SVConnector{

	private ObjectOutputStream oos = null;
	private ObjectInputStream ois = null;
	private Socket sock;
	private String Id;
	private final String SERVER_IP = "203.252.182.162";
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
	/*�α���*/
	public String Login(String id, String pw)
	{
		String result = "FAIL";
		try
		{
			oos.writeUTF("[Login]"+id+"/"+pw);
			oos.flush();
			if(null==ois) ois = new ObjectInputStream(this.sock.getInputStream());
			result = ois.readUTF();
			id = this.Id;
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
	/*��ü �뺸(��ȭ��ȣ�� ArrayList���)*/
	public boolean notifyMessage(ArrayList<String> ph, String msg)
	{
		boolean result = false;
		
		try{
			oos.writeUTF("[Notification]");
			oos.flush();
			
			mPacket packet = new mPacket(ph,msg);
			oos.writeObject(packet);
			oos.flush();
			
			result = ois.readBoolean();
			
		}catch(Exception ex){
			System.out.println("[SVConnector] notifyMessage->ArrayList error " + ex);
		}
		
		return result;
	}
	/*��ü ���� �޽��� (���ڿ� �迭 ���)*/
	public boolean notifyMessage(String[] ph, String msg)
	{
		boolean result = false;
		
		try{
			oos.writeUTF("[Notification]");
			oos.flush();
			
			mPacket packet = new mPacket(ph,msg);
			oos.writeObject(packet);
			oos.flush();
			
			result = ois.readBoolean();
			
		}catch(Exception ex){
			System.out.println("[SVConnector] notifyMessage->String error " + ex);
		}
		
		return result;
	}
	/*���� ���� �޽��� Ȯ��*/
	public ArrayList<String> getAllMessages()
	{
		ArrayList<String> result = new ArrayList<String>();
		try{
			
			oos.writeUTF("[GetMessages]");
			oos.flush();
			
			mPacket packet = (mPacket)ois.readObject();

			String[] temp = packet.getAllMessages();
			for(int i=0; i<temp.length; i++)
			result.add(temp[i]);
			
		}catch(Exception ex){
			System.out.println("[SVConnector] getMessages error " + ex);
		}
		
		return result;
	}
	/*������ ���� ����*/
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
}
