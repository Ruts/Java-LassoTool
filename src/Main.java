import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.SwingUtilities;
 
public class Main
{
	
	public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {

                JPanel panel = new JPanel();
                PanelTwo panelTwo = new PanelTwo();
                PanelOne panelOne = new PanelOne(panelTwo);

                JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, panelOne, panelTwo);
                panel.add(split);

                JFrame frame = new JFrame("Lasso Tool");
                frame.add(panel);
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.pack();
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            }
        });
    }
	
	private static class PanelOne extends JPanel {

		Point pointStart = null;
	    Point pointEnd = null;
		Color brown = new Color(182,129,76);
        BufferedImage image;
        Image scaledImage;
        PanelTwo panelTwo;
        JButton imageSelect = new JButton();

        public PanelOne(PanelTwo panelTwo) {
            this.panelTwo = panelTwo;
            setBackground(brown);
            imageSelect = new JButton("Select Image");
            
            JPanel buttonPane = new JPanel();
            buttonPane.add(imageSelect);
            
            add(buttonPane, BorderLayout.WEST);
            
            imageSelect.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent ae) {
                    JFileChooser fc = new JFileChooser();
                    int result = fc.showOpenDialog(null);
                    if (result == JFileChooser.APPROVE_OPTION) {
                    	
                    	File file = fc.getSelectedFile();
                        String sname = file.getAbsolutePath();
                        
                        try {
    						image = ImageIO.read(new File(sname));
    					} catch (IOException e) {
    						e.printStackTrace();
    					}
                       
                        scaledImage = image.getScaledInstance(800, 400,Image.SCALE_SMOOTH);
                        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
                    }
                }
            });
            
            addMouseListener(new java.awt.event.MouseAdapter() {
            	public void mousePressed(java.awt.event.MouseEvent evt) {
                    pointStart = evt.getPoint();

                 }
                 public void mouseReleased(java.awt.event.MouseEvent evt) {
                    pointStart = null;
                 }
            });
            
            addMouseMotionListener(new MouseMotionAdapter() {
                public void mouseMoved(MouseEvent e) {
                    pointEnd = e.getPoint();
                }

                public void mouseDragged(MouseEvent e) {
                    pointEnd = e.getPoint();
                    repaint();
                }
            });
        }
        @Override
        public void paint(Graphics g) {
            super.paint(g);
            g.drawImage(scaledImage, 0, 0, this);

            if (pointStart != null && image != null) {
            	
            	Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
            	Color myColour = new Color(255, 100, 100, 100);
                
                g2.setPaint(myColour);
                
                float dash1[] = {10.0f};
                BasicStroke dashed = new BasicStroke(2.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dash1, 0.0f);
                
                g2.setStroke(dashed);
                
                if(pointEnd.x > pointStart.x && pointEnd.y > pointStart.y){
                	g2.fill(new Rectangle2D.Double(pointStart.x, pointStart.y, (pointEnd.x - pointStart.x), (pointEnd.y - pointStart.y)));
                	g2.draw(new Rectangle2D.Double(pointStart.x, pointStart.y, (pointEnd.x - pointStart.x), (pointEnd.y - pointStart.y)));
                }
               
               
                PanelTwo.setRect(pointStart, pointEnd);
                panelTwo.setImage(image);
            	}
        }
        @Override
        public Dimension getPreferredSize() {
            return new Dimension(800, 400);
        }       
        
    }

    private static class PanelTwo extends JPanel {

    	static Point pointStart = null;
	    static Point pointEnd = null;
	    Image scaledImage;
    	Color brown = new Color(182,129,76);
        BufferedImage bi;

        public PanelTwo() {
            setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            setBackground(brown);
        }

        public static void setRect(Point pointStart1, Point pointEnd1) {
			pointStart = pointStart1;
			pointEnd = pointEnd1;
		}

		public void setImage(BufferedImage image) {
            this.bi = image;
            scaledImage = bi.getScaledInstance(800, 400,Image.SCALE_SMOOTH);
            repaint();
        }

        private void setImage(JPanel panel) {
            Dimension d = panel.getPreferredSize();
            int w = (int)d.getWidth();
            int h =(int)d.getHeight();
            bi = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
            Graphics2D g = bi.createGraphics();
            panel.paint(g);
            g.dispose();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (pointStart != null) {
            	if(pointEnd.x > pointStart.x && pointEnd.y > pointStart.y){
            		g.drawImage(scaledImage, 0, 0, pointEnd.x, pointEnd.y, pointStart.x, pointStart.y, pointEnd.x , pointEnd.y, this);
            	}
            }
        }

        @Override
        public Dimension getPreferredSize() {
            return new Dimension(400, 200);
        }
    }
}   