package termProject;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
//import java.sql.Statement;
//import java.sql.ResultSet;

public class MainClass {
	public static void main(String[] args) {
		Connection conn = null;
		
		//DB와의 연결
		try {
			// 1. Driver loading (드라이버 로딩)
			Class.forName("com.mysql.cj.jdbc.Driver");
			
			// 2. connection (연결하기)
			conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/Market?characterEncoding=UTF-8&serverTimezone=UTC","knu","comp322");
			System.out.println("데이터 베이스 접속이 성공했습니다");
		}catch(SQLException ex) {
			System.out.println("SQLException:" + ex);
		}catch(Exception ex) {
			System.out.println("Exception:" + ex);
		}
		
		//로그인창 생
		login_win loginWindow = new login_win(conn);
		loginWindow.setVisible(true);
		
	}
}
