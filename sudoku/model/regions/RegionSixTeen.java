package sudoku.model.regions;

/**
 * Le type énuméré représentant les régions d'une grille
 * de taille 16x16.
 * 
 * @author Khabouri Izana
 */
public enum RegionSixTeen implements IRegion {

	// CONSTANTES

	REGION1(0, 3, 0, 3),
	REGION2(0, 3, 4, 7),
	REGION3(0, 3, 8, 11),
	REGION4(0, 3, 12, 15),
	REGION5(4, 7, 0, 3),
	REGION6(4, 7, 4, 7),
	REGION7(4, 7, 8, 11),
	REGION8(4, 7, 12, 15),
	REGION9(8, 11, 0, 3),
	REGION10(8, 11, 4, 7),
	REGION11(8, 11, 8, 11),
	REGION12(8, 11, 12, 15),
	REGION13(12, 15, 0, 3),
	REGION14(12, 15, 4, 7),
	REGION15(12, 15, 8, 11),
	REGION16(12, 15, 12, 15);

	// ATTRIBUTS

	private int startX;
	private int endX;
	private int startY;
	private int endY;

	// CONSTRUCTEUR

	private RegionSixTeen(int startX, int endX, int startY, int endY) {
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
