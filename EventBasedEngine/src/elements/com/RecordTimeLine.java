package elements.com;
import java.util.concurrent.LinkedBlockingQueue;


public class RecordTimeLine extends TimeLine {

	LinkedBlockingQueue<Integer>RecordTimeList= new LinkedBlockingQueue<>();
	
	public void addTimeStamp(int i)
	{
		RecordTimeList.add(i);
	}
}
