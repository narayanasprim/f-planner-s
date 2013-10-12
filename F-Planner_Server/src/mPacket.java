import java.io.Serializable;
import java.util.ArrayList;


@SuppressWarnings("serial")
public class mPacket implements Serializable{

	private String[] msg = null;
	private String[] phones;
	/*//ArrayList ��� ������*/
	public mPacket(ArrayList<String> ph, String message)
	{
		this.phones = new String[ph.size()];
		ph.toArray(phones);
		msg = new String[1];
		this.msg[0] = message;
	}
	/*//���ڿ� �迭 ��� ������*/
	public mPacket(String[] ph, String message)
	{
		this.phones = ph;
		msg = new String[1];
		this.msg[0] = message;
	}
	/*�޽��� ���ۿ� ������*/
	public mPacket(String[] message)
	{
		this.msg = message;
	}
	/*���� �޽����� ��ȯ*/
	public String getMessage()
	{
		return this.msg[0];
	}
	/*���� ��ȣ�� ��ȯ*/
	public String[] getAllPhones()
	{
		return this.phones;
	}
	/*���� �޽����� ��ȯ*/
	public String[] getAllMessages()
	{
		return this.msg;
	}
}
