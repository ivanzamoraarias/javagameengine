package elements.com;
import java.io.Serializable;


public class TimeLine implements Serializable{

	int Actualtime;
	int StarTime;
	int FinishTime;
	int ticSize;
	
	boolean flag;
	
	int factor;
	
	TimeLine fatherTime;
	
	TimeLine()
	{
		factor=1;
		fatherTime=null;
		StarTime=0;
		FinishTime=0;
		ticSize=0;
		flag= false;
	}
	TimeLine(int s, int f, int tic)
	{
		
	}
	public void setFactor(int f)
	{
		factor=f;
	}
	public void setActualTime(int i)
	{
		Actualtime=i;
	}
	public void setFinishTime( int i)
	{
		StarTime=FinishTime;
		FinishTime= i;
	}
	
	TimeLine(TimeLine t)
	{
		fatherTime= t;
	}
	
	public void increaseTime()
	{
		Actualtime+=ticSize;
	}
	public void calculateTic()
	{
		//System.out.println("Start "+ StarTime+ " Finish "+FinishTime);
		if(FinishTime!=0 && StarTime!=0)
		ticSize= FinishTime-StarTime;
		else 
			ticSize=0;
		//System.out.println("Start "+ StarTime+ " Finish "+FinishTime+ " Delta "+ticSize);
	}
	
	
	
}
