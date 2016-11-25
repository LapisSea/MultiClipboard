package lapissea.clipp;

import java.awt.Font;
import java.awt.Point;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.io.BufferedReader;
import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.json.JSONObject;

import javafx.util.Pair;
import lc.kra.system.keyboard.GlobalKeyboardHook;
import lc.kra.system.keyboard.event.GlobalKeyEvent;
import lc.kra.system.keyboard.event.GlobalKeyListener;
import lc.kra.system.mouse.GlobalMouseHook;
import lc.kra.system.mouse.event.GlobalMouseEvent;
import lc.kra.system.mouse.event.GlobalMouseListener;

public class Handler implements GlobalKeyListener,GlobalMouseListener{

	private Gui gui;
	private Point mousePos=new Point(0, 0);
	private String lastPress,activationKey="X+Ctrl+Shift";
	public int selectedSlot=0;
	public List<Slot> slots=new ArrayList<>();
	public ClipBoardListener clipBoardListener;
	protected boolean ignoreNext;
	public int buttonNumber;
	public double scrollTime;
	public Font font;
	public boolean switchMode,alwaysOnTop;
	
	private final Runnable renderRun=new Runnable(){
		private long nextTimeToRender;
		@Override
		public void run(){
			while(true){
				synchronized(this){
					try{
						if(!gui.isVisible())wait();
					}catch(InterruptedException e1){
						e1.printStackTrace();
					}
				}
				
				long time=System.currentTimeMillis();
				if(time>=nextTimeToRender){
					nextTimeToRender=time+17;
					gui.render();
				}
				try{
					Thread.sleep(1);
				}catch(InterruptedException e){break;}
			}
		}
	};
	
	public Handler(){

		File config=new File("Multi_copy_config.json");
		try{
			if(!config.exists()||!config.isFile())createConfig();
			try{
				readConfig();
			}catch(Exception e){
				createConfig();
				readConfig();
			}
		}catch(Exception e){
			throw new RuntimeException(e);
		}
		
		for(int i=0;i<buttonNumber;i++)slots.add(new Slot());
		
		
		clipBoardListener=new ClipBoardListener(){
			@Override
			protected void onChange(Clipboard c, Transferable t)throws Exception{
				if(ignoreNext){
					ignoreNext=false;
					return;
				}
				Object clip=null;
				if(t.isDataFlavorSupported(DataFlavor.imageFlavor))clip=t.getTransferData(DataFlavor.imageFlavor);
				else if(t.isDataFlavorSupported(DataFlavor.javaFileListFlavor))clip=t.getTransferData(DataFlavor.javaFileListFlavor);
				else if(t.isDataFlavorSupported(DataFlavor.stringFlavor))clip=t.getTransferData(DataFlavor.stringFlavor);
				else if(t.isDataFlavorSupported(DataFlavor.plainTextFlavor)){
					BufferedReader reader=new BufferedReader(DataFlavor.plainTextFlavor.getReaderForText(t));
					StringBuilder b=new StringBuilder();
					Iterator<String> i=reader.lines().iterator();
					while(i.hasNext()){
						b.append(i.next());
						if(i.hasNext())b.append('\n');
					}
					clip=b.toString();
				}
				if(clip!=null){
					slots.get(selectedSlot).newPaste(new Pair<Object,Transferable>(clip, t));
					if(gui!=null)gui.markDirty();
				}
			}
		};
		
		clipBoardListener.start();
		while(!clipBoardListener.isAlive());
		gui=new Gui(this);
		new Thread(renderRun).start();
		
		new GlobalKeyboardHook().addKeyListener(this);
		new GlobalMouseHook().addMouseListener(this);
		
		new TrayMenu();
	}
	
	private void createConfig()throws Exception{
		new File("data").mkdir();
		File file=new File("data/Multi_copy_config.json");
		file.createNewFile();
		
		JSONObject config=new JSONObject();

		config.put("activation-key", "X+Ctrl+Shift");
		config.put("button-number", 3);
		
		JSONObject font=new JSONObject();{
			font.put("is-bold", true);
			font.put("size", 14);
		}config.put("font", font);

		config.put("scroll-time (ms)", 1000);
		config.put("switch-mode", false);
		config.put("always-on-top", true);
		
		Files.write(file.toPath(), config.toString(4).getBytes());
	}
	
	private void readConfig()throws Exception{
		JSONObject config=new JSONObject(new String(Files.readAllBytes(new File("data/Multi_copy_config.json").toPath())));
		activationKey=config.getString("activation-key");
		buttonNumber=config.getInt("button-number");
		
		JSONObject fontJ=config.getJSONObject("font");
		font=new Font(Font.MONOSPACED, fontJ.getBoolean("is-bold")?Font.BOLD:Font.PLAIN, fontJ.getInt("size"));
		scrollTime=config.getInt("scroll-time (ms)");
		switchMode=config.getBoolean("switch-mode");
		alwaysOnTop=config.getBoolean("always-on-top");
	}
	
	private String eventToString(GlobalKeyEvent event){
		StringBuilder b=new StringBuilder();
		b.append((char)event.getVirtualKeyCode());
		if(event.isControlPressed())b.append("+Ctrl");
		if(event.isMenuPressed())b.append("+Alt");
		if(event.isShiftPressed())b.append("+Shift");
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
				if(switchMode)lastPress=null;
			}
			else{
				if(switchMode)lastPress=null;
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
		if(switchMode)return;
		if(gui.isVisible()){
			lastPress=null;
			gui.close();
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
}
