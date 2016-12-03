package lapissea.clipp;

import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.json.JSONObject;

import lc.kra.system.keyboard.GlobalKeyboardHook;
import lc.kra.system.keyboard.event.GlobalKeyEvent;
import lc.kra.system.keyboard.event.GlobalKeyListener;
import lc.kra.system.mouse.GlobalMouseHook;
import lc.kra.system.mouse.event.GlobalMouseEvent;
import lc.kra.system.mouse.event.GlobalMouseListener;

public class Handler implements GlobalKeyListener,GlobalMouseListener{
	
	private Gui					gui					=null;
	private Point				mousePos			=new Point(0, 0);
	public String				lastPress			=null,activationKey="X+Ctrl+Shift";
	public int					selectedSlot		=0;
	public List<Slot>			slots				=new ArrayList<>();
	public ClipBoardListener	clipBoardListener	=null;
	protected boolean			ignoreNext			=false;
	public int					buttonNumber		=3;
	public double				scrollTime			=1000;
	public Font					font				=new Font(Font.MONOSPACED, Font.BOLD, 14);
	public boolean				switchMode			=false,alwaysOnTop=true;
	public Color				colorTheme			=new Color(30,100,255);
	public Color				fontColor			=Color.WHITE;
	
	private final Runnable renderRun=new Runnable(){
		
		private long nextTimeToRender;
		
		@Override
		public void run(){
			while(true){
				if(!gui.isVisible()){
					synchronized(this){
						try{
							wait();
						}catch(InterruptedException e1){
							e1.printStackTrace();
						}
					}
				}
				
				
				long time=System.currentTimeMillis();
				if(time>=nextTimeToRender){
					nextTimeToRender=time+17;
					gui.render();
				}
				try{
					Thread.sleep(1);
				}catch(InterruptedException e){
					break;
				}
			}
		}
	};
	
	public Handler(){
		try{
			try{
				readConfig();
			}catch(Exception e){
				writeConfig();
				readConfig();
			}
		}catch(Exception e){
			throw new RuntimeException(e);
		}
		
		for(int i=0;i<buttonNumber;i++)
			slots.add(new Slot());
		
		clipBoardListener=new ClipBoardListener(){
			
			@SuppressWarnings("deprecation")
			@Override
			protected void onChange(Transferable t) throws Exception{
				if(ignoreNext){
					ignoreNext=false;
					return;
				}
				Object clip=null;
				if(t.isDataFlavorSupported(DataFlavor.imageFlavor)) clip=t.getTransferData(DataFlavor.imageFlavor);
				else if(t.isDataFlavorSupported(DataFlavor.javaFileListFlavor))clip=t.getTransferData(DataFlavor.javaFileListFlavor);
				else if(t.isDataFlavorSupported(DataFlavor.stringFlavor)) clip=t.getTransferData(DataFlavor.stringFlavor);
				else if(t.isDataFlavorSupported(DataFlavor.plainTextFlavor)){
					BufferedReader reader=new BufferedReader(DataFlavor.plainTextFlavor.getReaderForText(t));
					StringBuilder b=new StringBuilder();
					Iterator<String> i=reader.lines().iterator();
					while(i.hasNext()){
						b.append(i.next());
						if(i.hasNext()) b.append('\n');
					}
					clip=b.toString();
				}
				if(clip!=null){
					slots.get(selectedSlot).newPaste(clip);
					if(gui!=null)gui.markDirty();
				}
			}
		};
		
		gui=new Gui(this);
		new Thread(renderRun).start();
		
		new GlobalKeyboardHook().addKeyListener(this);
		new GlobalMouseHook().addMouseListener(this);
		new TrayIconHandler(this);
	}
	
	public void writeConfig() throws Exception{
		new File("data").mkdir();
		
		JSONObject config=new JSONObject();
		
		config.put("activation-key", activationKey);
		config.put("button-number", buttonNumber);
		
		JSONObject font=new JSONObject();
		font.put("is-bold", this.font.isBold());
		font.put("size", this.font.getSize());
		config.put("font", font);
		
		config.put("scroll-time (ms)", scrollTime);
		config.put("switch-mode", switchMode);
		config.put("always-on-top", alwaysOnTop);
		config.put("color-HEX", colorTheme.getRGB());
		
		Files.write(new File("data/Multi_copy_config.json").toPath(), config.toString(4).getBytes());
	}
	
	private void readConfig() throws Exception{
		JSONObject config=new JSONObject(new String(Files.readAllBytes(new File("data/Multi_copy_config.json").toPath())));
		activationKey=config.getString("activation-key");
		buttonNumber=config.getInt("button-number");
		
		JSONObject fontJ=config.getJSONObject("font");
		font=new Font(Font.MONOSPACED, fontJ.getBoolean("is-bold")?Font.BOLD:Font.PLAIN, fontJ.getInt("size"));
		scrollTime=config.getInt("scroll-time (ms)");
		switchMode=config.getBoolean("switch-mode");
		alwaysOnTop=config.getBoolean("always-on-top");
		colorTheme=new Color(config.getInt("color-HEX"));
		fontColor=(colorTheme.getGreen()+colorTheme.getGreen()+colorTheme.getBlue())/256F>2?Color.BLACK:Color.WHITE;
	}
	
