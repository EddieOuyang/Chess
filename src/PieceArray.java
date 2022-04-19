import java.util.*;
import javax.swing.ImageIcon;

public class PieceArray {
	private ChessPiece[][] board = new ChessPiece[8][8];
	private ImageIcon[] black = {new ImageIcon("pawnB.png"),new ImageIcon("rookB.png"),new ImageIcon("bishopB.png"),new ImageIcon("knightB.png"),new ImageIcon("queenB.png"),new ImageIcon("kingB.png")};
	private ImageIcon[] white = {new ImageIcon("pawnP.png"),new ImageIcon("rookP.png"),new ImageIcon("bishopP.png"),new ImageIcon("knightP.png"),new ImageIcon("queenP.png"),new ImageIcon("kingP.png")};
	private String options = "prbnqk";
	private int[][] king = {{-1,-1,-1,0,0,1,1,1},
							{-1,0,1,-1,1,-1,0,1}};
	private int[][] knight = {{-2,-2,-1,-1,1,1,2,2},
							  {-1,1,-2,2,-2,2,-1,1}};
	private String lastMove = "";
	
	public PieceArray(String[][] str) {
		ImageIcon[] pointer;
		boolean temp;
		for(int i = 0; i < 8; i++) {
			for(int k = 0; k < 8; k++) {
				if(str[i][k].equals("na")) {
					board[i][k] = new ChessPiece();
					continue;
				}
				temp = str[i][k].charAt(1) == 'p';
				if(temp) {
					pointer = white;
				} else {
					pointer = black;
				}
				board[i][k] = new ChessPiece(temp, str[i][k].charAt(0), pointer[options.indexOf(str[i][k].charAt(0))]);
			}
		}
	}
	
	public PieceArray(PieceArray other) {
		for(int i = 0; i < 8; i++) {
			for(int k = 0; k < 8; k++) {
				this.board[i][k] = new ChessPiece(other.board[i][k].getSide(), other.board[i][k].getType());
			}
		}
	}
	
	public void promote(int x, int y, char type) {
		ImageIcon[] pointer;
		boolean temp;
		temp = board[y][x].getSide();
		if(temp) {
			pointer = white;
		} else {
			pointer = black;
		}
		board[y][x].promoteTo(type, pointer[options.indexOf(type)]);
	}
	
	public ImageIcon iconAt(int x, int y) {
		return board[y][x].getIcon();
	}
	
	public boolean liveAt(int x , int y, int turn) {
		
		return (board[y][x].getSide() == (turn%2 == 1));
	}
	
	public String[] getMoves(String n) {
		int x = Integer.parseInt(Character.toString(n.charAt(0)));
		int y = Integer.parseInt(Character.toString(n.charAt(1)));
		ArrayList<Integer> pMoves = new ArrayList<Integer>();
		int d = 1;
		if(!board[y][x].getSide()) {d = -1;}
		boolean side = board[y][x].getSide();
		switch(board[y][x].getType()) {
		case 'p': if(!board[y - d][x].getLive()) {pMoves.add((x * 10) + (y - d));}
			if(board[y][x].getMoves() == 0 && (y == 1 || y == 6) && !board[y-2*d][x].getLive()) pMoves.add(moveTo(x, y - 2*d, side));
			pMoves.add(capture(x - 1, y - d, side));
			pMoves.add(capture(x + 1, y - d, side));
			pMoves.add(enP(x - 1, y, d, side));
			pMoves.add(enP(x + 1, y, d, side));
			break;
		case 'r': moveS(x,y,side,pMoves);
			break;
		case 'n': for(int i = 0; i < 8; i++) if(y+knight[1][i] > -1 && y+knight[1][i] < 8 && x+knight[0][i] > -1 && x+knight[0][i] < 8){{pMoves.add(moveTo(x + knight[0][i], y + knight[1][i],side));}}
			break;
		case 'b': moveD(x,y,side,pMoves);
			break;
		case 'q': moveS(x,y,side,pMoves);
			moveD(x,y,side,pMoves);
			break;
		case 'k': for(int i = 0; i < 8; i++) if(y+king[1][i] > -1 && y+king[1][i] < 8 && x+king[0][i] > -1 && x+king[0][i] < 8){{pMoves.add(moveTo(x + king[0][i], y + king[1][i],side));}}
			if(board[y][x].getMoves() == 0) {
				String castle = "";
				for(int i = 0; i < 8; i++) 
					if(board[y][i].getSide() == board[y][x].getSide())
						castle += board[y][i].getType();
				if(castle.indexOf("rxxxk") != -1)
					pMoves.add(20 + y);
				if(castle.indexOf("kxxr") != -1)
					pMoves.add(60 + y);
			}
			break;
		}
		
		String[] moves = new String[pMoves.size()];
		for(int i = 0; i < pMoves.size(); i++) {
			if(pMoves.get(i) == 99) continue;
			String nMove = String.format("%02d", pMoves.get(i));
			PieceArray illegal = new PieceArray(this);
			illegal.move(n, nMove);
			int kx=0,ky=0;
			outerloop:
			for(int k = 0; k < 8; k++) {
				for(int j = 0; j < 8; j++) {
					if(illegal.board[k][j].getType() == 'k' && illegal.board[k][j].getSide() == side) {
						kx = j;
						ky = k;
						break outerloop;
					}
				}
			}
			if(illegal.inCheck(kx, ky).length == 0) {
				moves[i] = String.format("%02d", pMoves.get(i));
			}
		}
		return Arrays.stream(moves).filter(Objects::nonNull).toArray(String[]::new);
	}
	
