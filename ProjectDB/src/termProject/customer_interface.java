package termProject;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.sql.*;
import javax.swing.*;

public class customer_interface extends JFrame implements ActionListener{

	public customer_interface() {
		
		setSize(1000, 600);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
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
		
		product.add(major_category);
		
		selectPanel.addTab("SHOPPINTBAG", shoppingbag);
		selectPanel.addTab("USER_INFO", user_info);
		
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
	}

}
