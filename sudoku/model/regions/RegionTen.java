package sudoku.model.regions;

/**
 * Le type énuméré représentant les régions d'une grille
 * de taille 10x10.
 * 
 * @author Khabouri Izana
 */
public enum RegionTen implements IRegion {

	// VALEURS STATIQUES

	REGION1(0, 1, 0, 4),
	REGION2(0, 1, 5, 9),
	REGION3(2, 3, 0, 4),
	REGION4(2, 3, 5, 9),
	REGION5(4, 5, 0, 4),
	REGION6(4, 5, 5, 9),
	REGION7(6, 7, 0, 4),
	REGION8(6, 7, 5, 9),
	REGION9(8, 9, 0, 4),
	REGION10(8, 9, 5, 9);

	// ATTRIBUTS

	private int startX;
	private int endX;
	private int startY;
	private int endY;

	// CONSTRUCTEUR

	private RegionTen(int startX, int endX, int startY, int endY) {
		this.startX = startX;
		this.endX = endX;
		this.startY = startY;
		this.endY = endY;
	}

	// REQUETES

	@Override
	public int getStartX() {
		return this.startX;
	}

	@Override
	public int getEndX() {
		return this.endX;
	}

	@Override
	public int getStartY() {
		return this.startY;
	}

	@Override
	public int getEndY() {
		return this.endY;
	}
}
