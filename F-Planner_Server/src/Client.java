import java.util.ArrayList;

public class Client {
	
	public static void printSchedule(Schedule sch)
	{
		System.out.println("���� : " + sch.Title);
		System.out.println("���۳�¥ : " + sch.sDate);
		System.out.println("���ᳯ¥ : " + sch.eDate);
		System.out.println("���� : " + sch.Day);
		System.out.println("��� : " + sch.Place);
		System.out.println("���� : " + sch.Content);
		System.out.println("�ݺ� : " + sch.Replay);
		System.out.println("�켱���� : " + sch.Priority);
	}
	
	public static void main(String[] args) {
		try {
			
			SVConnector sc = new SVConnector();
			if(sc.connectServer()) System.out.println("���� ���� ����");
			System.out.println("�α��� �õ�-> "+sc.Login("�ѱ�3", "��й�ȣ"));//�α���

			//System.out.println("�޽��� Ȯ�� �� ");
			
			//ArrayList<String> temp = sc.getAllMessages();
			//for(int i=0; i<temp.size(); i++)
				//System.out.println(temp.get(i));
			
//			Schedule[] schedule = sc.getSchedules();
//			Schedule schedule = new Schedule("���Ӱ� ������","201310080920","201310090920","Thu","���","���� �׽�Ʈ��",'F','B');
//			System.out.println("���� ��� -> " + sc.modifySchedule(4, schedule));
//			System.out.println("���� ��� -> " + sc.deleteSchedule(1));
//			System.out.println("����� �߰� : "+sc.addUser("���ο�", "�н�����", "01011110202", "����"));
//			Schedule schedule = new Schedule("���Ӱ� �߰���","201310080920","201310090920","Thu","���","���� �׽�Ʈ��",'F','B');
//			System.out.println("������ �߰� ��� : "+sc.addSchedule(schedule));
//			System.out.println(sc.addUser("�׽���", "���"));
//			System.out.println(sc.checkID("�ѱ�1"));
			/*
			if(null != sc.getSchedule(5) )
			{
				Schedule sch = sc.getSchedule(5);
				System.out.println("���� : " + sch.Title);
				System.out.println("���۳�¥ : " + sch.sDate);
				System.out.println("���ᳯ¥ : " + sch.eDate);
				System.out.println("���� : " + sch.Day);
				System.out.println("��� : " + sch.Place);
				System.out.println("���� : " + sch.Content);
				System.out.println("�ݺ� : " + sch.Replay);
				System.out.println("�켱���� : " + sch.Priority);
			}
			*/
			
			if(sc.EXIT()) System.out.println("���� ���� ����!"); 
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
	}
	
	
}
