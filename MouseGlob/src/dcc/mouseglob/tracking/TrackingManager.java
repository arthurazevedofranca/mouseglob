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
package dcc.mouseglob.tracking;

import java.util.ArrayList;
import java.util.List;

import processing.core.PGraphics;
import dcc.graphics.Paintable;
import dcc.graphics.image.BinaryImage;
import dcc.graphics.image.GrayscaleImage;
import dcc.graphics.image.Image;
import dcc.inject.Inject;
import dcc.mouseglob.analysis.AnalysesManager;
import dcc.mouseglob.applet.NewFrameListener;
import dcc.mouseglob.inspector.InspectableObject;
import dcc.mouseglob.inspector.Inspector;
import dcc.mouseglob.report.ReportDescriptor;
import dcc.mouseglob.report.ReportsManager;
import dcc.mouseglob.tracking.TrackingEvent.TrackingEventType;
import dcc.tree.DefaultTreeable;
import dcc.tree.TreeBranch;
import dcc.tree.TreeNode;
import dcc.tree.TreeSelectionListener;
import dcc.tree.Treeable;
import dcc.mouseglob.PropertiesManager;
import dcc.mouseglob.tracking.pipeline.FramePipeline;
import dcc.mouseglob.tracking.pipeline.JsonPipelineLoader;

/**
 * @author Daniel Coelho de Castro
 */
