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
	/*로그인*/
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
	/*단체 통보(전화번호가 ArrayList기반)*/
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
	/*단체 통지 메시지 (문자열 배열 기반)*/
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
	/*통지 받은 메시지 확인*/
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
	/*서버와 연결 해제*/
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
