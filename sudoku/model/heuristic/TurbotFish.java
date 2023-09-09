package sudoku.model.heuristic;

import java.util.ArrayList;

import sudoku.model.Action;
import sudoku.model.ICase;
import sudoku.model.IGrid;
import sudoku.model.info.Command;
import sudoku.util.Tools;

/**
 * La classe TurbotFish implémente l'interface IHeuristic.
 * Elle représente l'heuristique : TurbotFish.
 * Turbot Fish
 * Pour chaque n de 1 à SIZE:
 * 		Pour chaque ensemble de 5 cases dans le sudoku:
 *      	Si on retrouve 2 lignes avec 2 cases chacune && 2 colonnes avec 2 cases chacune et 1 région avec 2 cases && les 5 cases ont n comme candidats:
 *          	(Appliquer le principe du coloriage sur ces 5 cases uniquement)
 *          	Prendre 2 cases dans une même unité (case1 et case2)
 *          	Colorier case en bleu et case 2 en vert 
 *              Appel récursif:
 *              Si case de la même unité qu'une case bleu est aussi une case qui est dans la même unité qu'une case verte:
 *                  Supprimer n de la case qui fait l'intersection
 *              Colorier en bleu toutes les cases qui possède n et qui sont dans la même unité que les cases vertes
 *              Colorier en vert toutes les cases qui possède n et qui sont dans la même unité que les cases bleues
 *              Si case bleu est dans la même unité qu'une autre case bleu:
 *                  Supprimer tous les n des cases bleu
 *              Si case verte est dans la même unité qu'une autre case verte:
 *                  Supprimer tous les n des cases vertes
 *              Si pas de nouvelle case colorier:
 *                  Changer de case de départ
 *              Sinon:
 *                  Retourner à appel récursif
 * 
 * @author Cavelier Tanguy
 */
public class TurbotFish implements IHeuristic {
	// CONSTANTES
	private final int SIZE;

	// ATTRIBUTS
	private final IGrid grille;

	/**
	 * Constructeur de la classe TurbotFish qui prend une Grid en entrée
	 */
	public TurbotFish(IGrid grid) {
		grille = grid;
		SIZE = grid.getSize().getSize();
	}

	/**
	 * Méthode qui renvoie une string de ce que doit faire l'utilisateur si
	 * il y a une aide possible
	 */
	public Answer compute() {
		for (int digit = 1; digit <= SIZE; digit++) {
			ArrayList<ArrayList<Point>> a = generateCombinations();
			for (ArrayList<Point> points : a) {
				if (isValid(points, digit)) {
					ArrayList<Point> z = countNbRegion(points);
					ArrayList<Point> bleu = new ArrayList<Point>();
					ArrayList<Point> vert = new ArrayList<Point>();
					bleu.add(z.get(0));
					vert.add(z.get(1));
					ArrayList<ArrayList<Point>> s = addPointsInSameUnit(points, bleu, vert, digit);
					if (s != null) {
						return createAnswer(s, digit);
					}
				}
			}
		}
		return null;
	}

