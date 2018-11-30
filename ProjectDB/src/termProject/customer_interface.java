package termProject;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

public class customer_interface extends JFrame implements ActionListener{

	public customer_interface() {
		
		setSize(1000, 600);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JTabbedPane selectPanel = new JTabbedPane();
		
		JPanel product = new JPanel();
		JPanel shoppingbag = new JPanel();
		JPanel user_info = new JPanel();
		
		selectPanel.addTab("PRODUCT", product);
		selectPanel.addTab("SHOPPINTBAG", shoppingbag);
		selectPanel.addTab("USER_INFO", user_info);
		add(selectPanel);
		
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
	}

}
