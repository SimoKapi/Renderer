package renderer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
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
		resolutionLabel.setText(getWidth() + "x" + getHeight());
//		resolutionLabel.setText(String.valueOf(Main.FOV));
	}
	
	public void createWindow() {
		frame = new JFrame("Renderer");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLayout(new BorderLayout());
		
//		BufferedImage cursorImg = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
//		Cursor blankCursor = Toolkit.getDefaultToolkit().createCustomCursor(
//			    cursorImg, new Point(0, 0), "blank cursor");
//		frame.getContentPane().setCursor(blankCursor);

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
		
//		projectionCalculations.start();
	}
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		projectionCalculations.initializeValues(getWidth(), getHeight());
		g2d = (Graphics2D) g;
		g2d.setColor(Color.DARK_GRAY);
		g2d.fillRect(0, 0, getWidth(), getHeight());
				
		BufferedImage img = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
		
		HashMap<Vector2[], Color> points = projectionCalculations.getFinalImage();
			
		for (int y = 0; y < projectionCalculations.currentView.length - 1; y++) {
			for (int x = 0; x < projectionCalculations.currentView[y].length - 1; x++) {
				if (projectionCalculations.currentView[y][x] != null) {						
					img.setRGB(x, y, projectionCalculations.currentView[y][x].getRGB());
				}
			}
		}
			
//		for (Vector2[] key : points.keySet()) {
//			g2d.setColor(Color.RED);
////			g2d.drawLine((int) key[0].x, (int) key[0].y, (int) key[1].x, (int) key[1].y);
//			g2d.drawRect((int) key[0].x - 4, (int) key[0].y - 4, 8, 8);
//		}
	
		g2d.drawImage(img, 0, 0, null);
		Main.lateUpdate();
	}
}
