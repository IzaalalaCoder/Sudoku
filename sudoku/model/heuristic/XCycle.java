package sudoku.model.heuristic;

import java.util.ArrayList;
import java.util.Set;

import sudoku.model.Action;
import sudoku.model.ICase;
import sudoku.model.IGrid;
import sudoku.model.info.Command;
import sudoku.util.Tools;

/**
 * La classe XCycle implémente l'interface IHeuristic.
 * Elle représente l'heuristique : Coloriage (ou X-Cycle).
 * Coloriage (ou XCycle)
 * Pour chaque n de 1 à SIZE:
 * 		Pour chaque case du sudoku:
 *      	Si case a pour candidat n
 *          	Rechercher une case2 dans une des ses unités qui possède n
 *          	Si case2 existe:
 *              	Colorier case en bleu et case 2 en vert 
 *              	Appel récursif:
 *              	Si case de la même unité qu'une case bleu est aussi une case qui est dans la même unité qu'une case verte:
 *                  	Supprimer n de la case qui fait l'intersection
 *               	Colorier en bleu toutes les cases qui possède n et qui sont dans la même unité que les cases vertes
 *               	Colorier en vert toutes les cases qui possède n et qui sont dans la même unité que les cases bleues
 *               	Si case bleu est dans la même unité qu'une autre case bleu:
 *                   	Supprimer tous les n des cases bleu
 *               	Si case verte est dans la même unité qu'une autre case verte:
 *                   	Supprimer tous les n des cases vertes
 *               	Si pas de nouvelle case colorier:
 *                   	changer de case de départ
 *               	Sinon:
 *                   	Retourner à appel récursif
 * 
 * @author Cavelier Tanguy
 */

public class XCycle implements IHeuristic {
	// CONSTANTES
	private final int SIZE;

	// ATTRIBUTS
	private final IGrid grille;

	/**
	 * Constructeur de la classe XCycle qui prend une Grid en entrée
	 */
	public XCycle(IGrid grid) {
		grille = grid;
		SIZE = grid.getSize().getSize();
	}

