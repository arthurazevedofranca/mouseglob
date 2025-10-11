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

public abstract class DefaultWizardPanel implements WizardPanel {

	private final Object id, back, next;
	private boolean backEnabled, nextEnabled;
	private WizardController controller;

	public DefaultWizardPanel(Object id, Object back, Object next) {
		this.id = id;
		this.back = back;
		this.next = next;
		backEnabled = back != null;
		nextEnabled = next != null;
	}

	public DefaultWizardPanel(Object id) {
		this(id, null, null);
	}

	void setController(WizardController controller) {
		this.controller = controller;
	}

	@Override
	public boolean isBackEnabled() {
		return backEnabled;
	}

	@Override
	public boolean isNextEnabled() {
		return nextEnabled;
	}

	protected final void setNextEnabled(boolean enabled) {
		if (next != null) {
			nextEnabled = enabled;
			updateNavigationButtons();
		}
	}

	protected final void setBackEnabled(boolean enabled) {
		if (back != null) {
			backEnabled = enabled;
			updateNavigationButtons();
		}
	}

	private void updateNavigationButtons() {
		if (controller != null)
			controller.updateNavigationButtons();
	}

	@Override
	public final Object getId() {
		return id;
	}

	@Override
	public Object getNextId() {
		return next;
	}

	@Override
	public Object getBackId() {
		return back;
	}

	@Override
	public void aboutToDisplayPanel() {
	}

	@Override
	public void displayingPanel() {
	}

	@Override
	public void aboutToHidePanel() {
	}

}
