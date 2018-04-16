import java.util.ArrayList;

import processing.core.*;

public class Hexagon extends PApplet{
	PShape hex, bgTri;
	PFont din;
	private float partRot, rotIncrement;
	private boolean leftD, rightD, gameInProgress, dead;
	private int screenValue, gameTime;
	private final int CAPACITY = 20;
	private ArrayList<Boolean[]> trapList;
	private ArrayList<Boolean[][]> patterns;
	private float zoom, oscZoom;
	int blue1 = color(204,0,0);
	int blue2 = color(0,0,204);
	int trap1 = color(255,153,153);
	int trap2 = color(170,204,255);
	float sqrt3 = sqrt(3);
	
	public static void main (String[] args){
		PApplet.main("Hexagon");
	}
	public void settings(){
		fullScreen(P3D);
		//size(800,600,P3D);
	}
	public void setup(){
		din = createFont("din1451alt.ttf", 64);
		textFont(din);

		screenValue = max(width,height);
		
		createHex();
		createBgTri();
		
		patterns = new ArrayList<Boolean[][]>();
		addPatterns();
		
		rotIncrement = .10f;

		
//		gameInProgress = true;
	}
	public void draw(){
		translate(0,0,oscZoom);
		if(dead){
			oscZoom = 5*sin(millis()/100f);
			drawEnd();
		}
		else if(gameInProgress){
			oscZoom = 25*sin(millis()/100f);
			drawGame();
		}
		else{
			oscZoom = 5*sin(millis()/100f);
			drawMenu();
		}
	}
	public void createHex(){
		hex = createShape();
		hex.beginShape();
		hex.strokeWeight(5);
		hex.vertex(-50f, (float)(50*sqrt3));
		hex.vertex(50f, (float)(50*sqrt3));
		hex.vertex(100,0);
		hex.vertex(50f, -(float)(50*sqrt3));
		hex.vertex(-50f, -(float)(50*sqrt3));
		hex.vertex(-100, 0);
		hex.vertex(-50f, (float)(50*sqrt3));
		hex.endShape();
	}
	public void createBgTri(){
		bgTri = createShape();
		bgTri.beginShape();
		bgTri.fill(255,70);
		bgTri.noStroke();
		bgTri.vertex(0,0);
		bgTri.vertex(screenValue,(float)(screenValue*sqrt3));
		bgTri.vertex(screenValue*2,0);
		bgTri.endShape();
	}
	public void keyPressed(){
		if(keyCode == LEFT){
			leftD = true;
			rightD = false;
		}
		if(keyCode == RIGHT){
			rightD = true;
			leftD = false;
		}
		if(key == ' '  && (!gameInProgress || dead)) {
			startGame();
			dead = false;
		}
		if(dead && keyCode==UP){
			dead = false;
			gameInProgress = false;
		}
	}
	public void keyReleased(){
		if(keyCode == LEFT){
			leftD = false;
		}
		if(keyCode == RIGHT){
			rightD = false;
		}
	}
	public void zoomTo(float newZoom){
		if(zoom == newZoom)return;
		for(int i = 0; i < 30; i++){
			if(zoom>newZoom){
				zoom--;
			}else if (zoom<newZoom){
				zoom++;
			}
		}
	}
	public void drawGame(){
		zoomTo(0);
		
		generate();
		
		if(partRot<0)
			partRot+=TWO_PI;
		if(partRot>=TWO_PI)
			partRot-=TWO_PI;

		for(int i = 0; i < 5; i++){
			gameTime++;
			if(gameTime % 30 == 0){
				trapList.remove(0);
			}
			if(gameTime % 30 == 15){
				if(trapList.get(2)[(int)(partRot*3/PI)]){
					dead = true;
				}
			}
		}
		
		
		if(leftD)partRot-=rotIncrement;
		if(rightD)partRot+=rotIncrement;
		
		background((lerpColor(blue1, blue2, (float) ((((millis()/5000)%2==0)?millis()%5000:5000-millis()%5000)/5000.0))));
		
		//translate to center and background rotation
		translate(width/2f, height/2f,zoom);
		rotateZ(frameCount/75f);
		rotateX(0.125f*sin(frameCount/100f)-.25f);
		rotateY(0.2f*sin(frameCount/100f));

		
		//bg triangles
		translate(0,0,1);
		fill(100);
		shape(bgTri,0,0);
		rotateZ(2f*PI/3);
		shape(bgTri,0,0);
		rotateZ(2f*PI/3);
		shape(bgTri,0,0);
		rotateZ(2f*PI/3);
		
		translate(0,0,1);
		drawObstacles();
		
		translate(0,0,1);
		fill(0);
		shape(hex,0,0,100,(float)(50*sqrt3));
				
		//particle rotation
		rotateZ(partRot);
		noStroke();
		fill(255);
		triangle(75,0,60,10,60,-10);
		
	}
	public void drawMenu() {
		stroke(0);

		background((lerpColor(blue1, blue2, (float) ((((millis()/5000)%2==0)?millis()%5000:5000-millis()%5000)/5000.0))));
		textFont(din,64);
		textAlign(CENTER);
		text("Super Hexagon: Java Edition",width/2f,(height/4f));
		textFont(din, 32);
		text("Press Space to Start", width/2f, (3*height)/4f);
		text("Left and Right arrow to Move", width/2f,60);
		fill(255);
		translate(width/2f,height/2f, zoom);
		rotate(frameCount/75f);
		shape(hex,0,0,100,(float)(50*sqrt3));

		zoomTo(450);
	}
	public void drawEnd(){
		background((lerpColor(blue1, blue2, (float) ((((millis()/5000)%2==0)?millis()%5000:5000-millis()%5000)/5000.0))));
		
		//translate to center and background rotation
		translate(width/2f, height/2f,zoom);
		rotateZ(frameCount/75f);
		rotateX(0.125f*sin(frameCount/100f)-.25f);
		rotateY(0.2f*sin(frameCount/100f));
		
		//bg triangles
		translate(0,0,1);
		fill(100);
		shape(bgTri,0,0);
		rotateZ(2f*PI/3);
		shape(bgTri,0,0);
		rotateZ(2f*PI/3);
		shape(bgTri,0,0);
		rotateZ(2f*PI/3);
		
		translate(0,0,1);
		drawObstacles();
		
		translate(0,0,1);
		fill(0);
		shape(hex,0,0,100,(float)(50*sqrt3));
				 
		//particle rotation
		rotateZ(partRot);
		noStroke();
		fill(255);
		triangle(75,0,60,10,60,-10);
		
		zoomTo(450);
	}
	public void startGame() {
		trapList = new ArrayList<Boolean[]>();
		for(int i=0; i < 15; i++){
			trapList.add(emptyRow());
		}
		
		gameInProgress = true;
		zoom = 450;
		
	}
	public void goToMenu(){
		gameInProgress = false;
		
	}
	public void gameEnd(){
		
	}
	
