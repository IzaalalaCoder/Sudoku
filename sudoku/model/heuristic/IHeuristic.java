package sudoku.model.heuristic;

/**
 * L'interface gère les différentes heuristiques.
 * 
 * @author Tanguy Cavelier
 */

public interface IHeuristic {

  // REQUETES

  /**
   * Méthode qui renvoie une chaine de caractères de ce que doit
   * faire l'utilisateur s'il y a une aide possible
   */
  public Answer compute();
}
