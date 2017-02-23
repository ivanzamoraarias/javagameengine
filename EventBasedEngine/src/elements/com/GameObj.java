package elements.com;
import java.awt.Color;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.concurrent.PriorityBlockingQueue;

import javax.swing.text.AbstractDocument.BranchElement;

import processing.core.PApplet;

/*
 * [1] The object model is based on the previous game object from homework 1
 */
public class GameObj implements Serializable {
	
	public int ID;
	public int IDclient;
	
	//timeline
	TimeLine localTimeLine;
	TimeLine GlobalTimeLine;
	
	float worldWidth;
	float worldHeight;
	float objectWidth;
	float objectHeight;
	public float[] position= new float[2];
	//components
	Color color;
	public Movable moveComponent;
	public Drawable drawableComponent;
	public Collidable collidableComponent;
	CollideAsDeadZone collidableasdeadzone;
	
	int ident=0;
	
	LinkedList<Component> componentList= new LinkedList<>();
	
	public EventHandler objectEvent;
	
	public GameObj(float worldW, float worldH, float x, float y,float widthn,float heightn) 
	{
		ID= this.hashCode();
		worldWidth=worldW;
		worldHeight=worldH;
		
		objectWidth=widthn;
		objectHeight=heightn;
		setCenter(x, y);
	}
	
	public GameObj(PApplet p, float x, float y,float widthn,float heightn)
	{
		//p=p_;
		ID= this.hashCode();
		worldWidth=p.width;
		worldHeight=p.height;
		
		objectWidth=widthn;
		objectHeight=heightn;
		setCenter(x, y);
		
		
	}
	public EventHandler extractEvent()
	{
		if(objectEvent!=null)
		{	
			EventHandler te= objectEvent;
			objectEvent=null;
			return te;
		}
		return null;
	}
	public void setLocalTimeline(TimeLine t)
	{
		localTimeLine=t;
	}
	public void setGlobalTimeLine(TimeLine t)
	{
		GlobalTimeLine=t;
	}
	
	public String toString()
	{
		String s="GameObj; ID"+ID+" ;posX "+position[0]+" ;posY "+position[1];
		return s; 
	}
	public void setIdent(int a)
	{
		ident=a;
	}
	public void setIDclient(int i)
	{
		IDclient=i;
	}
	public void enableMovable()
	{
		moveComponent= new Movable(this, 2, 2);
		//componentList.add(moveComponent);
		//componentList.getFirst().update();
	}
	public void enableDrawable()
	{
		drawableComponent= new Drawable(this);
	}
	public void enableCollidable()
	{
		collidableComponent= new Collidable(this);
	}
	public void enableCollidableAsDeadZone()
	{
		collidableasdeadzone= new CollideAsDeadZone(this);
	}
	public void enableCollidableWithEverything()
	{
		if(collidableComponent==null)
		collidableComponent= new Collidable(this);
		
		collidableComponent.enableBoundaries(true, true, true, true);
		collidableComponent.enableCollidableBeweenObjects();
	}
	public void enableCollidableWithBoundaries()
	{
		if(collidableComponent==null)
		collidableComponent= new Collidable(this);
		
		collidableComponent.enableBoundaries(true, true, true, true);
	}
	public void enableCollidablewithOtherObject()
	{
		if(collidableComponent==null)
		collidableComponent= new Collidable(this);
		
		collidableComponent.enableCollidableBeweenObjects();
	}
	public void setCenter(float x, float y)
	{
		position[0]=x;
		position[1]=y;
	}
	public void setColor(int r, int g, int b)
	{
		color= new Color(r,g,b);
	}
	public void updateObject()
	{
		try {
			moveComponent.update();
		} catch (Exception e) {
			// TODO: handle exception
			//System.out.println("Move Component not seted");
		}
		try {
			collidableComponent.update();
		} catch (Exception e) {
			// TODO: handle exception
			//System.out.println("Collide Component not seted");
		}
		try {
			drawableComponent.update();
		} catch (Exception e) {
			// TODO: handle exception
			//System.out.println("Draw Component not seted");
		}
		
		
	}
	
	public void updateInServer()
	{
		try {
			moveComponent.update();
		} catch (Exception e) {
			// TODO: handle exception
			//System.out.println("Move Component not seted");
		}
		try {
			collidableComponent.update();
		} catch (Exception e) {
			// TODO: handle exception
			//System.out.println("Collide Component not seted");
		}
		try {
			collidableasdeadzone.update();
		} catch (Exception e) {
			// TODO: handle exception
			//System.out.println("Collide Component not seted");
		}
	}
	public void updateInClient(PApplet p)
	{
		try {
			drawableComponent.drawUpdate(p);
		} catch (Exception e) {
			// TODO: handle exception
			//System.out.println("Draw Component not seted");
		}
	}
	
	
}
