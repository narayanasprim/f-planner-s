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
	PreparedStatement pst = null;//���� �޽��� ���ۿ�
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
	/*DB ����*/
	public boolean DBConnection(Connection c)//���� ������ true ���н� false�� ����
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
	/*DB ���� ����*/
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
	/*ID, PW Ȯ��*/
	public String Login(String id, String passwd)//������ SUCCESS��� ���ڿ���, ���н� ���� ������ ����
	{
		String result = "SUCCESS";
	
		if(!isSet(id) || !isSet(passwd))	result = "FAIL";
		else
		{
			try{
			query = "select Id,Pw from userinfo1202 where Id='"+id+"'";
			rs = st.executeQuery(query);
			if(!rs.first()) return "JOIN";
			//ù��° �ʵ� : ���̵� �ι�° �ʵ� : ��й�ȣ
			//��
			if(!rs.getString("Pw").equals(passwd)) result = "DISMATCH";				
			else//���� ����
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
	/*ȸ�����Խ�, �ߺ��� ���̵� �ִ��� check*/
	public boolean checkID(String id)//ID �ߺ��� true ��ȯ
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
	/*Log*/
	public void Log(String error)
	{
		display.append(error+"\n");
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
	/*�Ϸ� ������ ������ ����*/
	public Schedule[] getDaySchedule(String YMD)
	{
		Schedule[] sch = null;
		int index = 0;
		try{
			
			query = "select Wnum,Id,Title,Content,sDate,eDate,if(Day IS NULL,'����',Day) as Day,if(Place IS NULL,'����',Place) as Place,Replay,Priority from Schedule1202 where Id='"+this.Id+"' and sDate>="+YMD+"0000 and sDate<"+YMD+"2500 order by sDate";
			rs = st.executeQuery(query);
			if(rs.last())//���� ���ٸ�
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
	/*�ð�ǥ ���� ��ü�� ��� schedule ��ü�� ����*/
	public Schedule[] getAllSchedules()
	{
		int index = 0;
		
		try{
			query = "select Wnum, Title,sDate,eDate,if(Day is null,'���Ͼ���',Day) as Day,if(Place is null,'��Ҿ���',Place) as Place,Content,Replay,Priority from schedule1202 where Id='"+this.Id+"' order by sDate";
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
	/*�׷� ���� �� �޽����� �뺸*/
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
			 
			 //����Ʈ���� ��ȣ�� ��� �ش� ��ȣ�� ���̵� ��´�.
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
			 names = names.substring(0,names.length()-1);//������ , �� ����
			
			//findtime ���̺� ������ �����.
			 try{
			 query = "insert into findtime1202(Leader,Gname,People,Pcount,Content,sDate,eDate,aTime,DATE) " +
					"values('"+this.Id+"','"+title+"','"+names+"',"+(idList.size()+1)+",'"+content+"','"+sDate+"','"+eDate+"','"+aTime+"',now())";
			 st.executeUpdate(query);
			 }catch(Exception ex){System.out.println("[DBConnector] requestGroupTime->insert findtime");}
			
			//���� �ֱٿ� ������ �׷��� Gid�� �����´�.
			try{
				query = "select LAST_INSERT_ID() from findtime1202 where Leader = '"+this.Id+"'";
				rs = st.executeQuery(query); rs.first();
				Gid = rs.getString(1);
			}catch(Exception ex){System.out.println("[DBConnector] requestGroupTime->get Gid");}
			
			 //�ڽ��� �׷� �÷� ������ ������Ʈ �Ѵ�.
			 query = "insert into group1202 values('"+this.Id+"',"+Gid+",'REQUEST','ACCEPT',now())";
			 st.executeUpdate(query);
			
			//groupSchedule�� �����.
			try{
				query = "insert into groupschedule1202(Gid,Pcount,sDate,eDate) values("+Gid+",0,0,0)";
				st.executeUpdate(query);
				MergeSchedule(Gid);
			}catch(Exception ex){System.out.println("[DBConnector] requestGroupTime->insert groupschedule");}
			
			//�ش� Gid�� �ٸ� ����ڵ鿡�� �޽����� ������.
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
	/*����� �ǰ� ����*/
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
	/*�������� ��ħ(�׷� �������� ��)*/
	public boolean MergeSchedule(String Gid)
	{
		boolean result = false;
		int Pcount = 0;
		int index = 0;
		try{
					
			//�ڽ��� ������ ������ ������
			query = "select sc.sDate, sc.eDate from schedule1202 sc, findtime1202 ft  where sc.Id='"+this.Id+"' and ft.Gid="+Gid+" and sc.eDate>=ft.sDate and sc.sDate<=ft.eDate order by sDate";
			rs = st.executeQuery(query);
			
			rs.last(); 
			Schedule[] userSchedule = new Schedule[rs.getRow()];
			rs.beforeFirst();
			while(rs.next()) userSchedule[index++] = new Schedule(rs.getString("sDate"),rs.getString("eDate"));
			
			
			//���� �׷��� ������ ������ ������
			ArrayList<Schedule> groupSchedule= new ArrayList<Schedule>();
			query = "select Pcount, sDate, eDate from groupschedule1202 where Gid="+Gid+" order by sDate asc";
			rs = st.executeQuery(query); 
			while(rs.next()) groupSchedule.add(new Schedule(rs.getString("sDate"),rs.getString("eDate")));
			
			rs.first(); Pcount = Integer.parseInt(rs.getString("Pcount"));
			
			
			if(groupSchedule.get(0).sDate.equals("0"))
			{//��ϵ� �׷� �������� ���ٸ�,
				groupSchedule.clear();
				
				for(Schedule s : userSchedule)
				groupSchedule.add(s);
			}
			else//������ �׷� �������� �����Ѵٸ�,
			{
				for(Schedule s : userSchedule)
				{
					groupSchedule.add(s);
					arrangeList(groupSchedule);
				}
			}
			/////Merge �Ϸ�//////
			//������ ������ ������ ��� ����
			query = "delete from groupschedule1202 where Gid="+Gid;
			st.executeUpdate(query);
			//���Ӱ� ������ �Է�
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
	/*����Ʈ ����*/
	public void arrangeList(ArrayList<Schedule> groupList)
	{
		//1. UeDate < GsDate || UsDate > GeDate (���� ��) -> �������
		//2. UsDate < GsDate && (UeDate > GsDate && UeDate < GeDate) (�Ʒ� ��ħ)
		//3. UsDate < GsDate && UeDate > GeDate (����)
		//4. (UsDate > GsDate && UsDate < GeDate) && UeDate > GeDate (�� ��ħ)
		try{	
				ArrayList<Integer> removeList = new ArrayList<Integer>();
				Schedule user = groupList.get(groupList.size()-1);//���� ����Ʈ���� ��������.
	
				for(int i=0; i<groupList.size()-1; i++)
				{
					//��¥�� ������ �׷������� �ߺ����� �ʴ´ٸ�,
					if(!user.sDate.substring(0,8).equals(groupList.get(i).sDate.substring(0,8))) continue;
//					if(user.eHMinute<groupList.get(i).sHMinute) break;///sorting �Ǿ�������, ����
					
					if((user.eHMinute < groupList.get(i).eHMinute) && 
					(groupList.get(i).sHMinute<=user.eHMinute && user.sHMinute < groupList.get(i).sHMinute))
					{//user�� endDate ���� �ٲ۴�.
						user.eDate = groupList.get(i).eDate;//�Ʒ��� �ø�
						user.splitScheduleDate();
						//�׷츮��Ʈ�� �ش� �ε����� ���� ��Ͽ� �Է�
						removeList.add(i);
						System.out.println("�׷��� ���� �Ʒ��� ����");
					}
					else if((user.sHMinute <= groupList.get(i).sHMinute) && (user.eHMinute >= groupList.get(i).eHMinute))
					{//����� ���� ���ο� �׷������� ����
						removeList.add(i);
						System.out.println("������ �׷��� ������");
					}
					else if(groupList.get(i).sHMinute < user.sHMinute &&
					(user.sHMinute<=groupList.get(i).eHMinute && groupList.get(i).eHMinute<user.eHMinute))
					{//����� ���� ���� �׷������� ��ħ
						user.sDate = groupList.get(i).sDate;//���� �ø�
						user.splitScheduleDate();
						removeList.add(i);
						System.out.println("�׷��� ���� ���� ����");
					}
					else if(groupList.get(i).sHMinute<=user.sHMinute && groupList.get(i).eHMinute >= user.eHMinute)//����� ������ Ư�� �׷������� ���Ե�
					{
						System.out.println("�׷��� ������ ������");
						//����� �ڽ��� ����
						removeList.add(groupList.size()-1);
						break;
					}
				}
				
				if(!removeList.isEmpty())//�����ؾ��� ����� �ִٸ�,
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
	/*������� ��� �޽����� ����*/
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
	/*����ڰ� �Ҽӵ� ��� �׷� ������ ����*/
	public Group[] getAllGroupInfo()
	{
		Group[] group = null;
		String[] gid = null;
		int i=0;

		try{
			Log(this.Id+" �� �׷������� ��û");
			query = "select Gid from group1202 where Id='"+this.Id+"'";
			
			rs = st.executeQuery(query); 
			
			if(!rs.next()) return group;
			else rs.last();
						
			{//����ڰ� ���� �ִ� �׷��ȣ�� �迭�� ����. ���ʴ�� �׷������� ��� Group ��ü�� ����
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
	/*�Ϲ� �޽��� ������ �޼���*/
	public boolean sendMessage(mPacket packet)
	{
		boolean result = false;
		ArrayList<String> phones = packet.getList();
		String title = packet.getTitle();
		String content  = packet.getContent();
		ArrayList<String> idList = new ArrayList<String>();
		Log("���� : " + title + " ���� : " + content);
		
		int i = 0;
		
		try{
			//�ڵ�����ȣ�� ������� ������� ���̵� ���� ���θ� �Ǵ�. �ش�Ǵ� ��� ����Ʈ�� �����Ѵ�.
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
			Log(idList.size()+"������");
			
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
	/*�޽����� �����ϴ� �޼���*/
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
	/*�ֱ� �޽����� ���� �ð��� ��ȯ�ϴ� �޼���*/
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
	/*�׷���� �̸��� ������ ����*/
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
	/*�׷���� �ǰ��� ����*/
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
	/*�׷� �����ڰ� �׷��� ���*/
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
	/*�ش� �׷��� ������ġ�� ��Ҹ��� ����*/
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
	/*����ڰ� �ǽð����� ��ġ�� ������ ���� ���θ� ����*/
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
	/*�ش� �׷� �׷������ ��ġ ������ ����*/
	public Message[] getGroupPeopleLocation(String Gid)
	{
		Message[] messages = null;
		int index = 0;
		try{
			
			//�׷��ȣ Gid�� ���ϴ� ����� ��, ������ ������� ��ġ ������ ���� 
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
	/*���� ��ġ�� ����*/
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
	/*�� �ð��� ���*/
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
			
			//�׷� ������ ���´�.
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
			//�ش� Gid �� ������� Group ���̺��� ������ ����� ���̵� ���,
			//�ش���̵� ������� �˻����� �� �̿밡�� �ð��� �´� �ð�ǥ���� ��´�.
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
								if((end - start)>=findAtime)//�� ������ �˻������� �����ϸ�,
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
			
			Log("FindFreeTime ��� ��� ���� : "+resultList.size());
			if(resultList.size()==0) return null;
			
			result = new Schedule[resultList.size()];
			resultList.toArray(result);
			
		}catch(Exception ex){
			Log("[DBConnector] findFreeTime error "+ex);
			ex.printStackTrace();
		}
		
		
		return result;
	}
	/*�׷� �������� �����*/
	public boolean addGroupSchedule(Schedule s)
	{
		boolean result = false;
		try{//wNum �� Gid �� �����ؼ� �ѱ��. , title�� Xǥ, content �� Y��ǥ�� �����ؿ´�.
			query  = "Insert into groupschedule1202 values('"+s.wNum+"','"+s.sDate+"','"+s.eDate+"','"+s.Place+"',Point("+s.Title+","+s.Content+"))";
			st.executeUpdate(query);
			
			query = "update findtime1202 set Register='TRUE' where Gid="+s.wNum;
			if(st.executeUpdate(query)>0) result = true;
			
		}catch(Exception ex){
			System.out.println();
		}
		
		return result;
	}
	/*�ڽ��� ���� ��� �׷� ������ ������ ����*/
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
	/*�������� ������� ���� ���� �������� ����*/
	public Schedule[] getDayGroupSchedule(String YMD)
	{
		Schedule[] sch = null;
		int index = 0;
		try{
			query = "select g.Gid, g.sDate, g.eDate, g.Place, f.Gname, f.content where f.Gid=g.Gid and f.sDate>="+YMD+"0000 and f.sDate<"+YMD+"2500";
			rs = st.executeQuery(query);
			if(rs.last())//���� ���ٸ�
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
