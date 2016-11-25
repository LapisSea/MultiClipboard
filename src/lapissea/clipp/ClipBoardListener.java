package lapissea.clipp;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.Transferable;

public abstract class ClipBoardListener extends Thread implements ClipboardOwner{
	
	private static final Clipboard sysClip=Toolkit.getDefaultToolkit().getSystemClipboard();
	
	
	@Override
	public void run(){
		lostOwnership(sysClip,sysClip.getContents(this));
	}
	
	@Override
	public void lostOwnership(Clipboard c, Transferable t){
		boolean notDone=true;
		while(notDone){
			try{
				ClipBoardListener.sleep(10);
				Transferable contents=sysClip.getContents(this);
				onChange(c,contents);
				takeOwnership(contents);
				notDone=false;
			}catch(Exception ex){
				System.out.println(ex);
			}
		}
	}
	
	private void takeOwnership(Transferable t){
		sysClip.setContents(t, this);
	}
	
	protected abstract void onChange(Clipboard c, Transferable t)throws Exception;
}
