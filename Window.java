//Class for JFrame extension
import javax.swing.*; //for java gui

//Window that holds the display JPanel.
public class Window extends JFrame {

	//Variable for final JFrame size
	private final int HEIGHT = 800; 
	private final int WIDTH = 800;

	//Default constructor
	Window(boolean pause) {

		//Set the title of the frame
		setTitle("Taxi Run (Obstacle Course game)-Ajia Athar, Saada Asghar, Duaa Ali");

		//Set the size of the frame
		setSize(WIDTH, HEIGHT);

		//Set window to screen center
		setLocationRelativeTo(null);

		//Specify the close button action
		//setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		//set resize.
		//setResizable(false);

		//Add panel to frame.
		add(new Display(pause));

		//Display the window.
		setVisible(true);
	}


	//Start program
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		//Pause game if first run
		final boolean pause = true;

		//Create window for game
		new Window(pause);
	}
}

