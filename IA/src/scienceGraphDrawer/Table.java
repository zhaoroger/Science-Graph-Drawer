package scienceGraphDrawer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import javax.swing.text.*;

import java.util.*;

public class Table{
	private String[] columnnames = {"#", "x", "y"};
	static JTable table;
	JPanel panel = new JPanel(); //main component
	static ArrayList<Double>xvalues = new ArrayList<Double>();
	static ArrayList<Double>yvalues = new ArrayList<Double>();
	private Integer[][] data = {{},
			{}};
	private int rowcount = 3;
	JButton addbutton, removebutton, clearbutton;
	static JButton columnname = new JButton("Name Columns");
	static JButton submitbutton = new JButton("Submit Values");
	private boolean columnnamecheck = true;
	private String xstring = "x";
	private String ystring = "y";
	public Table()
	{
		@SuppressWarnings("serial")
		DefaultTableModel model = new DefaultTableModel(data, columnnames)
		{
			public boolean isCellEditable(int row, int column)
			{
				switch(column)
				{
				case 0:
					return false;
				default:
					return true;
				}
			}
		};
		table = new JTable(model);
		table.setIntercellSpacing(new Dimension(0,0)); //makes spacing look more like a table
		table.setPreferredScrollableViewportSize(new Dimension(200, 250)); //sets the table frame size
		table.setFillsViewportHeight(true);

		CustomRenderer cr = new CustomRenderer(table.getDefaultRenderer(Object.class), Color.black, Color.black,
				Color.black, Color.black); 
		table.setDefaultRenderer(Object.class, cr);
		// ^ my mac has issues formatting so this connects to the renderer and fixes the formatting a bit

		table.getTableHeader().setReorderingAllowed(false);
		table.getTableHeader().setResizingAllowed(false);
		// ^ stops user from reordering no real reason for them to be unable to reorder things, i just would rather they cant

		((DefaultTableCellRenderer)table.getTableHeader().getDefaultRenderer()).setHorizontalAlignment
		(JLabel.CENTER);
		TableColumn trials = table.getColumnModel().getColumn(0);
		TableColumn xcolumn = table.getColumnModel().getColumn(1);
		TableColumn ycolumn = table.getColumnModel().getColumn(2);
		table.setRowHeight(20);
		trials.setPreferredWidth(2);
		xcolumn.setCellRenderer(cr);
		ycolumn.setCellRenderer(cr);
		//makes stuff centered and makes it so that you can't input "0." or something that ends with a decimal

		JTextField textbox = new JTextField(); //make the textbox for the cells for future adjustment
		xcolumn.setCellEditor(new DefaultCellEditor(textbox));
		ycolumn.setCellEditor(new DefaultCellEditor(textbox));
		for(int i = 0; i<=table.getRowCount()-1; i++)
			table.setValueAt(Integer.toString(i+1), i, 0);
		((AbstractDocument)textbox.getDocument()).setDocumentFilter(new doclistener()); 
		/*document listener for the cells to prevent
		 * 1. strings
		 * 2. multiple decimals
		 */
		JScrollPane scrollPane = new JScrollPane(table);
		addbutton = new JButton("Add Row");
		removebutton = new JButton("Remove Row");
		clearbutton = new JButton("Clear Table Values");

		class tablebuttonclick implements ActionListener
		{
			public void actionPerformed(ActionEvent e)
			{

				if(e.getSource()==addbutton) //add rows
				{
					DefaultTableModel dtm = (DefaultTableModel) table.getModel();
					if(dtm.getRowCount() != 20)
					{
						dtm.addRow(new String[] {""});
						table.setValueAt(Integer.toString(rowcount), rowcount-1, 0);
						rowcount++;
					}
					else
						JOptionPane.showMessageDialog(null, "Max number of rows is 20");
				}
				if(e.getSource()==removebutton) //remove rows
				{
					DefaultTableModel dtm = (DefaultTableModel) table.getModel();
					if(dtm.getRowCount() > 2)
					{
						dtm.removeRow(table.getRowCount()-1);
						rowcount--;
					}
					else
						JOptionPane.showMessageDialog(null, "You have to have at least 2 rows");
				}
				if(e.getSource() == clearbutton)
				{
					for (int i = 0; i < table.getRowCount(); i++) 
					{
						for(int j = 1; j < table.getColumnCount(); j++) 
						{
							table.setValueAt("", i, j);
						}
					}
					xvalues.clear();
					yvalues.clear();
					Curvedraw.panel.repaint();
				}
				if(e.getSource()==columnname) //opens frame to change column names
				{
					new columnnamechanger();
					table.repaint();
				}
				if(e.getSource()==submitbutton) //makes grid/ for now outputs table values
				{
					xvalues.clear();
					yvalues.clear();
					for (int row = 0; row < Table.table.getRowCount(); row++)
					{
						for (int column = 1; column <= 2; column++)
						{
							if(Table.table.getValueAt(row, column) == null || Table.table.getValueAt(row, column) == "")
							{
								xvalues.clear();
								yvalues.clear();
								row = Table.table.getRowCount();
								column = 3;
							}
							else if(Table.table.getValueAt(row, column) != null)
							{
								try
								{
									double i = Double.parseDouble(Table.table.getValueAt(row, column).toString());
									if(column == 1)
										xvalues.add(i);
									else
										yvalues.add(i);
								} 
								catch(NumberFormatException ex)
								{
									if(Table.table.getValueAt(row, column) == "")
									{
										String i = (String)Table.table.getValueAt(row, column) + "0";
										double ii = Double.parseDouble(i);
										if(column == 1)
											xvalues.add(ii);
										else
											yvalues.add(ii);
									}
								}
							}
						}
					}
					Curvedraw.panel.repaint();
				}
			}
		}
		addbutton.addActionListener(new tablebuttonclick());
		removebutton.addActionListener(new tablebuttonclick());
		clearbutton.addActionListener(new tablebuttonclick());
		columnname.addActionListener(new tablebuttonclick());
		submitbutton.addActionListener(new tablebuttonclick());

		//gui formatting
		JPanel container = new JPanel();
		container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
		JPanel panel1 = new JPanel();
		JPanel panel2 = new JPanel();
		panel1.add(scrollPane);
		panel1.setSize(200,200);
		panel2.setSize(200,250);
		panel2.setLayout(new GridLayout(0, 1));
		panel2.add(addbutton);
		panel2.add(removebutton);
		panel2.add(clearbutton);
		panel2.add(columnname);
		panel2.add(submitbutton);
		container.add(panel1);
		container.add(panel2);
		panel.add(container);
		panel.setSize(200,375);
	}

