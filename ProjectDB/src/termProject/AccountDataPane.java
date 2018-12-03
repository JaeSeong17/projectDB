package termProject;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.sql.*;
import javax.swing.*;

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
	
	protected JPanel[] dataPanel = new JPanel[4];		// 3개로 구성된 개인정보 창 (1. 개인정보창 큰 바탕, 2. 개인정보, 3. 내부 데이터, 4. 주문내역) 
	
	private JLabel[] subTitle = new JLabel[2];		// 각 패널에 달릴 부제 설정
	private JLabel[] accountTag = new JLabel[9];	// 개인정보 표시부 각 데이터 태그 설정
	protected JLabel immutableIDData;					// 해당 ID 표시 (수정 불가능)
	protected JTextField[] mutableData = new HintTextField[8];	// 각 태그에 해당되는 수정가능한 개인정보 표시 (직접입력 수정)
	
	private String[] dataArr = new String[9];	// DB로부터 해당 회원정보를 받아와서 저장할 문자열 배열
	
	private JButton changeBtn;
	
	Statement stmt = null;
	ResultSet rs = null;
	
	public AccountDataPane(Connection conn, String Cus_id) {
		super("account data Panel");
		setSize(1000, 800);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		for(int i=0;i<dataPanel.length;i++) {
			dataPanel[i] = new JPanel();
		}
		dataPanel[0].setLayout(new GridLayout(1,2));
			
		// 개인정보 패널 구현부
		dataPanel[1].setBackground(Color.GRAY);		// 개인정보 패널
		dataPanel[1].setLayout(new BorderLayout());
		dataPanel[2].setBackground(Color.GRAY);		// 내부 상세 데이터 패널
		dataPanel[2].setLayout(new GridLayout(9, 2, 5, 5));
		
		
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
		subTitle[0] = new JLabel("My Account");
		subTitle[0].setFont(new Font("San Serif", Font.PLAIN, 20));
		subTitle[0].setForeground(Color.WHITE);
		dataPanel[1].add(subTitle[0], BorderLayout.NORTH);
		
		// 각 정보에 달릴 태그 설정 
		accountTag[0] = new JLabel("  User ID  ");
		accountTag[1] = new JLabel("  Password  ");
		accountTag[2] = new JLabel("  Address  ");
		accountTag[3] = new JLabel("  Phone number  ");
		accountTag[4] = new JLabel("  First name  ");
		accountTag[5] = new JLabel("  Last name  ");
		accountTag[6] = new JLabel("  Sex  ");
		accountTag[7] = new JLabel("  Birth date  ");
		accountTag[8] = new JLabel("  Job  ");
		
		for(int i=0; i<accountTag.length; i++) {
			accountTag[i].setForeground(Color.WHITE);
			accountTag[i].setFont(new Font("San Serif", Font.PLAIN, 15));
		}

		// 각 정보가 출력될 글 상자 설정
		immutableIDData = new JLabel(dataArr[0]);
		immutableIDData.setForeground(Color.WHITE);
		immutableIDData.setFont(new Font("San Serif", Font.PLAIN, 15));
		for(int i=0; i<mutableData.length; i++) {
			mutableData[i] = new HintTextField(dataArr[i+1]);
			mutableData[i].setFont(new Font("San Serif", Font.PLAIN, 15));
			mutableData[i] = new HintTextField(dataArr[i+1]);
		}
				
		// 패널에 글상자 붙이기
		for(int i=0; i<accountTag.length; i++) {
			dataPanel[2].add(accountTag[i]);
			if(i==0) {
				dataPanel[2].add(immutableIDData);
			}else {
				dataPanel[2].add(mutableData[i-1]);
				mutableData[i-1].setFocusable(false);
				mutableData[i-1].setFocusable(true);
			}
		}
		dataPanel[1].add(dataPanel[2], BorderLayout.CENTER);
		
		
		// 개인정보 수정 버튼 
		changeBtn = new JButton("Change");
		changeBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				subFinalCheckWin subCheck = new subFinalCheckWin(conn, dataArr);
				subCheck.setVisible(true);
			}
			
		});
		dataPanel[1].add(changeBtn, BorderLayout.SOUTH);
		
		dataPanel[0].add(dataPanel[1], 0, 0);
		//------------------------------------------------------------
		
		// 주문내역정보 패널 구현부
		dataPanel[3].setBackground(Color.GRAY);		// 주문내역 패널
		dataPanel[3].setLayout(null);
		
		
		dataPanel[0].add(dataPanel[3], 0, 1);
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
		
		public subFinalCheckWin(Connection conn, String[] accountData) {
			
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
					try {
						stmt = conn.createStatement();
						
						String sql = "";
						
						rs = stmt.executeQuery(sql);
						
						while(rs.next()) {
							
						}
					}catch(SQLException ex) {
						System.out.println("Error : " + ex);
					}
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
}
