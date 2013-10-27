package com.f_planner_app;
import java.io.Serializable;
import java.util.ArrayList;


public class mPacket implements Serializable{
	private static final long serialVersionUID = 1L;
	/*그룹전용*/
	private String content;
	private ArrayList<String> phones;
	private String type;
	private String eDate, sDate, aTime;
	private String Gname;
	/*단순 메시지*/
	private Message[] message;
	/*FindfreeTime 전용 메시지 생성자*/
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
	/*단순전달 단일 메시지 생성자*/
	public mPacket(Message m)
	{
		this.message = new Message[1];
		this.message[0] = m;
	}
	/*단순전달 다중 메시지 생성자*/
	public mPacket(Message[] mm)
	{
		this.message = mm;
	}
	//------------------------------//
	/*번호 리스트를 반환*/
	public ArrayList<String> getAllPhones()
	{
		return this.phones;
	}
	/*메시지 타입을 얻음*/
	public String getMessageType()
	{
		return this.type;
	}
	/*sDate 값을 얻음*/
	public String getSdate()
	{
		return this.sDate;
	}
	/*eDate 값을 얻음*/
	public String getEdate()
	{
		return this.eDate;
	}
	/*aTime 값을 얻음*/
	public String getAtime()
	{
		return this.aTime;
	}
	/*그룹명(Gname)을 얻음*/
	public String getGname()
	{
		return this.Gname;
	}
	/*메시지 내용을 얻음*/
	public String getContent()
	{
		return this.content;
	}
//------------------------------------//	
	/*단일 메시지를 받음*/
	public Message getMessage()
	{
		return this.message[0];
	}
	/*모든 메시지를 받음*/
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
