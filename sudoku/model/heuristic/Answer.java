package sudoku.model.heuristic;

import java.util.ArrayList;
import java.util.List;

import sudoku.model.Action;
import sudoku.util.Point;

/**
 * La classe Answer généralise les réponses des heuristiques.
 * @author Khabouri Izana
 */
public class Answer {
	
	// ATTRIBUTS
	
	private List<Action> actions;
	private List<Point> proofs;
	private String message;
	private String author;
	
	// CONSTRUCTEUR 
	
	public Answer(String str) {
		this.actions = new ArrayList<Action>();
		this.proofs = new ArrayList<Point>();
		this.message = str;
		this.author = "";
	}
	
	// REQUETES
	
	/**
	 * Retourne toutes les actions associé à notre réponse.
	 */
	public List<Action> getAllRecommendedActions() {
		List<Action> act = new ArrayList<Action>();
		for (Action a : this.actions) {
			act.add(a);
		}
		return act;
	}
	
	/**
	 * Retourne toutes les case à illuminer.
	 */
	public List<Point> getCasesOfProofs() {
		List<Point> pts = new ArrayList<Point>();
		for (Point p : this.proofs) {
			pts.add(p);
		}
		return pts;
	}
	
	/**
	 * Retourne le message associé à cette réponse.
	 */
	public String computeMessage() {
		return this.message;
	}
	
	/**
	 * Retourne l'auteur de la réponse.
	 */
	public String getAuthor() {
		return this.author;
	}

	// COMMANDES
	
	/**
	 * Ajoute l'action a dans notre liste d'action.
	 */
	public void addRecommendedActions(Action a) {
		this.actions.add(a);
	}
	
	/**
	 * Ajoute la coordonnées dans notre liste de coordonnées.
	 */
	public void addCasesOfProofs(int i, int j) {
		this.proofs.add(new Point(i, j));
	}
	
	/**
	 * Met à jour l'auteur de la réponse.
	 */
	public void setAuthor(String s) {
		this.author = s;
	}
}
