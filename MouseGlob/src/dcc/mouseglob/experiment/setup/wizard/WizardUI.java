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
package dcc.mouseglob.experiment.setup.wizard;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Container;
import java.awt.Frame;
import java.awt.Insets;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.border.EmptyBorder;

class WizardUI {

	private WizardController wizardController;

	private JDialog wizardDialog;

	private JPanel cardPanel;
	private CardLayout cardLayout;

	private JButton backButton;
	private JButton nextButton;
	private JButton cancelButton;

	WizardUI(Frame owner, String title, WizardController wizardController) {
		wizardDialog = new JDialog(owner, title);
		wizardDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		this.wizardController = wizardController;
		wizardController.setWizard(this);

		initComponents();
	}

	private void initComponents() {
		cardPanel = new JPanel();
		cardPanel.setBorder(new EmptyBorder(new Insets(0, 0, 0, 0)));

		cardLayout = new CardLayout();
		cardPanel.setLayout(cardLayout);
		backButton = new JButton(wizardController.backAction);
		nextButton = new JButton(wizardController.nextAction);
		cancelButton = new JButton(wizardController.cancelAction);

		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BorderLayout());
		buttonPanel.add(new JSeparator(), BorderLayout.NORTH);

		Box buttonBox = new Box(BoxLayout.X_AXIS);
		buttonBox.setBorder(new EmptyBorder(new Insets(5, 10, 5, 10)));
		buttonBox.add(backButton);
		buttonBox.add(Box.createHorizontalStrut(10));
		buttonBox.add(nextButton);
		buttonBox.add(Box.createHorizontalStrut(30));
		buttonBox.add(cancelButton);
		buttonPanel.add(buttonBox, BorderLayout.EAST);

		Container contentPane = wizardDialog.getContentPane();
		contentPane.setLayout(new BorderLayout());
		contentPane.add(cardPanel, BorderLayout.CENTER);
		contentPane.add(buttonPanel, BorderLayout.SOUTH);
	}

	void registerWizardPanel(WizardPanel panel) {
		cardPanel.add(panel.getComponent(), panel.getId());
	}

	void setCurrentPanel(Object id) {
		cardLayout.show(cardPanel, id.toString());
	}

	void setFinishActive(boolean isFinishActive) {
		nextButton.setAction(isFinishActive ? wizardController.finishAction
				: wizardController.nextAction);
	}

	void show() {
		wizardDialog.pack();
		wizardDialog.setVisible(true);
	}

	void hide() {
		wizardDialog.dispose();
	}

}
