package explorer;

import java.util.Arrays;
import java.util.Scanner;

import entity.BreakableWall;
import entity.Camera;
import entity.Guard;
import entity.Thing;
import entity.Wall;
import metalGear.CaveExplorer;

public class tempMain {
	
	public static boolean playing;
	
	private static int[][] lvl; // level represented by ints
	public static Thing[][] olvl; // actually level; made of objects
	private static Scanner in;
	

	public static entity.Player p; // Player is represented by p
	public static Camera c; // Camera is represented by c
	public static Guard[] g; // Array of all guards
	
	
	public static void playLevel() {
		//print("lvl:" + CaveExplorer.currentlvl);
		if(CaveExplorer.currentlvl == 0) {
			playBen();
		}else if(CaveExplorer.currentlvl == 1) {
			playSisi();
		}else {
			print("there is currently no lvl 2");
		}
	}
	
	
	
	public static void playBen() {
		playing = true;
		
		in = new Scanner(System.in);
		
		p = new entity.Player(Benevel.PLAYERSPAWN[0], Benevel.PLAYERSPAWN[1]);
		c = new Camera(-1,-1);
		Benevel.createGuards();
		g = Benevel.GUARDS;
		
		lvl = Benevel.LEVEL;
		convertLevel();
		
		for(int i = 0; i < g.length; i++) {
			olvl[g[i].getRow()][g[i].getColumn()] = g[i];
		}
		
		print("\n\n\n");
		
		playGame();
	}
	
	public static void playSisi() {
		
		playing = true;
		
		in = new Scanner(System.in);
		
		p = new entity.Player(SisiLevel.PLAYERSPAWN[0], SisiLevel.PLAYERSPAWN[1]);
		c = new Camera(-1,-1);
		SisiLevel.createGuards();
		g = SisiLevel.GUARDS;
		
		lvl = SisiLevel.LEVEL;
		convertLevel();
		
		for(int i = 0; i < g.length; i++) {
			olvl[g[i].getRow()][g[i].getColumn()] = g[i];
		}
		
		print("\n\n\n");
		
		playGame();
	}
	

	public static void playGame() {
		int psn;
		int dirFacing;
		int[] convertedDir;
		String[][] render;
		
		int tempPlayerR;
		int tempPlayerC;
		
		while(playing) {
			
			//DISPLAY:
			render = new String[olvl.length][olvl[0].length];
			
			for (String[] row: render)
			    Arrays.fill(row, ".");
			
			render = rayCast(render);
			
			if(c.isCameraPlaced()) {
				//temporarily move player to camer locastion for raycast;
				tempPlayerR = p.getR();
				tempPlayerC = p.getC();
				p.rayMove(c.getR(),c.getC());
				render = rayCast(render);
				p.rayMove(tempPlayerR,tempPlayerC);
				render[c.getR()][c.getC()] = "C";
				
			}
			
			render[p.getR()][p.getC()] = "X";
			
			
			displayRender(render);
			
			
			
			
			
//INPUT:
			
			//if the camera is placed, and player can drop gaurd
			if(c.isCameraPlaced() && p.pickedUpGuard()) {
				psn = getInput("wasdg");
			//if the camera is placed, and player cant drop gaurd
			}else if(c.isCameraPlaced()) {
				psn = getInput("wasd");
			//if camera is not placed and player can drop gaurd
			}else if(p.pickedUpGuard()){
				psn = getInput("wasdgc");
			//if camera is not placed and player cant drop gaurd
			}else {
				psn = getInput("wasdwc");	
			}
			
			//place camera button pushed
			if(psn == 5) {
				dialouge("select direction for camera");
				dirFacing = getInput("wasd");
				convertedDir = convertDir(dirFacing);
				while(!olvl[p.getR() + convertedDir[0]][p.getC() + convertedDir[1]].toString().equals(" ")) {
					print("invalid input");
					dialouge("select direction for camera");
					dirFacing = getInput("wasd");
					convertedDir = convertDir(dirFacing);
				}
				//olvl[p.getR() + convertedDir[0]][p.getC() + convertedDir[1]] = c;
				c.placeCamera(p.getR() + convertedDir[0],p.getC() + convertedDir[1]);
			}
			
			
			
			//gaurd button pushed
			if(psn==4) {
				
				dialouge("select direction for gaurd");
				dirFacing = getInput("wasd");
				convertedDir = convertDir(dirFacing);
				while(!olvl[p.getR() + convertedDir[0]][p.getC() + convertedDir[1]].toString().equals(" ")) {
					print("invalidd input");
					dialouge("select direction for gaurd");
					dirFacing = getInput("wasd");
					convertedDir = convertDir(dirFacing);
				}
				
				//print(olvl[p.getR() + convertedDir[0]][p.getC() + convertedDir[1]].toString());
				//p.placeGuard(p.getR() + convertedDir[0],p.getC() + convertedDir[1]);
				
				olvl[p.getR() + convertedDir[0]][p.getC() + convertedDir[1]] = p.getCurrentGuard();
				p.getCurrentGuard().currentRow = p.getR() + convertedDir[0];
				p.getCurrentGuard().currentCol = p.getC() + convertedDir[1];
				
				
				p.setCurrentGuard(null);
				p.pickUpGuard(false);
				
			}
			
			
			convertedDir = convertDir(psn);

			updateOlvlPlayer();
			olvl[p.getR() + convertedDir[0]][p.getC() + convertedDir[1]].interact();
			print(olvl[4][5].toString());
			
			for(int i = 0; i < g.length; i++) {
				
				if(g[i].isAlive()) {
					g[i].act();
				}
				
			}
			
			
			//IF PLAYER IS IN GAURD FEILD POF VIEW:
			//make a double for loop
				//for each guard
					//for each set of coords within gaurds FOW
						//check if player.getR & getC are in
							//if so -> game over: DISPLAY GAME OVER
							//RELOAD GAME by calling playLevel
						
			
		}
	}
	
