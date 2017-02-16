package elements.com;

import java.util.concurrent.ConcurrentHashMap;

import processing.core.PApplet;

public class ClientEA extends PApplet {
	
	int gameWorldWidth;
	int gameWorldHeigh;
	
	ConcurrentHashMap<Integer, GameObj> clientsMap = new ConcurrentHashMap<>();
	

	public void setup() 
	{
		
	}
	
	public void draw() 
	{
		 
	}
	public void gameWorldCreation()
	{
		// static platform
		GameObj platform1= new GameObj(gameWorldWidth, gameWorldHeigh, 
				280, (float)(gameWorldHeigh*0.75),80,30);
		platform1.setColor(255, 0, 0);
		platform1.enableDrawable();
		platform1.enableCollidable();
		platform1.collidableComponent.enableCollidableBeweenObjects();
		clientsMap.put(platform1.ID, platform1);
		
		//moving platform
		GameObj Movingplatform= new GameObj(gameWorldWidth, gameWorldHeigh, 
				(float)(gameWorldWidth*0.75), (float)(gameWorldHeigh*0.75),80,30);
		Movingplatform.setColor(0, 255, 0);
		Movingplatform.enableDrawable();
		Movingplatform.enableMovable();
		//Movingplatform.moveComponent.EnableAutomaticPatter(0, 0, 300, 0);
		Movingplatform.moveComponent.setVelosity((float)0.5, (float)0);
		Movingplatform.moveComponent.EnableAutomaticPatterEvent(0, 0, 300, 0);
		Movingplatform.enableCollidable();
		Movingplatform.collidableComponent.enableCollidableBeweenObjects();
		Movingplatform.setIdent(34);
		clientsMap.put(Movingplatform.ID, Movingplatform);
		
		
		
	}
}
