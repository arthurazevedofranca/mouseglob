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
package dcc.mouseglob.applet;

public class MouseEvent {

	public static enum Type {
		MOVED, PRESSED, RELEASED, CLICKED, DRAGGED, DOUBLE_CLICKED
	}

	public static enum Button {
		LEFT, RIGHT
	}

	private final int mouseX;
	private final int mouseY;
	private final float imageX;
	private final float imageY;
	private final MouseEvent.Button button;
	private final MouseEvent.Type type;

	MouseEvent(int mouseX, int mouseY, float imageX, float imageY,
			MouseEvent.Button button, MouseEvent.Type type) {
		this.mouseX = mouseX;
		this.mouseY = mouseY;
		this.imageX = imageX;
		this.imageY = imageY;
		this.button = button;
		this.type = type;
	}

	public int getMouseX() {
		return mouseX;
	}

	public int getMouseY() {
		return mouseY;
	}

	public float getImageX() {
		return imageX;
	}

	public float getImageY() {
		return imageY;
	}

	public MouseEvent.Button getButton() {
		return button;
	}

	public MouseEvent.Type getType() {
		return type;
	}

}
