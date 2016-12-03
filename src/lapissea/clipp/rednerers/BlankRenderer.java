package lapissea.clipp.rednerers;

import java.awt.Color;
import java.awt.Graphics2D;

import lapissea.clipp.Gui;


public class BlankRenderer implements Renderable{
	
	Gui gui;
	
	
	public BlankRenderer(Gui gui){
		this.gui=gui;
	}


	@Override
	public void render(Graphics2D g, int width, int height, float highlight, int id){
		Color c=gui.handler.colorTheme;
		
		float re=c.getRed()/256F;
		float gr=c.getGreen()/256F;
		float bl=c.getBlue()/256F;
		
		g.setColor(new Color(re+(re>0.3?-1:0)*highlight*0.3F, gr+(gr>0.3?-1:0)*highlight*0.3F, bl+(bl>0.3?-1:0)*highlight*0.3F, 0.4F));
		g.fillRect(0, 0, width, height);
	}
	
}
