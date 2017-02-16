package elements.com;
import java.io.Serializable;

import processing.core.PApplet;


public class Drawable implements Component, Serializable{
	GameObj gameobject;
	//PApplet p;
	public Drawable(GameObj a) {
		// TODO Auto-generated constructor stub
		gameobject=a;
		//p=p_;
	}
	public void setPApplet(PApplet p_)
	{
		//p=p_;
	}

	@Override
	public void update() {
		// TODO Auto-generated method stub
		//p.rectMode(p.CENTER);
		//p.fill(gameobject.color.getRed(),gameobject.color.getGreen(),gameobject.color.getBlue());
		//p.rect(gameobject.position[0],gameobject.position[1],gameobject.objectWidth,gameobject.objectHeight);
	}
	public void drawUpdate(PApplet p_)
	{
		p_.rectMode(p_.CENTER);
		p_.fill(gameobject.color.getRed(),gameobject.color.getGreen(),gameobject.color.getBlue());
		p_.rect(gameobject.position[0],gameobject.position[1],gameobject.objectWidth,gameobject.objectHeight);
	}

}
