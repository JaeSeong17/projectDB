package termProject;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.sql.*;
import javax.swing.*;
import java.util.Calendar;

//customer 계정으로 로그인시 띄우는 화면
public class customer_interface extends JFrame implements ActionListener{
	
	private Calendar today = Calendar.getInstance();
	
	Connection con;
	Statement stmt = null;
	ResultSet rs;
	String sql = null;
	String ID = null;
	
	JPanel normal_info = new JPanel();
	JLabel date_info = new JLabel();	//날짜정보
	JLabel shopname = new JLabel("JS Store");	//가게이름
	JLabel customer_id = new JLabel();	//현재 접속한 CUSTOMER의 ID표시
	
	JPanel select = new JPanel();
	JTabbedPane selectPanel = new JTabbedPane();
	JPanel product = new JPanel();
	JPanel shoppingbag = new JPanel();
	JPanel user_info = new JPanel();
	
	JTabbedPane major_category = new JTabbedPane();
	
	JPanel[] major_panel = new JPanel[10];			//개수를 동적으로 넣을지 정적으로 넣을지 선택
	JTabbedPane[] minor_category = new JTabbedPane[10];	//major_panel과 같은개수
	minor_panel[] minor_panel = new minor_panel[10];		//여기에 제품정보 출력
	
	JDialog buying_decision = new JDialog();

