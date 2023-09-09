package sudoku.model.heuristic;

import sudoku.model.Action;
import sudoku.model.ICase;
import sudoku.model.IGrid;
import sudoku.model.info.Command;
import sudoku.util.Tools;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Set;

/**
 * La classe SwordFish implémente l'interface IHeuristic.
 * Elle représente l'heuristique : SwordFish.
 * Swordfish
 * 1er cas (2ème cas): 
 * Pour chaque n de 1 à SIZE
 * 		Pour chaque combinaison de 3 lignes (ou colonnes):
 *      	Si chaque lignes à au moins 2 occurrences de n
 *          	Récupérer les indices dans chaque lignes (ou colonnes) où se trouve n
 *              Faire une intersection des indices pour trouver 3 indices où se situe n
 *              Supprimer n du reste des colonnes (ou lignes) où se situe n sauf dans les lignes (ou colonnes) de départ
 *
 * 3ème cas (4ème cas): Idem mais on fait l'intersection pour trouver 2 colonnes (ou lignes) et 1 région ou se situe n au moins 2 fois dans chaque
 *
 * 5ème cas (6ème cas):
 * Pour chaque n de 1 à SIZE
 * 		Pour chaque combinaison de 2 lignes (ou colonnes) et 1 région:
 *      	Si chaque unité à au moins 2 occurrences de n:
 *          	Récupérer les indices dans chaque lignes (ou colonnes) où se trouve n
 *              Faire une intersection des indices pour trouver 2 colonnes(ou lignes) et 2 régions où se situe n
 *              Supprimer n du reste des 2 colonnes (ou lignes) et 2 régions où se situe n sauf dans les unités de départ
 * 
 * @author Cavelier Tanguy
 */
public class SwordFish implements IHeuristic {
	// CONSTANTES
	private final int SIZE;
	// ATTRIBUTS

	private final IGrid grid;

	// CONSTRUCTEUR

	/**
	 * Constructeur de la classe SwordFish qui prend une Grid en entrée
	 */
	public SwordFish(IGrid grille) {
		grid = grille;
		SIZE = grid.getSize().getSize();
	}

	// REQUETES

	/**
	 * Méthode qui renvoie une string de ce que doit faire l'utilisateur si
	 * il y a une aide possible
	 */

