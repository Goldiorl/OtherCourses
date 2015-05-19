import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.*;

public class MenuOperation {
	JFrame frame = new JFrame("Convex Hull Application");
	JPanel myPanel;
	JLabel label;
	JComboBox comboBox;
	JButton runButton, clearButton;
	JScrollPane scrollPane;
	String algorithms[] = { "Brute Force", "Graham Scan", "Jarvis March" };
	String s;

	public void init(final ArrayList<MyPoint> pointList) {
		try {
			SwingUtilities.invokeAndWait(new Runnable() {
				public void run() {
					makeGUI(pointList);
				}
			});
		} catch (Exception exc) {
			System.out.println("Can't create because of " + exc);
		}
	}

	@SuppressWarnings("serial")
	private void makeGUI(final ArrayList<MyPoint> pointList) {
		frame.setLayout(new FlowLayout());
		comboBox = new JComboBox(algorithms);
		frame.add(comboBox);
		comboBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				s = (String) comboBox.getSelectedItem().toString();
			}
		});
		myPanel = new JPanel() {
			public void paintComponent(Graphics g) {

				super.paintComponent(g);
				final Graphics2D g2D = (Graphics2D) g;
				g2D.setColor(Color.gray);
				g2D.fillRect(0, 0, 800, 800);
				for (int i = 0; i < pointList.size(); i++) {
					g.setColor(Color.white);
					((MyPoint) pointList.get(i)).draw(g, 8);
					g2D.setColor(Color.white);
					g2D.drawString(pointList.get(i).getName(), (int) pointList
							.get(i).getX() + 4,
							(int) pointList.get(i).getY() + 4);
				}
			}

		};
		myPanel.setPreferredSize(new Dimension(800, 800));
		scrollPane = new JScrollPane(myPanel);
		scrollPane.setPreferredSize(new Dimension(600, 600));
		label = new JLabel(
				"Welcome to CONVEX HULL!   Please choose an algorithm!");
		runButton = new JButton("RUN");

		runButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				Graphics2D g = (Graphics2D) myPanel.getGraphics();
				if (s == null)
					new BruteForce(g, pointList);
				else if (s.equals(algorithms[0]))
					new BruteForce(g, pointList);
				else if (s.equals(algorithms[1]))
					new GrahamScan(g, pointList);
				else if (s.equals(algorithms[2]))
					new JarvisMarch(g, pointList);
			}
		});

		clearButton = new JButton("CLEAR");

		clearButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				Graphics2D g = (Graphics2D) myPanel.getGraphics();
				g.setBackground(Color.GRAY);
				g.clearRect(0, 0, 800, 800);
			}
		});
		frame.add(label);
		frame.add(comboBox);
		frame.add(runButton);
		frame.add(clearButton);
		frame.setVisible(true);
		frame.setSize(700, 700);
		frame.getContentPane().add(scrollPane, BorderLayout.CENTER);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(true);
	}
}
