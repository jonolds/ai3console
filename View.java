import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import javax.swing.JFrame;
import javax.swing.border.TitledBorder;
import java.awt.SystemColor;

public class View extends JFrame implements ActionListener {
	public static final int REPLAY_GRANULARITY = 30;
	Controller controller;
	MyPanel panel;
	
	Model model;
	Object secret_symbol; // used to limit access to methods that agents could potentially use to cheat
	ArrayList<Controller> replayPoints;
	int slomo;
	int skipframes;

	public View(Controller c, Model m, Object symbol) throws Exception {
		setAlwaysOnTop(true);
		this.controller = c;
		this.model = m;
		secret_symbol = symbol;
		// Make the game window
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setTitle("AI Tournament");
		this.setSize(1203, 836);
		
		this.setVisible(true);
		this.replayPoints = new ArrayList<Controller>();
		getContentPane().setLayout(null);
		
		MyPanel panel = new MyPanel(this);
		panel.setBorder(new TitledBorder(null, "", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel.addMouseListener(c);
		panel.setBounds(0, 0, 1230, 610);
		getContentPane().add(panel);
		
		Console console = new Console(this);
		console.setBackground(SystemColor.inactiveCaption);
		console.setBounds(0, 611, 1187, 212);
		for(int i = 0; i < model.sprites_blue.size(); i++)
			console.addAgentInfoBox(model.sprites_blue.get(i), "blue", i);
		for(int i = 0; i < model.sprites_red.size(); i++)
			console.addAgentInfoBox(model.sprites_red.get(i), "red", i);
		getContentPane().add(console);
	}
	public void actionPerformed(ActionEvent evt) { repaint(); } // indirectly calls MyPanel.paintComponent

	void doInstantReplay(int x) {
		if(x >= 1190) { // If the user clicked in the slomo box
			if(slomo > 0) slomo = 0;
			else slomo = 5;
			return;
		}
		int i = x * (int)Controller.MAX_ITERS / (1200 * REPLAY_GRANULARITY);
		if(i < replayPoints.size()) {
			System.out.println("Replaying " + Integer.toString(i));
			Controller c = replayPoints.get(i);
			if(this.panel.getMouseListeners()[0] != controller)
				System.out.println("other listener?");
			this.model = c.getModel();
			this.controller = c;
			MouseListener[] oldListeners = panel.getMouseListeners();
			if(oldListeners.length > 0)
				this.panel.removeMouseListener(oldListeners[0]);
			this.panel.addMouseListener(controller);
			replayPoints.set(i, controller.makeReplayPoint(secret_symbol));
		} else
			System.out.println("Cannot replay the future");
	}
}