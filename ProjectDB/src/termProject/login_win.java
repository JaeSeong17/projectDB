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
	ResultSet rs = null;
	
	public login_win(Connection conn) {
		super("login_window");
		setSize(260, 400);
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
					String sql = "SELECT First_name, Last_name FROM CUSTOMER WHERE Cus_id = \'" + temp_id
							+ "\' AND Cus_password = \'" + temp_pass + "\'";
					
					// 5. operate query
					rs = stmt.executeQuery(sql);
					
					// 6. print result of execution
					if(rs.isBeforeFirst()==false) {
						container.add(failMessage);
						failMessage.setBounds(33, 200, 200, 20);
					}else {
						while(rs.next()) {
							String Fname = rs.getString(1);
							String Lname = rs.getString(2);
						
							System.out.println(Fname + " - " + Lname);
						}
					}
					
				}catch(SQLException ex) {
					System.out.println("Error : " + ex);
				}
			}
		});
		
		
		JButton newAccBtn = new JButton("Register new account");
		newAccBtn.setFont(new Font("San Serif", Font.PLAIN, 15));
		newAccBtn.setForeground(Color.GRAY);

		newAccBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				SubRegisterFrame srf = new SubRegisterFrame();
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
	
	

	public class SubRegisterFrame extends JFrame implements ActionListener{ 
		/**
		* 회원가입 창 구현부
		* 해당 정보 모두 입력시 DB로 쿼리 전달 " INSERT INTO CUSTOMER VALUES(...)"
	 	*/
		private static final long serialVersionUID = 1L;
		
		Statement insertCusData = null;
		
		public SubRegisterFrame() {
			super("Register Window");
			//setSize()
			
			
			
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			dispose();
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