	/** gets user input with input of possible inputs**/
	public static int getInput(String possibilities) {
		
		int psn;
		String input = in.nextLine();
		psn = possibilities.indexOf(input);
		while(psn == -1) {
			psn = possibilities.indexOf(input);
			print("invalid input");
			input = in.nextLine();
		}
		return psn;
	}
	
	/** quick hand for System.out.println**/
	public static void print(String s) {System.out.println(s);}
	
	/** USED FOR RENDERING: Get coordinates of all border walls**/
	public static int[][] getBorderCoords(){
		int[][] borderCoords = new int[olvl.length*2+olvl[0].length*2][2];
		
		//for all rows
		int tempCount = 0;
		for(int i = 0; i < olvl.length; i+=olvl.length-1) {
			for(int j = 0; j < olvl[0].length; j++) {
				borderCoords[tempCount][0] = i;
				borderCoords[tempCount][1] = j;
				//System.out.println(i + " " + j);
				tempCount++;
			}
		}
		//for all cols
		for(int i = 0; i < olvl[0].length; i+=olvl[0].length-1) {
			for(int j = 0; j < olvl.length; j++) {
				borderCoords[tempCount][0] = j;
				borderCoords[tempCount][1] = i;
				//System.out.println(j + " " + i);
				tempCount++;
			}
		}
		return borderCoords;
	}
	
	/** USED FOR RENDERING: Get slopes of all lines from player to each border coord **/
	public static double[] getSlopes(int[][] borderCoords) {
		double[] slopes = new double[borderCoords.length];
		
		//print("pRow"+p.getR());
		//print("pCol"+p.getC());
		
		for(int i = 0; i < borderCoords.length; i++) {
			//m = y/x
			//print(borderCoords[i][0] + " ghgu" + borderCoords[i][1]);
			
			slopes[i] = (double) (borderCoords[i][0]-p.getR())/ (double) (borderCoords[i][1]-p.getC());
			
			//System.out.println(slopes[i]);
		}
		return slopes;
	}
	
	/** USED FOR RENDERING: RAY CAST**/
	public static String[][] updateTopSteep(String[][] render,double slope){
		double checkC;
		//go row by row, decreasing, to get cols
		for(int u = p.getR(); u >= 0; u--) {
			checkC = p.getC()-((p.getR()-u)/slope); 

			//if checkC is an integer:
			if ((checkC == Math.floor(checkC)) && !Double.isInfinite(checkC)) {
				render[u][(int) checkC] = olvl[u][(int) checkC].toString();
			    if(olvl[u][(int) checkC] instanceof Wall) {
			    		
			    		
			    		olvl[u][(int) checkC].makeDiscovered();
			    		break;
			    }
			 //else check the boxes to left and right
			}else {
				
				render[u][(int) checkC] = olvl[u][(int) checkC].toString();
				if(olvl[u][(int) checkC] instanceof Wall) {
			    		
			    		olvl[u][(int) checkC].makeDiscovered();
			    		break;
			    }
				render[u][(int) checkC+1] = olvl[u][(int) checkC+1].toString();
				if(olvl[u][(int) checkC+1] instanceof Wall) {
		    			
		    			olvl[u][(int) checkC].makeDiscovered();
		    			break;
				}
			}
		
		}
		return render;
	}
	
