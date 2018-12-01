package termProject;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.sql.*;
import javax.swing.*;
import java.util.Calendar;

//admin 계정으로 로그인시 띄우는 화면
public class admin_interface extends JFrame implements ActionListener{
	
	public admin_interface(Connection conn) {
		
		setSize(1000, 600);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
	}

}
