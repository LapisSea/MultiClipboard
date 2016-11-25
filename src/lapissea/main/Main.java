package lapissea.main;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.Files;

import lapissea.clipp.Handler;

public class Main{
	
	public static void main(String[] args){
		try{
			File f=new File("MultiClip.lock");
			f.deleteOnExit();
			FileChannel channel;
			FileLock lock;
			if(f.exists()) f.delete();
			channel=new RandomAccessFile(f, "rw").getChannel();
			lock=channel.tryLock();
			if(lock==null){
				channel.close();
				System.out.println("Already running");
				return;
			}
			Runtime.getRuntime().addShutdownHook(new Thread(()->{
				try{
					if(lock!=null){
						lock.release();
						channel.close();
						f.delete();
					}
				}catch(Exception e){}
			}));
		}catch(Exception e){
			System.out.println("Already running");
			return;
		}
		
		Thread.setDefaultUncaughtExceptionHandler((t, e)->{
			try{
				File folder=new File("error-log"),errorLog=new File("error-log/Fatal error_"+System.currentTimeMillis()+".txt");
				if(!folder.exists()||!folder.isDirectory())folder.mkdirs();
				errorLog.createNewFile();
				
				StringBuilder report=new StringBuilder("Error in: "+t.getName()+"\n==========================================\n\n");
				
				e.printStackTrace(new PrintStream(new OutputStream(){
					
					@Override
					public void write(int b) throws IOException{
						report.append((char)b);
					}
				}));
				
				report.append("\n==========================================");
				
				String s=report.toString();
				System.err.println(s);
				
				Files.write(errorLog.toPath(), s.getBytes());
				System.exit(1);
			}catch(Exception e1){
				e1.printStackTrace();
			}
			
		});
		new Handler();
	}
}