	/** USED FOR RENDERING: RAY CAST**/
	public static String[][] updateTopShallow(String[][] render, double slope){
		double checkR;
		
		//go col by col, to get rows
		
		//for increasing col
		if(slope < 0) {
			for(int u = p.getC(); u < olvl[0].length; u++) {
				checkR = p.getR()- (p.getC()-u)*slope;
				//print("slopee: "+ slope);
				
				//if checkC is an integer:
				if ((checkR == Math.floor(checkR)) && !Double.isInfinite(checkR)) {
					render[(int) checkR][u] = olvl[(int) checkR][u].toString();
				    if(olvl[(int) checkR][u] instanceof Wall) {
				    		
				    		olvl[(int) checkR][u].makeDiscovered();
				    		break;
				    }
				 //else check the boxes to left and right
				}else {
					render[(int) checkR][u] = olvl[(int) checkR][u].toString();	
					if(olvl[(int) checkR][u] instanceof Wall) {
				    		
				    		olvl[(int) checkR][u].makeDiscovered();
				    		break;
				    }
					render[(int) checkR+1][u] = olvl[(int) checkR][u].toString();
					if(olvl[(int) checkR+1][u] instanceof Wall) {
			    			
			    			olvl[(int) checkR][u].makeDiscovered();
			    			break;
					}
				}
			}
		
		//for decreasing col
		}else {
			for(int u = p.getC(); u >= 0; u--) {
				checkR = p.getR()- (p.getC()-u)*slope;
				//print("slopee: "+ slope);
				
				//if checkC is an integer:
				if ((checkR == Math.floor(checkR)) && !Double.isInfinite(checkR)) {
					render[(int) checkR][u] = olvl[(int) checkR][u].toString();
				    if(olvl[(int) checkR][u] instanceof Wall) {
				    		
				    		olvl[(int) checkR][u].makeDiscovered();
				    		break;
				    }
				 //else check the boxes to left and right
				}else {
					render[(int) checkR][u] = olvl[(int) checkR][u].toString();
					if(olvl[(int) checkR][u] instanceof Wall) {
				    		
				    		olvl[(int) checkR][u].makeDiscovered();
				    		break;
				    }
					render[(int) checkR+1][u] = olvl[(int) checkR+1][u].toString();
					if(olvl[(int) checkR+1][u] instanceof Wall) {
			    			
			    			olvl[(int) checkR][u].makeDiscovered();
			    			break;
					}
				}
			}
		}
		return render;
	}
	
	/** USED FOR RENDERING: RAY CAST**/
	public static String[][] updateBottomSteep(String[][] render, double slope){
		double checkC;
		for(int u = p.getR(); u < olvl.length; u++ ) {
			checkC = p.getC()-((p.getR()-u)/slope);
			//print("slope: "+ slope);
			//if checkC is an integer:
			if ((checkC == Math.floor(checkC)) && !Double.isInfinite(checkC)) {
				render[u][(int) checkC] = olvl[u][(int) checkC].toString();
			    if(olvl[u][(int) checkC] instanceof Wall) {
			    		
			    		olvl[u][(int) checkC].makeDiscovered();
			    		break;
			    }
			 //else check the boxes to left and right
			}else {
				render[u][(int) checkC] = olvl[u][(int) checkC].toString();
				if(olvl[u][(int) checkC] instanceof Wall) {
			    		
			    		olvl[u][(int) checkC].makeDiscovered();
			    		break;
			    }
				render[u][(int) checkC+1] = olvl[u][(int) checkC+1].toString();
				if(olvl[u][(int) checkC+1] instanceof Wall) {
		    			
		    			olvl[u][(int) checkC].makeDiscovered();
		    			break;
				}
			}
		}
		return render;
	}
	
