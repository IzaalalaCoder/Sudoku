package sudoku.model.heuristic;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import sudoku.model.Action;
import sudoku.model.ICase;
import sudoku.model.IGrid;

import sudoku.model.info.Command;

import sudoku.model.regions.IRegion;

import sudoku.util.Point;
import sudoku.util.Tools;


/**
 * La classe BruteStrength implémente l'interface IHeuristic.
 * Elle représente l'heuristique : Essai de diverse possibilités.
 * Essai de diverses possibilités
 * Variable:
 * 	Liste de case qui n'ont de valeur fixé - lst
 * 	Position courante - pos
 * Algorithme:
 * 	Pour chaque candidat de lst(pos)
 * 		placer le candidat
 * 		supprimer le candidat dans la ligne, colonne, et région
 * 		Si la grille ne contient pas de case avec 0 candidats
 * 			continuer avec la case suivante et attendre la réponse
 * 			Si la résolution avec les autres case a été vaines
 * 				défaire et continuer avec le candidat suivant
 * 			Sinon
 * 				retourne vrai
 * 		Sinon 
 * 			défaire et continuer avec le candidat suivant
 * 		retourne faux
 * @author Khabouri Izana
 */ 
public class BruteStrength implements IHeuristic {
	
	// CONSTANTES
	
	private final int EMPTY = 0;
	
	// ATTRIBUTS
	
	private IGrid grid;
	private Point coordinateAnswer;
	private List<Point> coords;
	private int valueAnswer;
	
	// CONSTRUCTEUR
	
	public BruteStrength(IGrid m) {
		assert m != null;
		this.grid = m;
		this.coords = new ArrayList<Point>();
		this.coordinateAnswer = null;
		this.valueAnswer = EMPTY;
	}
	
	// REQUETES

	public IGrid isResolve() {
		IGrid g = grid.getCopy();
		this.searchCandidates(g);
		if (resolve(g, 0)) {
			return g;
		}
		return null;
	}
	
	@Override
	public Answer compute() {
		IGrid g = grid.getCopy();
		this.searchCandidates(g);
		boolean check = this.resolve(g, 0);
		if (check) {
			this.changeResponse(g);
		}
		String msg = check ? toAnswer() : null;
		Answer a = this.createAnswer(check, msg);
		return a;
	}
	
	// OUTILS
	
	/**
	 * Modifie l'état de la réponse.
	 */
	private void changeResponse(IGrid g) {
		final int index = Tools.getAlea(g.getSize().getSize());
		this.coordinateAnswer = this.coords.get(index);
		int x = coordinateAnswer.getX();
		int y = coordinateAnswer.getY();
		this.valueAnswer = g.getCase(x, y).getValue();
	}
	
	/**
	 * Récupére toutes les cases vides de g.
	 */
	private void searchCandidates(IGrid g) {
		this.coords.clear();
		final int size = g.getSize().getSize();
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				ICase c = g.getGrid()[i][j];
				if (!c.getIsFixed()) {
					this.coords.add(new Point(i, j));
				}
			}
		}
	}
	
	/**
	 * En fonction de check retourne une réponse.
	 */
	private Answer createAnswer(boolean check, String msg) {
		Answer a =  check ? new Answer(msg) : null;
		
		if (check) {
			int x = coordinateAnswer.getX();
			int y = coordinateAnswer.getY();
			Action act = new Action(x, y, this.valueAnswer, 
				Command.SET_VALUE, false);
			a.addRecommendedActions(act);
		}
		
		return a;
	}
	
	/**
	 * Créer une chaine de caractère représentant la réponse.
	 */
	private String toAnswer() {
		return 
			"La case à la position (" + (coordinateAnswer.getX() + 1)
			+ ", " + (this.coordinateAnswer.getY() + 1)
			+ "), tu peux placer la valeur " 
			+ Tools.getData(valueAnswer, grid.getType())
			+ "car la grille est résolue avec cette valeur"; 
	}
	
	/**
	 * Résout la grille.
	 */
	private boolean resolve(IGrid g, int position) {
		if (this.coords.size() == 0) {
			return false;
		}
 		Point p = this.coords.get(position);
		ICase c = g.getGrid()[p.getX()][p.getY()];
		for (Integer x : c.getCandidates()) {
			c.setValue(x);
			if (!checkCondition(g, p)) {
				c.unsetValue();
			} else {
				if (position + 1 == this.coords.size()) {
					return true;
				}
				if (this.checkGrid(g)) {
					if (!this.resolve(g, position + 1)) {
						c.unsetValue();
					} else {
						return true;
					}
				} else {
					c.unsetValue();
				}
			}
		}
		return false;
	}
	
	/**
	 * Retourne vrai si les conditions d'un sudoku est respecté faux sinon.
	 */
	private boolean checkCondition(IGrid g, Point p) {
		final int size = g.getSize().getSize();
		Set<Integer> number = new HashSet<Integer>();
		
		// Parcours ligne
		for (int j = 0; j < size; j++) {
			ICase c = g.getCase(p.getX(), j);
			if (c.getIsFixed()) {
				if (number.contains(c.getValue())) {
					return false;
				} else {
					number.add(c.getValue());
				}
			}
		}
		
		number.clear();
		
		// Parcours colonne
		for (int i = 0; i < size; i++) {
			ICase c = g.getCase(i, p.getY());
			if (c.getIsFixed()) {
				if (number.contains(c.getValue())) {
					return false;
				} else {
					number.add(c.getValue());
				}
			}
		}
		
		number.clear();
		
		// Parcours région
		IRegion r = g.searchRegion(p.getX(), p.getY());
		for (int i = r.getStartX(); i <= r.getEndX(); i++) {
			for (int j = r.getStartY(); j <= r.getEndY(); j++) {
				ICase c = g.getCase(i, j);
				if (c.getIsFixed()) {
					if (number.contains(c.getValue())) {
						return false;
					} else {
						number.add(c.getValue());
					}
				}
			}
		}
		
		return true;
	}
	
	/**
	 * Retourne faux si la grille présente une contradiction vrai sinon.
	 */
	private boolean checkGrid(IGrid g) {
		final int size = g.getSize().getSize();
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				ICase c = g.getGrid()[i][j];
				if (!c.getIsFixed() && c.getCandidates().size() == 0) {
					return false;
				}
			}
		}
		return true;
	}
	
}
