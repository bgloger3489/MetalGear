package caves;

import entity.Guard;

public class CaveRoom {

	private String description;//tells what the room looks like
	private String directions;//tells what you can do
	private String contents;//a symbol representing what's in the room
	private String defaultContents;
	//the rooms are organize by direction, 'null' signifies no room/door in that direction
	private CaveRoom[] borderingRooms;
	private Door[] doors;

	//constants
	public static final int NORTH = 0;
	public static final int EAST = 1;
	public static final int SOUTH =2;
	public static final int WEST = 3;
	public static final int[] UP = {-1, 0};
	public static final int[] DOWN = {1, 0};
	public static final int[] LEFT = {0, -1};
	public static final int[] RIGHT = {0, 1};


	public CaveRoom(String description) {
		this.description = description;
		setDefaultContents(" ");
		contents = defaultContents;
		//difference between defaultContents and contents is "contents" becaomes an 'X' when you are
		//inside this room, when you leave, it goes back to defaultContents

		//note: by default, arrays will populate with 'null' meaning there are no connections
		borderingRooms = new CaveRoom[4];
		doors = new Door[4];
		setDirections();
	}


	/**
	 * for every door in doors[] appends a String to "directions" describing the access.
	 * For example:
	 *  "There is a door to the north"
	 *  "There is a door to the south"...etc
	 *  
	 * If there are no doors at all, directions should say:
	 *    "There are no doors, you are trapped in here."
	 */
	public void setDirections() {
		directions = "";
		boolean doorFound = false;
		for(int i =0; i < doors.length; i++) {
			if(doors[i] != null) {
				doorFound = true;
				directions += "\n   There is a "+doors[i].getDescription() + " to " +
						toDirection(i)+". "+doors[i].getDetails();
			}
		}
		if(!doorFound) {
			directions += "There is no way out. You are trapped in here.";
		}
	}

	/**
	 * converts an int to a direction
	 * 	toDirection(0) -> "the North"
	 * etc
	 * @param dir
	 * @return
	 */
	public static String toDirection(int dir) {
		String[] direction = {"the North", "the East", "the South","the West"};
		return direction[dir];
	}


	public void enter() {
		contents = "P";
	}

	public void leave() {
		contents = defaultContents;
	}

	/**
	 * Gives this room access to anotherRoom (and vice-versa)
	 * and sets a door between them, updating the directions
	 * @param direction
	 * @param anotherRoom
	 * @param door
	 */
	public void setConnection(int direction, CaveRoom anotherRoom, 
			Door door) {
		addRoom(direction, anotherRoom, door);
		anotherRoom.addRoom(oppositeDirection(direction), this, door);
	}


	public static int oppositeDirection(int direction) {
		return (direction + 2)%4;
	}


	public void addRoom(int direction, CaveRoom cave, Door door) {
		borderingRooms[direction] = cave;
		doors[direction] = door;
		setDirections();
	}

	public void interpretInput(String input) {
		System.out.println("interpretting input "+input);
		while(!isValid(input)) {
			printAllowedEntry();
			input = ExplorerMain.in.nextLine();
		}
		System.out.println("marked as valid: "+input);
		//task: convert user input into a direction
		//DO NOT USE AN IF STATEMENT
		//(or, if you must, don't use more than 1)
		String dirs = validKeys();
		respondToKey(dirs.indexOf(input));
	}

	/**
	 * override to add more keys, but always include 'wdsa'
	 * @return
	 */
	public String validKeys() {
		return "wdsae";
	}

	/**
	 * override to print a custom string describing what keys do
	 */
	public void printAllowedEntry() {
		System.out.println("You can only enter 'w', 'a', 's' or 'd'.");
	}


	private boolean isValid(String input) {
		String validEntries = validKeys();
		return validEntries.indexOf(input) > -1 && input.length() ==1;
	}


	private void respondToKey(int direction) {
		//first, protect against null pointer exception
		//(user cannot go through non-existent door
		if(direction < 4) {
			if(borderingRooms[direction] != null && 
					doors[direction] != null) {
				ExplorerMain.currentRoom.leave();
				ExplorerMain.currentRoom = borderingRooms[direction];
				ExplorerMain.currentRoom.enter();
				ExplorerMain.inventory.updateMap();
			}
		}else {
			performAction(direction);
		}
	}