	@Override
	public Answer compute() {
		for (int digit = 1; digit <= SIZE; digit++) {
			for (int row1 = 0; row1 < SIZE - 2; row1++) {
				for (int row2 = row1 + 1; row2 < SIZE - 1; row2++) {
					for (int row3 = row2 + 1; row3 < SIZE; row3++) {
						ICase[] cells1 = grid.getLine(row1);
						ICase[] cells2 = grid.getLine(row2);
						ICase[] cells3 = grid.getLine(row3);

						ArrayList<ICase[]> list = new ArrayList<ICase[]>();
						list.add(cells1);
						list.add(cells2);
						list.add(cells3);

						ICase[] col1 = grid.getColumn(row1);
						ICase[] col2 = grid.getColumn(row2);
						ICase[] col3 = grid.getColumn(row3);

						ArrayList<ICase[]> list2 = new ArrayList<ICase[]>();
						list2.add(col1);
						list2.add(col2);
						list2.add(col3);
						ArrayList<Integer>[] t = findIndices(list, digit);
						ArrayList<Integer>[] t2 = findIndices(list2, digit);
						if (t != null) {// test 3 lignes
							int[] b = findCommonIntegers(t, true, digit);
							if (b != null) {// Cas 3 lignes 3 colonnes
								String s = "Vous pouvez supprimer les " + Tools.getData(digit, grid.getType())
										+ " qui se situent dans les cases rouges car d'après SwordFish si on ne trouve que 2 candidats d'une même valeur dans 3 lignes et en même temps dans 3 colonnes alors on peut supprimer les autres candidats de cette valeur dans les colonnes sauf dans les lignes de départ";
								return createAnswer(s, b, row1, row2, row3, 1, digit);
							}
							int[] a = findCommonIntegers2(t, row1, row2, row3, true, digit);
							if (a != null) {// Cas 3 lignes 2 colonnes 1 région
								String s = "Vous pouvez supprimer les " + Tools.getData(digit, grid.getType())
										+ " qui se situent dans les cases rouges car d'après SwordFish si on ne trouve au moins 2 candidats d'une même valeur dans 3 lignes et en même temps dans 2 colonnes et 1 région alors on peut supprimer les autres candidats de cette valeur dans ces 2 colonnes et 1 région sauf dans les lignes de départ";
								return createAnswer(s, a, row1, row2, row3, 2, digit);
							}
						}
						if (t2 != null) { // test 3 colonnes
							int[] b = findCommonIntegers(t2, false, digit);
							if (b != null) {// Cas 3 colonnes 3 lignes
								String s = "Vous pouvez supprimer les " + Tools.getData(digit, grid.getType())
										+ " qui se situent dans les cases rouges car d'après SwordFish si on ne trouve que 2 candidats d'une même valeur dans 3 colonnes et en même temps dans 3 lignes alors on peut supprimer les autres candidats de cette valeur dans les lignes sauf dans les colonnes de départ";
								return createAnswer(s, b, row1, row2, row3, 3, digit);
							}
							int[] a = findCommonIntegers2(t2, row1, row2, row3, false, digit);
							if (a != null) { // Cas 3 colonnes 2 lignes 1 région
								String s = "Vous pouvez supprimer les " + Tools.getData(digit, grid.getType())
										+ " qui se situent dans les cases rouges car d'après SwordFish si on ne trouve que 2 candidats d'une même valeur dans 3 colonnes et en même temps dans 2 lignes et 1 région alors on peut supprimer les autres candidats de cette valeur dans ces 2 lignes et 1 région sauf dans les colonnes de départ";
								return createAnswer(s, a, row1, row2, row3, 4, digit);
							}
						}
					}
				}
			}
		}
		for (int digit = 1; digit <= SIZE; digit++) {
			for (int row1 = 0; row1 < SIZE - 2; row1++) {
				for (int row2 = row1 + 1; row2 < SIZE - 1; row2++) {
					for (int r1 = 0; r1 < SIZE; r1++) {
						for (int r2 = 0; r2 < SIZE; r2++) {
							ICase[] cells1 = grid.getLine(row1);
							ICase[] cells2 = grid.getLine(row2);
							ICase[] cells3 = grid.getRegion(row1, row2);
							ArrayList<ICase[]> list = new ArrayList<ICase[]>();
							list.add(cells1);
							list.add(cells2);
							list.add(cells3);
							ICase[] col1 = grid.getColumn(row1);
							ICase[] col2 = grid.getColumn(row2);
							ArrayList<ICase[]> list2 = new ArrayList<ICase[]>();
							list2.add(col1);
							list2.add(col2);
							list2.add(cells3);
							ArrayList<Integer>[] t = findIndices(list, digit);
							ArrayList<Integer>[] t2 = findIndices(list2, digit);
							if (t != null) {// 2 lignes 1 region -> 2 colonnes 2 régions
								int[] a = findCommonInteger3(t, row1, row2, r1, r2, true, digit);
								if (a != null) {
									String s = "Vous pouvez supprimer les " + Tools.getData(digit, grid.getType())
											+ " qui se situent dans les cases rouges car d'après SwordFish si on ne trouve que 2 candidats d'une même valeur dans 2 lignes et 1 région et en même temps dans 2 colonnes et 2 régions alors on peut supprimer les autres candidats de cette valeur dans ces 2 colonnes et 2 régions sauf dans les 2 lignes et région de départ";
									return createAnswer2(s, a, row1, row2, r1, r2, true, digit);
								}
							}
							if (t2 != null) {// 2 colonnes 1 region -> 2 lignes 2 régions
								int[] a = findCommonInteger3(t2, row1, row2, r1, r2, false, digit);
								if (a != null) {
									String s = "Vous pouvez supprimer les " + Tools.getData(digit, grid.getType())
											+ " qui se situent dans les cases rouges car d'après SwordFish si on ne trouve que 2 candidats d'une même valeur dans 2 colonnes et 1 région et en même temps dans 2 lignes et 2 régions alors on peut supprimer les autres candidats de cette valeur dans ces 2 lignes et 2 régions sauf dans les 2 colonnes et région de départ";
									return createAnswer2(s, a, row1, row2, r1, r2, false, digit);
								}
							}
						}
					}
				}
			}
		}
		return null;
	}

