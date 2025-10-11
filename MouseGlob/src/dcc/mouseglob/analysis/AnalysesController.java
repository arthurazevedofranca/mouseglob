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
package dcc.mouseglob.analysis;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.swing.JOptionPane;

import dcc.inject.Inject;
import dcc.module.Controller;
import dcc.mouseglob.analysis.Analysis.AnalysisInfo;
import dcc.ui.Action;
import dcc.ui.ToggleAction;

public class AnalysesController implements Controller {

	@Inject
	private AnalysesManager manager;
	@Inject
	private AnalysesView view;
	List<AnalysisAction> actions;
	@SuppressWarnings("serial")
	Action manageAnalysesAction = new Action("Manage Analyses...") {
		@Override
		public void actionPerformed() {
			initActions();
			for (AnalysisAction action : actions) {
				if (manager.isSelected(action.analysis))
					action.setSelected(true);
				if (manager.isRequired(action.analysis))
					action.setEnabled(false);
			}
			JOptionPane.showMessageDialog(null, view.makePanel(), "Analyses",
					JOptionPane.QUESTION_MESSAGE);
		}
	};

	@Inject
	private void initActions() {
		actions = new ArrayList<AnalysisAction>();
		List<Class<? extends Analysis>> analyses = manager
				.getAvailableAnalyses();
		Collections.sort(analyses, new Comparator<Class<? extends Analysis>>() {
			@Override
			public int compare(Class<? extends Analysis> o1,
					Class<? extends Analysis> o2) {
				return o1.getSimpleName().compareTo(o2.getSimpleName());
			}
		});
		for (Class<? extends Analysis> clazz : analyses) {
			AnalysisAction action = new AnalysisAction(clazz);
			if (manager.isSelected(clazz))
				action.setSelected(true);
			if (manager.isRequired(clazz))
				action.setEnabled(false);
			actions.add(action);
		}
	}

	private void update() {
		boolean changed;
		do {
			changed = false;
			for (AnalysisAction action : actions) {
				boolean isAnalysisSelected = manager
						.isSelected(action.analysis);
				boolean isActionSelected = action.isSelected();
				if (isAnalysisSelected != isActionSelected) {
					action.setSelected(isAnalysisSelected);
					changed = true;
				}
			}
		} while (changed);
	}

	private static String getName(Class<?> clazz) {
		AnalysisInfo info = clazz.getAnnotation(AnalysisInfo.class);
		return info != null ? info.value() : clazz.getSimpleName();
	}

	private String getToolTip(Class<? extends Analysis> clazz) {
		StringBuilder sb = new StringBuilder("<html>");
		sb.append("<b>").append(getName(clazz)).append("</b>");

		if (manager.isRequired(clazz))
			sb.append(" [required]");

		AnalysisInfo info = clazz.getAnnotation(AnalysisInfo.class);
		if (info != null) {
			String description = info.description();
			if (!description.isEmpty())
				sb.append("<br>").append(description);
		}

		Collection<Class<? extends Analysis>> dependencies = manager
				.getDependencies(clazz);
		if (!dependencies.isEmpty()) {
			sb.append("<br>(depends on: ");
			boolean isFirst = true;
			for (Class<?> dependency : dependencies) {
				if (!isFirst)
					sb.append(", ");
				isFirst = false;
				sb.append(getName(dependency));
			}
			sb.append(")");
		}

		return sb.append("</html>").toString();
	}

	@SuppressWarnings("serial")
	class AnalysisAction extends ToggleAction {

		private final Class<? extends Analysis> analysis;

		public AnalysisAction(Class<? extends Analysis> clazz) {
			super(getName(clazz));
			setDescription(getToolTip(clazz));
			this.analysis = clazz;
		}

		@Override
		public void itemStateChanged(boolean state) {
			if (state)
				manager.select(analysis);
			else
				manager.deselect(analysis);
			update();
		}

	}

}
