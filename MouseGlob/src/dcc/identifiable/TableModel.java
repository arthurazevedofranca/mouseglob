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
package dcc.identifiable;

import javax.swing.table.DefaultTableModel;

/**
 * A model to retrieve tabular data.
 * 
 * @author Daniel Coelho de Castro
 */
@SuppressWarnings("serial")
public abstract class TableModel extends DefaultTableModel {
	/**
	 * The names of the column headers
	 */
	private String[] columnNames;

	/**
	 * Constructor for the <code>IdentifiableTabelModel</code> class. All
	 * subclasses should invoke this constructor.
	 * 
	 * @param headers
	 *            - the column header names
	 * @param numRows
	 *            - the number of rows to generate
	 */
	protected TableModel(String[] headers, int numRows) {
		columnNames = headers;
		Object[][] data = new Object[numRows][headers.length];
		for (int i = 0; i < numRows; i++)
			data[i] = makeRow(i);

		setDataVector(data, columnNames);
	}

	@Override
	public final Object getValueAt(int row, int col) {
		return makeRow(row)[col];
	}

	/**
	 * Creates a row of the table.
	 * 
	 * @param row
	 *            - the index of the row to retrieve
	 * @return an array of objects, which should be consistent with the
	 *         structure of the table
	 */
	public abstract Object[] makeRow(int row);

	@Override
	public final String getColumnName(int col) {
		return columnNames[col];
	}

	@Override
	public final int getColumnCount() {
		return columnNames.length;
	}

	@Override
	public Class<?> getColumnClass(int col) {
		return String.class;
	}

	@Override
	public boolean isCellEditable(int row, int col) {
		return false;
	}
}
