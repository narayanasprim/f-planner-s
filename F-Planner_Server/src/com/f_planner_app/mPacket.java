package com.f_planner_app;
import java.io.Serializable;
import java.util.ArrayList;


public class mPacket implements Serializable{
	private static final long serialVersionUID = 1L;
	/*�׷�����*/
	private String content;
	private ArrayList<String> phones;
	private String type;
	private String eDate, sDate, aTime;
	private String title;
	/*�ܼ� �޽���*/
	private Message[] message;
	/*FindfreeTime ���� �޽��� ������*/
	public mPacket(String title, ArrayList<String> ph, String content, String sDate, String eDate,String aTime, String type)
	{
		this.title = title;
		this.phones = ph;
		this.content = content;
		this.sDate = sDate;
		this.eDate = eDate;
		this.aTime = aTime;
		this.type = type;
	}
	/*�ܼ����� ���� �޽��� ������*/
	public mPacket(ArrayList<String> ph, String title, String content)
	{
		this.title = title;
		this.phones = ph;
		this.content = content;
	}
	/*�ܼ����� ���� �޽��� ������*/
	public mPacket(Message[] mm)
	{
		this.message = mm;
	}
	//------------------------------//
	/*��ȣ ����Ʈ�� ��ȯ*/
	public ArrayList<String> getAllPhones()
	{
		return this.phones;
	}
	/*�޽��� Ÿ���� ����*/
	public String getMessageType()
	{
		return this.type;
	}
	/*sDate ���� ����*/
	public String getSdate()
	{
		return this.sDate;
	}
	/*eDate ���� ����*/
	public String getEdate()
	{
		return this.eDate;
	}
	/*aTime ���� ����*/
	public String getAtime()
	{
		return this.aTime;
	}
	/*�޽��� ������ ����*/
	public String getTitle()
	{
		return this.title;
	}
	/*�޽��� ������ ����*/
	public String getContent()
	{
		return this.content;
	}
//------------------------------------//	
	/*���� �޽����� ����*/
	public Message getMessage()
	{
		return this.message[0];
	}
	/*��� �޽����� ����*/
	public Message[] getAllMessages()
	{
		return this.message;
	}
}

@SuppressWarnings("serial")
class Message implements Serializable
{
	public static final String ACCEPT="ACCEPT";
	public static final String REJECT="REJECT";
	public static final String REQUEST="REQUEST";
	public static final String NOT_DECISION="NOT_DECISION";
	public static final String NOTIFY = "NOTIFY";
	
	public String title;
	public String leader;
	public String type;
	public String content;
	public String time;
	public String decision;
	public int Gid;
	
	public Message()
	{
		//Empty
	}
	
	public Message(String Gid,String title, String leader ,String content, String type, String decision ,String time)
	{
		if(null == Gid) this.Gid = -1;
		else			this.Gid = Integer.parseInt(Gid);
		this.title = title;
		this.leader = leader;
		this.content = content;
		this.type = type;
		this.decision = decision;
		this.time = time;
	}
}
