package com.f_planner_app;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import javax.swing.JTextArea;


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
	JTextArea display;
	public DBConnector(JTextArea display) {
		this.display = display;
	}
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
			rs.last();
			schedules = new Schedule[rs.getRow()];
			rs.beforeFirst();
			while(rs.next())
			{
				schedules[index++] = new Schedule(rs.getString("Title"),rs.getString("sDate"),rs.getString("eDate"),rs.getString("Day"),rs.getString("Place"),rs.getString("Content"),rs.getString("Replay").charAt(0),rs.getString("Priority").charAt(0));
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
	/*그룹 생성 및 메시지를 통보*/
	public boolean requestGroupTime(mPacket packet)
	{
		 String Gname = packet.getGname();
		 ArrayList<String> phones = packet.getAllPhones();
		 String content = packet.getContent();
		 String sDate = packet.getSdate();
		 String eDate = packet.getEdate();
		 String aTime = packet.getAtime();
		 String type = packet.getMessageType();
		 boolean result = false;
		 String names = "",Gid = "";
		 ArrayList<String> idList = new ArrayList<String>();
		 try{
			 
			 //리스트에서 번호를 얻어 해당 번호의 이름을 얻는다.
			 for(int i=0; i<phones.size(); i++)
			 {
				 try{
				 query = "select Id,Name from userinfo1202 where Phone='"+phones.get(i)+"'";
				 rs = st.executeQuery(query);
				 rs.next();
				 names += rs.getString("Name")+",";
				 idList.add(rs.getString("Id"));
				 }catch(Exception ex){}
			 }
			 names = names.substring(0,names.length()-1);//마지막 , 를 지움
			
			//findtime 테이블에 정보를 만든다.
			 try{
			 query = "insert into findtime1202(Leader,Gname,People,Pcount,Content,sDate,eDate,aTime,DATE) " +
					"values('"+this.Id+"','"+Gname+"','"+names+"',"+(idList.size()+1)+",'"+content+"','"+sDate+"','"+eDate+"','"+aTime+"',now())";
			 st.executeUpdate(query);
			 }catch(Exception ex){System.out.println("[DBConnector] requestGroupTime->insert findtime");}
			
			//가장 최근에 생성한 그룹의 Gid를 가져온다.
			try{
				query = "select Gid from findtime1202 where Leader = '"+this.Id+"' order by DATE desc limit 1";
				rs = st.executeQuery(query);
				rs.next();
				Gid = rs.getString("Gid");
			}catch(Exception ex){System.out.println("[DBConnector] requestGroupTime->get Gid");}
			
			 //자신의 그룹 컬럼 정보를 업데이트 한다.
			 query = "update userinfo1202 set Groups=concat(Groups,'"+Gid+"/') where Id='"+this.Id+"'";
			 st.executeUpdate(query);
			
			//groupSchedule을 만든다.
			try{
				query = "insert into groupschedule1202(Gid,Pcount,sDate,eDate) values("+Gid+",0,0,0)";
				st.executeUpdate(query);
				MergeSchedule(Gid);
			}catch(Exception ex){System.out.println("[DBConnector] requestGroupTime->insert groupschedule");}
			
			//해당 Gid로 다른 사용자들에게 메시지를 보낸다.
			for(int i=0; i<idList.size(); i++)
			{
				query = "insert into message1202(Receiver,Gid,Leader,Content,Type,Time) values('"+idList.get(i)+"',"+Gid+",'"+this.Id+"','"+content+"','"+type+"',now())";
				st.executeUpdate(query);
			}
			
			result = true;
			
		 }catch(Exception ex){
			 System.out.println("[DBConnector] requestGroupTime error " + ex);
		 }
	
		return result;
	
	}
	/*사용자 의견 적용*/
	public boolean sendOpinion(String Gid, String opinion)
	{
		boolean result = false;
		
		try{
			if(opinion.equals("ACCEPT"))
			{
				query = "update userinfo1202 set Groups=concat(Groups,'"+Gid+"/') where Id='"+this.Id+"'";
				st.executeUpdate(query);
				//여기서 GroupSchedule 과 Merge 가 수행되어야 한다.
				MergeSchedule(Gid);
			}
			
			query = "update message1202 set Decision='"+opinion+"' where Receiver='"+this.Id+"' and Gid ="+Gid+" and Type='REQUEST'";
			st.executeUpdate(query);
			
			result = true;
			
		}catch(Exception ex){
			System.out.println("[DBConnector] sendOpinion error " + ex);
		}
		
		return result;
	}
	/*스케줄을 합침(그룹 스케줄이 됨)*/
	public boolean MergeSchedule(String Gid)
	{
		boolean result = false;
		int Pcount = 0;
		int index = 0;
		try{
					
			//자신의 스케줄 정보를 가져옴
			query = "select sc.sDate, sc.eDate from schedule1202 sc, findtime1202 ft  where sc.Id='"+this.Id+"' and ft.Gid="+Gid+" and sc.eDate>=ft.sDate and sc.sDate<=ft.eDate order by sDate";
			rs = st.executeQuery(query);
			
			rs.last(); 
			Schedule[] userSchedule = new Schedule[rs.getRow()];
			rs.beforeFirst();
			while(rs.next()) userSchedule[index++] = new Schedule(rs.getString("sDate"),rs.getString("eDate"));
			
			
			//기존 그룹의 스케줄 정보를 가져옴
			ArrayList<Schedule> groupSchedule= new ArrayList<Schedule>();
			query = "select Pcount, sDate, eDate from groupschedule1202 where Gid="+Gid+" order by sDate asc";
			rs = st.executeQuery(query); 
			while(rs.next()) groupSchedule.add(new Schedule(rs.getString("sDate"),rs.getString("eDate")));
			
			rs.first(); Pcount = Integer.parseInt(rs.getString("Pcount"));
			
			
			if(groupSchedule.get(0).sDate.equals("0"))
			{//등록된 그룹 스케줄이 없다면,
				groupSchedule.clear();
				
				for(Schedule s : userSchedule)
				groupSchedule.add(s);
			}
			else//기존에 그룹 스케줄이 존재한다면,
			{
				for(Schedule s : userSchedule)
				{
					groupSchedule.add(s);
					arrangeList(groupSchedule);
				}
			}
			/////Merge 완료//////
			//기존의 스케줄 정보를 모두 삭제
			query = "delete from groupschedule1202 where Gid="+Gid;
			st.executeUpdate(query);
			//새롭게 정보를 입력
			for(Schedule ls : groupSchedule)
			{
				query = "insert into groupschedule1202(Gid,Pcount,sDate,eDate) values("+Gid+","+(Pcount+1)+","+ls.sDate+",+"+ls.eDate+") ";
				st.executeUpdate(query);
			}
			result = true;
			
		}catch(Exception ex){
			System.out.println("[DBConnector] MergeSchedule error " + ex);
		}
		
		return result;
	}
	/*리스트 정리*/
	public void arrangeList(ArrayList<Schedule> groupList)
	{
		//1. UeDate < GsDate || UsDate > GeDate (범위 밖) -> 상관없음
		//2. UsDate < GsDate && (UeDate > GsDate && UeDate < GeDate) (아래 걸침)
		//3. UsDate < GsDate && UeDate > GeDate (포함)
		//4. (UsDate > GsDate && UsDate < GeDate) && UeDate > GeDate (위 걸침)
		try{	
				ArrayList<Integer> removeList = new ArrayList<Integer>();
				Schedule user = groupList.get(groupList.size()-1);//제일 최근에 들어온 것 한개
	
				for(int i=0; i<groupList.size()-1; i++)
				{
					//날짜가 기존의 그룹정보와 중복되지 않는다면,
					if(!user.sDate.substring(0,8).equals(groupList.get(i).sDate.substring(0,8))) continue;
					
					if((user.eHMinute < groupList.get(i).eHMinute) && 
					(groupList.get(i).sHMinute<=user.eHMinute && user.sHMinute < groupList.get(i).sHMinute))
					{//user의 endDate 값을 바꾼다.
						user.eDate = groupList.get(i).eDate;//아래로 늘림
						user.splitScheduleDate();
						//그룹리스트의 해당 인덱스는 삭제 목록에 입력
						removeList.add(i);
						System.out.println("그룹이 유저 아래에 붙음");
					}
					else if((user.sHMinute <= groupList.get(i).sHMinute) && (user.eHMinute >= groupList.get(i).eHMinute))
					{//사용자 정보 내부에 그룹정보가 포함
						removeList.add(i);
						System.out.println("유저가 그룹을 포함함");
					}
					else if(groupList.get(i).sHMinute < user.sHMinute &&
					(user.sHMinute<=groupList.get(i).eHMinute && groupList.get(i).eHMinute<user.eHMinute))
					{//사용자 정보 위에 그룹정보가 걸침
						user.sDate = groupList.get(i).sDate;//위로 늘림
						user.splitScheduleDate();
						removeList.add(i);
						System.out.println("그룹이 유저 위에 붙음");
					}
					else if(groupList.get(i).sHMinute<=user.sHMinute && groupList.get(i).eHMinute >= user.eHMinute)//사용자 정보가 특정 그룹정보에 포함됨
					{
						System.out.println("그룹이 유저를 포함함");
						//사용자 자신을 제거
						removeList.add(groupList.size()-1);
						break;
					}
				}
				
				if(!removeList.isEmpty())//삭제해야할 목록이 있다면,
				{
					for(int i=removeList.size()-1; i>=0; i--)
					{
						groupList.remove((int)removeList.get(i));
					}
				}
		}catch(Exception ex){
			System.out.println("arrange error " + ex);
		}
		
	}
	/*사용자의 모든 메시지를 얻음*/
	public Message[] getAllMessages()
	{
		Message[] message = null;
		int index = 0;
		try{
			query = "select Gid,leader,Content,Type, Decision, Time from message1202 where Receiver='"+this.Id+"'";
			rs = st.executeQuery(query);
			rs.last();
			message = new Message[rs.getRow()];
			
			rs.beforeFirst();
			
			while(rs.next()) message[index++] = new Message(Integer.parseInt(rs.getString("Gid")),rs.getString("Leader"),rs.getString("Content"),rs.getString("Type"),rs.getString("Decision"),rs.getString("Time")); 
			
		}catch(Exception ex){
			System.out.println("[DBConnector] getAllMessages error " + ex);
		}
		display.append("메시지 갯수 : "+message.length+"\n");
		return message;
	}
	/*사용자가 소속된 모든 그룹 정보를 얻음*/
	public Group[] getAllGroupInfo()
	{
		Group[] group = null;
		String[] groupNum = null;
		try{
			
			query = "select Groups from userinfo1202 where Id='"+this.Id+"'";
			rs = st.executeQuery(query); rs.next();
			groupNum = rs.getString("Groups").split("/");
			
			group = new Group[groupNum.length];
			
			for(int i=0; i<groupNum.length; i++)
			{
				query = "select * from findtime1202 where Gid="+groupNum[i];
				rs = st.executeQuery(query); rs.next();
				group[i] = new Group(Integer.parseInt(rs.getString("Gid")),rs.getString("Leader"),rs.getString("Gname"),rs.getString("People"),Integer.parseInt(rs.getString("Pcount")),rs.getString("Content"),rs.getString("sDate"),rs.getString("eDate"),Integer.parseInt(rs.getString("aTime")),rs.getString("DATE"));
			}
			
		}catch(Exception ex){
			System.out.println("[DBConnector] getAllGroupInfo error " + ex);
		}
		
		return group;
	}
	
	
}
