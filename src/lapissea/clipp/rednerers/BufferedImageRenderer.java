package lapissea.clipp.rednerers;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import lapissea.clipp.Gui;

public class BufferedImageRenderer implements Renderable{
	
	private final BufferedImage	img;
	private final Gui			gui;
	
	public BufferedImageRenderer(Gui gui, BufferedImage img){
		this.img=img;
		this.gui=gui;
	}
	
	protected void markDirty(){
		gui.markDirty();
	}
	
	@Override
	public void render(Graphics2D g, int width, int height, float highlight, int id){
		float w=width,h=height;
		float imgScale=Math.max(w/img.getWidth(), h/img.getHeight());
		
		int actualWidth=(int)(img.getWidth()*imgScale);
		int actualHeight=(int)(img.getHeight()*imgScale);
		
		if(actualHeight>height){
			double off=Math.sin((System.currentTimeMillis()/gui.handler.scrollTime+id)%(Math.PI*2))*0.5+0.5;
			g.translate(0, -off*(actualHeight-height));
			markDirty();
		}else if(actualWidth>width){
			double off=Math.sin((System.currentTimeMillis()/gui.handler.scrollTime+id)%(Math.PI*2))*0.5+0.5;
			g.translate(-off*(actualWidth-width), 0);
			markDirty();
		}
		
		g.drawImage(img, 0, 0, actualWidth, actualHeight, null);
	}
	
}
