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

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JPopupMenu;

@SuppressWarnings("serial")
public class PopupMenu extends JPopupMenu implements MouseListener {
	public PopupMenu() {
		setLightWeightPopupEnabled(false);
	}

	@Override
	public final void mouseClicked(MouseEvent e) {
	}

	@Override
	public final void mouseEntered(MouseEvent e) {
	}

	@Override
	public final void mouseExited(MouseEvent e) {
	}

	@Override
	public final void mousePressed(MouseEvent e) {
		if (e.isPopupTrigger())
			show(e.getComponent(), e.getX(), e.getY());
	}

	@Override
	public final void mouseReleased(MouseEvent e) {
		if (e.isPopupTrigger())
			show(e.getComponent(), e.getX(), e.getY());
	}
}
