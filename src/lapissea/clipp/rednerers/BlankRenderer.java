package lapissea.clipp.rednerers;

import java.awt.Color;
import java.awt.Graphics2D;


public class BlankRenderer implements Renderable{
	
	@Override
	public void render(Graphics2D g, int width, int height, float highlight, int id){
		g.setColor(new Color(0.3F-highlight*0.3F, 0.3F-highlight*0.3F, 1, 0.4F));
		g.fillRect(0, 0, width, height);
	}
	
}
