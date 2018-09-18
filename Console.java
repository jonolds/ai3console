import java.awt.SystemColor;
import java.util.Vector;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;


class Console extends JPanel {
	View view;
	Model model;
	int nextOpenHorizSpace = 0, height = 180;
	
	Vector<AgentInfoBox> agentBoxes;
	
	
	void addAgentInfoBox(Model.Sprite s, String color, int num) {
		AgentInfoBox agentInfo = new AgentInfoBox();
//		AgentInfoBox agentInfo = new AgentInfoBox(this, s, color, num);
		agentBoxes.add(agentInfo.init(this, s, color, num));
		this.add(agentInfo);
		nextOpenHorizSpace += agentInfo.width;
	}
	
	public Console() {
		this.setBorder(new EmptyBorder(10, 10, 10, 10));
		this.setSize(1203, 200);
		this.setBackground(SystemColor.menu);
		this.setLayout(null);
		this.setVisible(true);
	}
	
	void init(Model m, View v) {
		this.model = m;
		this.view = view;
		//Add info for each agent
		for(int i = 0; i < model.sprites_blue.size(); i++)
			addAgentInfoBox(model.sprites_blue.get(i), "blue", i);
		for(int i = 0; i < model.sprites_red.size(); i++)
			addAgentInfoBox(model.sprites_red.get(i), "red", i);
	}
}