	// OUTIL
	/**
	 * méthode qui créé une liste d'ensemble de 5 points chacun qui représente
	 * chaque combinaison de 5 points qu'on peut retrouver
	 * dans un sudoku
	 */
	private ArrayList<ArrayList<Point>> generateCombinations() {
		ArrayList<ArrayList<Point>> combinations = new ArrayList<ArrayList<Point>>();

		for (int x1 = 0; x1 < SIZE; x1++) {
			for (int y1 = 0; y1 < SIZE; y1++) {
				Point p1 = new Point(x1, y1);

				for (int x2 = x1; x2 < SIZE; x2++) {
					for (int y2 = 0; y2 < SIZE; y2++) {
						if (x2 == x1 && y2 <= y1) {
							continue;
						}
						Point p2 = new Point(x2, y2);

						for (int x3 = x2; x3 < SIZE; x3++) {
							for (int y3 = 0; y3 < SIZE; y3++) {
								if (x3 == x2 && y3 <= y2) {
									continue;
								}
								if (x3 == x1 && y3 <= y1) {
									continue;
								}
								Point p3 = new Point(x3, y3);

								for (int x4 = x3; x4 < SIZE; x4++) {
									for (int y4 = 0; y4 < SIZE; y4++) {
										if (x4 == x3 && y4 <= y3) {
											continue;
										}
										if (x4 == x2 && y4 <= y2) {
											continue;
										}
										if (x4 == x1 && y4 <= y1) {
											continue;
										}
										Point p4 = new Point(x4, y4);

										for (int x5 = x4; x5 < SIZE; x5++) {
											for (int y5 = 0; y5 < SIZE; y5++) {
												if (x5 == x4 && y5 <= y4) {
													continue;
												}
												if (x5 == x3 && y5 <= y3) {
													continue;
												}
												if (x5 == x2 && y5 <= y2) {
													continue;
												}
												if (x5 == x1 && y5 <= y1) {
													continue;
												}
												Point p5 = new Point(x5, y5);

												ArrayList<Point> combination = new ArrayList<Point>();
												combination.add(p1);
												combination.add(p2);
												combination.add(p3);
												combination.add(p4);
												combination.add(p5);
												combinations.add(combination);
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}
		return combinations;
	}

	/**
	 * Crée une réponse associé à la réponse trouvée pour les 4 premiers cas
	 */
	private Answer createAnswer(ArrayList<ArrayList<Point>> list, int target) {
		String msg;
		if (list.size() == 4) {
			msg = "Coloriage des candidats " + Tools.getData(target, grille.getType())
					+ ". Il est possible de supprimer les candidats " + Tools.getData(target, grille.getType())
					+ " des cases en rouges, car il y en a 2 dans la même unité.(TurbotFish)";
		} else {
			msg = "Coloriage des candidats " + Tools.getData(target, grille.getType())
					+ ". Il est possible de supprimer les candidats " + Tools.getData(target, grille.getType())
					+ " des cases en rouge, à l'intersection des 2 autres couleurs.(TurbotFish)";
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
	 * méthode récursive qui prend 2 list de point, un ensemble de 5 points et un
	 * entier qui est le candidat cible et qui renvoie une liste de 3 listes des
	 * points composé
	 * de la liste de point bleu et de la liste de point vert dans l'heuristique du
	 * coloriage et la liste des points qui sont dans les 2 listes en même
	 * temps ou 4 listes composé de la liste bleu, la liste verte, du point bleu qui
	 * est dans la même unité qu'un un point bleu si la 4ème liste a pour
	 * coordonnées (0,0) sinon le point vert qui est dans la même unité qu'un autre
	 * point vert, si on ne peut plus ajouter des points dans les 2 listes
	 * cela renvoie null ce qui veut dire qu'on a pas trouvé de point qui est dans
	 * les 2 listes
	 */
	private ArrayList<ArrayList<Point>> addPointsInSameUnit(ArrayList<Point> points, ArrayList<Point> list1,
			ArrayList<Point> list2, int c) {
		ArrayList<Point> tempList1 = new ArrayList<Point>(list1);
		ArrayList<Point> tempList2 = new ArrayList<Point>(list2);
		ArrayList<Point> tempList3 = new ArrayList<Point>();
		ArrayList<Point> tempList4 = new ArrayList<Point>();
		ArrayList<ArrayList<Point>> response = new ArrayList<ArrayList<Point>>();
		for (Point point : tempList1) {
			ArrayList<Point> sameUnitPoints = getSameUnit(points, point);
			for (Point p1 : sameUnitPoints) {
				for (Point point2 : tempList2) {
					ArrayList<Point> sameUnitPoints2 = getSameUnit(points, point2);
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
			ArrayList<Point> sameUnitPoints = getSameUnit(points, point);
			for (Point p1 : sameUnitPoints) {
				for (Point point2 : tempList1) {
					ArrayList<Point> sameUnitPoints2 = getSameUnit(points, point2);
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
		return addPointsInSameUnit(points, tempList1, tempList2, c);
	}

	/**
	 * méthode qui prend un ensemble de 5 points et 1 point de cette ensemble entier
	 * et qui renvoie les points qui sont dans la même unité de p et qui sont dans
	 * points
	 */
	private ArrayList<Point> getSameUnit(ArrayList<Point> points, Point p) {
		ArrayList<Point> l = new ArrayList<Point>();
		int x = p.getX();
		int y = p.getY();
		for (Point z : points) {
			int x1 = z.getX();
			int y1 = z.getY();
			if (!p.equals(z) && (x1 == x || y1 == y || grille.isInSameRegion(x, y, x1, y1))) {
				l.add(z);
			}
		}
		return l;
	}

	/**
	 * méthode qui prend un ensemble de 5 points et un candidat cible et qui vérifie
	 * que les 5 points respectent les
	 * conditions de TurbotFish, donc si dans les 5 points on retrouve 2 qui sont
	 * dans une même région, 2 lignes avec 2 points chacun
	 * et 2 colonnes avec 2 points chacun
	 */
	private boolean isValid(ArrayList<Point> points, int c) {
		for (Point p : points) {
			int x = p.getX();
			int y = p.getY();
			ICase cell = grille.getCase(x, y);
			if (cell.getIsFixed() || !cell.getCandidates().contains(c)) {
				return false;
			}
		}
		if (countNbRegion(points) == null || (countNbLine(points) != 2) || (countNbCol(points) != 2)) {
			return false;
		}
		return true;
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
	 * méthode qui prend une ensemble de point et qui compte le nombre de lignes
	 * où il y a 2 points
	 */
	private int countNbLine(ArrayList<Point> points) {
		int n = 0;
		int[] tab = new int[SIZE];
		for (Point p : points) {
			tab[p.getX()]++;
			if (tab[p.getX()] == 2) {
				n++;
			} else if (tab[p.getX()] == 3) {
				return 0;
			}
		}
		return n;
	}

	/**
	 * méthode qui prend une ensemble de point et qui compte le nombre de colonnes
	 * où il y a 2 points
	 */
	private int countNbCol(ArrayList<Point> points) {
		int n = 0;
		int[] tab = new int[SIZE];
		for (Point p : points) {
			tab[p.getY()]++;
			if (tab[p.getY()] == 2) {
				n++;
			} else if (tab[p.getY()] == 3) {
				return 0;
			}
		}
		return n;
	}

	/**
	 * méthode qui prend une ensemble de point et qui compte le nombre de région
	 * différente où se trouve les 5 points et si ils sont pas dans 4 régions
	 * différentes
	 * , donc ne respectent pas les conditions de TurbotFish alors retourne null
	 */
	private ArrayList<Point> countNbRegion(ArrayList<Point> points) {
		ArrayList<Point> regions = new ArrayList<Point>();
		Point z = new Point(0, 0);
		for (Point p : points) {
			int[] tab = findregion(p.getX(), p.getY());
			Point p1 = new Point(tab[0], tab[1]);
			if (containsP(regions, p1)) {
				z = p;
			} else {
				regions.add(p1);
			}
		}
		if (regions.size() == 4) {
			return getPointSameRegion(z, points);
		}
		return null;
	}

	/**
	 * méthode qui prend une ensemble de point et un point p et qui renvoie
	 * un ensemble composé de p et d'un point qui est dans sa région et dans la
	 * liste
	 * points si il est seul dans la liste, cela retourne null
	 */
	private ArrayList<Point> getPointSameRegion(Point p, ArrayList<Point> points) {
		ArrayList<Point> p1 = new ArrayList<Point>();
		p1.add(p);
		for (Point p2 : points) {
			int x1 = p2.getX();
			int y1 = p2.getY();
			int x2 = p.getX();
			int y2 = p.getY();
			if (grille.isInSameRegion(x1, y1, x2, y2)) {
				p1.add(p2);
				return p1;
			}
		}
		return null;
	}

	/**
	 * méthode qui prend les coordonnées d'un point et qui renvoie la case qui se
	 * situe en haut à gauche de la région donc la 1ère case
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
	 * classe imbriquée qui représente un Point mais avec des coordonnées
	 * entières et pas double
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

		public int getY() {
			return y;
		}

		public void setX(int x) {
			this.x = x;
		}

		public void setY(int y) {
			this.y = y;
		}

		public boolean equals(Point p) {
			return p.getX() == x && p.getY() == y;
		}

		public String toString() {
			return "(" + x + ", " + y + ")";
		}
	}
}
