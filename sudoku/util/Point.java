package sudoku.util;

/**
 * La classe Point représente une coordonnées non
 * changeante.
 */
public class Point {

	// ATTRIBUTS

	private int x;
	private int y;

	// CONSTRUCTEURS

	public Point(int a, int b) {
		this.x = a;
		this.y = b;
	}

	// REQUETES

	public int getX() {
		return this.x;
	}

	public int getY() {
		return this.y;
	}
}
