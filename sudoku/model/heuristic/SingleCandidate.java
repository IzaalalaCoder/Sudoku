package sudoku.model.heuristic;

import java.util.Set;

import sudoku.model.Action;
import sudoku.model.ICase;
import sudoku.model.IGrid;

import sudoku.model.info.Command;

import sudoku.util.Tools;

/**
 * La classe SingleCandidate implémente l'interface IHeuristic.
 * Elle représente l'heuristique : Candidat unique.
 * Candidat Unique
 * Pour chaque ligne, colonne et région :
 * 		Compter le nombre d'occurrence de chaque candidat
 *   	Si un entier à une occurrence de 1:
 *       	Le placer dans la cellule où il est présent
 * 
 * @author Cavelier Tanguy
 */
public class SingleCandidate implements IHeuristic {

    // ATTRIBUTS

    private final IGrid grille;

    // CONSTRUCTEUR

    /**
     * Constructeur de la classe SigleCandidate qui prend une Grid en entrée
     */
    public SingleCandidate(IGrid grid) {
        grille = grid;
    }

    // REQUETES

    /**
     * Méthode qui renvoie une string de ce que doit faire l'utilisateur si
     * il y a une aide possible
     */
    public Answer compute() {
        for (int i = 0; i < grille.getSize().getSize(); i++) {
            String msg = null;
            int[] t = findSingleCandidate(grille.getLine(i));
            if (t != null) {
                msg = "La valeur qui se trouve dans la case verte est le "
                        + Tools.getData(t[0], grille.getType()) + " car c'est le seul " +
                        Tools.getData(t[0], grille.getType()) + " de sa ligne \n";
                return createAnswer(i, t[1], t[0], 1, msg);
            }
            t = findSingleCandidate(grille.getColumn(i));
            if (t != null) {
                msg = "La valeur qui se trouve dans la case verte est le "
                        + Tools.getData(t[0], grille.getType()) + " car c'est le seul " +
                        Tools.getData(t[0], grille.getType()) + " de sa colonne \n";
                return createAnswer(t[1], i, t[0], 2, msg);
            }
            for (int j = 0; j < grille.getSize().getSize(); j++) {
                t = findSingleCandidate(grille.getRegion(i, j));
                if (t != null) {
                    int r_sizey = grille.getSize().getNumberRegionLines();
                    //.println("" + t[1]);
                    int x = (i + (t[1] / r_sizey));
                    int y = (j + (t[1] % r_sizey));
                    msg = "La valeur qui se trouve dans la case verte est le "
                            + Tools.getData(t[0], grille.getType()) + " car c'est le seul " +
                            Tools.getData(t[0], grille.getType()) + " de sa région \n";
                    return createAnswer(x, y, t[0], 3, msg);
                }

            }
        }
        return null;
    }

    // OUTILS

    /**
     * Crée une réponse associé à la réponse trouvée.
     */
    private Answer createAnswer(int i, int j, int v, int n, String msg) {
        Answer a = new Answer(msg);
        Action act = new Action(i, j, v, Command.SET_VALUE, false);
        a.addRecommendedActions(act);
        if (n == 1) {
            for (int x = 0; x < grille.getSize().getSize(); x++) {
                if (x != j) {
                    a.addCasesOfProofs(i, x);
                }
            }
        }
        if (n == 2) {
            for (int x = 0; x < grille.getSize().getSize(); x++) {
                if (x != i) {
                    a.addCasesOfProofs(x, j);
                }
            }
        }
        if (n == 3) {
            for (int x = 0; x < grille.getSize().getSize(); x++) {
                for (int y = 0; y < grille.getSize().getSize(); y++) {
                    if (grille.isInSameRegion(x, y, i, j) && (x != i || y != j)) {
                        a.addCasesOfProofs(x, y);
                    }
                }
            }
        }
        return a;
    }

    /**
     * Méthode qui prend une liste de Cell en entrée et cherche une Cell où
     * sa liste de candidat possède un candidat qui ne se trouve que dans cette
     * Cell et pas dans les autres
     */
    private int[] findSingleCandidate(ICase[] candidates) {
        int[] count = new int[candidates.length + 1];
        int[] result = new int[2];
        for (int i = 0; i < candidates.length; i++) {
            ICase c = candidates[i];
            if (!c.getIsFixed()) {
                Set<Integer> s = c.getCandidates();
                if (s.size() == 0) {
                    return null;
                }
                for (Integer element : s) {
                    int value = element.intValue();
                    count[value]++;
                }
            }
        }

        for (int i = 1; i <= candidates.length; i++) {
            if (count[i] == 1) {
                result[0] = i;
                result[1] = findSetNumber(candidates, i);
                return result;
            }
        }
        return null;
    }

    /**
     * Méthode outil qui prend une liste de Cell et un entier en entrée et
     * qui renvoie la Cell où se trouve number
     */
    private int findSetNumber(ICase[] sets, int number) {
        for (int i = 0; i < sets.length; i++) {
            ICase c = sets[i];
            if (!c.getIsFixed()) {
                Set<Integer> s = c.getCandidates();
                if (s.contains(number)) {
                    return i;
                }
            }
        }
        return -1;
    }
}
