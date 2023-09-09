package sudoku.model.heuristic;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import sudoku.model.IGrid;
import sudoku.model.Action;
import sudoku.model.ICase;

import sudoku.model.info.Command;
import sudoku.util.Tools;

/**
 * La classe TwinsTriplets implémente l'interface IHeuristic.
 * Elle représente l'heuristique : Jumeaux et triplés.
 * JumeauxTriplé 
 * Pour chaque région:
 * 		Pour chaque ligne (ou colonne) de la région:
 *      	Compter le nombre d'occurence de chaque candidat
 *       	Si un entier à une occurrence d'au moins 2:
 *          	Si vérifier qu'il n'apparait pas dans le reste de la région && Vérifier qu'il apparait sur le reste de la ligne (ou colonne) du sudoku:
 *              	Supprimer le candidat du reste de la ligne ou de la colonne du sudoku or de la ligne de la région
 * 
 * @author Cavelier Tanguy
 */
public class TwinsTriplets implements IHeuristic {

    // ATTRIBUTS CONSTANTES

    private final int NOT_HERE = -1;

    // ATTRIBUT

    private final IGrid grille;

    // CONSTRUCTEUR

    /**
     * Constructeur de la classe TwinsTriplets qui prend une Grid en entrée
     */
    public TwinsTriplets(IGrid grid) {
        grille = grid;
    }

    // REQUETES

    @Override
    public Answer compute() {
        for (int i = 0; i < grille.getSize().getSize(); i++) {
            for (int j = 0; j < grille.getSize().getSize(); j++) {
                int[] t = checkTwins(grille.getRegion(i, j), i, j);
                if (t != null) {
                    String msg = null;
                    if (t[1] != -1) {
                        msg = "Les seuls candidats "
                                + Tools.getData(t[0], grille.getType())
                                + ", alignés dans cette région, donnent la possibilité de supprimer les "
                                + Tools.getData(t[0], grille.getType()) +
                                " dans les autres régions de cette ligne car ils n'appaissent que dans la ligne de cette région donc forcément le "
                                +
                                Tools.getData(t[0], grille.getType())
                                + " se trouvera dans cette ligne de la région donc pas dans les autres régions de la ligne (jumeaux, triplets).\n ";
                        return createAnswer((i + t[1]), NOT_HERE, t[0], i, j, msg);
                    }
                    msg = "Les seuls candidats "
                            + Tools.getData(t[0], grille.getType())
                            + ", alignés dans cette région, donnent la possibilité de supprimer les "
                            + Tools.getData(t[0], grille.getType()) +
                            " dans les autres régions de cette colonne car ils n'appaissent que dans la colonne de cette région donc forcément le "
                            +
                            Tools.getData(t[0], grille.getType())
                            + " se trouvera dans cette colonne de la région donc pas dans les autres régions de la colonne (jumeaux, triplets).\n ";
                    return createAnswer(NOT_HERE, (j + t[2]), t[0], i, j, msg);
                }
            }
        }
        return null;
    }

    // OUTILS

