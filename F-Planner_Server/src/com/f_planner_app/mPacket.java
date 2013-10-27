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
	private String Gname;
	/*�ܼ� �޽���*/
	private Message[] message;
	/*FindfreeTime ���� �޽��� ������*/
	public mPacket(String Gname, ArrayList<String> ph, String content, String sDate, String eDate,String aTime, String type)
	{
		this.Gname = Gname;
		this.phones = ph;
		this.content = content;
		this.sDate = sDate;
		this.eDate = eDate;
		this.aTime = aTime;
		this.type = type;
	}
	/*�ܼ����� ���� �޽��� ������*/
	public mPacket(Message m)
	{
		this.message = new Message[1];
		this.message[0] = m;
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
	/*�׷��(Gname)�� ����*/
	public String getGname()
	{
		return this.Gname;
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
	public Message[] getAllMessage()
	{
		return this.message;
	}
}

@SuppressWarnings("serial")
class Message implements Serializable
{
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
	
	public Message(int Gid,String leader ,String content, String type, String decision ,String time)
	{
		this.Gid = Gid;
		this.leader = leader;
		this.content = content;
		this.type = type;
		this.decision = decision;
		this.time = time;
	}
}
