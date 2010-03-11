//    Flood and evacuation simulator using multi-agent technology

package gui.Map;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Stroke;

import javax.swing.JPanel;
import javax.swing.Scrollable;
import javax.swing.SwingConstants;

import util.Hexagon2D;
import util.HexagonalGrid;

public class MapPane extends JPanel implements Scrollable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8509235099644365808L;
	private HexagonalGrid grid = null;
	private int radius = -1;
	private int hexWidth;
	private int hexHeight;
	private Dimension size;
	private int maxUnitIncrement = 1;

	public Dimension updateGrid(HexagonalGrid grid, Dimension dim) {
		size = dim;
		this.grid = grid;
//		setSize(size);
		
		
		// Calcular el radio de los hexágonos a representar
		int radiusX = (int) (((size.width / grid.getColumns()) / 2) * 1.1);
		int radiusY = (int) (((size.height / grid.getRows()) / 2) * 1.3);
		if (radiusX < radiusY)
			radius = radiusX;
		else
			radius = radiusY;
		if (radius < 6)
			radius = 6;

		// Calcular las distancias de referencia de los hexágonos
		Polygon p = new Hexagon2D(0, 0, radius);
		this.hexWidth = p.xpoints[4] - p.xpoints[2];
		this.hexHeight = p.ypoints[1] - p.ypoints[3];

		int width = ((hexWidth * grid.getColumns())  + (hexWidth /2 ));
		int height = (hexHeight * grid.getRows());
		
		size =  new Dimension(width, height);
		setSize(size);
		return size;
		// repaint();
	}

	// TODO Rotar
	@Override
	public void paint(Graphics g) {
		if (grid != null) {
			Graphics2D g2 = (Graphics2D) g;

			// Preferencias para el renderizado, puede que en algunas
			// plataformas se ignoren. Anteponemos velocidad a calidad.
			RenderingHints rh = new RenderingHints(
					RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_OFF);
			rh.put(RenderingHints.KEY_RENDERING,
					RenderingHints.VALUE_RENDER_SPEED);
			g2.addRenderingHints(rh);

			// Estilo de pincel
			Stroke stroke = new BasicStroke(1, BasicStroke.CAP_ROUND,
					BasicStroke.JOIN_ROUND);
			g2.setStroke(stroke);

			
			int endX = grid.getOffX() + grid.getColumns();
			int endY = grid.getOffY() + grid.getRows();
			for (int i = grid.getOffX(); i < endX; i++) {
				for (int j = grid.getOffY(); j < endY; j++) {
					int posX;
					if (j % 2 == 0) { // Fila par
						posX = (hexWidth / 2)
								+ ((i - grid.getOffX()) * hexWidth);
					} else { // Fila impar
						posX = hexWidth + ((i - grid.getOffX()) * hexWidth);
					}
					int posY = radius + ((j - grid.getOffY()) * hexHeight);

					// Generar hexágono
					Polygon hex = new Hexagon2D(posX, posY, radius);
					// Dibujar y colorear según la altura
					int value = grid.getStreetValue(i, j);
					if (value != 0) {
						g2.setColor(new Color(value * 1000));
						// System.err.println("pintando en "+i+","+j+", ");
					} else {
						g2.setColor(Color.WHITE);
					}
					g2.fillPolygon(hex);
				}
			}
		}
	}


	public void setGrid(HexagonalGrid grid) {
		this.grid = grid;
	}

	public Dimension getPreferredSize() {
		return size;
	}

	public Dimension getPreferredScrollableViewportSize() {
		return getPreferredSize();
	}

	public int getScrollableUnitIncrement(Rectangle visibleRect,
			int orientation, int direction) {
		// Get the current position.
		int currentPosition = 0;
		if (orientation == SwingConstants.HORIZONTAL) {
			currentPosition = visibleRect.x;
		} else {
			currentPosition = visibleRect.y;
		}

		// Return the number of pixels between currentPosition
		// and the nearest tick mark in the indicated direction.
		if (direction < 0) {
			int newPosition = currentPosition
					- (currentPosition / maxUnitIncrement) * maxUnitIncrement;
			return (newPosition == 0) ? maxUnitIncrement : newPosition;
		} else {
			return ((currentPosition / maxUnitIncrement) + 1)
					* maxUnitIncrement - currentPosition;
		}
	}

	public int getScrollableBlockIncrement(Rectangle visibleRect,
			int orientation, int direction) {
		if (orientation == SwingConstants.HORIZONTAL) {
			return visibleRect.width - maxUnitIncrement;
		} else {
			return visibleRect.height - maxUnitIncrement;
		}
	}

	public boolean getScrollableTracksViewportWidth() {
		return false;
	}

	public boolean getScrollableTracksViewportHeight() {
		return false;
	}

	public void setMaxUnitIncrement(int pixels) {
		maxUnitIncrement = pixels;
	}

}
