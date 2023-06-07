package scienceGraphDrawer;
import javax.swing.*;
import javax.swing.JSpinner.DefaultEditor;

import java.awt.*;
import java.util.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.math.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.AbstractDocument;

public class Curvedraw 
{
	//initializing stuff for access in multiple areas
	static JButton curve = new JButton("Draw Curve");
	static JButton modify = new JButton("Modify");
	static JButton delete = new JButton("Delete curve");
	static JButton clear = new JButton("Clear All");
	static JButton colorchange = new JButton("Change line color");
	static JButton modifytextbutton = new JButton("Modify Text");
	static JButton newgrid = new JButton("Set New Grid");
	private static JCheckBox gridtoggle = new JCheckBox("Grid", true);
	private static JCheckBox numbertoggle = new JCheckBox("Axis Numbers", true);
	JPanel canvas;
	static JPanel panel = new JPanel();
	private JTextField status;
	private int r, gr, b;
	private String xaxistext = "X axis";
	private String yaxistext = "Y axis";
	private String title = "Title";
	private int xaxiss = 100;
	private int xaxise = 750;
	private int xaxisy = 580;
	private int yaxiss = 30;
	private int yaxise = 580;
	private int yaxisx = 100; 
	private int intdistance = 20;
	private double tempxbase = 0;
	private double tempxadditive = 1;
	private double tempybase = 0;
	private double tempyadditive = 1;
	private int tempxintervalnumber = 10;
	private int tempyintervalnumber = 10;
	private boolean griddrawcheck = true;
	private boolean numberdrawcheck = true;
	private boolean addtextcheck = true;
	private boolean colorchangecheck = true;
	private boolean gridchangecheck = true;
	private int sigdig = 3;
	//http://charm.cs.uiuc.edu/~gzheng/java/bezier/bezier.java.html
	public Curvedraw()
	{
		canvas = new graphpanel();
		panel.add(canvas);
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		class infobuttonclick implements ActionListener
		{
			public void actionPerformed(ActionEvent e)
			{
				String txt;

				if(e.getSource() == clear)
					txt = "All curves are cleared.";
				else if(e.getSource() == curve)
					txt = "Draw Curve: click to add a point, double click to finish drawing"; 
				else if(e.getSource() == modify)
					txt = "Modify: select a point that forms a line, drag mouse to modify and release to finish.";
				else if(e.getSource() == delete)
					txt = "Delete: Click on a point that forms a line to delete";
				else if(e.getSource() == colorchange)
					txt = "A new window will open. Select color based on button";
				else if(e.getSource() == modifytextbutton)
					txt = "A new window will open. First textbox is X, second is Y, third is title.";
				else if(e.getSource() == newgrid)
					txt = "A new window will open. Example: 0, 5, 5 in X makes the axis go from 0-25 in increments of 5. No sigdigs in value entry.";
				else if(e.getSource() == Table.columnname)
					txt = "A new window will open. The first textbox is X axis header and the second is Y axis header";
				else if(e.getSource() == Table.submitbutton)
					txt = "Points from table are drawn based on grid. To clear points, clear all cells and reclick the Submit Values button";
				else
					txt = "";

				status.setText(txt);
			}
		}//actionlistener to modify bottom textfield text for simple instructions
		curve.addActionListener(new infobuttonclick());
		modify.addActionListener(new infobuttonclick());
		delete.addActionListener(new infobuttonclick());
		clear.addActionListener(new infobuttonclick());
		colorchange.addActionListener(new infobuttonclick());
		modifytextbutton.addActionListener(new infobuttonclick());
		newgrid.addActionListener(new infobuttonclick());
		Table.submitbutton.addActionListener(new infobuttonclick());
		Table.columnname.addActionListener(new infobuttonclick());

		status = new JTextField("Draw Curve: click to add a point, double click to finish drawing", 45);
		status.setEditable(false);
		panel.add(status);//settings for bottom text
		
		JPanel checkboxpanel = new JPanel();
		gridtoggle.setBounds(100,150,50,50);
		numbertoggle.setBounds(100,150,50,50);
		checkboxpanel.add(gridtoggle);
		checkboxpanel.add(numbertoggle);
		panel.add(checkboxpanel); //add toggles for grid

	}

	@SuppressWarnings("serial")
	class graphpanel extends JPanel
	{
		private PointList pts[];
		private int line;
		private int curObj;
		private boolean drawing;  
		private int action;
		private final int curving=1, modifying=2, deleting=3, neutral=4;
		private Image img = null;
		private Graphics background;
		private Graphics2D background2 = (Graphics2D) background;

