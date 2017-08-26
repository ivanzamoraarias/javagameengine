package test.com;

import processing.core.PApplet;

public class testProcessing  extends PApplet {
	public void setup() 
	{
		 size(480, 120);
	}
	public void draw() 
	{
		if (mousePressed) {
		    fill(0);
		  } else {
		    fill(255);
		  }
		  ellipse(mouseX, mouseY, 80, 80);
	}
}
