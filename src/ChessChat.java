import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;

import javax.swing.*;

public class ChessChat extends JPanel implements KeyListener{
	private JTextArea moves;
	private JTextField state;
	private String[][] states = {{"Pink to Move", "Pink in Check", "Black Wins By Mate"},
								 {"Black to Move", "Black in Check", "Pink Wins By Mate"}};
	private Font font = new Font("Algerian", Font.BOLD, 20);
	private Font font2 = new Font(Font.MONOSPACED, Font.BOLD, 14);
	private String side = "pb";
	private String type = "prnbqk";
	private String axis  = "01234567";
	private String[] sideA = {"Pink  ", "Black "};
	private String[] typeA = {"Pawn  ", "Rook  ", "Knight", "Bishop", "Queen ", "King  "};
	private String y = "87654321";
	private String x = "abcdefgh";
	private int scrollTop = 0;
	private ArrayList<String> history = new ArrayList<String>();
	
	public ChessChat(Color c) {
		JFrame j = new JFrame();
		j.setLayout(new BorderLayout());
		
		moves = new JTextArea();
		moves.setBackground(Color.gray);
		moves.setForeground(c);
		moves.setEditable(false);
		moves.setPreferredSize(new Dimension(260,400));
		moves.setFont(font2);
		moves.addKeyListener(this);
		
		
		state = new JTextField();
		state.setBackground(Color.gray);
		state.setForeground(c);
		state.setEditable(false);
		state.setPreferredSize(new Dimension(250,50));
		state.setHorizontalAlignment(state.CENTER);
		state.setFont(font);
		state.setText(states[0][0]);
		
		this.add(moves);
		this.add(state);		
		this.setBackground(Color.lightGray);
	}
	
	public void addMove(String str) {
		String newMove = sideA[side.indexOf(str.charAt(1))] + typeA[type.indexOf(str.charAt(0))] + " [" + x.charAt(axis.indexOf(str.charAt(4))) + y.charAt(axis.indexOf(str.charAt(5))) + " > " + x.charAt(axis.indexOf(str.charAt(2))) + y.charAt(axis.indexOf(str.charAt(3))) + "]";
		history.add(newMove);
		if(scrollTop < history.size()-20) {scrollTop = history.size()-20;}
		String text = "";
		for(int i = scrollTop, k = 0; k < 20 && i < history.size(); k++, i++) {
			text += "   " + history.get(i) + "\n";
		}
		moves.setText(text);
	}

	public void setState(String str) {
		int a = str.charAt(0) - 97;
		int b = str.charAt(1) - 97;
		state.setText(states[a][b]);
		
	}
	
	public void reset() {
		history.clear();
		moves.setText("");
		state.setText(states[0][0]);
	}
	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if(e.getKeyCode() == 38 && scrollTop > 0) {
			scrollTop--;
		} 
		if(e.getKeyCode() == 40 && scrollTop < history.size()-20) {
			scrollTop++;
		}
		String text = "";
		for(int i = scrollTop, k = 0; k < 20 && i < history.size(); k++, i++) {
			text += "   " + history.get(i) + "\n";
		}
		moves.setText(text);
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}
}
