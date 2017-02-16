package elements.com;
import java.io.Serializable;

/*  
 * BIBLIOGRAPHY:
[1]creating dialogs in the client recording system: 
https://docs.oracle.com/javase/tutorial/uiswing/components/dialog.html
[2] Enumeration for the EventTypesEnum was based on: 
https://docs.oracle.com/javase/tutorial/java/javaOO/enum.html
[3] the server monitor is based in the answer from stack overflow forum: 
http://stackoverflow.com/questions/289434/how-to-make-a-java-thread-wait-for-another-threads-output
 * */
public enum EventTypesEnum implements Serializable{
	KEYBOARD_PRESS_LEFT, 
	KEYBOARD_PRESS_RIGHT, 
	KEYBOARD_PRESS_DOWN, 
	KEYBOARD_PRESS_UP ,
	KEYBOARD_PRESS_SPACEBAR
	,
	COLLIDE_WITH_OTHER_PLAYER,
	COLLIDE_WITH_BOUNDARIES,
	
	AUTOMATIC_PATTERN
	
}
