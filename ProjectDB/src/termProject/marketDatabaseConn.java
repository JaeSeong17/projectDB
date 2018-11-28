package termProject;

import java.sql.*;
//import javax.swing.*;

public class marketDatabaseConn {
	public static void main(String[] args) {
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		
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
		
		try { 
			// 3. create object to operate query (쿼리 수행을 위한 객체 생성)
			stmt = conn.createStatement();
			
			// 4. write SQL query
			String sql = "SELECT First_name, Last_name FROM CUSTOMER";
			
			// 5. operate query
			rs = stmt.executeQuery(sql);
			
			// 6. print result of execution
			while(rs.next()) {
				String Fname = rs.getString(1);
				String Lname = rs.getString(2);
				
				System.out.println(Fname + " - " + Lname);
			}
			
		}catch(SQLException ex) {
			System.out.println("Error : " + ex);
		}
		
		finally{
			try{
				if( conn != null && !conn.isClosed()){
					conn.close();
		        }
		        if( stmt != null && !stmt.isClosed()){
		        		stmt.close();
		        }
		        if( rs != null && !rs.isClosed()){
		        		rs.close();
		        }
			}
		    catch( SQLException e){
		        e.printStackTrace();
		    }
		}
	}
}
