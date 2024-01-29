//Class to draw graphics of sprite/taxi

import javax.swing.*;
import java.awt.*;

//Class to make rectangles for collision method so doesnt leave the screen from left and right
//Class to set sprite image.

 //Class that creates a sprite with an image, location, directional movement and a collision method.

class Sprite {

	//Sprite location.
	private double xloc, yloc;

	//Sprite direction.
	private double xdir, ydir;

	//Holds the image of the sprite/taxi.not initialized. takes input for the image
	private ImageIcon image;

	//Draw sprite image or not.
	private boolean show = true;

	//Holds the image filename. is the input of image
	private String filename = "";


// The default constructor. image not on screen or assigned yet
	Sprite() {
		image = null;
		xloc = 0;
		yloc = 0;
		xdir = 0;
		ydir = 0;
	}


//Constructor that sets the sprite image and location
	public Sprite(String filename, int xloc, int yloc) {
		setImage(filename);
		this.xloc = xloc;
		this.yloc = yloc;
	}

//Constructor that takes the location as the argument
	public Sprite(int xloc, int yloc) {
		this.xloc = xloc;
		this.yloc = yloc;
	}

//Constructor that takes an image filename as the argument
	Sprite(String filename) {
		setImage(filename);
	}


//Method to set the image variable
//using try and catch error detection in case of some error in detecting picture from project file
	void setImage(String filename) {
		this.filename = filename;

		try {
			this.image = new ImageIcon(getClass().getResource(filename));
		} catch (Exception e) {
			image = null;
		}
	}

//Getters and setters of attributes
	//Get xloc.
	int getXLoc() {
		return (int) xloc;
	}
	//Sets xloc.
	void setXLoc(int xloc) {
		this.xloc = xloc;
	}

	//Get yloc.
	int getYLoc() {
		return (int) yloc;
	}

	//Sets yloc.
	void setYLoc(int yloc) {
		this.yloc = yloc;
	}

	//Get xdir.
	public double getXDir() {
		return xdir;
	}

	//Sets xdir.
	void setXDir(double xdir) {
		this.xdir = xdir;
	}

	//Get ydir.
	public double getYDir() {
		return ydir;
	}

	//Sets ydir.
	void setYDir(double ydir) {
		this.ydir = ydir;
	}

	//Get image filename.
	String getFileName() {
		return filename;
	}
	
//Moves character by adding the direction to the location.
	void move() {
		xloc += xdir;
		yloc += ydir;
	}

//Return the width of the sprite or default 20 if the image is null
	int getWidth() {
		if (image == null)
			return 20;
		else
			return image.getIconWidth();
	}

//Return the height of the sprite or default 20 if the image in null
	int getHeight() {
		if (image == null)
			return 20;
		else
			return image.getIconHeight();
	}


//Method to draw sprite onto JPanel.
	void paint(Graphics g, JPanel panel) {
		if (show) {
			if (image == null)
				g.drawRect((int) xloc, (int) yloc, 50, 50);
			else
				image.paintIcon(panel, g, (int) xloc, (int) yloc);
		}
	}
	
	
}