	// OUTIL
	/**
	 * Crée une réponse associé à la réponse trouvée pour les 4 premiers cas
	 */
	private Answer createAnswer(String msg, int[] tab, int r1, int r2, int r3, int n, int target) {
		Answer a = new Answer(msg);
		if (n == 1 || n == 2) {
			for (int i = 0; i < SIZE; i++) {
				a.addCasesOfProofs(r1, i);
			}
			for (int i = 0; i < SIZE; i++) {
				a.addCasesOfProofs(r2, i);
			}
			for (int i = 0; i < SIZE; i++) {
				a.addCasesOfProofs(r3, i);
			}
			for (int z = 0; z < 2; z++) {
				for (int i = 0; i < SIZE; i++) {
					Action act = new Action(i, tab[z], target, Command.REM_CANDIDATES, false);
					a.addRecommendedActions(act);
				}
			}
		}
		if (n == 3 || n == 4) {
			for (int i = 0; i < SIZE; i++) {
				a.addCasesOfProofs(i, r1);
			}
			for (int i = 0; i < SIZE; i++) {
				a.addCasesOfProofs(i, r2);
			}
			for (int i = 0; i < SIZE; i++) {
				a.addCasesOfProofs(i, r3);
			}
			for (int z = 0; z < 2; z++) {
				for (int i = 0; i < SIZE; i++) {
					Action act = new Action(tab[z], i, target, Command.REM_CANDIDATES, false);
					a.addRecommendedActions(act);
				}
			}
		}
		if (n == 1) {
			for (int i = 0; i < SIZE; i++) {
				Action act = new Action(i, tab[2], target, Command.REM_CANDIDATES, false);
				a.addRecommendedActions(act);
			}
		}
		if (n == 3) {
			for (int i = 0; i < SIZE; i++) {
				Action act = new Action(tab[2], i, target, Command.REM_CANDIDATES, false);
				a.addRecommendedActions(act);
			}
		}
		if (n == 2 || n == 4) {
			ArrayList<Point> points = getPointSameRegion(new Point(tab[2], tab[3]));
			for (Point point : points) {
				int x = (int) point.getX();
				int y = (int) point.getY();
				Action act = new Action(x, y, target, Command.REM_CANDIDATES, false);
				a.addRecommendedActions(act);
			}
		}
		return a;
	}

	/**
	 * Crée une réponse associé à la réponse trouvée pour les 2 derniers cas
	 */
	private Answer createAnswer2(String msg, int[] tab, int row1, int row2, int r1, int r2, boolean isLine,
			int target) {
		Answer a = new Answer(msg);
		ArrayList<Point> region = getPointSameRegion(new Point(r1, r2));
		for (Point point : region) {
			int x = (int) point.getX();
			int y = (int) point.getY();
			a.addCasesOfProofs(x, y);
		}
		if (isLine) {
			for (int i = 0; i < SIZE; i++) {
				a.addCasesOfProofs(row1, i);
				a.addCasesOfProofs(row2, i);
				Action act = new Action(i, tab[0], target, Command.REM_CANDIDATES, false);
				a.addRecommendedActions(act);
				act = new Action(i, tab[1], target, Command.REM_CANDIDATES, false);
				a.addRecommendedActions(act);
			}
		} else {
			for (int i = 0; i < SIZE; i++) {
				a.addCasesOfProofs(i, row1);
				a.addCasesOfProofs(i, row2);
				Action act = new Action(tab[0], i, target, Command.REM_CANDIDATES, false);
				a.addRecommendedActions(act);
				act = new Action(tab[1], i, target, Command.REM_CANDIDATES, false);
				a.addRecommendedActions(act);
			}
		}

		ArrayList<Point> points = getPointSameRegion(new Point(tab[2], tab[3]));
		for (Point point : points) {
			int x = (int) point.getX();
			int y = (int) point.getY();
			Action act = new Action(x, y, target, Command.REM_CANDIDATES, false);
			a.addRecommendedActions(act);
		}
		ArrayList<Point> points2 = getPointSameRegion(new Point(tab[4], tab[5]));
		for (Point point : points2) {
			int x = (int) point.getX();
			int y = (int) point.getY();
			Action act = new Action(x, y, target, Command.REM_CANDIDATES, false);
			a.addRecommendedActions(act);
		}

		return a;
	}

	/**
	 * Méthode qui prend un point et qui renvoie tous les points qui sont dans sa
	 * région.
	 */
	private ArrayList<Point> getPointSameRegion(Point p) {
		ArrayList<Point> p1 = new ArrayList<Point>();
		for (int x = 0; x < SIZE; x++) {
			for (int y = 0; y < SIZE; y++) {
				int x2 = (int) p.getX();
				int y2 = (int) p.getY();
				if (grid.isInSameRegion(x, y, x2, y2)) {
					p1.add(new Point(x2, y2));
				}
			}
		}
		return p1;
	}

