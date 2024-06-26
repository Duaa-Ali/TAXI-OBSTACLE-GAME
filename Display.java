//Class for all the graphics of our project.

import javax.swing.*; //for GUI of our project.
import java.awt.*; // for graphics (scroll, buttons etc) and to create graphics and images
import java.awt.event.ActionEvent;//used for when button is clicked (cursor)
import java.awt.event.ActionListener;//used for determining where the user clicked (with cursor)
import java.awt.event.KeyAdapter;//for detecting users keyboard inputs
import java.awt.event.KeyEvent;//extends the keyadapter to listen to the inputs like the WASD keys
import java.util.ArrayList; //for creating a list of different variables
import java.util.Random;//random value(number or info) generator

//Class for timer

// JPanel to hold the game in the window.
//using inheritance and abstraction 
class Display extends JPanel implements ActionListener {

	// Variable for the game logo 'Taxi run' with object and image constructor
 Sprite logo = new Sprite("Misc/logo.jpg");
	private boolean showLogo = false; // to turn the logo off when the game starts
	// New game variables.
	private boolean newGame = false;

	
	//create objects of classes to implement
	
	// Creates a strip generator object.
	 StripGenerator stripGen = new StripGenerator();
	// Holds Number of strips on screen.
	private int numOfStrips = 9;
	// 2D array for holding strips of background
	 Sprite[][] allStrips = new Sprite[numOfStrips][8];
	// Holds the index values of special strip images.
	private ArrayList<Integer> special = new ArrayList<>();
	// Holds number of special occasional images in special strip.
	private int land = 1, water = 0;
	// Array that holds the cars.
	private ArrayList<Sprite> cars = new ArrayList<>();
	// Array that holds the trains.
	private ArrayList<Sprite> trains = new ArrayList<>();
	// buttons we created earlier for start and control
	private JButton startButton, controlsButton;

	 ManageVehicles vManager = new ManageVehicles();

	// Create hero (taxi) sprite.
	 Sprite hero = new Sprite("Taxi/taxi_backfinal.png");

	// Variable to hold score and travel.
	private int score = 0, movement = 0;
	private Score scoreManager = new Score();

	// Variables for directional control.
	private int up = 0, down = 0, left = 0, right = 0;
	private boolean press = false;

	// Variables for invincibility power.
	private boolean invincibility = false;
	private int invDuration = 0, invTimeLeft = 0;

	// Create timer.
	private Timer gameLoop;

