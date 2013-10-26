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
	
	public static void printMessage(Message m)
	{
		System.out.println("�׷��ȣ : " + m.Gid);
		System.out.println("���� : " + m.leader);
		System.out.println("���� : " + m.content);
		System.out.println("Ÿ�� : " + m.type);
		System.out.println("���� ���� : " + m.decision);
		System.out.println("�����ð� : " + m.time);
	}

	public static void printGroupInfo(Group g)
	{
		System.out.println("�׷� ��ȣ : " + g.Gid);
		System.out.println("�׷� ���� : " + g.Leader);
		System.out.println("�׷� �̸� : " + g.Gname);
		System.out.println("�׷� ����� : " + g.People);
		System.out.println("�׷� ��� �� : " + g.Pcount);
		System.out.println("�׷� ���� : " + g.Content);
		System.out.println("�׷� ���۹�����¥ : " + g.sDate);
		System.out.println("�׷� ���������¥ : " + g.eDate);
		System.out.println("�׷� �̿�ð� : " + g.aTime);
		System.out.println("�׷� �����ð� : " + g.DATE);
	}
	
	public static void main(String[] args) {
		try {
			
			SVConnector sc = new SVConnector();
			if(sc.connectServer()) System.out.println("���� ���� ����");
//			System.out.println("�α��� �õ�-> "+sc.Login("ȣ��", "a"));//�α���
//			System.out.println("�α��� �õ�-> "+sc.Login("�ٿ�", "b"));//�α���
//			System.out.println("�α��� �õ�-> "+sc.Login("�⹮", "c"));//�α���
			System.out.println("�α��� �õ�-> "+sc.Login("����", "d"));
			/*
			ArrayList<String> ph = new ArrayList<String>();
			ph.add("01063360267");//�ٿ�
			ph.add("01012341234");//�⹮
			ph.add("01012345678");//����
			
			boolean result = sc.requestGroupTime("���ο�׷�", ph, "���� ������", "201310150500", "201310150900", "120", "REQUEST");
			System.out.println("��û ��� : " + result);
			*/
			
			System.out.println("�޽��� Ȯ�� ��--------------- ");
			Message[] message = sc.getAllMessages();
			for(Message m : message)
			printMessage(m);
			
			Thread.sleep(4000);
			
			System.out.println("----------------------------");
			//ù��° �޽��� �³�!
			boolean result = sc.sendOpinion(message[0].Gid, "ACCEPT");
			System.out.println("�޽��� ��� " + result);
			
			
			
//			System.out.println("�׷� ���� Ȯ�� �� ");
//			Group[] group = sc.getAllGroupInfo();
//			for(Group g : group)
//			printGroupInfo(g);
			

			
	
			

//			Schedule[] schedule = sc.getSchedules();
//			Schedule schedule = new Schedule("���Ӱ� ������","201310080920","201310090920","Thu","���","���� �׽�Ʈ��",'F','B');
//			System.out.println("���� ��� -> " + sc.modifySchedule(4, schedule));
//			System.out.println("���� ��� -> " + sc.deleteSchedule(1));
//			System.out.println("����� �߰� : "+sc.addUser("���ο�", "�н�����", "01011110202", "����"));
//			Schedule schedule = new Schedule("���Ӱ� �߰���","201310080920","201310090920","Thu","���","���� �׽�Ʈ��",'F','B');
//			System.out.println("������ �߰� ��� : "+sc.addSchedule(schedule));
//			System.out.println(sc.addUser("�׽���", "���"));
//			System.out.println(sc.checkID("�ѱ�1"));
		
			
			if(sc.EXIT()) System.out.println("���� ���� ����!"); 
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
	}
	
	
}
