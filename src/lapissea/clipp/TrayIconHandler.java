package lapissea.clipp;

import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class TrayIconHandler extends TrayIcon implements MouseListener{
	
	private TrayMenu			menu;
	private final SystemTray	tray=SystemTray.getSystemTray();
	private Handler				handler;
	
	public TrayIconHandler(Handler handler){
		super(Toolkit.getDefaultToolkit().getImage("data/Icon2.png"), "Lapis Multi-clipboard");
		this.handler=handler;
		
		if(!SystemTray.isSupported()) throw new IllegalStateException("Your pc does not support tray icons!");
		
		try{
			addMouseListener(this);
			tray.add(this);
		}catch(Exception e){
			throw new RuntimeException(e);
		}
	}
	
	public void kill(){
		tray.remove(this);
	}
	
	@Override
	public void mouseClicked(MouseEvent e){
		if(menu==null) menu=new TrayMenu(handler, this);
		menu.trayClicked(e);
	}
	
	@Override
	public void mousePressed(MouseEvent e){}
	
	@Override
	public void mouseReleased(MouseEvent e){}
	
	@Override
	public void mouseEntered(MouseEvent e){}
	
	@Override
	public void mouseExited(MouseEvent e){}
	
}