	/**
	 * Méthode qui renvoie une string de ce que doit faire l'utilisateur si
	 * il y a une aide possible
	 */
	public Answer compute() {
		for (int digit = 1; digit <= SIZE; digit++) {
			for (int x = 0; x < SIZE; x++) {
				for (int y = 0; y < SIZE; y++) {
					ICase[] cells1 = grille.getLine(x);
					ICase[] cells2 = grille.getColumn(y);
					ICase[] cells3 = grille.getRegion(x, y);
					int n = countOccurrences(cells1, digit);
					int n1 = countOccurrences(cells2, digit);
					int n2 = countOccurrences(cells3, digit);
					ArrayList<Point> vert = new ArrayList<Point>();
					ArrayList<Point> bleu = new ArrayList<Point>();
					if (n2 == 2) {
						ArrayList<Integer> p = findSetNumber(cells3, digit);
						int[] t = findregion(x, y);
						int r_size = grille.getSize().getNumberRegionLines();
						Point p1 = new Point(t[0] + (p.get(0) / r_size), t[1] + (p.get(0) % r_size));
						Point p2 = new Point(t[0] + (p.get(1) / r_size), t[1] + (p.get(1) % r_size));
						bleu.add(p1);
						vert.add(p2);
						ArrayList<ArrayList<Point>> s = addPointsInSameUnit(bleu, vert, digit);
						if (s != null) {
							return createAnswer(s, digit);
						}
					}
					vert = new ArrayList<Point>();
					bleu = new ArrayList<Point>();
					if (n == 2) {
						ArrayList<Integer> p = findSetNumber(cells1, digit);
						bleu.add(new Point(x, p.get(0)));
						vert.add(new Point(x, p.get(1)));
						ArrayList<ArrayList<Point>> s = addPointsInSameUnit(bleu, vert, digit);
						if (s != null) {
							return createAnswer(s, digit);
						}
					}
					vert = new ArrayList<Point>();
					bleu = new ArrayList<Point>();
					if (n1 == 2) {
						ArrayList<Integer> p = findSetNumber(cells2, digit);
						bleu.add(new Point(p.get(0), y));
						vert.add(new Point(p.get(1), y));
						ArrayList<ArrayList<Point>> s = addPointsInSameUnit(bleu, vert, digit);
						if (s != null) {
							return createAnswer(s, digit);
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
	private Answer createAnswer(ArrayList<ArrayList<Point>> list, int target) {
		String msg;
		if (list.size() == 4) {
			msg = "Coloriage des candidats " + Tools.getData(target, grille.getType())
					+ ". Il est possible de supprimer les candidats " + Tools.getData(target, grille.getType())
					+ " des cases en rouges, car il y en a 2 dans la même unité.";
		} else {
			msg = "Coloriage des candidats " + Tools.getData(target, grille.getType())
					+ ". Il est possible de supprimer les candidats " + Tools.getData(target, grille.getType())
					+ " des cases en rouge, à l'intersection des 2 autres couleurs.";
		}
		Answer a = new Answer(msg);
		if (list.size() == 4) {
			int n = list.get(3).get(0).getX();
			ArrayList<Point> tempList1 = list.get(n);
			ArrayList<Point> tempList3 = list.get(2);
			ArrayList<Point> tempList2;
			if (n == 0) {
				tempList2 = list.get(1);
			} else {
				tempList2 = list.get(0);
			}
			for (Point p : tempList1) {
				int x = (int) p.getX();
				int y = (int) p.getY();
				Action act = new Action(x, y, target, Command.REM_CANDIDATES, false);
				a.addRecommendedActions(act);
			}
			for (Point p : tempList2) {
				int x = (int) p.getX();
				int y = (int) p.getY();
				Action act = new Action(x, y, target, Command.SET_VALUE, false);
				a.addRecommendedActions(act);
			}
			Point p1 = tempList3.get(0);
			int x = (int) p1.getX();
			int y = (int) p1.getY();
			a.addCasesOfProofs(x, y);
		} else {
			ArrayList<Point> tempList1 = list.get(0);
			ArrayList<Point> tempList2 = list.get(1);
			ArrayList<Point> tempList3 = list.get(2);
			for (Point p : tempList3) {
				int x = (int) p.getX();
				int y = (int) p.getY();
				Action act = new Action(x, y, target, Command.REM_CANDIDATES, false);
				a.addRecommendedActions(act);
			}
			for (Point p : tempList1) {
				int x = (int) p.getX();
				int y = (int) p.getY();
				Action act = new Action(x, y, target, Command.SET_VALUE, false);
				a.addRecommendedActions(act);
			}
			for (Point p : tempList2) {
				int x = (int) p.getX();
				int y = (int) p.getY();
				a.addCasesOfProofs(x, y);
			}
		}
		return a;
	}

	/**
	 * méthode récursive qui prend 2 list de point et un entier qui est le candidat
	 * cible et qui renvoie une liste de 3 listes des points composé
	 * de la liste de point bleu et de la liste de point vert dans l'heuristique du
	 * coloriage et la liste des points qui sont dans les 2 listes en même
	 * temps ou 4 listes composé de la liste bleu, la liste verte, du point bleu qui
	 * est dans la même unité qu'un un point bleu si la 4ème liste a pour
	 * coordonnées (0,0) sinon le point vert qui est dans la même unité qu'un autre
	 * point vert, si on ne peut plus ajouter des points dans les 2 listes
	 * cela renvoie null ce qui veut dire qu'on a pas trouvé de point qui est dans
	 * les 2 listes
	 */
	private ArrayList<ArrayList<Point>> addPointsInSameUnit(ArrayList<Point> list1, ArrayList<Point> list2, int c) {
		ArrayList<Point> tempList1 = new ArrayList<Point>(list1);
		ArrayList<Point> tempList2 = new ArrayList<Point>(list2);
		ArrayList<Point> tempList3 = new ArrayList<Point>();
		ArrayList<Point> tempList4 = new ArrayList<Point>();
		ArrayList<ArrayList<Point>> response = new ArrayList<ArrayList<Point>>();
		for (Point point : tempList1) {
			ArrayList<Point> sameUnitPoints = getSameUnit(point, c);
			for (Point p1 : sameUnitPoints) {
				for (Point point2 : tempList2) {
					ArrayList<Point> sameUnitPoints2 = getSameUnit(point2, c);
					if (containsP(sameUnitPoints2, p1) && !containsP(tempList3, p1)) {
						tempList3.add(p1);
					}
				}
				if (tempList3.size() > 0) {
					response.add(tempList1);
					response.add(tempList2);
					response.add(tempList3);
					return response;
				}
				if (!containsP(tempList2, p1)) {
					tempList2.add(p1);
				}
				if (containsP(tempList1, p1)) {
					tempList3.add(p1);
					response.add(tempList1);
					response.add(tempList2);
					response.add(tempList3);
					tempList4.add(new Point(0, 0));
					response.add(tempList4);
					return response;
				}
			}
		}
		for (Point point : tempList2) {
			ArrayList<Point> sameUnitPoints = getSameUnit(point, c);
			for (Point p1 : sameUnitPoints) {
				for (Point point2 : tempList1) {
					ArrayList<Point> sameUnitPoints2 = getSameUnit(point2, c);
					if (containsP(sameUnitPoints2, p1) && !containsP(tempList3, p1)) {
						tempList3.add(p1);
					}
				}
				if (tempList3.size() > 0) {
					response.add(tempList1);
					response.add(tempList2);
					response.add(tempList3);
					return response;
				}
				if (!containsP(tempList1, p1)) {
					tempList1.add(p1);
				}
				if (containsP(tempList2, p1)) {
					tempList3.add(p1);
					response.add(tempList1);
					response.add(tempList2);
					response.add(tempList3);
					tempList4.add(new Point(1, 1));
					response.add(tempList4);
					return response;
				}
			}
		}
		if (tempList1.size() == list1.size() && tempList2.size() == list2.size()) {
			return null;
		}
		return addPointsInSameUnit(tempList1, tempList2, c);
	}

	/**
	 * méthode qui prend un point et un candidat et qui renvoie la liste de point
	 * qui se trouve dans la même unité que lui et qui possède c en candidat
	 */
	private ArrayList<Point> getSameUnit(Point p, int c) {
		ArrayList<Point> l = new ArrayList<Point>();
		int x = p.getX();
		int y = p.getY();
		ICase[] cells1 = grille.getLine(x);
		ICase[] cells2 = grille.getColumn(y);
		ICase[] cells3 = grille.getRegion(x, y);
		for (int n = 0; n < SIZE; n++) {
			if (!cells1[n].getIsFixed()) {
				if (cells1[n].getCandidates().contains(c) && y != n) {
					Point p1 = new Point(x, n);
					l.add(p1);
				}
			}
		}
		for (int n = 0; n < SIZE; n++) {
			if (!cells2[n].getIsFixed()) {
				if (cells2[n].getCandidates().contains(c) && x != n) {
					Point p1 = new Point(n, y);
					l.add(p1);
				}
			}
		}
		for (int n = 0; n < SIZE; n++) {
			if (!cells3[n].getIsFixed()) {
				int r_size = grille.getSize().getNumberRegionLines();
				int[] tab = findregion(x, y);
				int x1 = tab[0] + (n / r_size);
				int y1 = tab[1] + (n % r_size);
				if (cells3[n].getCandidates().contains(c) && (x != x1 || y != y1)) {
					Point p1 = new Point(x1, y1);
					l.add(p1);
				}
			}
		}
		return l;
	}

	/**
	 * Méthode outil qui prend une liste de Cell et un entier en entrée et
	 * qui renvoie la Cell où se trouve number
	 */
	private ArrayList<Integer> findSetNumber(ICase[] sets, int number) {
		ArrayList<Integer> s = new ArrayList<Integer>();
		for (int i = 0; i < sets.length; i++) {
			ICase c = sets[i];
			if (!c.getIsFixed()) {
				Set<Integer> s1 = c.getCandidates();
				if (s1.contains(number)) {
					s.add(i);
				}
			}
		}
		return s;
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
	 * méthode contains sur le type imbriqué Point et une liste de ce type imbriqué
	 */
	private boolean containsP(ArrayList<Point> list, Point point) {
		for (Point p : list) {
			if (p.equals(point)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * méthode qui prend les coordonées d'une case et qui renvoie la 1ère case de sa
	 * région
	 * (donc la case la plus en haut à gauche de sa région)
	 */
	private int[] findregion(int a, int b) {
		int[] tab = new int[2];
		for (int m = 0; m < SIZE; m++) {
			for (int n = 0; n < SIZE; n++) {
				if (grille.isInSameRegion(a, b, m, n)) {
					tab[0] = m;
					tab[1] = n;
					return tab;
				}
			}
		}
		return tab;
	}

	// CLASSE IMBRIQUEE
	/**
	 * classe imbriquée Point qui représente un point mais avec des entiers comme
	 * coordonnée
	 */
	public class Point {
		private int x;
		private int y;

		public Point(int x, int y) {
			this.x = x;
			this.y = y;
		}

		public int getX() {
			return x;
		}

		public boolean equals(Point p) {
			return p.getX() == x && p.getY() == y;
		}

		public int getY() {
			return y;
		}

		public void setX(int x) {
			this.x = x;
		}

		public void setY(int y) {
			this.y = y;
		}

		public String toString() {
			return "(" + x + ", " + y + ")";
		}
	}

}