	/**
	 * override to give response to keys other than wasd
	 * @param direction
	 */
	public void performAction(int direction) {
		
		//5 represents "C" <- addded to valid keys
		if(direction == 5)
			ExplorerMain.inventory.subCoffee();
		else
			System.out.println("That key does nothing.");

	}


	/**
	 * This will be where your group sets up all the caves 
	 * and all the connections
	 */
	public static void setUpCaves(int x, int y) {
		//ALL OF THIS CODE CAN BE CHANGED
		//1. Decide how big your caves should be
		ExplorerMain.caves = new CaveRoom[x][y];
		//2. Populate with caves and a defualt description: hint: when starting, use coordinates (helps debugging)
		for(int row = 0; row < ExplorerMain.caves.length; row++) {
			//PLEASE PAY ATTENTION TO THE DIFFERENCE:
			for(int col = 0; col < ExplorerMain.caves[row].length; col++) {
				//create a "default" cave
				ExplorerMain.caves[row][col] = 
						new NPCRoom("This cave has coords ("+row+","+col+")");
			}
		}
		//3. Replace default rooms with custom rooms

		//--- WE WILL DO LATER
		
		
		
		ExplorerMain.npcs = new NPC[1];

		ExplorerMain.caves[4][3] = new SisiSavageRoom("What up, it's yer boy Sisi. Talk to the guy for a challenge. Oh yeah, and we're in (4, 3).");
		ExplorerMain.caves[0][4].setDescription("Teleportation. Pretty cool huh??? You're in (0, 4). By the way, you might be stuck...");
		ExplorerMain.caves[1][4].setDescription("You're in (1, 4). By the way, you might be stuck...");
		ExplorerMain.caves[2][4].setDescription("You're in (2, 4). By the way, you might be stuck...");
		
		ExplorerMain.npcs = new NPC[2];

		ExplorerMain.npcs[0] = new Guard("Patrol", new int[][] {DOWN, DOWN, UP, UP}, 0, 1);
		ExplorerMain.npcs[0].setPosition(1, 1);

		ExplorerMain.npcs[1] = new SisiSavage("Jimmy");
		ExplorerMain.npcs[1].setPosition(4, 3);


		//4. Set your starting room:
		ExplorerMain.currentRoom = ExplorerMain.caves[0][1];
		ExplorerMain.currentRoom.enter();
		//5. Set up doors
		CaveRoom[][] c = ExplorerMain.caves;
		c[0][1].setConnection(SOUTH, c[1][1], new Door());

		
		c[1][1].setConnection(EAST, c[1][2], new Door());
		
		
		
		// = new metalGear.BensCafe("Temop");
		
		CaveRoom cafe = new caves.customcaves.BensCafe("");//metalGear.BensCafe("temp");
		
		//System.out.print(cafe instanceof CaveRoom);
		
		c[0][2] = cafe;
		
		
		
		
		c[0][1].setConnection(EAST, c[0][2], new Door());
		

		c[1][1].setConnection(SOUTH, c[2][1], new Door());
		c[2][1].setConnection(EAST, c[2][2], new Door());
		c[2][2].setConnection(SOUTH, c[3][2], new Door());
		c[3][2].setConnection(SOUTH, c[4][2], new Door());
		c[4][2].setConnection(EAST, c[4][3], new Door());
		
		c[0][4].setConnection(SOUTH, c[1][4], new Door());
		c[1][4].setConnection(SOUTH, c[2][4], new Door());

		CaveRoom sidCasino = new caves.customcaves.SidCasino("Asdasd"); 
		
		c[0][0] = sidCasino;
		
		c[0][0].setConnection(EAST, c[0][1], new Door());
		
		
		
		/**
		 * Special requests:
		 * moving objects in caves
		 * what happens when you lose?
		 * can another object move toward you?
		 */

	}


	public String getDescription() {
		return description + "\n"+directions;
	}


	public void setDescription(String description) {
		this.description = description;
	}


	public String getContents() {
		return contents;
	}


	public void setContents(String contents) {
		this.contents = contents;
	}


	public void setDefaultContents(String defaultContents) {
		this.defaultContents = defaultContents;
	}


	public Door getDoor(int direction) {
		if (direction >=0 && direction < doors.length) {
			return doors[direction];
		}else {
			return null;
		}
	}



}
