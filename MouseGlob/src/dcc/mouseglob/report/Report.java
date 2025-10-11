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
package dcc.mouseglob.report;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;

public abstract class Report {

	private static JFrame parent;

	public static void setParent(JFrame parent) {
		Report.parent = parent;
	}

	public JDialog getDialog() {
		ReportDescriptor descriptor = getDescriptor();

		JPanel contentPane = new JPanel();
		contentPane.setLayout(new BorderLayout());
		contentPane.add(getView(), BorderLayout.CENTER);

		JDialog dialog = new JDialog(parent);
		dialog.setTitle(descriptor.getName());
		dialog.setIconImage(descriptor.getIconImage());
		dialog.setContentPane(contentPane);
		dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		dialog.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosed(WindowEvent e) {
				onClose();
			}
		});
		dialog.pack();

		return dialog;
	}

	public final void show() {
		getDialog().setVisible(true);
	}

	protected abstract Component getView();

	public ReportDescriptor getDescriptor() {
		return new ReportDescriptor(getClass());
	}

	protected void onClose() {
	}

}
