
import java.awt.*;
import java.awt.event.*;
import java.util.Iterator;
import java.util.LinkedList;
import javax.swing.*;
import javax.swing.border.*;


public class HandWritingPanel
		extends JPanel
		implements MouseListener, MouseMotionListener {

	private static final long serialVersionUID = 1L;

	KanjiDictionary parent;
	
	final static int SIZE = 250;
	
	Image buffer;
	Graphics bufferGraphics;
	
	LinkedList<LinkedList<Point>> pointsList
			= new LinkedList<LinkedList<Point>>();
	LinkedList<Point> currentPointList = null;

	public HandWritingPanel(KanjiDictionary parent) {
		this.parent = parent;
		this.setPreferredSize(new Dimension(SIZE, SIZE));
		this.setBorder(new EtchedBorder());
		this.setForeground(Color.BLACK);
		this.setBackground(Color.WHITE);
		super.addMouseListener(this);
		super.addMouseMotionListener(this);
	}

	public void paintComponent(Graphics g) {
		if (buffer == null) {
			buffer = this.createImage(SIZE, SIZE);
			bufferGraphics = buffer.getGraphics();
			bufferGraphics.setColor(Color.WHITE);
			bufferGraphics.fillRect(0, 0, SIZE, SIZE);
		}
		super.paintComponent(g);
		g.drawImage(buffer, 0, 0, this);
	}

	public void mouseEntered(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {}
	public void mouseClicked(MouseEvent e) {}
	public void mousePressed(MouseEvent e) {
		currentPointList = new LinkedList<Point>();
	}
	public void mouseReleased(MouseEvent e) {
		pointsList.add(currentPointList);
		currentPointList = null;
	}
	public void mouseDragged(MouseEvent e) {
		Point point1 = e.getPoint();
		Point point0 = null;
		try {
			point0 = currentPointList.getLast();
			bufferGraphics.setColor(Color.BLACK);
			bufferGraphics.drawLine(point0.x, point0.y,
					point1.x, point1.y);
		} catch (java.util.NoSuchElementException nsee) {
		}
		currentPointList.add(point1);
		this.repaint();
	}
	public void mouseMoved(MouseEvent e) {}

	public void clearLines() {
		pointsList.clear();
		buffer = null;
		this.repaint();
	}

	public void cancelLine() {
		try {
			pointsList.removeLast();
		} catch (java.util.NoSuchElementException e) {
		}
		bufferGraphics.setColor(Color.WHITE);
		bufferGraphics.fillRect(0, 0, SIZE, SIZE);
		bufferGraphics.setColor(Color.BLACK);
		Iterator<LinkedList<Point>> iter = pointsList.iterator();
		while (iter.hasNext()) {
			LinkedList<Point> pointList = iter.next();
			Point previousPoint = null;
			Iterator<Point> iter2 = pointList.iterator();
			while (iter2.hasNext()) {
				Point point = iter2.next();
				if (previousPoint != null) {
					bufferGraphics.drawLine(previousPoint.x, previousPoint.y,
							point.x, point.y);
				}
				previousPoint = point;
			}
		}
		this.repaint();
	}
}
