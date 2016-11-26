package lapissea.clipp;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;

import lapissea.clipp.rednerers.BlankRenderer;
import lapissea.clipp.rednerers.BufferedImageRenderer;
import lapissea.clipp.rednerers.FileListRenderer;
import lapissea.clipp.rednerers.Renderable;
import lapissea.clipp.rednerers.StringRenderer;

public class Button{
	
	protected Gui		gui;
	protected Slot		slot;
	public float		highlight,wantedHighlight;
	public int			id;
	private Renderable	renderer=null;
	
	@SuppressWarnings("unchecked")
	private final Runnable updateRenderer=()->{
		Object obj=slot.getActive()==null?null:slot.getActive();
		
		if(obj instanceof String) renderer=new StringRenderer(this.gui, (String)obj);
		else if(obj instanceof BufferedImage) renderer=new BufferedImageRenderer(this.gui, (BufferedImage)obj);
		else if(obj instanceof List) renderer=new FileListRenderer(this.gui, (List<File>)obj);
		else renderer=new BlankRenderer();
	};
	
	public Button(Gui gui, Slot slot, int id){
		this.gui=gui;
		this.slot=slot;
		this.id=id;
		slot.setChangeHook(updateRenderer);
	}
	
	public void draw(Graphics2D g, int width, int height){
		float speed=0.1F,diff=Math.abs(wantedHighlight-highlight),lastHighlight=highlight;
		
		if(diff<speed) highlight=wantedHighlight;
		else if(highlight>wantedHighlight) highlight-=speed;
		else highlight+=speed;

		
		AffineTransform mat=g.getTransform();
		float scale=(float)(0.95+Math.sin(highlight*Math.PI/2)*0.06);
		g.translate(width/2F, height/2F);
		g.scale(scale, scale);
		g.translate(-width/2F, -height/2F);
		AffineTransform mat2=g.getTransform();
		
		g.setClip(1, 1, width-2, height-2);
		g.translate(1, 1);
		
		if(renderer==null)updateRenderer.run();
		if(lastHighlight-highlight!=0)renderer.update(g, width-2, height-2);
		
		renderer.render(g, width-2, height-2, highlight, id);
		
		g.setClip(null);
		g.setTransform(mat2);
		
		g.setColor(new Color(0, 150, 255));
		g.drawRect(0, 0, width, height);
		
		g.setTransform(mat);
		if(diff>0) markDirty();
	}
	
	protected void markDirty(){
		gui.markDirty();
	}
	
}
