package metalGear;

import explorer.CaveRoom;

public class SisiTutorialRoom extends CaveRoom {

	public SisiTutorialRoom(String description) {
		super(description);
	}
	
	public String validKeys() {
		return "wdsae";
	}

	public void printAllowedEntry() {
		System.out.println("You can only enter 'w', 'a', 's' 'd', or 'e'.");
	}
	
	/** briefs player**/
	public void performAction (int direction) {
		BenTempMain.print(""+direction);
		if (direction==4) {
			BenTempMain.dialouge("Snake... seems you need a refresher on things.../"
					+ "You can walk around each level by using the wasd keys.../="
					+ "There will be guards represented by arrows in every level/"
					+ "Guards move every other turn, so take advantage of that/"
					+ "Their field of view is right in front of them, /"
					+ "2 spaces forward, and to the left and right of 2 spaces forward/"
					+ "When a Guard sees a camera or a dead body, they'll get rid of it and be alerted/"
					+ "You can kill guards by walking up to them while you are behind them/"
					+ "Make sure you dont get spotted though or else its GAME OVER.../="
					+ "Dead guards look like: 'D'/"
					+ "You can pick a dead guard up by walking into them./"
					+ "Put down a picked up guard with the 'g' key...="
					+ "Cameras allow you to see more of a level./"
					+ "Use the 'c' key to place a camera./="
					+ "Now lets get you back out there...");
			
			
			
			/*
			 * An arrow means they're alive, a G means they're dead
Guards move every other turn, so take advantage of that
Their field of view is right in front of them, 2 spaces forward, and to the left and right of 2 spaces forward
You kill Guards by walking into them out of their FOV
If you move in their FOV, it's game over
You can tell which way a guard is looking by the direction of the arrow
Guards can't see through walls, so take advantage
When a Guard sees a camera or a dead body, they'll get rid of it and be alerted
Careful! When a guard is alerted, their FOV increases, and they see both in front of and behind them

			 * */
			
		}else {
			System.err.println("Invalid input.");
		}
	}

}
