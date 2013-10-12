import java.io.Serializable;
import java.util.ArrayList;


@SuppressWarnings("serial")
public class mPacket implements Serializable{

	private String[] msg = null;
	private String[] phones;
	/*//ArrayList 기반 생성자*/
	public mPacket(ArrayList<String> ph, String message)
	{
		this.phones = new String[ph.size()];
		ph.toArray(phones);
		msg = new String[1];
		this.msg[0] = message;
	}
	/*//문자열 배열 기반 생성자*/
	public mPacket(String[] ph, String message)
	{
		this.phones = ph;
		msg = new String[1];
		this.msg[0] = message;
	}
	/*메시지 전송용 생성자*/
	public mPacket(String[] message)
	{
		this.msg = message;
	}
	/*단일 메시지를 반환*/
	public String getMessage()
	{
		return this.msg[0];
	}
	/*여러 번호를 반환*/
	public String[] getAllPhones()
	{
		return this.phones;
	}
	/*여러 메시지를 반환*/
	public String[] getAllMessages()
	{
		return this.msg;
	}
}
