package sudoku.model.regions;

/**
 * Le type énuméré représentant les régions d'une grille
 * de taille 4x4.
 * 
 * @author Khabouri Izana
 */
public enum RegionFour implements IRegion {

	// CONSTANTES

	REGION1(0, 1, 0, 1),
	REGION2(0, 1, 2, 3),
	REGION3(2, 3, 0, 1),
	REGION4(2, 3, 2, 3);

	// ATTRIBUTS

	private int startX;
	private int endX;
	private int startY;
	private int endY;

	// CONSTRUCTEUR

	private RegionFour(int startX, int endX, int startY, int endY) {
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