	public void move(String start, String end) {
		int x = Integer.parseInt(Character.toString(start.charAt(0)));
		int y = Integer.parseInt(Character.toString(start.charAt(1)));
		int x2 = Integer.parseInt(Character.toString(end.charAt(0)));
		int y2 = Integer.parseInt(Character.toString(end.charAt(1)));
		lastMove = board[y][x].getName() + end + start;
		
		board[y2][x2].replace(board[y][x]);
		board[y][x].reset();
		
		if((Math.abs(Integer.parseInt(start) - Integer.parseInt(end)) == 11 || x + y == x2 + y2 ) && lastMove.charAt(0) == 'p' && (lastMove.charAt(3) == '2' || lastMove.charAt(3) == '5')) {
			board[y][x2].reset();
		}
		if(Math.abs(x2 - x) == 2 && lastMove.charAt(0) == 'k') {
			if(x2 == 6) {
				board[y][5].replace(board[y][7]);
				board[y][7].reset();
			} else {
				board[y][3].replace(board[y][0]);
				board[y][0].reset();
			}
		}
	}
	
	public ImageIcon getPiece(int x, int y) {
		return board[y][x].getIcon();
	}
	
	public boolean getSide(int x, int y) {
		return board[y][x].getSide();
	}
	
	public boolean getLive(int x, int y) {
		return board[y][x].getLive();
	}
	
	public String lMove() {
		return lastMove;
	}
	
	public String[] gameState(boolean side) {
		int kx=0,ky=0;
		outerloop:
		for(int k = 0; k < 8; k++) {
			for(int j = 0; j < 8; j++) {
				if(board[k][j].getType() == 'k' && board[k][j].getSide() == side) {
					kx = j;
					ky = k;
					break outerloop;
				}
			}
		}
		String[] checkInc = inCheck(kx,ky);
		String[] plusState = new String[checkInc.length + 2];
		String state = "b";
		if(side) state = "a";
		if(checkInc.length == 0) state += "a";
		else if (checkMate(side)){
			state += "c";
		} else {
			state += "b";
		}
		for(int i = 0; i < checkInc.length; i++)
			plusState[i] = checkInc[i];
		plusState[plusState.length - 1] = state;
		if(checkInc.length > 0) plusState[plusState.length - 2] = "" + kx + ky;
		return plusState;
	}
	
	public void setBoard(String[][] str) {
		ImageIcon[] pointer;
		boolean temp;
		for(int i = 0; i < 8; i++) {
			for(int k = 0; k < 8; k++) {
				if(str[i][k].equals("na")) {
					board[i][k] = new ChessPiece();
					continue;
				}
				temp = str[i][k].charAt(1) == 'p';
				if(temp) {
					pointer = white;
				} else {
					pointer = black;
				}
				board[i][k] = new ChessPiece(temp, str[i][k].charAt(0), pointer[options.indexOf(str[i][k].charAt(0))]);
			}
		}
	}
	
