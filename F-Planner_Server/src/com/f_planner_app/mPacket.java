package com.f_planner_app;
import java.io.Serializable;
import java.util.ArrayList;


public class mPacket implements Serializable{
	private static final long serialVersionUID = 1L;
	/*그룹전용*/
	private String content;
	private ArrayList<String> list;
	private String type;
	private String eDate, sDate, aTime;
	private String title;
	/*단순 메시지*/
	private Message[] message;
	/*FindfreeTime 전용 메시지 생성자*/
	public mPacket(ArrayList<String> ph , String title, String content, String sDate, String eDate,String aTime, String type)
	{
		this.title = title;
		this.list = ph;
		this.content = content;
		this.sDate = sDate;
		this.eDate = eDate;
		this.aTime = aTime;
		this.type = type;
	}
	/*단순전달 단일 메시지 생성자*/
	public mPacket(ArrayList<String> ph, String title, String content)
	{
		this.title = title;
		this.list = ph;
		this.content = content;
	}
	/*단순전달 다중 메시지 생성자*/
	public mPacket(Message[] mm)
	{
		this.message = mm;
	}
	/*삭제 리스트*/
	public mPacket(ArrayList<String> del)
	{
		this.list = del;
	}
	//------------------------------//
	/*번호 리스트를 반환*/
	public ArrayList<String> getList()
	{
		return this.list;
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
	/*메시지 제목을 얻음*/
	public String getTitle()
	{
		return this.title;
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
	
	public String name;
	public String title;
	public String leader;
	public String type;
	public String content;
	public String time;
	public String decision;
	public String Unum;//메시지 고유 번호
	public String Gid;
	
	public Message()
	{
		//Empty
	}
	
	public Message(String Unum, String Gid,String title, String leader ,String content, String type, String decision ,String time)
	{
		this.Gid = Gid;
		this.Unum = Unum;
		this.title = title;
		this.leader = leader;
		this.content = content;
		this.type = type;
		this.decision = decision;
		this.time = time;
	}
	
	//그룹원 확인용
	public Message(String name, String decision)
	{
		this.name = name;
		this.decision = decision;
	}
}
