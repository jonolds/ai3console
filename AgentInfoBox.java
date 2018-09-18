import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.SystemColor;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;
import java.awt.Dimension;
import java.awt.ComponentOrientation;
import java.awt.FlowLayout;

class AgentInfoBox extends JPanel {
	final int width = 90, height = 180;

	Console console;
	String team;
	Color color;
	int num;
	Model.Sprite sprite;
	JLabel agentInfoLabel;
	
	public AgentInfoBox() {
		FlowLayout flowLayout = (FlowLayout) getLayout();
		flowLayout.setAlignOnBaseline(true);
		setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
		setSize(new Dimension(80, 180));
		this.setLayout(flowLayout);
		this.setAlignmentX(Component.LEFT_ALIGNMENT);
		this.setBackground(SystemColor.controlHighlight);
		this.setFont(new Font("Tahoma", Font.BOLD, 11));
		this.setBorder(new TitledBorder(null, "", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		this.setVisible(true);
		
	}
	
	public AgentInfoBox init(Console console, Model.Sprite s, String color, int num) {
		this.console = console;
		this.sprite = s;
		this.team = color;
		this.num = num;
		this.color = (team.equals("blue") ? Color.BLUE : Color.red);
		
		this.setBounds(console.nextOpenHorizSpace, 0, this.width, this.height);

		
		
		agentInfoLabel = new JLabel(color + Integer.toString(num));
		agentInfoLabel.setFont(new Font("Tahoma", Font.BOLD, 12));
		agentInfoLabel.setForeground(this.color);
		agentInfoLabel.setBounds(5, 0, this.getPreferredSize().width, 15);
		agentInfoLabel.setVisible(true);
		
		this.add(agentInfoLabel);
		
		this.setVisible(true);
		return this;
	}
}