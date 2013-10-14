import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class Calc {

	String url = "jdbc:mysql://127.0.0.1:3306/scheduler?useUnicode=yes&characterEncoding=utf8";
	String user = "root", pass = "secufact";
	Connection con;
	Statement st;
	ResultSet rs;
	int find_Gid,find_Pcount,find_aTime;
	String find_sDate,find_eDate,sTime,eTime;
	int sYear,sMonth,sDay, sHour, sMinute, eYear,eMonth,eDay,eHour, eMinute;//findtime
//	int group_Gid,group_Pcount, group_Wnum, group_sDate, group_eDate, group_Day;
	ScheduleDate userS, groupS;
	String query;
	ArrayList<ListScheduleDate> groupList = new ArrayList<ListScheduleDate>();
	
	public Calc()
	{
		try {

			con = DriverManager.getConnection(url, user, pass);
			st = con.createStatement();

				/*�̿밡�� �ð� ������ ����*/
				query = "select Gid,Pcount,sDate,eDate,aTime from findtime1202";
				rs = st.executeQuery(query);
				getFindtimeinfo(rs);
				
				ArrayList<String> ph = new ArrayList<String>();
				ph.add("01055954945");
				ph.add("01063360267");
				ph.add("01012341234");
				
				//������ ��ü
				mergeSchedule(ph);
				
				//�����ð� ��� �޼���
				//CalcAvailabletime();
				

		} catch (Exception ex) {
			System.out.println("[error]");
		}
	}

	class ScheduleDate 
	{
		String sDate[], eDate[];
		int sYear[],sMonth[],sDay[];
		int eYear[],eMonth[],eDay[];
		int sHour[],eHour[];
		int sMinute[],eMinute[];
		int sHMinute[], eHMinute[];
		
		int scheduleCount;//������ ����
		
		public ScheduleDate(int count)
		{
			sDate = new String[count];
			eDate = new String[count];
			sHour = new int[count];
			eHour = new int[count];
			sMinute = new int[count];
			eMinute = new int[count];
			sYear = new int[count];
			sMonth = new int[count];
			sDay = new int[count];
			eYear = new int[count];
			eMonth = new int[count];
			eDay = new int[count];
			sHMinute = new int[count];
			eHMinute = new int[count];
			
			this.scheduleCount = count;
		}
	}
	
	class ListScheduleDate
	{
		public String sDate,eDate;
		public int sYear,sMonth,sDay;
		public int sHour,sMinute;
		public int eYear,eMonth,eDay;
		public int eHour,eMinute;
		public int sHMinute, eHMinute;
		
		public ListScheduleDate()
		{
			//Empty
		}
		
		public ListScheduleDate(String sDate, String eDate)
		{
			this.sDate = sDate;
			this.eDate = eDate;
			if(sDate.equals("0") || eDate.equals("0"))
			zeroSet();
			else
			splitListDate();
		}
		
		public void zeroSet()
		{
			this.sYear = sMonth = sDay = sHour = sMinute = sHMinute = 0;
			this.eYear = eMonth = eDay = eHour = eMinute = eHMinute = 0;
		}
	
		public void splitListDate()
		{
			int[] splitSdate = splitDate(this.sDate);
			sYear = splitSdate[0];
			sMonth = splitSdate[1];
			sDay = splitSdate[2];
			sHour = splitSdate[3];
			sMinute = splitSdate[4];
			sHMinute = (splitSdate[3] * 60 + splitSdate[4]);
			
			int[] splitEdate = splitDate(this.eDate);
			eYear = splitEdate[0];
			eMonth = splitEdate[1];
			eDay = splitEdate[2];
			eHour = splitEdate[3];
			eMinute = splitEdate[4];
			eHMinute = (splitEdate[3] * 60 + splitEdate[4]);
			
		}
	}
	
	public void setScheduleDate(ResultSet r, ScheduleDate sch)
	{
		int count=0;
		try{
			
		while(r.next())
		{
			int[] splitSdate = splitDate(r.getString("sDate"));
			sch.sDate[count] = r.getString("sDate");
			sch.sYear[count] = splitSdate[0];//��
			sch.sMonth[count] = splitSdate[1];//��
			sch.sDay[count] = splitSdate[2];//��
			sch.sHour[count] = splitSdate[3];//��
			sch.sMinute[count] = splitSdate[4];//��
			sch.sHMinute[count] = (splitSdate[3] * 60 + splitSdate[4]);
			
			int[] splitEdate = splitDate(r.getString("eDate"));
			sch.eDate[count] = r.getString("eDate");
			sch.eYear[count] = splitEdate[0];
			sch.eMonth[count] = splitEdate[1];
			sch.eDay[count] = splitEdate[2];
			sch.eHour[count] = splitEdate[3];
			sch.eMinute[count] = splitEdate[4];
			sch.eHMinute[count] = (splitEdate[3] * 60 + splitEdate[4]);
			
			count++;
		}
		
		}catch(Exception ex){
			System.out.println("setScheduleDate error " + ex);
		}
	}
	
	public void setListScheduleDate(ResultSet r)
	{
		try{
			
			while(r.next())
			{
				ListScheduleDate ls = new ListScheduleDate(r.getString("sDate"),r.getString("eDate"));
				groupList.add(ls);
			}
			
		}catch(Exception ex){
			System.out.println("setListScheduleDate error " + ex );
		}
	}
	
	public void mergeSchedule(ArrayList<String> people)//���ڷ� ������ ������� �޴´�.
	{
		//people���� ������ ������� �ڵ��� ��ȣ�� �����Ǿ��ִ�.
		//��ȣ�� ������� �� ����� ������ ��¥�� ��´�.
		
		ScheduleDate[] userSchedule = new ScheduleDate[people.size()];
		try{
			
			for(int i=0; i<people.size(); i++)//��� �� ��ŭ �ݺ�
			{
				int index = 0;
				query = "select sDate,eDate from schedule1202 where Id=(select Id from userinfo1202 where Phone='"+people.get(i)+"') order by sDate asc";
				rs = st.executeQuery(query);
				while(rs.next()) index++;
				userSchedule[i] = new ScheduleDate(index);
				rs.beforeFirst();
				setScheduleDate(rs,userSchedule[i]);
			}//userSchdule �迭 �ϼ�.
			
			System.out.println("��� �� ������ ����");

			for(int i=0; i<people.size(); i++)
			{
				System.out.println(i+" " + userSchedule[i].scheduleCount);
			}
		}catch(Exception ex){
			System.out.println("merge error "+ ex);
		}
		
		try{
			//�׷콺���� ������ �о�´�.
			query = "select sDate,eDate from groupschedule1202 where Gid="+find_Gid+" order by sDate asc";
			rs = st.executeQuery(query);
			//�׷츮��Ʈ(groupList) �ϼ�
			setListScheduleDate(rs);
			
			//���� ���� �о� 0���� Ȯ��
			if(groupList.get(0).sDate.equals("0"))
			{
				groupList.clear();
				//ù��° ��� ������ �Ϲ� ����
				insertSchedule(userSchedule[0]);
				
				//���� ����
				for(int i=1; i<userSchedule.length; i++)
				Merge(groupList,userSchedule[i]);
			}
			else
			{
				//�� ���� ����
				for(int i=0; i<userSchedule.length; i++)
				Merge(groupList,userSchedule[i]);
			}
			
			groupListQuery();//����� ������ ����
			
		}catch(Exception ex){
			System.out.println("get groupschedule error " +ex );
		}
	}
	/*���� ����� ������ ����*/
	public void groupListQuery()
	{
		String sql = "";
		try{
			System.out.println("���� ��� ���� : " + groupList.size());
			
			for(int i=0; i<groupList.size(); i++)
			{
				sql = "insert into groupschedule1202(Gid,sDate,eDate) values("+find_Gid+",'"+groupList.get(i).sDate+"','"+groupList.get(i).eDate+"')";
				if(st.executeUpdate(sql)<=0) 
				{
					System.out.println("insert error");
					break;
				}
			}
			
			
		}catch(Exception ex){
			System.out.println("groupListQuery error " + ex);
		}
	}
	
	public void Merge(ArrayList<ListScheduleDate> group, ScheduleDate user)
	{
		
		int count=0;
		try{
			while(count<user.scheduleCount)
			{
				//�ϴ� �׷쿡 ���Ѵ�.
				group.add(new ListScheduleDate(user.sDate[count],user.eDate[count]));
				arrangeList();
				count++;
			}
			
		}catch(Exception ex){
			System.out.println("[Merge] error" + ex);
		}
	}
	
	public void arrangeList()
	{
		//1. UeDate < GsDate || UsDate > GeDate (���� ��) -> �������
		//2. UsDate < GsDate && (UeDate > GsDate && UeDate < GeDate) (�Ʒ� ��ħ)
		//3. UsDate < GsDate && UeDate > GeDate (����)
		//4. (UsDate > GsDate && UsDate < GeDate) && UeDate > GeDate (�� ��ħ)
	try{	
			ArrayList<Integer> removeList = new ArrayList<Integer>();
			ListScheduleDate user = groupList.get(groupList.size()-1);//���� �ֱٿ� ���� ��
			System.out.println("������ : " + user.sDate + " " + user.eDate);
			System.out.println("�׷콺���� ���� " + groupList.size()+"���� Merge ����");
			for(int i=0; i<groupList.size()-1; i++)
			{
				if((user.eHMinute < groupList.get(i).eHMinute) && 
				(groupList.get(i).sHMinute<=user.eHMinute && user.sHMinute < groupList.get(i).sHMinute))
				{//user�� endDate ���� �ٲ۴�.
					user.eDate = groupList.get(i).eDate;//�Ʒ��� �ø�
					user.splitListDate();
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
					user.splitListDate();
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
	
	public void insertSchedule(ScheduleDate sch)
	{
		try{
			int count = 0;
			while(count<sch.scheduleCount)
			{
				ListScheduleDate ls = new ListScheduleDate(sch.sDate[count],sch.eDate[count]);
				groupList.add(ls);
				count++;
			}
			
		}catch(Exception ex){
			System.out.println("insertSchedule error " + ex);
		}
	}
	
	public  void CalcAvailabletime()//������ �ð� ��� ������
	{
		int count = 0;
		int term = 0;
		
		term = userS.sHour[count]*60+userS.sMinute[count] - sHour*60+sMinute;
		
		if(term >= find_aTime)
		{
			// sHour, Sminute , userS.sHour[count], userS.sMinute[count] ����
			System.out.println(sHour+" " + sMinute + " " + userS.sHour[count] + " " + userS.sMinute[count]);
		}
		
		if(userS.scheduleCount>=2)
		{
			System.out.println("if");
			while(count<userS.scheduleCount-1)
			{
				count++;
				System.out.println("in while");
				term = userS.sHour[count]*60+userS.sMinute[count] - userS.eHour[count-1]*60+userS.eMinute[count-1];
				
				if(term >= find_aTime)
				{
					//userS.eHour[count], userS.eMinute[count], userS.sHour[count-1], userS.sMinute[count-1] ����
					System.out.println(userS.sHour[count]+" " + userS.sMinute[count] + " " + userS.eHour[count-1] + " " + userS.eMinute[count-1]);
				}
				
			}
			
		}
		System.out.println("End of while");
		term = eHour*60+eMinute - userS.eHour[count]*60+userS.eMinute[count];
		
		if(term >= find_aTime)
		{
			System.out.println(eHour+" " + eMinute + " " + userS.eHour[count] + " " + userS.eMinute[count]);
			// eHour, eMinute , userS.eHour[count], userS.eMinute[count] ����
		}
		
	}
	
	public int[] splitDate(String date)
	{
		int[] result = new int[5];
		
		result[0] = Integer.parseInt(date.substring(0,4));
		result[1] = Integer.parseInt(date.substring(4,6));
		result[2] = Integer.parseInt(date.substring(6,8));
		result[3] = Integer.parseInt(date.substring(8,10));
		result[4] = Integer.parseInt(date.substring(10));
		
		return result;
	}

	public void getFindtimeinfo(ResultSet r) throws Exception
	{
		r.next();
		
		find_Gid = Integer.parseInt(r.getString("Gid"));
		find_Pcount = Integer.parseInt(r.getString("Pcount"));
		find_sDate = r.getString("sDate");
		find_eDate = r.getString("eDate");
		find_aTime = Integer.parseInt(r.getString("aTime"));
		
		int[] splitSdate = splitDate(find_sDate);
		sYear = splitSdate[0];
		sMonth = splitSdate[1];
		sDay = splitSdate[2];
		sHour = splitSdate[3];
		sMinute = splitSdate[4];
		
		int[] splitEdate = splitDate(find_eDate);
		eYear = splitEdate[0];
		eMonth = splitEdate[1];
		eDay = splitEdate[2];
		eHour = splitEdate[3];
		eMinute = splitEdate[4];
		
	}
	
	public void getPersoninfo()
	{
		/*���� ������ ���� ����*/
		/*
		query = "select sDate,eDate from schedule1202 where Id='�ٿ�' and Priority='M' and sDate>="+find_sDate+" and eDate<="+find_eDate+" order by sDate asc";
		rs = st.executeQuery(query);
		int index = 0;
		while(rs.next()) index++;
		
		System.out.println("��� ���� :" + index);
		
		userS = new ScheduleDate(index);
		
		rs.beforeFirst(); 
		
		int count=0;
		
		while(count<index)
		{
			rs.next();
			
			userS.sYear[count] = Integer.parseInt(rs.getString("sDate").substring(0,4));
			userS.sMonth[count] = Integer.parseInt(rs.getString("sDate").substring(4,6));
			userS.sDay[count] = Integer.parseInt(rs.getString("sDate").substring(6,8));
			userS.sHour[count] = Integer.parseInt(rs.getString("sDate").substring(8,10));
			userS.sMinute[count] = Integer.parseInt(rs.getString("sDate").substring(10));

			userS.eYear[count] = Integer.parseInt(rs.getString("eDate").substring(0,4));
			userS.eMonth[count] = Integer.parseInt(rs.getString("eDate").substring(4,6));
			userS.eDay[count] = Integer.parseInt(rs.getString("eDate").substring(6,8));
			userS.eHour[count] = Integer.parseInt(rs.getString("eDate").substring(8,10));
			userS.eMinute[count] = Integer.parseInt(rs.getString("eDate").substring(10));
			
			count++;
		}
		//user (���� ������ ����) ��ü �ϼ�
		*/
	}
	
	public static void main(String[] args) {
		new Calc();
		
	}// end of main

}
