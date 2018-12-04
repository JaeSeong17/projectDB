package termProject;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.sql.*;
import javax.swing.*;
import java.util.Calendar;

public class minor_panel extends JPanel implements ActionListener{
	
	Statement stmt = null;
	ResultSet rs;
	String sql = null;
	String ID = null;
	
	JTable item_table = new JTable();		//ITEM정보 들어갈 TABLE
	JButton put_button = new JButton();
	JComboBox<String> quantitycombobox = new JComboBox();
	String[] quantityary = new String[99];
	String[][] item_info = new String[30][4];
	String[] item_column = {"Product", "Price", "Producer", "Origin"};
	
	public minor_panel(Connection conn, String Cus_id, int minor_category_number) {
		
		try {
			stmt = conn.createStatement();
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		ID = Cus_id;

		for(int k=1;k<100;k++)
			quantityary[k-1] = Integer.toString(k);
		
		sql = "SELECT * FROM ITEM, CATEGORY WHERE CATEGORY.Product_number = ITEM.Product_number AND"
				+ " CATEGORY.Minor_number = " + minor_category_number;
		
		try {
			
			rs = stmt.executeQuery(sql);
		
			int num_of_item = 0;
		
			while(rs.next()) {
			
				item_info[num_of_item][0] = rs.getString(2);
				item_info[num_of_item][1] = rs.getString(6);
				item_info[num_of_item][2] = rs.getString(7);

				num_of_item++;	
			
			}
		
			for(int k=0;k<num_of_item;k++) {

				sql = "SELECT * FROM PRODUCERLOCATION P WHERE P.Pl_num = " + item_info[k][2];
		
				rs = stmt.executeQuery(sql);
		
				while(rs.next()) {
			
					item_info[k][2] = rs.getString(2);
					item_info[k][3] = rs.getString(3);
			
				}
		
			}
		
			item_table = new JTable(item_info, item_column);
			//minor_panel[j].setLayout(new BorderLayout());
			JScrollPane scroll = new JScrollPane(item_table);
			add(scroll);
			quantitycombobox = new JComboBox<String>(quantityary);	
			add(quantitycombobox);
			put_button = new JButton("Put in Cart");
			put_button.addActionListener(this);
			add(put_button);
			item_table.getColumn("Product").setPreferredWidth(150);
			item_table.getColumn("Price").setPreferredWidth(100);
			item_table.getColumn("Producer").setPreferredWidth(150);
			item_table.getColumn("Origin").setPreferredWidth(200);
		
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		
		String actionCmd = e.getActionCommand();
		
		if(actionCmd.equals("Put in Cart")) {
						
				String item_name;
				item_name = item_info[item_table.getSelectedRow()][0];
				
				int quantity = Integer.parseInt((String) quantitycombobox.getSelectedItem());
						
				try {
							
					sql = "SELECT Product_number FROM ITEM WHERE Product_name = \'" + item_name + "\'";
					rs = stmt.executeQuery(sql);
						
					rs.next();
					int product_number = rs.getInt(1);
							
					sql = "INSERT INTO SHOPPINGBAG VALUES ('" + ID + "', " + product_number + ", " + quantity + ")";
					System.out.println(sql);
					int update = stmt.executeUpdate(sql);
							
				} catch (SQLException e1) {
							
					e1.printStackTrace();
						
				}
		
		}
				
	}
	
}
