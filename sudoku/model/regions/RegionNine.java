package sudoku.model.regions;

/**
 * Le type énuméré représentant les régions d'une grille
 * de taille 9x9.
 * 
 * @author Khabouri Izana
 */
public enum RegionNine implements IRegion {

	// VALEURS STATIQUES

	REGION1(0, 2, 0, 2),
	REGION2(0, 2, 3, 5),
	REGION3(0, 2, 6, 8),
	REGION4(3, 5, 0, 2),
	REGION5(3, 5, 3, 5),
	REGION6(3, 5, 6, 8),
	REGION7(6, 8, 0, 2),
	REGION8(6, 8, 3, 5),
	REGION9(6, 8, 6, 8);

	// ATTRIBUTS

	private int startX;
	private int endX;
	private int startY;
	private int endY;

	// CONSTRUCTEUR

	private RegionNine(int startX, int endX, int startY, int endY) {
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