		public graphpanel() 
		{
			pts = new PointList[200];
			line = -1;
			drawing = false;
			action = curving;

			addMouseListener(new MouseAdapter() 
			{ 
				public void mouseReleased(MouseEvent e) 
				{ 
					int x = e.getX();
					int y = e.getY();
					if(action == curving)
					{
						if(drawing) {
							if(!pts[line].addPoint(x,y))
							{
								
								drawing = false;
								line --;
								setcursor(drawing);
							}
						}
						repaint();
					}
					if(action == modifying)
					{
						if(drawing) 
						{
							drawing = false;
							setcursor(drawing);
						}
					}
					if(action == deleting)
					{
						if(curObj != -1)
						{
							for(int i=curObj; i< line; i++) 
								pts[i] = pts[i+1];
							line--;
							repaint();
						}
					}
				} 
			}); 


			addMouseListener(new MouseAdapter(){
				public void mousePressed(MouseEvent e)
				{
					int x = e.getX();
					int y = e.getY();
					if(action == curving)
					{
						if(drawing == false)
						{
							line ++;
							if (action == curving) 
								pts[line] = new curve();

							pts[line].addPoint(x,y);
							drawing = true;
							setcursor(drawing);
						}
						else 
						{
							if(e.getClickCount() == 2) 
							{
								if(!pts[line].done())
								{
									
									line --;
								}
								drawing = false;
								setcursor(drawing);
							}
						}
					}
					if(action == modifying)
					{
						if(curObj != -1) {
							drawing = true;
							setcursor(drawing);
						}
					}
				}
			});

			addMouseMotionListener(new MouseAdapter() 
			{
				public void mouseMoved(MouseEvent e)
				{
					int x = e.getX();
					int y = e.getY();
					if(action == curving)
					{
						if(drawing)
						{
							pts[line].changePoint(x,y);
							repaint();
						}
					}
					if(action == modifying || action == deleting)
					{
						if(drawing == false)
						{
							int oldObj = curObj;
							curObj = -1;
							for(int i=0; i<=line; i++)
							{
								if(pts[i].inRegion(x,y) != -1) 
								{
									curObj = i;
									break; 
								}
							}
							if(oldObj != curObj) 
								repaint();
						}
					}
				}
			});

			addMouseMotionListener(new MouseAdapter() 
			{
				public void mouseDragged(MouseEvent e) 
				{
					int x = e.getX();
					int y = e.getY();
					if (action == modifying)
					{
						if(drawing == true)  
						{
							pts[curObj].changeModPoint(x,y);
							if(!pts[curObj].createFinal())
							{
								
								line --;
							}
							repaint();
						}
					}
				}
			});

			class buttonlistener implements ActionListener
			{
				public void actionPerformed(ActionEvent e)
				{
					if(drawing)
					{
						drawing = false;
						setcursor(drawing);
					}
					if(e.getSource() == clear)
					{
						line = -1;
						repaint();
						return;
					}
					if(action == curving)
					{
						if(drawing) 
							pts[line].done();
					}
					if(e.getSource() == curve)
					{
						action = curving;
						for(int i=0; i<=line; i++)
							pts[i].setShow(false);
						repaint();
					}
					else if(e.getSource() == modify)
					{
						action = modifying;
						for(int i=0; i<=line; i++)
							pts[i].setShow(true);
						repaint();
					}
					else if(e.getSource() == delete)
					{
						action = deleting;
						for(int i=0; i<=line; i++)
							pts[i].setShow(true);
						repaint();
					}
					else if(e.getSource() == colorchange)
					{
						action = neutral;
						new colorchanger();
					}
					else if(e.getSource() == modifytextbutton)
					{
						action = neutral;
						new modifytext();
					}
					else if(e.getSource() == newgrid)
					{
						action = neutral;
						new gridchanger();
					}
				}
			}
			curve.addActionListener(new buttonlistener());
			modify.addActionListener(new buttonlistener());
			delete.addActionListener(new buttonlistener());
			clear.addActionListener(new buttonlistener());
			colorchange.addActionListener(new buttonlistener());
			modifytextbutton.addActionListener(new buttonlistener());
			newgrid.addActionListener(new buttonlistener());
		}

		void setcursor(boolean working)
		{
			Cursor cursor;
			if(working) 
				cursor = new Cursor(Cursor.HAND_CURSOR);
			else
				cursor = new Cursor(Cursor.DEFAULT_CURSOR);
			setCursor(cursor);
		}

		public Dimension getPreferredSize()
		{
			return new Dimension(800,640);
		}


		public void paint(Graphics g) 
		{
			graph(g);
			text(g);
			tablepoints(g);
		}
		
