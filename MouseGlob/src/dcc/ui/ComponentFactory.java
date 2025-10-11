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

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.awt.event.ItemListener;
import java.net.URL;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeListener;

/**
 * @author Daniel Coelho de Castro
 */
public final class ComponentFactory {
	public static final int BUTTON_SIZE = 26;
	public static final Dimension BUTTON_DIMENSION = new Dimension(BUTTON_SIZE,
			BUTTON_SIZE);

	/**
	 * Create a button with the given information and attach an ActionListener.
	 * 
	 * @param text
	 *            - text to be displayed on the button
	 * @param image
	 *            - category and the name of the icon that will be displayed on
	 *            the button. Example: <code>"media/Stop16"</code>
	 * @param toolTip
	 *            - text to be displayed as a tool tip when the mouse hovers the
	 *            button
	 * @param altText
	 *            - text to be displayed if there is a problem loading the icon
	 *            file
	 * @param listener
	 *            - the action listener for the button
	 * @return the new button
	 */
	public static JButton makeButton(String text, String image, String toolTip,
			String altText, ActionListener listener) {
		JButton button = new JButton(text);
		button.setToolTipText(toolTip);
		button.addActionListener(listener);

		ImageIcon icon = getIcon(image);
		if (icon != null)
			button.setIcon(icon);
		else {
			button.setText(altText);
			System.out.println("Problem loading file.");
		}

		if (text.isEmpty()) {
			button.setPreferredSize(BUTTON_DIMENSION);
			button.setMaximumSize(BUTTON_DIMENSION);
			button.setMinimumSize(BUTTON_DIMENSION);
		}

		return button;
	}

	/**
	 * Create a button with the given information and attach an ActionListener.
	 * 
	 * @param action
	 *            - the action for the button
	 * @return the new button
	 */
	public static JButton makeButton(Action action) {
		JButton button = new JButton(action);

		button.setPreferredSize(BUTTON_DIMENSION);
		button.setMaximumSize(BUTTON_DIMENSION);
		button.setMinimumSize(BUTTON_DIMENSION);

		return button;
	}

	/**
	 * Creates a toggle button.
	 * 
	 * @param text
	 *            - the text to be displayed on the button
	 * @param toolTip
	 *            - text to be displayed as a tool tip when the mouse hovers the
	 *            button
	 * @param image
	 *            - the path of the icon to be displayed on the button
	 * @param listener
	 *            - the item listener for the button
	 * @return the toggle button
	 */
	public static JToggleButton makeToggleButton(String text, String toolTip,
			String image, ItemListener listener) {
		JToggleButton toggle = new JToggleButton(text, false);
		toggle.setToolTipText(toolTip);
		toggle.addItemListener(listener);

		URL imageURL = ComponentFactory.class.getResource(image);

		if (imageURL != null)
			toggle.setIcon(new ImageIcon(imageURL));
		else {
			System.out.println("Problem loading file.");
		}

		if (text.isEmpty()) {
			toggle.setPreferredSize(BUTTON_DIMENSION);
			toggle.setMaximumSize(BUTTON_DIMENSION);
			toggle.setMinimumSize(BUTTON_DIMENSION);
		}

		return toggle;
	}

	/**
	 * Creates a slider.
	 * 
	 * @param label
	 *            - the border title
	 * @param min
	 *            - the minimum value
	 * @param max
	 *            - the maximum value
	 * @param value
	 *            - the initial value
	 * @param showTicks
	 *            - whether to show the ticks
	 * @param minor
	 *            - the minor tick spacing
	 * @param major
	 *            - the major tick spacing
	 * @param size
	 *            - the size of the slider
	 * @param listener
	 *            - the change listener
	 * @return the slider
	 */
	public static JSlider makeSlider(String label, int min, int max, int value,
			boolean showTicks, int minor, int major, Dimension size,
			ChangeListener listener) {
		JSlider slider = new JSlider(JSlider.HORIZONTAL, min, max, value);
		slider.setVisible(true);
		slider.addChangeListener(listener);
		if (showTicks) {
			slider.setMinorTickSpacing(minor);
			slider.setMajorTickSpacing(major);
		}
		slider.setPaintTicks(showTicks);
		slider.setPaintLabels(showTicks);
		slider.setPreferredSize(size);
		slider.setBorder(BorderFactory.createTitledBorder(label + ": "
				+ slider.getValue()));

		return slider;
	}

