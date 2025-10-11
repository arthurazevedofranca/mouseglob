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
package dcc.mouseglob.report;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.JPanel;

import processing.core.PApplet;
import dcc.mouseglob.FileType;
import dcc.mouseglob.ui.FileChooser;
import dcc.ui.Action;
import dcc.ui.PopupMenu;
import dcc.mouseglob.report.ReportExportUtil;
import java.nio.file.Paths;
import java.nio.file.Path;
import javax.swing.JOptionPane;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.Base64;
import javax.imageio.ImageIO;
import processing.core.PImage;

public class AppletReport extends Report {

	private PApplet applet;
	private PopupMenu popupMenu;

	protected void setApplet(PApplet applet) {
		this.applet = applet;
		popupMenu = new PopupMenu();
		popupMenu.add(new SaveAsPngAction());
		popupMenu.add(new SaveAsCsvAction());
		popupMenu.add(new SaveAsSvgAction());
		applet.addMouseListener(popupMenu);
		applet.init();
	}

	protected PopupMenu getPopupMenu() {
		return popupMenu;
	}

	@Override
	protected final Component getView() {
		JPanel panel = new JPanel(new BorderLayout());
		panel.add(applet, BorderLayout.CENTER);
		panel.addComponentListener(new ResizeListener());
		return panel;
	}

	@Override
	protected void onClose() {
		applet.noLoop();
		applet.stop();
	}

	private class ResizeListener extends ComponentAdapter {

		@Override
		public void componentResized(ComponentEvent e) {
			Dimension dim = e.getComponent().getSize();
			applet.setSize(dim.width, dim.height);
			applet.redraw();
		}

	}

	@SuppressWarnings("serial")
	private class SaveAsPngAction extends Action {

		private final FileChooser fileChooser;

		public SaveAsPngAction() {
			super("Save As Image...", "/toolbarButtonGraphics/SaveAs16.gif");
			fileChooser = new FileChooser(FileType.REPORT_IMAGE, "Report Image");
		}

		@Override
		public void actionPerformed() {
			String fileName = fileChooser.save();
			if (fileName != null)
				applet.save(fileName);
		}

	}

	@SuppressWarnings("serial")
	private class SaveAsCsvAction extends Action {
		private final FileChooser fileChooser;
		public SaveAsCsvAction() {
			super("Export CSV...");
			fileChooser = new FileChooser(FileType.REPORT_CSV, "Report CSV");
		}
		@Override
		public void actionPerformed() {
			if (!(AppletReport.this instanceof SeriesReport)) {
				JOptionPane.showMessageDialog(null, "CSV export is available for series reports only.", "Export CSV", JOptionPane.INFORMATION_MESSAGE);
				return;
			}
			String fileName = fileChooser.save();
			if (fileName != null) {
				try {
					ReportExportUtil.exportSeriesToCSV((SeriesReport) AppletReport.this, Paths.get(fileName));
					JOptionPane.showMessageDialog(null, "Report exported to CSV.", "Export CSV", JOptionPane.INFORMATION_MESSAGE);
				} catch (Exception ex) {
					JOptionPane.showMessageDialog(null, "Failed to export CSV: " + ex.getMessage(), "Export CSV", JOptionPane.ERROR_MESSAGE);
				}
			}
		}
	}


	@SuppressWarnings("serial")
	private class SaveAsSvgAction extends Action {
		private final FileChooser fileChooser;
		public SaveAsSvgAction() {
			super("Save As SVG...");
			fileChooser = new FileChooser(FileType.REPORT_SVG, "Report SVG");
		}
		@Override
		public void actionPerformed() {
			String fileName = fileChooser.save();
			if (fileName == null) return;
			try {
				// Snapshot as PNG and embed into minimal SVG
				PImage snap = applet.get();
				BufferedImage bi = new BufferedImage(snap.width, snap.height, BufferedImage.TYPE_INT_ARGB);
				int[] px = snap.pixels;
				bi.setRGB(0, 0, snap.width, snap.height, px, 0, snap.width);
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				ImageIO.write(bi, "png", baos);
				String b64 = Base64.getEncoder().encodeToString(baos.toByteArray());
				String svg = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
					"<svg xmlns=\"http://www.w3.org/2000/svg\" width=\""+snap.width+"\" height=\""+snap.height+"\">"+
					"<image href=\"data:image/png;base64,"+b64+"\" width=\"100%\" height=\"100%\"/>"+
					"</svg>\n";
				java.nio.file.Files.write(Paths.get(fileName), svg.getBytes(java.nio.charset.StandardCharsets.UTF_8));
			} catch (Exception ex) {
				JOptionPane.showMessageDialog(null, "Failed to export SVG: " + ex.getMessage(), "Export SVG", JOptionPane.ERROR_MESSAGE);
			}
		}
	}

}
