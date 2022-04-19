import javax.swing.ImageIcon;

public class ChessPiece {
	private boolean side;
	private char type;
	private ImageIcon img;
	private boolean live;
	private int moves;
	private String name;

	public ChessPiece() {
		type = 'x';
		img = null;
		live = false;
		name = "na";
	}
	
	public ChessPiece(boolean side, char type) {
		this.side = side;
		this.type = type;
		live = true;
	}
	
	public ChessPiece(boolean side, char type, ImageIcon img) {
		this.side = side;
		this.type = type;
		this.img = img;
		live = true;
		moves = 0;
		char sd = 'b';
		if(side) {sd = 'p';} 
		name = "" + type + sd;
	}
	
	public void promoteTo(char type, ImageIcon img) {
		this.type = type;
		this.img = img;
		name = type + name.substring(1);
	}
	
	public ImageIcon getIcon() {
		return img;
	}
	
	public boolean getLive() {
		return live;
	}
	
	public boolean getSide() {
		return side;
	}
	
	public char getType() {
		return type;
	}
	
	public int getMoves() {
		return moves;
	}
	
	public void replace(ChessPiece other) {
		this.side = other.side;
		this.type = other.type;
		this.img = other.img;
		this.moves = other.moves + 1;
		this.live = other.live;
		this.name = other.getName();
	}
	
	public void reset() {
		type = 'x';
		img = null;
		live = false;
	}
	
	public String getName() {
		return name;
	}
}
