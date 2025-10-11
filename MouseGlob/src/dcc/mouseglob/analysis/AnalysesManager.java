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
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import dcc.inject.TypedDependencyGraph;
import dcc.mouseglob.analysis.Analysis.AnalysisInfo;
import dcc.mouseglob.analysis.Dataset.Time;
import dcc.mouseglob.analysis.analyses.MomentsAnalysis;
import dcc.mouseglob.analysis.analyses.MouseModel;
import dcc.mouseglob.analysis.analyses.OrientationAnalysis;
import dcc.mouseglob.analysis.analyses.OrientationCorrectionAnalysis;
import dcc.mouseglob.analysis.analyses.PositionAnalysis;
import dcc.mouseglob.tracking.Tracker;
import dcc.mouseglob.visit.VisitAnalysis;
import dcc.util.ClassCache;
import dcc.xml.XMLProcessor;
import dcc.xml.XMLProcessor.IXMLDecoder;
import dcc.xml.XMLProcessor.IXMLEncoder;
import dcc.xml.XMLProcessor.XMLEncodable;

public class AnalysesManager implements XMLEncodable {

	private static final Class<?>[] REQUIRED_ANALYSES = { Time.class,
			MomentsAnalysis.class, PositionAnalysis.class,
			OrientationAnalysis.class, OrientationCorrectionAnalysis.class,
			MouseModel.class, VisitAnalysis.class };

	private Set<Class<? extends Analysis>> availableAnalyses;
	private Set<Class<? extends Analysis>> selectedAnalyses;
	private List<Class<? extends Analysis>> orderedAnalyses;
	private TypedDependencyGraph<Analysis> dependencyGraph;
	private boolean dirty = true;

	private List<AnalysisSelectionListener> listeners;

	@SuppressWarnings("unchecked")
	public AnalysesManager() {
		availableAnalyses = new java.util.LinkedHashSet<>();
		// 1) Discover via ServiceLoader SPI
		try {
			java.util.ServiceLoader<dcc.mouseglob.analysis.spi.AnalysisProvider> loader =
					java.util.ServiceLoader.load(dcc.mouseglob.analysis.spi.AnalysisProvider.class);
			for (dcc.mouseglob.analysis.spi.AnalysisProvider p : loader) {
				java.util.Collection<Class<? extends Analysis>> provided = p.getAnalyses();
				if (provided != null) availableAnalyses.addAll(provided);
			}
		} catch (Throwable t) {
			// ignore and fallback
		}
		// 2) Fallback to legacy classpath scanning if nothing found
		if (availableAnalyses.isEmpty()) {
			availableAnalyses = ClassCache.getInstance().subclassesWithAnnotation(
					Analysis.class, AnalysisInfo.class);
		}

		dependencyGraph = new TypedDependencyGraph<Analysis>(Analysis.class);
		dependencyGraph.addAll(availableAnalyses);

		listeners = new ArrayList<AnalysisSelectionListener>();

		selectedAnalyses = new HashSet<Class<? extends Analysis>>();
		for (Class<?> analysis : REQUIRED_ANALYSES)
			select((Class<? extends Analysis>) analysis);
	}

	public List<Class<? extends Analysis>> getAvailableAnalyses() {
		return dependencyGraph.getOrder();
	}

	public void select(Class<? extends Analysis> analysisClass) {
		dirty = true;
		selectedAnalyses.add(analysisClass);
		selectedAnalyses.addAll(dependencyGraph.getDependencies(analysisClass));

		for (AnalysisSelectionListener listener : listeners)
			listener.analysisSelected(analysisClass);
	}

	public void deselect(Class<? extends Analysis> analysisClass) {
		dirty = true;
		selectedAnalyses.remove(analysisClass);
		selectedAnalyses.removeAll(dependencyGraph
				.getAllThatDependOn(analysisClass));

		for (AnalysisSelectionListener listener : listeners)
			listener.analysisDeselected(analysisClass);
	}

	boolean isRequired(Class<? extends Analysis> clazz) {
		for (Class<?> req : REQUIRED_ANALYSES)
			if (req == clazz)
				return true;
		return false;
	}

	boolean isSelected(Class<? extends Analysis> clazz) {
		return selectedAnalyses.contains(clazz);
	}

	Set<Class<? extends Analysis>> getSelectedAnalyses() {
		return selectedAnalyses;
	}

	Set<Class<? extends Analysis>> getDependencies(
			Class<? extends Analysis> clazz) {
		return dependencyGraph.getDependencies(clazz);
	}

	private void finishSelection() {
		if (dirty) {
			TypedDependencyGraph<Analysis> graph = new TypedDependencyGraph<>(
					Analysis.class);
			graph.addAll(selectedAnalyses);
			orderedAnalyses = Collections.unmodifiableList(graph.getOrder());
			dirty = false;
		}
	}

	public List<Class<? extends Analysis>> getAnalyses() {
		finishSelection();
		return orderedAnalyses;
	}

	public Dataset getNewDataset(Tracker tracker) {
		finishSelection();
		return new Dataset(tracker, orderedAnalyses);
	}

	public void addSelectionListener(AnalysisSelectionListener listener) {
		listeners.add(listener);
	}

	public static interface AnalysisSelectionListener {
		void analysisSelected(Class<? extends Analysis> analysisClass);

		void analysisDeselected(Class<? extends Analysis> analysisClass);
	}

	@Override
	public String getTagName() {
		return "analyses";
	}

	@Override
	public IXMLEncoder getEncoder(XMLProcessor processor) {
		return new AnalysisXMLCodec(processor, this);
	}

	@Override
	public IXMLDecoder getDecoder(XMLProcessor processor) {
		return new AnalysisXMLCodec(processor, this);
	}

}
