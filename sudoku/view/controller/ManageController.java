package sudoku.view.controller;

import java.awt.event.ActionListener;
import java.awt.event.MouseListener;

import java.beans.PropertyChangeListener;
import java.beans.VetoableChangeListener;

import java.util.Set;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JMenuItem;

import sudoku.model.IGrid;

/**
 * Gestion des évènements.
 * 
 * @author Izana Khabouri
 */
public class ManageController {

	// CONSTANTES DE CLASSES

	public static int DRAFT_MODE = 1;
	public static int FIXED_MODE = 2;

	// METHODES STATIQUES

	public static void removeActionListener(JButton... btn) {
		for (JButton b : btn) {
			for (ActionListener al : b.getActionListeners()) {
				b.removeActionListener(al);
			}
		}
	}

	public static void removeActionListener(JMenuItem... items) {
		for (JMenuItem it : items) {
			for (ActionListener al : it.getActionListeners()) {
				it.removeActionListener(al);
			}
		}
	}

	public static void removeMouseListener(Set<JComponent> c) {
		for (JComponent comp : c) {
			for (MouseListener ml : comp.getMouseListeners()) {
				comp.removeMouseListener(ml);
			}
		}
	}

	public static void removeListenerOnModel(IGrid model) {
		for (PropertyChangeListener pcl : model.getPropertyChangeListeners(IGrid.PROP_HELP)) {
			model.removePropertyChangeListener(IGrid.PROP_HELP, pcl);
		}

		for (PropertyChangeListener pcl : model.getPropertyChangeListeners(IGrid.PROP_FINISHED)) {
			model.removePropertyChangeListener(IGrid.PROP_FINISHED, pcl);
		}

		for (VetoableChangeListener vcl : model.getVetoableChangeListeners(IGrid.PROP_FINISHED)) {
			model.removeVetoableChangeListener(IGrid.PROP_FINISHED, vcl);
		}

		for (PropertyChangeListener pcl : model.getPropertyChangeListeners(IGrid.PROP_DATA)) {
			model.removePropertyChangeListener(IGrid.PROP_DATA, pcl);
		}

		for (VetoableChangeListener vcl : model.getVetoableChangeListeners(IGrid.PROP_GAME)) {
			model.removeVetoableChangeListener(IGrid.PROP_GAME, vcl);
		}

		for (PropertyChangeListener pcl : model.getPropertyChangeListeners(IGrid.PROP_GAME)) {
			model.removePropertyChangeListener(IGrid.PROP_GAME, pcl);
		}

		for (PropertyChangeListener pcl : model.getPropertyChangeListeners(IGrid.PROP_AUTOCOMPLETE)) {
			model.removePropertyChangeListener(IGrid.PROP_AUTOCOMPLETE, pcl);
		}
	}
}
