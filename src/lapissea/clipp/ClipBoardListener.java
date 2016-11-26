package lapissea.clipp;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.FlavorEvent;
import java.awt.datatransfer.FlavorListener;
import java.awt.datatransfer.Transferable;

public abstract class ClipBoardListener implements FlavorListener{
	
	private static final Clipboard sysClip=Toolkit.getDefaultToolkit().getSystemClipboard();
	
	
	public ClipBoardListener(){
		sysClip.addFlavorListener(this);
	}
	@Override
	public void flavorsChanged(FlavorEvent e){
		call();
	}
	
	public void call(){
		try{
			onChange(sysClip.getContents(this));
		}catch(Exception e1){
			System.out.println(e1);
		}
	}
	
	protected abstract void onChange(Transferable t)throws Exception;
}