    /**
     * Crée une réponse associé à la réponse trouvée.
     */
    private Answer createAnswer(int i, int j, int v, int x1, int y1, String msg) {
        Answer a = new Answer(msg);
        final int size = this.grille.getSize().getSize();
        Action act = null;
        if (i == NOT_HERE) {
            // parcours en colonne
            for (int x = 0; x < size; x++) {
                if (!grille.isInSameRegion(x, j, x1, y1)) {
                    act = new Action(x, j, v, Command.REM_CANDIDATES, false);
                    a.addRecommendedActions(act);
                }
            }
        } else if (j == NOT_HERE) {
            // parcours en ligne
            for (int y = 0; y < size; y++) {
                if (!grille.isInSameRegion(i, y, x1, y1)) {
                    act = new Action(i, y, v, Command.REM_CANDIDATES, false);
                    a.addRecommendedActions(act);
                }
            }
        }
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                if (grille.isInSameRegion(x, y, x1, y1)) {
                    a.addCasesOfProofs(x, y);
                }
            }
        }
        return a;
    }

    /**
     * Méthode qui prend une liste de liste de candidats d'une région
     * et renvoie une liste de 3 entiers :
     * -la valeur à supprimer
     * -le numéro de la ligne où il faut supprimer la valeur (-1 si on supprime sur
     * la colonne)
     * -le numéro de la colonne où il faut supprimer la valeur (-1 si on supprime
     * sur la ligne)
     */
    private int[] checkTwins(ICase[] candidates, int x, int y) {
        ICase[][] matrix = listToMatrix(candidates);
        int[] result = new int[3];
        Set<Integer> s;
        ICase c;
        for (int i = 0; i < matrix.length; i++) {
            List<Set<Integer>> t = new ArrayList<Set<Integer>>();
            for (int j = 0; j < matrix.length; j++) {
                c = matrix[i][j];
                if (!c.getIsFixed()) {
                    s = c.getCandidates();
                    if (s.size() == 0) {
                        return null;
                    }
                    t.add(s);
                }
            }
            for (int k = 1; k <= grille.getSize().getSize(); k++) {
                if (ContainTwins(t, matrix, k, i, true, x, y)) {
                    result[0] = k;
                    result[1] = i;
                    result[2] = -1;
                    return result;
                }
            }
            t = new ArrayList<Set<Integer>>();
            for (int j = 0; j < matrix.length; j++) {
                c = matrix[j][i];
                if (!c.getIsFixed()) {
                    s = c.getCandidates();
                    if (s.size() == 0) {
                        return null;
                    }
                    t.add(s);
                }
            }
            for (int k = 1; k <= grille.getSize().getSize(); k++) {
                if (ContainTwins(t, matrix, k, i, false, x, y)) {
                    result[0] = k;
                    result[1] = -1;
                    result[2] = i;
                    return result;
                }
            }
        }
        return null;
    }

    /**
     * Méthode outil qui transforme une liste de cellule qui est en entrée
     * qui représente une région du sudoku en matrice de cellule
     */
    private ICase[][] listToMatrix(ICase[] list) {
        int x = grille.getSize().getNumberRegionCols();
        int y = grille.getSize().getNumberRegionLines();
        ICase[][] matrix = new ICase[x][y];
        int index = 0;
        for (int i = 0; i < x; i++) {
            for (int j = 0; j < y; j++) {
                matrix[i][j] = list[index];
                index++;
            }
        }
        return matrix;
    }

    /**
     * Méthode qui prend une matrice de cellule et un numéro et qui compte
     * le nombre d'occurence de num dans chaque liste de candidat de matrix
     */
    private int countOccurrencesMatrix(ICase[][] matrix, int num) {
        ICase[] l = matrixToArray(matrix);
        return countOccurrences(l, num);
    }

    /**
     * Méthode outil qui transforme une matrice de cellule en liste de cellule
     */
    private ICase[] matrixToArray(ICase[][] matrix) {
        int rows = matrix.length;
        int cols = matrix[0].length;
        ICase[] array = new ICase[rows * cols];
        int index = 0;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                array[index++] = matrix[i][j];
            }
        }
        return array;
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
     * Méthode outil qui prend une liste de liste de candidat, une matrice
     * de liste de candidats qui représente une région du sudoku
     * un entier j qui est la valeur à chercher, un entier k qui est le
     * numéro de la ligne ou de la colonne où chercher et un booleen
     * qui correspond à si k correspond à une ligne (false si colonne)
     * et qui renvoie si il y a bien un doublé
     * de valeur sur une ligne de la région mais pas sur le reste de la région
     * et qu'il y a j sur la ligne où la colonne mais pas dans la région
     */
    private boolean ContainTwins(List<Set<Integer>> s, ICase[][] matrix, int j, int k, boolean isLine, int x, int y) {
        int b = 0;
        for (Set<Integer> set : s) {
            if (set.contains(j)) {
                b++;
            }
        }
        int[] tab = findregion(x, y);
        if (!isLine && b >= 2 && countOccurrencesMatrix(matrix, j) == b
                && countOccurrences(grille.getColumn(tab[1] + k), j) > b) {
            return true;
        }
        if (isLine && b >= 2 && countOccurrencesMatrix(matrix, j) == b
                && countOccurrences(grille.getLine(tab[0] + k), j) > b) {
            return true;
        }
        return false;
    }

    /**
     * méthode qui prend les coordonées d'une case et qui renvoie la 1ère case de sa
     * région
     * (donc la case la plus en haut à gauche de sa région)
     */
    private int[] findregion(int a, int b) {
        int[] tab = new int[2];
        for (int m = 0; m < grille.getSize().getSize(); m++) {
            for (int n = 0; n < grille.getSize().getSize(); n++) {
                if (grille.isInSameRegion(a, b, m, n)) {
                    tab[0] = m;
                    tab[1] = n;
                    return tab;
                }
            }
        }
        return tab;
    }
}
