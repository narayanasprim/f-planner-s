package com.f_planner_app;
import java.io.Serializable;


public class gPacket implements Serializable{
	
	private static final long serialVersionUID = 1L;
	private Group[] group;
	
	public gPacket(Group g)
	{
		this.group = new Group[1];
		group[0] = g;
	}
	
	public gPacket(Group[] gg)
	{
		this.group = gg;
	}
	
	public Group getGroup()
	{
		return this.group[0];
	}
	
	public Group[] getAllGroup()
	{
		return this.group;
	}
	
}

@SuppressWarnings("serial")
class Group implements Serializable
{
	public int Gid;
	public String Leader;
	public String Gname;
	public String People;
	public int Pcount;
	public String Content;
	public String sDate;
	public String eDate;
	public int aTime;
	public String Register;
	public String DATE;
	
	public Group()
	{
		//Empth
	}
	
	public Group(int Gid,String Leader,String Gname,String People,int Pcount,String content,String sDate,String eDate,int aTime,String Register,String DATE){
	
		this.Gid = Gid;
		this.Leader = Leader;
		this.Gname = Gname;
		this.People = People;
		this.Pcount = Pcount;
		this.Content = content;
		this.sDate = sDate;
		this.eDate = eDate;
		this.aTime = aTime;
		this.Register = Register;
		this.DATE = DATE;
	}
}
