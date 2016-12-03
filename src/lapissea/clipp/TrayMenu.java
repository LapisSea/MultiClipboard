package lapissea.clipp;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JColorChooser;
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
	
	
	public TrayMenu(Handler handler, TrayIconHandler iconHandler) throws Exception{
		super("Multi-clipboard settings");
		setDefaultCloseOperation(HIDE_ON_CLOSE);
		getContentPane().setLayout(new FlowLayout());
		setSize(470, 258);
		
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		
		setType(Type.POPUP);
		setIconImage(Gui.icon);
		JToggleButton ross=new JToggleButton("Run on system start");
		getContentPane().add(ross);
		ross.addActionListener(e->{
			File startup=new File(System.getProperty("java.io.tmpdir").replace("Local\\Temp\\", "Roaming\\Microsoft\\Windows\\Start Menu\\Programs\\Startup")+"\\MultiClip.vbs");
			if(!ross.isSelected()){
				startup.delete();
				return;
			}
			String path=Handler.getJarPath();
			if(path.endsWith(".jar")){
				String slashN=System.getProperty("line.separator"),vbsContent=
						  "Set WshShell = CreateObject(\"WScript.Shell\" )"+slashN
						+ "WshShell.Run chr(34) & \""+new File(path)+"\" & Chr(34), 0"+slashN
						+ "Set WshShell = Nothing";
				try{
					Files.write(startup.toPath(), vbsContent.getBytes());
				}catch(Exception e1){
					e1.printStackTrace();
				}
			}
			
		});
		ross.setSelected(handler.font.isBold());
		
		JButton guiColor=new JButton("Gui color");
		
		getContentPane().add(guiColor);
		
		guiColor.addActionListener(new ActionListener(){
			
			@Override
			public void actionPerformed(ActionEvent e){
				Color c=JColorChooser.showDialog(TrayMenu.this, "Select a gui color",handler.colorTheme);
				if(c!=null){
					handler.colorTheme=c;
					handler.fontColor=(c.getGreen()+c.getGreen()+c.getBlue())/256F>2?Color.BLACK:Color.WHITE;
				}
			}
		});
		
		
		JPanel activationKeyWrapp=new JPanel();
		
		getContentPane().add(activationKeyWrapp);
		
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
		getContentPane().add(guiLook);
		
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
		getContentPane().add(usageOptions);
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
		
		URI uri = new URI("http://lapissea.byethost8.com/");
		
		JLabel link = new JLabel("<html>Made by <font color=#000099><u>LapisSea</u></font>");
		link.addMouseListener(new MouseAdapter(){
			@Override
			public void mouseClicked(MouseEvent e){
				if(Desktop.isDesktopSupported()){
					try{
						Desktop.getDesktop().browse(uri);
					}catch(IOException e1){
						throw new RuntimeException(e1);
					}
				}
			}
		});
		link.setToolTipText(uri.toString());
		link.setCursor(new Cursor(Cursor.HAND_CURSOR));
		
		getContentPane().add(link);
		
		JPanel panel_3=new JPanel();
		getContentPane().add(panel_3);
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
			}catch(Exception e1){
				throw new RuntimeException(e1);
			}
			Handler.restart();
		});
		
		setPreferredSize(new Dimension(470, 235));
		pack();
		setResizable(false);
	}
	
	
	
	public void trayClicked(MouseEvent e){
		Rectangle bonds=GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
		setLocation(bonds.width-getWidth(), bonds.height-getHeight());
		setVisible(true);
	}
}