	private String eventToString(GlobalKeyEvent event){
		StringBuilder b=new StringBuilder();
		b.append((char)event.getVirtualKeyCode());
		if(event.isControlPressed()) b.append("+Ctrl");
		if(event.isMenuPressed()) b.append("+Alt");
		if(event.isShiftPressed()) b.append("+Shift");
		return b.toString();
	}
	
	@Override
	public void keyPressed(GlobalKeyEvent event){
		String keys=eventToString(event);
		
		
		if(keys.equals(lastPress))return;
		lastPress=keys;
		
		if(keys.equals(activationKey)){
			if(gui.isVisible()){
				gui.close();
				if(switchMode) lastPress=null;
			}else{
				if(switchMode) lastPress=null;
				gui.setLocation(mousePos);
				gui.open();
				synchronized(renderRun){
					renderRun.notify();
				}
				
			}
		}
		
	}
	
	@Override
	public void keyReleased(GlobalKeyEvent event){
		char c=Character.toUpperCase((char)event.getVirtualKeyCode());
		if(event.isControlPressed()&&(c=='C'||c=='X')){
			clipBoardListener.call();
		}
		if(switchMode)return;
		if(gui.isVisible()){
			lastPress=null;
			gui.close();
		}
	}
	
	@SuppressWarnings("unchecked")
	public void paste(){
		Object obj=slots.get(selectedSlot).getActive();
		if(obj!=null){
			ignoreNext=true;
			Transferable clip=null;
			Clipboard cb=Toolkit.getDefaultToolkit().getSystemClipboard();
			if(obj instanceof String){
				StringSelection ss=new StringSelection((String)obj);
				cb.setContents(ss, ss);
				return;
			}
			else if(obj instanceof List)clip=new TransferableFileList((List<File>)obj);
			else if(obj instanceof Image)clip=new TransferableImage((Image)obj);
			
			if(clip!=null) cb.setContents(clip, null);
		}
	}
	
	
	private class TransferableFileList implements Transferable{
		
		List<File> i;
		
		public TransferableFileList(List<File> i){
			this.i=i;
		}
		
		@Override
		public Object getTransferData(DataFlavor flavor)throws UnsupportedFlavorException,IOException{
			if(flavor.equals(DataFlavor.javaFileListFlavor)&&i!=null){
				return i;
			}else{
				throw new UnsupportedFlavorException(flavor);
			}
		}
		
		@Override
		public DataFlavor[] getTransferDataFlavors(){
			DataFlavor[] flavors=new DataFlavor[1];
			flavors[0]=DataFlavor.javaFileListFlavor;
			return flavors;
		}
		
		@Override
		public boolean isDataFlavorSupported(DataFlavor flavor){
			DataFlavor[] flavors=getTransferDataFlavors();
			for(int i=0;i<flavors.length;i++){
				if(flavor.equals(flavors[i])){ return true; }
			}
			
			return false;
		}
	}
	private class TransferableImage implements Transferable{
		
		Image i;
		
		public TransferableImage(Image i){
			this.i=i;
		}
		
		@Override
		public Object getTransferData(DataFlavor flavor)
				throws UnsupportedFlavorException,IOException{
			if(flavor.equals(DataFlavor.imageFlavor)&&i!=null){
				return i;
			}else{
				throw new UnsupportedFlavorException(flavor);
			}
		}
		
		@Override
		public DataFlavor[] getTransferDataFlavors(){
			DataFlavor[] flavors=new DataFlavor[1];
			flavors[0]=DataFlavor.imageFlavor;
			return flavors;
		}
		
		@Override
		public boolean isDataFlavorSupported(DataFlavor flavor){
			DataFlavor[] flavors=getTransferDataFlavors();
			for(int i=0;i<flavors.length;i++){
				if(flavor.equals(flavors[i])){ return true; }
			}
			
			return false;
		}
	}
	
	@Override
	public void mousePressed(GlobalMouseEvent event){
		updatePos(event);
	}
	
	@Override
	public void mouseReleased(GlobalMouseEvent event){
		updatePos(event);
	}
	
	@Override
	public void mouseMoved(GlobalMouseEvent event){
		updatePos(event);
	}
	
	@Override
	public void mouseWheel(GlobalMouseEvent event){
		updatePos(event);
	}
	
	private void updatePos(GlobalMouseEvent event){
		mousePos.setLocation(event.getX(), event.getY());
	}

	public static String getJarPath(){
		String path;
		try{
			path=Handler.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
		}catch(URISyntaxException e){
			throw new RuntimeException(e);
		}
		if(path.startsWith("/")||path.startsWith("\\")) path=path.substring(1);
		return path;
	}
	public static void restart(){
		try{
			String path=Handler.getJarPath();
			if(!path.endsWith(".jar")){
				System.out.println("Start again");
				System.exit(0);
				return;
			}
			List<String> command=new ArrayList<>();
			command.add("javaw");
			command.add("-jar");
			command.add(path);
			
			final ProcessBuilder builder=new ProcessBuilder(command);
			builder.start();
		}catch(Exception e1){
			throw new RuntimeException(e1);
		}
		System.exit(0);
	}
}