	// Create random generator.
	private Random rand = new Random();

	
	Display(boolean pause) {

		// Set layout to absolute for buttons.
		setLayout(null);

		// Create button component, set image, remove borders.
		startButton = new JButton(new ImageIcon(getClass().getResource("Misc/Start.png")));
		//creating a border around the start button
		startButton.setBorder(BorderFactory.createEmptyBorder());
		controlsButton = new JButton(new ImageIcon(getClass().getResource("Misc/Controls.png")));
		//creating a border around the control button
		controlsButton.setBorder(BorderFactory.createEmptyBorder());

		//detecting wjere the buttons are clicked on the jpanel
		startButton.addActionListener(this);
		controlsButton.addActionListener(this);

		//adding the buttons to the project
		add(startButton);
		add(controlsButton);
		
//initialising the size and dimensions of the buttons
		startButton.setBounds(250, 175, 300, 200);
		controlsButton.setBounds(300, 390, 200, 100);

		// Create key listener for character.
		addKeyListener(new KeyPressing());

		// Set the focus to JPanel.
		setFocusable(true);

		// Make the movement smooth.
		setDoubleBuffered(true);

		// Method to set the sprite locations.
		setInitialLocations();

		// Create the game timer and start it.
		gameLoop = new Timer(25, this);

		/// Pauses the game on first run.
		if (!pause) {
			startButton.setVisible(false);
			controlsButton.setVisible(false);
			gameLoop.start();
		} else
			showLogo = true;

	}

//Method to set the initial location of the sprite/taxi
	private void setInitialLocations() {

		// Sets the taxis default location.
		hero.setXLoc(298);
		hero.setYLoc(400);

		// Initializes game with land strips.
		for (int i = 0; i < numOfStrips; i++) {

			// Creates a new land sprite strip.
			Sprite[] strip = stripGen.getLandStrip();

			// Adds sprite strip to strips array.
			allStrips[i] = strip;
		}

// Sets a grass image under and in front of the taxi location(Prevents the taxi from starting on a tree or shrub)
		allStrips[5][3].setImage("Misc/Grass.png");
		allStrips[4][3].setImage("Misc/Grass.png");

// Sets the location for the taxi in the strip array.
		// Spaces sprites 100 pixels apart horizontally so they do not collide
		int x = 0;
// Spaces sprites 100 pixels apart vertically so they are not colliding
		int y = -100;
	//designing a plot of sprites with specific dimensions
		for (int i = 0; i < numOfStrips; i++) {

			for (int z = 0; z < 8; z++) {

				allStrips[i][z].setXLoc(x);

				allStrips[i][z].setYLoc(y);
				x += 100;
			}
			x = 0;
			y += 100;
		}

// Sets special array to first initialized land sprite array. Prevents water/lillypad offset if it is generated right after the grass field.
		for (int i = 0; i < 8; i++) {
			if (allStrips[0][i].getFileName().equals("Misc/Grass.png")) {
				special.add(i);
				land++;
			}
		}
	}


// Timer runs the statement on a loop
	public void actionPerformed(ActionEvent e) {

		// Makes a new game if start button is clicked.
		if (e.getSource() == startButton) {

			newGame = true;
			newGame();

		}
		// Show message dialog with controls.
		else if (e.getSource() == controlsButton) {

			JOptionPane.showMessageDialog(null,
					"Arrow Keys:  Move the taxi." + "\nCtrl:  Activates 3 seconds of invincibility once per game."
							+ "\n         (Makes taxi pass through any object)" + "\nShift:  Pause / Resume the game."
							+ "\nEnter:  Start game / Restart game while paused.");

		}
		// Runs the timer.
		else {

			// Method that prevents taxi from moving onto trees and checks for death and invincibility.
			//calls taxi's boundaries
			heroBounds();

			// Method to smoothly move the character one block.
			jumpHero();

			// Sprite method that moves the hero.
			hero.move();

			// Method to move cars.
			manageCars();

			// Method to move trains.
			manageTrains();

			// Moves all the sprites in the sprite strips.
			for (int i = 0; i < numOfStrips; i++) {
				for (int x = 0; x < 8; x++) {
					allStrips[i][x].move();
				}
			}

			// Method that resets the strips.
			manageStrips();

			// Method to set the scrolling speed.
			scrollScreen();

	// Assigns farthest travel to score
			if (movement > score)
				score = movement;

	// Redraws sprites on screen
			repaint();

	// Stop stuttering (linux issue)
			Toolkit.getDefaultToolkit().sync();
		}
	}

// Method that starts a new game
	private void newGame() {

		if (newGame) {

			// Get old frame and overwrite it
			JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
			frame.dispose();

			// Create new main menu frame.
			new Window(false);
		}
	}

//Method to end game. Stops loop, saves scores, displays message

