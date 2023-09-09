package sudoku;

import javax.swing.SwingUtilities;

import sudoku.view.menu.Launcher;

public class Main {
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				new Launcher().display();
			}
		});
	}
}