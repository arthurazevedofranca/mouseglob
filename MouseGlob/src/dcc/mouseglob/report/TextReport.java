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

import java.awt.Component;

import javax.swing.JTextPane;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.StyleSheet;

import dcc.ui.Action;
import dcc.ui.PopupMenu;
import dcc.util.TextTransfer;

public class TextReport extends Report {

	private static final String BODY_STYLE = "body { font-family: Segoe UI,Verdana,Arial; font-size: 14pt; }";

	private final JTextPane textPane;
	private final StyleSheet styleSheet;

	@SuppressWarnings("serial")
	public TextReport() {
		textPane = new JTextPane();
		textPane.setContentType("text/html");
		textPane.setEditable(false);
		styleSheet = ((HTMLDocument) textPane.getDocument()).getStyleSheet();

		PopupMenu popupMenu = new PopupMenu();
		popupMenu.add(new Action("Copy to clipboard") {
			@Override
			public void actionPerformed() {
				TextTransfer.copy(getText());
			}
		});
		textPane.addMouseListener(popupMenu);

		styleSheet.addRule(BODY_STYLE);
	}

	public void setText(String text) {
		textPane.setText("<html>" + text + "</html>");
	}

	public String getText() {
		return textPane.getText();
	}

	@Override
	protected Component getView() {
		return textPane;
	}

	protected static class TableBuilder {

		private StringBuilder sb;
		private int colWidth = -1;
		private int[] colWidths = null;

		public TableBuilder() {
			sb = new StringBuilder("<table width='100%'>");
		}

		public TableBuilder addHeader(Object... cells) {
			sb.append("<tr>");
			for (int i = 0; i < cells.length; i++)
				sb.append(getTag(i, "th")).append(cells[i]).append("</th>");
			sb.append("</tr>");
			return this;
		}

		public TableBuilder addRow(Object... cells) {
			sb.append("<tr>");
			for (int i = 0; i < cells.length; i++)
				sb.append(getTag(i, "td")).append("<center>").append(cells[i])
						.append("</center></td>");
			sb.append("</tr>");
			return this;
		}

		public void setColumnWidth(int colWidth) {
			this.colWidth = colWidth;
		}

		public void setColumnWidths(int... colWidths) {
			this.colWidths = colWidths;
		}

		private String getTag(int i, String tag) {
			if (colWidths == null || i >= colWidths.length || colWidths[i] <= 0)
				return String.format("<%s>", tag);
			if (colWidth > 0)
				return String.format("<%s style='width:%dpx'>", tag, colWidth);
			return String.format("<%s style='width:%dpx'>", tag, colWidths[i]);
		}

		@Override
		public String toString() {
			return sb.toString() + "</table>";
		}

	}

}
