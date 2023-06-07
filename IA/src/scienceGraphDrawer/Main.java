package scienceGraphDrawer;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class Main {
	public static void main(String[] args) {
		Curvedraw c = new Curvedraw();
		Table t = new Table();
		Buttonlayout b = new Buttonlayout();
		JFrame frame = new JFrame("Science Graph Drawer");
		JPanel container = new JPanel();
		JPanel subcontainer = new JPanel();
		subcontainer.setLayout(new BoxLayout(subcontainer, BoxLayout.Y_AXIS));
		subcontainer.add(t.getMainComponent());
		subcontainer.add(b.getMainComponent());
		container.setLayout(new BoxLayout(container, BoxLayout.X_AXIS));
		container.add(subcontainer);
		container.add(c.getMainComponent());
		frame.add(container);
		frame.pack();
		frame.setResizable(false);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
}
