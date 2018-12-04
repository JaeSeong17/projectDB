package termProject;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

public class AccountDataPane extends JFrame{

	/**
	 * 개인정보 창 구현부 
	 * 1. 개인정보 열람
	 * 2. 회원정보 수정
	 * (회원정보 양식 - Cus_id, Cus_password, Address, Phone_num, First_name, Last_name, Sex, Birth_date, Job)
	 * 	- 아이디 수정 불가능, 아이디 제외 정보 수정 가능
	 * 
	 * 3. 주문 내역 확인
	 */
	private static final long serialVersionUID = 1L;
	
	protected JPanel mainAccPanel;		//가장 바탕이 되는 메인 패널 - (계정정보창 + 주문내역)
	protected JPanel[] dataPanel = new JPanel[8];		// 계정정보창에 사용되는 패널
	protected JPanel[] orderDataPanel = new JPanel[2];	// 주문기록창에 사용되는 패널
	
	private JLabel[] subTitle = new JLabel[2];		// 각 패널에 달릴 부제 설정
	
	private JLabel[] accountTag = new JLabel[9];	// 개인정보 표시부 각 데이터 태그 설정
	private JLabel[] accountData = new JLabel[9];
	protected JLabel immutableIDData;					// 수정패널 - 해당 ID 표시 (수정 불가능)
	protected JTextField[] mutableData = new JTextField[8];	// 수정패널 - 각 태그에 해당되는 수정가능한 개인정보 표시 (직접입력 수정)
	protected JTextField[] mutablePhoneData = new JTextField[3]; // 수정패널 -핸드폰 번호
	private JPanel phonePanel;
	private JPanel birthPanel;
	
	//지역 선택상자 선언
	private String selectAddress[] = {"Seoul", "Busan", "Ulsan", "Daejeon",
				"Daegu", "Inchen", "Gwangju", "Jeollabuk-do", "Chungcheongbuk-do",
				"Jeju-do", "Chungcheongnam-do", "Gyeonsangbuk-do", "Gyeonsangnam-do",
				"Gangwon-do", "Gyeonggi-do", "Jeollabuk-do", "Jeollanam-do"
	};
	private JComboBox<String> addComboBox;
	
	//성별 선택상자 선언
	private String selectSex[] = {"Select", "M", "W"};
	private JComboBox<String> sexComboBox;
	
	//생년월일 선택상자 선언
	private String[] arrYear = new String[101];
	private String[] arrMonth = new String[13];
	private String[] arrDate = new String[32];
	private JComboBox<String> yearComboBox;
	private JComboBox<String> monthComboBox;
	private JComboBox<String> dateComboBox;
	
	private String[] dataArr = new String[9];	// DB로부터 해당 회원정보를 받아와서 저장할 문자열 배열
	
	private JButton[] changeBtn = new JButton[3]; //회원정보 수정 확인 버튼
	
	
	//주문 기록에 대한 테이블 설정
	private DefaultTableModel model;
	private JTable orderList;
	private String header[] = {"Product Name", "Number of Item ordered", "Ordered Date"};	//주문 기록 테이블 헤더
	private String list[][] = new String[0][7];		//테이블 내부 정보 저장 배열;
	private JScrollPane scrollPane;
	private JTextField jtfFilter = new JTextField();
	private TableRowSorter<TableModel> rowSorter;
	
	
	Statement stmt = null;
	ResultSet rs = null;
	
