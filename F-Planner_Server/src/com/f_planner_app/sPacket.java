package com.f_planner_app;
import java.io.Serializable;

/*sPacket 클라이언트*/


public class sPacket implements Serializable{

		
	private static final long serialVersionUID = 2L;
	private Schedule[] schedule;
	
	public sPacket()
	{
		//Empty
	}

	public sPacket(Schedule[] ss)//생성자1
	{
		this.schedule = ss;
	}
	
	public sPacket(Schedule s)//생성자2
	{
		this.schedule = new Schedule[1];
		schedule[0] = s;
	}
	
	public Schedule[] getSchedules()//여러 스케줄 리턴
	{
		return this.schedule;
	}
	
	public Schedule getSchedule()//하나의 스케줄 리턴
	{
		return this.schedule[0];
	}
}

@SuppressWarnings("serial")
class Schedule implements Serializable,Comparable<Schedule>
{
	public int wNum = 0;//<-- 이 정보는 서버가 정함.
	public String Title = null;// 제목
	public String sDate = null;// 시작날짜
	public String eDate = null;// 끝날짜
	public String Day = null;// 요일
	public String Place = null;// 장소
	public String Content = null;// 내용
	public char Replay;// 반복여부(연간->Y 월간->M 일간->D 시간당->H 없음->F)
	public char Priority;// 우선순위( (높음)A > B(낮음) )
	// Date 를 년, 월 , 일 , 시, 분 으로 분리(숫자)
	public int sYear, eYear;
	public short sMonth, eMonth;
	public short sDay, eDay;
	public short sHour, eHour;
	public short sMinute, eMinute;
	public int sHMinute, eHMinute;
	
	public Schedule()
	{
		//Empty
	}
	public Schedule(String title, String content, String start_time, String end_time)
	{
		this.Title = title;
		this.Content = content;
		this.sDate = start_time;
		this.eDate = end_time;
	}
	/*스케줄 정보용*/
	public Schedule(String Title,String sDate, String eDate, String Day , String Place, String Content, char Replay, char Priority)
	{
		this.Title = Title;
		this.sDate = sDate;
		this.eDate = eDate;
		this.Day = Day;
		this.Place = Place;
		this.Content = Content;
		this.Replay = Replay;
		this.Priority = Priority;
		splitScheduleDate();//날짜 상세 분리
	}
	/*날짜 계산용*/
	public Schedule(String sDate, String eDate)
	{
		this.sDate = sDate;
		this.eDate = eDate;
		if(sDate.equals("0") || eDate.equals("0"))
		zeroSet();
		else
		splitScheduleDate();//날짜 분리
	}
	public void zeroSet()
	{
		this.sMonth = sDay = sHour = sMinute = 0;
		this.eMonth = eDay = eHour = eMinute = 0;
		this.sYear = sHMinute = 0;
		this.eYear = eHMinute = 0;
	}
	/*년,월,일,시,분을 구분지음*/
	public void splitScheduleDate()
	{
		this.sYear = Integer.parseInt(sDate.substring(0,4));
		this.sMonth = Short.parseShort(sDate.substring(4,6));
		this.sDay = Short.parseShort(sDate.substring(6,8));
		this.sHour = Short.parseShort(sDate.substring(8,10));
		this.sMinute = Short.parseShort(sDate.substring(10,12));
		this.sHMinute = (this.sHour*60 + this.sMinute);
		
		this.eYear = Integer.parseInt(eDate.substring(0,4));
		this.eMonth = Short.parseShort(eDate.substring(4,6));
		this.eDay = Short.parseShort(eDate.substring(6,8));
		this.eHour = Short.parseShort(eDate.substring(8,10));
		this.eMinute = Short.parseShort(eDate.substring(10,12));
		this.eHMinute = (this.eHour*60 + this.eMinute);
	}
	
	@Override
	public int compareTo(Schedule s) {
		int result = 0;
		
		if(Integer.parseInt(this.sDate)>Integer.parseInt(s.sDate)) result = 1;
		else if(Integer.parseInt(this.sDate)<Integer.parseInt(s.sDate)) result = -1;
		else
		{
			if(Integer.parseInt(this.eDate)>Integer.parseInt(s.eDate)) result = 1;
			else if(Integer.parseInt(this.eDate)<Integer.parseInt(s.eDate)) result = -1;
		}
			
		return result;
	}
}

