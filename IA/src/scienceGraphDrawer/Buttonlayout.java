package scienceGraphDrawer;
import java.awt.*;
import javax.swing.*;

public class Buttonlayout {
	JPanel panel = new JPanel();
	public Buttonlayout()
	{
		JPanel buttonpanel = new JPanel();
		buttonpanel.setLayout(new GridLayout(0,1));
		JScrollPane sp = new JScrollPane(buttonpanel, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		sp.setPreferredSize(new Dimension(200, 200));
		sp.getVerticalScrollBar().setUnitIncrement(16);
		buttonpanel.add(Curvedraw.curve);
		buttonpanel.add(Curvedraw.modify);
		buttonpanel.add(Curvedraw.delete);
		buttonpanel.add(Curvedraw.clear);
		buttonpanel.add(Curvedraw.colorchange);
		buttonpanel.add(Curvedraw.modifytextbutton);
		buttonpanel.add(Curvedraw.newgrid);
		panel.add(sp);
	}
	public JComponent getMainComponent() 
	{
		return panel;
	}
	
	public static void main(String[] args) {
		Buttonlayout b = new Buttonlayout();
		JFrame frame = new JFrame();
		frame.add(b.getMainComponent());
		frame.setSize(500,500);
		frame.setResizable(false);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

}




