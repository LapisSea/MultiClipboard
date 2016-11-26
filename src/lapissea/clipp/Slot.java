package lapissea.clipp;

public class Slot{
	
//	private List<Pair<Object,Transferable>> history=new ArrayList<>();
	private Object obj;
//	private int historyPos=0;
	private Runnable changeHook;
	
	public void setChangeHook(Runnable hook){
		changeHook=hook;
	}
	
	public void newPaste(Object o){
		if(getActive()!=null&&getActive().equals(o))return;
//		while(historyPos<history.size())delLast();
//		if(history.size()>10)delLast();
//		history.add(o);
//		historyPos=history.size()-1;
		obj=o;
		
		callHook();
		System.gc();
	}
	
//	public void prev(){
//		historyPos--;
//		if(historyPos<0)historyPos=0;
//		callHook();
//	}
//	public void next(){
//		historyPos++;
//		if(historyPos>=history.size())historyPos=history.size()-1;
//		callHook();
//	}
	
	public Object getActive(){
		return obj;//history.isEmpty()?null:history.get(historyPos);
	}
	
//	private void delLast(){
//		history.remove(0);
//	}
	
	private void callHook(){
		if(changeHook!=null)changeHook.run();
	}
}
