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
package dcc.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;

@SuppressWarnings("serial")
public class StatusBar extends JPanel {

	protected JPanel leftPanel;
	protected JPanel rightPanel;

	public StatusBar() {
		setLayout(new BorderLayout());
		setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
		setPreferredSize(new Dimension(getWidth(), 23));

		leftPanel = new JPanel(new FlowLayout(FlowLayout.LEADING, 3, 1));
		leftPanel.setOpaque(false);
		add(leftPanel, BorderLayout.WEST);

		rightPanel = new JPanel(new FlowLayout(FlowLayout.TRAILING, 3, 1));
		rightPanel.setOpaque(false);
		add(rightPanel, BorderLayout.EAST);
	}

	@Override
	public Component add(Component component) {
		if (leftPanel.getComponentCount() == 0) {
			leftPanel.add(component);
		} else {
			rightPanel.add(new JSeparator(SwingConstants.VERTICAL));
			rightPanel.add(component);
		}
		return component;
	}

	// @Override
	// protected void paintComponent(Graphics g) {
	// super.paintComponent(g);
	//
	// int y = 0;
	// g.setColor(new Color(156, 154, 140));
	// g.drawLine(0, y, getWidth(), y);
	// y++;
	//
	// g.setColor(new Color(196, 194, 183));
	// g.drawLine(0, y, getWidth(), y);
	// y++;
	//
	// g.setColor(new Color(218, 215, 201));
	// g.drawLine(0, y, getWidth(), y);
	// y++;
	//
	// g.setColor(new Color(233, 231, 217));
	// g.drawLine(0, y, getWidth(), y);
	//
	// y = getHeight() - 3;
	//
	// g.setColor(new Color(233, 232, 218));
	// g.drawLine(0, y, getWidth(), y);
	// y++;
	//
	// g.setColor(new Color(233, 231, 216));
	// g.drawLine(0, y, getWidth(), y);
	// y++;
	//
	// g.setColor(new Color(221, 221, 220));
	// g.drawLine(0, y, getWidth(), y);
	// }

}
