package com.f_planner_app;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import javax.swing.JTextArea;

public class DBConnector {
	
	Connection con = null;
	Statement st = null;
	PreparedStatement pst = null;//통지 메시지 전송용
	ResultSet rs = null;
	String query = null;
	String Id = null;
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
			pst = con.prepareStatement("insert into message1202(Receiver,Title,Leader,Content,Time) values(?,?,?,?,now())");
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
			this.pst.close();
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
	
		if(!isSet(id) || !isSet(passwd))	result = "FAIL";
		else
		{
			try{
			query = "select Id,Pw from userinfo1202 where Id='"+id+"'";
			rs = st.executeQuery(query);
			if(!rs.first()) return "JOIN";
			//첫번째 필드 : 아이디 두번째 필드 : 비밀번호
			//비교
			if(!rs.getString("Pw").equals(passwd)) result = "DISMATCH";				
			else//접속 성공
			{
				this.Id = id;
				this.isLogin = true;
			}

			}catch(SQLException e){
				Log("[DBConnector] Login error "+e);
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
			query = "select Id from userinfo1202 where Id = '"+id+"'";
			rs = st.executeQuery(query);
			rs.first();
			
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
	/*Log*/
	public void Log(String error)
	{
		display.append(error+"\n");
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
			
			query = "select if(max(Wnum) IS null, 0, max(Wnum)) as Wnum from schedule1202 where Id='"+this.Id+"'";
			rs = st.executeQuery(query);
			
			if(!rs.first()) Wnum = 1;
			else
			Wnum = Integer.parseInt(rs.getString("Wnum"))+1;
			Log("insert into schedule1202(Id,Wnum,Title,Content,sDate,eDate) values('"+this.Id+"',"+sc.wNum+",'"+sc.Title+"','"+sc.Content+"','"+sc.sDate+"','"+sc.eDate+"')");
			query = "insert into schedule1202 values('"+this.Id+"',"+Wnum+",'"+sc.Title+"','"+sc.sDate+"','"+sc.eDate+"','"+sc.Day+"','"+sc.Place+"','"+sc.Content+"','"+sc.Replay+"','"+sc.Priority+"')";
			//query = "insert into schedule1202(Id,Wnum,Title,Content,sDate,eDate) values('"+this.Id+"',"+sc.wNum+",'"+sc.Title+"','"+sc.Content+"','"+sc.sDate+"','"+sc.eDate+"')";
		
			
			if(st.executeUpdate(query)>0) result = true;
			
		}catch(SQLException e){
			Log("[DBConnector] addSchedule error " + e);
			result = false;
		}
		
		return result;
	}
	/*하루 스케줄 정보를 얻음*/
	public Schedule[] getDaySchedule(String YMD)
	{
		Schedule[] sch = null;
		int index = 0;
		try{
			
			query = "select Wnum,Id,Title,Content,sDate,eDate,if(Day IS NULL,'없음',Day) as Day,if(Place IS NULL,'없음',Place) as Place,Replay,Priority from Schedule1202 where Id='"+this.Id+"' and sDate>="+YMD+"0000 and sDate<"+YMD+"2500 order by sDate";
			rs = st.executeQuery(query);
			if(rs.last())//값이 없다면
			{
				sch = new Schedule[rs.getRow()];
				rs.beforeFirst();
				while(rs.next())
				{
					sch[index] = new Schedule(rs.getString("Title"),rs.getString("sDate"),rs.getString("eDate"),rs.getString("Day"),rs.getString("Place"),rs.getString("Content"),rs.getString("Replay").charAt(0),rs.getString("Priority").charAt(0));
					sch[index++].wNum = Integer.parseInt(rs.getString("Wnum"));
				}
			}
			
		}catch(Exception ex){
			Log("DBConnector error " + ex);
		}
		
		return sch;
	}
	/*시간표 정보 전체를 얻어 schedule 객체에 저장*/
	public Schedule[] getAllSchedules()
	{
		int index = 0;
		
		try{
			query = "select Wnum, Title,sDate,eDate,if(Day is null,'요일없음',Day) as Day,if(Place is null,'장소없음',Place) as Place,Content,Replay,Priority from schedule1202 where Id='"+this.Id+"' order by sDate";
			rs = st.executeQuery(query);
			rs.last();
			schedules = new Schedule[rs.getRow()];
			rs.beforeFirst();
			while(rs.next())
			{
				schedules[index] = new Schedule(rs.getString("Title"),rs.getString("sDate"),rs.getString("eDate"),rs.getString("Day"),rs.getString("Place"),rs.getString("Content"),rs.getString("Replay").charAt(0),rs.getString("Priority").charAt(0));
				schedules[index++].wNum = Integer.parseInt(rs.getString("Wnum"));
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
		ArrayList<String> phones = packet.getList();
		 String title = packet.getTitle();
		 String content = packet.getContent();
		 String sDate = packet.getSdate();
		 String eDate = packet.getEdate();
		 String aTime = packet.getAtime();
		 boolean result = false;
		 String names = "",Gid = "";
		 ArrayList<String> idList = new ArrayList<String>();
		 String phone;
		 try{
			 
			 //리스트에서 번호를 얻어 해당 번호의 아이디를 얻는다.
			 for(int i=0; i<phones.size(); i++)
			 {
				 try{
			     phone = phones.get(i).replaceAll("-","");
				 query = "select Id,Name from userinfo1202 where Phone='"+phone+"'";
				 rs = st.executeQuery(query); rs.next();
				 names += rs.getString("Name")+",";
				 idList.add(rs.getString("Id"));
				 }catch(Exception ex){}
			 }
			 names = names.substring(0,names.length()-1);//마지막 , 를 지움
			
			//findtime 테이블에 정보를 만든다.
			 try{
			 query = "insert into findtime1202(Leader,Gname,People,Pcount,Content,sDate,eDate,aTime,DATE) " +
					"values('"+this.Id+"','"+title+"','"+names+"',"+(idList.size()+1)+",'"+content+"','"+sDate+"','"+eDate+"','"+aTime+"',now())";
			 st.executeUpdate(query);
			 }catch(Exception ex){System.out.println("[DBConnector] requestGroupTime->insert findtime");}
			
			//가장 최근에 생성한 그룹의 Gid를 가져온다.
			try{
				query = "select LAST_INSERT_ID() from findtime1202 where Leader = '"+this.Id+"'";
				rs = st.executeQuery(query); rs.first();
				Gid = rs.getString(1);
			}catch(Exception ex){System.out.println("[DBConnector] requestGroupTime->get Gid");}
			
			 //자신의 그룹 컬럼 정보를 업데이트 한다.
			 query = "insert into group1202 values('"+this.Id+"',"+Gid+",'REQUEST','ACCEPT',now())";
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
				query = "insert into message1202(Receiver,Gid,Title,Leader,Content,Time) values('"+idList.get(i)+"',"+Gid+",'"+title+"','"+this.Id+"','"+content+"',now())";
				st.executeUpdate(query);
				query = "insert into group1202(Id,Gid,Type,Time) values('"+idList.get(i)+"',"+Gid+",'REQUEST',now())";
				st.executeUpdate(query);
			}
			
			result = true;
			
		 }catch(Exception ex){
			 System.out.println("[DBConnector] requestGroupTime error " + ex);
		 }
	
		return result;
	
	}
	/*사용자 의견 적용*/
	public boolean sendOpinion(Message m)
	{
		boolean result = false;
		
		try{
			
			if(m.decision.equals(Message.ACCEPT))
			{
				Log(this.Id + " : " + m.Gid + " -> update ");
			}
			
			query = "update group1202 set Decision='"+m.decision+"' where Id='"+this.Id+"' and Gid="+m.Gid;
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
				Schedule user = groupList.get(groupList.size()-1);//현재 리스트에서 마지막것.
	
				for(int i=0; i<groupList.size()-1; i++)
				{
					//날짜가 기존의 그룹정보와 중복되지 않는다면,
					if(!user.sDate.substring(0,8).equals(groupList.get(i).sDate.substring(0,8))) continue;
//					if(user.eHMinute<groupList.get(i).sHMinute) break;///sorting 되어있을때, 가능
					
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
			query = "select m.Unum , m.title,m.leader, m.content, m.gid, m.time ,if(m.gid IS NULL,'NOTIFY',g.type) as Type , if(m.gid IS NULL,'NOT_DECISION',g.Decision) as Decision from message1202 m , group1202 g where m.receiver='"+this.Id+"' and m.receiver = g.Id and (m.gid=g.gid or m.gid is null) group by m.Unum order by m.time desc";
			rs = st.executeQuery(query);
			
			if(!rs.next()) return message;
			else rs.last();
			
			message = new Message[rs.getRow()];
			
			rs.beforeFirst();
			
			while(rs.next()) message[index++] = new Message(rs.getString("Unum"),rs.getString("Gid"),rs.getString("Title"),rs.getString("Leader"),rs.getString("Content"),rs.getString("Type"),rs.getString("Decision"),rs.getString("Time")); 
			
		}catch(Exception ex){
			System.out.println("[DBConnector] getAllMessages error " + ex);
		}
		return message;
	}
	/*사용자가 소속된 모든 그룹 정보를 얻음*/
	public Group[] getAllGroupInfo()
	{
		Group[] group = null;
		String[] gid = null;
		int i=0;

		try{
			Log(this.Id+" 가 그룹정보를 요청");
			query = "select Gid from group1202 where Id='"+this.Id+"'";
			
			rs = st.executeQuery(query); 
			
			if(!rs.next()) return group;
			else rs.last();
						
			{//사용자가 속해 있는 그룹번호를 배열에 저장. 차례대로 그룹정보를 얻어 Group 객체를 생성
				gid = new String[rs.getRow()];
				group = new Group[gid.length];
				
				rs.beforeFirst();
				while(rs.next()) gid[i++] = rs.getString("Gid");
				
				for( i=0; i<gid.length; i++)
				{
					query = "select f.Gid,u.name,f.leader,f.Gname,f.People,f.Pcount,f.Content,f.sDate,f.eDate,f.aTime,f.Register,f.DATE,g.Decision from findtime1202 f, group1202 g , userinfo1202 u where f.Gid="+gid[i]+" and f.Gid=g.Gid and g.Id='"+this.Id+"' and f.Leader=u.Id";
					rs = st.executeQuery(query); rs.first();
					group[i] = new Group(Integer.parseInt(rs.getString("Gid")),
														  (rs.getString("name")+"/"+rs.getString("leader")),
														  rs.getString("Gname"),
														  rs.getString("People"),
														  Integer.parseInt(rs.getString("Pcount")),
														  rs.getString("Content"),
														  rs.getString("sDate"),
														  rs.getString("eDate"),
														  Integer.parseInt(rs.getString("aTime")),
														  rs.getString("Register"),
														  rs.getString("DATE"));
					group[i].Decision = rs.getString("Decision");
				}
			}
			
		}catch(Exception ex){
			System.out.println("[DBConnector] getAllGroupInfo error " + ex);
		}
		
		return group;
	}
	/*일반 메시지 보내는 메서드*/
	public boolean sendMessage(mPacket packet)
	{
		boolean result = false;
		ArrayList<String> phones = packet.getList();
		String title = packet.getTitle();
		String content  = packet.getContent();
		ArrayList<String> idList = new ArrayList<String>();
		Log("제목 : " + title + " 내용 : " + content);
		
		int i = 0;
		
		try{
			//핸드폰번호를 기반으로 사용자의 아이디 존재 여부를 판단. 해당되는 경우 리스트에 저장한다.
			for(i=0; i<phones.size(); i++)
			{
				try{
					String tmpPhoneNum = phones.get(i).replaceAll("-","");
					
					query = "SELECT Id from userinfo1202 where Phone ='"+tmpPhoneNum+"'";
					rs = st.executeQuery(query); rs.first();
					idList.add(rs.getString("Id"));
					
				}catch(Exception e){
					Log(""+e);
				}
			}
			Log(idList.size()+"사이즈");
			
			for(i=0; i<idList.size(); i++)
			{
				try{
					pst.setString(1, idList.get(i));
					pst.setString(2, title);
					pst.setString(3, this.Id);
					pst.setString(4, content);
					pst.executeUpdate();
				}catch(Exception ex){
					Log("pst error" + ex);
				}
			}
			
			result = true;
		}catch(Exception ex){
			Log("[DBConnector] sendMessage error " + ex);
		}
		
		return result;
	}
	/*메시지를 삭제하는 메서드*/
	public boolean deleteMessage(ArrayList<String> deleteList)
	{
		boolean result = false;
		
		try{
			
			for(int i=0; i<deleteList.size(); i++)
			{
				query = "delete from message1202 where Unum="+deleteList.get(i);
				st.executeUpdate(query);
			}
			
			result = true;
			
		}catch(Exception ex){
			System.out.println("[DBConnector] deleteMessage error " + ex);
		}
		
		return result;
	}
	/*최근 메시지를 받은 시간을 반환하는 메서드*/
	public String getRecentMessageDate()
	{
		String result = "NULL";
		
		try{
			
			query = "select Time from message1202 where Receiver='"+this.Id+"' order by Time desc";
			rs = st.executeQuery(query);
			if(!rs.first()) 	return result;
			else				result = rs.getString("Time");
			
		}catch(Exception ex){
			System.out.println("[DBConnector] getRecentMessageDate error " + ex);
		}
		
		return result;
	}
	/*그룹원의 이름과 결정을 얻음*/
	public Message[] getGroupPeopleOpinion(int Gid)
	{
		Message[] m = null;
		int index = 0;
		
		try{
			
			query = "select u.Name, g.Decision from userinfo1202 u, group1202 g where g.Gid="+Gid+" and g.Id=u.Id";
			rs = st.executeQuery(query);
			rs.last();
			m = new Message[rs.getRow()];
			rs.beforeFirst();
			
			while(rs.next()) m[index++] = new Message(rs.getString("Name"),rs.getString("Decision"));
			
		}catch(Exception ex){
			System.out.println("[DBConnector] getGroupPeopleOpinion error " + ex);
		}
		
		return m;
	}
	/*그룹원의 의견을 조정*/
	public boolean SetGroupPeopleOpinion(String Gid, String opinion)
	{
		boolean result = false;
		try{
			
			query = "Update Group1202 set Decision='"+opinion+"' where Gid="+Gid;
			
			if(st.executeUpdate(query)>0) result = true;
			
		}catch(Exception ex )
		{Log("[DBConnector] SetGroupPeopleOpinion error " + ex);}
		
		return result;
	}
	/*그룹 주최자가 그룹을 취소*/
	public boolean CancelGroup(String Gid)
	{
		boolean result = false;
		try{
			
			query = "Delete from message1202 where Gid="+Gid;
			st.executeUpdate(query);
			query = "Delete from group1202 where Gid="+Gid;
			st.executeUpdate(query);
			query = "Delete from findtime1202 where Gid="+Gid;
			st.executeUpdate(query);
			
			result = true;
		}catch(Exception ex){
			Log("[DBConnector] CancelGroup error " + ex);
		}
		return result;
	}
	/*해당 그룹의 도착위치와 장소명을 얻음*/
	public Schedule getDestination(String Gid)
	{
		Schedule s = null;
		try{
			
			query = "select Place, X(dLocation) as x , Y(dLocation) as y from groupschedule1202 where Gid="+Gid;
			rs = st.executeQuery(query);
			
			if(rs.first())
			{
				s = new Schedule();
				s.Place = rs.getString("Place");
				s.sDate = rs.getString("x");
				s.eDate = rs.getString("y");
			}
			
		}catch(Exception ex){
			Log("[DBConnector] getDestination error " + ex);
		}
		
		return s;
	}
	/*사용자가 실시간으로 위치와 목적지 도착 여부를 보냄*/
	public boolean sendLocation(String X, String Y,String rTime ,String isArrive)
	{
		boolean result = false;
		
		try{
			query = "Update gps1202 set cLocation=Point("+X+","+Y+") , rTime='"+rTime+"', Arrive = '"+isArrive+"'";
			if(st.executeUpdate(query)>0) result = true;
		}catch(Exception ex){
			Log("[DBConnector] sendLocation error " + ex);
		}
		
		return result;
	}
	/*해당 그룹 그룹원들의 위치 정보를 얻음*/
	public Message[] getGroupPeopleLocation(String Gid)
	{
		Message[] messages = null;
		int index = 0;
		try{
			
			//그룹번호 Gid에 속하는 사람들 중, 동의한 사람들의 위치 정보를 얻음 
			query = "select u.name, X(gps.cLocation) as x, Y(gps.cLocation) as y, gps.rTime, gps.Arrive from userinfo1202 u, gps1202 gps, group1202 g where g.Gid="+Gid+" and g.Decision='ACCEPT' and g.Id=u.Id and u.Id=gps.Id";
			rs = st.executeQuery(query);
			if(rs.last()) messages = new Message[rs.getRow()];
			rs.beforeFirst();
			while(rs.next())
			{
				messages[index] = new Message();
				messages[index].name = rs.getString("Name");
				messages[index].title = rs.getString("x");
				messages[index].leader = rs.getString("y");
				messages[index].time = rs.getString("rTime");
				messages[index].decision = rs.getString("Arrive");
				index++;
			}
			
		}catch(Exception ex){
			Log("[DBConnector] getGroupPeopleLocation error " + ex);
		}
		
		return messages;
	}
	/*현재 위치를 설정*/
	public Message[] getGroupArriveTime()
	{
		Message[] message = null;
		int index = 0;
		try{
			
			query = "select gs.sDate, gs.gid, gps.Arrive from group1202 g, groupschedule1202 gs,gps1202 gps where g.Id='d' and g.Decision='ACCEPT' and g.gid=gs.gid and g.Id=gps.Id order by gs.sDate";
			rs = st.executeQuery(query);
			if(! rs.last()) return null;
			message = new Message[rs.getRow()];
			rs.beforeFirst();
			while(rs.next())
			{
				message[index] = new Message();
				message[index].Gid = rs.getString("Gid");
				message[index].time = rs.getString("sDate");
				message[index].decision = rs.getString("Arrive");
				index++;
			}
			
		}catch(Exception ex){
			Log("[DBConnector] getGroupArriveTime error " + ex);
		}
		
		return message;
	}
	/*빈 시간을 계산*/
	public Schedule[] findFreeTime(String Gid)
	{
		Schedule[] result = null;
		ArrayList<Schedule> resultList = new ArrayList<Schedule>();
		String findSdate="", findEdate="";
		int findAtime = -1,index=0;
		int dayPeriod = 0;
		int acceptPeople = 0;
		
		int[][] timeArray = null;
		
		try{
			
			//그룹 정보를 얻어온다.
			query = "Select sDate, eDate, aTime from findtime1202 where Gid="+Gid;
			Log("FindFreeTime 0 -> " + query);
			rs = st.executeQuery(query);
			if(rs.first())
			{
				findSdate = rs.getString("sDate");
				findEdate = rs.getString("eDate");
				findAtime = Integer.parseInt(rs.getString("aTime"));
				
				dayPeriod = betweenDay(findSdate,findEdate);
				timeArray = new int[dayPeriod+1][1440];
			}
			 
			query = "Select count(Id) as Available from group1202 where Decision='ACCEPT' and Gid="+Gid;
			Log("DayPeriod -> " + dayPeriod);
			rs = st.executeQuery(query); rs.first();
			acceptPeople = Integer.parseInt(rs.getString("Available"));
			//해당 Gid 를 기반으로 Group 테이블에서 동의한 사람의 아이디를 얻고,
			//해당아이디를 기반으로 검색범위 및 이용가능 시간에 맞는 시간표들을 얻는다.
			query = "select s.sDate , s.eDate from group1202 g , schedule1202 s, findtime1202 f where g.Gid="+Gid+" and g.Decision='ACCEPT' and g.Id=s.Id and g.Gid=f.Gid and s.eDate>f.sDate and s.sDate<f.eDate";
			rs = st.executeQuery(query);
			while(rs.next())
			{
				for(index=getMinute(rs.getString("sDate")); index<=getMinute(rs.getString("eDate")); index++)
				{ 
					timeArray[betweenDay(findSdate,rs.getString("sDate"))][index]+=1;
				}
			}
			int start,end;
			
			for(int p=0; p<acceptPeople; p++)
			{
				for(int i=0; i<dayPeriod+1; i++)
				{
					start = getMinute(findSdate);
					for(int j=getMinute(findSdate); j<=getMinute(findEdate); j++)
					{
						end = j;
							
						if(timeArray[i][end]>p || end>=getMinute(findEdate))
						{
								if((end - start)>=findAtime)//그 범위가 검색범위에 충족하면,
								{
									Schedule s =  new Schedule(mixDate(findSdate,i,start-getMinute(findSdate)),mixDate(findSdate,i,end-getMinute(findSdate)));
									s.wNum = acceptPeople - p;
									resultList.add(s);
									start = end;
								}
								else
									start = end;
						}
							else 
								start = end;
					}
				}
			}
			
			Log("FindFreeTime 계산 결과 갯수 : "+resultList.size());
			if(resultList.size()==0) return null;
			
			result = new Schedule[resultList.size()];
			resultList.toArray(result);
			
		}catch(Exception ex){
			Log("[DBConnector] findFreeTime error "+ex);
			ex.printStackTrace();
		}
		
		
		return result;
	}
	/*그룹 스케줄을 등록함*/
	public boolean addGroupSchedule(Schedule s)
	{
		boolean result = false;
		try{//wNum 에 Gid 를 저장해서 넘긴다. , title에 X표, content 에 Y좌표를 저장해온다.
			query  = "Insert into groupschedule1202 values('"+s.wNum+"','"+s.sDate+"','"+s.eDate+"','"+s.Place+"',Point("+s.Title+","+s.Content+"))";
			st.executeUpdate(query);
			
			query = "update findtime1202 set Register='TRUE' where Gid="+s.wNum;
			if(st.executeUpdate(query)>0) result = true;
			
		}catch(Exception ex){
			System.out.println();
		}
		
		return result;
	}
	/*자신이 속한 모든 그룹 스케줄 정보를 얻음*/
	public Schedule[] getAllGroupScheduleInfo()
	{
		Schedule[] schedule = null;
		Log(this.Id+"  ->    request own group schedule information ");
		
		try{
			int[] Gid = null;
			int index = 0;
			query = "select Gid from group1202 where Id='"+this.Id+"' and Decision='ACCEPT'";
			rs = st.executeQuery(query);
			if(rs.last())
			{
				Gid = new int[rs.getRow()];
				schedule = new Schedule[rs.getRow()];
			}
			rs.beforeFirst();
			
			while(rs.next()) Gid[index++]  =  Integer.parseInt(rs.getString("Gid"));
			
			for(int i=0; i<Gid.length; i++)
			{
				query = "Select g.Gid, g.sDate, g.eDate, g.Place, f.Gname , f.content from groupschedule1202 g, findtime1202 f where g.Gid="+Gid[i]+" and f.gid=g.gid";
				rs = st.executeQuery(query);
				schedule[i] = new Schedule();
				if(rs.first())
				{
					schedule[i].wNum = Integer.parseInt(rs.getString("Gid"));
					schedule[i].sDate = rs.getString("sDate");
					schedule[i].eDate = rs.getString("eDate");
					schedule[i].Place = rs.getString("Place");
					schedule[i].Title = rs.getString("Gname");
					schedule[i].Content = rs.getString("Content");
				}
				
			}
			
			
		}catch(Exception ex){
			Log("[DBConnector] getAllGroupScheduleInfo error  " + ex);
		}
		
		return schedule;
	}
	/*연월일을 기반으로 모임 일정 정보들을 얻음*/
	public Schedule[] getDayGroupSchedule(String YMD)
	{
		Schedule[] sch = null;
		int index = 0;
		try{
			query = "select g.Gid, g.sDate, g.eDate, g.Place, f.Gname, f.content where f.Gid=g.Gid and f.sDate>="+YMD+"0000 and f.sDate<"+YMD+"2500";
			rs = st.executeQuery(query);
			if(rs.last())//값이 없다면
			{
				sch = new Schedule[rs.getRow()];
				rs.beforeFirst();
				while(rs.next())
				{
					sch[index] = new Schedule(rs.getString("Gname"),rs.getString("sDate"),rs.getString("eDate"),"",rs.getString("Place"),rs.getString("Content"),' ',' ');
					sch[index++].wNum = Integer.parseInt(rs.getString("Gid"));
				}
			}
			
		}catch(Exception ex){
			Log("DBConnector error " + ex);
		}
		
		return sch;
	}
	

	public int getMinute(String date)
	{
		return Integer.parseInt(date.substring(8,10))*60+Integer.parseInt(date.substring(10));
	}
	
	public int betweenDay(String date1, String date2)
	{
		Calendar c1 = Calendar.getInstance(), c2 = Calendar.getInstance(); 
		Date d1, d2;

		c1.set(Integer.parseInt(date1.substring(0,4)),Integer.parseInt(date1.substring(4,6))-1,Integer.parseInt(date1.substring(6,8)));
		c2.set(Integer.parseInt(date2.substring(0,4)),Integer.parseInt(date2.substring(4,6))-1,Integer.parseInt(date2.substring(6,8)));
		
		d1 = c1.getTime(); d2 = c2.getTime();
		long between = d2.getTime() - d1.getTime();
		
		return (int)(between/86400000);
	
	}
	
	public String mixDate(String startDate, int plusDay, int plusMinute)
	{
		Calendar c = Calendar.getInstance();
		SimpleDateFormat form = new SimpleDateFormat("yyyyMMddhhmm",Locale.KOREA);
		Date date = new Date();
		try {
			date = form.parse(startDate);
			c.setTime(date);
			c.add(Calendar.DATE, plusDay);
			c.add(Calendar.MINUTE, plusMinute);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		return form.format(c.getTime());
	}
	
	

}
