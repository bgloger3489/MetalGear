package entity;

import explorer.tempMain;

public class Guard extends Thing {
	
	//constants
	public static final int NORTH = 0;
	public static final int EAST = 1;
	public static final int SOUTH = 2;
	public static final int WEST = 3;
	public static final int[] DIRECTIONS = {-1, 1, 1, -1}; //-1 means up or left; 1 means right or down
	public static final String[] ICONS = {"▲", "►", "▼", "◄"}; //Guard front-end icons
	
	//fields relating to navigation
	private int currentRow;
	private int currentCol;
	private int[][] path; //Every coordinate in which the Guard will go to is stored here
	private int direction;
	private int currentPos; //The current index of path
	
	//fields relating to character
	private int[][] fieldOfView;
	private boolean isAlive; //When false, the Guard doesn't do anything and can be picked up and put down by Player
	private boolean active; //When false, the Guard doesn't move
	private boolean alerted; //When true, the Guard's FOV increases
	
	/**
	 * Constructor
	 * NOTE: The field path and the parameter path are different
	 * The field stores every coordinate in which the Guard with go to
	 * The parameter path is used to make this.path, and stores the directions for the Guard relative to starting position
	 * E.g. this.path would be {{3,3},{3,2}} while the parameter path would pass in {{0,-1},{0,1}}
	 * @param path
	 * @param row
	 * @param col
	 */
	public Guard(int[][] path, int row, int col) {
		
		super(row, col);
		this.currentRow = row;
		this.currentCol = col;
		this.path = new int[path.length][2];
		
		//Creates Guard's path from instructions given when instantiated
		for(int i = 0; i < path.length; i++) {
			
			currentRow += path[i][0];
			currentCol += path[i][1];
			this.path[i][0] = currentRow;
			this.path[i][1] = currentCol;
			
		}
		
		this.currentPos = path.length - 1;
		setDirection();
		setFieldOfView();
		this.isAlive = true;

	}
	
	/**
	 * To be used in conjunction with the Player class
	 * Kills the Guard when it is alive
	 * Picks up the Guard when it is dead
	 */
	public void interact() {
		
		if(isAlive) {
			tempMain.print("The guard has been killed");
			kill();
		} else {
			
			if(tempMain.p.pickedUpGuard()) {
				tempMain.dialouge("Snake, you can't pick up 2 dead gaurds!");
			} else {
				tempMain.breakWall(this.currentRow,this.currentCol);
				this.currentRow = -1;
				this.currentCol = -1;
				tempMain.p.pickUpGuard(true);
			}
			
		}
		
	}
	
	/**
	 * Updates Guard's visuals on the map
	 * Moves the Guard, sets its coordinates, sets its FOV
	 * Updates instructions for Guard path
	 */
	public void act() {
		
		//Remove Guard from level map
		tempMain.breakWall(currentRow,currentCol);
		
		if(isAlive) {
	
			if(active) {
				
				move(currentPos);
				setDirection();
				setFieldOfView();
				currentPos++; //Sets currentPos to the next position of the Guard
				
				//If at the last position of the path, reset back to the first one
				if(currentPos == path.length) {
					currentPos = 0;
				}
				
			}
			
			//If it's active or inactive, set it to the opposite (effectively makes Guard move every OTHER turn)
			active = !active; 
		
		}
		
		//Place Guard in level map
		tempMain.olvl[currentRow][currentCol] = this;

	}
	
	/**
	 * Sets the Guard's coordinates
	 * @param row
	 * @param col
	 */
	public void setPosition(int row, int col) {
		
		currentRow = row;
		currentCol = col;
		
	}
	
	/**
	 * Sets the Guard's coordinates to the next coordinate pair in path
	 * @param pos
	 */
	public void move(int pos) {
		
		setPosition(path[pos][0], path[pos][1]);
		
	}
	
	/**
	 * Determines which way the guard is facing
	 */
	public void setDirection() {
		
		if(currentPos > 0) {
			
			if(currentRow < path[currentPos - 1][0]) {
				direction = NORTH;
			} else if(currentRow > path[currentPos - 1][0]) {
				direction = SOUTH;
			} else if(currentRow == path[currentPos - 1][0]) {
				direction = (int)(Math.random() * 4);
			} else if(currentCol < path[currentPos - 1][1]) {
				direction = WEST;
			} else if(currentCol > path[currentPos - 1][1]) {
				direction = EAST;
			} else if(currentCol == path[currentPos - 1][1]) {
				direction = (int)(Math.random() * 4);
			}
		
		} else {
			
			if(path[currentPos][0] > path[currentPos + 1][0]) {
				direction = NORTH;
			} else if(path[currentPos][0] < path[currentPos + 1][0]) {
				direction = SOUTH;
			} else if(path[currentPos][0] == path[currentPos + 1][0]) {
				direction = (int)(Math.random() * 4);
			} else if(path[currentPos][1] > path[currentPos + 1][1]) {
				direction = WEST;
			} else if(path[currentPos][1] < path[currentPos + 1][1]) {
				direction = EAST;
			} else if(path[currentPos][1] == path[currentPos + 1][1]) {
				direction = (int)(Math.random() * 4);
			}
			
		}
		
	}
	
