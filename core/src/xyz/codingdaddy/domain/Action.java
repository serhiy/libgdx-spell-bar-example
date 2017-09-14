package xyz.codingdaddy.domain;

/**
 * Represents an action to be executed on determined event.
 * 
 * @author serhiy
 */
public class Action {
	
	/**
	 * @param value to be printed on the screen when the action is triggered.
	 */
	public void execute(String value) {
		System.out.println("Executed action: " + value);
	}
}
