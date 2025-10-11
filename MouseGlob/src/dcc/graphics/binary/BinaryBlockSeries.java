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
package dcc.graphics.binary;

import java.util.ArrayList;
import java.util.List;

public class BinaryBlockSeries extends DefaultBinaryMask1D {

	private static final int DEFAULT_INITIAL_SIZE = 100;

	private final List<Block> blocks;
	private Block last;
	private int size = 0;

	public BinaryBlockSeries() {
		this(DEFAULT_INITIAL_SIZE);
	}

	public BinaryBlockSeries(int initialSize) {
		blocks = new ArrayList<Block>();
		size = initialSize;
	}

	@Override
	public boolean get(int index) {
		for (Block block : blocks) {
			if (index >= block.start) {
				if (index < block.end)
					return true;
			}
		}
		return false;
	}

	public void add(int start, int end) {
		Block block = new Block(start, end);
		if (last != null && block.start == last.end)
			blocks.set(blocks.size() - 1, new Block(last.start, block.end));
		else {
			blocks.add(block);
			last = block;
		}
		size = Math.max(size, block.end);
	}

	public int count() {
		int count = 0;
		for (Block block : blocks)
			count += block.end - block.start;
		return count;
	}

	public int size() {
		return size;
	}

	public boolean isEmpty() {
		return size() == 0;
	}

	public static BinaryBlockSeries from(BinaryMask1D mask, int size) {
		BinaryBlockSeries series = new BinaryBlockSeries(size);
		int start = -1;
		for (int i = 0; i < size; i++) {
			if (mask.get(i) && start < 0) {
				start = i;
			} else if (!mask.get(i) && start >= 0) {
				series.add(start, i);
				start = -1;
			}
		}
		if (start >= 0)
			series.add(start, size);
		return series;
	}

	private static class Block {

		private final int start, end;

		private Block(int start, int end) {
			this.start = start;
			this.end = end;
		}

	}

}
