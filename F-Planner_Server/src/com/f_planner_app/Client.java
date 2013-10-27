package com.f_planner_app;
import java.util.ArrayList;

public class Client {
	
	public static SVConnector sc = new SVConnector(); 
	
	public static void printSchedule(Schedule sch)
	{
		System.out.println("제목 : " + sch.Title);
		System.out.println("시작날짜 : " + sch.sDate);
		System.out.println("종료날짜 : " + sch.eDate);
		System.out.println("요일 : " + sch.Day);
		System.out.println("장소 : " + sch.Place);
		System.out.println("내용 : " + sch.Content);
		System.out.println("반복 : " + sch.Replay);
		System.out.println("우선순위 : " + sch.Priority);
	}
	
	public static void printMessage(Message m)
	{
		System.out.println("그룹번호 : " + m.Gid);
		System.out.println("리더 : " + m.leader);
		System.out.println("그룹이름 : " + m.Gname);
		System.out.println("내용 : " + m.content);
		System.out.println("타입 : " + m.type);
		System.out.println("현재 결정 : " + m.decision);
		System.out.println("받은시간 : " + m.time);
	}

	public static void printGroupInfo(Group g)
	{
		System.out.println("그룹 번호 : " + g.Gid);
		System.out.println("그룹 리더 : " + g.Leader);
		System.out.println("그룹 이름 : " + g.Gname);
		System.out.println("그룹 사람들 : " + g.People);
		System.out.println("그룹 사람 수 : " + g.Pcount);
		System.out.println("그룹 내용 : " + g.Content);
		System.out.println("그룹 시작범위날짜 : " + g.sDate);
		System.out.println("그룹 종료범위날짜 : " + g.eDate);
		System.out.println("그룹 이용시간 : " + g.aTime);
		System.out.println("그룹 생성시간 : " + g.DATE);
	}
	
	public static void Hojin()
	{
		System.out.println("로그인 시도-> "+sc.Login("h", "h"));//로그인
		ArrayList<String> ph = new ArrayList<String>();
		ph.add("01063360267");//다운
		ph.add("01012341234");//기문
		ph.add("01012345678");//영준
		
		boolean result = sc.requestGroupTime("새로운그룹", ph, "오늘 모이자", "201310130500", "201310132400", "120", "REQUEST");
		System.out.println("요청 결과 : " + result);
	}
	
	public static void Dawoon()
	{
		System.out.println("로그인 시도-> "+sc.Login("d", "d"));//로그인
		checkMessage();
	}
	
	public static void Kimoon()
	{
		System.out.println("로그인 시도-> "+sc.Login("k", "k"));//로그인
		checkMessage();
	}
	
	public static void Youngjoon()
	{
		System.out.println("로그인 시도-> "+sc.Login("y", "y"));
		checkMessage();
	}
	
	public static void checkMessage()
	{
		System.out.println("메시지 확인 ↓--------------- ");
		Message[] message = sc.getAllMessages();
		for(Message m : message)
		printMessage(m);
		
		
		System.out.println("----------------------------");
		//첫번째 메시지 승낙!
		boolean result = sc.sendOpinion(message[0].Gid, Message.ACCEPT);
		System.out.println("메시지 결과 " + result);
	}
	
	public static void main(String[] args) {
		try {
			
		if(sc.connectServer()) System.out.println("서버 접속 성공");

			
//			Hojin();

//			Kimoon();

//			Dawoon();
		
			Youngjoon();
		
		
		
//			System.out.println("그룹 정보 확인 ↓ ");
//			Group[] group = sc.getAllGroupInfo();
//			for(Group g : group)
//			printGroupInfo(g);
			

//			Schedule[] schedule = sc.getSchedules();
//			Schedule schedule = new Schedule("새롭게 변경한","201310080920","201310090920","Thu","장소","에서 테스트중",'F','B');
//			System.out.println("변경 결과 -> " + sc.modifySchedule(4, schedule));
//			System.out.println("삭제 결과 -> " + sc.deleteSchedule(1));
//			System.out.println("사용자 추가 : "+sc.addUser("새로운", "패스워드", "01011110202", "뮤쥑"));
//			Schedule schedule = new Schedule("새롭게 추가한","201310080920","201310090920","Thu","장소","에서 테스트중",'F','B');
//			System.out.println("스케줄 추가 결과 : "+sc.addSchedule(schedule));
//			System.out.println(sc.addUser("테스터", "비밀"));
//			System.out.println(sc.checkID("한글1"));
		
			
			if(sc.EXIT()) System.out.println("연결 종료 성공!"); 
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
	}
	
	
}