	private boolean checkMate(boolean side) {
		boolean mate = true;
		outerloop:
		for(int y = 0; y < 8; y++) {
			for(int x = 0; x < 8; x++) {
				if(board[y][x].getSide() == side) {
					String[] temp = getMoves("" + x + y);
					if(temp.length > 0) {
						mate = false;
						break outerloop;
					}
				}
			}
		}
		return mate;
	}
	
	private String[] inCheck(int x, int y) {
		ArrayList<Integer> checks = new ArrayList<Integer>();
		boolean side = board[y][x].getSide();
		int d = 1;
		if(!board[y][x].getSide()) {d = -1;}
		if(y-d < 8 && y-d > -1 && x-1 > -1 ) {if(board[y-d][x-1].getType() == 'p') checks.add(capture(x - 1, y - d, side));}
		if(y-d < 8 && y-d > -1 && x+1 < 8 ) {if(board[y-d][x+1].getType() == 'p') checks.add(capture(x + 1, y - d, side));}
		for(int i = 0; i < 8; i++) {
			if(y+king[1][i] > -1 && y+king[1][i] < 8 && x+king[0][i] > -1 && x+king[0][i] < 8) {
				if(board[y+king[1][i]][x+king[0][i]].getType() == 'k')
					checks.add(moveTo(x + king[0][i], y + king[1][i],side));
			}
		}
		for(int i = 0; i < 8; i++) {
			if(y+knight[1][i] > -1 && y+knight[1][i] < 8 && x+knight[0][i] > -1 && x+knight[0][i] < 8) {
			if(board[y+knight[1][i]][x+knight[0][i]].getType() == 'n')
				checks.add(moveTo(x + knight[0][i], y + knight[1][i],side));
			}
		}
		if(y < 7 && x < 7) {
			for(int i = y + 1, k = x + 1, go = 1; i < 8 && k < 8 && go == 1; i++, k++) {
				if(board[i][k].getType() == 'b' || board[i][k].getType() == 'q') {
					checks.add(capture(k,i,side));
				}
				if(board[i][k].getType() != 'x')
					go = 0;
			}
		}
		if(y > 0 && x > 0) {
			for(int i = y - 1, k = x - 1, go = 1; i > -1 && k > -1 && go == 1; i--, k--) {
				if(board[i][k].getType() == 'b' || board[i][k].getType() == 'q') {
					checks.add(capture(k,i,side));
				}
				if(board[i][k].getType() != 'x')
					go = 0;
			}
		}
		if(y > 0 && x < 7) {
			for(int i = y - 1, k = x + 1, go = 1; i > -1 && k < 8 && go == 1; i--, k++) {
				if(board[i][k].getType() == 'b' || board[i][k].getType() == 'q') {
					checks.add(capture(k,i,side));
				}
				if(board[i][k].getType() != 'x')
					go = 0;
			}
		}
		if(y < 7 && x > 0) {
			for(int i = y + 1, k = x - 1, go = 1; i < 8 && k > -1 && go == 1; i++, k--) {
				if(board[i][k].getType() == 'b' || board[i][k].getType() == 'q') {
					checks.add(capture(k,i,side));
				}
				if(board[i][k].getType() != 'x')
					go = 0;
			}
		}
		if(y < 7) {
			for(int i = y + 1, go = 1; i < 8 && go == 1; i++) {
				if(board[i][x].getType() == 'r' || board[i][x].getType() == 'q') {
					checks.add(capture(x,i,side));
				}
				if(board[i][x].getType() != 'x')
					go = 0;
			}
		}
		if(y > 0) {
			for(int i = y - 1, go = 1; i > -1 && go == 1; i--) {
				if(board[i][x].getType() == 'r' || board[i][x].getType() == 'q') {
					checks.add(capture(x,i,side));
				}
				if(board[i][x].getType() != 'x')
					go = 0;
			}
		}
		if(x < 7) {
			for(int i = x + 1, go = 1; i < 8 && go == 1; i++) {
				if(board[y][i].getType() == 'r' || board[y][i].getType() == 'q') {
					checks.add(capture(i,y,side));
				}
				if(board[y][i].getType() != 'x')
					go = 0;
			}
		}
		if(x > 0) {
			for(int i = x - 1, go = 1; i > -1 && go == 1; i--) {
				if(board[y][i].getType() == 'r' || board[y][i].getType() == 'q') {
					checks.add(capture(i,y,side));
				}
				if(board[y][i].getType() != 'x')
					go = 0;
			}
		}
		
		String[] moves = new String[checks.size()];
		for(int i = 0; i < checks.size(); i++) {
			if(checks.get(i) == 99) continue;
			moves[i] = String.format("%02d", checks.get(i));
		}
		return Arrays.stream(moves).filter(Objects::nonNull).toArray(String[]::new);
	}
	
