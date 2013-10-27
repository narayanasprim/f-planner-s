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
			 
			 //����Ʈ���� ��ȣ�� ��� �ش� ��ȣ�� �̸��� ��´�.
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
			 names = names.substring(0,names.length()-1);//������ , �� ����
			
			//findtime ���̺� ������ �����.
			 try{
			 query = "insert into findtime1202(Leader,Gname,People,Pcount,Content,sDate,eDate,aTime,DATE) " +
					"values('"+this.Id+"','"+Gname+"','"+names+"',"+(idList.size()+1)+",'"+content+"','"+sDate+"','"+eDate+"','"+aTime+"',now())";
			 st.executeUpdate(query);
			 }catch(Exception ex){System.out.println("[DBConnector] requestGroupTime->insert findtime");}
			
			//���� �ֱٿ� ������ �׷��� Gid�� �����´�.
			try{
				query = "select Gid from findtime1202 where Leader = '"+this.Id+"' order by DATE desc limit 1";
				rs = st.executeQuery(query);
				rs.next();
				Gid = rs.getString("Gid");
			}catch(Exception ex){System.out.println("[DBConnector] requestGroupTime->get Gid");}
			
			 //�ڽ��� �׷� �÷� ������ ������Ʈ �Ѵ�.
			 query = "update userinfo1202 set Groups=concat(Groups,'"+Gid+"/') where Id='"+this.Id+"'";
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
				query = "insert into message1202(Receiver,Gid,Leader,Content,Type,Time) values('"+idList.get(i)+"',"+Gid+",'"+this.Id+"','"+content+"','"+type+"',now())";
				st.executeUpdate(query);
			}
			
			result = true;
			
		 }catch(Exception ex){
			 System.out.println("[DBConnector] requestGroupTime error " + ex);
		 }
	
		return result;
	
	}
	/*����� �ǰ� ����*/
	public boolean sendOpinion(String Gid, String opinion)
	{
		boolean result = false;
		
		try{
			if(opinion.equals("ACCEPT"))
			{
				query = "update userinfo1202 set Groups=concat(Groups,'"+Gid+"/') where Id='"+this.Id+"'";
				st.executeUpdate(query);
				//���⼭ GroupSchedule �� Merge �� ����Ǿ�� �Ѵ�.
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
				Schedule user = groupList.get(groupList.size()-1);//���� �ֱٿ� ���� �� �Ѱ�
	
				for(int i=0; i<groupList.size()-1; i++)
				{
					//��¥�� ������ �׷������� �ߺ����� �ʴ´ٸ�,
					if(!user.sDate.substring(0,8).equals(groupList.get(i).sDate.substring(0,8))) continue;
					
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
			query = "select Gid,leader,Content,Type, Decision, Time from message1202 where Receiver='"+this.Id+"'";
			rs = st.executeQuery(query);
			rs.last();
			message = new Message[rs.getRow()];
			
			rs.beforeFirst();
			
			while(rs.next()) message[index++] = new Message(Integer.parseInt(rs.getString("Gid")),rs.getString("Leader"),rs.getString("Content"),rs.getString("Type"),rs.getString("Decision"),rs.getString("Time")); 
			
		}catch(Exception ex){
			System.out.println("[DBConnector] getAllMessages error " + ex);
		}
		display.append("�޽��� ���� : "+message.length+"\n");
		return message;
	}
	/*����ڰ� �Ҽӵ� ��� �׷� ������ ����*/
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
