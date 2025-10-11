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
package dcc.mouseglob.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.JMenu;
import javax.swing.Scrollable;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;

/**
 * @author Daniel Coelho de Castro
 */
public class InternalFrameUI {
	private MGDesktopPane desktop;

	/**
	 * Constructor for the <code>InternalFrameUI</code> class.
	 */
	public InternalFrameUI() {
		desktop = new MGDesktopPane();
		desktop.setBackground(new Color(96, 96, 96));
	}

	/**
	 * Adds a new internal frame to the desktop pane.
	 * 
	 * @param content
	 *            - the content of the frame
	 * @param title
	 *            - the title of the window
	 * @param menu
	 *            - the menu to which will be added a check box menu item
	 *            corresponding to this frame
	 * @return the newly created frame
	 */
	public JInternalFrame addInternalFrame(Container content, String title,
			JMenu menu) {
		final MGInternalFrame frame = new MGInternalFrame(content);
		frame.setTitle(title);
		desktop.add(frame);

		if (menu != null)
			menu.add(frame.associateMenuItem());

		return frame;
	}

	/**
	 * Adds a new internal frame to the desktop pane.
	 * 
	 * @param content
	 *            - the content of the frame
	 * @param title
	 *            - the title of the window
	 * @param menu
	 *            - the menu to which will be added a check box menu item
	 *            corresponding to this frame
	 * @param x
	 *            - the horizontal coordinate of the frame
	 * @param y
	 *            - the vertical coordinate of the frame
	 * @return the newly created frame
	 */
	public JInternalFrame addInternalFrame(Container content, String title,
			JMenu menu, int x, int y) {
		JInternalFrame frame = addInternalFrame(content, title, menu);
		frame.setLocation(x, y);

		return frame;
	}

	/**
	 * Adds a new internal frame to the desktop pane.
	 * 
	 * @param content
	 *            - the content of the frame
	 * @param title
	 *            - the title of the window
	 * @param menu
	 *            - the menu to which will be added a check box menu item
	 *            corresponding to this frame
	 * @param visible
	 *            - whether the frame will be initially visible on the desktop
	 * @return the newly created frame
	 */
	public JInternalFrame addInternalFrame(Container content, String title,
			JMenu menu, boolean visible) {
		MGInternalFrame frame = new MGInternalFrame(content);
		frame.setTitle(title);
		frame.setVisible(visible);
		desktop.add(frame);

		if (menu != null) {
			JCheckBoxMenuItem menuItem = frame.associateMenuItem();
			menuItem.setSelected(visible);
			menu.add(menuItem);
		}

		return frame;
	}

	public JInternalFrame getFrame(String title) {
		Component[] components = desktop.getComponents();

		for (Component c : components)
			if (c instanceof JInternalFrame)
				if (((JInternalFrame) c).getTitle().equals(title))
					return (JInternalFrame) c;

		return null;
	}

	/**
	 * Gets the desktop pane.
	 * 
	 * @return the JDesktopPane
	 */
	public MGDesktopPane getDesktopPane() {
		return desktop;
	}

	@SuppressWarnings("serial")
	public static class MGDesktopPane extends JDesktopPane implements
			Scrollable {
		@Override
		public Dimension getPreferredScrollableViewportSize() {
			return getPreferredSize();
		}

		@Override
		public int getScrollableUnitIncrement(Rectangle r, int axis, int dir) {
			return 50;
		}

		@Override
		public int getScrollableBlockIncrement(Rectangle r, int axis, int dir) {
			return 200;
		}

		@Override
		public boolean getScrollableTracksViewportHeight() {
			return false;
		}

		@Override
		public boolean getScrollableTracksViewportWidth() {
			return false;
		}
	}

	@SuppressWarnings("serial")
	public static class MGInternalFrame extends JInternalFrame {
		private static int openFrameCount = 0;
		private static final int X_OFFSET = 30;
		private static final int Y_OFFSET = 30;

		private MGInternalFrame(Container content, int x, int y) {
			super("Frame " + (++openFrameCount), false, // resizable
					true, // closable
					false, // maximizable
					false);// iconifiable
			if (content == null)
				setSize(300, 300);
			else
				setSize(content.getSize());

			setDefaultCloseOperation(HIDE_ON_CLOSE);

			if (content != null)
				setContentPane(content);

			setLocation(x, y);
			setVisible(true);
			pack();
		}

		private MGInternalFrame(Container content) {
			this(content, X_OFFSET * openFrameCount, Y_OFFSET * openFrameCount);
		}

		private MGInternalFrame(Container content, String title, boolean visible) {
			this(content);
			setTitle(title);
			setVisible(visible);
		}

		private JCheckBoxMenuItem associateMenuItem() {
			final JCheckBoxMenuItem menuItem = new JCheckBoxMenuItem(title,
					true);

			menuItem.addItemListener(new ItemListener() {
				@Override
				public void itemStateChanged(ItemEvent e) {
					boolean state = ((JCheckBoxMenuItem) e.getSource())
							.isSelected();
					MGInternalFrame.this.setVisible(state);
				}
			});

			addInternalFrameListener(new InternalFrameListener() {
				@Override
				public void internalFrameActivated(InternalFrameEvent e) {
				}

				@Override
				public void internalFrameClosed(InternalFrameEvent e) {
				}

				@Override
				public void internalFrameClosing(InternalFrameEvent e) {
					menuItem.setSelected(false);
				}

				@Override
				public void internalFrameDeactivated(InternalFrameEvent e) {
				}

				@Override
				public void internalFrameDeiconified(InternalFrameEvent e) {
				}

				@Override
				public void internalFrameIconified(InternalFrameEvent e) {
				}

				@Override
				public void internalFrameOpened(InternalFrameEvent e) {
					menuItem.setSelected(true);
				}
			});

			return menuItem;
		}
	}
}
