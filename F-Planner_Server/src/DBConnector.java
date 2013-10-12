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
	
	/*DB 연결*/
	public boolean DBConnection(Connection c)//접속 성공시 true 실패시 false를 리턴
	{
		try {
			this.con = c;
			st = con.createStatement();
			return true;
		} catch (SQLException e) {
			return false;
		}
	}
	/*DB 연결 해제*/
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
	/*ID, PW 확인*/
	public String Login(String id, String passwd)//성공시 SUCCESS라는 문자열을, 실패시 실패 내용을 리턴
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
			//첫번째 필드 : 아이디 두번째 필드 : 비밀번호
			//비교
			if(!rs.getString(3).equals(passwd)) result = "DISMATCH";				
			else//접속 성공
			{
				this.Id = id;
//				this.Unum = Integer.parseInt(rs.getString(1));//사용자 번호
				this.isLogin = true;
			}

			}catch(SQLException e){
				result = "JOIN";
			}
		}
		
		return result;
	}
	/*회원가입시, 중복된 아이디가 있는지 check*/
	public boolean checkID(String id)//ID 중복시 true 반환
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
	/*아이디 부재 검증*/
	public boolean isIdSet()
	{
		if(null == this.Id || "".equals(Id)) return false;
		else return true;
	}
	/*Schedule DB로부터 column(단일)에 해당하는 정보들을 얻음*/
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
	/*아이디 기반 제목들을 얻음*/
	public String[] getScheduleTitles()//성공시 제목들이 저장된 배열을 , 실패시 FAIL 을 리턴
	{
		return getInfo("Title");
	}
	/*아이디 기반 날짜들을 얻음*/
	public String[] getScheduleDates()
	{
		return getInfo("Date");
	}
	/*요일을 얻음*/
	public String[] getDays()
	{
		return getInfo("Day");
	}
	/*장소를 얻음*/
	public String[] getPlace()
	{
		return getInfo("Place");
	}
	/*내용을 얻음*/
	public String[] getContent()
	{
		return getInfo("Content");
	}
	/*반복되는지 여부를 얻음*/
	public String[] getReplays()
	{
		return getInfo("Replay");
	}
	/*우선순위를 얻음*/
	public String[] getPriorities()
	{
		return getInfo("Priority");
	}
	/*사용자 정보를 입력함*/
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
	/*정보 부재 체크*/
	public boolean isSet(String info)
	{
		if(null == info || "".equals(info)) return false;
		else 								return true;
	}
	/*시간표 정보를 입력*/
	public boolean addSchedule(Schedule sc)
	{//제목, 날짜, 요일, 아이디는 필수 정보
		
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
	/*시간표 정보 전체를 얻어 schedule 객체에 저장*/
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
	/*글 수정*/
	public boolean modifySchedule(Schedule sc)//글 번호를 인자로 받음
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
	/*선택된 번호의 스케줄을 얻음*/
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
	/*선택된 번호의 스케줄을 지움*/
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
	/*사용자 정보를 지움(스케줄 정보도 연달아 삭제)*/
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
	/*통지 메시지를 넣음*/
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
			
			if(!result)//하나라도 실패했을 경우,
			{//기존에 삽입되었던 데이터 삭제
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
	/*ID 기반 통보된 메시지를 얻음*/
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
