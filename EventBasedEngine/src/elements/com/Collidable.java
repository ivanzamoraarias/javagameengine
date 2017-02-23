package elements.com;
import java.io.Serializable;
import java.util.LinkedList;


public class Collidable implements Component, Serializable{

	GameObj gameobject;
	LinkedList<GameObj> objectsToInteract;
	
	boolean []boundaries=new boolean [4];
	public boolean collidableBetweenObjects;
	
	//LinkedList<EventHandlerTest> objectEvent= new LinkedList<>();
	
	
	Collidable(GameObj a)
	{
		gameobject=a;
		objectsToInteract= new LinkedList<>();
		enableBoundaries(false, false, false, false);
		collidableBetweenObjects=false;
	}
	
	public void enableBoundaries(boolean up, boolean down,boolean right, boolean left)
	{
		boundaries[0]=up;
		boundaries[1]=down;
		boundaries[2]=right;
		boundaries[3]=left;
	}
	public void addObjectsToInteract(GameObj a)
	{
		objectsToInteract.add(a);
	}
	public boolean isMoveComponent()
	{
		if(gameobject.moveComponent!=null)
			return true;
	
		return false;
	}
	public void CollideObjectsBehavior()
	{
		if(collidableBetweenObjects)
		{
			for(GameObj i: objectsToInteract)
			{
				if(i!=gameobject)
				{
					float h=gameobject.objectHeight/2;
					float b= gameobject.objectWidth/2;
					float ho=i.objectHeight/2;
					float bo= i.objectWidth/2;
			
					float dx = Math.abs(i.position[0] - gameobject.position[0]);
					float dy = Math.abs(i.position[1] - gameobject.position[1]);
		    
					boolean colH = dx < b + bo;
					boolean colV = dy < h + ho;
		    
					if(colH && colV)
					{
						if(isMoveComponent())
							MakeBehabior();
		    	//gameobject.moveComponent.speed[0]*=-1;
		    	//gameobject.moveComponent.speed[1]*=-1;
		    	//xspeed*=-1;
		    	//yspeed*=-1;
		    	//xspeed *= colH ? -1 : 1;
			      //yspeed *= colV ? -1 : 1;
					}
				}
			}
		}
	}
	public void MakeBehabior()
	{
		gameobject.moveComponent.speed[0]*=-1;
    	gameobject.moveComponent.speed[1]*=-1;
	}
	public void enableCollidableBeweenObjects()
	{
		collidableBetweenObjects=true;
		
	}
	public void BoundariesBehavior()
	{
		if(boundaries[2])
		if ((gameobject.position[0]+gameobject.objectWidth/2 > gameobject.worldWidth) )
		{
			gameobject.moveComponent.speed[0]*=-1;
		}
		if(boundaries[3])	
		if(gameobject.position[0]-gameobject.objectWidth/2 < 0)
		{
					gameobject.moveComponent.speed[0]*=-1;
		}
		
		if(boundaries[0])
		if (gameobject.position[1]+gameobject.objectHeight/2 > gameobject.worldHeight) 
				{
			gameobject.moveComponent.speed[1]*=-1;
		    
		}
		if(boundaries[1])	
		if(gameobject.position[1]-gameobject.objectHeight/2 < 0)
		{
			gameobject.moveComponent.speed[1]*=-1;
		    
		}
		
	}
	
	public boolean isCollideWithOther(GameObj i)
	{
		float h=gameobject.objectHeight/2;
		float b= gameobject.objectWidth/2;
		float ho=i.objectHeight/2;
		float bo= i.objectWidth/2;

		float dx = Math.abs(i.position[0] - gameobject.position[0]);
		float dy = Math.abs(i.position[1] - gameobject.position[1]);

		boolean colH = dx < b + bo;
		boolean colV = dy < h + ho;
		boolean temp=colH && colV;
		if(temp==true)
		return temp;
		
		return colH && colV;
	}
	
	public boolean isCollisionBtweenAgents()
	{
		boolean ret=false;
		if(collidableBetweenObjects)
		{
			for(GameObj i: objectsToInteract)
			{
				if(i!=gameobject)
				{
					float h=gameobject.objectHeight/2;
					float b= gameobject.objectWidth/2;
					float ho=i.objectHeight/2;
					float bo= i.objectWidth/2;
			
					float dx = Math.abs(i.position[0] - gameobject.position[0]);
					float dy = Math.abs(i.position[1] - gameobject.position[1]);
		    
					boolean colH = dx < b + bo;
					boolean colV = dy < h + ho;
					boolean temp=colH && colV;
					
					if(temp==true)
					{
						ret= temp;
						//objectEvent.add(new EventHandlerTest());
					
					}
					
					//return colH && colV;
				}
			}
			
		}
		return ret;
	}
	@Override
	public void update() {
		// TODO Auto-generated method stub
		
		BoundariesBehavior();
		//CollideObjectsBehavior();
		
		
	    
	   
		
	}

}
