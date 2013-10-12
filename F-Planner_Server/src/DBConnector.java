import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


public class DBConnector {
	
	Connection con = null;
	Statement st = null;
	ResultSet rs = null;
	String query = null;
	String Id = null;
	//int Unum;
	String[] fail = {"FAIL"};
	Schedule[] schedules;
	Schedule schedule;
	boolean isLogin = false;
	
	/*DB ����*/
	public boolean DBConnection(Connection c)//���� ������ true ���н� false�� ����
	{
		try {
			this.con = c;
			st = con.createStatement();
			return true;
		} catch (SQLException e) {
			return false;
		}
	}
	/*DB ���� ����*/
	public void Disconnection()
	{
		try {
			this.st.close();
			rs = null;
		} catch (SQLException e)
		{
			System.out.println("[DBConnector] Disconnection error " + e);
		}
	}
	/*ID, PW Ȯ��*/
	public String Login(String id, String passwd)//������ SUCCESS��� ���ڿ���, ���н� ���� ������ ����
	{
		String result = "SUCCESS";
	
		if(null==id || "".equals(id) || null == passwd || "".equals(passwd))
		{
			result = "FAIL";
		}
		else
		{
			try{
			query = "select Unum,Id,Pw from userinfo1202 where Id='"+id+"'";
			rs = st.executeQuery(query);
			rs.next();
			//ù��° �ʵ� : ���̵� �ι�° �ʵ� : ��й�ȣ
			//��
			if(!rs.getString(3).equals(passwd)) result = "DISMATCH";				
			else//���� ����
			{
				this.Id = id;
//				this.Unum = Integer.parseInt(rs.getString(1));//����� ��ȣ
				this.isLogin = true;
			}

			}catch(SQLException e){
				result = "JOIN";
			}
		}
		
		return result;
	}
	/*ȸ�����Խ�, �ߺ��� ���̵� �ִ��� check*/
	public boolean checkID(String id)//ID �ߺ��� true ��ȯ
	{
		if(!isSet(id) || !isLogin) return true;
		boolean result = true;
		
		try{
			query = "select Unum from userinfo1202 where Id = '"+id+"'";
			rs = st.executeQuery(query);
			rs.next();
			if(null == rs.getString("Unum")) result = false;
			
		}catch(SQLException ex){
			System.out.println("[DBConnector] checkID error " + ex);
			result = false;
		}
		
		return result;
		
	}
	/*���̵� ���� ����*/
	public boolean isIdSet()
	{
		if(null == this.Id || "".equals(Id)) return false;
		else return true;
	}
	/*Schedule DB�κ��� column(����)�� �ش��ϴ� �������� ����*/
	public String[] getInfo(String column)
	{
		if(!isIdSet()) return fail;
		
		String[] result;
		int index = 0;
		try{
			
			query = "select "+column+" from schedule1202 where Id='"+this.Id+"'"; 
			rs = st.executeQuery(query);
			while(rs.next()) index++;
			result = new String[index];
			rs.beforeFirst(); index = 0;
			while(rs.next()) result[index++] = rs.getString(1);
			
		}catch(SQLException e){
			result = fail;
		}
		
		return result;
	}
	/*���̵� ��� ������� ����*/
	public String[] getScheduleTitles()//������ ������� ����� �迭�� , ���н� FAIL �� ����
	{
		return getInfo("Title");
	}
	/*���̵� ��� ��¥���� ����*/
	public String[] getScheduleDates()
	{
		return getInfo("Date");
	}
	/*������ ����*/
	public String[] getDays()
	{
		return getInfo("Day");
	}
	/*��Ҹ� ����*/
	public String[] getPlace()
	{
		return getInfo("Place");
	}
	/*������ ����*/
	public String[] getContent()
	{
		return getInfo("Content");
	}
	/*�ݺ��Ǵ��� ���θ� ����*/
	public String[] getReplays()
	{
		return getInfo("Replay");
	}
	/*�켱������ ����*/
	public String[] getPriorities()
	{
		return getInfo("Priority");
	}
	/*����� ������ �Է���*/
	public boolean addUser(String ID, String PW, String PHONE, String NAME)
	{
		if(!isSet(ID)||!isSet(PW)||!isSet(PHONE)||!isSet(NAME)) return false;
			
		boolean result = false;
		
		try{
			
			query = "insert into userinfo1202(Id,Pw,Phone,Name) values('"+ID+"','"+PW+"','"+PHONE+"','"+NAME+"')";
			if(st.executeUpdate(query)>0)
			{
				result = true;
				this.Id = ID;
			}
		
		}catch(SQLException e){
			System.out.println("[DBConnector] addUser error " +  e);
			result = false;
		}
		
		return result;
	}
	/*���� ���� üũ*/
	public boolean isSet(String info)
	{
		if(null == info || "".equals(info)) return false;
		else 								return true;
	}
	/*�ð�ǥ ������ �Է�*/
	public boolean addSchedule(Schedule sc)
	{//����, ��¥, ����, ���̵�� �ʼ� ����
		
		if(!isIdSet() || !isLogin) return false;
		
		boolean result = false;
		int Wnum = 0;
		try{
			
			query = "select max(Wnum) from schedule1202 where Id='"+this.Id+"'";
			rs = st.executeQuery(query);
			rs.next();
			Wnum = Integer.parseInt(rs.getString(1))+1;
			query = "insert into schedule1202 values(NULL,"+Wnum+",'"+this.Id+"','"+sc.Title+"','"+sc.sDate+"','"+sc.eDate+"','"+sc.Day+"','"+sc.Place+"','"+sc.Content+"','"+sc.Replay+"','"+sc.Priority+"')";
		
			if(st.executeUpdate(query)>0) result = true;
			
		}catch(SQLException e){
			System.out.println("[DBConnector] addSchedule error " + e);
			result = false;
		}
		
		return result;
	}
	/*�ð�ǥ ���� ��ü�� ��� schedule ��ü�� ����*/
	public Schedule[] getAllSchedules()
	{
		int index = 0;
		
		try{
			query = "select Title,sDate,eDate,Day,Place,Content,Replay,Priority from schedule1202 where Id='"+this.Id+"'";
			rs = st.executeQuery(query);
			while(rs.next()) index++;
			schedules = new Schedule[index];
			rs.beforeFirst(); index = 0;
			while(rs.next())
			{
				schedules[index] = new Schedule(rs.getString("Title"),rs.getString("sDate"),rs.getString("eDate"),rs.getString("Day"),rs.getString("Place"),rs.getString("Content"),rs.getString("Replay").charAt(0),rs.getString("Priority").charAt(0));
				
				index++;
			}
			
		}catch(SQLException e){
			System.out.println("[DBConnector] getSchedules error "+e);
		}
		
		return schedules;
	}
	/*�� ����*/
	public boolean modifySchedule(Schedule sc)//�� ��ȣ�� ���ڷ� ����
	{
		 if(!isIdSet()) return false;
		 
		 boolean result = false;
		 try{
			 
			 query = "update schedule1202 set Title='"+sc.Title+"',sDate='"+sc.sDate+"',eDate='"+sc.eDate+"',Day='"+sc.Day+"',Place='"+sc.Place+"',Content='"+sc.Content+"',Replay='"+sc.Replay+"',Priority='"+sc.Priority+"' where Id='"+this.Id+"' and Wnum="+sc.wNum;
			
			 if(st.executeUpdate(query)>0) result = true;
			 
		 }catch(SQLException e){
			 result = false;
		 }
		
		return result;
	}
	/*���õ� ��ȣ�� �������� ����*/
	public Schedule getSchedule(int wNum)
	{
		try{
			
			query = "select Title,sDate,eDate,Day,Place,Content,Replay,Priority from schedule1202 where id='"+this.Id+"' and Wnum = "+wNum; 
			rs = st.executeQuery(query);
			rs.next();
			schedule = new Schedule(rs.getString("Title"),rs.getString("sDate"),rs.getString("eDate"),rs.getString("Day"),rs.getString("Place"),rs.getString("Content"),rs.getString("Replay").charAt(0),rs.getString("Priority").charAt(0));
			
		}catch(SQLException e){
			System.out.println("[DBConnector] getSchedule error " + e);
		}
		
		return schedule;
	}
	/*���õ� ��ȣ�� �������� ����*/
	public boolean deleteSchedule(int wNum)
	{
		if(! isIdSet()) return false;
		boolean result = false;
		try{
		
			query = "delete from schedule1202 where Id='"+this.Id+"' and Wnum="+wNum;
			if(st.executeUpdate(query)>0) result = true;
			
		}catch(SQLException e){
			result = false;
		}
		
		return result;
	}
	/*����� ������ ����(������ ������ ���޾� ����)*/
	public boolean deleteAllSchedules()
	{
		if(!isIdSet()) return false;
		
		boolean result = false;
		
		try{
			
			query = "delete from schedule1202 where Id='"+this.Id+"'";
			if(st.executeUpdate(query)>0) result = true;
			
			
		}catch(SQLException ex){
			System.out.println("[DBConnector] deleteAllSchedule error " + ex);
		}
		
		return result;
	}
	/*���� �޽����� ����*/
	public boolean notificationMessage(String[] phones, String msg)
	{
		boolean result = false;
		try{
			for(int i=0; i<phones.length; i++)
			{
				query = "insert into notification1202 values((select Id from userinfo1202 where Phone='"+phones[i]+"'),'"+msg+"',now())";
				if(st.executeUpdate(query) > 0) result = true;
				else {result = false; break;}
			}
			
		}catch(Exception ex){
			System.out.println("[DBConnector] notificationMessage error " + ex);
			
			if(!result)//�ϳ��� �������� ���,
			{//������ ���ԵǾ��� ������ ����
				for(int i=0; i<phones.length; i++)
				{
					query = "delete from notification1202 where Id=(select Id from userinfo1202 where Phone='"+phones[i]+"' and Msg='"+msg+"')";
					try {
						st.executeUpdate(query);
					} catch (SQLException e) {}
				}
			}
		}
		
		return result;
		
	}
	/*ID ��� �뺸�� �޽����� ����*/
	public String[] getAllMessages()
	{
		String[] result = null;
		int index = 0;
		try{
			
			query = "select Msg from notification1202 where Id='"+this.Id+"' order by Time";
			rs = st.executeQuery(query);
			while(rs.next()) index++;
			result = new String[index];
			rs.beforeFirst(); index = 0;
			while(rs.next()) result[index++] = rs.getString("Msg");
			
		}catch(Exception ex){
			System.out.println("[DBConnector] getMessages error " + ex);
		}
		
		return result;
	}
	
}