	/** USED FOR RENDERING: RAY CAST**/
	public static String[][] updateBottomShallow(String[][] render, double slope){
		double checkR;
		
		for(int u = p.getC(); u >= 0; u-- ) {
			checkR = p.getR()- (p.getC()-u)*slope;
			
			//print("u: "+ u + " slope: "+slope + " player r, c" + p.getR() + p.getC());
			
			//if checkC is an integer:
			if ((checkR == Math.floor(checkR)) && !Double.isInfinite(checkR)) {
				render[(int) checkR][u] = olvl[(int) checkR][u].toString();
			    if(olvl[(int) checkR][u] instanceof Wall) {
			    		
			    		olvl[(int) checkR][u].makeDiscovered();
			    		break;
			    }
			 //else check the boxes to left and right
			}else {
				render[(int) checkR][u] = olvl[(int) checkR][u].toString();
				if(olvl[(int) checkR][u] instanceof Wall) {
			    		
			    		olvl[(int) checkR][u].makeDiscovered();
			    		break;
			    }
				render[(int) checkR+1][u] = olvl[(int) checkR+1][u].toString();
				if(olvl[(int) checkR+1][u] instanceof Wall) {
		    			
		    			olvl[(int) checkR][u].makeDiscovered();
		    			break;
				}
			}
			
		}
		return render;
	}
	
	/** USED FOR RENDERING: RAY CAST**/
	public static String[][] updateLeftSteep(String[][] render, double slope){

		double checkC;
		
		//for decreasing row
		if(slope > 0) {
			for(int u = p.getR(); u >=0; u-- ) {
				checkC = p.getC()-((p.getR()-u)/slope);
				//print("slop: "+ slope);
				//print("c/: "+checkC);
				//if checkC is an integer:
				if ((checkC == Math.floor(checkC)) && !Double.isInfinite(checkC)) {
					render[u][(int) checkC] = olvl[u][(int) checkC].toString();
				    if(olvl[u][(int) checkC] instanceof Wall) {
				    		
				    		olvl[u][(int) checkC].makeDiscovered();
				    		break;
				    }
				 //else check the boxes to left and right
				}else {
					render[u][(int) checkC] = olvl[u][(int) checkC].toString();
					if(olvl[u][(int) checkC] instanceof Wall) {
				    		
				    		olvl[u][(int) checkC].makeDiscovered();
				    		break;
				    }
					render[u][(int) checkC+1] = olvl[u][(int) checkC+1].toString();
					if(olvl[u][(int) checkC+1] instanceof Wall) {
			    			
			    			olvl[u][(int) checkC].makeDiscovered();
			    			break;
					}
				}
			}
		
		//for increasing row
		}else {
			for(int u = p.getC(); u < olvl.length; u++ ) {
				checkC = p.getC()-((p.getR()-u)/slope);
				//if checkC is an integer:
				if ((checkC == Math.floor(checkC)) && !Double.isInfinite(checkC)) {
					render[u][(int) checkC] = olvl[u][(int) checkC].toString();
				    if(olvl[u][(int) checkC] instanceof Wall) {
				    		
				    		olvl[u][(int) checkC].makeDiscovered();
				    		break;
				    }
				 //else check the boxes to left and right
				}else {
					render[u][(int) checkC] = olvl[u][(int) checkC].toString();
					if(olvl[u][(int) checkC] instanceof Wall) {
				    		
				    		olvl[u][(int) checkC].makeDiscovered();
				    		break;
				    }
					render[u][(int) checkC+1] = olvl[u][(int) checkC+1].toString();
					if(olvl[u][(int) checkC+1] instanceof Wall) {
							olvl[u][(int) checkC+1].makeDiscovered();
			    			break;
					}
				}
			}
		}
		
		return render;
	}

