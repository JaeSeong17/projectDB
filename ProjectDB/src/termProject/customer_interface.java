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
	
	Statement stmt = null;
	ResultSet rs;
	String sql = null;
	
	String ID = "do1234";		//임시 테스트 아이디

	public customer_interface(Connection conn, String Cus_id) {

		try {
			stmt = conn.createStatement();
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		setSize(1000, 600);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		setLayout(new BorderLayout());	//전체 레이아웃 설정
		
		//상단에 위치할 기본정보 패널
		JPanel normal_info = new JPanel();
		add(normal_info, BorderLayout.NORTH);
		
		normal_info.setLayout(new GridLayout(0, 3));	//왼쪽 => 오늘날짜, 오른쪽 => 사용자 ID
		
		JLabel date_info = new JLabel();	//날짜정보
		normal_info.add(date_info);
		date_info.setText(today.get(Calendar.YEAR) + "/" + today.get(Calendar.MONTH) + "/" + today.get(Calendar.DATE));
		date_info.setFont(new Font("Serif", Font.BOLD, 30));
		
		JLabel shopname = new JLabel("JS Store");	//가게이름
		normal_info.add(shopname);
		shopname.setFont(new Font("Serif", Font.BOLD, 50));
		
		JLabel customer_id = new JLabel();	//현재 접속한 CUSTOMER의 ID표시
		normal_info.add(customer_id);
		customer_id.setText("Welcome " + ID + "!");
		date_info.setFont(new Font("Serif", Font.BOLD, 20));
		
		//기본탭 생성-----------------------------------------------------
		JTabbedPane selectPanel = new JTabbedPane();
		add(selectPanel);
		
		JPanel product = new JPanel();
		JPanel shoppingbag = new JPanel();
		JPanel user_info = new JPanel();
		
		selectPanel.addTab("PRODUCT", product);
		//PRODUCT탭 디자인
		JTabbedPane major_category = new JTabbedPane();
		product.add(major_category);
		//major_category에 DB에서 카테고리 이름 끌어와서 설정
		
		JPanel[] major_panel = new JPanel[10];			//개수를 동적으로 넣을지 정적으로 넣을지 선택
		JTabbedPane[] minor_category = new JTabbedPane[10];	//major_panel과 같은개수
		JPanel[] minor_panel = new JPanel[10];			//여기에 제품정보 출력
		JTable[][] item_table = new JTable[10][10];		//ITEM정보 들어갈 TABLE
		JButton[][] put_button = new JButton[30][4];
		String[][][][] item_info = new String[10][10][30][4];
		String[] item_column = {"Product", "Price", "Producer", "Origin"};
		
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
				major_panel[i].setPreferredSize(new Dimension(1000, 800));
				
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
				
					minor_panel[j] = new JPanel();
					minor_category[i].addTab(minor_category_info[j], minor_panel[j]);
					minor_panel[j].setPreferredSize(new Dimension(800, 600));
					
					sql = "SELECT * FROM ITEM, CATEGORY WHERE CATEGORY.Product_number = ITEM.Product_number AND"
							+ " CATEGORY.Minor_number = " + minor_category_number[j];
					
					rs = stmt.executeQuery(sql);
					
					int num_of_item = 0;
					
					while(rs.next()) {
						
						item_info[i][j][num_of_item][0] = rs.getString(2);
						item_info[i][j][num_of_item][1] = rs.getString(6);
						item_info[i][j][num_of_item][2] = rs.getString(7);

						num_of_item++;
						
					}
					
					for(int k=0;k<num_of_item;k++) {

						sql = "SELECT * FROM PRODUCERLOCATION P WHERE P.Pl_num = " + item_info[i][j][k][2];
					
						rs = stmt.executeQuery(sql);
					
						while(rs.next()) {
						
							item_info[i][j][k][2] = rs.getString(2);
							item_info[i][j][k][3] = rs.getString(3);
						
						}
					
					}
					
					item_table[i][j] = new JTable(item_info[i][j], item_column);
					//minor_panel[j].setLayout(new BorderLayout());
					JScrollPane scroll = new JScrollPane(item_table[i][j]);
					minor_panel[j].add(scroll);
					//minor_panel[j].add(item_table[i][j]);
					put_button[i][j] = new JButton("Put in Cart");
					minor_panel[j].add(put_button[i][j]);
					item_table[i][j].getColumn("Product").setPreferredWidth(150);
					item_table[i][j].getColumn("Price").setPreferredWidth(100);
					item_table[i][j].getColumn("Producer").setPreferredWidth(150);
					item_table[i][j].getColumn("Origin").setPreferredWidth(200);
					
				}

			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		//----------------------------------------------------------------
		
		selectPanel.addTab("SHOPPINTBAG", shoppingbag);
		
		String[][] shoppingbag_info = new String[40][5];
		int[] personal_shoppingbag = new int[40];
		String[] shoppingbag_column = {"Product", "Total Price", "Producer", "Origin", "Quantity"};
		JTable shoppingbag_table = new JTable(shoppingbag_info, shoppingbag_column);
		JScrollPane scroll = new JScrollPane(shoppingbag_table);
		shoppingbag.add(scroll);
		shoppingbag_table.getColumn("Product").setPreferredWidth(150);
		shoppingbag_table.getColumn("Total Price").setPreferredWidth(100);
		shoppingbag_table.getColumn("Producer").setPreferredWidth(150);
		shoppingbag_table.getColumn("Origin").setPreferredWidth(200);
		shoppingbag_table.getColumn("Quantity").setPreferredWidth(100);
		
		JPanel purchase_panel = new JPanel();
		shoppingbag.add(purchase_panel);
		purchase_panel.setLayout(new GridLayout(2, 0));
		int total_price = 0;
		String price = "Total Price : " + total_price;
		
		try {
			
			sql = "SELECT Product_number, Add_num  FROM SHOPPINGBAG S WHERE Cus_id = \'" + ID + "\'";
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
		
		JLabel price_panel  = new JLabel("Total Price : " + total_price);
		purchase_panel.add(price_panel);
		JButton purchase_button = new JButton("Purchase");
		purchase_panel.add(purchase_button);
		
		//----------------------------------------------------------------
		
		AccountDataPane accDataPane = new AccountDataPane(conn, Cus_id);
		selectPanel.addTab("USER_INFO", accDataPane.dataPanel[0]);
		
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
	}

}