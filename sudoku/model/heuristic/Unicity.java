package sudoku.model.heuristic;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import sudoku.model.Action;
import sudoku.model.ICase;
import sudoku.model.IGrid;

import sudoku.model.info.Command;

import sudoku.util.Point;
import sudoku.util.Tools;

/**
 * La classe Unicity implémente l'interface IHeuristic.
 * Elle représente l'heuristique : Unicité.
 * Unicité
 * Pour chaque 4 uplet de case qui forme un "carré" dans 2 régions:
 * 		Compter le nombre d'occurence de chaque nombre
 *   	Si exactement 2 entiers a et b possèdent 4 occurrences && un autre entier au moins une occurrence:
 *      	Mettre les cases qui sont dans la même région dans la même paire
 *      	Si une paire possède 2 cases avec uniquement a et b:
 *          	Si la 2nde paire possède une case avec uniquement a et b:
 *              	On retire a et b des candidats de la dernière case
 *          	Sinon retirer l'intersection des candidats des candidats de la 2nde paire aux autres cases de la région possèdant la 2nde paire
 * 
 * @author Cavelier Tanguy
 */
public class Unicity implements IHeuristic {

	// CONSTANTES
	private final int SIZE;
	private final int KEEP_MODE = 1;
	private final int SET_MODE = 0;

	// ATTRIBUTS

	private final IGrid grille;

	// CONSTRUCTEUR

	/**
	 * Constructeur de la classe Unicity qui prend une Grid en entrée
	 */
	public Unicity(IGrid grid) {
		grille = grid;
		SIZE = grid.getSize().getSize();
	}

	// REQUETES