	/** USED FOR RENDERING: RAY CAST**/
	public static String[][] updateLeftShallow(String[][] render, double slope){

		double checkR;
		
		//go decreasing cols to find row
		for(int u = p.getC(); u >= 0; u--) {
			checkR = p.getR()- (p.getC()-u)*slope;
			//print("slopee: "+ slope);
			
			//if checkC is an integer:
			if ((checkR == Math.floor(checkR)) && !Double.isInfinite(checkR)) {
				render[(int) checkR][u] = olvl[(int) checkR][u].toString();
			    if(olvl[(int) checkR][u] instanceof Wall) {
			    		
			    		olvl[(int) checkR][u].makeDiscovered();
			    		break;
			    }
			 //else check the boxes to left and right
			}else {
				render[(int) checkR][u] = olvl[(int) checkR][u].toString();
				if(olvl[(int) checkR][u] instanceof Wall) {
			    		
			    		olvl[(int) checkR][u].makeDiscovered();
			    		break;
			    }
				render[(int) checkR+1][u] = olvl[(int) checkR+1][u].toString();
				if(olvl[(int) checkR+1][u] instanceof Wall) {
		    			
		    			olvl[(int) checkR][u].makeDiscovered();
		    			break;
				}
			}
		}
		return render;
	}
	
	/** USED FOR RENDERING: RAY CAST**/
	public static String[][] updateRightSteep(String[][] render, double slope){
		double checkC;
		
		//for increasing row
		if(slope > 0) {
			for(int u = p.getR(); u < olvl.length; u++ ) {
				checkC = p.getC()-((p.getR()-u)/slope);
				//print("slop: "+ slope);
				//print("c/: "+checkC);
				//if checkC is an integer:
				if ((checkC == Math.floor(checkC)) && !Double.isInfinite(checkC)) {
					render[u][(int) checkC] = olvl[u][(int) checkC].toString();
				    if(olvl[u][(int) checkC] instanceof Wall) {
				    		
				    		olvl[u][(int) checkC].makeDiscovered();
				    		break;
				    }
				 //else check the boxes to left and right
				}else {
					render[u][(int) checkC] = olvl[u][(int) checkC].toString();
					if(olvl[u][(int) checkC] instanceof Wall) {
				    		
				    		olvl[u][(int) checkC].makeDiscovered();
				    		break;
				    }
					render[u][(int) checkC+1] = olvl[u][(int) checkC+1].toString();
					if(olvl[u][(int) checkC+1] instanceof Wall) {
			    			
			    			olvl[u][(int) checkC+1].makeDiscovered();
			    			break;
					}
				}
			}
		
		//for decreasing row
		}else {
			for(int u = p.getR(); u >=0; u-- ) {
				checkC = p.getC()-((p.getR()-u)/slope);
				//print("slop: "+ slope);
				//print("c/: "+checkC);
				//if checkC is an integer:
				if ((checkC == Math.floor(checkC)) && !Double.isInfinite(checkC)) {
					render[u][(int) checkC] = olvl[u][(int) checkC].toString();
				    if(olvl[u][(int) checkC] instanceof Wall) {
				    		
				    		olvl[u][(int) checkC].makeDiscovered();
				    		break;
				    }
				 //else check the boxes to left and right
				}else {
					render[u][(int) checkC] = olvl[u][(int) checkC].toString();
					if(olvl[u][(int) checkC] instanceof Wall) {
				    		
				    		olvl[u][(int) checkC].makeDiscovered();
				    		break;
				    }
					render[u][(int) checkC+1] = olvl[u][(int) checkC+1].toString();
					if(olvl[u][(int) checkC+1] instanceof Wall) {
			    			
			    			olvl[u][(int) checkC+1].makeDiscovered();
			    			break;
					}
				}
			}
		}
		
		return render;
	}
	
	/** USED FOR RENDERING: RAY CAST**/
	public static String[][] updateRightShallow(String[][] render, double slope){
		double checkR;
		
		//go decreasing cols to find row
		for(int u = p.getC(); u < olvl[0].length; u++) {
			checkR = p.getR()- (p.getC()-u)*slope;
			//print("slopee: "+ slope);
			
			//if checkC is an integer:
			if ((checkR == Math.floor(checkR)) && !Double.isInfinite(checkR)) {
				render[(int) checkR][u] = olvl[(int) checkR][u].toString();
			    if(olvl[(int) checkR][u] instanceof Wall) {
			    		
			    		olvl[(int) checkR][u].makeDiscovered();
			    		break;
			    }
			 //else check the boxes to left and right
			}else {
				render[(int) checkR][u] = olvl[(int) checkR][u].toString();
				if(olvl[(int) checkR][u] instanceof Wall) {
			    		
			    		olvl[(int) checkR][u].makeDiscovered();
			    		break;
			    }
				render[(int) checkR+1][u] = olvl[(int) checkR+1][u].toString();
				if(olvl[(int) checkR+1][u] instanceof Wall) {
		    			
		    			olvl[(int) checkR+1][u].makeDiscovered();
		    			break;
				}
			}
		}
		return render;
	}
	
