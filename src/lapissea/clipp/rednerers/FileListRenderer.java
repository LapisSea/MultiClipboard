package lapissea.clipp.rednerers;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import lapissea.clipp.Gui;
import sun.awt.shell.ShellFolder;

public class FileListRenderer implements Renderable{

	private final List<File>	files;
	private BufferedImageRenderer compiled;
	private final Gui			gui;
	
	public FileListRenderer(Gui gui, List<File> files){
		this.files=files;
		this.gui=gui;
	}
	
	@Override
	public void render(Graphics2D g, int width, int height, float highlight, int id){
		if(compiled==null)update(g, width, height);
		g.setColor(new Color(0.3F-highlight*0.3F, 0.3F-highlight*0.3F, 1, 0.4F));
		g.fillRect(0, 0, width, height);
		compiled.render(g, width, height, highlight, id);
	}
	
	@Override
	public void update(Graphics2D g1, int width, int height){
		List<BufferedImage> fileImgs=new ArrayList<>();
		try{
			files.forEach(f->fileImgs.add(compileFile(f, width)));
		}catch(Exception e){
			e.printStackTrace();
		}
		BufferedImage combined = new BufferedImage(width, 74*fileImgs.size()-4, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = combined.createGraphics();
		g.translate(0, 3);
		fileImgs.forEach(img->{
			g.drawImage(img, 0, 0, null);
			g.translate(0, 74);
		});
		g.dispose();
		
		compiled=new BufferedImageRenderer(gui, combined);
	}
	
	private BufferedImage compileFile(File file,int width){
		Image fileIcon=null;
		try{
			fileIcon=ShellFolder.getShellFolder(file).getIcon(true).getScaledInstance(64, 64, 0);
		}catch(Exception e){
			e.printStackTrace();
		}
		
		BufferedImage bi = new BufferedImage(width, 64, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = bi.createGraphics();
		g.setFont(gui.handler.font);
		g.translate(5, 0);
		if(fileIcon!=null)g.drawImage(fileIcon, 0, 0, null);
		String path=file.getAbsolutePath();
		int lines=64/gui.handler.font.getSize();
		
		int aviablePLine=(int)Math.floor((width-64-13F)/g.getFontMetrics().charWidth('a'));
		
		int maxSize=aviablePLine*lines;
		if(path.length()>maxSize){
			maxSize-=3;
			path="..."+path.substring(path.length()-maxSize);
		}
		StringRenderer rednerer=new StringRenderer(gui, path).withNoBG();
		g.translate(64, 0);
		rednerer.render(g, width-64, Integer.MAX_VALUE, 0, 0);
		
		g.dispose();
		return bi;
	}
	
}
