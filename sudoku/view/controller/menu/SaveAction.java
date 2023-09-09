package sudoku.view.controller.menu;

/**
 * La classe SaveAction implémente l'interface ActionListener.
 * Elle gère le chargement d'un fichier en fonction de la taille de
 * la grille de sudoku ainsi que son niveau de difficulté.
 * @author Ducroq Yohann.
 */
import sudoku.view.Sudoku;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public class SaveAction implements ActionListener {
	private final JFileChooser chooser = new JFileChooser();
	private final Sudoku sudoku;

	public SaveAction(Sudoku sudoku) {
		super();
		FileNameExtensionFilter fnef = new FileNameExtensionFilter("xml files (*.xml)", "xml");
		chooser.setFileFilter(fnef);
		chooser.addChoosableFileFilter(fnef);
		this.sudoku = sudoku;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		int returnVal = chooser.showSaveDialog(null);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File file = chooser.getSelectedFile();
			if (!file.toString().endsWith(".xml")) {
				file = new File(file.toString() + ".xml");
			}

			sudoku.save(file);
		}
	}
}