	/** USED FOR RENDERING: Sends ray cast**/
	public static String[][] rayCast(String[][] render) {

		
		
		
		//STEP 1: for now, check all lines from x to border
		int[][] borderCoords = getBorderCoords();
		
		
		//STEP 2: get all slopes
		double[] slopes = getSlopes(borderCoords);
			

		
		//different thing for each side
		for(int i = 0; i < slopes.length; i++) {
			
			//top, bottom, left, right
			
			//check top
			if((i>= 0&&i<slopes.length/4)) {
				if(Math.abs(slopes[i])>=1) {
					render = updateTopSteep(render, slopes[i]);
				}else {
					render = updateTopShallow(render, slopes[i]);
				}
			}
				
			//check bottom
			if(i>= slopes.length/4 && i<slopes.length*2/4){
				if(Math.abs(slopes[i])>=1) {
					render = updateBottomSteep(render,slopes[i]);
				}else {
					render = updateBottomShallow(render,slopes[i]);
				}
			} 
			
			//check left
			if(i>= slopes.length*2/4&&i<slopes.length*3/4) {
				if(Math.abs(slopes[i])>1) {
					render = updateLeftSteep(render,slopes[i]);
					//render = updateTop
				}else {
					render = updateLeftShallow(render,slopes[i]);
				}
			}
			
			if(i>= slopes.length*3/4) {
				if(Math.abs(slopes[i])>1) {
					render = updateRightSteep(render,slopes[i]);
					//render = updateTop
				}else {
					render = updateRightShallow(render,slopes[i]);
				}
			}

			
		}
		return render;
		
	}
	
	

	/** USED FOR RENDERING: places empty at players old coord**/
	public static void updateOlvlPlayer() {
		olvl[p.getR()][p.getC()] = new Thing(p.getR(),p.getC());
	}
	
	/** USED FOR RENDERING: RAY CAST**/
	public static int[] convertDir(int dir) {
		int[][] temp = {{-1,0},{0,-1},{1,0},{0,1},{0,0},{0,0}};
		return temp[dir];
	}
	
	
	/** For testing purposes**/
	public static int[][] setLevel1() {
		
		//blank = 0 
		//wall = 1
		//breakable wall = 2
		//IntelFile = 3;
		//extraction point = 4

		int[][] temp= {
				{1,1,1,1,1,1,1,1,1,1},
				{1,2,0,0,0,0,0,0,0,1},
				{1,0,0,0,0,0,3,0,0,1},
				{1,0,0,0,0,0,1,0,0,1},
				{1,0,0,2,0,0,1,0,0,1},
				{1,0,0,0,0,0,1,0,0,1},
				{1,0,0,0,0,0,1,0,0,1},
				{1,0,0,1,1,1,1,0,0,1},
				{4,0,0,0,0,0,0,0,0,1},
				{1,1,1,1,1,1,1,1,1,1}
		};
		
		return temp;
	}
	
