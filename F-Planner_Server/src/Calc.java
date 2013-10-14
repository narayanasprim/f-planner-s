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

				/*이용가능 시간 정보를 얻음*/
				query = "select Gid,Pcount,sDate,eDate,aTime from findtime1202";
				rs = st.executeQuery(query);
				getFindtimeinfo(rs);
				
				ArrayList<String> ph = new ArrayList<String>();
				ph.add("01055954945");
				ph.add("01063360267");
				ph.add("01012341234");
				
				//스케줄 합체
				mergeSchedule(ph);
				
				//공강시간 계산 메서드
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
		
		int scheduleCount;//스케줄 갯수
		
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
			sch.sYear[count] = splitSdate[0];//년
			sch.sMonth[count] = splitSdate[1];//월
			sch.sDay[count] = splitSdate[2];//일
			sch.sHour[count] = splitSdate[3];//시
			sch.sMinute[count] = splitSdate[4];//분
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
	
	public void mergeSchedule(ArrayList<String> people)//인자로 수락한 사람들을 받는다.
	{
		//people에는 선택한 사람들의 핸드폰 번호가 저정되어있다.
		//번호를 기반으로 그 사람의 스케줄 날짜를 얻는다.
		
		ScheduleDate[] userSchedule = new ScheduleDate[people.size()];
		try{
			
			for(int i=0; i<people.size(); i++)//사람 수 만큼 반복
			{
				int index = 0;
				query = "select sDate,eDate from schedule1202 where Id=(select Id from userinfo1202 where Phone='"+people.get(i)+"') order by sDate asc";
				rs = st.executeQuery(query);
				while(rs.next()) index++;
				userSchedule[i] = new ScheduleDate(index);
				rs.beforeFirst();
				setScheduleDate(rs,userSchedule[i]);
			}//userSchdule 배열 완성.
			
			System.out.println("사람 별 스케줄 갯수");

			for(int i=0; i<people.size(); i++)
			{
				System.out.println(i+" " + userSchedule[i].scheduleCount);
			}
		}catch(Exception ex){
			System.out.println("merge error "+ ex);
		}
		
		try{
			//그룹스케줄 정보를 읽어온다.
			query = "select sDate,eDate from groupschedule1202 where Gid="+find_Gid+" order by sDate asc";
			rs = st.executeQuery(query);
			//그룹리스트(groupList) 완성
			setListScheduleDate(rs);
			
			//최초 값을 읽어 0인지 확인
			if(groupList.get(0).sDate.equals("0"))
			{
				groupList.clear();
				//첫번째 사람 스케줄 일반 삽입
				insertSchedule(userSchedule[0]);
				
				//병합 시작
				for(int i=1; i<userSchedule.length; i++)
				Merge(groupList,userSchedule[i]);
			}
			else
			{
				//비교 병합 시작
				for(int i=0; i<userSchedule.length; i++)
				Merge(groupList,userSchedule[i]);
			}
			
			groupListQuery();//결과를 쿼리로 저장
			
		}catch(Exception ex){
			System.out.println("get groupschedule error " +ex );
		}
	}
	/*최종 결과를 쿼리로 날림*/
	public void groupListQuery()
	{
		String sql = "";
		try{
			System.out.println("최종 목록 갯수 : " + groupList.size());
			
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
				//일단 그룹에 더한다.
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
		//1. UeDate < GsDate || UsDate > GeDate (범위 밖) -> 상관없음
		//2. UsDate < GsDate && (UeDate > GsDate && UeDate < GeDate) (아래 걸침)
		//3. UsDate < GsDate && UeDate > GeDate (포함)
		//4. (UsDate > GsDate && UsDate < GeDate) && UeDate > GeDate (위 걸침)
	try{	
			ArrayList<Integer> removeList = new ArrayList<Integer>();
			ListScheduleDate user = groupList.get(groupList.size()-1);//제일 최근에 들어온 것
			System.out.println("유저값 : " + user.sDate + " " + user.eDate);
			System.out.println("그룹스케줄 갯수 " + groupList.size()+"에서 Merge 시작");
			for(int i=0; i<groupList.size()-1; i++)
			{
				if((user.eHMinute < groupList.get(i).eHMinute) && 
				(groupList.get(i).sHMinute<=user.eHMinute && user.sHMinute < groupList.get(i).sHMinute))
				{//user의 endDate 값을 바꾼다.
					user.eDate = groupList.get(i).eDate;//아래로 늘림
					user.splitListDate();
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
					user.splitListDate();
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
	
	public  void CalcAvailabletime()//가능한 시간 계산 때리기
	{
		int count = 0;
		int term = 0;
		
		term = userS.sHour[count]*60+userS.sMinute[count] - sHour*60+sMinute;
		
		if(term >= find_aTime)
		{
			// sHour, Sminute , userS.sHour[count], userS.sMinute[count] 저장
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
					//userS.eHour[count], userS.eMinute[count], userS.sHour[count-1], userS.sMinute[count-1] 저장
					System.out.println(userS.sHour[count]+" " + userS.sMinute[count] + " " + userS.eHour[count-1] + " " + userS.eMinute[count-1]);
				}
				
			}
			
		}
		System.out.println("End of while");
		term = eHour*60+eMinute - userS.eHour[count]*60+userS.eMinute[count];
		
		if(term >= find_aTime)
		{
			System.out.println(eHour+" " + eMinute + " " + userS.eHour[count] + " " + userS.eMinute[count]);
			// eHour, eMinute , userS.eHour[count], userS.eMinute[count] 저장
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
		/*개인 스케줄 정보 얻음*/
		/*
		query = "select sDate,eDate from schedule1202 where Id='다운' and Priority='M' and sDate>="+find_sDate+" and eDate<="+find_eDate+" order by sDate asc";
		rs = st.executeQuery(query);
		int index = 0;
		while(rs.next()) index++;
		
		System.out.println("결과 갯수 :" + index);
		
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
		//user (개인 스케줄 정보) 객체 완성
		*/
	}
	
	public static void main(String[] args) {
		new Calc();
		
	}// end of main

}
