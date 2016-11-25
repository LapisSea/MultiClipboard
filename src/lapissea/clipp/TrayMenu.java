package lapissea.clipp;

import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JToggleButton;
import javax.swing.UIManager;

@SuppressWarnings("serial")
public class TrayMenu extends JFrame{
	
	public TrayMenu(){
		
		if(!SystemTray.isSupported())throw new IllegalStateException("Your pc does not support tray icons!");
		
		TrayIcon trayIcon=new TrayIcon(Toolkit.getDefaultToolkit().getImage("data/Icon2.png"), "Lapis Multi-clipboard");
		SystemTray tray=SystemTray.getSystemTray();
		try{
			trayIcon.addMouseListener(new MouseAdapter(){
				
				@Override
				public void mouseClicked(MouseEvent e){
					trayClicked(e);
				}
			});
			tray.add(trayIcon);
		}catch(Exception e){
			throw new RuntimeException(e);
		}
		
		
		setDefaultCloseOperation(HIDE_ON_CLOSE);
		setLayout(new FlowLayout());
		setSize(300, 200);
		
		try{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}catch(Exception e){
			throw new RuntimeException(e);
		}
		
		setType(Type.POPUP);
		setIconImage(Gui.icon);
		
		addGen(new JButton("Shut down Lapis-multi-clipboard")).addActionListener(e->{
			tray.remove(trayIcon);
			System.exit(0);
		});
		JToggleButton runOnSysStart=new JToggleButton("Run on system start (ON)");
		addGen(runOnSysStart).addActionListener(e->{
			runOnSysStart.setText("Run on system start ("+(runOnSysStart.isSelected()?"ON":"OFF")+")");
			
			try{
				String path=Handler.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
				if(path.startsWith("/")||path.startsWith("\\"))path=path.substring(1);
				if(path.endsWith(".jar")){
					String batContent="@javaw -jar "+path;
					File startup=new File(System.getProperty("java.io.tmpdir").replace("Local\\Temp\\", "Roaming\\Microsoft\\Windows\\Start Menu\\Programs\\Startup")+"\\MultiClip.bat");
					try{
						startup.createNewFile();
						Files.write(startup.toPath(), batContent.getBytes());
					}catch(IOException e1){
						e1.printStackTrace();
					}
				}
				
				
			}catch(URISyntaxException e1){
				e1.printStackTrace();
			}
			
		});
		
		JPanel p1=new JPanel();
		JSpinner buttonNum=new JSpinner();

		buttonNum.addChangeListener(e->{
			int w=this.getWidth();
			int h=this.getHeight();
			
			this.action(null, null);
		});
		
		p1.add(new JLabel("Button ammount:"));
		p1.add(buttonNum);
		
		add(p1);
		
	}
	
	public <T extends Component> T addGen(T comp){
		super.add(comp);
		return comp;
	}
	
	final static String LOOKANDFEEL="Metal";
	
	private void trayClicked(MouseEvent e){
		setVisible(true);
	}
	
}
