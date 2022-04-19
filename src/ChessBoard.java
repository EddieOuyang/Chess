import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;

import javax.swing.*;

public class ChessBoard extends JFrame implements ActionListener {
	private JButton[][] field = new JButton[8][8];
	private JButton[] promote = new JButton[4];
	private JPanel grid = new JPanel(new GridLayout(8,8));
	private JPanel options = new JPanel(new GridLayout(2,2,5,5));
	private Color pink = new Color(226,165,185);
	private Color beige = new Color(224,224,224);
	private Color blue = new Color(155,228,255);
	private ChessChat chat = new ChessChat(pink);
	private int turn = 1;
	private int phase = 1;
	private String selected = "99";
	private PieceArray pieces;
	private ImageIcon[] promoteIcon = {new ImageIcon("knightB.png"),new ImageIcon("bishopB.png"),new ImageIcon("rookB.png"),new ImageIcon("queenB.png")};
	private String prmT = "nbrq";
	private String illegalMV;
	private String gmState;
	private String rst;
	
	private String[][] start = {
			{"rb","nb","bb","qb","kb","bb","nb","rb"},
			{"pb","pb","pb","pb","pb","pb","pb","pb"},
			{"na","na","na","na","na","na","na","na"},
			{"na","na","na","na","na","na","na","na"},
			{"na","na","na","na","na","na","na","na"},
			{"na","na","na","na","na","na","na","na"},
			{"pp","pp","pp","pp","pp","pp","pp","pp"},
			{"rp","np","bp","qp","kp","bp","np","rp"}};
	
	public ChessBoard() {
		super("Chess");
		
		pieces = new PieceArray(start);
		
		for(int i = 0; i < 8; i++) {
			for(int k = 0; k < 8; k++) {
				field[i][k] = new JButton();
				field[i][k].setBorderPainted(false);
				if((i + k) % 2 == 1) {
					field[i][k].setBackground(beige);
				} else {
					field[i][k].setBackground(pink);
				}
				field[i][k].setIcon(pieces.iconAt(k, i));
				field[i][k].setDisabledIcon(pieces.iconAt(k, i));
				field[i][k].setEnabled(pieces.liveAt(k,i,turn));
				field[i][k].setName("" + k + i);
				field[i][k].addActionListener(this);
				field[i][k].setBorder(BorderFactory.createMatteBorder(2, 2, 2, 2, blue));
				grid.add(field[i][k]);
			}
		}
		
		for(int i = 0; i < 4; i++) {
			promote[i] = new JButton();
			promote[i].setBackground(Color.gray);
			promote[i].addActionListener(this);
			promote[i].setEnabled(false);
			promote[i].setIcon(promoteIcon[i]);
			promote[i].setDisabledIcon(promoteIcon[i]);
			promote[i].setBorder(BorderFactory.createMatteBorder(5, 5, 5, 5, blue));
			promote[i].setBorderPainted(false);
			promote[i].setName(Character.toString(prmT.charAt(i)));
			options.add(promote[i]);
		}
		options.setBackground(Color.lightGray);
		options.setBorder(BorderFactory.createMatteBorder(5, 5, 5, 5, Color.lightGray));
		options.setPreferredSize(new Dimension(200,150));
		
		Box vert = Box.createVerticalBox();
		vert.add(chat);
		vert.add(options);
		vert.setBackground(Color.lightGray);
		vert.setBorder(BorderFactory.createMatteBorder(2, 2, 2, 2, Color.lightGray));
		Box full = Box.createHorizontalBox();
		full.add(grid);
		full.add(vert);
		
		grid.setBorder(BorderFactory.createMatteBorder(2, 2, 2, 2, Color.lightGray));
		getRootPane().setBorder(BorderFactory.createMatteBorder(10, 10, 10, 10, Color.gray));
		Container c = getContentPane();
		c.add(full);
	}
	
