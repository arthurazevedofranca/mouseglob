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
package dcc.mouseglob;

import dcc.inject.Inject;
import dcc.module.AbstractController;
import dcc.mouseglob.applet.MouseGlobApplet;
import dcc.mouseglob.tracking.TrackingManager;
import dcc.mouseglob.tracking.TrackingManager.ImageType;
import dcc.ui.Action;
import dcc.ui.EnumButtonGroup;

/**
 * This class handles the GUI for the <code>MouseGlob</code> applet.
 * 
 * @author Daniel Coelho de Castro
 */
@SuppressWarnings("serial")
public final class MouseGlobController extends AbstractController<MouseGlob> {

	private static final int DEFAULT_WIDTH = 640, DEFAULT_HEIGHT = 480;

	final Action exitAction;
	final EnumButtonGroup<ImageType> imageTypeGroup;

	private final MouseGlobApplet applet;

	/**
	 * Constructor for the <code>MouseGlobController</code> class.
	 */
	@Inject
	public MouseGlobController(final MouseGlobApplet applet,
			final TrackingManager trackingManager) {
		this.applet = applet;
		applet.init();
		applet.setAppletSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);

		exitAction = new ExitAction();

		imageTypeGroup = new EnumButtonGroup<ImageType>() {
			@Override
			public void valueSelected(ImageType value) {
				trackingManager.setImageType(value);
			}
		};
		imageTypeGroup.add("MouseGlob Clean Image", ImageType.CLEAN);
		imageTypeGroup.add("MouseGlob Tracking Image", ImageType.GLOB);
		imageTypeGroup.select(0);

		applet.addMouseListener(imageTypeGroup.makePopupMenu());
	}

	private class ExitAction extends Action {
		public ExitAction() {
			super("Exit");
			setDescription("Exit MouseGlob");
		}

		@Override
		public void actionPerformed() {
			applet.exit();
		}
	}
}
