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
		
		setSize(2000, 800);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(new GridLayout(0, 2));
		
		//재고증가, 재고 모자란 아이템 리스트, 매출확인
		
		String[][] stock = new String[30][9];
		String[] item_column = {"Product number", "Product_name", "Date_of_manufacture", "Expiration_date", "Importer", "Price", "PL_num", "Producer", "Producer Location"};
		JTable stock_table = new JTable(stock, item_column);
		JScrollPane scroll = new JScrollPane(stock_table);
		add(scroll);
		
		JPanel extra_panel  = new JPanel();		//매출, 그외 버튼이 들어갈 패널
		add(extra_panel);
		extra_panel.setLayout(new GridLayout(2, 0));		//매출패널, 주문버튼 분
		
		JButton order_button = new JButton("Order");
		extra_panel.add(order_button);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
	}

}
