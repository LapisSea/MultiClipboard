package lapissea.clipp.rednerers;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import lapissea.clipp.Gui;

public class BufferedImageRenderer implements Renderable{
	
	private final BufferedImage	img;
	private final Gui			gui;
	private BufferedImage		imgRender;
	
	public BufferedImageRenderer(Gui gui, BufferedImage img){
		this.img=img;
		this.gui=gui;
	}
	
	protected void markDirty(){
		gui.markDirty();
	}
	
	@Override
	public void render(Graphics2D g, int width, int height, float highlight, int id){
		if(imgRender==null) update(g, width, height);
		
		if(imgRender.getHeight()>height){
			double off=Math.sin((System.currentTimeMillis()/gui.handler.scrollTime+id)%(Math.PI*2))*0.5+0.5;
			g.translate(0, -off*(imgRender.getHeight()-height));
			markDirty();
		}else if(imgRender.getWidth()>width){
			double off=Math.sin((System.currentTimeMillis()/gui.handler.scrollTime+id)%(Math.PI*2))*0.5+0.5;
			g.translate(-off*(imgRender.getWidth()-width), 0);
			markDirty();
		}
		
		g.drawImage(imgRender, 0, 0, null);
	}
	
	@Override
	public void update(Graphics2D g, int width, int height){
		float imgScale=Math.max(width/(float)img.getWidth(), height/(float)img.getHeight());
		
		int actualWidth=(int)(img.getWidth()*imgScale);
		int actualHeight=(int)(img.getHeight()*imgScale);
		
		imgRender=new BufferedImage(actualWidth, actualHeight, img.getType());
		Graphics2D g1=imgRender.createGraphics();
		g1.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
		g1.setRenderingHint(RenderingHints.KEY_RENDERING,RenderingHints.VALUE_RENDER_QUALITY);
		g1.setRenderingHint(RenderingHints.KEY_INTERPOLATION,RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		
		g1.drawImage(img, 0, 0, imgRender.getWidth(), imgRender.getHeight(), null);
		
		g1.finalize();
	}
}