	/**
	 * Méthode qui renvoie une string de ce que doit faire l'utilisateur si
	 * il y a une aide possible
	 */
	public Answer compute() {
		Set<PairofPair> set = getSquare();
		for (PairofPair i : set) {
			ICase c1 = i.getKey().getKey();
			ICase c2 = i.getKey().getValue();
			ICase c3 = i.getValue().getKey();
			ICase c4 = i.getValue().getValue();

			if (c1 != null && c2 != null && c3 != null && c4 != null
					&& !c1.getIsFixed() && !c2.getIsFixed()
					&& !c3.getIsFixed() && !c4.getIsFixed()) {

				Set<Integer> liste1 = c1.getCandidates();
				Set<Integer> liste2 = c2.getCandidates();
				Set<Integer> liste3 = c3.getCandidates();
				Set<Integer> liste4 = c4.getCandidates();
				int[] t = compterOccurrences(liste1, liste2, liste3, liste4);
				List<Point> coords = new ArrayList<Point>();
				List<Point> coords2 = new ArrayList<Point>();
				if (test(t)) {
					if (liste1.size() > 2 && liste2.size() == 2
							&& liste3.size() == 2 && liste4.size() == 2) {
						Integer[] a = occur1(t);
						String s;
						coords.add(new Point(i.getKey().getTKey()[0], i.getKey().getTKey()[1]));
						coords2.add(new Point(i.getKey().getTValue()[0], i.getKey().getTValue()[1]));
						coords2.add(new Point(i.getValue().getTKey()[0], i.getValue().getTKey()[1]));
						coords2.add(new Point(i.getValue().getTValue()[0], i.getValue().getTValue()[1]));
						if (a != null) {
							if (a.length == 1) {
								s = "La case en vert ne peut prendre que la valeur "
										+ Tools.getData(a[0], grille.getType())
										+ " car sur le carré de valeur en gris on retrouve les mêmes candidats "
										+ "sauf sur la case en vert donc on ne peut mettre que cette valeur car si on met l'une des 2 autres valeurs,"
										+ " on peut obtenir 2 solutions différentes valables, or, c'est impossible car un sudoku ne possède qu'une seule solution unique\n";
							} else {
								s = "La case en vert ne peut prendre que les valeurs ";
								for (Integer l : a) {
									s += Tools.getData(l, grille.getType()) + " ";
								}
								s += " car sur le carré de valeur en gris et vert, on retrouve les mêmes candidats sauf sur la case en vert "
										+ "donc on ne peut mettre que ces valeurs car si on met l'une des 2 autres valeurs, on peut obtenir 2 solutions différentes valables, "
										+ "or, c'est impossible car un sudoku ne possède qu'une seule solution unique\n";
							}
							return createAnswer(s, coords, coords2, a, SET_MODE);
						}
					}

					if (liste1.size() == 2 && liste2.size() > 2
							&& liste3.size() == 2 && liste4.size() == 2) {
						Integer[] a = occur1(t);
						String s;
						coords2.add(new Point(i.getKey().getTKey()[0], i.getKey().getTKey()[1]));
						coords.add(new Point(i.getKey().getTValue()[0], i.getKey().getTValue()[1]));
						coords2.add(new Point(i.getValue().getTKey()[0], i.getValue().getTKey()[1]));
						coords2.add(new Point(i.getValue().getTValue()[0], i.getValue().getTValue()[1]));
						if (a != null) {
							if (a.length == 1) {
								s = "La case en vert ne peut prendre que la valeur "
										+ Tools.getData(a[0], grille.getType())
										+ " car sur le carré de valeur en gris on retrouve les mêmes candidats "
										+ "sauf sur la case en vert donc on ne peut mettre que cette valeur car si on met l'une des 2 autres valeurs, "
										+ "on peut obtenir 2 solutions différentes valables, or, c'est impossible car un sudoku ne possède qu'une seule solution unique\n";
							} else {
								s = "La case en vert ne peut prendre que les valeurs ";
								for (Integer l : a) {
									s += Tools.getData(l, grille.getType()) + " ";
								}
								s += " car sur le carré de valeur en gris et vert, on retrouve les mêmes candidats sauf sur la case en vert "
										+ "donc on ne peut mettre que ces valeurs car si on met l'une des 2 autres valeurs, on peut obtenir 2 solutions différentes valables, "
										+ "or, c'est impossible car un sudoku ne possède qu'une seule solution unique\n";
							}
							return createAnswer(s, coords, coords2, a, SET_MODE);
						}
					}

					if (liste1.size() == 2 && liste2.size() == 2
							&& liste3.size() > 2 && liste4.size() == 2) {
						Integer[] a = occur1(t);
						String s;
						coords2.add(new Point(i.getKey().getTKey()[0], i.getKey().getTKey()[1]));
						coords2.add(new Point(i.getKey().getTValue()[0], i.getKey().getTValue()[1]));
						coords.add(new Point(i.getValue().getTKey()[0], i.getValue().getTKey()[1]));
						coords2.add(new Point(i.getValue().getTValue()[0], i.getValue().getTValue()[1]));
						if (a != null) {
							if (a.length == 1) {
								s = "La case en vert ne peut prendre que la valeur "
										+ Tools.getData(a[0], grille.getType())
										+ " car sur le carré de valeur en gris on retrouve les mêmes candidats "
										+ "sauf sur la case en vert donc on ne peut mettre que cette valeur car si on met l'une des 2 autres valeurs, "
										+ "on peut obtenir 2 solutions différentes valables, or, c'est impossible car un sudoku ne possède qu'une seule solution unique\n";
							} else {
								s = "La case en vert ne peut prendre que les valeurs ";
								for (Integer l : a) {
									s += Tools.getData(l, grille.getType()) + " ";
								}
								s += " car sur le carré de valeur en gris et vert, on retrouve les mêmes candidats sauf sur la case en vert donc on ne peut mettre que ces valeurs "
										+ "car si on met l'une des 2 autres valeurs, on peut obtenir 2 solutions différentes valables, or, "
										+ "c'est impossible car un sudoku ne possède qu'une seule solution unique\n";
							}
							return createAnswer(s, coords, coords2, a, SET_MODE);
						}
					}

					if (liste1.size() == 2 && liste2.size() == 2
							&& liste3.size() == 2 && liste4.size() > 2) {
						Integer[] a = occur1(t);
						String s = "";
						coords2.add(new Point(i.getKey().getTKey()[0], i.getKey().getTKey()[1]));
						coords2.add(new Point(i.getKey().getTValue()[0], i.getKey().getTValue()[1]));
						coords2.add(new Point(i.getValue().getTKey()[0], i.getValue().getTKey()[1]));
						coords.add(new Point(i.getValue().getTValue()[0], i.getValue().getTValue()[1]));
						if (a != null) {
							if (a.length == 1) {
								s = "La case en vert ne peut prendre que la valeur "
										+ Tools.getData(a[0], grille.getType())
										+ " car sur le carré de valeur en gris on retrouve les mêmes candidats "
										+ "sauf sur la case en vert donc on ne peut mettre que cette valeur car si on met l'une des 2 autres valeurs, "
										+ "on peut obtenir 2 solutions différentes valables, or, c'est impossible car un sudoku ne possède qu'une seule solution unique\n";
							} else {
								s = "La case en vert ne peut prendre que les valeurs ";
								for (Integer l : a) {
									s += Tools.getData(l, grille.getType()) + " ";
								}
								s += " car sur le carré de valeur en gris et vert, on retrouve les mêmes candidats sauf sur la case en vert "
										+ "donc on ne peut mettre que ces valeurs car si on met l'une des 2 autres valeurs, on peut obtenir 2 solutions différentes valables, "
										+ "or, c'est impossible car un sudoku ne possède qu'une seule solution unique\n";
							}
							return createAnswer(s, coords, coords2, a, SET_MODE);
						}
					}
					//

					if (liste1.size() == 2 && liste2.size() == 2
							&& liste3.size() > 2 && liste4.size() > 2) {
						Point p = new Point(i.getValue().getTValue()[0], i.getValue().getTValue()[1]);
						Integer[] a = occur2(t, p);
						String s = "";
						coords.add(p);
						coords.add(new Point(i.getValue().getTKey()[0], i.getValue().getTKey()[1]));
						coords2.add(new Point(i.getKey().getTKey()[0], i.getKey().getTKey()[1]));
						coords2.add(new Point(i.getKey().getTValue()[0], i.getKey().getTValue()[1]));
						if (a != null) {
							if (a.length == 1) {
								s = "La valeur "
										+ Tools.getData(a[0], grille.getType())
										+ " ne peut se trouver que dans les cases vertes et pas dans les cases rouges dans leur région "
										+ "car sur le carré de valeur en gris et vert, on retrouve les mêmes candidats sauf sur les cases en vert "
										+ "donc on ne peut mettre que cette valeur car si on met l'une des 2 autres valeurs, on peut obtenir 2 solutions différentes valables,"
										+ " or, c'est impossible car un sudoku ne possède qu'une seule solution unique\n";
							} else {
								s = "Les valeurs ";
								for (Integer l : a) {
									s += Tools.getData(l, grille.getType()) + " ";
								}
								s += "ne peuvent se trouver que dans les cases vertes et pas dans les cases rouges dans leur région car sur le carré de valeur en gris et vert, "
										+ "on retrouve les mêmes candidats sauf sur les cases en vert donc on ne peut mettre que ces valeurs car si on met l'une des 2 autres valeurs,"
										+ " on peut obtenir 2 solutions différentes valables, or, c'est impossible car un sudoku ne possède qu'une seule solution unique\n";
							}
							return createAnswer(s, coords, coords2, a, KEEP_MODE);
						}
					}

					if (liste1.size() > 2 && liste2.size() > 2
							&& liste3.size() == 2 && liste4.size() == 2) {
						Point p = new Point(i.getKey().getTValue()[0], i.getKey().getTValue()[1]);
						Integer[] a = occur2(t, p);
						String s = "";
						coords.add(p);
						coords.add(new Point(i.getKey().getTKey()[0], i.getKey().getTKey()[1]));
						coords2.add(new Point(i.getValue().getTKey()[0], i.getValue().getTKey()[1]));
						coords2.add(new Point(i.getValue().getTValue()[0], i.getValue().getTValue()[1]));
						if (a != null) {
							if (a.length == 1) {
								s = "La valeur "
										+ Tools.getData(a[0], grille.getType())
										+ " ne peut se trouver que dans les cases vertes et pas dans les cases rouges dans leur région "
										+ "car sur le carré de valeur en gris et vert, on retrouve les mêmes candidats sauf sur les cases en vert "
										+ "donc on ne peut mettre que cette valeur car si on met l'une des 2 autres valeurs, on peut obtenir 2 solutions différentes valables,"
										+ " or, c'est impossible car un sudoku ne possède qu'une seule solution unique\n";
							} else {
								s = "Les valeurs ";
								for (Integer l : a) {
									s += Tools.getData(l, grille.getType()) + " ";
								}
								s += "ne peuvent se trouver que dans les cases vertes et pas dans les cases rouges dans leur région car sur le carré de valeur en gris et vert,"
										+ " on retrouve les mêmes candidats sauf sur les cases en vert donc on ne peut mettre que ces valeurs car si on met l'une des 2 autres valeurs,"
										+ " on peut obtenir 2 solutions différentes valables, or, c'est impossible car un sudoku ne possède qu'une seule solution unique\n";
							}
							return createAnswer(s, coords, coords2, a, KEEP_MODE);
						}
					}
				}
			}
		}
		return null;
	}

