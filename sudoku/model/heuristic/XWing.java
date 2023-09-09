package sudoku.model.heuristic;

import sudoku.model.Action;
import sudoku.model.ICase;
import sudoku.model.IGrid;

import sudoku.model.info.Command;

import sudoku.util.Point;
import sudoku.util.Tools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * La classe XWing implémente l'interface IHeuristic.
 * Elle représente l'heuristique : X-Wing.
 * @author Valentin Gangloff
 */ 
public class XWing implements IHeuristic {

	// ATTRIBUTS

	private final IGrid model;
	private int checkedCandidate;
	List<Point> Keep;
	List<Point> Remove;

	
	/**
	 * 
	 * La classe Jellyfish implémente l'heuristique ainsi nommée Jellyfish.
	 * Son unique méthode publique compute renverra un objet de type Answer contenant
	 * une suggestion de modifications sur la grille en adéquation avec l'application
	 * de l'heuristique.
	 * @author Valentin GANGLOFF
	 */

	//CONSTRUCTEUR 
	
	public XWing(IGrid m) {
		if (m == null) {
			throw new AssertionError("XWing constr arg null");
		}

		model = m;
	}


	// REQUETES

	@Override
	public Answer compute() {
		ICase[][] cells = model.getGrid();
		Map<Integer, List<Point>> candCols = checkCandidates(cells);

		if (candCols == null) {
			return null;
		}

		purify(candCols);
		String mes = message(candCols);
		Answer answer = new Answer(mes);

		for (Point p : Keep) {
			answer.addCasesOfProofs(p.getY(), p.getX());
		}

		if (Remove.size() != 0) {
			for (Point p: Remove) {
				answer.addRecommendedActions(new Action(p.getY(), p.getX(), 
						this.checkedCandidate,
				Command.REM_CANDIDATES, false));
			}
		}
		return answer;
	}

	// OUTILS

	private void purify(Map<Integer, List<Point>> candCols) {
		Set<Integer> keySet = candCols.keySet();
		Integer [] keys = keySet.toArray(new Integer[keySet.size()]);
		List<Point> frst = candCols.get(keys[0]);
		List<Point> scnd = candCols.get(keys[1]);
		Keep = new ArrayList<Point>();
		Remove = new ArrayList<Point>();
		int check = 0;
		for (Point k : frst) {
			for (Point k2 : scnd) {
				if(k.getY() == k2.getY()) {
					Keep.add(k);
					Keep.add(k2);
					++check;
					break;
				}
				
			}
			if(check >= 2) {
				Remove.addAll(frst);
				Remove.addAll(scnd);
				Remove.removeAll(Keep);
				return;
			}
		}
	}


	private Map<Integer, List<Point>> checkCandidates(ICase[][] cells) {
		Map<Integer, List<Point>> candidatesForCols;
		List<Point> foundCells;

		for (int n = 1; n <= model.getSize().getSize(); ++n) {
			checkedCandidate = n;
			candidatesForCols = new HashMap<Integer, List<Point>>();
			for (int j = 0; j < model.getSize().getSize(); ++j) {
				foundCells = new ArrayList<Point>();
				for (int i = 0; i < model.getSize().getSize(); ++i) {
					if (cells[i][j].getCandidates().contains(n)) {
						foundCells.add(new Point(j, i));
					}
				}
				if (foundCells.size() >= 2) {
					candidatesForCols.put(j, foundCells);
				}
			}
			
			if (candidatesForCols.keySet().size()>= 2) {
				Set<Integer> keySet = candidatesForCols.keySet();
				Integer [] keys = keySet.toArray(new Integer[keySet.size()]);
				for (int i = 0; i < keys.length; ++i) {
					for (int j = i + 1; j < keys.length; ++j) {
						List<Point> frst = candidatesForCols.get(keys[i]);
						List<Point> scnd = candidatesForCols.get(keys[j]);
						int matchs = 0;
						for(Point k : frst) {

							for(Point k2 : scnd) {
								if (k2.getY() == k.getY()) {
									++matchs;
	
								}
							}

						}
						if (matchs >= 2) {
							Map<Integer, List<Point>> finalMap = new HashMap<Integer,List<Point>>();
							finalMap.put(keys[i], frst);
							finalMap.put(keys[j], scnd);
							return finalMap;
						}
					}
				}
			}
		}

		return null;
	}

	private String message(Map<Integer, List<Point>> candLines) {
		String msg = "Avec la valeur " + Tools.getData(checkedCandidate, model.getType());
		
		msg = msg + " presente en double occurences aux colonnes ";
		for(int i : candLines.keySet()) {
			msg = msg + i + ", ";
		}

		msg = msg 
		+ "nous avons donc un XWing";

		if (Remove.size() == 0) {
			msg = msg + " cependant, aucune autre occurence se trouve sur les lignes externes.";
		} else {
			msg = msg + ", ce qui veut dire que les " 
			+ Tools.getData(checkedCandidate, model.getType()) + " en positions ";

			for (Point p : Remove) {
				msg = msg + "(" + p.getX() + "," + p.getY() + ") ";
			}

			msg = msg + "ne peuvent être candidats."; 
		}
		return msg;
	}
}