	/**
	 * Méthode outil qui compte le nombre d'occurrence de num dans chaque
	 * liste de candidat de liste
	 */
	private int countOccurrences(ICase[] liste, int num) {
		int count = 0;
		for (int i = 0; i < liste.length; i++) {
			ICase c = liste[i];
			Set<Integer> s;
			if (!c.getIsFixed()) {
				s = c.getCandidates();
				if (s.contains(num)) {
					count++;
				}
			}
		}
		return count;
	}

	/**
	 * méthode qui fait la somme des cases de 3 tableaux d'entiers (reponse[i] =
	 * tab1[i] = tab2[i] = tab3[i])
	 */
	private int[] sommeTableaux(int[] tab1, int[] tab2, int[] tab3) {
		int[] resultat = new int[tab1.length];
		for (int i = 0; i < tab1.length; i++) {
			resultat[i] = tab1[i] + tab2[i] + tab3[i];
		}
		return resultat;
	}

	/**
	 * méthode qui prend des tableaux qui correspondent aux indices auxquels on
	 * trouve target
	 * dans chaque ligne (si isLine) ou colonne sinon et qui renvoie les indices de
	 * 3 colonnes ou ligne où on trouve
	 * target au moins 2 fois
	 */
	private int[] findCommonIntegers(ArrayList<Integer>[] lists, boolean isLine, int target) {
		ArrayList<Integer> commonIntegers = new ArrayList<Integer>();
		int[] tab1 = countOccu(lists[0]);
		int[] tab2 = countOccu(lists[1]);
		int[] tab3 = countOccu(lists[2]);
		int[] tab = sommeTableaux(tab1, tab2, tab3);
		int[] response = new int[3];
		for (int i = 0; i < tab.length; i++) {
			if (isLine) {
				if (tab[i] > 2 && commonIntegers.size() == 0 && countOccurrences(grid.getColumn(i), target) > tab[i]) {
					commonIntegers.add(i);
				}
			}
			if (!isLine) {
				if (tab[i] > 2 && commonIntegers.size() == 0 && countOccurrences(grid.getLine(i), target) > tab[i]) {
					commonIntegers.add(i);
				}
			}
		}
		for (int i = 0; i < tab.length; i++) {
			if (tab[i] > 2 && commonIntegers.size() != 3) {
				commonIntegers.add(i);
			}
		}
		if (commonIntegers.size() != 3) {
			return null;
		}
		for (int n = 0; n < 3; n++) {
			response[n] = commonIntegers.get(n);
		}
		return response;
	}

	/**
	 * méthode qui prend des tableaux de list d'indices où on trouve target dans les
	 * lignes (si isLine)
	 * ou colonnes a, b et c et qui renvoie une liste de Point
	 */
	private ArrayList<Point> tabToArray(ArrayList<Integer>[] lists, int a, int b, int c, boolean isLine) {
		ArrayList<Point> points = new ArrayList<Point>();
		for (int i = 0; i < 2; i++) {
			for (int x : lists[i]) {
				if (isLine) {
					if (i == 0) {
						points.add(new Point(a, x));
					}
					if (i == 1) {
						points.add(new Point(b, x));
					}
					if (i == 2) {
						points.add(new Point(c, x));
					}
				} else {
					if (i == 0) {
						points.add(new Point(x, a));
					}
					if (i == 1) {
						points.add(new Point(x, b));
					}
					if (i == 2) {
						points.add(new Point(x, c));
					}
				}
			}
		}
		return points;
	}

	/**
	 * méthode qui prend des tableaux de list d'indices où on trouve target dans les
	 * lignes (si isLine)
	 * ou colonnes a, b et dans la région c et qui renvoie une liste de Point
	 */
	private ArrayList<Point> tabToArray2(ArrayList<Integer>[] lists, int a, int b, int c, int d, boolean isLine) {
		ArrayList<Point> points = new ArrayList<Point>();
		for (int i = 0; i < 2; i++) {
			for (int x : lists[i]) {
				if (isLine) {
					if (i == 0) {
						points.add(new Point(a, x));
					}
					if (i == 1) {
						points.add(new Point(b, x));
					}
					if (i == 2) {
						int r_size = grid.getSize().getNumberRegionLines();
						points.add(new Point(c + (x / r_size), d + (x % r_size)));
					}
				} else {
					if (i == 0) {
						points.add(new Point(x, a));
					}
					if (i == 1) {
						points.add(new Point(x, b));
					}
					if (i == 2) {
						int r_size = grid.getSize().getNumberRegionLines();
						points.add(new Point(c + (x / r_size), d + (x % r_size)));
					}
				}
			}
		}
		return points;
	}

