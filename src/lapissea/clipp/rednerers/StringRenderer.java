package lapissea.clipp.rednerers;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;

import lapissea.clipp.Gui;

public class StringRenderer implements Renderable{
	
	private final String	src;
	private List<String>	lines;
	private final Gui		gui;
	private boolean			noBG=false;
	
	public StringRenderer(Gui gui, String string){
		this.src=string;
		this.gui=gui;
	}
	
	protected void markDirty(){
		gui.markDirty();
	}
	
	@Override
	public void render(Graphics2D g, int width, int height, float highlight, int id){
		if(lines==null) update(g, width, height);
		
		if(!noBG){
			g.setColor(new Color(0.3F-highlight*0.3F, 0.3F-highlight*0.3F, 1, 0.4F));
			g.fillRect(0, 0, width, height);
		}
		
		g.setColor(Color.WHITE);
		
		double pos=0;
		int textHeight=lines.size()*(gui.handler.font.getSize()+1);
		
		if(textHeight>height){
			double off=Math.sin((System.currentTimeMillis()/gui.handler.scrollTime+id)%(Math.PI*2))*0.5+0.5;
			g.translate(0, pos=(-off*(textHeight-height)));
			gui.markDirty();
		}
		pos+=3;
		
		int i=(int)Math.max(Math.floor(-pos/gui.handler.font.getSize()), 0);
		if(i>0) g.translate(0, gui.handler.font.getSize()*i);
		
		for(int j=(int)Math.min(i+Math.ceil(height/(float)gui.handler.font.getSize())+1, lines.size());i<j;i++){
			String l=lines.get(i);
			if(l!=null)g.drawString(l, 5, 15);
			g.translate(0, gui.handler.font.getSize());
		}
	}
	
	@Override
	public void update(Graphics2D g, int width, int height){
		String s=src;
		int w=g.getFontMetrics().charWidth('a'),aviable=(int)Math.floor((width-13F)/w);
		
		lines=new ArrayList<>();
		if(s.length()>aviable){
			
			while(!s.isEmpty()){
				if(s.charAt(0)=='\n'){
					lines.add("\n\\n");
					s=s.substring(1);
					continue;
				}
				
				String sCut;
				int toCut=-1;
				if(s.length()<=aviable) sCut=s;
				else{
					StringBuilder builder=new StringBuilder();
					for(int i=0;i<aviable;i++){
						char c=s.charAt(i);
						if(c=='\n'){
							builder.append(c);
							toCut=builder.length();
							builder.append("\\n");
							break;
						}
						builder.append(c);
					}
					sCut=builder.toString();
				}
				if(toCut==-1) toCut=sCut.length();
				lines.add(sCut);
				if(s.length()<=toCut) s="";
				else s=s.substring(toCut);
				
			}
		}else lines.add(s);
		
	}
	
	public StringRenderer withNoBG(){
		noBG=true;
		return this;
	}
	
}
