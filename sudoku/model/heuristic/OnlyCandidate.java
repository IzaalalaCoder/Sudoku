package sudoku.model.heuristic;

import java.util.Set;

import sudoku.model.Action;
import sudoku.model.ICase;
import sudoku.model.IGrid;

import sudoku.model.info.Command;

import sudoku.util.Tools;

/**
 * La classe OnlyCandidate implémente l'interface IHeuristic.
 * Elle représente l'heuristique : Un seul candidat.
 * Candidat Seul
 *  Pour chaque case du sudoku:
 *      Si la liste de candidat est de taille 1:
 *          Mettre le candidat dans la case
 * 
 * @author Cavelier Tanguy
 */
public class OnlyCandidate implements IHeuristic {

    // ATTRIBUT

    private final IGrid grille;

    // CONSTRUCTEUR

    /**
     * Constructeur de la classe OnlyCandidate qui prend une Grid en entrée
     */
    public OnlyCandidate(IGrid grid) {
        grille = grid;
    }

    // REQUETES

    @Override
    public Answer compute() {
        String msg = null;
        for (int i = 0; i < grille.getSize().getSize(); i++) {
            int[] t = findOnlyCandidate(grille.getLine(i));
            if (t != null) {
                msg = "La valeur qui se trouve dans la case verte est le "
                        + Tools.getData(t[0], grille.getType())
                        + " car il est le seul candidat de sa case\n";
                return createAnswer(i, t[1], t[0], msg);
            }
        }
        return null;
    }

    // OUTILS

    /**
     * Crée une réponse associé à la réponse trouvée.
     */
    private Answer createAnswer(int i, int j, int v, String msg) {
        Answer a = new Answer(msg);
        Action act = new Action(i, j, v, Command.SET_VALUE, false);
        a.addRecommendedActions(act);
        return a;
    }

    /**
     * Méthode qui prend une liste de case et une liste composée de :
     * -la valeur à compléter
     * -sa position dans la liste de case
     * la valeur correspond à la valeur qui se trouve seul dans une case du sudoku
     */
    private int[] findOnlyCandidate(ICase[] candidates) {
        int[] result = new int[2];
        for (int i = 0; i < candidates.length; i++) {
            ICase c = candidates[i];
            if (!c.getIsFixed()) {
                Set<Integer> s = c.getCandidates();
                if (s.size() == 1) {
                    result[0] = s.iterator().next();
                    result[1] = i;
                    return result;
                }
            }
        }
        return null;
    }
}
