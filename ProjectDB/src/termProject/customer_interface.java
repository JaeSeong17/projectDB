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

	public customer_interface(Connection conn) {
		
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
		customer_id.setText("Welcome " + "ID" + "!");
		date_info.setFont(new Font("Serif", Font.BOLD, 20));
		
		//기본탭 생성
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
				
				sql = "SELECT Minor_tag FROM MINOR_CATEGORY WHERE Major_number = " + major_category_number[i];
				
				rs = stmt.executeQuery(sql);
				
				String[] minor_category_info = new String[10];
				int num_of_minor = 0;
				
				while(rs.next()) {
					
					minor_category_info[num_of_minor] = rs.getString(1);
					
					num_of_minor++;
					
				}

				for(int j=0;j<num_of_minor;j++) {
				
					minor_panel[j] = new JPanel();
					minor_category[i].addTab(minor_category_info[j], minor_panel[j]);
					
//					for(int k=0;k<(category별 item갯수);k++) {
//					
//						//minor_panel에 출력할 정보 만들
//
//					}
					
				}

			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//----------------------------------------------------------------
		
		selectPanel.addTab("SHOPPINTBAG", shoppingbag);
		//SHOPPINGBAG탭 디자인
		
		//----------------------------------------------------------------
		
		selectPanel.addTab("USER_INFO", user_info);
		
		user_info.setLayout(new GridLayout(4, 0));
		JLabel Name_Label = new JLabel("Name : "/*쿼리로 불러온 lastname + firstname*/);
		JLabel ID_Label  = new JLabel("ID : " + ""/*쿼리로 불러온 ID 정보*/);	
		JLabel password_Label = new JLabel("PASSWORD : ");
		JTextField password_modify = new JTextField(/*쿼리로 불러온 password정보*/); // password_Label 뒤에 붙임
		JLabel Address_Label = new JLabel("Address : ");
		JTextField Address_modify = new JTextField(/*쿼리로 불러온 address정보*/); // address_Label 뒤에 붙임
		//address 나중에 예외처리 해주기
		JLabel Phonenum_Label = new JLabel("Address : ");
		JTextField Phonenum_modify = new JTextField(/*쿼리로 불러온 Phone_num정보*/); // Phonenum_Label 뒤에 붙
		
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
	}

}
