package sudoku.model.regions;

/**
 * Le type énuméré représentant les régions d'une grille
 * de taille 8x8.
 * 
 * @author Khabouri Izana
 */
public enum RegionEight implements IRegion {

	// CONSTANTES

	REGION1(0, 1, 0, 3),
	REGION2(0, 1, 4, 7),
	REGION3(2, 3, 0, 3),
	REGION4(2, 3, 4, 7),
	REGION5(4, 5, 0, 3),
	REGION6(4, 5, 4, 7),
	REGION7(6, 7, 0, 3),
	REGION8(6, 7, 4, 7);

	// ATTRIBUTS

	private int startX;
	private int endX;
	private int startY;
	private int endY;

	// CONSTRUCTEUR

	private RegionEight(int startX, int endX, int startY, int endY) {
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