	private void killMsg(String killer) {

		repaint();
		gameLoop.stop();
		scoreManager.updateScores(score);

		// Displays correct message based on method of death
		switch (killer) {
		case "water":
			JOptionPane.showMessageDialog(null, "You drowned!" + "\nScore: " + score);
			break;
		case "tooFarDown":
			JOptionPane.showMessageDialog(null, "You were trapped!" + "\nScore: " + score);
			break;
		case "tooFarUp":
			JOptionPane.showMessageDialog(null, "You left the game!" + "\nScore: " + score);
			break;
		case "car":
			JOptionPane.showMessageDialog(null, "You got hit by a car!" + "\nScore: " + score);
			break;
		case "train":
			JOptionPane.showMessageDialog(null, "You got hit by a train!" + "\nScore: " + score);
			break;
		}

		// Show start button which makes a new window
		startButton.setVisible(true);
		controlsButton.setVisible(true);

		showLogo = true;
	}

//Method that prevent hero from moving on trees, and checks for death with water, train, or cars
	private void heroBounds() {

		// Invincibility countdown
		if (invincibility) {
			invDuration++;

			if (invDuration == 1)
				invTimeLeft = 3;
			if (invDuration == 50)
				invTimeLeft = 2;
			if (invDuration == 100)
				invTimeLeft = 1;
			if (invDuration == 150) {
				invTimeLeft = 0;
				invincibility = false;
			}
		}

		// Collision method for taxi
		for (int i = 0; i < numOfStrips; i++) {
			for (Sprite s : allStrips[i]) {

				// Checks too see if user is invincible. if not, do following
				if (!invincibility) {

					// Prevents hero from jumping through trees.
					if (s.getFileName().equals("Misc/Tree_One.png") || s.getFileName().equals("Misc/Tree_Two.png")) {
						if (collision(hero, s)) {

							if ((s.getYLoc() + 100) - (hero.getYLoc()) < 5 && (s.getXLoc() + 100) - hero.getXLoc() < 125
									&& (s.getXLoc() + 100) - hero.getXLoc() > 20) {
								up = 0;
							} else if ((hero.getYLoc() + 105) > (s.getYLoc())
									&& (hero.getXLoc() + 100) - s.getXLoc() < 125
									&& (hero.getXLoc() + 100) - s.getXLoc() > 20) {
								down = 0;
							} else if (hero.getXLoc() - (s.getXLoc() + 100) > -5
									&& (s.getYLoc() + 100) - hero.getYLoc() < 125
									&& (s.getYLoc() + 100) - hero.getYLoc() > 20) {
								left = 0;
							} else if (s.getXLoc() - (hero.getXLoc() + 100) > -5
									&& (hero.getYLoc() + 100) - s.getYLoc() < 125
									&& (hero.getYLoc() + 100) - s.getYLoc() > 20) {
								right = 0;
							}
						}
					}

					// Ends game if user lands on water.
					if (s.getFileName().equals("Misc/Water.png")) {
						if (s.getXLoc() - hero.getXLoc() > 0 && s.getXLoc() - hero.getXLoc() < 10) {
							if (s.getYLoc() - hero.getYLoc() > 0 && s.getYLoc() - hero.getYLoc() < 10) {

								// Method to end game.
								killMsg("water");
							}
						}
					}
				}

				// Ends game if user goes too far down
				if (hero.getYLoc() > 800) {

					// Reset hero location
					hero.setYLoc(500);
					hero.setXLoc(900);

					// Method to end game
					killMsg("tooFarDown");
				}

				// Ends game if user goes too far up
				if (hero.getYLoc() < -110) {

					// Reset hero location.
					hero.setYLoc(500);
					hero.setXLoc(900);

					// Method to end game.
					killMsg("tooFarUp");
				}
			}
		}
	}

//Moves the character one strip forward or one strip backwards WITHOUT OFF-SETTING THE LOCATION DUE TO SCROLLING.
//Moves taxi smoothly by movement and not location. up,down,left,right : number of iterations. press : prevents over moving issue.
	private void jumpHero() {
		// Holds the hero's location.
		int location;

		// If left/right is pressed.
		if (left > 0 && press) {
			hero.setXDir(-12.5);
			left--;
			hero.setImage("Taxi/taxi_leftfinal.png");
		} else if (right > 0 && press) {
			hero.setXDir(12.5);
			right--;
			hero.setImage("Taxi/taxi_rightfinal.png");
		} else if (left == 0 && right == 0 && up == 0 && down == 0) {
			hero.setXDir(0);
			press = false;
		}

		// If up is pressed.
		if (up > 0 && press) {

			// Set hero speed.
			hero.setYDir(-10);
			hero.move();
			hero.setImage("Taxi/taxi_backfinal.png");

			// Get hero Y location.
			location = hero.getYLoc();

			// Sets the hero's location up one strip.
			for (int i = 0; i < numOfStrips; i++) {

				Sprite x = allStrips[i][0];

				// Aligns hero to strip after movement.
				if (location - x.getYLoc() > 95 && location - x.getYLoc() < 105) {

					hero.setYDir(0);
					up = 0;
					press = false;

					hero.setYLoc(x.getYLoc() + 101);

					// Increases travel keeper.
					movement++;

					i = numOfStrips;
				}
			}
		}

		// If down in pressed.
		else if (down > 0 && press) {

			// Set hero speed.
			hero.setYDir(10);
			hero.move();
			hero.setImage("Taxi/taxi_frontfinal.png");

			// Get hero location
			location = hero.getYLoc();

			// Sets the heros location down one strip.
			for (int i = 0; i < numOfStrips; i++) {

				Sprite x = allStrips[i][0];

				// Align hero to strip after movement.
				if (location - x.getYLoc() < -95 && location - x.getYLoc() > -105) {

					hero.setYDir(0);
					down = 0;
					press = false;

					hero.setYLoc(x.getYLoc() - 99);
					// location = x.getYLoc() - 100;
					// Decreases travel keeper.
					movement--;

					i = numOfStrips;
				}
			}
		}
	}

//Method that Moves cars, Removes cars getting out of Y boundary n checks for car collisions. Note: foreach not working correctly.
	private void manageCars() {

		// Cycles through car sprites
		for (int i = 0; i < cars.size(); i++) {

			Sprite car = cars.get(i);

			// Removes cars getting out of Y boundary
			if (car.getYLoc() > 800) {
				cars.remove(i);
			} else {

				// Moves car sprites
				car.move();

				// Reset cars gettimg out of X boundarys
				if (car.getXLoc() < -(rand.nextInt(700) + 400)) {

					// Right to left
					car.setXDir(-(rand.nextInt(10) + 10));

					car.setXLoc(900);

					car.setImage(vManager.randomCar("left"));
				} else if (car.getXLoc() > (rand.nextInt(700) + 1100)) {

					// Left to right.
					car.setXDir((rand.nextInt(10) + 10));

					car.setXLoc(-200);

					car.setImage(vManager.randomCar("right"));
				}
			}

			// Checks for car collisions
			if (collision(car, hero) && !invincibility) {

				// Method to end game.
				killMsg("car");
			}
		}
	}

//Method that: Moves trains. Removes trains getting out of Y boundarys. Checks for train collisions.
	private void manageTrains() {

		// Cycles through train sprites
		for (int i = 0; i < trains.size(); i++) {

			Sprite train = trains.get(i);

			// Removes trains gettimg out of Y boundarys
			if (train.getYLoc() > 800) {
				trains.remove(i);
			} else {

				// Moves train sprites
				train.move();

				// Reset X boundarys
				if (train.getXLoc() < -(rand.nextInt(2500) + 2600)) {
					train.setXDir(-(rand.nextInt(10) + 30));

					train.setXLoc(900);

					train.setImage(vManager.randomTrain());
				} else if (train.getXLoc() > rand.nextInt(2500) + 1800) {
					train.setXDir((rand.nextInt(10) + 30));

					train.setXLoc(-1500);

					train.setImage(vManager.randomTrain());
				}
			}

			// Checks for train collisions.
			if (collision(train, hero) && !invincibility) {

				// Method to end game.
				killMsg("train");
			}
		}
	}

//Method that correctly resets the strips
	private void manageStrips() {

	// Blank strip for testing variables
		int allWater;
		int allGrass;

		// Cycles through each strip
		for (int v = 0; v < numOfStrips; v++) {

			// Checks if strip is out of bounds
			if (allStrips[v][0].getYLoc() > 800) {

				// Generates a new strip.
				allStrips[v] = stripGen.getStrip();

				// Prevents an all water or grass strip.
				do {
					// initialise variables
					allWater = 0;
					allGrass = 0;

					// Check sprites in strip
					for (Sprite s : allStrips[v]) {
						if (s.getFileName().equals("Misc/Water.png"))
							allWater++;
						if (s.getFileName().equals("Misc/Grass.png"))
							allGrass++;
					}

					if (allWater == 8)
						allStrips[v] = stripGen.getWaterStrip();
					if (allGrass == 8)
						allStrips[v] = stripGen.getLandStrip();

				} while (allWater == 8 || allGrass == 8);

				// If there was previously a water strip, and this strip is a water strip, match this strips lillypads to the previous strip
				if (water > 0) {
					if (allStrips[v][0].getFileName().equals("Misc/Water.png")
							|| allStrips[v][0].getFileName().equals("Misc/Lillypad.png")) {

						water = 0;

						for (int i : special) {
							allStrips[v][i].setImage("Misc/Lillypad.png");
						}
					}
				}

				// If there was previously a water strip, and this strip is a land strip, match the grass to the previous strips lillypads
				if (water > 0) {
					if (allStrips[v][0].getFileName().equals("Misc/Grass.png")
							|| allStrips[v][0].getFileName().equals("Misc/Shrub.png")
							|| allStrips[v][0].getFileName().equals("Misc/Tree_One.png")
							|| allStrips[v][0].getFileName().equals("Misc/Tree_Two.png")) {

						allStrips[v] = stripGen.getSpecialLandStrip();

						water = 0;

						for (int i : special) {
							allStrips[v][i].setImage("Misc/Grass.png");
						}
					}
				}

				// If there was previously a land strip, and this strip is a water strip, match the lillypads to the grass.
				if (land > 0) {
					if (allStrips[v][0].getFileName().equals("Misc/Water.png")
							|| allStrips[v][0].getFileName().equals("LilyPad.png")) {

						land = 0;

						int val = 0;

						while (val == 0) {

							allStrips[v] = stripGen.getWaterStrip();

							for (int i = 0; i < 8; i++) {
								if (allStrips[v][i].getFileName().equals("Misc/Lillypad.png")) {
									// Remove
									for (int s : special) {
										if (i == s) {
											val++;
										}
									}
								}
							}
						}
					}
				}

				// if there is a water strip, write down the index of the Lillypads.
				if (allStrips[v][0].getFileName().equals("Misc/Water.png")
						|| allStrips[v][0].getFileName().equals("Misc/Lillypad.png")) {

					special.clear();
					water = 0;

					for (int i = 0; i < 8; i++) {
						if (allStrips[v][i].getFileName().equals("Misc/Lillypad.png")) {
							special.add(i);
							water++;
						}
					}
				} else
					water = 0;

				// if there is a land strip, write down the index of the grass.
				if (allStrips[v][0].getFileName().equals("Misc/Grass.png")
						|| allStrips[v][0].getFileName().equals("Misc/Shrub.png")
						|| allStrips[v][0].getFileName().equals("Misc/Tree_One.png")
						|| allStrips[v][0].getFileName().equals("Misc/Tree_Two.png")) {

					special.clear();

					land = 0;

					for (int i = 0; i < 8; i++) {
						if (allStrips[v][i].getFileName().equals("Misc/Grass.png")) {
							special.add(i);
							land++;
						}
					}
				}

				// Variable to reset horizontal strip location
				int X = 0;

				// Reset the location of the strip.
				for (int i = 0; i < 8; i++) {

					allStrips[v][i].setYLoc(-99);
					allStrips[v][i].setXLoc(X);

					X += 100;
				}

				// Set car.
				if (allStrips[v][0].getFileName().equals("Misc/Road.png")) {
					cars.add(vManager.setCar(allStrips[v][0].getYLoc() + 10));
				}

				// Set train.
				if (allStrips[v][0].getFileName().equals("Misc/Tracks.png")) {
					trains.add(vManager.setTrain(allStrips[v][0].getYLoc() + 10));
				}
			}
		}
	}
//Scrolls the strips and the hero/taxi
	private void scrollScreen() {

		// Cycles through strip array
		for (int v = 0; v < numOfStrips; v++) {
			for (int x = 0; x < 8; x++) {
				allStrips[v][x].setYDir(2);
			}
		}
		// Sets scrolling even if taxi is not moving
		if (!press) {
			hero.setYDir(2);
		}
	}

