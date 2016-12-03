package lapissea.clipp;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipFile;

import javax.swing.JFrame;

import com.sun.awt.AWTUtilities;

@SuppressWarnings("serial")
public class Gui extends JFrame{
	
	private double openningTransition;
	private boolean renderDirty=true;
	public final Handler handler;
	private List<Button> buttons=new ArrayList<>();
	private int selectedId=-1,lastSelectedId=-2;
	public Color bColor;
	
	static{
		File icon=new File("data/Icon.png"),iconMini=new File("data/IconMini.png");
		try{
			if(!icon.exists()){
				ZipFile jar=new ZipFile(new File(Handler.getJarPath()));
				Files.copy(jar.getInputStream(jar.getEntry("Icon.png")), icon.toPath());
				
				if(!iconMini.exists()){
					Files.copy(jar.getInputStream(jar.getEntry("IconMini.png")), iconMini.toPath());
					Handler.restart();
				}
				
				jar.close();
				Handler.restart();
			}else if(!iconMini.exists()){
				ZipFile jar=new ZipFile(new File(Handler.getJarPath()));
				Files.copy(jar.getInputStream(jar.getEntry("IconMini.png")), iconMini.toPath());
				
				jar.close();
				Handler.restart();
			}
		}catch(Exception e){
			throw new RuntimeException(e);
		}
	}
	
	public static final Image icon=Toolkit.getDefaultToolkit().getImage("data/Icon.png"),iconMini=Toolkit.getDefaultToolkit().getImage("data/IconMini.png");
	
	public Gui(Handler handler){
		this.handler=handler;
		
		for(int i=0;i<handler.buttonNumber;i++)buttons.add(new Button(this,handler.slots.get(buttons.size()),buttons.size()));
		
		if(!handler.switchMode)setType(Type.UTILITY);
		else{
			setTitle("Lapis Multi-clipboard");
			setIconImage(icon);
		}
		setUndecorated(true);
		setSize(200, 80*buttons.size());
		setAlwaysOnTop(handler.alwaysOnTop);
		AWTUtilities.setWindowOpaque(this, false);
		
		MouseAdapter ma=new MouseAdapter(){
			@Override
			public void mouseMoved(MouseEvent e){
				
				selectedId=-1;
				
				int x=e.getX(),y=e.getY();
				
				if(x>5&&x<getWidth()-5&&y>5&&y<getHeight()-25){
					x-=5;
					y-=5;
					int buttonId=y/80,buttonY=y-buttonId*80,buttonYOff=buttonId==0?0:5;
					if(buttonId<buttons.size()&&buttonId>=0&&buttonY<70+buttonYOff&&buttonY>buttonYOff){
						selectedId=buttonId;
					}
				}
				updateButtons();
			}
			
			@Override
			public void mouseExited(MouseEvent e){
				selectedId=-1;
				updateButtons();
				markDirty();
			}
			
			@Override
			public void mouseClicked(MouseEvent e){
				mouseMoved(e);
				if(selectedId!=-1){
					handler.selectedSlot=selectedId;
					updateButtons();
					markDirty();
					handler.paste();
				}
			}
			@Override
			public void mouseWheelMoved(MouseWheelEvent e){
				selectedId=handler.selectedSlot+e.getWheelRotation();
				
				if(selectedId<0)selectedId=buttons.size()-1;
				else if(selectedId>=buttons.size())selectedId=0;
				
				handler.ignoreNext=handler.slots.get(selectedId).getActive()!=null;
				
				handler.selectedSlot=selectedId;
				updateButtons();
				markDirty();
				handler.paste();
			}
		};
		
		addMouseListener(ma);
		addMouseMotionListener(ma);
		addMouseWheelListener(ma);
		
	}
	
	
	@Override
	public void setSize(int width, int height){
		super.setSize(width, height+20);
	}
	@Override
	public void setLocation(int x, int y){
		x-=getWidth()/2;
		y-=getHeight()/2;
		
		Dimension dim=Toolkit.getDefaultToolkit().getScreenSize();
		if(x<0)x=0;
		else if(x>dim.width-getWidth())x=dim.width-getWidth();
		
		if(y<0)y=0;
		else if(y>dim.height-getHeight())y=dim.height-getHeight();
		
		super.setLocation(x,y);
	}
	
	public void open(){
		openningTransition=System.currentTimeMillis()+200;
		setVisible(true);
	}
	public void close(){
		setVisible(false);
	}
	
	
	public void markDirty(){
		renderDirty=true;
	}
	
	
	private void updateButtons(){
		if(lastSelectedId!=selectedId)markDirty();
		lastSelectedId=selectedId;
		
		setCursor(new Cursor(selectedId==-1?Cursor.DEFAULT_CURSOR:Cursor.HAND_CURSOR));
		for(int i=0;i<buttons.size();i++){
			buttons.get(i).wantedHighlight=i==selectedId||handler.selectedSlot==i?1:0;
		}
	}
	
	@Override
	public void paint(Graphics gg){
		Graphics2D g=(Graphics2D)gg;
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_RENDERING,RenderingHints.VALUE_RENDER_QUALITY);
		g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		g.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS,RenderingHints.VALUE_FRACTIONALMETRICS_ON);
		g.setFont(handler.font);
		
		Color c=handler.colorTheme;
		
		double d=(openningTransition-System.currentTimeMillis())/200;
		if(d<=0)d=0;
		else markDirty();
		g.translate(0, d*d*30);
		g.scale(1-d*0.1, 1-d*0.1);
		g.setColor(new Color(c.getRed(),c.getGreen(),c.getBlue(),150));
		g.fillRect(1, 1, getWidth()-1, getHeight()-21);
		
		bColor=new Color(
						c.getRed  ()+(c.getRed  ()+30>255?-1:1)*30,
						c.getGreen()+(c.getGreen()+30>255?-1:1)*30,
						c.getBlue ()+(c.getBlue ()+30>255?-1:1)*30
						);
		g.setColor(bColor);
		
		g.drawRect(0, 0, getWidth()-1, getHeight()-20);
		
		Rectangle r=new Rectangle(1, 1, getWidth()-1, getHeight()-21);
		
		g.setClip(r);
		
		g.translate(5, 5);
		buttons.forEach(b->{
			g.setClip(r);
			b.draw(g, getWidth()-12, 70);
			g.translate(0, 80);
		});
	}

	public void render(){
		if(!renderDirty)return;
		renderDirty=false;
		float d=(float)((openningTransition-System.currentTimeMillis())/200);
		if(d<0)d=0;
		setOpacity(1-((int)d*255)/255F);
	}
	
}
