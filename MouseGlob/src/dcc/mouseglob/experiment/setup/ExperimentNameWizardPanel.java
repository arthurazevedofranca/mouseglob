/*******************************************************************************
 * Copyright (c) 2016 Daniel Coelho de Castro.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 *     Daniel Coelho de Castro - initial API and implementation
 ******************************************************************************/
package dcc.mouseglob.experiment.setup;

import java.awt.Component;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import dcc.mouseglob.FileType;
import dcc.mouseglob.experiment.setup.wizard.DefaultWizardPanel;
import dcc.mouseglob.ui.FileChooser;
import dcc.ui.Action;
import dcc.ui.ComponentFactory;

public class ExperimentNameWizardPanel extends DefaultWizardPanel {

	static final Object ID = "experiment";

	private JTextField nameField;
	private FileChooser fileChooser;

	public ExperimentNameWizardPanel() {
		super(ID, null, ChooseVideoWizardPanel.ID);
		fileChooser = new FileChooser(FileType.EXPERIMENT_FILE, "experiment");
	}

	@SuppressWarnings("serial")
	@Override
	public Component getComponent() {
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
		panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		panel.add(new JLabel("Experiment name: "));
		panel.add(Box.createHorizontalStrut(10));

		nameField = new JTextField(40);
		nameField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));
		panel.add(nameField);
		panel.add(Box.createHorizontalStrut(10));

		panel.add(new JButton(new Action("Browse...", ComponentFactory
				.getIcon("general/Open16")) {
			@Override
			public void actionPerformed() {
				String fileName = fileChooser.save();
				if (fileName != null)
					nameField.setText(fileName);
			}
		}));

		return panel;
	}

	@Override
	public void aboutToHidePanel() {
		System.out.println("Experiment name: " + nameField.getText());
	}

}
