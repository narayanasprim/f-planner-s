import java.util.ArrayList;

public class Client {
	
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
	
	public static void main(String[] args) {
		try {
			
			SVConnector sc = new SVConnector();
			if(sc.connectServer()) System.out.println("서버 접속 성공");
			System.out.println("로그인 시도-> "+sc.Login("한글3", "비밀번호"));//로그인

			//System.out.println("메시지 확인 ↓ ");
			
			//ArrayList<String> temp = sc.getAllMessages();
			//for(int i=0; i<temp.size(); i++)
				//System.out.println(temp.get(i));
			
//			Schedule[] schedule = sc.getSchedules();
//			Schedule schedule = new Schedule("새롭게 변경한","201310080920","201310090920","Thu","장소","에서 테스트중",'F','B');
//			System.out.println("변경 결과 -> " + sc.modifySchedule(4, schedule));
//			System.out.println("삭제 결과 -> " + sc.deleteSchedule(1));
//			System.out.println("사용자 추가 : "+sc.addUser("새로운", "패스워드", "01011110202", "뮤쥑"));
//			Schedule schedule = new Schedule("새롭게 추가한","201310080920","201310090920","Thu","장소","에서 테스트중",'F','B');
//			System.out.println("스케줄 추가 결과 : "+sc.addSchedule(schedule));
//			System.out.println(sc.addUser("테스터", "비밀"));
//			System.out.println(sc.checkID("한글1"));
			/*
			if(null != sc.getSchedule(5) )
			{
				Schedule sch = sc.getSchedule(5);
				System.out.println("제목 : " + sch.Title);
				System.out.println("시작날짜 : " + sch.sDate);
				System.out.println("종료날짜 : " + sch.eDate);
				System.out.println("요일 : " + sch.Day);
				System.out.println("장소 : " + sch.Place);
				System.out.println("내용 : " + sch.Content);
				System.out.println("반복 : " + sch.Replay);
				System.out.println("우선순위 : " + sch.Priority);
			}
			*/
			
			if(sc.EXIT()) System.out.println("연결 종료 성공!"); 
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
	}
	
	
}