	public void actionPerformed(ActionEvent e) {
		JButton source = (JButton)e.getSource();
		if(phase == 1) {
			source.setBorderPainted(true);
			String moves = Arrays.toString(pieces.getMoves(source.getName()));
			source.setBorder(BorderFactory.createMatteBorder(2,2,2,2, Color.red));;
			illegalMV = source.getName();
			if(moves.length() != 2) {
				for(int i = 0; i < 8; i++) {
					for(int j = 0; j < 8; j++) {
						boolean b = moves.indexOf(field[i][j].getName()) != -1; 
						field[i][j].setEnabled(b);
						field[i][j].setBorderPainted(b);
						if(field[i][j].getName().equals(illegalMV))
							field[i][j].setBorder(BorderFactory.createMatteBorder(2,2,2,2, blue));
					}
				}
				illegalMV = "";
				selected = source.getName();
				phase = 2;
			}
		} else if (phase == 2) {
			turn++;
			pieces.move(selected, source.getName());
			String lm = pieces.lMove();
			gmState = Arrays.toString(pieces.gameState(turn%2 == 1));
			boolean pr = true;
			if(lm.charAt(0) == 'p' && (lm.charAt(3) == '0' || lm.charAt(3) == '7')) {
				pr = false;
				for(int i = 0; i < 4; i++) {
					promote[i].setEnabled(true);
					promote[i].setBorderPainted(true);
				}
				phase = 3;
			} else {
				phase = 1;
			}
			for(int y = 0; y < 8; y++) {
				for(int x = 0; x < 8; x++) {
					field[y][x].setBorderPainted(false);
					field[y][x].setIcon(pieces.getPiece(x, y));
					field[y][x].setDisabledIcon(pieces.getPiece(x, y));
					field[y][x].setEnabled(pieces.getSide(x, y) == (turn%2 == 1) && pieces.getLive(x, y) && pr);
					if(gmState.indexOf(field[y][x].getName()) != -1) {
						field[y][x].setBorderPainted(true);
						field[y][x].setBorder(BorderFactory.createMatteBorder(2, 2, 2, 2, Color.red));
					}
				}
			}
			chat.addMove(lm);
			chat.setState(gmState.substring(gmState.length() - 3, gmState.length() - 1));
			if(gmState.charAt(gmState.length()-2) == 'c') {
				phase = 4;
				rst = gmState.substring(gmState.length() - 7, gmState.length() - 5);
				field[Integer.parseInt(Character.toString(rst.charAt(1)))][Integer.parseInt(Character.toString(rst.charAt(0)))].setBorder(BorderFactory.createMatteBorder(2, 2, 2, 2, Color.yellow));
			}
		} else if (phase == 3) {
			int a = Integer.parseInt(Character.toString(pieces.lMove().charAt(2)));
			int b = Integer.parseInt(Character.toString(pieces.lMove().charAt(3)));
			pieces.promote(a, b, source.getName().charAt(0));
			for(int i = 0; i < 4; i++) {
				promote[i].setEnabled(false);
				promote[i].setBorderPainted(false);
			}
			
			for(int y = 0; y < 8; y++) {
				for(int x = 0; x < 8; x++) {
					field[y][x].setBorderPainted(false);
					field[y][x].setIcon(pieces.getPiece(x, y));
					field[y][x].setDisabledIcon(pieces.getPiece(x, y));
					field[y][x].setEnabled(pieces.getSide(x, y) == (turn%2 == 1) && pieces.getLive(x, y));
				}
			}
			phase = 1;
		} else if (phase == 4) {
			if (source.getName().equals(rst))
				resetGame();
		}
	}
	
	private void resetGame() {
		pieces.setBoard(start);
		chat.reset();
		for(int i = 0; i < 8; i++) {
			for(int k = 0; k < 8; k++) {
				field[i][k].setBorderPainted(false);
				field[i][k].setIcon(pieces.iconAt(k, i));
				field[i][k].setDisabledIcon(pieces.iconAt(k, i));
				field[i][k].setEnabled(pieces.liveAt(k,i,turn));
				field[i][k].setBorder(BorderFactory.createMatteBorder(2, 2, 2, 2, blue));
			}
		}
		for(int i = 0; i < 4; i++) {
			promote[i].setEnabled(false);
			promote[i].setBorderPainted(false);
		}
		turn = 1;
		phase = 1;
		selected = "99";
		rst = "99";
	}
	
	public static void main(String args[]) {
		ChessBoard window = new ChessBoard();
		window.setBounds(100,0,970,720);
		window.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		window.setResizable(false);
		window.setVisible(true);
	}
}