	/**
	 * Creates a text field.
	 * 
	 * @param text
	 *            - the initial text
	 * @param width
	 *            - the width of the field
	 * @param listener
	 *            - the action listener
	 * @return the text field
	 */
	public static JTextField makeTextField(String text, int width,
			ActionListener listener) {
		JTextField field = new JTextField(text);
		field.addActionListener(listener);
		field.setPreferredSize(new Dimension(width, 20));
		field.setMinimumSize(new Dimension(width, 20));
		field.setMaximumSize(new Dimension(width, 20));

		return field;
	}

	/**
	 * Creates a labeled panel for a text field.
	 * 
	 * @param field
	 *            - the text field
	 * @param label
	 *            - the label
	 * @return the text field panel
	 */
	public static JPanel makeTextFieldPanel(JTextField field, String label) {
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.LINE_AXIS));
		panel.add(field);
		panel.add(new JLabel(" " + label));
		field.setAlignmentX(Component.LEFT_ALIGNMENT);

		return panel;
	}

	/**
	 * Creates a titled border.
	 * 
	 * @param title
	 *            - the title of the border
	 * @return the border
	 */
	public static JPanel makeBorder(String title) {
		JPanel panel = new JPanel();
		panel.setBorder(BorderFactory.createTitledBorder(title));

		return panel;
	}

	/**
	 * Creates a menu item.
	 * 
	 * @param label
	 *            - the label
	 * @param listener
	 *            - the action listener
	 * @return the menu item
	 */
	public static JMenuItem makeMenuItem(String label, ActionListener listener) {
		JMenuItem menuItem = new JMenuItem(label);
		menuItem.addActionListener(listener);

		return menuItem;
	}

	/**
	 * Creates a menu item.
	 * 
	 * @param action
	 *            - the action
	 * @return the menu item
	 */
	public static JMenuItem makeMenuItem(Action action) {
		return new JMenuItem(action);
	}

	/**
	 * Creates a menu item.
	 * 
	 * @param label
	 *            - the label
	 * @param image
	 *            - the path of the icon to be displayed on the menu item
	 * @param listener
	 *            - the action listener
	 * @return the menu item
	 */
	public static JMenuItem makeMenuItem(String label, String image,
			ActionListener listener) {
		JMenuItem menuItem = makeMenuItem(label, listener);

		String imgLocation = "/toolbarButtonGraphics/" + image + "16.gif";
		URL imageURL = ComponentFactory.class.getResource(imgLocation);

		if (imageURL != null)
			menuItem.setIcon(new ImageIcon(imageURL));
		else
			System.out.println("Problem loading file.");

		return menuItem;
	}

	/**
	 * Creates a check box menu item.
	 * 
	 * @param label
	 *            - the label
	 * @param checked
	 *            - whether the item is initially checked
	 * @param listener
	 *            - the item listener
	 * @return the check box menu item
	 */
	public static JCheckBoxMenuItem makeCheckBoxMenuItem(String label,
			boolean checked, ItemListener listener) {
		JCheckBoxMenuItem menuItem = new JCheckBoxMenuItem(label, checked);
		menuItem.addItemListener(listener);

		return menuItem;
	}

	public static ImageIcon getIcon(String image) {
		URL imageURL;

		if (!image.startsWith("/resource/")) {
			String imgLocation = "/toolbarButtonGraphics/" + image + ".gif";
			imageURL = ComponentFactory.class.getResource(imgLocation);
		} else
			imageURL = ComponentFactory.class.getResource(image);

		if (imageURL != null)
			return new ImageIcon(imageURL);

		return null;
	}

	public static JPanel makeToolBarPanel(JToolBar... toolBars) {
		JPanel toolBarPanel = new JPanel();
		BoxLayout layout = new BoxLayout(toolBarPanel, BoxLayout.X_AXIS);
		toolBarPanel.setLayout(layout);
		JToolBar lastToolBar = null;
		for (int i = 0; i < toolBars.length; i++) {
			JToolBar toolBar = toolBars[i];
			toolBar.setFloatable(false);
			if (i > 0) {
				JSeparator sep = new JSeparator(SwingConstants.VERTICAL);
				sep.setMaximumSize(new Dimension(1, Short.MAX_VALUE));
				toolBarPanel.add(sep);
			}
			toolBarPanel.add(toolBar);
			lastToolBar = toolBar;
		}
		if (lastToolBar != null)
			lastToolBar.setMaximumSize(new Dimension(Short.MAX_VALUE,
					Short.MAX_VALUE));
		return toolBarPanel;
	}
}