	/**
	 * Retourne une réponse correspondant à la fixation d'une valeur.
	 */
	private Answer createAnswer(String str, List<Point> c, List<Point> c2, Integer[] v, int m) {
		Answer a = new Answer(str);
		for (Point p : c) {
			final int i = p.getX();
			final int j = p.getY();
			for (Integer x : v) {
				Action act = new Action(i, j, x, Command.SET_VALUE, false);
				a.addRecommendedActions(act);
			}
		}
		if (m == KEEP_MODE) {
			final int i = c.get(0).getX();
			final int j = c.get(0).getY();
			final int k = c.get(1).getX();
			final int l = c.get(1).getY();
			for (int x = 0; x < SIZE; x++) {
				for (int y = 0; y < SIZE; y++) {
					if (grille.isInSameRegion(i, j, x, y) && (i != x || j != y) && (k != x || l != y)) {
						for (Integer x1 : v) {
							Action act = new Action(x, y, x1, Command.REM_CANDIDATES, false);
							a.addRecommendedActions(act);
						}
					}
				}
			}
		}
		for (Point p : c2) {
			final int i = p.getX();
			final int j = p.getY();
			a.addCasesOfProofs(i, j);
		}
		return a;
	}

	/**
	 * Fonction prend une tableau d'entier qui représente le nombre d'occurence de
	 * chaque entier de 0
	 * à SIZE et qui renvoie un tableau d'entier avec les entiers qui ont 1
	 * occurence mais qui apparaissent
	 * dans le reste de la région
	 */
	private Integer[] occur1(int[] t) {
		Set<Integer> p = new HashSet<Integer>();
		for (int i = 0; i < SIZE; i++) {
			if (t[i] == 1) {
				p.add(i + 1);
			}
		}
		if (p.size() == 0) {
			return null;
		}
		return p.toArray(new Integer[0]);
	}