	public void generate(){
		while(trapList.size() < CAPACITY){
			for(Boolean[] add : patterns.get((int)(Math.random()*patterns.size())))
				trapList.add(add);
		}
	}
	public void addPatterns(){
		for(int i = 0; i < 6; i++){
			patterns.add(new Boolean[][]{emptyRow(),
				emptyRow(),
				emptyRow(),
				emptyRow(),
				rowFive(i)});
		}
		patterns.add(new Boolean[][]{
			emptyRow(),
			emptyRow(),
			emptyRow(),
			emptyRow(),
			{true, false, true, false, true, false}
		});
		patterns.add(new Boolean[][]{
			emptyRow(),
			emptyRow(),
			emptyRow(),
			emptyRow(),
			emptyRow(),
			{true, true, true, false, true, false}
			});
		patterns.add(new Boolean[][]{
			emptyRow(),
			emptyRow(),
			emptyRow(),
			emptyRow(),
			emptyRow(),
			{true, false, true, true, true, false}
			});
		patterns.add(new Boolean[][]{
			emptyRow(),
			emptyRow(),
			emptyRow(),
			emptyRow(),
			emptyRow(),
			{false, true, true, true, true, true},
			emptyRow(),
			emptyRow(),
			{true, false, true, true, true, true},
			emptyRow(),
			emptyRow(),
			{true, true, false, true, true, true},
			emptyRow(),
			emptyRow(),
			{true, true, true, false, true, true},
			emptyRow(),
			emptyRow(),
			{true, true, true, true, false, true},
			emptyRow(),
			emptyRow(),
			{true, true, true, true, true, false}
			});
		patterns.add(new Boolean[][]{
			emptyRow(),
			emptyRow(),emptyRow(),emptyRow(),
			{false, true, false, true, false, true},
			emptyRow(),
			emptyRow(),emptyRow(),emptyRow(),
			{true, false, true, false, true, false},
			emptyRow(),
			emptyRow(),emptyRow(),emptyRow(),
			{false, true, false, true, false, true},
			emptyRow(),
			emptyRow(),emptyRow(),emptyRow(),
			{true, false, true, false, true, false}});
	
		patterns.add(new Boolean[][]{
			emptyRow(),emptyRow(),emptyRow(),emptyRow(),emptyRow(),emptyRow(),
			{true, false, true, true, true, true},
			{true, false, true, true, true, true},
			{true, false, true, true, true, true},
			{true, false, false, true, true, true},
			{true, false, false, true, true, true},
			{true, false, false, true, true, true},
			{true, true, false, true, true, true},
			{true, true, false, true, true, true},
			{true, true, false, true, true, true},
			{true, true, false, false, true, true},
			{true, true, false, false, true, true},
			{true, true, false, false, true, true},
			{false, true, true, false, true, false},
			{false, true, true, false, true, false},
			{false, true, true, false, true, false},
			{false, true, true, false, false, false},
			{false, true, true, false, false, false}});
		patterns.add(new Boolean[][]{
			emptyRow(),
			emptyRow(),
			emptyRow(),
			emptyRow(),
			{true, true, true, true, true, false},
			emptyRow(),
			emptyRow(),
			emptyRow(),
			{true, true, true, false, true, true},
			emptyRow(),
			emptyRow(),
			emptyRow(),
			{true, true, false, true, true, true},
			emptyRow(),
			emptyRow(),
			emptyRow(),
			{true, true, true, true, false, true},
			emptyRow(),
			emptyRow(),
			emptyRow(),
			{true, true, true, true, true, false},
			emptyRow(),
			emptyRow(),
			emptyRow(),
			emptyRow(),
			{true, true, false, true, true, true}
			});
		patterns.add(new Boolean[][]{
			emptyRow(),
			emptyRow(),
			emptyRow(),emptyRow(),emptyRow(),emptyRow(),
			{true, false, false, true, false, false},
			{true, false, false, true, false, false},
			{true, false, false, true, false, false},
			{true, true, false, true, true, false},
			{true, false, false, true, false, false},
			{true, false, false, true, false, false},
			{true, false, false, true, false, false},
			{true, false, true, true, false, true}
			});
		patterns.add(new Boolean[][]{emptyRow(),emptyRow(),emptyRow()});
	}
	
	public Boolean[] rowFive(int num){
		Boolean[] arr = new Boolean[]{true, true, true, true, true, true};
		arr[num] = false;
		return arr;
	}
	public Boolean[] emptyRow(){
		return new Boolean[]{false, false, false, false, false, false};
	}
	public void drawTrap(float d) {
		
	   	int w = 50;
	   	fill((lerpColor(trap1, trap2, (float) ((((millis()/5000)%2==0)?millis()%5000:5000-millis()%5000)/5000.0))),255);
	   	quad(d, 0, d+w, 0, (d+w)/2, (-sqrt3*(d+w))/2, d/2, (-sqrt3*d)/2);
	}
	public void drawObstacles(){
		for(int i=0; i < trapList.size(); i++){
			Boolean[] arr = trapList.get(i);
			for(int j = 0; j < 6; j++){
				rotateZ(PI/3);
				if(arr[j]){
					drawTrap(i*50 - 5f/3*(gameTime%30));
				}
			}
		}
	}
}