		public void text(Graphics g)
		{
			Graphics2D g2d = (Graphics2D) g;
			g2d.setFont(new Font("TimesRoman", Font.BOLD, 15));
			g2d.drawString(xaxistext, 400-(xaxistext.length()*4), 620);
			g2d.drawString(title, 400-(title.length()*4), 25);
			AffineTransform orig = g2d.getTransform();
			g2d.rotate(-Math.PI/2);
			g2d.drawString(yaxistext, -305-(yaxistext.length()*4), 25);
			g2d.setTransform(orig);
		}
		
		public void tablepoints(Graphics g)
		{
			int xaxislength = xaxise - xaxiss;
			double xportion = xaxislength/tempxintervalnumber;
			double singlevaluex = xportion/tempxadditive;
					
			int yaxislength = yaxise - yaxiss;
			double yportion = yaxislength/tempyintervalnumber;
			double singlevaluey = yportion/tempyadditive;
			
			Iterator<Double> xiter = Table.xvalues.iterator();
			Iterator<Double> yiter = Table.yvalues.iterator();
			
			Graphics2D g2 = (Graphics2D) g;
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		    g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
		    Ellipse2D.Double shape;
			while(xiter.hasNext())
			{
				shape = new Ellipse2D.Double((xiter.next()*singlevaluex)+xaxiss-3.5, yaxise - (yiter.next()*singlevaluey) - 3, 8, 8);
				g2.fill(shape);
			}
		}

		public void graph(Graphics g) 
		{ 
			double xbase = tempxbase;
			double xadditive = tempxadditive;
			double ybase = tempybase;
			double yadditive = tempyadditive;
			int xintervalnumber = tempxintervalnumber;
			int yintervalnumber = tempyintervalnumber;
			int n;
			Dimension d = new Dimension(800,640);

			if(img == null)
			{
				img = createImage(d.width, d.height);
				background2 = (Graphics2D) img.getGraphics();
			}

			background2.setColor(new Color(255,255,255));    //Set color for background
			background2.fillRect(0,0, d.width, d.height);  //Draw Backround

			// draw border
			background2.setStroke(new BasicStroke(1));
			background2.setColor(new Color(0,0,0));
			background2.drawRect(1,1,d.width-3,d.height-3);

			for(n=0; n <= line; n++)
				pts[n].draw(background2);

			g.drawImage(img, 0, 0, this);
			
			Graphics2D g2 = (Graphics2D) g;
			g2.setColor(Color.BLACK);
			g2.setStroke(new BasicStroke(3));
			g2.drawLine(xaxiss,xaxisy,xaxise,xaxisy);
			g2.drawLine(yaxisx, yaxiss, yaxisx, yaxise);

			int xLength = (xaxise - xaxiss) / xintervalnumber;
			int yLength = (yaxise - yaxiss) / yintervalnumber;
			class checkboxevent implements ActionListener
			{
				public void actionPerformed(ActionEvent e)
				{
					if(gridtoggle.isSelected())
					{
						griddrawcheck = true;
						repaint();
					}	
					else
					{
						griddrawcheck = false;
						repaint();
					}
					if(numbertoggle.isSelected())
					{
						numberdrawcheck = true;
						repaint();
					}
					else
					{
						numberdrawcheck = false;
						repaint();
					}

				}
			}
			gridtoggle.addActionListener(new checkboxevent());
			numbertoggle.addActionListener(new checkboxevent());
			if(griddrawcheck == true)
			{
				//draw x axis grid lines
				for(int i = 0; i <= yintervalnumber; i++) 
				{
					g2.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER));
					g2.drawLine(xaxiss, xaxisy - (i * yLength), xaxise, xaxisy - (i * yLength));
				}