	//Checks for sprite collisions
	private boolean collision(Sprite one, Sprite two) {

		// Creates rectangles around sprites and checks for interesection
		Rectangle first = new Rectangle(one.getXLoc(), one.getYLoc(), one.getWidth(), one.getHeight());
		Rectangle second = new Rectangle(two.getXLoc(), two.getYLoc(), two.getWidth(), two.getHeight());

		return first.intersects(second);
	}

//Draws graphics onto screen
	public void paintComponent(Graphics g) {

		// Erases the previous screen
		super.paintComponent(g);

		// Draws strips
		for (int i = 0; i < numOfStrips; i++) {
			for (int x = 0; x < 8; x++) {
				allStrips[i][x].paint(g, this);
			}
		}

		// Draw taxi
		hero.paint(g, this);

		// Draw car sprites
		for (Sprite s : cars)
			s.paint(g, this);

		// Draw train sprites
		for (Sprite s : trains)
			s.paint(g, this);

		// Set the font size and color
		Font currentFont = g.getFont();
		Font newFont = currentFont.deriveFont(currentFont.getSize() * 3f);
		g.setFont(newFont);
		g.setColor(Color.green);

		// Draws the high score on the screen.
		g.drawString("Highest Score: " + scoreManager.readScore(), 50, 50);

		// Set the font size and color
		Font cF = g.getFont();
		Font nF = cF.deriveFont(cF.getSize() * 3f);
		g.setFont(nF);
		g.setColor(Color.yellow);

		// Draws the score on the screen.
		g.drawString("" + score, 50, 150);

		// Set the font size and color
		Font CF = g.getFont();
		Font NF = CF.deriveFont(CF.getSize() * 1f);
		g.setFont(NF);
		g.setColor(Color.red);

		// Draws invincibility status
		if (invincibility)
			g.drawString("" + invTimeLeft, 350, 350);

		// Draws logo on screen
		if (showLogo) {
			logo.setXLoc(175);
			logo.setYLoc(75);
			logo.paint(g, this);
		}

		// Stop buffering n lagging (linux issue).
		Toolkit.getDefaultToolkit().sync();
	}

