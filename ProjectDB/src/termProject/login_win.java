package termProject;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.sql.*;
import javax.swing.*;

public class login_win extends JFrame{
	/**
	 * 	log_in window
	 *  -check user/administrator
	 *  -if there are not exist user data, send wrong data message
	 */
	private static final long serialVersionUID = 1L;
	
	Statement stmt = null;
	PreparedStatement pstmt = null;
	ResultSet rs = null;
	
	public login_win(Connection conn) {
		super("login_window");
		setSize(260, 350);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Container container = this.getContentPane();
		container.setBackground(Color.GRAY);
		container.setLayout(null);
		
		JLabel title = new JLabel("Welcome Market");
		title.setForeground(Color.WHITE);
		title.setFont(new Font("SanSerif", Font.ITALIC, 25));
		JLabel sub_title = new JLabel("Please Log In");
		sub_title.setFont(new Font("SanSerif", Font.ITALIC, 15));
		sub_title.setForeground(Color.WHITE);
		JLabel failMessage = new JLabel("Fail to login. Check your login data.");
		failMessage.setFont(new Font("SanSerif", Font.PLAIN, 10));
		failMessage.setForeground(Color.RED);
		
		
		JTextField input_id = new HintTextField("USER ID");
		input_id.setFont(new Font("San Serif", Font.PLAIN, 15));

		JTextField input_pass = new HintTextField("PASSWORD");
		input_pass.setFont(new Font("San Serif", Font.PLAIN, 15));
		
		
		//로그인 버튼 구현
		JButton confirmBtn = new JButton("Log In");
		confirmBtn.setFont(new Font("San Serif", Font.PLAIN, 15));
		confirmBtn.setForeground(new Color(70,70,70));
		
		confirmBtn.addActionListener(new ActionListener() {
			/**
			 * 로그인 버튼을 누를 때 마다 DB 연결후 유저정보 확인
			 * 일치하는 정보가 있을경우 정상적 로그인
			 * 일치하는 정보가 없을경우 정보없음 텍스트 출력 
			 */
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				
				try { 
					// 3. create object to operate query (쿼리 수행을 위한 객체 생성)
					stmt = conn.createStatement();
					
					// 4. write SQL query
					String temp_id = input_id.getText(); //JTextField로부터 아이디입력값 전달
					String temp_pass = input_pass.getText();//JTextField로부터 비밀번호 입력값 전달
					String sql1 = "SELECT * FROM ADMIN";
					String sql2 = "SELECT First_name, Last_name FROM CUSTOMER WHERE Cus_id = \'" + temp_id
							+ "\' AND Cus_password = \'" + temp_pass + "\'";
					
					// 5.operate query -> admin 계정인지 체크
					rs = stmt.executeQuery(sql1);
					
					// 6. print result of execution
					if(rs.isBeforeFirst()==true) {
						while(rs.next()) {
							String adminID = rs.getString(1);
							String adminPASS = rs.getString(2);
						
							System.out.println(adminID + " - " + adminPASS);
							dispose();
							
							admin_interface ad_inter = new admin_interface(conn);
							ad_inter.setVisible(true);
						}
					}
					
					// 5. operate query -> 일반 회원인지 체크
					rs = stmt.executeQuery(sql2);
					
					// 6. print result of execution
					//로그인 정보 불일치시
					if(rs.isBeforeFirst()==false) {
						container.add(failMessage);
						failMessage.setBounds(33, 200, 200, 20);
					}else {//로그인 정보 일치
						while(rs.next()) {
							String Fname = rs.getString(1);
							String Lname = rs.getString(2);
						
							System.out.println("Login successed : " +  Fname + " - " + Lname);
							dispose();
							
							customer_interface cus_inter = new customer_interface(conn);
							cus_inter.setVisible(true);
						}
					}
					
				}catch(SQLException ex) {
					System.out.println("Error : " + ex);
				}
			}
		});
		
		
		//회원가입 버튼 구현부
		JButton newAccBtn = new JButton("Register new account");
		newAccBtn.setFont(new Font("San Serif", Font.PLAIN, 15));
		newAccBtn.setForeground(Color.GRAY);

		newAccBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				SubRegisterFrame srf = new SubRegisterFrame(conn);
				srf.setVisible(true);
			}
			
		});

		
		
		container.add(title);
		title.setBounds(30, 20, 200, 30);
		container.add(sub_title);
		sub_title.setBounds(80, 50, 100, 20);
		
		container.add(input_id);
		input_id.setBounds(30, 100, 200, 50);
		
		container.add(input_pass);
		input_pass.setBounds(30, 150, 200, 50);
		
		container.add(confirmBtn);
		confirmBtn.setBounds(30, 220, 200, 50);
		
		container.add(newAccBtn);
		newAccBtn.setBounds(30, 270, 200, 40);


	}
	
	

	public class SubRegisterFrame extends JFrame{ 
		/**
		* 회원가입 창 구현부
		* 해당 정보 모두 입력시 DB로 쿼리 전달 " INSERT INTO CUSTOMER VALUES(...)"
	 	*/
		private static final long serialVersionUID = 1L;
		
		//회원가입의 태그와 텍스트필드 선언
		private JLabel[] regTag = new JLabel[9]; 
		private JLabel[] idCheckStr = new JLabel[3];
		private JTextField[] regData = new JTextField[9];
		
		//아이디 중복체크 버튼
		private JButton checkBtn;
		//아이디 체크 변수
		private Boolean checkDuplication = false;
		
		//성별 선택상자 선언
		private String selectSex[] = {"Select", "M", "W"};
		private JComboBox<String> sexComboBox;
		
		//생년월일 선택상자 선언
		private Integer[] integerYear = new Integer[100];
		private Integer[] integerMonth = new Integer[12];
		private Integer[] integerDate = new Integer[31];
		private JComboBox<Integer> yearComboBox;
		private JComboBox<Integer> monthComboBox;
		private JComboBox<Integer> dateComboBox;
		
		//필수 입력란 작성 요구 주의문
		private JLabel[] noticeStr = new JLabel[6];
		
		//가입완료 버튼
		private JButton registerBtn;

		public Boolean checkReq = true;


		//구현부
		public SubRegisterFrame(Connection conn) {
			super("Register Window");
			setSize(360, 550);
			setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			Container subcon = this.getContentPane();
			subcon.setBackground(Color.GRAY);
			subcon.setLayout(null);
			
			//회원가입 - 제목 라벨
			JLabel regTitle = new JLabel("Register New Account");
			regTitle.setForeground(Color.WHITE);
			regTitle.setFont(new Font("SanSerif", Font.ITALIC, 20));
			
			subcon.add(regTitle);
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
				subcon.add(idCheckStr[i]);
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
			
			subcon.add(checkBtn);
			checkBtn.setVisible(true);
			checkBtn.setBounds(280, 60, 50, 20);
			
			
			//회원가입 1 - 비밀번호 입력
			regTag[1] = new JLabel("* Password");
			
			//회원가입 2 - 주소
			regTag[2] = new JLabel("* Address");
			
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
			
			for(int i=0; i<100; i++) {
				integerYear[i] = 2018 - i;
			}
			for(int i=0; i<12; i++) {
				integerMonth[i] = 1 + i;
			}
			for(int i=0; i<31; i++) {
				integerDate[i] = 1 + i;
			}
			yearComboBox = new JComboBox<Integer>(integerYear);
			monthComboBox = new JComboBox<Integer>(integerMonth);
			dateComboBox = new JComboBox<Integer>(integerDate);
			
			//회원가입 7 - 직업
			regTag[8] = new JLabel("Job");
			
			//테그폰트 설정과 텍스트 박스 선언
			for(int i=0; i<regTag.length; i++) {
				regTag[i].setFont(new Font("SanSerif", Font.PLAIN, 15));
				regTag[i].setForeground(Color.WHITE);
				regData[i] = new JTextField("");
				regData[i].setFont(new Font("San Serif", Font.PLAIN, 15));
			}
			
			//프레임에 태그와 텍스트박스 붙이기
			for(int i=0; i<regTag.length; i++) {
				regData[i] = new JTextField("");
				regData[i].setFont(new Font("San Serif", Font.PLAIN, 15));
				subcon.add(regTag[i]);
				regTag[i].setBounds(30, 60 + (i*40), 80, 20);
				if(i==6) { // 성별 선택 박스 설정
					subcon.add(sexComboBox);
					sexComboBox.setBounds(125, 60 + (i*40), 100, 20);
				}else if(i==7) { // 생년월일 선택 박스 설정 
					subcon.add(yearComboBox);
					subcon.add(monthComboBox);
					subcon.add(dateComboBox);
					yearComboBox.setBounds(125, 60 + (i*40), 85, 20);
					monthComboBox.setBounds(205, 60 + (i*40), 70, 20);
					dateComboBox.setBounds(270, 60 + (i*40), 70, 20);
				}
				else {
					subcon.add(regData[i]);
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
				subcon.add(noticeStr[i]);
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
							// 회원가입을 위한 CUTOMER table 새로운 회원정보 입력 쿼리문 작성
							String sql = "INSERT INTO CUSTOMER VALUES(?,?,?,?,?,?,?,?,?)";
							pstmt = conn.prepareStatement(sql);
							pstmt.setString(1, regData[0].getText());
							pstmt.setString(2, regData[1].getText());
							pstmt.setString(3, regData[2].getText());
							pstmt.setString(4, regData[3].getText());
							pstmt.setString(5, regData[4].getText());
							pstmt.setString(6, regData[5].getText());
							pstmt.setString(7, sexComboBox.getSelectedItem().toString());
							pstmt.setString(8, yearComboBox.getSelectedItem().toString() + "-" + monthComboBox.getSelectedItem().toString() + "-" + dateComboBox.getSelectedItem().toString() +"\', ");
							pstmt.setString(9, regData[8].getText());

							pstmt.executeUpdate();
							
							System.out.println("CUSTOMER 테이블에 새로운 레코드를 추가했습니다.");
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
			subcon.add(registerBtn);
			registerBtn.setBounds(35, 450, 290, 40);
			
			
			
		}

	}
}


class HintTextField extends JTextField implements FocusListener {
	/**
	 * 로그인 화면에서 아이디 및 비밀번호 입력란의 hint 출력 구현
	 * 입력창에 입력값이 없을경우 다시 힌트 출력
	 */
	private static final long serialVersionUID = 1L;

	private final String hint;
	private boolean showingHint;

	public HintTextField(final String hint) {
		super(hint);
	    this.hint = hint;
	    this.showingHint = true;
	    super.addFocusListener(this);
	}

	 @Override
	 public void focusGained(FocusEvent e) {
	    if(this.getText().isEmpty()) {
	    		super.setText("");
	    		showingHint = false;
	    }
	 }
	  @Override
	  public void focusLost(FocusEvent e) {
	    if(this.getText().isEmpty()) {
	      super.setText(hint);
	      showingHint = true;
	    }
	  }

	  @Override
	  public String getText() {
	    return showingHint ? "" : super.getText();
	 }
}