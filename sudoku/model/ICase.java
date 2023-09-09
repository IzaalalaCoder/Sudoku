package sudoku.model;

import java.util.Set;

import org.jdom2.Element;

/**
 * L'interface gère les case d'un sudoku.
 * 
 * @author Khabouri Izana.
 */
public interface ICase {

   // ATTRIBUTS STATIQUES

   public static final int CASE_EMPTY = 0;
   public static final int SIZE_MIN = 4;

   // REQUETES

   /**
    * Retourne la case o est égale à la case courante.
    */
   public boolean isEquals(ICase o);

   /**
    * Retourne si la valeur contenue dans la case est fixée par la grille.
    */
   public boolean getIsFixedByGrid();

   /**
    * Retourne si la valeur contenue dans la case est
    * fixée par l'utilisateur.
    */
   public boolean getIsFixedByUser();

   /**
    * Retourne s'il existe une valeur dans la case.
    */
   public boolean getIsFixed();

   /**
    * Retourne la valeur contenue dans la case s'il y en a une.
    * CASE_EMPTY sinon.
    */
   public Integer getValue();

   /**
    * Retourne tous les candidats de notre case.
    */
   public Set<Integer> getCandidates();

   // COMMANDES

   /**
    * Met tout les candidats dans la case sauf ceux de la liste n.
    */
   public void removeCandidateInSet(Set<Integer> n, int size);

   /**
    * Place le numéro x dans la case.
    */
   public void setValue(int x);

   /**
    * Enlève le numéro x de la case.
    */
   public void unsetValue();

   /**
    * Ajoute le candidat x dans notre case dans le cas ou
    * il n'existe pas de x parmis nos candidats.
    */
   public void addCandidates(int x);

   /**
    * Supprime le candidat x de notre case dans le cas ou
    * il existe un x parmis nos candidats.
    */
   public void remCandidates(int x);

   /**
    * Supprime tout les candidats de notre case.
    */
   public void removeAllCandidates();

   /**
    * Remet par défaut la case.
    */
   public void clear();

   /**
    * Sauvegarde les données d'une case dans un objet de type Element du package
    * JDOM
    */
   public Element save(String nom);
}
