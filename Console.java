import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.SystemColor;
import java.util.Vector;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;


class Console extends JPanel {
	private final View view;
	int nextOpenHorizSpace = 0, height = 180;
	
	Vector<AgentInfoBox> agentBoxes = new Vector<>();
	
	
	void addAgentInfoBox(Model.Sprite s, String color, int num) {
		AgentInfoBox agentInfo = new AgentInfoBox(this, s, color, num);
		agentBoxes.add(agentInfo);
		this.add(agentBoxes.lastElement());
		nextOpenHorizSpace += agentInfo.width;
	}
	
	
	class AgentInfoBox extends JPanel {
		final int width = 90, height = 180;

		Console console;
		String team;
		Color color;
		int num;
		Model.Sprite sprite;
		JLabel agentInfoLabel;
		
		
		AgentInfoBox(Console console, Model.Sprite s, String color, int num) {
			this.console = console;
			this.sprite = s;
			this.team = color;
			this.num = num;
			this.color = (team.equals("blue") ? Color.BLUE : Color.red);
			
			this.setBounds(nextOpenHorizSpace, 0, this.width, this.height);
			this.setLayout(null);
			this.setAlignmentX(Component.LEFT_ALIGNMENT);
			this.setBackground(SystemColor.controlHighlight);
			this.setFont(new Font("Tahoma", Font.BOLD, 11));
			this.setBorder(new TitledBorder(null, "", TitledBorder.LEADING, TitledBorder.TOP, null, null));
			
			
			agentInfoLabel = new JLabel(color + Integer.toString(num));
			agentInfoLabel.setFont(new Font("Tahoma", Font.BOLD, 12));
			agentInfoLabel.setForeground(this.color);
			agentInfoLabel.setBounds(5, 0, this.getPreferredSize().width, 15);
			agentInfoLabel.setVisible(true);
			
			this.add(agentInfoLabel);
			
			this.setVisible(true);
		}
	}

	Console(View view) {
		this.view = view;
		this.setBorder(new EmptyBorder(10, 10, 10, 10));
		this.setSize(1203, 200);
		this.setBackground(SystemColor.menu);
		setLayout(null);
	}
}