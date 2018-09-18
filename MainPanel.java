import java.awt.Color;
import java.awt.ComponentOrientation;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

class MainPanel extends JPanel {
	Controller control;
	View view;
	public static final int FLAG_IMAGE_HEIGHT = 25;
	Image image_robot_blue, image_robot_red, image_broken, image_flag_blue, image_flag_red;
	MySoundClip sound_doing;
	
	public MainPanel() {
		this.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
		this.setBorder(new TitledBorder(null, "", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		this.setBounds(0, 0, 1230, 610);
		this.setVisible(true);
	}

	void init(Controller c, View view) throws Exception {
		this.control = c;
		this.view = view;
		this.addMouseListener(this.control);
		this.image_robot_blue = ImageIO.read(new File("robot_blue.png"));
		this.image_robot_red = ImageIO.read(new File("robot_red.png"));
		this.image_broken = ImageIO.read(new File("broken.png"));
		this.image_flag_blue = ImageIO.read(new File("flag_blue.png"));
		this.image_flag_red = ImageIO.read(new File("flag_red.png"));
		this.sound_doing = new MySoundClip("metal_doing.wav", 3);
	}

	private void drawTerrain(Graphics g) {
		byte[] terrain = this.view.model.getTerrain(this.view.secret_symbol);
		int posBlue = 0;
		int posRed = (60 * 60 - 1) * 4;
		for(int y = 0; y < 60; y++) {
			for(int x = 0; x < 60; x++) {
				int bb = terrain[posBlue + 1] & 0xff;
				int gg = terrain[posBlue + 2] & 0xff;
				int rr = terrain[posBlue + 3] & 0xff;
				g.setColor(new Color(rr, gg, bb));
				g.fillRect(10 * x, 10 * y, 10, 10);
				posBlue += 4;
			}
			for(int x = 60; x < 120; x++) {
				int bb = terrain[posRed + 1] & 0xff;
				int gg = terrain[posRed + 2] & 0xff;
				int rr = terrain[posRed + 3] & 0xff;
				g.setColor(new Color(rr, gg, bb));
				g.fillRect(10 * x, 10 * y, 10, 10);
				posRed -= 4;
			}
		}
	}

	private void drawSprites(Graphics g) {
		ArrayList<Model.Sprite> sprites_blue = this.view.model.getSpritesBlue(this.view.secret_symbol);
		for(int i = 0; i < sprites_blue.size(); i++) {
			// Draw the robot image
			Model.Sprite s = sprites_blue.get(i);
			if(s.energy >= 0) {
				g.drawImage(image_robot_blue, (int)s.x - 12, (int)s.y - 32, null);
				// Draw energy bar
				g.setColor(new Color(0, 0, 128));
				g.drawRect((int)s.x - 18, (int)s.y - 32, 3, 32);
				int energy = (int)(s.energy * 32.0f);
				g.fillRect((int)s.x - 17, (int)s.y - energy, 2, energy);
			}
			else
				g.drawImage(image_broken, (int)s.x - 12, (int)s.y - 32, null);
			// Draw selection box
			if(i == this.view.controller.getSelectedSprite()) {
				g.setColor(new Color(100, 0, 0));
				g.drawRect((int)s.x - 22, (int)s.y - 42, 44, 57);
			}
		}
		ArrayList<Model.Sprite> sprites_red = this.view.model.getSpritesRed(this.view.secret_symbol);
		for(int i = 0; i < sprites_red.size(); i++) {
			// Draw the robot image
			Model.Sprite s = sprites_red.get(i);
			if(s.energy >= 0) {
				g.drawImage(image_robot_red, (int)(Model.XMAX - 1 - s.x) - 12, (int)(Model.YMAX - 1 - s.y) - 32, null);
				// Draw energy bar
				g.setColor(new Color(128, 0, 0));
				g.drawRect((int)(Model.XMAX - 1 - s.x) + 14, (int)(Model.YMAX - 1 - s.y) - 32, 3, 32);
				int energy = (int)(s.energy * 32.0f);
				g.fillRect((int)(Model.XMAX - 1 - s.x) + 15, (int)(Model.YMAX - 1 - s.y) - energy, 2, energy);
			}
			else
				g.drawImage(image_broken, (int)(Model.XMAX - 1 - s.x) - 12, (int)(Model.YMAX - 1 - s.y) - 32, null);
		}
	}

	private void drawBombs(Graphics g) {
		ArrayList<Model.Bomb> bombs = this.view.model.getBombsFlying(this.view.secret_symbol);
		for(int i = 0; i < bombs.size(); i++) {
			Model.Bomb b = bombs.get(i);
			int x = (int)b.getX();
			int y = (int)b.getY();
			int height = (int)(0.01 * b.position * (b.distance - b.position));
			g.setColor(new Color(128, 64, 192));
			g.fillOval(x - 5, y - 5 - height, 10, 10);
			g.setColor(new Color(100, 100, 100));
			g.fillOval(x - 5, y - 5, 10, 10);
		}
		bombs = this.view.model.getBombsExploding(this.view.secret_symbol);
		for(int i = 0; i < bombs.size(); i++) {
			Model.Bomb b = bombs.get(i);
			int x = (int)b.getX();
			int y = (int)b.getY();
			if(b.isDetonating()) { sound_doing.play(); }
			g.setColor(new Color(128, 0, 64));
			int r = (int)(b.position - b.distance);
			g.drawOval(x - r, y - r, 2 * r, 2 * r);
			r = (int)Model.BLAST_RADIUS;
			g.drawOval(x - r, y - r, 2 * r, 2 * r);
		}
	}

	private void drawTitles(Graphics g) {
		g.setColor(new Color(0, 0, 128));
		g.drawString(this.view.controller.getBlueName(), (int)Model.XFLAG, (int)Model.YFLAG - 2 * FLAG_IMAGE_HEIGHT);
		g.setColor(new Color(128, 0, 0));
		g.drawString(this.view.controller.getRedName(), (int)Model.XFLAG_OPPONENT - 80,  (int)Model.YFLAG_OPPONENT - 2 * FLAG_IMAGE_HEIGHT);
	}

	private void drawFlags(Graphics g) {	// Blue/Red
		g.drawImage(image_flag_blue, (int)Model.XFLAG, (int)Model.YFLAG - FLAG_IMAGE_HEIGHT, null);
		g.setColor(new Color(0, 0, 128));
		g.drawRect((int)Model.XFLAG - 3, (int)Model.YFLAG - 25, 3, 32);
		int energy = (int)(this.view.model.getFlagEnergySelf() * 32.0f);
		g.fillRect((int)Model.XFLAG - 2, (int)Model.YFLAG + 7 - energy, 2, energy);

		g.drawImage(image_flag_red, (int)Model.XFLAG_OPPONENT,  (int)Model.YFLAG_OPPONENT - FLAG_IMAGE_HEIGHT, null);
		g.setColor(new Color(128, 0, 0));
		g.drawRect((int)Model.XFLAG_OPPONENT - 3, (int)Model.YFLAG_OPPONENT - 25, 3, 32);
		energy = (int)(this.view.model.getFlagEnergyOpponent() * 32.0f);
		g.fillRect((int)Model.XFLAG_OPPONENT - 2, (int)Model.YFLAG_OPPONENT + 7 - energy, 2, energy);
	}

	private void drawTime(Graphics g) {
		int iter = (int)this.view.controller.getIter();
		if(this.view.replayPoints.size() < iter / View.REPLAY_GRANULARITY) {
			this.view.replayPoints.add(this.view.controller.makeReplayPoint(this.view.secret_symbol));
			//System.out.println("Recording " + Integer.toString(replayPoints.size()));
		}
		int i = 1200 * iter / (int)Controller.MAX_ITERS;
		int j = this.view.replayPoints.size() * View.REPLAY_GRANULARITY * 1200 / (int)Controller.MAX_ITERS;
		g.setColor(new Color(128, 128, 128));
		g.fillRect(i, 600, j - i, 10);
		g.setColor(new Color(0, 128, 128));
		g.fillRect(0, 600, i, 10);
		// Draw slomo box
		if(this.view.slomo > 0) g.fillRect(1190, 600, 10, 10);
		else g.drawRect(1190, 600, 10, 10);
	}

	public void paintComponent(Graphics g) {
		if(this.view.skipframes > 0)
			this.view.skipframes--;
		else {
			// Give the agents a chance to make decisions
			if(!this.view.controller.update()) {
				this.view.model.setPerspectiveBlue(this.view.secret_symbol);
				if(this.view.model.getFlagEnergySelf() < 0.0f && this.view.model.getFlagEnergyOpponent() >= 0.0f)
					System.out.println("\nRed wins!");
				else if(this.view.model.getFlagEnergyOpponent() < 0.0f && this.view.model.getFlagEnergySelf() >= 0.0f)
					System.out.println("\nBlue wins!");
				else
					System.out.println("\nTie.");
				this.view.dispatchEvent(new WindowEvent(this.view, WindowEvent.WINDOW_CLOSING)); // The game is over, so close this window
			}
			this.view.skipframes = this.view.slomo;
		}
		// Draw the view
		this.view.model.setPerspectiveBlue(this.view.secret_symbol);
		drawTerrain(g);
		drawTitles(g);
		drawFlags(g);
		drawSprites(g);
		drawBombs(g);
		drawTime(g);
	}
	
	class MySoundClip {
		Clip[] clips;
		int pos;
		MySoundClip(String filename, int copies) throws Exception {
			clips = new Clip[copies];
			for(int i = 0; i < copies; i++) {
				AudioInputStream inputStream = AudioSystem.getAudioInputStream(new File(filename));
				DataLine.Info info = new DataLine.Info(Clip.class, inputStream.getFormat());
				clips[i] = (Clip)AudioSystem.getLine(info);
				clips[i].open(inputStream);
			}
			pos = 0;
		}
		void play() {
//			clips[pos].setFramePosition(0);
//			clips[pos].loop(0);
//			if(++pos >= clips.length)
//				pos = 0;
		}
	}
}