	/**
	 * méthode qui prend une liste de Point et qui renvoie une liste de région
	 * qui possède au moins 2 points
	 */
	private ArrayList<ArrayList<Point>> getRegions(ArrayList<Point> points) {
		ArrayList<ArrayList<Point>> regions = new ArrayList<ArrayList<Point>>();
		for (Point currentPoint : points) {
			int x = (int) currentPoint.getX();
			int y = (int) currentPoint.getY();
			boolean foundRegion = false;
			for (ArrayList<Point> region : regions) {
				int x1 = (int) region.get(0).getX();
				int y1 = (int) region.get(0).getY();
				if (grid.isInSameRegion(x, y, x1, y1)) {
					region.add(currentPoint);
					foundRegion = true;
				}
			}
			if (!foundRegion) {
				ArrayList<Point> newRegion = new ArrayList<Point>();
				newRegion.add(currentPoint);
				regions.add(newRegion);
			}
		}
		ArrayList<ArrayList<Point>> regions2 = new ArrayList<ArrayList<Point>>();
		for (int i = 0; i < regions.size(); i++) {
			if (regions.get(i).size() == 1) {
				regions2.add(regions.get(i));
			}
		}
		return regions2;
	}

	/**
	 * méthode qui prend des tableaux qui correspondent aux indices auxquels on
	 * trouve target
	 * dans chaque ligne (si isLine) ou colonne sinon et qui renvoie les indices de
	 * 2 colonnes ou lignes et 1 région où on trouve
	 * target au moins 2 fois
	 */
	private int[] findCommonIntegers2(ArrayList<Integer>[] lists, int a, int b, int c, boolean isLine, int target) {
		ArrayList<Integer> commonIntegers = new ArrayList<Integer>();
		for (int i : lists[0]) {
			for (int j = 1; j < lists.length; j++) {
				if (lists[j].contains(i)) {
					commonIntegers.add(i);
				}
			}
		}
		for (int i : lists[1]) {
			if (lists[2].contains(i)) {
				commonIntegers.add(i);
			}
		}
		if (commonIntegers.size() < 2) {
			return null;
		}
		int[] tab = new int[4];
		ArrayList<ArrayList<Point>> regions = getRegions(tabToArray(lists, a, b, c, isLine));
		for (ArrayList<Point> p1 : regions) {
			int x1 = (int) p1.get(0).getX();
			int y1 = (int) p1.get(0).getY();
			if (countOccurrences(grid.getRegion(x1, y1), target) > p1.size()) {
				tab[0] = commonIntegers.get(0);
				tab[1] = commonIntegers.get(1);
				tab[2] = (int) p1.get(0).getX();
				tab[3] = (int) p1.get(0).getY();
				return tab;
			}
		}
		return null;
	}

	/**
	 * méthode qui prend une liste de point et renvoie une liste de colonne (si
	 * isLine) ou ligne
	 * qui possède au moins 2 points
	 */
	private ArrayList<ArrayList<Point>> getColumn(ArrayList<Point> points, boolean isLine) {
		ArrayList<ArrayList<Point>> col = new ArrayList<ArrayList<Point>>();
		int x;
		int x1;
		for (Point currentPoint : points) {
			if (isLine) {
				x = (int) currentPoint.getY();
			} else {
				x = (int) currentPoint.getX();
			}
			boolean foundLine = false;
			for (ArrayList<Point> line : col) {
				if (isLine) {
					x1 = (int) line.get(0).getY();
				} else {
					x1 = (int) line.get(0).getX();
				}
				if (x == x1) {
					line.add(currentPoint);
					foundLine = true;
				}
			}
			if (!foundLine) {
				ArrayList<Point> newLine = new ArrayList<Point>();
				newLine.add(currentPoint);
				col.add(newLine);
			}
		}
		for (int i = 0; i < col.size(); i++) {
			if (col.get(i).size() == 1) {
				col.remove(i);
			}
		}
		return col;
	}