	//Reads keyboard input for moving when key is pressed down
	private class KeyPressing extends KeyAdapter {

		public void keyPressed(KeyEvent e) {

			switch (e.getKeyCode()) {

			// Moves hero within left and right boundarys
			case KeyEvent.VK_RIGHT:
				if (!press && hero.getXLoc() < 695) {
					right = 8;
					press = true;
				}
				break;
			case KeyEvent.VK_LEFT:
				if (!press && hero.getXLoc() > 0) {
					left = 8;
					press = true;
				}
				break;
			case KeyEvent.VK_UP:
				if (!press) {
					up = 10;
					press = true;
				}
				break;
			case KeyEvent.VK_DOWN:
				if (!press) {
					down = 10;
					press = true;
				}
				break;
			case KeyEvent.VK_CONTROL:
				if (!invincibility && invDuration < 150)
					invincibility = true;
				break;
			case KeyEvent.VK_SHIFT:
				if (gameLoop.isRunning())
					gameLoop.stop();
				else
					gameLoop.start();
				break;
			case KeyEvent.VK_ENTER:
				if (!gameLoop.isRunning()) {
					newGame = true;
					newGame();
				}
				break;
			}
		}

//Reads keyboard for stopping when key is not pressed down
		public void keyReleased(KeyEvent e) {

			switch (e.getKeyCode()) {

			case KeyEvent.VK_RIGHT:
				hero.setXDir(0);
				break;
			case KeyEvent.VK_LEFT:
				hero.setXDir(0);
				break;
			case KeyEvent.VK_UP:
				hero.setYDir(2);
				break;
			case KeyEvent.VK_DOWN:
				hero.setYDir(2);
				break;
			}
		}
	}
}