	public AccountDataPane(Connection conn, String Cus_id) {
		super("account data Panel");
		setSize(800, 800);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		// 개인정보 화면 구성 패널 생성
		for(int i=0;i<dataPanel.length;i++) {
			dataPanel[i] = new JPanel();
			dataPanel[i].setBackground(Color.GRAY);
		}
		phonePanel = new JPanel();
		phonePanel.setBackground(Color.GRAY);
		birthPanel = new JPanel();
		birthPanel.setBackground(Color.GRAY);
		// 개인정보 화면의 전체 패널
		// 좌 - 회원정보조회 및 수정
		// 우 - 주문기록 조회 
		mainAccPanel = new JPanel();
		mainAccPanel.setLayout(new GridLayout(1,2));
		mainAccPanel.setPreferredSize(new Dimension(650, 410));
		
		/** 개인정보 패널 구현부
		 * 계정정보 패널위 디자인 설명
		 * 1. 위 계정정보창임을 알려주는 타이틀 
		 * 2. 가운데(좌) 각 정보가 의미하는 태그가 붙는 패널
		 * 3-1. 가운데(우) 각 태그에 해당되는 정보를 출력하는 패널
		 * 3-2. 가운데(우)-(정보수정 모드) 정보수정 버튼을 눌렀을때 전환되고 수정할 정보를 입력할수 있는 창
		 * 4-1. 아래 사용자가 개인정보를 수정하고자 할때 수정 버튼이 붙는 패널
		 * 4-2. 아래-(정보수정 모드) 정보수정 버튼을 눌렀을 때 전환되고 수정 최종확인 버튼과 취소버튼이 붙는 패널
		 */
		
		dataPanel[0].setLayout(new BorderLayout()); // 개인정보 메인 패널
		dataPanel[1].setLayout(new GridLayout(1,2));
		dataPanel[2].setLayout(new GridLayout(9, 1, 5, 5));// 내부 데이터 태그 패널 (좌)	
		dataPanel[3].setLayout(new GridLayout(9, 1, 5, 5));// 내부 상세 데이터 패널 (우)-1	
		dataPanel[4].setLayout(new GridLayout(9, 1, 5, 5));// 내부 상세 데이터 패널 (우)-2
		dataPanel[5].setLayout(new FlowLayout()); // 하부 버튼 패널 - 1
		dataPanel[6].setLayout(new FlowLayout()); // 하부 버튼 패널 - 2
		
		phonePanel.setLayout(new GridLayout(1,3,5,5));
		birthPanel.setLayout(new GridLayout(1,3));
		
		
		// DB로부터 개인정보를 받아와 문자열 배열에 저장
		try {
			stmt = conn.createStatement();
			String sql = "SELECT * FROM CUSTOMER WHERE Cus_id = \'" + Cus_id + "\'";
			rs = stmt.executeQuery(sql);
			while(rs.next()) {
				for(int i=0; i<dataArr.length; i++) {
					dataArr[i] = rs.getString(i+1);
				}
			}
		}catch(SQLException ex) {
			System.out.println("Error : " + ex);
		}
		
		
		// 개인정보 패널에 달릴 부제목 설정
		subTitle[0] = new JLabel("   My Account   ");
		subTitle[0].setFont(new Font("San Serif", Font.PLAIN, 20));
		subTitle[0].setForeground(Color.WHITE);
		dataPanel[0].add(subTitle[0], BorderLayout.NORTH);
		
		// 각 정보에 달릴 태그 설정 
		accountTag[0] = new JLabel("  User ID * ");
		accountTag[1] = new JLabel("  Password * ");
		accountTag[2] = new JLabel("  Address * ");
		accountTag[3] = new JLabel("  Phone number * ");
		accountTag[4] = new JLabel("  First name  ");
		accountTag[5] = new JLabel("  Last name  ");
		accountTag[6] = new JLabel("  Sex  ");
		accountTag[7] = new JLabel("  Birth date  ");
		accountTag[8] = new JLabel("  Job  ");
		
		for(int i=0; i<accountData.length; i++) {
			accountData[i] = new JLabel(dataArr[i]);
		}
		
		// 각 정보가 출력될 조회 패널의 글 라벨 설정
		for(int i=0; i<accountTag.length;i++) {
			accountTag[i].setForeground(Color.WHITE);
			accountTag[i].setFont(new Font("San Serif", Font.PLAIN, 15));
			accountData[i].setForeground(Color.WHITE);
			accountData[i].setFont(new Font("San Serif", Font.PLAIN, 15));
		}

		// 각 정보가 출력될 수정 패널의 글 상자 설정
		immutableIDData = new JLabel(dataArr[0]);
		immutableIDData.setForeground(Color.WHITE);
		immutableIDData.setFont(new Font("San Serif", Font.PLAIN, 15));
		for(int i=0; i<mutableData.length; i++) {
			if(i==1) {
				addComboBox = new JComboBox<String>(selectAddress);
				addComboBox.setSelectedItem(dataArr[i+1]);
			}else if(i==2) {
				mutablePhoneData[0] = new JTextField(dataArr[i+1].substring(0, 3));
				mutablePhoneData[0].setFont(new Font("San Serif", Font.PLAIN, 15));
				mutablePhoneData[1] = new JTextField(dataArr[i+1].substring(4, 8));
				mutablePhoneData[1].setFont(new Font("San Serif", Font.PLAIN, 15));
				mutablePhoneData[2] = new JTextField(dataArr[i+1].substring(9, 13));
				mutablePhoneData[2].setFont(new Font("San Serif", Font.PLAIN, 15));
			}else if(i==5){
				sexComboBox = new JComboBox<String>(selectSex);
				sexComboBox.setSelectedItem(dataArr[i+1]);
			}else if(i==6){
				int temp = 2018;
				for(int j=0; j<100; j++) {
					arrYear[j+1] = String.valueOf(temp);
					temp -= 1;
				}
				temp = 1;
				for(int j=0; j<12; j++) {
					arrMonth[j+1] = String.valueOf(temp);
					temp += 1;
				}
				temp = 1;
				for(int j=0; j<31; j++) {
					arrDate[j+1] = String.valueOf(temp);
					temp += 1;
				}
				yearComboBox = new JComboBox<String>(arrYear);
				yearComboBox.setSelectedItem(dataArr[i+1].substring(0, 4));
				monthComboBox = new JComboBox<String>(arrMonth);
				if(Integer.parseInt(dataArr[i+1].substring(5,7))/10<1) {
					monthComboBox.setSelectedItem(dataArr[i+1].substring(6, 7));
				}else {
					monthComboBox.setSelectedItem(dataArr[i+1].substring(5, 7));
				}
				dateComboBox = new JComboBox<String>(arrDate);
				dateComboBox.setSelectedItem(dataArr[i+1].substring(8, 10));
				
			}else {
				mutableData[i] = new JTextField(dataArr[i+1]);
				mutableData[i].setFont(new Font("San Serif", Font.PLAIN, 15));
			}
		}
				
		// 패널에 글상자 붙이기
		for(int i=0; i<accountTag.length; i++) { // 태그 좌 패널에 붙이기
			dataPanel[2].add(accountTag[i]);
		}
		for(int i=0; i<accountData.length; i++) { // 데이터 우-1 패널에 붙이
			dataPanel[3].add(accountData[i]);
		}
		dataPanel[4].add(immutableIDData);// 데이터 우-2 패널에 붙이기
		for(int i=0; i<mutableData.length; i++) { 
			if(i==1) {
				dataPanel[4].add(addComboBox);
			}else if(i==2){
				phonePanel.add(mutablePhoneData[0]);
				phonePanel.add(mutablePhoneData[1]);
				phonePanel.add(mutablePhoneData[2]);
				dataPanel[4].add(phonePanel);
			}else if(i==5){
				dataPanel[4].add(sexComboBox);
			}else if(i==6){
				birthPanel.add(yearComboBox);
				birthPanel.add(monthComboBox);
				birthPanel.add(dateComboBox);
				dataPanel[4].add(birthPanel);
			}else {
				dataPanel[4].add(mutableData[i]);
			}
		}
		dataPanel[1].add(dataPanel[2]);
		dataPanel[1].add(dataPanel[3]);
		dataPanel[0].add(dataPanel[1], BorderLayout.CENTER);
		
		
		// 개인정보 조회 모드 버튼 패널
		changeBtn[0] = new JButton("Change");
		changeBtn[0].addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				dataPanel[1].removeAll();
				dataPanel[1].add(dataPanel[2]);
				dataPanel[1].add(dataPanel[4]);
				dataPanel[1].revalidate();
				dataPanel[1].repaint();
				
				dataPanel[5].removeAll();
				dataPanel[5].add(dataPanel[7]);
				dataPanel[5].revalidate();
				dataPanel[5].repaint();
			}
			
		});
		
		// 개인정보 수정 모드 버튼 패널
		changeBtn[1] = new JButton("Confirm");	// 확인 버튼
		changeBtn[1].addActionListener(new ActionListener() {

			private String [] temps = new String[mutableData.length];
			private Boolean check = true;

			@Override
			public void actionPerformed(ActionEvent e) {
				for(int i=0;i<mutableData.length;i++) {
					if(i==0) {
						if(mutableData[i].getText().equals("")) {
							System.out.println(i + " field blank");
							check = false;
							break;
						}else {
							temps[i] = mutableData[i].getText();
						}
					}else if(i==1) {
						temps[i] = addComboBox.getSelectedItem().toString();
					}else if(i==2) {
						if(mutablePhoneData[0].getText().equals("")||mutablePhoneData[1].getText().equals("")||mutablePhoneData[2].getText().equals("")){
							System.out.println(i + " field blank");
							check = false;
							break;
						}else {
							temps[i] = mutablePhoneData[0].getText() + "-" + mutablePhoneData[1].getText() + "-" + mutablePhoneData[2].getText();
						}
					}else if(i==5) {
						if(sexComboBox.getSelectedItem().toString().equals("Select")) {
							temps[i] = "";
						}else {
							temps[i] = sexComboBox.getSelectedItem().toString();
						}
					}else if(i==6) {
						if(yearComboBox.getSelectedItem()==null||monthComboBox.getSelectedItem()==null||dateComboBox.getSelectedItem()==null) {
							temps[i] = "";
						}else {
							temps[i] = yearComboBox.getSelectedItem().toString() + "-"
								+ monthComboBox.getSelectedItem().toString() + "-"
								+ dateComboBox.getSelectedItem().toString();
						}
					}else {
						temps[i] = mutableData[i].getText();
					}
					
					System.out.println(i + " : " + temps[i]);
				}
				
				if(check) {
					subFinalCheckWin subCheck = new subFinalCheckWin(conn, Cus_id, temps);
					subCheck.setVisible(true);
				}else {
					subWarnWin subWarn = new subWarnWin();
					subWarn.setVisible(true);
					check=true;
				}
			}
			
		});
		changeBtn[2] = new JButton("Cancel");	// 취소 버튼
		changeBtn[2].addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				dataPanel[1].removeAll();
				dataPanel[1].add(dataPanel[2]);
				dataPanel[1].add(dataPanel[3]);
				dataPanel[1].revalidate();
				dataPanel[1].repaint();
			
				dataPanel[5].removeAll();
				dataPanel[5].add(dataPanel[6]);
				dataPanel[5].revalidate();
				dataPanel[5].repaint();
			}
			
		});
		dataPanel[6].add(changeBtn[0]);
		dataPanel[5].add(dataPanel[6]);
		dataPanel[7].add(changeBtn[1]);
		dataPanel[7].add(changeBtn[2]);
		
		dataPanel[0].add(dataPanel[5], BorderLayout.SOUTH);
		
		mainAccPanel.add(dataPanel[0]);
		
		
		
		//---------------------------------------------------------------------------
		//---------------------------------------------------------------------------
		
		
		
		/**
		 * 주문내역정보 패널 구현부
		 * 고객은 자신의 과거 구매 내역을 확인할 수 있다
		 * ORDERED table 에서 본인의 아이디 정보에 맞는 주문 기록을 받아옴
		 * ORDERED_PRODUCT 해당 주문 기록 번호에 맞는 구매 기록 품목 정보를 받아옴
		 */
		// 주문기록 화면의 전체 패널 
		orderDataPanel[0] = new JPanel();
		orderDataPanel[0].setBackground(Color.GRAY);
		orderDataPanel[0].setLayout(new BorderLayout());
		orderDataPanel[0].setPreferredSize(new Dimension(400,400));
		
		// 개인정보 패널에 달릴 부제목 설정
		subTitle[1] = new JLabel("   My Order History   ");
		subTitle[1].setFont(new Font("San Serif", Font.PLAIN, 20));
		subTitle[1].setForeground(Color.WHITE);
		orderDataPanel[0].add(subTitle[1], BorderLayout.NORTH);
				
		// 주문기록 내부 패널
		orderDataPanel[1] = new JPanel();
		orderDataPanel[1].setBackground(Color.GRAY);
		orderDataPanel[0].add(orderDataPanel[1], BorderLayout.CENTER);
		
		// ORDERED table로부터 회원의 구매 정보를 받아와 테이블에 저장
		model = new DefaultTableModel(list, header) {

			/**
			 * 주문 기록을 DB로 부터 받아 저장, 출력할 테이블 모델 설정
			 */
			private static final long serialVersionUID = 1L;
			public boolean isCellEditable(int rowIndex, int ColIndex) {
				return false;
			}
		};
		orderList = new JTable(model);
		rowSorter = new TableRowSorter<>(orderList.getModel());
		orderList.setRowSorter(rowSorter);
		orderList.setRowHeight(25);
		scrollPane = new JScrollPane(orderList);
		scrollPane.setPreferredSize(new Dimension(400, 300));
		
		//orderList.getColumnModel().getColmn(c).setMinWidth(d);
		
		jtfFilter.getDocument().addDocumentListener(new DocumentListener(){
			
			@Override
			public void insertUpdate(DocumentEvent e) {
				String text = jtfFilter.getText();
				
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
				String text = jtfFilter.getText();
				
				if(text.trim().length() == 0) {
					rowSorter.setRowFilter(null);
				}else {
					rowSorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
				}
				
			}
		});	
		
		try {
			stmt = conn.createStatement();
			String sql = "SELECT Order_num, Ordered_date, Product_name, Product_quantity "
					+ "FROM ORDERED NATURAL JOIN ORDERED_PRODUCT, ITEM "
					+ "WHERE ORDERED_PRODUCT.Product_number = ITEM.Product_number "
					+ "AND Cus_id = \'" + Cus_id + "\'";
			rs = stmt.executeQuery(sql);
			System.out.println("CUSTOMER ID : " + Cus_id);
			DefaultTableModel tempModel = (DefaultTableModel) orderList.getModel();
			while(rs.next()) {
				//String Order_num = rs.getString(1);
				String Ordered_date = rs.getString(2);
				String Product_name = rs.getString(3);
				String Product_quantity = rs.getString(4);
				
				String[] row = {Product_name, Product_quantity, Ordered_date};
				tempModel.addRow(row);
				//System.out.println("Order_number : " + Order_num + "\t Order_date : " + Ordered_date);
			}
		}catch(SQLException ex) {
			System.out.println("Error : " + ex);
		}
		scrollPane.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
		scrollPane.setBackground(Color.GRAY);
		orderDataPanel[0].add(scrollPane, BorderLayout.CENTER);
		jtfFilter.setSize(300, 20);
		orderDataPanel[0].add(jtfFilter, BorderLayout.SOUTH);
		
		
		mainAccPanel.add(orderDataPanel[0]);
	}
	public void resetTable() {
		DefaultTableModel dm = (DefaultTableModel) orderList.getModel();
		int rowCount = dm.getRowCount();
		for (int i = rowCount - 1; i >= 0; i--) {
		    dm.removeRow(i);
		}
	}
	
	public void refreshAll(Connection conn, String Cus_id) {
		resetTable();
		try {
			stmt = conn.createStatement();
			String sql = "SELECT Order_num, Ordered_date, Product_name, Product_quantity "
					+ "FROM ORDERED NATURAL JOIN ORDERED_PRODUCT, ITEM "
					+ "WHERE ORDERED_PRODUCT.Product_number = ITEM.Product_number "
					+ "AND Cus_id = \'" + Cus_id + "\'";
			rs = stmt.executeQuery(sql);
			System.out.println("CUSTOMER ID : " + Cus_id);
			DefaultTableModel tempModel = (DefaultTableModel) orderList.getModel();
			while(rs.next()) {
				//String Order_num = rs.getString(1);
				String Ordered_date = rs.getString(2);
				String Product_name = rs.getString(3);
				String Product_quantity = rs.getString(4);
				
				String[] row = {Product_name, Product_quantity, Ordered_date};
				tempModel.addRow(row);
				//System.out.println("Order_number : " + Order_num + "\t Order_date : " + Ordered_date);
			}
		}catch(SQLException ex) {
			System.out.println("Error : " + ex);
		}
	}
	
	public class subFinalCheckWin extends JFrame{

		/**
		 * 회원정보 수정 버튼 클릭시 최종 확인 창 구현
		 * 확인을 누르면 완료 되었음을 알리는 창과 함께 정보 수정
		 */
		private static final long serialVersionUID = 1L;
		
		private JPanel strPane;
		private JPanel btnPane;
		private JLabel[] finalCheckStr = new JLabel[2];
		private JButton[] finalCheckBtn = new JButton[2];
		
		Statement stmt = null;
		ResultSet rs = null;
		
		public subFinalCheckWin(Connection conn, String Cus_id, String[] changeData) {
			
			// 기본 창 배경환경 설정
			super("Final Check Window");
			setSize(300, 120);
			Container subcon = this.getContentPane();
			subcon.setBackground(Color.GRAY);
			subcon.setLayout(new BorderLayout());
			
			//문장 패널 구현부
			//계정정보 변경 확인 문장 설정
			strPane = new JPanel();
			strPane.setLayout(new BoxLayout(strPane, BoxLayout.Y_AXIS));
			
			finalCheckStr[0] = new JLabel("Do you want to change");
			finalCheckStr[1] = new JLabel("you account information?");
			finalCheckStr[0].setFont(new Font("San Serif", Font.PLAIN, 20));
			finalCheckStr[1].setFont(new Font("San Serif", Font.PLAIN, 20));
			finalCheckStr[0].setAlignmentX(Component.CENTER_ALIGNMENT);
			finalCheckStr[1].setAlignmentX(Component.CENTER_ALIGNMENT);
			
			strPane.add(finalCheckStr[0]);
			strPane.add(finalCheckStr[1]);
			
			subcon.add(strPane);
			
			// 버튼 패널 구현부
			// 확인 버튼 구현부
			btnPane = new JPanel();
			btnPane.setLayout(new FlowLayout());

			finalCheckBtn[0] = new JButton("OK");
			finalCheckBtn[0].setSize(30, 20);
			finalCheckBtn[0].addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub
					try { // 수정된 개인정보에 따라 update query 문 작성
						stmt = conn.createStatement();
						for(int i=0; i<changeData.length;i++) {
							System.out.println(changeData[i]);
						}
						
						Boolean check = false;
						String sql = "UPDATE CUSTOMER SET ";
						if(!changeData[0].equals("")) {
							sql = sql +"Cus_password = \'" + changeData[0] +"\' ";
							check = true;
						}
						
						if(!changeData[1].equals("")) {
							if(check) {
								sql = sql +", ";
							}
							sql = sql +"Address =\'" + changeData[1] + "\' ";
							check = true;
						}
						
						if(!changeData[2].equals("")) {
							if(check) {
								sql = sql +", ";
							}
							sql = sql +"Phone_num =\'" + changeData[2] + "\' ";
							check = true;
						}
						
						if(!changeData[3].equals("")) {
							if(check) {
								sql = sql +", ";
							}
							sql = sql +"First_name =\'" + changeData[3] + "\' ";
							check = true;
						}else {
							if(check) {
								sql = sql +", ";
							}
							sql = sql +"First_name = null ";
							check = true;
						}
						
						if(!changeData[4].equals("")) {
							if(check) {
								sql = sql +", ";
							}
							sql = sql +"Last_name =\'" + changeData[4] + "\' ";
							check = true;
						}else {
							if(check) {
								sql = sql +", ";
							}
							sql = sql +"Last_name = null ";
							check = true;
						}
						
						if(!changeData[5].equals("")) {
							if(check) {
								sql = sql +", ";
							}
							sql = sql +"Sex =\'" + changeData[5] + "\' ";
							check = true;
						}else {
							if(check) {
								sql = sql +", ";
							}
							sql = sql +"Sex = null ";
							check = true;
						}
						
						if(!changeData[6].equals("")) {
							if(check) {
								sql = sql +", ";
							}
							sql = sql +"Birthdate =\'" + changeData[6] + "\' ";
							check = true;
						}else {
							if(check) {
								sql = sql +", ";
							}
							sql = sql +"Birthdate = null ";
							check = true;
						}
						
						if(!changeData[7].equals("")) {
							if(check) {
								sql = sql +", ";
							}
							sql = sql +"Job =\'" + changeData[7] + "\' ";
						}else {
							if(check) {
								sql = sql +", ";
							}
							sql = sql +"Job = null ";
						}
						
						sql = sql +"WHERE Cus_id = \'" + Cus_id + "\'";
						
						int count = stmt.executeUpdate(sql);
						System.out.println("Update "+ count +" rows.");
						
						
					}catch(SQLException ex) {
						System.out.println("Query. UPDATE Error : " + ex);
					}
					
					try {//수정된 정보로 회원정보조회 화면 갱신
						stmt = conn.createStatement();
						
						String sql = "Select * FROM CUSTOMER "
								+ "WHERE Cus_id = \'" + Cus_id + "\'";
										
						rs = stmt.executeQuery(sql);
						
						while(rs.next()) {
							dataPanel[3].removeAll();
							dataPanel[3].add(accountData[0]);
							for(int i=1; i<accountData.length;i++) {
								accountData[i] = new JLabel(rs.getString(i+1));
								accountData[i].setForeground(Color.WHITE);
								accountData[i].setFont(new Font("San serif", Font.PLAIN, 15));
								dataPanel[3].add(accountData[i]);
							}
							dataPanel[3].revalidate();
							dataPanel[3].repaint();
						}
					}catch(SQLException ex) {
						System.out.println("SELECT Error : " + ex);
					}
					
					dataPanel[1].removeAll();
					dataPanel[1].add(dataPanel[2]);
					dataPanel[1].add(dataPanel[3]);
					dataPanel[1].revalidate();
					dataPanel[1].repaint();
				
					dataPanel[5].removeAll();
					dataPanel[5].add(dataPanel[6]);
					dataPanel[5].revalidate();
					dataPanel[5].repaint();
					dispose();
				}
				
			});
			btnPane.add(finalCheckBtn[0]);
			
			// 취소 버튼 구현부
			finalCheckBtn[1] = new JButton("Cancel");
			finalCheckBtn[1].setSize(30, 20);
			finalCheckBtn[1].addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub
					dispose();
				}
				
			});
			btnPane.add(finalCheckBtn[1]);
			subcon.add(btnPane, BorderLayout.SOUTH);
			
			
		}
	}
	
	public class subWarnWin extends JFrame{
		/**
		 * 필수 입력칸이 공백일 경우에 출력되는 경고창
		 */
		private static final long serialVersionUID = 1L;
		
		private JPanel warnPanel;
		private JPanel warnBtnPanel;
		private JLabel[] warnStr = new JLabel[2];
		private JButton warnBtn;
		public subWarnWin() {
			super("Warning Window");
			setSize(300, 120);
			Container subcon = this.getContentPane();
			subcon.setBackground(Color.GRAY);
			subcon.setLayout(new BorderLayout());
			
			warnPanel = new JPanel();
			warnPanel.setLayout(new BoxLayout(warnPanel, BoxLayout.Y_AXIS));
			warnBtnPanel = new JPanel();
			warnBtnPanel.setLayout(new FlowLayout());
			
			warnStr[0] = new JLabel("You must fill in");
			warnStr[1] = new JLabel("the Required Field");
			warnStr[0].setFont(new Font("San Serif", Font.PLAIN, 20));
			warnStr[1].setFont(new Font("San Serif", Font.PLAIN, 20));
			warnStr[0].setAlignmentX(Component.CENTER_ALIGNMENT);
			warnStr[1].setAlignmentX(Component.CENTER_ALIGNMENT);
			
			warnPanel.add(warnStr[0]);
			warnPanel.add(warnStr[1]);
			subcon.add(warnPanel, BorderLayout.CENTER);
			
			warnBtn = new JButton("OK");
			warnBtn.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub
					dispose();
				}
				
			});
			warnBtnPanel.add(warnBtn);
			subcon.add(warnBtnPanel, BorderLayout.SOUTH);
			
			
		}
	}
}
