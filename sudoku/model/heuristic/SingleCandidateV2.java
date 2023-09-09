package sudoku.model.heuristic;

import java.util.HashMap;
import java.util.Map;

import sudoku.model.Action;
import sudoku.model.IGrid;
import sudoku.model.info.Command;
import sudoku.model.regions.IRegion;

/**
 * La classe SingleCandidateV2 implémente l'interface IHeuristic.
 * Elle représente l'heuristique : Candidat unique.
 * @author Khabouri Izana
 */ 
public class SingleCandidateV2 implements IHeuristic {
	
	// ATTRIBUTS
	
	private final IGrid grille;
	
	// CONSTRUCTEURS
	
	public SingleCandidateV2(IGrid grid) {
		assert grid != null;
		this.grille = grid;
	}
	
	// REQUETES

	@Override
	public Answer compute() {
		// parcours LIGNE
		Pair<Point, Integer> answer = searchInLines();
		if (answer != null) {
			return response(answer);
		} 
		answer = searchInColumns();
		if (answer != null) {
			return response(answer);
		}
		answer = searchInRegions();
		if (answer != null) {
			return response(answer);
		}
		return null;
	}
	
	// OUTILS
	
	/**
	 * Retourne la réponse de l'heuristique dans le cas ou l'aide 
	 * n'a pas été vaine.
	 */
	private Answer response(Pair<Point, Integer> answer) {
		int x = answer.getKey().getX();
		int y = answer.getKey().getY();
		String msg = "La valeur qui se trouve à la ligne " + x
    		+ " et à la colonne " + y + " est le " + answer.getValue() + "\n";
		Answer a = new Answer(msg);
		Action act = new Action(x, y, answer.getValue(), Command.SET_VALUE, false);
		a.addRecommendedActions(act);
		return a;
	}
	
	/**
	 * Recherche dans toutes les lignes s'il existe un numéro 
	 * apparaissant qu'une seule fois.
	 * Retourne null si rien n'a été trouvé ou si une erreur de lecture a eu lieu.
	 * Retourne la coordonnée ainsi que la valeur en cas de d'aide trouvé.
	 */
	private Pair<Point, Integer> searchInLines() {
		Map<Integer, Integer> numberValue = initMap();
		for (int i = 0; i < grille.getSize().getSize(); i++) {
			for (int j = 0; j < grille.getSize().getSize(); j++) {
				if (!this.grille.getGrid()[i][j].getIsFixed()) {
					if (this.grille.getGrid()[i][j].getCandidates().size() == 0) {
						this.reset(numberValue);
						break;
					}
  				for (Integer x : this.grille.getGrid()[i][j].getCandidates()) {
  					numberValue.replace(x, numberValue.get(x) + 1);
  				}
				}
			}
			Integer x = foundSingle(numberValue);
			if (x != null) {
				return new Pair<Point, Integer>(new Point(i, getY(i, x)), x); 
			}
			numberValue = initMap();
		}
		return null;
	}
	
	/**
	 * Recherche dans toutes les colonnes s'il existe un numéro 
	 * apparaissant qu'une seule fois.
	 * Retourne null si rien n'a été trouvé ou si une erreur de lecture a eu lieu.
	 * Retourne la coordonnée ainsi que la valeur en cas de d'aide trouvé.
	 */
	private Pair<Point, Integer> searchInColumns() {
		Map<Integer, Integer> numberValue = initMap();
		for (int j = 0; j < grille.getSize().getSize(); j++) {
			for (int i = 0; i < grille.getSize().getSize(); i++) {
				if (!this.grille.getGrid()[i][j].getIsFixed()) {
					if (this.grille.getGrid()[i][j].getCandidates().size() == 0) {
						this.reset(numberValue);
						break;
					}
  				for (Integer x : this.grille.getGrid()[i][j].getCandidates()) {
  					numberValue.replace(x, numberValue.get(x) + 1);
  				}
				}
			}
			Integer x = foundSingle(numberValue);
			if (x != null) {
				return new Pair<Point, Integer>(new Point(getX(j, x), j), x); 
			}
			numberValue = initMap();
		}
		return null;
	}
	
	/**
	 * Recherche dans toutes les région s'il existe un numéro 
	 * apparaissant qu'une seule fois.
	 * Retourne null si rien n'a été trouvé ou si une erreur de lecture a eu lieu.
	 * Retourne la coordonnée ainsi que la valeur en cas de d'aide trouvé.
	 */
	private Pair<Point, Integer> searchInRegions() {
		for (IRegion r : this.grille.getSize().getTypeRegion()) {
			Pair<Point, Integer> response = this.readRegion(r);
			if (response != null) {
				return response;
			}
		}
		return null;
	}
	