	private int moveTo(int x, int y, boolean side) {
		if(-1 < x && x < 8 && -1 < y && y < 8) {
			if(!board[y][x].getLive() || (board[y][x].getLive() && board[y][x].getSide() != side)) {
				return (x * 10) + y;
			}
		}
		return 99;
	}
	
	private int capture(int x, int y, boolean side) {
		if(-1 < x && x < 8 && -1 < y && y < 8) {
			if((board[y][x].getLive() && board[y][x].getSide() != side)) {
				return (x * 10) + y;
			}
		}
		return 99;
	}
	
	private void moveD(int x, int y,boolean side,ArrayList Moves) {
		if(y < 7 && x < 7) {
			for(int i = y + 1, k = x + 1, go = 1; i < 8 && k < 8 && go == 1; i++, k++) {
				Moves.add(moveTo(k,i,side));
				if(board[i][k].getType() != 'x')
					go = 0;
			}
		}
		if(y > 0 && x > 0) {
			for(int i = y - 1, k = x - 1, go = 1; i > -1 && k > -1 && go == 1; i--, k--) {
				Moves.add(moveTo(k,i,side));
				if(board[i][k].getType() != 'x')
					go = 0;
			}
		}
		if(y > 0 && x < 7) {
			for(int i = y - 1, k = x + 1, go = 1; i > -1 && k < 8 && go == 1; i--, k++) {
				Moves.add(moveTo(k,i,side));
				if(board[i][k].getType() != 'x')
					go = 0;
			}
		}
		if(y < 7 && x > 0) {
			for(int i = y + 1, k = x - 1, go = 1; i < 8 && k > -1 && go == 1; i++, k--) {
				Moves.add(moveTo(k,i,side));
				if(board[i][k].getType() != 'x')
					go = 0;
			}
		}
	}
	
	private void moveS(int x, int y,boolean side,ArrayList Moves) {
		if(y < 7) {
			for(int i = y + 1, go = 1; i < 8 && go == 1; i++) {
				Moves.add(moveTo(x,i,side));
				if(board[i][x].getType() != 'x')
					go = 0;
			}
		}
		if(y > 0) {
			for(int i = y - 1, go = 1; i > -1 && go == 1; i--) {
				Moves.add(moveTo(x,i,side));
				if(board[i][x].getType() != 'x')
					go = 0;
			}
		}
		if(x < 7) {
			for(int i = x + 1, go = 1; i < 8 && go == 1; i++) {
				Moves.add(moveTo(i,y,side));
				if(board[y][i].getType() != 'x')
					go = 0;
			}
		}
		if(x > 0) {
			for(int i = x - 1, go = 1; i > -1 && go == 1; i--) {
				Moves.add(moveTo(i,y,side));
				if(board[y][i].getType() != 'x')
					go = 0;
			}
		}
	}
	
	private int enP(int x, int y, int d, boolean side) {
		if(-1 < x && x < 8 && -1 < y && y < 8) {
			if(!board[y - d][x].getLive() && board[y][x].getSide() != side && board[y][x].getMoves() == 1 && board[y][x].getType() == 'p' && lastMove.charAt(0) == 'p' && (lastMove.charAt(3) == '3' || lastMove.charAt(3) == '4')) {
				return (x * 10) + y - d;
			}
		}
		return 99;
	}
}
