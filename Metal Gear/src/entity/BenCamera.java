package entity;

import metalGear.BenTempMain;

public class BenCamera extends BenThing{
	private boolean cameraPlaced;// keeps track of whether the camera has been placed and thus needs to be rendered
	
	//CAMERA: temporarily moves the player to its coordinates for the purposes of rendering
	
	public BenCamera(int r, int c) {
		super(r, c);
	}
	
	public int getR() {
		return this.r;
	}
	public int getC() {
		return this.c;
	}
	
	//breaks on interact
	public void interact() {
		cameraPlaced = false;
		BenTempMain.breakWall(this.r,this.c);
		this.r = -1;
		this.c = -1;
	}
	
	public String toString() {
		return "C";
	}
	
	//places camera in olvl
	public void placeCamera(int r, int c){
		cameraPlaced = true;
		
		this.r = r;
		this.c = c;
		
		BenTempMain.olvl[this.r][this.c] = this;
	}
	
	public boolean isCameraPlaced() {
		return cameraPlaced;
	}
	
	/**
	 * Pass in each Guard
	 * If camera is within any Guard's FOV, this returns true
	 * When true, remove the camera from the map
	 * @param g
	 * @return
	 */
	public boolean seenByGuard(SisiGuard g) {
		for(int i = 0; i < g.getFieldOfView().length; i++) {
			if(g.getFieldOfView()[i][0] == this.r && g.getFieldOfView()[i][1] == this.c) {
				return true;
			}
		}
		return false;
	}

}