	/**
	 * Fonction prend une tableau d'entier qui représente le nombre d'occurence de
	 * chaque entier de 0
	 * à SIZE et qui renvoie un tableau d'entier avec les entiers qui ont 2
	 * occurences ou null
	 */
	private Integer[] occur2(int[] t, Point p1) {
		int x = p1.getX();
		int y = p1.getY();
		Set<Integer> p = new HashSet<Integer>();
		for (int i = 0; i < SIZE; i++) {
			if (t[i] == 2 && countOccurrences(grille.getRegion(x, y), i + 1) > 2) {
				p.add(i + 1);
			}
		}
		if (p.size() == 0) {
			return null;
		} else {
			return p.toArray(new Integer[0]);
		}
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
	 * Fonction prend une tableau d'entier qui réprésente le nombre d'occurrence de
	 * chaque entier de 0 à SIZE
	 * et qui renvoie si il y a exactement 2 entiers qui ont chacun 4 occurrences
	 */
	private boolean test(int[] t) {
		int count = 0;
		for (int i = 0; i < SIZE; i++) {
			if (t[i] == 4) {
				count++;
			}
		}
		if (count == 2) {
			return true;
		}
		return false;
	}

	/**
	 * Fonction prend un 4 Set et qui renvoie un tableau d'entier qui réprésente
	 * le nombre d'occurrence de chaque entier de 0 à SIZE qui sont dans les 4 Set
	 * en entrée
	 */
	private int[] compterOccurrences(Set<Integer> liste1, Set<Integer> liste2,
			Set<Integer> liste3, Set<Integer> liste4) {
		int[] occurrences = new int[SIZE];

		for (int i = 1; i <= SIZE; i++) {
			for (Integer entier : liste1) {
				if (entier == i) {
					occurrences[i - 1]++;
				}
			}
			for (Integer entier : liste2) {
				if (entier == i) {
					occurrences[i - 1]++;
				}
			}
			for (Integer entier : liste3) {
				if (entier == i) {
					occurrences[i - 1]++;
				}
			}
			for (Integer entier : liste4) {
				if (entier == i) {
					occurrences[i - 1]++;
				}
			}
		}
		return occurrences;
	}

	/**
	 * Fonction qui renvoie tous les carrés d'une grille de sudoku (4 cases de
	 * sudoku
	 * si représente un carré dans un sudoku graphiquement)
	 */
	private Set<PairofPair> getSquare() {
		Set<PairofPair> result = new HashSet<PairofPair>();
		for (int x1 = 0; x1 < SIZE; x1++) {
			for (int x2 = x1 + 1; x2 < SIZE; x2++) {
				for (int y1 = 0; y1 < SIZE; y1++) {
					for (int y2 = y1 + 1; y2 < SIZE; y2++) {
						if (!grille.isInSameRegion(x1, y1, x2, y2)) {
							Pair p1, p2;
							ICase c1 = grille.getCase(x1, y1);
							ICase c2 = grille.getCase(x1, y2);
							ICase c3 = grille.getCase(x2, y1);
							ICase c4 = grille.getCase(x2, y2);
							int[] t1 = { x1, y1 };
							int[] t2 = { x1, y2 };
							int[] t3 = { x2, y1 };
							int[] t4 = { x2, y2 };
							if (grille.isInSameRegion(x1, y1, x1, y2)) {
								p1 = new Pair(c1, t1, c2, t2);
								p2 = new Pair(c3, t3, c4, t4);
								PairofPair p = new PairofPair(p1, p2);
								result.add(p);
							} else if (grille.isInSameRegion(x1, y1, x2, y1)) {
								p1 = new Pair(c1, t1, c3, t3);
								p2 = new Pair(c2, t2, c4, t4);
								PairofPair p = new PairofPair(p1, p2);
								result.add(p);
							}
						}
					}
				}
			}
		}
		return result;
	}

	// TYPE IMBRIQUE

	/**
	 * Type qui représente un pair de Cell avec un tableau qui contient les
	 * coordonnées de chaque Cell
	 */
	public class Pair {

		// ATTRIBUTS
		ICase elt1;
		ICase elt2;
		int[] tKey;
		int[] tValue;

		// CONSTRUCTEUR

		public Pair(ICase key, int[] t1, ICase value, int[] t2) {
			elt1 = key;
			elt2 = value;
			tKey = t1;
			tValue = t2;
		}

		// REQUETES

		public ICase getKey() {
			return elt1;
		}

		public ICase getValue() {
			return elt2;
		}

		public int[] getTKey() {
			return tKey;
		}

		public int[] getTValue() {
			return tValue;
		}
	}

	/**
	 * Type qui représente une paire de Pair de Cell qui réprésente graphiquement un
	 * Carré
	 */
	public class PairofPair {

		// ATTRIBUTS

		Pair elt1;
		Pair elt2;

		// CONSTRUCTEUR

		public PairofPair(Pair key, Pair value) {
			elt1 = key;
			elt2 = value;
		}

		// REQUETES

		public Pair getKey() {
			return elt1;
		}

		public Pair getValue() {
			return elt2;
		}

	}
}
