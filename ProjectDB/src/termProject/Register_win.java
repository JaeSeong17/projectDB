package termProject;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import javax.swing.*;

public class Register_win extends JFrame{ 
	/**
	* 회원가입 창 구현부
	* 1. 아이디 중복체크
	* 2. 비밀번호, 주소, 전화번호, 이름, 성별, 생년월일
	* 
	* 해당 정보 모두 입력시 DB로 쿼리 전달 " INSERT INTO CUSTOMER VALUES(...)"
 	*/
	private static final long serialVersionUID = 1L;
	
	Statement stmt = null;
	PreparedStatement pstmt = null;
	ResultSet rs = null;
	
	//회원가입의 태그와 텍스트필드 선언
	private JLabel[] regTag = new JLabel[9]; 
	private JLabel[] idCheckStr = new JLabel[3];
	private JTextField[] regData = new JTextField[9];
	private JTextField[] regPhoneNumber = new JTextField[3];
	
	//아이디 중복체크 버튼
	private JButton checkBtn;
	//아이디 체크 변수
	private Boolean checkDuplication = false;
	
	//지역 선택상자 선언
	private String selectAddress[] = {"Select", "Seoul", "Busan", "Ulsan", "Daejeon",
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
	
	//필수 입력란 작성 요구 주의문
	private JLabel[] noticeStr = new JLabel[6];
	
	//가입완료 버튼
	private JButton registerBtn;

	public Boolean checkReq = true;


	//구현부
	public Register_win(Connection conn) {
		super("Register Window");
		setSize(360, 550);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		Container container = this.getContentPane();
		container.setBackground(Color.GRAY);
		container.setLayout(null);
		
		//회원가입 - 제목 라벨
		JLabel regTitle = new JLabel("Register New Account");
		regTitle.setForeground(Color.WHITE);
		regTitle.setFont(new Font("SanSerif", Font.ITALIC, 20));
		
		container.add(regTitle);
		regTitle.setBounds(65, 20, 250, 30);
		
		//회원가입 0 - 아이디 입력
		regTag[0] = new JLabel("* User ID");

		//회원가입 - 아이디 중복체크 확인 문장 출력부
		idCheckStr[0] = new JLabel("Need input");
		idCheckStr[0].setFont(new Font("San Serif", Font.PLAIN, 10));
		idCheckStr[0].setForeground(Color.RED);
		
		idCheckStr[1] = new JLabel("Accepted");
		idCheckStr[1].setFont(new Font("San Serif", Font.PLAIN, 10));
		idCheckStr[1].setForeground(Color.GREEN);
		
		idCheckStr[2] = new JLabel("Already Existed ID");
		idCheckStr[2].setFont(new Font("San Serif", Font.PLAIN, 10));
		idCheckStr[2].setForeground(Color.RED);
		
		for(int i=0; i<idCheckStr.length; i++) {
			container.add(idCheckStr[i]);
			idCheckStr[i].setBounds(140, 80, 100, 15);
			idCheckStr[i].setVisible(false);

		}

		
		//회원가입 - 아이디 중복체크 버튼
		checkBtn = new JButton("check");
		checkBtn.setFont(new Font("SanSerif", Font.PLAIN, 10));
		checkBtn.setForeground(Color.GRAY);
		checkBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				
				try {
					// 먼저 입력란이 비어있는지 아닌지 체크
					if(regData[0].getText().equals("")) {
						checkDuplication = false;
						idCheckStr[0].setVisible(true);
						idCheckStr[1].setVisible(false);
						idCheckStr[2].setVisible(false);

					}else {
						// 아이디 중복체크 쿼리
						stmt = conn.createStatement();
							String createID = regData[0].getText();
						String sql = "SELECT Cus_id FROM CUSTOMER WHERE Cus_id = \'" + createID + "\'";
						rs = stmt.executeQuery(sql);
					
						// 중복되는 아이디 없을경우 accept 출력
						if(rs.isBeforeFirst()==false) {
							checkDuplication = true;
							idCheckStr[0].setVisible(false);
							idCheckStr[1].setVisible(true);
							idCheckStr[2].setVisible(false);
						
						}else {// 중복되는 아이디 있을경우 reject 출력
							while(rs.next()) {
								checkDuplication = false;
								idCheckStr[0].setVisible(false);
								idCheckStr[1].setVisible(false);		
								idCheckStr[2].setVisible(true);	
								String Cus_id = rs.getString(1);
								System.out.println("CUSTOMER ID already existed - " + Cus_id);
							}
						}
					}
					
				}catch(SQLException ex) {
					System.out.println("Error : " + ex);
				}
			}
			
		});
		
		container.add(checkBtn);
		checkBtn.setVisible(true);
		checkBtn.setBounds(280, 60, 50, 20);
		
		
		//회원가입 1 - 비밀번호 입력
		regTag[1] = new JLabel("* Password");
		
		//회원가입 2 - 주소
		regTag[2] = new JLabel("* Address");
		
		addComboBox = new JComboBox<String>(selectAddress);
		
		//회원가입 3 - 전화번호
		regTag[3] = new JLabel("* Phone");
		
		//회원가입 4 - 이름
		regTag[4] = new JLabel("First name");
		regTag[5] = new JLabel("Last name");
		
		//회원가입 5 - 성별
		regTag[6] = new JLabel("Sex");
		
		sexComboBox = new JComboBox<String>(selectSex);
		
		//회원가입 6 - 생년월일
		regTag[7] = new JLabel("Birth Date");
		
		arrYear[0] = "Year";
		arrMonth[0] = "M";
		arrDate[0] = "D";
		int temp = 2018;
		for(int i=0; i<100; i++) {
			arrYear[i+1] = String.valueOf(temp);
			temp -= 1;
		}
		temp = 1;
		for(int i=0; i<12; i++) {
			arrMonth[i+1] = String.valueOf(temp);
			temp += 1;
		}
		temp = 1;
		for(int i=0; i<31; i++) {
			arrDate[i+1] = String.valueOf(temp);
			temp += 1;
		}
		yearComboBox = new JComboBox<String>(arrYear);
		monthComboBox = new JComboBox<String>(arrMonth);
		dateComboBox = new JComboBox<String>(arrDate);
		
		//회원가입 7 - 직업
		regTag[8] = new JLabel("Job");
		
		//테그폰트 설정과 텍스트 박스 선언
		for(int i=0; i<regTag.length; i++) {
			regTag[i].setFont(new Font("San Serif", Font.PLAIN, 15));
			regTag[i].setForeground(Color.WHITE);
			regData[i] = new JTextField("");
			regData[i].setFont(new Font("San Serif", Font.PLAIN, 15));
		}
		for(int i=0; i<regPhoneNumber.length; i++) {
			regPhoneNumber[i] = new JTextField("");
			regPhoneNumber[i].setFont(new Font("San Serif", Font.PLAIN, 15));
		}
		
		//프레임에 태그와 텍스트박스 붙이기
		for(int i=0; i<regTag.length; i++) {
			regData[i] = new JTextField("");
			regData[i].setFont(new Font("San Serif", Font.PLAIN, 15));
			container.add(regTag[i]);
			regTag[i].setBounds(30, 60 + (i*40), 80, 20);
			if(i==2) { // 주소 선택 박스 설정 
				container.add(addComboBox);
				addComboBox.setBounds(125, 60 + (i*40), 150, 20);
			}else if(i==3) { // 전화번호 입력란 글상자 3칸으로 설
				for(int j=0; j<regPhoneNumber.length; j++) {
					container.add(regPhoneNumber[j]);
					regPhoneNumber[j].setBounds(130 + (50*j), 60 + (i*40), 50, 20);
				}
			}else if(i==6) { // 성별 선택 박스 설정
				container.add(sexComboBox);
				sexComboBox.setBounds(125, 60 + (i*40), 100, 20);
			}else if(i==7) { // 생년월일 선택 박스 설정 
				container.add(yearComboBox);
				container.add(monthComboBox);
				container.add(dateComboBox);
				yearComboBox.setBounds(125, 60 + (i*40), 85, 20);
				monthComboBox.setBounds(205, 60 + (i*40), 70, 20);
				dateComboBox.setBounds(270, 60 + (i*40), 70, 20);
			}else {
				container.add(regData[i]);
				regData[i].setBounds(130, 60 + (i*40), 150, 20);
			}
		}
		
		// * 표시란 필수 작성 주의문
		noticeStr[0] = new JLabel("User ID is required");
		noticeStr[1] = new JLabel("Password is required");
		noticeStr[2] = new JLabel("Address is required");
		noticeStr[3] = new JLabel("Phone is required");
		noticeStr[4] = new JLabel("* marked fields are required");
		noticeStr[5] = new JLabel("Confim ID duplication check is required");
		
		for(int i=0;i<noticeStr.length-1;i++) {
			noticeStr[i].setFont(new Font("San Serif", Font.PLAIN, 10));
			container.add(noticeStr[i]);
			if(i==4) {
				noticeStr[i].setForeground(Color.WHITE);
				noticeStr[i].setBounds(30,410, 200, 10);

			}else {
				noticeStr[i].setForeground(Color.RED);
				noticeStr[i].setBounds(30, 80+(i*40), 150, 15);
				noticeStr[i].setVisible(false);
			}
		}
		noticeStr[5].setForeground(Color.RED);
		noticeStr[5].setBounds(30, 420, 150, 10);
		noticeStr[5].setVisible(false);
		
		// 가입 완료 버튼
		registerBtn = new JButton("Register");
		registerBtn.setFont(new Font("San Serif", Font.PLAIN, 20));
		registerBtn.setForeground(Color.GRAY);
		registerBtn.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				//필수 입력란에 빈칸이 있는지 체크
				for(int k=0; k<noticeStr.length-2;k++) {
					if(k==2) {
						if(addComboBox.getSelectedItem().equals("Select")){
							noticeStr[k].setVisible(true);
							checkReq = false;
						}else {
							noticeStr[k].setVisible(false);
						}
						continue;
					}else if(k==3) {
						for(int l=0; l<regPhoneNumber.length; l++) {
							if(regPhoneNumber[l].getText().equals("")){
								noticeStr[k].setVisible(true);
								checkReq = false;
								break;
							}else {
								noticeStr[k].setVisible(false);
							}
						}
						continue;
					}
					
					if(regData[k].getText().equals("")) {
						noticeStr[k].setVisible(true);
						checkReq = false;
					}else {
						noticeStr[k].setVisible(false);
					}
				}
				if(checkDuplication == false) {
					noticeStr[noticeStr.length-1].setVisible(true);
					checkReq = false;
				}else {
					noticeStr[noticeStr.length-1].setVisible(false);
				}
				
				//모든 필수 입력란이 채워졌다면 쿼리문 진행
				if(checkReq == false) {
					checkReq = true;
				}else {
					try {
						String sql = "INSERT INTO CUSTOMER VALUES(?,?,?,?,?,?,?,?,?)";
						pstmt = conn.prepareStatement(sql);
						for(int k=0;k<2;k++) {
							pstmt.setString(k+1, regData[k].getText());
						}
						pstmt.setString(3, addComboBox.getSelectedItem().toString());
						pstmt.setString(4, regPhoneNumber[0].getText() + "-" + regPhoneNumber[1].getText() + "-" + regPhoneNumber[2].getText());
						
						if(regData[4].getText().equals("")) {
							pstmt.setString(5, null);
						}else {
							pstmt.setString(5, regData[4].getText());
						}
						if(regData[5].getText().equals("")) {
							pstmt.setString(6, null);
						}else {
							pstmt.setString(6, regData[5].getText());
						}
						if(sexComboBox.getSelectedItem().equals("Select")) {
							pstmt.setString(7, null);
						}else {
							pstmt.setString(7, sexComboBox.getSelectedItem().toString());
						}
						if(!yearComboBox.getSelectedItem().equals("Year")&&!monthComboBox.getSelectedItem().equals("M")&&!dateComboBox.getSelectedItem().equals("D")) {
							pstmt.setString(8, yearComboBox.getSelectedItem().toString() + "-" + monthComboBox.getSelectedItem().toString() + "-" + dateComboBox.getSelectedItem().toString() +"\', ");
						}else {
							pstmt.setString(8, null);
						}
						if(regData[8].getText().equals("")) {
							pstmt.setString(9, null);
						}else {
							pstmt.setString(9, regData[8].getText());
						}
						pstmt.executeUpdate();
						
						System.out.println("CUSTOMER 테이블에 새로운 레코드를 추가했습니다.");
						SubSuccessWin suc = new SubSuccessWin();
						suc.setVisible(true);
						dispose();
						
					}catch(SQLException ex) {
						System.out.println("Error : " + ex);
						System.out.println("CUSTOMER 테이블에 새로운 레코드 추가에 실패했습니다.");
					}finally {
						if(pstmt != null) try{pstmt.close();}catch(SQLException sqle){}  
					}
				}
			}
			
		});
		container.add(registerBtn);
		registerBtn.setBounds(35, 450, 290, 40);
		
		
		
	}
	
	public class SubSuccessWin extends JFrame{

		/**
		 * 가입이 완료되었음을 알리는 하위 창 
		 * 성공적으로 가입이 되었을 떄 "가입이 완료되었습니다"를 출력하고 회원가입창 종료
		 */
		private static final long serialVersionUID = 1L;
		
		private JLabel checkStr;
		private JButton checkBtn;
		
		public SubSuccessWin() {
			super("Register Success");
			setSize(350, 100);
			setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			Container con = this.getContentPane();
			con.setBackground(Color.GRAY);
			con.setLayout(new BorderLayout());
			
			checkStr = new JLabel("Registeration has been successfully completed");
			checkStr.setFont(new Font("San Serif", Font.PLAIN, 15));
			checkStr.setForeground(Color.WHITE);
			con.add(checkStr, BorderLayout.CENTER);
			
			checkBtn = new JButton("Confirm");
			checkBtn.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub
					dispose();
				}
				
			});
			con.add(checkBtn, BorderLayout.SOUTH);
		}
	}

}
