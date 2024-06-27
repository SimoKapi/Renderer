package renderer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.util.HashMap;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JSlider;
import javax.swing.SwingConstants;

public class GUI extends JPanel {
	private static final long serialVersionUID = 1L;
	ProjectionCalculations projectionCalculations = new ProjectionCalculations();
	Graphics2D g2d;
	
	public static JSlider distanceSlider, ySlider, FOVSlider;
	private static JFrame frame;
	private static JLabel label, resolutionLabel;
	
	void Update() {
		label.setText("Rotation: " + Main.camera.rotation.toString() + " Position: " + Main.camera.position.toString());
		repaint();
//		resolutionLabel.setText(getWidth() + "x" + getHeight());
		resolutionLabel.setText(String.valueOf(Main.FOV));
	}
	
	public void createWindow() {
		frame = new JFrame("Renderer");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLayout(new BorderLayout());
		
		distanceSlider = new JSlider(SwingConstants.VERTICAL, -100, 100, 0);
		frame.add(distanceSlider, BorderLayout.WEST);
        ySlider = new JSlider(-180, 180, 0);
        frame.add(ySlider, BorderLayout.SOUTH);
        FOVSlider = new JSlider(SwingConstants.VERTICAL, 0, 200, 20);
        frame.add(FOVSlider, BorderLayout.EAST);
		

		distanceSlider.addChangeListener(e -> Main.changeDistance(distanceSlider.getValue()));
		FOVSlider.addChangeListener(e -> Main.changeFOV(FOVSlider.getValue()));
		frame.add(this);
		
		JPanel infoPanel = new JPanel();
		label = new JLabel(Main.camera.rotation.toString());
		infoPanel.add(label, BorderLayout.WEST);
		
		JSeparator sep = new JSeparator(JSeparator.VERTICAL);
		infoPanel.add(sep);
		
		resolutionLabel = new JLabel(Main.camera.resolution.toString());
		infoPanel.add(resolutionLabel, BorderLayout.EAST);
		
		frame.add(infoPanel, BorderLayout.NORTH);
		frame.setSize((int) Main.camera.resolution.x, (int) Main.camera.resolution.y);
		frame.setVisible(true);
	}
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		g2d = (Graphics2D) g;
		g2d.setColor(Color.DARK_GRAY);
		g2d.fillRect(0, 0, getWidth(), getHeight());
				
		BufferedImage img = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
		
		projectionCalculations.initializeValues(getWidth(), getHeight());
		
		try {
			HashMap<Vector2[], Color> points = projectionCalculations.getFinalImage();
			
			for (Vector2[] key : points.keySet()) {
				g2d.setColor(points.get(key));
				g2d.drawLine((int) key[0].x, (int) key[0].y, (int) key[1].x, (int) key[1].y);
			}
	
			g2d.drawImage(img, 0, 0, null);
		} catch(Exception e) {}
	}
			
	public Vector3 convert(Matrix in) {
		return new Vector3(in.values[0][0], in.values[1][0], in.values[2][0]);
	}
}