	/**
	 * Sets the Guard's field of view based on direction faced
	 * The FOV of a Guard is always its current coordinates, 1 space forward, 2 spaces forward, and 2 spaces forward to the left/right
	 * When alerted, FOV of the Guard becomes the above coordinates AND the same thing behind the Guard
	 */
	public void setFieldOfView() {
		
		if(!alerted) {
			
			fieldOfView = new int[5][2];
			
			if(direction == NORTH || direction == SOUTH) {
				
				fieldOfView[0][0] = currentRow + DIRECTIONS[direction];
				fieldOfView[1][0] = fieldOfView[0][0] + DIRECTIONS[direction];
				fieldOfView[2][0] = fieldOfView[1][0];
				fieldOfView[3][0] = fieldOfView[1][0];
				fieldOfView[0][1] = currentCol;
				fieldOfView[1][1] = currentCol;
				fieldOfView[2][1] = currentCol + 1;
				fieldOfView[3][1] = currentCol - 1;
				
			
			}
			
			if(direction == EAST || direction == WEST) {
				
				fieldOfView[0][1] = currentCol + DIRECTIONS[direction];
				fieldOfView[1][1] = fieldOfView[0][1] + DIRECTIONS[direction];
				fieldOfView[2][1] = fieldOfView[1][1];
				fieldOfView[3][1] = fieldOfView[1][1];
				fieldOfView[0][0] = currentRow;
				fieldOfView[1][0] = currentRow;
				fieldOfView[2][0] = currentRow + 1;
				fieldOfView[3][0] = currentRow - 1;
			
			}
			
			fieldOfView[4][0] = currentRow;
			fieldOfView[4][1] = currentCol;
	
		} else {
			
			fieldOfView = new int[9][2];
			
			if(direction == NORTH || direction == SOUTH) {
				
				for(int i = 0; i < 2; i++) {
					
					fieldOfView[0][0] = currentRow + DIRECTIONS[i];
					fieldOfView[1][0] = fieldOfView[0][0] + DIRECTIONS[i];
					fieldOfView[2][0] = fieldOfView[1][0];
					fieldOfView[3][0] = fieldOfView[1][0];
					fieldOfView[0][1] = currentCol;
					fieldOfView[1][1] = currentCol;
					fieldOfView[2][1] = currentCol + 1;
					fieldOfView[3][1] = currentCol - 1;
				
				}
				
			
			}
			
			if(direction == EAST || direction == WEST) {
				
				for(int i = 2; i < 4; i++) {
					
					fieldOfView[0][1] = currentCol + DIRECTIONS[i];
					fieldOfView[1][1] = fieldOfView[0][1] + DIRECTIONS[i];
					fieldOfView[2][1] = fieldOfView[1][1];
					fieldOfView[3][1] = fieldOfView[1][1];
					fieldOfView[0][0] = currentRow;
					fieldOfView[1][0] = currentRow;
					fieldOfView[2][0] = currentRow + 1;
					fieldOfView[3][0] = currentRow - 1;
				
				}
			
			}
			
			fieldOfView[8][0] = currentRow;
			fieldOfView[8][1] = currentCol;
			
		}
		
		for(int i = 0; i < fieldOfView.length; i++) {
			
			if(tempMain.olvl[0][0] instanceof Wall) {
				fieldOfView[i][0] = currentRow;
				fieldOfView[i][1] = currentCol;
			}
			
		}
		
	}
	
	/**
	 * Returns the Guard's FOV
	 * @return
	 */
	public int[][] getFieldOfView() {
		
		return fieldOfView;
		
	}
	
	/**
	 * Returns the Guard's current row
	 * @return
	 */
	public int getRow() {
		
		return currentRow;
		
	}
	
	/**
	 * Return's the Guard's current column
	 * @return
	 */
	public int getColumn() {
		
		return currentCol;
		
	}
	
	/**
	 * Returns the current direction in which the Guard is facing
	 * @return
	 */
	public int getDirection() {
		
		return direction;
		
	}
	
	/**
	 * Returns the current coordinates of the Guard
	 * @return
	 */
	public int[] getPosition() {
		
		return path[currentPos];
		
			
	}
	
	/**
	 * Returns the coordinates of the next position of the Guard
	 * @return
	 */
	public int[] getNextPosition() {
		
		if(currentPos == path.length - 1) {
			return path[0];
		}
		
		return path[currentPos + 1];
			
	}
	
	/**
	 * Sets alerted to true (effectively alerts the Guard)
	 */
	public void alert() {
		
		alerted = true;
		
	}
	
	/**
	 * Returns whether or not the Guard is alive
	 * @return
	 */
	public boolean isAlive() {
	
		return isAlive;
		
	}
	
	/**
	 * Sets isAlive to false (effectively kills the Guard)
	 */
	public void kill() {
		
		isAlive = false;
		
	}
	
	/**
	 * Sets the Guard's front-end visuals
	 * If alive, the Guard's icon is an arrow depicting the direction in which it is facing
	 * If dead, the Guard's icon is a 'G'
	 */
	public String toString() {
		
		if(isAlive) {
			return ICONS[direction];
		} else {
			return "G";
		}
		
	}

}