	/** Showcase shading**/
	public static int[][] setLevel2() {
		
		//blank = 0 
		//wall = 1
		//player = 2

		int[][] temp= {
				{1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},
				{1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,1},
				{1,0,0,0,0,0,0,1,0,0,0,0,0,0,0,1,0,1,1,1,0,0,1},
				{1,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,1,0,0,1},
				{1,0,0,0,2,0,0,1,0,0,1,0,0,1,0,0,0,0,0,1,0,0,1},
				{1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
				{1,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
				{1,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
				{1,0,0,0,0,0,0,1,1,1,1,0,0,0,1,1,0,0,0,0,0,0,1},
				{1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,1},
				{1,0,0,0,1,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,1},
				{1,0,0,0,1,1,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,1},
				{1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
				{1,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,1,0,0,1},
				{1,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
				{1,0,0,0,1,0,0,1,0,0,0,0,0,0,0,1,0,0,0,0,0,0,1},
				{1,0,0,0,1,0,0,0,0,0,0,0,0,0,0,1,0,0,0,1,0,0,1},
				{1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,1},
				{1,0,0,0,0,0,0,0,1,1,1,1,1,1,1,1,0,0,0,0,0,0,1},
				{1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,1,0,0,1},
				{1,0,0,0,1,0,0,0,0,0,0,1,0,0,0,1,0,0,1,1,0,0,1},
				{1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
				{1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},
		};
		
		return temp;
	}
	
	/** converts int[][] to thing[][]**/
	public static void convertLevel() {
		int temp = 0;
		
		olvl = new Thing[lvl.length][lvl[0].length];
		for(int i = 0; i < lvl.length; i++) {
			for(int j = 0; j< lvl[0].length; j++) {
				temp = lvl[i][j];
				
				if(temp == 0) {
					olvl[i][j] = new Thing(i,j);
				}else if(temp == 2) {
					olvl[i][j] = new BreakableWall(i,j);
				}else if(temp == 3) {
					olvl[i][j] = new IntelFile(i,j);
				}else if(temp == 4) {
					olvl[i][j] = new ExtractionPoint(i,j);
				} else {
					olvl[i][j] = new Wall(i,j);
				}
				
			}
		}
		
		olvl[p.getR()][p.getC()] = p;
		
	}
	
	/** Displays without shading**/
	public static void displayOLevel() {
		Thing temp;
		
		for(int i = 0; i < lvl.length; i++) {
			for(int j = 0; j< lvl[0].length; j++) {
				temp = olvl[i][j];
				
				if(temp instanceof entity.Player) {
					System.out.print("X");
				}else if(temp instanceof BreakableWall) {
					System.out.print("◘");
				}else if(temp instanceof Wall) {
					System.out.print("■");
				}else{
					System.out.print(" ");
				}
				
				
			}
			System.out.print("\n");
		}
	}
	
	/** Displays finaized render**/
	public static void displayRender(String[][] render) {

		for(int i = 0; i < render.length; i++) {
			for(int j = 0; j< render[0].length; j++) {
				
				if(olvl[i][j].isDiscovered()) {
					System.out.print(olvl[i][j].toString());
				}else {
					System.out.print(render[i][j]);
				}
			}
			System.out.print("\n");
		}
		
	}

	/** brief for story room**/
	public static void brief() {
		
		if(CaveExplorer.currentlvl==0) {
			dialouge("Hey Boss,/ Welocome to the base!/ We just finished the construction of the r&d and intel platforms.//"
					+ " Our intel team intercepted a tranmission from the soviets,= discussing their plans to build a fusion-based "
					+ "metal gear in Afganistan./ We need you to investigate.....");
		}else if(CaveExplorer.currentlvl == 1) {
			print("oh sh");
			
		}else if(CaveExplorer.currentlvl == 2) {
			print("asd");
		}
		
		
	}
	
	/** slowly prints text**/
	public static void dialouge(String s) {
		String temp = "";
		for(int i =0; i< s.length(); i++) {
			temp = s.substring(i, i+1);
			if(temp.equals("/")) {
				pause(1000);
				print("\n");
			}else if(temp.equals("=")){
				print("");
			}else {
				pause(30);
				System.out.print(temp);
			}
		}
		print("");
		pause(500);
	}
	/** for dialouge()**/
	public static void pause(int i) {
		try {
			Thread.sleep(i);
		} catch (InterruptedException e) {e.printStackTrace();}
	}

	/** REPLACES OBJECT IN OLVL AT COORDS WITH EMOTY SPACE **/
	public static void breakWall(int r, int c) {
		olvl[r][c] = new Thing(r,c);
		//print("breaking wall at: " + r+ c);
		//print("player : " + p.getR() + p.getC());
	}
	
	/** For demonstration purposes**/
	//private static int[] startingPsn = {3,3};
	
	
	/*public static void main(String[] args) {
		//print(new Wall(1,1));
		//print("aaaaaaa");
		
		
		playing = true;
		
		in = new Scanner(System.in);
		
		p = new entity.Player(SisiLevel.PLAYERSPAWN[0], SisiLevel.PLAYERSPAWN[1]);
		c = new Camera(-1,-1);
		SisiLevel.createGuards();
		g = SisiLevel.GUARDS;
		
		lvl = SisiLevel.LEVEL;
		convertLevel();
		
		for(int i = 0; i < g.length; i++) {
			olvl[g[i].getRow()][g[i].getColumn()] = g[i];
		}
		
		print("olvl: ");
		
		displayOLevel();
		
		print("\n\n\n");
		//brief();
		
		playGame();
		
	}*/
}