public final class TrackingManager extends DefaultTreeable implements
		TreeBranch<Tracker>, NewFrameListener, Paintable, InspectableObject,
		TreeSelectionListener {

	public enum TrackingMode {
		NORMAL("normal"), DIFFERENCE("difference");

		private String value;

		private TrackingMode(String value) {
			this.value = value;
		}

		@Override
		public String toString() {
			return value;
		}

		public static TrackingMode get(String value) {
			for (TrackingMode mode : values())
				if (mode.value.equals(value))
					return mode;
			return null;
		}
	}

	public enum ThresholdMode {
		LIGHT("light"), DARK("dark");

		private String value;

		private ThresholdMode(String value) {
			this.value = value;
		}

		@Override
		public String toString() {
			return value;
		}

		public static ThresholdMode get(String value) {
			for (ThresholdMode mode : values())
				if (mode.value.equals(value))
					return mode;
			return null;
		}
	}

	static final int MAX_TRACKER_SIZE = 100;
	static final int MAX_BACKGROUND_OFFSET = 128;
	static final int DEFAULT_THRESHOLD = 128;
	static final int DEFAULT_TRACKER_SIZE = 20;

	private Image cleanImage;
	private GrayscaleImage grayscaleImage;
	private GrayscaleImage differenceImage;
	private GrayscaleImage backgroundImage;
	private BinaryImage thresholdedImage;
	private BinaryImage globImage;

	private int trackerSize;
	private int threshold;
	private ThresholdMode thresholdMode;
	private TrackingMode trackingMode;
	private List<Tracker> trackers;
	private int trackerCount;

	boolean isAddingTracker;

	private final TreeNode node;
	private final Inspector trackingInspector;

	private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(TrackingManager.class);
	private FramePipeline pipeline;
	private boolean pipelineEnabled;

	@Inject
	private AnalysesManager analysisManager;
	@Inject
	private ReportsManager reportsManager;

	private final List<TrackingListener> trackingListeners;
	private final List<NewFrameListener> newFramelisteners;

	public enum ImageType {
		CLEAN, GLOB
	}

	private ImageType imageType = ImageType.CLEAN;

	public void setImageType(ImageType type) {
		imageType = type;
	}

	@Inject
	TrackingManager(TrackingController controller) {
		super("Tracking", "/resource/tracking16.png");

		this.trackerSize = DEFAULT_TRACKER_SIZE;
		this.threshold = DEFAULT_THRESHOLD;
		this.thresholdMode = ThresholdMode.LIGHT;
		this.trackingMode = TrackingMode.NORMAL;
		trackers = new ArrayList<Tracker>();
		trackerCount = 0;

		isAddingTracker = false;

		node = getNode();
		trackingInspector = new TrackingInspector(this);

		trackingListeners = new ArrayList<TrackingListener>();
		newFramelisteners = new ArrayList<NewFrameListener>();

		addTrackingListener(controller);
		initPipeline();
	}

	private void initPipeline() {
		try {
			PropertiesManager pm = PropertiesManager.getInstance();
			String enabled = pm.get("tracking.pipeline.enabled", "true");
			pipelineEnabled = Boolean.parseBoolean(enabled);
			if (!pipelineEnabled) {
				log.info("Tracking pipeline disabled via properties.");
				pipeline = null;
				return;
			}
			String path = pm.get("tracking.pipeline.file", "/resource/pipelines/default.json");
			FramePipeline p = null;
			if (path != null) {
				// Try as classpath resource first
				p = JsonPipelineLoader.loadFromResource(path);
				if (p == null && path.startsWith("/")) p = JsonPipelineLoader.loadFromResource(path.substring(1));
				// Fallback to filesystem path
				if (p == null) p = JsonPipelineLoader.loadFromFile(path);
			}
			pipeline = p;
			if (pipeline != null) log.info("Loaded tracking pipeline ({} stages) from {}", pipeline.getStages().size(), path);
			else log.info("No pipeline configuration found (path={}). Using legacy processing.", path);
		} catch (Throwable t) {
			log.warn("Failed to initialize tracking pipeline: {}", t.toString());
			pipeline = null;
			pipelineEnabled = false;
		}
	}

	/**
	 * Determines whether there are any trackers.
	 * 
	 * @return <code>true</code> if there aren't any, <code>false</code>
	 *         otherwise
	 */
	public boolean isEmpty() {
		return trackers.isEmpty();
	}

	/**
	 * Adds a new tracker.
	 * 
	 * @param tracker
	 *            - tracker to add
	 */
	@Override
	public void add(Tracker tracker) {
		trackerCount++;
		if (tracker.getName() == null)
			tracker.setName(Integer.toString(trackerCount));

		analysisManager.getNewDataset(tracker);
		TreeNode trackerNode = tracker.getNode();
		reportsManager.updateEnabledReports();
		for (ReportDescriptor descriptor : reportsManager.getDescriptors()) {
			System.err.println(trackerNode + ".add(" + descriptor + ")");
			trackerNode.add(descriptor.getHandle(tracker));
		}

		synchronized (trackers) {
			trackers.add(tracker);
		}

		node.add(tracker);

		notifyTrackingEvent(TrackingEventType.TRACKER_ADDED, tracker);
	}

	/**
	 * Removes the given tracker.
	 * 
	 * @param tracker
	 *            - the tracker to be removed
	 */
	@Override
	public void remove(Tracker tracker) {
		node.remove(tracker);

		synchronized (trackers) {
			trackers.remove(tracker);
		}

		notifyTrackingEvent(TrackingEventType.TRACKER_REMOVED, tracker);
	}

	private void notifyTrackingEvent(TrackingEventType type, Tracker tracker) {
		TrackingEvent event = new TrackingEvent(type, tracker, trackers.size());
		for (TrackingListener listener : trackingListeners)
			listener.onTrackingEvent(event);
	}

	/**
	 * Gets the number of existing trackers.
	 * 
	 * @return the number of trackers
	 */
	public int getTrackerCount() {
		return trackers.size();
	}

	/**
	 * Gets the trackers.
	 * 
	 * @return the tracker vector
	 */
	public List<Tracker> getTrackers() {
		return trackers;
	}

	/**
	 * @return the trackerSize
	 */
	public int getTrackerSize() {
		return trackerSize;
	}

	/**
	 * @param trackerSize
	 *            the trackerSize to set
	 */
	void setTrackerSize(int trackerSize) {
		this.trackerSize = trackerSize;
		synchronized (trackers) {
			for (Tracker tracker : trackers)
				tracker.setSize(trackerSize);
		}
	}

	/**
	 * @return the threshold
	 */
	public int getThreshold() {
		return threshold;
	}

	/**
	 * @param threshold
	 *            - the threshold to set
	 */
	void setThreshold(int threshold) {
		this.threshold = threshold;
	}

	/**
	 * @return the tracking mode
	 */
	public TrackingMode getTrackingMode() {
		return trackingMode;
	}

	/**
	 * @param mode
	 *            - the tracking mode
	 */
	public void setTrackingMode(TrackingMode mode) {
		if (mode != trackingMode && mode == TrackingMode.DIFFERENCE)
			resetBackground();
		trackingMode = mode;
	}

	/**
	 * @param mode
	 *            - the tracking mode (<code>"normal"</code> or
	 *            <code>"difference"</code>)
	 */
	void setTrackingMode(String mode) {
		setTrackingMode(TrackingMode.get(mode));
	}

	/**
	 * @return the threshold mode
	 */
	public ThresholdMode getThresholdMode() {
		return thresholdMode;
	}

	/**
	 * @param thresholdMode
	 *            - the threshold mode
	 */
	void setThresholdMode(ThresholdMode thresholdMode) {
		this.thresholdMode = thresholdMode;
	}

	/**
	 * @param mode
	 *            - the threshold mode (<code>"light"</code> or
	 *            <code>"dark"</code>)
	 */
	void setThresholdMode(String mode) {
		setThresholdMode(ThresholdMode.get(mode));
	}

	public boolean hasImage() {
		switch (trackingMode) {
		case DIFFERENCE:
			return differenceImage != null;
		case NORMAL:
			return cleanImage != null;
		default:
			return false;
		}
	}

	public void addTrackingListener(TrackingListener listener) {
		trackingListeners.add(listener);
	}

	@Override
	public void paint(PGraphics g) {
		synchronized (trackers) {
			for (Tracker tracker : trackers)
				tracker.paint(g);
		}
	}

	@Override
	public void newFrame(Image frame, long time) {
		cleanImage = frame;
		globImage = process(cleanImage);

		synchronized (trackers) {
			for (Tracker tracker : trackers)
				tracker.update(globImage, time);
		}

		if (imageType == ImageType.CLEAN) {
			for (NewFrameListener listener : newFramelisteners)
				listener.newFrame(cleanImage, time);
		} else {
			for (NewFrameListener listener : newFramelisteners)
				listener.newFrame(globImage, time);
		}
	}

	public void addNewFrameListener(NewFrameListener listener) {
		newFramelisteners.add(listener);
	}

	private BinaryImage process(Image image) {
		// If a configurable pipeline is available, use it
		if (pipeline != null && pipelineEnabled) {
			dcc.mouseglob.tracking.pipeline.PipelineContext ctx = pipeline.run(image, backgroundImage);
			// Update internal references for UI/consumers
			grayscaleImage = ctx.gray;
			backgroundImage = ctx.background;
			if (ctx.mask != null) thresholdedImage = ctx.mask;
			if (thresholdedImage != null) return thresholdedImage;
			// Fallback to legacy if pipeline did not produce a mask
		}
		
		grayscaleImage = image.luminance(grayscaleImage);
		
		if (trackingMode == TrackingMode.DIFFERENCE) {
			differenceImage = grayscaleImage.subtract(backgroundImage,
					differenceImage);
			grayscaleImage = differenceImage;
		}
		
		if (thresholdMode == ThresholdMode.LIGHT)
			thresholdedImage = grayscaleImage.threshold(threshold,
					thresholdedImage);
		else
			thresholdedImage = grayscaleImage.inverseThreshold(threshold,
					thresholdedImage);
		
		return thresholdedImage;
	}

	GrayscaleImage getBackground() {
		return backgroundImage;
	}

	void setBackground(GrayscaleImage backgroundImage) {
		this.backgroundImage = backgroundImage;
	}

	void resetBackground() {
		if (cleanImage != null)
			backgroundImage = cleanImage.luminance(backgroundImage);
	}

	public Image getCleanImage() {
		if (trackingMode == TrackingMode.NORMAL)
			return cleanImage;
		else
			return differenceImage;
	}

	public Image getTrackingImage() {
		return globImage;
	}

	/**
	 * Gets the tracker that contains the given point (typically the mouse
	 * cursor).
	 * 
	 * @param x
	 *            - horizontal coordinate of the point
	 * @param y
	 *            - vertical coordinate of the point
	 * @return the tracker being hovered
	 */
	public synchronized Tracker getHovered(int x, int y) {
		synchronized (trackers) {
			for (Tracker tracker : trackers)
				if (tracker.getBox().contains(x, y))
					return tracker;
		}

		return null;
	}

	/**
	 * Removes the tracker that contains the given point (typically the mouse
	 * cursor).
	 * 
	 * @param x
	 *            - horizontal coordinate of the point
	 * @param y
	 *            - vertical coordinate of the point
	 */
	public synchronized void removeHovered(int x, int y) {
		Tracker tracker = getHovered(x, y);
		if (tracker != null)
			remove(tracker);
	}

	@SuppressWarnings("unused")
	private List<Superposition> collisions() {
		List<Superposition> collisions = new ArrayList<>();
		for (int i = 0; i < trackerCount; i++) {
			Tracker t1 = trackers.get(i);
			for (int j = i + 1; j < trackerCount; j++) {
				Tracker t2 = trackers.get(j);
				if (t1.getBox().isSuperposed(t2.getBox()))
					collisions.add(new Superposition(t1, t2));
			}
		}
		return collisions;
	}

	@Override
	public void nodeSelected(Treeable object) {
		synchronized (trackers) {
			for (Tracker tracker : trackers)
				tracker.setSelected(false);
		}
		if (object instanceof Tracker)
			((Tracker) object).setSelected(true);
	}

	@Override
	public void nodeDoubleClicked(Treeable object) {
		if (object instanceof Tracker) {
			Tracker tracker = (Tracker) object;
			tracker.getDetail().show();
		}
	}

	@Override
	public Inspector getInspector() {
		return trackingInspector;
	}

}

