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
	
	private Calendar today = Calendar.getInstance();
	
	Statement stmt = null;
	ResultSet rs;
	String sql = null;
	String ID = null;
	
	JTabbedPane selectPanel = new JTabbedPane();
	
	JPanel stock_panel = new JPanel();
	
	String[][] stock = new String[100][5];
	String[] item_column = {"Product number", "Product name", "Importer", "Price", "PL num"};
	JTable stock_table = new JTable(stock, item_column);
	JScrollPane scroll = new JScrollPane(stock_table);
	JComboBox<String> quantitycombobox = new JComboBox();
	String[] quantityary = new String[99];
	
	JPanel sales_panel = new JPanel();
	
	public admin_interface(Connection conn) {
		
		try {
			stmt = conn.createStatement();
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		setSize(1400, 800);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(new BorderLayout());
		
		//재고증가, 재고 모자란 아이템 리스트, 매출확인
		
		add(selectPanel);
		selectPanel.addTab("STOCK", stock_panel);
		stock_panel.add(scroll);
		scroll.setPreferredSize(new Dimension(1200, 700));
		stock_table.getColumn("Product number").setPreferredWidth(100);
		stock_table.getColumn("Product name").setPreferredWidth(100);
		stock_table.getColumn("Importer").setPreferredWidth(150);
		stock_table.getColumn("Price").setPreferredWidth(100);
		stock_table.getColumn("PL num").setPreferredWidth(50);
		
		int i=0;
		
		try {
			
			sql = "SELECT * FROM ITEM";
			rs = stmt.executeQuery(sql);
			
			while(rs.next()) {
				
				stock[i][0] = Integer.toString(rs.getInt(1));
				stock[i][1] = rs.getString(2);
				stock[i][2] = rs.getString(5);
				stock[i][3] = Integer.toString(rs.getInt(6));
				stock[i][4] = Integer.toString(rs.getInt(7));
				
				i++;
				
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		JPanel extra_panel  = new JPanel();		//매출, 그외 버튼이 들어갈 패널
		stock_panel.add(extra_panel, BorderLayout.EAST);
		extra_panel.setLayout(new GridLayout(2, 0));		//매출패널, 주문버튼 분
		
		for(i=1;i<100;i++)
			quantityary[i-1] = Integer.toString(i);
		
		quantitycombobox = new JComboBox<String>(quantityary);	
		extra_panel.add(quantitycombobox);
		
		JButton order_button = new JButton("Order");
		order_button.addActionListener(this);
		extra_panel.add(order_button);
		
		//-----------------------------------------------------------------------
		
		selectPanel.addTab("SALES", sales_panel);
		
	}
	
	

	@Override
	public void actionPerformed(ActionEvent e) {
	
		String actionCmd = e.getActionCommand();
		
		if(actionCmd.equals("Order")) {
			
			//
			
		}
		
	}

}
