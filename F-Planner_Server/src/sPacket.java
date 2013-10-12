import java.io.Serializable;


@SuppressWarnings("serial")
public class sPacket implements Serializable{

	private Schedule[] schedule;
	
	public sPacket()
	{
		//Empty
	}

	public sPacket(Schedule[] ss)//������1
	{
		this.schedule = ss;
	}
	
	public sPacket(Schedule s)//������2
	{
		this.schedule = new Schedule[1];
		schedule[0] = s;
	}
	
	public Schedule[] getSchedules()//���� ������ ����
	{
		return this.schedule;
	}
	
	public Schedule getSchedule()//�ϳ��� ������ ����
	{
		return this.schedule[0];
	}
}


@SuppressWarnings("serial")
class Schedule implements Serializable
{
	public int wNum = 0;//<-- �� ������ ������ ����.
	public String Title = null;// ����
	public String sDate = null;// ���۳�¥
	public String eDate = null;// ����¥
	public String Day = null;// ����
	public String Place = null;// ���
	public String Content = null;// ����
	public char Replay;// �ݺ�����(����->Y ����->M �ϰ�->D �ð���->H ����->F)
	public char Priority;// �켱����( (����)A > B(����) )
	// Date �� ��, �� , �� , ��, �� ���� �и�(����)
	public int sYear, eYear;
	public short sMonth, eMonth;
	public short sDay, eDay;
	public short sHour, eHour;
	public short sMinute, eMinute;
	
	public Schedule()
	{
		//Empty
	}
	
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
		this.splitScheduleDate();//��¥ �� �и�
	}
	
	/*��,��,��,��,���� ��������*/
	public void splitScheduleDate()
	{
		this.sYear = Integer.parseInt(sDate.substring(0,4));
		this.sMonth = Short.parseShort(sDate.substring(4,6));
		this.sDay = Short.parseShort(sDate.substring(6,8));
		this.sHour = Short.parseShort(sDate.substring(8,10));
		this.sMinute = Short.parseShort(sDate.substring(10,12));
		
		this.eYear = Integer.parseInt(eDate.substring(0,4));
		this.eMonth = Short.parseShort(eDate.substring(4,6));
		this.eDay = Short.parseShort(eDate.substring(6,8));
		this.eHour = Short.parseShort(eDate.substring(8,10));
		this.eMinute = Short.parseShort(eDate.substring(10,12));
	}
}