	/**
	 * Recherche dans une seule région s'il existe un numéro 
	 * apparaissant qu'une seule fois.
	 * Retourne null si rien n'a été trouvé ou si une erreur de lecture a eu lieu.
	 * Retourne la coordonnée ainsi que la valeur en cas de d'aide trouvé.
	 */
	private Pair<Point, Integer> readRegion(IRegion r) {
		Map<Integer, Integer> numberValue = initMap();
		for (int i = r.getStartX(); i <= r.getEndX(); i++) {
			for (int j = r.getStartY(); j <= r.getEndY(); j++) {
				if (!this.grille.getGrid()[i][j].getIsFixed()) {
					if (this.grille.getGrid()[i][j].getCandidates().size() == 0) {
						return null;
					}
  				for (Integer x : this.grille.getGrid()[i][j].getCandidates()) {
  					numberValue.replace(x, numberValue.get(x) + 1);
  				}
				}
			}
		}
		Integer x = foundSingle(numberValue);
		if (x != null) {
			Point p = getCoordRegion(r, x);
			return new Pair<Point, Integer>(p, x); 
		}
		return null;
	}
	
	/**
	 * Retourne les coordonnées de x par rapport à la région r.
	 */
	private Point getCoordRegion(IRegion r, int x) {
		for (int i = r.getStartX(); i <= r.getEndX(); i++) {
			for (int j = r.getStartY(); j <= r.getEndY(); j++) {
					if (!this.grille.getGrid()[i][j].getIsFixed()) {
						if (this.grille.getGrid()[i][j].getCandidates().contains(x)) {
							return new Point(i, j);
						}
					}
			}
		}
		return null;
	}
	
	/**
	 * Retourne la colonne ou se trouve value dans la ligne line.
	 */
	private int getY(int line, int value) {
		for (int j = 0; j < this.grille.getSize().getSize(); j++) {
			if (!this.grille.getGrid()[line][j].getIsFixed()) {
				for (Integer x : this.grille.getGrid()[line][j].getCandidates()) {
					if (x == value) {
						return j;
					}
				}
			}
		}
		return -1;
	}
	
	/**
	 * Retourne la ligne ou se trouve value dans la colonne column.
	 */
	private int getX(int column, int value) {
		for (int i = 0; i < this.grille.getSize().getSize(); i++) {
			if (!this.grille.getGrid()[i][column].getIsFixed()) {
				for (Integer x : this.grille.getGrid()[i][column].getCandidates()) {
					if (x == value) {
						return i;
					}
				}
			}
		}
		return -1;
	}
	
	/**
	 * Retourne le candidat dont son occurence vaut 1.
	 */
	private Integer foundSingle(Map<Integer, Integer> map) {
		int candidateSingle = 0;
		Integer candidate = null;
		for (Integer x : map.keySet()) {
			
			if (map.get(x) == 1) {
				candidateSingle += 1;
				candidate = x;
			}
		}
		return (candidateSingle == 1 ? candidate : null);
	}

	/**
	 * Retourne la map initialisée avec des valeurs allant de 1 à la taille
	 * de la grille qui sont les clés.
	 * Ainsi que leur valeur associé qui vaut 0 par défaut, il s'agit
	 * de l'occurence de la clé.
	 */
	private Map<Integer, Integer> initMap() {
		Map<Integer, Integer> numberValue = new HashMap<Integer, Integer>();
		for (int i = 1; i <= this.grille.getSize().getSize(); i++) {
			numberValue.put(i, 0);
		}
		return numberValue;
	}
	
	/**
	 * Remet à 0 toutes les valeurs de chaque clé.
	 */
	private void reset(Map<Integer, Integer> m) {
		for (Integer k : m.keySet()) {
			m.replace(k, 0);
		}
	}
	
	// TYPES IMBRIQUEES
	
	/**
	 * Représente la classe Point dont les coordonnées 
	 * sont des constantes.
	 */
	private class Point {
		
		// ATTRIBUTS
		
		private int x;
		private int y;
		
		
		// CONSTRUCTEURS
		
		public Point(int a, int b) {
			this.x = a;
			this.y = b;
		}
		
		
		// REQUETES 
		
		public int getX() {
			return this.x;
		}
		
		public int getY() {
			return this.y;
		}
	}
	
	/**
	 * Représente la classe Point dont les coordonnées 
	 * sont des constantes.
	 */
	private class Pair<K, V> {
		
		// ATTRIBUTS
		
		private K key;
		private V value;
		
		// CONSTRUCTEUR
		
		public Pair(K key, V val) {
			this.key = key;
			this.value = val;
		}
		
		// REQUETES
		
		public K getKey() {
			return this.key;
		}
		
		public V getValue() {
			return this.value;
		}
		
	}
}
