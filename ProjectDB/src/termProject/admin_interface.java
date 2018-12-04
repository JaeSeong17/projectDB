package termProject;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.sql.*;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

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
	
	String[][] stock = new String[100][6];
	String[] item_column = {"Product number", "Product name", "Importer", "Price", "Producer", "Origin"};
	JTable stock_table = new JTable(stock, item_column);
	JScrollPane scroll = new JScrollPane(stock_table);
	JComboBox<String> quantitycombobox = new JComboBox();
	String[] quantityary = new String[99];
	
	JDialog buying_decision = new JDialog();
	JComboBox<String> locationcombobox = new JComboBox();
	String[] locationary = {"Seoul", "Busan", "Ulsan", "Daejeon",
			"Daegu", "Inchen", "Gwangju", "Jeollabuk-do", "Chungcheongbuk-do",
			"Jeju-do", "Chungcheongnam-do", "Gyeonsangbuk-do", "Gyeonsangnam-do",
			"Gangwon-do", "Gyeonggi-do", "Jeollabuk-do", "Jeollanam-do"
	};
	JButton ok_button = new JButton("OK");
	
	JPanel sales_panel = new JPanel();
	
	//---------------------------------------------------------------------------------------------------------
	//
	//매출정보탭에 사용될 구성요소 선언부
	private JPanel[] sales_subPanel = new JPanel[4];
	private JLabel[] sales_subTitle = new JLabel[4];
	
	//주문 기록에 대한 테이블 설정
	private DefaultTableModel ordModel;
	private JTable ordTable;
	private String ordHeader[] = {"Order_num", "Ordered_date", "Cus_id", "Product_name", "Product_quantity", "Address"};	//주문 기록 테이블 헤더
	private String ordList[][] = new String[0][6];		//테이블 내부 정보 저장 배열;
	private JScrollPane ordScrollPane;
	private JTextField ordJtfFilter = new JTextField();
	private TableRowSorter<TableModel> rowSorter;
	
	//매장별 매출 기록에 대한 테이블 설정
	private DefaultTableModel retModel;
	private JTable retTable;
	private String retHeader[] = {"Office_num", "Address", "Cus_id", "Product_name", "Product_quantity", "Ordered_date"};	//주문 기록 테이블 헤더
	private String retList[][] = new String[0][6];		//테이블 내부 정보 저장 배열;
	private JScrollPane retScrollPane;
	private JTextField retJtfFilter = new JTextField();
	//private TableRowSorter<TableModel> rowSorter;
	
	//----------------------------------------------------------------------------------------------------------
	
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
		stock_table.getColumn("Producer").setPreferredWidth(150);
		stock_table.getColumn("Origin").setPreferredWidth(200);
		
		locationcombobox = new JComboBox<String>(locationary);
		
		buying_decision.setSize(300,150);
		buying_decision.setLayout(new GridLayout(0, 2));
		
		buying_decision.add(locationcombobox);
		buying_decision.add(ok_button);
		
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
			
			for(int j=0;j<i;j++) {
				
				sql = "SELECT * FROM PRODUCERLOCATION WHERE Pl_num = " + stock[j][4];
				rs = stmt.executeQuery(sql);
				
				rs.next();
				stock[j][4] = rs.getString(2);
				stock[j][5] = rs.getString(3);
				
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
		
		//--------------------------------------------------------------------------------------------
		//--------------------------------------------------------------------------------------------
		
		/**
		 * SALES 탭 구현부
		 * 2*2 그리드로 화면구성
		 * -1.1 전체 주문기록 화면	
		 * -1.2 매장별 주문기록 화면
		 * -2.2 일별 매출 차트
		 * -2.2 월별 매출 차트
		 */
		
		selectPanel.addTab("SALES", sales_panel);
		sales_panel.setLayout(new GridLayout(2,2,30,30));
		
		sales_subPanel[0] = new JPanel();
		sales_subPanel[0].setLayout(new BorderLayout());

		sales_subPanel[1] = new JPanel();
		sales_subPanel[1].setLayout(new BorderLayout());
		
		
		//전체 주문 기록 테이블 1.1
		sales_subTitle[0] = new JLabel("Total Order Data");
		sales_subTitle[0].setForeground(Color.WHITE);
		sales_subTitle[0].setFont(new Font("San Serif", Font.PLAIN, 20));
		sales_subPanel[0].add(sales_subTitle[0], BorderLayout.NORTH);
		ordModel = new DefaultTableModel(ordList, ordHeader) {

			/**
			 * 주문 기록을 DB로 부터 받아 저장, 출력할 테이블 모델 설정
			 */
			private static final long serialVersionUID = 1L;
			public boolean isCellEditable(int rowIndex, int ColIndex) {
				return false;
			}
		};
		ordTable = new JTable(ordModel);
		rowSorter = new TableRowSorter<>(ordTable.getModel());
		ordTable.setRowSorter(rowSorter);
		ordTable.setRowHeight(25);
		ordScrollPane = new JScrollPane(ordTable);
		ordScrollPane.setPreferredSize(new Dimension(400, 300));
		
		//ordTable.getColumnModel().getColmn(c).setMinWidth(d);
		
		ordJtfFilter.getDocument().addDocumentListener(new DocumentListener(){
			
			@Override
			public void insertUpdate(DocumentEvent e) {
				String text = ordJtfFilter.getText();
				
				if(text.trim().length() == 0) {
					rowSorter.setRowFilter(null);
				}else {
					rowSorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
				}
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				throw new UnsupportedOperationException("Not supproted yet.");
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				String text = ordJtfFilter.getText();
				
				if(text.trim().length() == 0) {
					rowSorter.setRowFilter(null);
				}else {
					rowSorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
				}
				
			}
		});	
		
		try {
			stmt = conn.createStatement();
			String sql = "SELECT Order_num, Ordered_date, Cus_id, Product_name, Product_quantity, Address "
					+ "FROM ORDERED NATURAL JOIN ORDERED_PRODUCT, ITEM, RETAILER "
					+ "WHERE ORDERED_PRODUCT.Product_number = ITEM.Product_number "
					+ "AND RETAILER.Office_num = ORDERED.Office_num";
			rs = stmt.executeQuery(sql);
			DefaultTableModel tempModel = (DefaultTableModel) ordTable.getModel();
			while(rs.next()) {
				String Order_num = rs.getString(1);
				String Ordered_date = rs.getString(2);
				String Cus_id = rs.getString(3);
				String Product_name = rs.getString(4);
				String Product_quantity = rs.getString(5);
				String Address = rs.getString(6);
				
				String[] row = {Order_num, Ordered_date, Cus_id, Product_name, Product_quantity, Address};
				tempModel.addRow(row);
				//System.out.println("Order_number : " + Order_num + "\t Order_date : " + Ordered_date);
			}
		}catch(SQLException ex) {
			System.out.println("Error : " + ex);
		}
		
		ordScrollPane.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
		ordScrollPane.setBackground(Color.GRAY);
		sales_subPanel[0].add(ordScrollPane, BorderLayout.CENTER);
		ordJtfFilter.setSize(300, 20);
		sales_subPanel[0].add(ordJtfFilter, BorderLayout.SOUTH);
		
		
		sales_panel.add(sales_subPanel[0]);
		
		
		
		
		// 매장별 주문기록 테이블 1.2

		sales_subTitle[1] = new JLabel("Total Order Data");
		sales_subTitle[1].setForeground(Color.WHITE);
		sales_subTitle[1].setFont(new Font("San Serif", Font.PLAIN, 20));
		sales_subPanel[1].add(sales_subTitle[1], BorderLayout.NORTH);
		retModel = new DefaultTableModel(retList, retHeader) {

			/**
			 * 주문 기록을 DB로 부터 받아 저장, 출력할 테이블 모델 설정
			 */
			private static final long serialVersionUID = 1L;
			public boolean isCellEditable(int rowIndex, int ColIndex) {
				return false;
			}
		};
		retTable = new JTable(retModel);
		rowSorter = new TableRowSorter<>(retTable.getModel());
		retTable.setRowSorter(rowSorter);
		retTable.setRowHeight(25);
		retScrollPane = new JScrollPane(retTable);
		retScrollPane.setPreferredSize(new Dimension(400, 300));
		
		//retTable.getColumnModel().getColmn(c).setMinWidth(d);
		
		retJtfFilter.getDocument().addDocumentListener(new DocumentListener(){
			
			@Override
			public void insertUpdate(DocumentEvent e) {
				String text = retJtfFilter.getText();
				
				if(text.trim().length() == 0) {
					rowSorter.setRowFilter(null);
				}else {
					rowSorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
				}
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				throw new UnsupportedOperationException("Not supproted yet.");
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				String text = retJtfFilter.getText();
				
				if(text.trim().length() == 0) {
					rowSorter.setRowFilter(null);
				}else {
					rowSorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
				}
				
			}
		});	
		
		try {
			stmt = conn.createStatement();
			String sql = "SELECT RETAILER.Office_num, Address, Cus_id, Product_name, Product_quantity, Ordered_date "
					+ "FROM ORDERED NATURAL JOIN ORDERED_PRODUCT, ITEM, RETAILER "
					+ "WHERE ORDERED_PRODUCT.Product_number = ITEM.Product_number "
					+ "AND RETAILER.Office_num = ORDERED.Office_num";
			rs = stmt.executeQuery(sql);
			DefaultTableModel tempModel = (DefaultTableModel) retTable.getModel();
			while(rs.next()) {
				String Office_num = rs.getString(1);
				String Address = rs.getString(2);
				String Cus_id = rs.getString(3);
				String Product_name = rs.getString(4);
				String Product_quantity = rs.getString(5);
				String Ordered_date = rs.getString(6);
				
				String[] row = {Office_num, Address, Cus_id, Product_name, Product_quantity, Ordered_date};
				tempModel.addRow(row);
				//System.out.println("Order_number : " + Order_num + "\t Order_date : " + Ordered_date);
			}
		}catch(SQLException ex) {
			System.out.println("Error : " + ex);
		}
		
		retScrollPane.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
		retScrollPane.setBackground(Color.GRAY);
		sales_subPanel[1].add(retScrollPane, BorderLayout.CENTER);
		retJtfFilter.setSize(300, 20);
		sales_subPanel[1].add(retJtfFilter, BorderLayout.SOUTH);
		
		
		sales_panel.add(sales_subPanel[1]);
		
	}
	
	

	@Override
	public void actionPerformed(ActionEvent e) {
	
		String actionCmd = e.getActionCommand();
		
		if(actionCmd.equals("Order")) {
			
			buying_decision.setVisible(true);
			
		}
		
		else if(actionCmd.equals("OK")) {
			
			int item_number = 0;
			item_number = Integer.parseInt(stock[stock_table.getSelectedRow()][1]);
			
			int quantity = Integer.parseInt((String) quantitycombobox.getSelectedItem());
					
			try {
						
				sql = "UPDATE STOCK_DATA SET Stock = " + (quantity+stock[stock_table.getSelectedRow()][1]) + " WHERE Office_num = " + locationcombobox.getSelectedItem() +
						" AND Product_number = " + item_number;
				System.out.println(sql);
				int update = stmt.executeUpdate(sql);
						
			} catch (SQLException e1) {
						
				e1.printStackTrace();
					
			}
			
			buying_decision.setVisible(false);
			
		}
		
	}

}
