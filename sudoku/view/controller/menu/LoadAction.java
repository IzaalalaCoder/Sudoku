package sudoku.view.controller.menu;

import sudoku.view.Sudoku;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

/**
 * La classe LoadAction implémente l'interface ActionListener.
 * Elle gère le chargement d'un fichier en fonction de la taille de
 * la grille de sudoku ainsi que son niveau de difficulté.
 * 
 * @author Ducroq Yohann.
 */
public class LoadAction implements ActionListener {

	final Sudoku sudoku;

	private final JFileChooser chooser = new JFileChooser();

	public LoadAction(Sudoku sudoku) {
		FileNameExtensionFilter fnef = new FileNameExtensionFilter("xml files (*.xml)", "xml");
		chooser.setFileFilter(fnef);
		chooser.addChoosableFileFilter(fnef);
		this.sudoku = sudoku;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		int returnVal = chooser.showOpenDialog(null);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File file = chooser.getSelectedFile();

			if (!file.toString().endsWith(".xml")) {
				JOptionPane.showMessageDialog(null, "Le fichier n'est pas au format .xml", "Erreur lecture",
						JOptionPane.ERROR_MESSAGE);
			}

			sudoku.load(file);
		}
	}
}
