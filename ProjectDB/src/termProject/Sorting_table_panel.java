package termProject;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.sql.*;
import javax.swing.*;
import java.util.Calendar;

public class Sorting_table_panel extends JPanel implements ActionListener{
	
	Statement stmt = null;
	ResultSet rs;
	String sql = null;
	
	String[][] stock = new String[100][5];
	String[] item_column = {"Product number", "Product name", "Importer", "Price", "PL num"};
	JTable stock_table = new JTable(stock, item_column);
	JScrollPane scroll = new JScrollPane(stock_table);
	JComboBox<String> quantitycombobox = new JComboBox();
	String[] quantityary = new String[99];
	
	public Sorting_table_panel(Connection conn) {
		
		try {
			stmt = conn.createStatement();
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		add(scroll);
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
		
		//table_sort(stock, i);
		
		JPanel extra_panel  = new JPanel();		//매출, 그외 버튼이 들어갈 패널
		add(extra_panel, BorderLayout.EAST);
		extra_panel.setLayout(new GridLayout(2, 0));		//매출패널, 주문버튼 분
		
		for(i=1;i<100;i++)
			quantityary[i-1] = Integer.toString(i);
		
		quantitycombobox = new JComboBox<String>(quantityary);	
		extra_panel.add(quantitycombobox);
		
		JButton order_button = new JButton("Order");
		order_button.addActionListener(this);
		extra_panel.add(order_button);
		
	}
	
//	void table_sort(String[][] table, int size) {
//		
//	        int min; //최소값을 가진 데이터의 인덱스 저장 변수
//	        String[] temp = new String[5];
//	        
//	        for(int i=0; i<size-1; i++){ // size-1 : 마지막 요소는 자연스럽게 정렬됨
//	            min = i;
//	            for(int j=i+1; j<size; j++){
//	                if(table[i][] > data[j]){
//	                    min = j;
//	                }
//	            }
//	            temp = data[min];
//	            data[min] = data[i];
//	            data[i] = temp;
//	        }
//	        
//	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
	}

}
