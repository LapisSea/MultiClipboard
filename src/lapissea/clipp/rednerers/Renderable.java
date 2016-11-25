package lapissea.clipp.rednerers;

import java.awt.Graphics2D;

public interface Renderable{
	
	void render(Graphics2D g, int width, int height, float highlight, int id);
	
	default void update(Graphics2D g, int width, int height){}
}