	public customer_interface(Connection conn, String Cus_id) {
		
		ID = Cus_id;

		try {
			stmt = conn.createStatement();
			con = conn;
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		setSize(1200, 800);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		setLayout(new BorderLayout());	//전체 레이아웃 설정
		
		//상단에 위치할 기본정보 패널
		add(normal_info, BorderLayout.NORTH);
		
		normal_info.setLayout(new GridLayout(0, 3));	//왼쪽 => 오늘날짜, 오른쪽 => 사용자 ID
		
		normal_info.add(date_info);
		date_info.setText(today.get(Calendar.YEAR) + "/" + today.get(Calendar.MONTH) + "/" + today.get(Calendar.DATE));
		date_info.setFont(new Font("Serif", Font.BOLD, 30));
		
		normal_info.add(shopname);
		shopname.setFont(new Font("Serif", Font.BOLD, 50));
		
		normal_info.add(customer_id);
		customer_id.setText("Welcome " + Cus_id + "!");
		date_info.setFont(new Font("Serif", Font.BOLD, 20));
		
		//기본탭 생성-----------------------------------------------------
		
		add(select);
		select.add(selectPanel);
		
		
		selectPanel.addTab("PRODUCT", product);
		//PRODUCT탭 디자인
		product.add(major_category);
		//major_category에 DB에서 카테고리 이름 끌어와서 설정
		
		//동적으로 갯수, 이름 맞춰서 반복문 통해서 정보 집어넣음(3중 반복문)
		//"중요" 배열건드릴때 nullpointerexception 생각하고 만들것
		
		try {
			
			sql = "SELECT Major_tag, Major_number FROM MAJOR_CATEGORY";
			String[] major_category_info = new String[10];
			int[] major_category_number = new int[10];
			int num_of_major = 0;
			
			rs = stmt.executeQuery(sql);
			
			while(rs.next()) {
				
				major_category_info[num_of_major] = rs.getString(1);
				major_category_number[num_of_major] = rs.getInt(2);
				num_of_major++;
				
			}
			
			for(int i=0;i<num_of_major;i++) {

			 	major_panel[i] = new JPanel();
			 	minor_category[i] = new JTabbedPane();
				major_category.addTab(major_category_info[i], major_panel[i]);
				major_panel[i].add(minor_category[i]);
				major_panel[i].setPreferredSize(new Dimension(800, 600));
				
				sql = "SELECT Minor_tag, Minor_number FROM MINOR_CATEGORY WHERE Major_number = " + major_category_number[i];
				
				rs = stmt.executeQuery(sql);
				
				String[] minor_category_info = new String[10];
				int[] minor_category_number = new int[10];
				int num_of_minor = 0;
				
				while(rs.next()) {
					
					minor_category_info[num_of_minor] = rs.getString(1);
					minor_category_number[num_of_minor] = rs.getInt(2);
					num_of_minor++;
					
				}

				for(int j=0;j<num_of_minor;j++) {
				
					minor_panel[j] = new minor_panel(conn, Cus_id, minor_category_number[j]);
					
					minor_category[i].addTab(minor_category_info[j], minor_panel[j]);
					minor_panel[j].setPreferredSize(new Dimension(800, 600));
					
				}

			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		//----------------------------------------------------------------
		
		selectPanel.addTab("SHOPPINTBAG", shoppingbag);
		shoppingbag_refresh(shoppingbag, Cus_id);					//새로고침 기능 위해서 함수로 생성
			
		//----------------------------------------------------------------
		
		AccountDataPane accDataPane = new AccountDataPane(conn, Cus_id);
		selectPanel.addTab("USER_INFO", accDataPane.mainAccPanel);
	
	}
	
	public void shoppingbag_refresh(JPanel shoppingbag, String Cus_id) {
		
		String[][] shoppingbag_info = new String[40][5];
		
		int[] personal_shoppingbag = new int[40];
		String[] shoppingbag_column = {"Product", "Total Price", "Producer", "Origin", "Quantity"};
		JTable shoppingbag_table = new JTable(shoppingbag_info, shoppingbag_column);
		JScrollPane scroll = new JScrollPane(shoppingbag_table);
		shoppingbag.add(scroll);
		scroll.setPreferredSize(new Dimension(800, 650));
		shoppingbag_table.getColumn("Product").setPreferredWidth(150);
		shoppingbag_table.getColumn("Total Price").setPreferredWidth(100);
		shoppingbag_table.getColumn("Producer").setPreferredWidth(150);
		shoppingbag_table.getColumn("Origin").setPreferredWidth(200);
		shoppingbag_table.getColumn("Quantity").setPreferredWidth(100);
		
		JPanel purchase_panel = new JPanel();
		shoppingbag.add(purchase_panel);
		purchase_panel.setLayout(new GridLayout(3, 0));
		int total_price = 0;
		
		try {
			
			sql = "SELECT Product_number, Add_num  FROM SHOPPINGBAG S WHERE Cus_id = \'" + Cus_id + "\'";
			rs = stmt.executeQuery(sql);
			
			int i=0;
			
			while(rs.next()) {
				
				personal_shoppingbag[i] = rs.getInt(1);
				shoppingbag_info[i][4] = rs.getString(2);
				
				i++;
				
			}
			
			for(int j=0;j<i;j++) {
				
			sql = "SELECT * FROM ITEM S WHERE Product_number = " + personal_shoppingbag[j];
			rs = stmt.executeQuery(sql);
			
				while(rs.next()) {
					
					shoppingbag_info[j][0] = rs.getString(2);
					total_price += Integer.parseInt(rs.getString(6)) * Integer.parseInt(shoppingbag_info[j][4]);
					shoppingbag_info[j][1] = Integer.toString(Integer.parseInt(rs.getString(6)) * Integer.parseInt(shoppingbag_info[j][4]));
					shoppingbag_info[j][2] = rs.getString(7);
				
				}

				sql = "SELECT * FROM PRODUCERLOCATION P WHERE P.Pl_num = " + shoppingbag_info[j][2];
			
				rs = stmt.executeQuery(sql);
			
				while(rs.next()) {
				
					shoppingbag_info[j][2] = rs.getString(2);
					shoppingbag_info[j][3] = rs.getString(3);
				
				}
			
			}
		
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		JLabel price_panel = new JLabel("Total Price : " + total_price);
		purchase_panel.add(price_panel);
		JButton purchase_button = new JButton("Purchase");
		purchase_button.addActionListener(this);
		purchase_panel.add(purchase_button);
		JButton refresh_button = new JButton("Refresh");
		refresh_button.addActionListener(this);
		purchase_panel.add(refresh_button);
		
		JLabel total_price_panel = new JLabel("Total Price : " + total_price);
		buying_decision.setSize(300,150);
		buying_decision.setLayout(new GridLayout(2, 0));
		buying_decision.add(total_price_panel);
		JButton decision_button = new JButton("Decision");
		decision_button.addActionListener(this);
		buying_decision.add(decision_button);
		
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		
		String actionCmd = e.getActionCommand();
		
		if(actionCmd.equals("Purchase")) {
			
			buying_decision.setVisible(true);
			
		}
		
		else if(actionCmd.equals("Decision")) {
			
			try {
				
				con.setAutoCommit(false);
				
				String date = today.get(Calendar.YEAR) + "-" + today.get(Calendar.MONTH) + "-" + today.get(Calendar.DATE);
				
				sql = "SELECT count(*) FROM SHOPPINGBAG WHERE Cus_id = \'" + ID + "\'";
				rs = stmt.executeQuery(sql);
				
				rs.next();
				int num_of_update = rs.getInt(1);
				
				sql = "SELECT MAX(Order_num) FROM ORDERED";
				rs = stmt.executeQuery(sql);
				
				rs.next();
				int order_num = rs.getInt(1) + 1;
				
				sql = "SELECT Address FROM CUSTOMER WHERE Cus_id = \'" + ID + "\'";
				rs = stmt.executeQuery(sql);
				
				rs.next();
				String address = rs.getString(1);
				
				sql = "SELECT Office_num FROM RETAILER WHERE Address = \'" + address + "\'";
				rs = stmt.executeQuery(sql);
				
				rs.next();
				int address_num = rs.getInt(1);
				
				sql = "INSERT INTO ORDERED VALUES (" + order_num + ", \'" + ID + "\', " + address_num + ", \'" + date + "\')";
				System.out.println(sql);
				int update = stmt.executeUpdate(sql);
				
				sql = "SELECT Product_number, Add_num FROM SHOPPINGBAG WHERE Cus_id = \'" + ID + "\'";
				rs = stmt.executeQuery(sql);
				int[][] shoppingbag_list = new int[40][2];
				int i=0;
				
				while(rs.next()) {
					
					shoppingbag_list[i][0] = rs.getInt(1);
					shoppingbag_list[i][1] = rs.getInt(2);
					i++;
					
				}
				
				for(int j=0;j<num_of_update;j++) {
					
					int quantity = 0;
			
					sql = "INSERT INTO ORDERED_PRODUCT VALUES (" + order_num + ", " + shoppingbag_list[j][0] + ", " + shoppingbag_list[j][1] + ")";
					System.out.println(sql);
					update = stmt.executeUpdate(sql);
					
					sql = "DELETE FROM SHOPPINGBAG WHERE Cus_id = \'" + ID + "\' AND Product_number = " + shoppingbag_list[j][0] + " AND Add_num = " + shoppingbag_list[j][1];
					System.out.println(sql);
					update = stmt.executeUpdate(sql);
					
					sql = "SELECT Stock FROM STOCK_DATA WHERE Office_num = " + address_num + " AND Product_number = " + shoppingbag_list[j][0];
					rs = stmt.executeQuery(sql);
					
					rs.next();
					quantity = rs.getInt(1);
					
					String office_location;
					
					sql = "SELECT Address FROM CUSTOMER WHERE Cus_id = " + ID;
					rs = stmt.executeQuery(sql);
					
					rs.next();
					office_location = rs.getString(1);
					
					int office_num;
					
					sql = "SELECT Office_num FROM RETAILER WHERE Address = " + office_location;
					rs = stmt.executeQuery(sql);
					
					rs.next();
					office_num = rs.getInt(1);
					
					if ((quantity-shoppingbag_list[j][1]) < 0) {
						
						System.out.println("There is not enough stock");
						
						sql = "SELECT Product_name FROM ITEM WHERE Product_number = " + shoppingbag_list[j][0];
						rs = stmt.executeQuery(sql);
						rs.next();
						JOptionPane.showMessageDialog(null, "There is not enough stock(" + rs.getString(1) + ")");
						
						con.rollback();
						return;
						
					}
					
					sql = "UPDATE STOCK_DATA SET Stock = " + (quantity-shoppingbag_list[j][1]) + " WHERE Product_number = " + shoppingbag_list[j][0] + " AND Stock = " + shoppingbag_list[j][1] + 
							" AND Office_num = " + office_num;
					System.out.println(sql);
					update = stmt.executeUpdate(sql);
				
				}
				
				con.commit();
				
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			
			
			shoppingbag = new JPanel();
			shoppingbag_refresh(shoppingbag, ID);
			
			selectPanel.add( shoppingbag, "SHOPPINTBAG", 2);
			selectPanel.removeTabAt(1);
			//selectPanel.add( shoppingbag, "SHOPPINTBAG", 1);
			
			buying_decision.dispose();
			
		}
		
		else if(actionCmd.equals("Refresh")) {
			
			shoppingbag = new JPanel();
			shoppingbag_refresh(shoppingbag, ID);
		
			selectPanel.add( shoppingbag, "SHOPPINTBAG", 2);
			selectPanel.removeTabAt(1);
			
		}
		
	}

}