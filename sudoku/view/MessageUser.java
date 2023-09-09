package sudoku.view;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import sudoku.model.IGrid;
import sudoku.model.info.Size;
import sudoku.model.info.Type;
import sudoku.util.Tools;

/**
 * La classe MessageUser génère les message avec une boîte de dialogue pour
 * différents cas.
 * 
 * @author Izana Khabouri
 */
public class MessageUser {

	// CONSTANTES
	private final String PATH_WIN = new Tools().getAbsolute("../temp"
			+ "lates/assets/win.png");
	private final String PATH_LOST = new Tools().getAbsolute("../temp"
			+ "lates/assets/lost.png");

	// COMMANDES

	/**
	 * Affiche un message d'érreur à l'utilisateur.
	 */
	public static void messageError(String title, String msg) {
		JOptionPane.showMessageDialog(null, msg,
				title, JOptionPane.ERROR_MESSAGE);
	}

	/**
	 * Affiche un message d'information à l'utilisateur.
	 */
	public static void messageInformation(String title, String msg) {
		String t = (title == null) ? "Information" : title;
		JOptionPane.showMessageDialog(null, msg, t,
				JOptionPane.INFORMATION_MESSAGE);
	}

	/**
	 * Affiche un message de demande à l'utilisateur.
	 */
	public static boolean messageQuestion(String title, String msg) {
		int result = JOptionPane.showConfirmDialog(null, msg, title,
				JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
		return JOptionPane.YES_OPTION == result;
	}

	/**
	 * Affiche un message de fin de partie.
	 */
	public void messageEndGame(boolean winner) {
		String t = "Fin de partie ! ";
		String msg = winner ? "Gagné ! " : "Perdu !";
		String path = winner ? PATH_WIN : PATH_LOST;
		ImageIcon img = new ImageIcon(path);
		JOptionPane.showMessageDialog(null, msg, t,
				JOptionPane.INFORMATION_MESSAGE, img);
	}

	/**
	 * Affiche une demande de changement de type.
	 */
	public static Type messageQuestionOnData(IGrid model) {
		Size sizeGrid = model.getSize();
		String[] types = new String[sizeGrid.getCanValue().length];
		int index = 0;
		int indexDefault = 0;
		for (Type t : model.getSize().getCanValue()) {
			types[index] = t.getLabel();
			index += 1;
			if (model.getType() == t) {
				indexDefault = index - 1;
			}
		}
		String msg = "Quel type choissisez-vous ?";
		String title = "Changement de type";
		int retour = JOptionPane.showOptionDialog(null, msg, title,
				JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE,
				null, types, types[indexDefault]);
		if (retour == JOptionPane.CLOSED_OPTION) {
			return null;
		}
		return model.getSize().getCanValue()[retour];
	}

}
