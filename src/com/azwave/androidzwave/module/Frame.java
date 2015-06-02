/**
 * 
 */
package com.azwave.androidzwave.module;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

/**
 * @author florian
 *
 */
public class Frame extends JFrame {

	private final int WIDTH = 700;
	private final int HEIGHT = 500;
	
	private JScrollPane scrollPane;
	private JPanel container, logContainer, left;
	private JLabel nodeName;
	
	public Frame(){
		createJFrame();
		nodeName = new JLabel("Node %d -- %s");
	}
	
	public void createJFrame(){
		this.setTitle("Java Zwave");
		this.setSize(new Dimension(WIDTH, HEIGHT));
		this.setLocationRelativeTo(null);
		this.setVisible(true);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		logContainer = new JPanel(new BorderLayout());
		container = new JPanel();
		container.setLayout(new BoxLayout(container, BoxLayout.PAGE_AXIS));
		scrollPane = new JScrollPane(container);
		logContainer.add(scrollPane, BorderLayout.EAST);
		left = new JPanel();
		left.setBackground(Color.white);
		logContainer.add(left, BorderLayout.CENTER);
		this.setContentPane(logContainer);
	}
	
	public void updateLogContent(ArrayList<String> _log){
		for(String tmp: _log){
			JLabel log = new JLabel();
			log.setText(log.getText() + tmp);
			container.add(log);
		}
		this.setVisible(true);
	}
}