	//frame to open column name changer
	@SuppressWarnings("serial")
	private class columnnamechanger extends JFrame
	{
		public columnnamechanger()
		{
			if(columnnamecheck == true)
			{
				columnnamecheck = false;
				this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				this.setUndecorated(true);
				JTextField xvariable = new JTextField();
				xvariable.setText(xstring);
				JTextField yvariable = new JTextField();
				yvariable.setText(ystring);
				JButton submittext = new JButton("Submit");
				JButton cancel = new JButton("Cancel");
				class textedit implements ActionListener
				{
					public void actionPerformed(ActionEvent e)
					{
						if(e.getSource() == cancel)
						{
							dispose();
							columnnamecheck = true;
						}
						else
						{
							xstring = xvariable.getText();
							ystring = yvariable.getText();
							table.getColumnModel().getColumn(1).setHeaderValue(xstring);
							table.getColumnModel().getColumn(2).setHeaderValue(ystring);
							table.getTableHeader().repaint();

							dispose();
							columnnamecheck = true;
						}
					}
				}
				xvariable.addActionListener(new textedit());
				yvariable.addActionListener(new textedit());
				submittext.addActionListener(new textedit());
				cancel.addActionListener(new textedit());
				xvariable.setPreferredSize(new Dimension(100,40));
				yvariable.setPreferredSize(new Dimension(100,40));
				JPanel panel = new JPanel();
				panel.setLayout(new GridLayout(2,2));
				panel.add(xvariable);
				panel.add(yvariable);
				panel.add(submittext);
				panel.add(cancel);
				this.setResizable(false);
				this.add(panel);
				this.pack();
				this.setLocationRelativeTo(null);
				this.setVisible(true);
			}
		}
	}
	//custom renderer for table
	private static class CustomRenderer implements TableCellRenderer{
		private TableCellRenderer render;
		private Border b;
		public CustomRenderer(TableCellRenderer r, Color top, Color left,Color bottom, Color right){
			render = r;
			((JLabel)render).setHorizontalAlignment(SwingConstants.CENTER); //centers things
			b = BorderFactory.createCompoundBorder(); //this and below is to make the formatting on mac less bad
			b = BorderFactory.createCompoundBorder(b, BorderFactory.createMatteBorder(1,0,0,0,top));
			b = BorderFactory.createCompoundBorder(b, BorderFactory.createMatteBorder(0,1,0,0,left));
			b = BorderFactory.createCompoundBorder(b, BorderFactory.createMatteBorder(0,0,1,0,bottom));
			b = BorderFactory.createCompoundBorder(b, BorderFactory.createMatteBorder(0,0,0,1,right));
		}

		//does the thing to things that ends in decimals
		public Component getTableCellRendererComponent(JTable table,
				Object value, boolean isSelected, boolean hasFocus, int row,
				int column) {
			String content = (String) table.getValueAt(row, column);
			if(content != null && content.endsWith("."))
			{
				content = content+"0";
			}
			if(content != null && content.startsWith("."))
			{
				content = "0"+content;
			}

			JComponent result = (JComponent)render.getTableCellRendererComponent(table, content, isSelected, hasFocus, row, column);
			result.setBorder(b);
			return result;
		}
	}

	public JComponent getMainComponent() 
	{
		return panel;
	}
	/*
	public static void main(String[] args) {
		Table t = new Table();
		JFrame frame = new JFrame();
		frame.add(t.getMainComponent());
		frame.pack();
		frame.setResizable(false);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	 */
}
