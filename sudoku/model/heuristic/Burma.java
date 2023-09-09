package sudoku.model.heuristic;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import sudoku.model.Action;
import sudoku.model.ICase;
import sudoku.model.IGrid;
import sudoku.model.info.Command;
import sudoku.util.Point;

public class Burma implements IHeuristic {
	
	// CONSTANTES 
	
	private final int NOT = 0;
	private final int LINE = 1;
	private final int COLUMN = 2;
	private final int NUMBER = 3;
	
	private final String COL = "Colonnes";
	private final String LIN = "Lignes";
	
	// ATTRIBUTS
	
	private IGrid grid;
	private Set<Integer> coordsUniteKeep;
	private Set<Integer> coordsUniteRemove;
	private int removeIn;
	private int value;
	
	private Map<Integer, Map<Integer, Integer>> occurenceColumn;
	private Map<Integer, Map<Integer, Integer>> occurenceLine;
	
	// CONSTRUCTEURS
	
	public Burma(IGrid model) {
		if (model == null) {
			throw new AssertionError();
		}
		this.grid = model.getCopy();
		this.coordsUniteKeep = new HashSet<Integer>();
		this.coordsUniteRemove = new HashSet<Integer>();
		this.removeIn = NOT;
		this.value = NOT;
	}
	
	// REQUETES

	@Override
	public Answer compute() {
		this.resolve();
		
		if (removeIn == NOT) {
			return null;
		}
		
		return createAnswer();
	}
	
	// OUTILS
	
	private Answer createAnswer() {
		final int size = this.grid.getSize().getSize();
		final String uniteKeep = removeIn == LINE ? COL : LIN; 
		final String uniteRemove = removeIn == LINE ? LIN : COL;
		String msg = "Nous avons 3 " + uniteKeep + ", dans lesquelles "
				+ "nous trouvons que " + NUMBER + " candidat suivant : " + value 
				+  ". De plus nous avons en commun exactement " + NUMBER 
				+ " " + uniteRemove + " qui possèdeux plus de " + NUMBER 
				+ " candidat suivant : " + value + ", alors il est donc possible "
				+ "de supprimer cette valeur dans les cases rouges.";
		Answer a = new Answer(msg);
		
		// création case à garder
		
		for (Integer i : coordsUniteKeep) {
			for (int index = 0; index < size; index++) {
				Point p = null;
				if (removeIn == LINE) {
					p = new Point(index, i);
				} else {
					p = new Point(i, index);
				}
				a.addCasesOfProofs(p.getX(), p.getY());
			}
		}
		
		// création case à supprimer
		
		for (Integer i : coordsUniteKeep) {
			for (int index = 0; index < size; index++) {
				Point p = null;
				if (removeIn == LINE) {
					p = new Point(i, index);
				} else {
					p = new Point(index, i);
				}
				Command cmd = Command.REM_CANDIDATES;
				Action act = new Action(p.getX(), p.getY(), value, cmd, false);
				a.addRecommendedActions(act);
			}
		}
		
		return a;
	}
	
	private void resolve() {
		this.readGrid();
		if (removeIn != NOT) {
			setAnswer();
		}
	}
	
	private void setAnswer() {
		final int size = grid.getSize().getSize();
		for (int index = 0; index < size; index++) {
			if (removeIn == LINE) {
				if (occurenceColumn.get(index).get(value) == NUMBER) {
					this.coordsUniteKeep.add(index);
				}
				if (occurenceLine.get(index).get(value) >= NUMBER) {
					this.coordsUniteRemove.add(index);
				}
			} else if (removeIn == COLUMN) {
				if (occurenceLine.get(index).get(value) == NUMBER) {
					this.coordsUniteKeep.add(index);
				}
				if (occurenceColumn.get(index).get(value) >= NUMBER) {
					this.coordsUniteRemove.add(index);
				}
			}
		}
	}
	
	private void readGrid() {
		final int size = grid.getSize().getSize();
		// Parcours colonne
		occurenceColumn = new HashMap<Integer, Map<Integer, Integer>>();
		for (int j = 0; j < size; j++) {
			occurenceColumn.put(j, initMap(size));
			for (int i = 0; i < size; i++) {
				for (int n = 1; n <= size; n++) {
					ICase c = grid.getCase(i, j);
					if (c.getCandidates().contains(n)) {
						int occurenceNumber = occurenceColumn.get(j).get(n);
						occurenceColumn.get(j).replace(n, occurenceNumber + 1);
					}
				}
			}
		}
		
		// Parcours ligne
		occurenceLine = new HashMap<Integer, Map<Integer, Integer>>();
		for (int i = 0; i < size; i++) {
			occurenceLine.put(i, initMap(size));
			for (int j = 0; j < size; j++) {
				for (int n = 1; n <= size; n++) {
					ICase c = grid.getCase(i, j);
					if (c.getCandidates().contains(n)) {
						int occurenceNumber = occurenceLine.get(i).get(n);
						occurenceLine.get(i).replace(n, occurenceNumber + 1);
					}
				}
			}
		}
		
		checkingResult(occurenceColumn, occurenceLine);
	}
	
	private void checkingResult(Map<Integer, Map<Integer, Integer>> c, 
			Map<Integer, Map<Integer, Integer>> l) {
		final int size = grid.getSize().getSize();
		
		for (int candidat = 1; candidat <= size; candidat++) {
			
			// compteur de lignes et de colonnes qui compte 
			// exactement NUMBER candidat 
			int countExactlyInLines = 0;
			int countExactlyInColumns = 0;
			
			// compteurs de lignes et de colonnes qui compte
			// plus de NUMBER candidat
			int countMoreInLines = 0;
			int countMoreInColumns = 0;
			
			// parcours colonne
			for (Integer j : c.keySet()) {
				if (c.get(j).get(candidat) == NUMBER) {
					countExactlyInColumns += 1;
				}
				if (c.get(j).get(candidat) > NUMBER) {
					countMoreInColumns += 1;
				}
			}
			
			// parcours ligne
			for (Integer i : l.keySet()) {
				if (c.get(i).get(candidat) == NUMBER) {
					countExactlyInLines += 1;
				}
				if (c.get(i).get(candidat) > NUMBER) {
					countMoreInLines += 1;
				}
			}
			
			// Analyse des résultat après parcours
			
			// Compteur de colonne ou lign ou est présent le candidat courant
			int countLines = countExactlyInLines + countMoreInLines;
			int countColumns = countExactlyInColumns + countMoreInColumns;
			if (countLines == NUMBER && countColumns == NUMBER) {
				this.value = candidat;
				if (countMoreInColumns == 0) {
					removeIn = LINE;
					return;
				} else if (countMoreInLines == 0) {
					removeIn = COLUMN;
					return;
				}
			} 
			value = NOT;
		}
	}
	
	private Map<Integer, Integer> initMap(int size) {
		Map<Integer, Integer> map = new HashMap<Integer, Integer>();
		for (int x = 1; x <= size; x++) {
			map.put(x, 0);
		}
		return map;
	}
}
