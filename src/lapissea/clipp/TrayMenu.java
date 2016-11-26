package lapissea.clipp;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.SpinnerNumberModel;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;

@SuppressWarnings("serial")
public class TrayMenu extends JFrame{
	
	
	public TrayMenu(Handler handler, TrayIconHandler iconHandler){
		setDefaultCloseOperation(HIDE_ON_CLOSE);
		setLayout(new FlowLayout());
		setSize(445, 374);
		
		try{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}catch(Exception e){
			throw new RuntimeException(e);
		}
		
		setType(Type.POPUP);
		setIconImage(Gui.icon);
		JToggleButton ross=new JToggleButton("Run on system start");
		add(ross);
		ross.addActionListener(e->{
			
			String path=getJarPath();
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
			
		});
		ross.setSelected(handler.font.isBold());
		
		JPanel activationKeyWrapp=addGen(new JPanel());
		
		activationKeyWrapp.add(new JLabel("Activation key: "));
		JTextField activationKey=new JTextField();
		activationKeyWrapp.add(activationKey);
		activationKey.setColumns(15);
		activationKey.addKeyListener(new KeyAdapter(){
			@Override
			public void keyTyped(KeyEvent e){
				handler.activationKey=activationKey.getText();
			}
			@Override
			public void keyPressed(KeyEvent e){
				handler.activationKey=activationKey.getText();
			}
			@Override
			public void keyReleased(KeyEvent e){
				handler.activationKey=activationKey.getText();
			}
		});
		activationKey.setText(handler.activationKey);
		
		JPanel guiLook=new JPanel();
		guiLook.setBorder(new TitledBorder(null, "GUI look: ", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		guiLook.setLayout(new BoxLayout(guiLook, BoxLayout.Y_AXIS));
		add(guiLook);
		
		JPanel scrollTimWrap=new JPanel();
		guiLook.add(scrollTimWrap);
		
		scrollTimWrap.add(new JLabel("Scroll time (ms)"));
		
		JSpinner scrollTim=new JSpinner(new SpinnerNumberModel(1D, 1, 10000, 1));
		scrollTimWrap.add(scrollTim);
		
		scrollTim.addChangeListener(e->handler.scrollTime=(double)scrollTim.getValue());
		scrollTim.setValue(handler.scrollTime);
		
		JPanel fontWrapp=new JPanel();
		fontWrapp.setBorder(new TitledBorder(null, "Font", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		guiLook.add(fontWrapp);
		
		JPanel fontSizeWrap=new JPanel();
		fontWrapp.add(fontSizeWrap);
		
		fontSizeWrap.add(new JLabel("Font size"));
		
		JSpinner fontSize=new JSpinner(new SpinnerNumberModel(1, 1, 50, 1));
		fontSizeWrap.add(fontSize);
		fontSize.addChangeListener(e->handler.font=handler.font.deriveFont(handler.font.isBold()?Font.BOLD:Font.PLAIN, (int)fontSize.getValue()));
		fontSize.setValue(handler.font.getSize());
		
		JToggleButton boldFont=new JToggleButton("Bold font");
		fontWrapp.add(boldFont);
		boldFont.addChangeListener(e->handler.font=handler.font.deriveFont(boldFont.isSelected()?Font.BOLD:Font.PLAIN, handler.font.getSize()));
		boldFont.setSelected(handler.font.isBold());
		
		JPanel usageOptions=new JPanel();
		usageOptions.setBorder(new TitledBorder(null, "Usage Options:", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		add(usageOptions);
		usageOptions.setLayout(new BoxLayout(usageOptions, BoxLayout.Y_AXIS));
		
		JPanel p1=new JPanel();
		usageOptions.add(p1);
		
		JSpinner buttonNum=new JSpinner();
		p1.add(new JLabel("Button ammount:"));
		p1.add(buttonNum);
		buttonNum.addChangeListener(e->handler.buttonNumber=(int)buttonNum.getValue());
		buttonNum.setValue(handler.buttonNumber);
		
		JPanel panel_2=new JPanel();
		usageOptions.add(panel_2);
		
		JToggleButton switchMode=new JToggleButton("Switch mode");
		panel_2.add(switchMode);
		switchMode.addChangeListener(e->handler.switchMode=switchMode.isSelected());
		switchMode.setSelected(handler.switchMode);
		
		JToggleButton alwaysOnTop=new JToggleButton("Always on top");
		panel_2.add(alwaysOnTop);
		alwaysOnTop.addChangeListener(e->handler.alwaysOnTop=alwaysOnTop.isSelected());
		alwaysOnTop.setSelected(handler.alwaysOnTop);
		
		
		JPanel panel_3=new JPanel();
		add(panel_3);
		panel_3.setLayout(new FlowLayout());
		JButton kill=new JButton("Shut down Lapis-multi-clipboard");
		panel_3.add(kill);
		kill.addActionListener(e->{
			iconHandler.kill();
			System.exit(0);
		});
		
		JButton restartAndApply=new JButton("Restart and apply");
		panel_3.add(restartAndApply);
		restartAndApply.addActionListener(e->{
			try{
				handler.writeConfig();
				String path=getJarPath();
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
		});
		
		setPreferredSize(new Dimension(470, 235));
		pack();
	}
	
	public <T extends Component> T addGen(T comp){
		super.add(comp);
		return comp;
	}
	
	final static String LOOKANDFEEL="Metal";
	
	public void trayClicked(MouseEvent e){
		Rectangle bonds=GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
		setLocation(bonds.width-getWidth(), bonds.height-getHeight());
		setVisible(true);
	}
	
	private String getJarPath(){
		String path;
		try{
			path=Handler.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
		}catch(URISyntaxException e){
			throw new RuntimeException(e);
		}
		if(path.startsWith("/")||path.startsWith("\\")) path=path.substring(1);
		return path;
	}
}