	/**
	 * méthode qui prend des tableaux qui correspondent aux indices auxquels on
	 * trouve target
	 * dans 2 lignes (si isLine) ou colonnes et 1 région et qui renvoie les indices
	 * de 2 colonnes ou lignes et 2 régions où on trouve
	 * target au moins 2 fois
	 */
	private int[] findCommonInteger3(ArrayList<Integer>[] lists, int a, int b, int c, int d, boolean isLine,
			int target) {
		ArrayList<Point> points = tabToArray2(lists, a, b, c, d, isLine);
		ArrayList<ArrayList<Point>> commonIntegers = getColumn(points, isLine);
		int[] tab = new int[6];
		ArrayList<Integer> response = new ArrayList<Integer>();
		ArrayList<ArrayList<Point>> regions = getRegions(points);
		boolean isValid = false;
		if (commonIntegers.size() < 2 || regions.size() < 2) {
			return null;
		}
		for (ArrayList<Point> p : commonIntegers) {
			if (isLine) {
				int y = (int) p.get(0).getY();
				if (countOccurrences(grid.getColumn(y), target) > p.size() && !isValid) {
					isValid = true;
					tab[0] = y;
				} else {
					response.add(y);
				}
			} else {
				int y = (int) p.get(0).getX();
				if (countOccurrences(grid.getLine(y), target) > p.size() && !isValid) {
					isValid = true;
					tab[0] = y;
				} else {
					response.add(y);
				}
			}
		}
		if (isValid) {
			tab[1] = response.get(0);
			tab[2] = (int) regions.get(0).get(0).getX();
			tab[3] = (int) regions.get(0).get(0).getY();
			tab[4] = (int) regions.get(1).get(0).getX();
			tab[5] = (int) regions.get(1).get(0).getY();
			return tab;
		} else {
			tab[0] = response.get(0);
			tab[1] = response.get(1);
		}
		for (int i = 0; i < regions.size(); i++) {
			ArrayList<Point> p1 = regions.get(i);
			int x1 = (int) p1.get(0).getX();
			int y1 = (int) p1.get(0).getY();
			if (grid.isInSameRegion(c, d, x1, y1)) {
				regions.remove(i);
			}
		}
		int z = -1;
		for (int i = 0; i < regions.size(); i++) {
			ArrayList<Point> p1 = regions.get(i);
			int x1 = (int) p1.get(0).getX();
			int y1 = (int) p1.get(0).getY();
			if (countOccurrences(grid.getRegion(x1, y1), target) > p1.size() && !isValid) {
				isValid = true;
				tab[2] = (int) p1.get(0).getX();
				tab[3] = (int) p1.get(0).getY();
				z = i;
			}
		}
		if (z == 0) {
			tab[4] = (int) regions.get(1).get(0).getX();
			tab[5] = (int) regions.get(1).get(0).getY();
		}
		if (z == 1) {
			tab[4] = (int) regions.get(0).get(0).getX();
			tab[5] = (int) regions.get(0).get(0).getY();
		}
		if (isValid) {
			return tab;
		}
		return null;
	}

	/**
	 * méthode qui prend une liste d'entier' et qui renvoie un tableau
	 * de l'occurences de chaque entier
	 */
	private int[] countOccu(ArrayList<Integer> t) {
		int[] tab = new int[SIZE];
		for (int k : t) {
			tab[k]++;
		}
		return tab;
	}

	/**
	 * méthode qui prend 3 tableaux de case de sudoku et un entier et qui renvoie
	 * les indices de chaque endroit où il trouve target dans chaque List et
	 * renvoie la réponse sous la forme de 3 list d'entier
	 */
	private ArrayList<Integer>[] findIndices(ArrayList<ICase[]> list, int target) {
		@SuppressWarnings("unchecked")
		ArrayList<Integer>[] tab = new ArrayList[3];
		int n = 0;
		for (ICase[] line : list) {
			ArrayList<Integer> indicesList = new ArrayList<Integer>();
			for (int i = 0; i < line.length; i++) {
				ICase cell = line[i];
				if (!cell.getIsFixed() && cell.getCandidates().contains(target)) {
					indicesList.add(i);
				}
			}
			if (indicesList.size() < 2) {
				return null;
			}
			tab[n] = indicesList;
			n++;
		}
		return tab;
	}

}
