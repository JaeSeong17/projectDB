package termProject;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.sql.*;
import javax.swing.*;
import java.util.Calendar;

public class customer_interface extends JFrame implements ActionListener{
	
	private Calendar today = Calendar.getInstance();

	public customer_interface() {
		
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
		
		JPanel[] major_panel;			//개수를 동적으로 넣을지 정적으로 넣을지 선택
		JTabbedPane[] minor_category;	//major_panel과 같은개수
		JPanel[] minor_panel;			//여기에 제품정보 출력
		
		//동적으로 갯수, 이름 맞춰서 반복문 통해서 정보 집어넣음(3중 반복문)
		//"중요" 배열건드릴때 nullpointerexception 생각하고 만들것
		
		/*for(int i=0;i<(majorcategory개수);i++) {

		 	major_panel[i] = new JPanel();
		 	minor_category[i] = new JTabbedPane();
			major_category.addTab("//쿼리문", major_panel[i]);
			major_panel[i].add(minor_category[i]);
			
			for(int j=0;j<(minorcategory개수;j++) {
			
				minor_panel[i] = new JPanel;
				minor_category[i].addTab("//쿼리문", minor_panel[i]);
				
				for(int k=0;k<(category별 item갯수);k++) {
				
					//minor_panel에 출력할 정보 만들

				}
				
			}
			
		}*/
		
		selectPanel.addTab("SHOPPINTBAG", shoppingbag);
		//SHOPPINGBAG탭 디자인
		
		
		selectPanel.addTab("USER_INFO", user_info);
		
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
	}

}