				//draw y axis grid lines
				for(int i = 0; i <= xintervalnumber; i++) 
				{
					g2.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER));
					g2.drawLine(yaxisx + (i * xLength), yaxiss, yaxisx + (i * xLength), yaxise);
				}
			}
			if(numberdrawcheck == true)
			{
				for(int i = 0; i <= xintervalnumber; i++)
				{
					BigDecimal bd = new BigDecimal(xbase, MathContext.DECIMAL64);
					bd = bd.round(new MathContext(sigdig, RoundingMode.HALF_UP));
					int precision = bd.precision();
					if (precision < sigdig)
						bd = bd.setScale(bd.scale() + (sigdig-precision));
					g2.drawString(bd.toString(), xaxiss + (i * xLength) - (bd.toString().length() * 3), xaxisy + intdistance);
					xbase += xadditive;
					g2.setStroke(new BasicStroke(3));
					g2.drawLine(xaxiss + (i * xLength), xaxisy, xaxiss + (i * xLength), xaxisy + 5);
				}
				for(int i = 0; i <= yintervalnumber; i++) 
				{
					BigDecimal bd = new BigDecimal(ybase, MathContext.DECIMAL64);
					bd = bd.round(new MathContext(sigdig, RoundingMode.HALF_UP));
					int precision = bd.precision();
					if (precision < sigdig)
						bd = bd.setScale(bd.scale() + (sigdig-precision));
					g2.drawString(bd.toString(), yaxisx - intdistance - (bd.toString().length() * 5), yaxise - (i * yLength) + 4);
					ybase += yadditive;
					g2.setStroke(new BasicStroke(3));
					g2.drawLine(yaxisx - 5, yaxise - (i * yLength), yaxisx, yaxise - (i * yLength));
				}
			}
		}
	}

	@SuppressWarnings("serial")
	private class modifytext extends JFrame
	{
		public modifytext()
		{
			if(addtextcheck == true)
			{
				addtextcheck = false;
				this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				this.setUndecorated(true);
				JTextField xtext = new JTextField(xaxistext);
				JTextField ytext = new JTextField(yaxistext);
				JTextField gtitle = new JTextField(title);
				JButton submittext = new JButton("Add text");
				JButton cancel = new JButton("Cancel");
				class textedit implements ActionListener
				{
					public void actionPerformed(ActionEvent e)
					{
						if(e.getSource() == submittext)
						{
							xaxistext = xtext.getText();
							yaxistext = ytext.getText();
							title = gtitle.getText();
							dispose();
							addtextcheck = true;
							panel.repaint();
						}
						else if(e.getSource() == cancel)
						{
							dispose();
							addtextcheck = true;
						}
					}
				}
				xtext.addActionListener(new textedit());
				ytext.addActionListener(new textedit());
				gtitle.addActionListener(new textedit());
				submittext.addActionListener(new textedit());
				cancel.addActionListener(new textedit());
				JPanel panel = new JPanel();
				panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
				JPanel subpanel1 = new JPanel();
				subpanel1.setLayout(new BoxLayout(subpanel1, BoxLayout.X_AXIS));
				subpanel1.add(xtext);
				subpanel1.add(ytext);
				subpanel1.add(gtitle);
				JPanel subpanel2 = new JPanel();
				subpanel2.setLayout(new GridLayout(0,2));
				subpanel2.add(submittext);
				subpanel2.add(cancel);
				panel.add(subpanel1);
				panel.add(subpanel2);
				this.setResizable(false);
				this.add(panel);
				this.pack();
				this.setLocationRelativeTo(null);
				this.setVisible(true);
			}
		}
	}

	class PointList {
		Point pt[];
		int num;
		int x,y,z; 	//color
		boolean showLine;
		int curPt;
		final int MAXCNTL = 50;
		final int range = 5;

		PointList() {
			num = 0;
			curPt = -1;
			pt = new Point[MAXCNTL];
			x = r;
			y = gr;
			z = b;
		}

		boolean addPoint(int x, int y)
		{
			if(num == MAXCNTL) return false;
			pt[num] = new Point(x,y);
			num++;
			return true;
		}

		void changePoint(int x, int y)
		{
			pt[num-1].x = x;
			pt[num-1].y = y;
		}

		void changeModPoint(int x, int y)
		{
			pt[curPt].x = x;
			pt[curPt].y = y;
		}

		boolean createFinal()
		{
			return true;
		}

		boolean done()
		{
			return true;
		}

		void setShow(boolean show)
		{
			showLine = show;
		}

		int inRegion(int x, int y)
		{
			int i;
			for(i=0; i<num; i++) 
				if(Math.abs(pt[i].x-x) < range && Math.abs(pt[i].y-y) < range)
				{
					curPt = i;
					return i;
				}
			curPt = -1;
			return -1;
		}

		void draw(Graphics g)
		{
			Graphics2D g2 = (Graphics2D) g;
			g2.setStroke(new BasicStroke(1));
			int i;
			int l = 3;
			for(i=0; i< num-1; i++)
			{
				g2.drawLine(pt[i].x-l, pt[i].y, pt[i].x+l, pt[i].y);
				g2.drawLine(pt[i].x, pt[i].y-l, pt[i].x, pt[i].y+l);
				drawDashLine(g2, pt[i].x,pt[i].y,pt[i+1].x,pt[i+1].y);
			}
			g2.drawLine(pt[i].x-l, pt[i].y, pt[i].x+l, pt[i].y);
			g2.drawLine(pt[i].x, pt[i].y-l, pt[i].x, pt[i].y+l);
		}

		protected void drawDashLine(Graphics g, int firstx, int firsty, int nextx, int nexty)
		{
			final float seg = 8;
			double x, y;
			Graphics2D g2 = (Graphics2D) g;

			if(firstx == nextx)
			{
				if(firsty > nexty)
				{
					int tmp = firsty;
					firsty = nexty;
					nexty = tmp;
				}
				y = (double)firsty;
				while(y < nexty)
				{
					double y0 = Math.min(y+seg, (double)nexty);
					g2.setStroke(new BasicStroke(1));
					g2.drawLine(firstx, (int)y, nextx, (int)y0);
					y = y0 + seg;
				}
				return;
			}
			else if(firstx > nextx) 
			{
				int tmp = firstx;
				firstx = nextx;
				nextx = tmp;
				tmp = firsty;
				firsty = nexty;
				nexty = tmp;
			}
			double ratio = 1.0*(nexty-firsty)/(nextx-firstx);
			double ang = Math.atan(ratio);
			double xinc = seg * Math.cos(ang);
			double yinc = seg * Math.sin(ang);
			x = (double)firstx;
			y = (double)firsty;

			while(x <= nextx)
			{
				double x0 = x + xinc;
				double y0 = y + yinc;
				if(x0 > nextx) 
				{
					x0 = nextx;
					y0  = y + ratio*(nextx-x);
				}
				g2.setStroke(new BasicStroke(1));
				g2.drawLine((int)x, (int)y, (int)x0, (int)y0);
				x = x0 + xinc;
				y = y0 + yinc;
			}
		}
	}


	class curve extends PointList {
		Point bpt[];
		int bnum;
		boolean ready;
		final int MAXPOINT = 1800;
		final int ENOUGH = 2;
		final int RECURSION = 900;
		int nPointAlloc;
		int enough;		// control how well we draw the curve.
		int nRecur;		// counter of number of recursion
		Point buffer[][];
		int nBuf, nBufAlloc;

		curve() 
		{
			bpt = new Point[MAXPOINT];
			nPointAlloc = MAXPOINT;
			bnum = 0;
			enough = ENOUGH;
			showLine = true;
			ready = false;
			buffer = null;
		}

		protected int distance(Point p0,Point p1,Point p2)
		{
			int a,b,y1,x1,d1,d2;

			if(p1.x==p2.x && p1.y==p2.y) 
				return Math.min(Math.abs(p0.x-p1.x),Math.abs(p0.y-p1.y));
			a=p2.x-p1.x;    
			b=p2.y-p1.y;
			y1=b*(p0.x-p1.x)+a*p1.y;
			x1=a*(p0.y-p1.y)+b*p1.x;
			d1=Math.abs(y1-a*p0.y);
			d2=Math.abs(x1-b*p0.x);
			if(a==0) 
				return Math.abs(d2/b);
			if(b==0) 
				return Math.abs(d1/a);
			return Math.min(Math.abs(d1/a),Math.abs(d2/b));
		}

		protected void curve_split(Point p[],Point q[],Point r[],int num)
		{
			int i,j;
			for(i=0;i<num;i++) q[i].copy(p[i]);
			for(i=1;i<=num-1;i++) {	
				r[num-i].copy(q[num-1]);
				for(j=num-1;j>=i;j--) {
					q[j].x = (q[j-1].x+q[j].x)/2;
					q[j].y = (q[j-1].y+q[j].y)/2;
				}
			}
			r[0].copy(q[num-1]);
		}

		
		private Point get_buf(int num)[]
		{
			Point b[];
			if(buffer == null)
			{
				buffer = new Point[500][num];
				nBufAlloc = 500;
				nBuf = 0;
			}
			if(nBuf == 0)
			{
				b = new Point[num];
				for(int i=0; i< num; i++) b[i] = new Point();
				return b;
			}
			else 
			{
				nBuf --;
				b = buffer[nBuf];
				return b;
			}
		}

		private void put_buf(Point b[])
		{
			if(nBuf >= nBufAlloc)
			{
				Point newBuf[][] = new Point[nBufAlloc + 500][num];
				for(int i=0; i<nBuf; i++) 
					newBuf[i] = buffer[i];
				nBufAlloc += 500;
				buffer = newBuf;
			}
			buffer[nBuf] = b;
			nBuf++;
		}

		protected boolean curve_generation(Point pt[], int num, Point result[], int n[])
		{
			Point   qt[],rt[];	// for split
			int d[],i,max;

			nRecur++;
			if(nRecur > RECURSION) return false;

			d = new int[MAXCNTL];
			for(i=1;i<num-1;i++) 
				d[i]=distance(pt[i],pt[0],pt[num-1]);
			max=d[1];
			for(i=2;i<num-1;i++) 
				if(d[i]>max) 
					max=d[i];
			if(max <= enough || nRecur > RECURSION) 
			{
				if(n[0]==0) {
					if(bnum > 0) 
						result[0].copy(pt[0]);
					else
						result[0] = new Point(pt[0]);
					n[0]=1;
				}
				//reuse
				if(bnum > n[0])
					result[n[0]].copy(pt[num-1]);
				else
					result[n[0]] = new Point(pt[num-1]);
				n[0]++;
				if(n[0] == MAXPOINT-1) return false;
			}
			else {
				//	   qt = new Point[num];
				//	   rt = new Point[num];
				qt = get_buf(num);
				rt = get_buf(num);
				curve_split(pt,qt,rt,num);
				if(!curve_generation(qt,num,result,n)) 
					return false;
				put_buf(qt);
				if(!curve_generation(rt,num,result,n)) 
					return false;
				put_buf(rt);
			}
			return true;
		}

		public boolean try_curve_generation(Point pt[], int num, Point result[], int n[])
		{
			int oldN = n[0];

			if(enough == ENOUGH && num > 6) 
				enough += 3;
			//       if (enough > ENOUGH) enough -= 5;
			nRecur = 0;
			// in case of recursion stack overflow, relax "enough" and keep trying
			while(!curve_generation(pt, num, bpt, n))
			{
				n[0] = oldN;
				enough += 5;
				nRecur = 0;
			}
			return true;
		}

		boolean createFinal()
		{
			int n[];
			n = new int[1];
			if(!try_curve_generation(pt, num, bpt, n)) 
			{
				bnum = 0;
				return false;
			}
			else {
				bnum = n[0];
				return true;
			}
		}

		boolean done()
		{
			num --;
			showLine = false;
			ready = true;
			return createFinal();
		}

		void draw(Graphics g)
		{
			Graphics2D g2 = (Graphics2D) g;
			g2.setRenderingHint(
					RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_ON);
			g2.setColor(new Color(x, y, z));
			if(showLine)
			{
				super.draw(g);
				if(curPt != -1)
					
					g.drawRect(pt[curPt].x-range, pt[curPt].y-range, 2*range+1,2*range+1);
			}

			if(ready)
				for(int i=0; i< bnum-1; i++)
				{
					g2.setStroke(new BasicStroke(2));
					g2.drawLine(bpt[i].x,bpt[i].y,bpt[i+1].x,bpt[i+1].y);   
				}
		}
	}

	@SuppressWarnings("serial")
	private class colorchanger extends JFrame
	{
		public colorchanger()
		{
			if(colorchangecheck == true)
			{
				colorchangecheck = false;
				this.setTitle("Select Color");
				this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				this.setUndecorated(true);
				JButton red = new JButton("Red");
				JButton blue = new JButton("Blue");
				JButton green = new JButton("Green");
				JButton yellow = new JButton("Yellow");
				JButton purple = new JButton("Purple");
				JButton black = new JButton("Black");
				JButton cancel = new JButton("Cancel");
				JButton blank = new JButton();
				class colorchange implements ActionListener
				{
					public void actionPerformed(ActionEvent e)
					{
						if(e.getSource()==red)
						{
							r = 255;
							gr = 0;
							b = 0;
							dispose();
							colorchangecheck = true;
						}
						else if(e.getSource()==green)
						{
							r = 0;
							gr = 255;
							b = 0;
							dispose();
							colorchangecheck = true;
						}
						else if(e.getSource()==blue)
						{
							r = 0;
							gr = 0;
							b = 255;
							dispose();
							colorchangecheck = true;
						}
						else if(e.getSource()==yellow)
						{
							r = 255;
							gr = 255;
							b = 0;
							dispose();
							colorchangecheck = true;
						}
						else if(e.getSource()==purple)
						{
							r = 255;
							gr = 0;
							b = 255;
							dispose();
							colorchangecheck = true;
						}
						else if(e.getSource()==black)
						{
							r = 0;
							gr = 0;
							b = 0;
							dispose();
							colorchangecheck = true;
						}
						else if(e.getSource()==cancel)
						{
							dispose();
							colorchangecheck = true;
						}
					}
				}
				red.addActionListener(new colorchange());
				blue.addActionListener(new colorchange());
				green.addActionListener(new colorchange());
				yellow.addActionListener(new colorchange());
				purple.addActionListener(new colorchange());
				black.addActionListener(new colorchange());
				cancel.addActionListener(new colorchange());

				JPanel buttonpanel = new JPanel();
				buttonpanel.setLayout(new GridLayout(3,3));
				buttonpanel.add(red);
				buttonpanel.add(blue);
				buttonpanel.add(green);
				buttonpanel.add(yellow);
				buttonpanel.add(purple);
				buttonpanel.add(black);
				buttonpanel.add(blank);
				blank.setVisible(false);
				buttonpanel.add(cancel);

				this.add(buttonpanel);
				this.pack();
				this.setResizable(false);
				this.setLocationRelativeTo(null);
				this.setVisible(true);
			}
		}
	}

	@SuppressWarnings("serial")
	public class gridchanger extends JFrame
	{
		public gridchanger()
		{
			if(gridchangecheck == true)
			{
				gridchangecheck = false;
				this.setUndecorated(true);
				Dimension d = new Dimension(100,40);

				JTextField xstartval = new JTextField(String.valueOf(tempxbase));

				JTextField ystartval = new JTextField(String.valueOf(tempybase));

				JTextField xinterval = new JTextField(String.valueOf(tempxadditive));

				JTextField yinterval = new JTextField(String.valueOf(tempyadditive));

				JTextField xintervalnum = new JTextField(String.valueOf(tempxintervalnumber));

				JTextField yintervalnum = new JTextField(String.valueOf(tempyintervalnumber));

				Integer[] sigdignum = {0, 1, 2, 3, 4};
				SpinnerListModel sigdigmodel = new SpinnerListModel(sigdignum);
				JSpinner spinner = new JSpinner(sigdigmodel);
				((DefaultEditor) spinner.getEditor()).getTextField().setEditable(false);
				((DefaultEditor) spinner.getEditor()).getTextField().setValue(sigdignum[0]);
				spinner.addChangeListener(new ChangeListener() {
					public void stateChanged(ChangeEvent e) 
					{
						if((Integer)spinner.getValue() == 0)
						{
							sigdig = 3;
						}
						if((Integer)spinner.getValue() == 1)
						{
							sigdig = 1;
						}
						if((Integer)spinner.getValue() == 2)
						{
							sigdig = 2;
						}
						if((Integer)spinner.getValue() == 3)
						{
							sigdig = 3;
						}
						if((Integer)spinner.getValue() == 4)
						{
							sigdig = 4;
						}

					}

				});

				JTextField spacer = new JTextField();
				spacer.setVisible(false);

				JButton submittextgrid = new JButton("Submit");
				JButton cancel = new JButton("Cancel");

				xstartval.addFocusListener(new FocusListener(){
					public void focusGained(FocusEvent e)
					{}
					public void focusLost(FocusEvent e)
					{
						if(xstartval.getText().endsWith("."))
						{
							xstartval.setText(xstartval.getText().substring(0, xstartval.getText().length()-1));
						}
					}
				});

				ystartval.addFocusListener(new FocusListener(){
					public void focusGained(FocusEvent e)
					{}
					public void focusLost(FocusEvent e)
					{
						if(ystartval.getText().endsWith("."))
						{
							ystartval.setText(ystartval.getText().substring(0, ystartval.getText().length()-1));
						}
					}
				});

				xinterval.addFocusListener(new FocusListener(){
					public void focusGained(FocusEvent e)
					{}
					public void focusLost(FocusEvent e)
					{
						if(xinterval.getText().endsWith("."))
						{
							xinterval.setText(xinterval.getText().substring(0, xinterval.getText().length()-1));
						}
					}
				});

				yinterval.addFocusListener(new FocusListener(){
					public void focusGained(FocusEvent e)
					{}
					public void focusLost(FocusEvent e)
					{
						if(yinterval.getText().endsWith("."))
						{
							yinterval.setText(yinterval.getText().substring(0, yinterval.getText().length()-1));
						}
					}
				});
				((AbstractDocument)xstartval.getDocument()).setDocumentFilter(new doclistener()); 
				((AbstractDocument)ystartval.getDocument()).setDocumentFilter(new doclistener());
				((AbstractDocument)xinterval.getDocument()).setDocumentFilter(new doclistener()); 
				((AbstractDocument)yinterval.getDocument()).setDocumentFilter(new doclistener());
				((AbstractDocument)xintervalnum.getDocument()).setDocumentFilter(new doclistener2());
				((AbstractDocument)yintervalnum.getDocument()).setDocumentFilter(new doclistener2());
				class textedit implements ActionListener
				{
					public void actionPerformed(ActionEvent e)
					{
						try{
							if(e.getSource() == cancel)
							{
								dispose();
								gridchangecheck = true;
							}
							else if(e.getSource() == submittextgrid)
							{
								tempxbase = Double.parseDouble(xstartval.getText());
								tempybase = Double.parseDouble(ystartval.getText()); 
								tempxadditive = Double.parseDouble(xinterval.getText()); 
								tempyadditive = Double.parseDouble(yinterval.getText());
								tempxintervalnumber = Integer.parseInt(xintervalnum.getText());
								tempyintervalnumber = Integer.parseInt(yintervalnum.getText());
								gridchangecheck = true;
								panel.repaint();
								dispose();
							}
						}
						catch(NumberFormatException er)
						{
							JOptionPane.showMessageDialog(null, "Make sure nothing is blank");
						}
					}
				}

				submittextgrid.addActionListener(new textedit());
				cancel.addActionListener(new textedit());
				xstartval.setPreferredSize(d);
				ystartval.setPreferredSize(d);
				xinterval.setPreferredSize(d);
				yinterval.setPreferredSize(d);
				xintervalnum.setPreferredSize(d);
				yintervalnum.setPreferredSize(d);
				JPanel overallpanel = new JPanel();
				overallpanel.setLayout(new BoxLayout(overallpanel, BoxLayout.Y_AXIS));
				JPanel panelpanel = new JPanel();
				panelpanel.setLayout(new GridLayout(3,3));
				JPanel buttonpanel = new JPanel();

				JPanel x1 = new JPanel();
				x1.setLayout(new BoxLayout(x1, BoxLayout.Y_AXIS));
				JLabel lx1 = new JLabel("First Value of X axis");
				x1.add(lx1);
				x1.add(xstartval);

				JPanel y1 = new JPanel();
				y1.setLayout(new BoxLayout(y1, BoxLayout.Y_AXIS));
				JLabel yx1 = new JLabel("First Value of Y axis");
				y1.add(yx1);
				y1.add(ystartval);

				JPanel x2 = new JPanel();
				x2.setLayout(new BoxLayout(x2, BoxLayout.Y_AXIS));
				JLabel lx2 = new JLabel("Interval between grid markers on X axis");
				x2.add(lx2);
				x2.add(xinterval);

				JPanel y2 = new JPanel();
				y2.setLayout(new BoxLayout(y2, BoxLayout.Y_AXIS));
				JLabel ly2 = new JLabel("Interval between grid markers on Y axis");
				y2.add(ly2);
				y2.add(yinterval);

				JPanel x3 = new JPanel();
				x3.setLayout(new BoxLayout(x3, BoxLayout.Y_AXIS));
				JLabel lx3 = new JLabel("Number of markers on X axis");
				x3.add(lx3);
				x3.add(xintervalnum);

				JPanel y3 = new JPanel();
				y3.setLayout(new BoxLayout(y3, BoxLayout.Y_AXIS));
				JLabel ly3 = new JLabel("Number of markers on Y axis");
				y3.add(ly3);
				y3.add(yintervalnum);

				JPanel sigdigpanel = new JPanel();
				sigdigpanel.setLayout(new BoxLayout(sigdigpanel, BoxLayout.Y_AXIS));
				JLabel sigdiglabel = new JLabel("Number of Significant Digits");
				sigdigpanel.add(sigdiglabel);
				sigdigpanel.add(spinner);

				panelpanel.add(x1);
				panelpanel.add(x2);
				panelpanel.add(x3);
				panelpanel.add(y1);
				panelpanel.add(y2);
				panelpanel.add(y3);
				panelpanel.add(spacer);
				panelpanel.add(sigdigpanel);
				buttonpanel.add(submittextgrid);
				buttonpanel.add(cancel);
				overallpanel.setAlignmentX(CENTER_ALIGNMENT);
				overallpanel.add(panelpanel);
				overallpanel.add(buttonpanel);
				this.setResizable(false);
				this.add(overallpanel);
				this.pack();
				this.setLocationRelativeTo(null);
				this.setVisible(true);
			}
		}
	}

	class Point 
	{
		int x,y;

		Point(Point p)
		{
			x = p.x;
			y = p.y;
		}
		Point(int _x, int _y)
		{
			x = _x;
			y = _y;
		}
		Point()
		{
			x = 0;
			y = 0;
		}
		void copy(Point p)
		{
			x = p.x;
			y = p.y;
		}
	}

	public JComponent getMainComponent() 
	{
		return panel;
	}

	/*
	public static void main(String[] args) {
		NoBSpline c = new NoBSpline();
		JFrame frame = new JFrame();
		JPanel container = new JPanel();
		container.setLayout(new BoxLayout(container, BoxLayout.X_AXIS));
		container.add(c.getMainComponent());
		frame.add(container);
		frame.setSize(775,600);
		frame.setResizable(false);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	 */

}





