package sudoku.model.regions;

/**
 * Le type énuméré représentant les régions d'une grille
 * de taille 6x6.
 * 
 * @author Khabouri Izana
 */
public enum RegionSix implements IRegion {

	// CONSTANTES

	REGION1(0, 1, 0, 2),
	REGION2(0, 1, 3, 5),
	REGION3(2, 3, 0, 2),
	REGION4(2, 3, 3, 5),
	REGION5(4, 5, 0, 2),
	REGION6(4, 5, 3, 5);

	// ATTRIBUTS

	private int startX;
	private int endX;
	private int startY;
	private int endY;

	// CONSTRUCTEUR

	private RegionSix(int startX, int endX, int startY, int endY) {
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
