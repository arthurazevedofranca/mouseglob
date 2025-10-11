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
package dcc.mouseglob.visit;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AlternationCounter {

	private final List<Object> sequence;
	private final int sequenceLength;
	private final int cycleLength;

	private int alternationCount;
	private boolean hasCounted;

	public AlternationCounter(List<?> sequence, int cycleLength) {
		this.sequence = new ArrayList<>(sequence);
		this.sequenceLength = sequence.size();
		this.cycleLength = cycleLength;
	}

	private void countAlternations() {
		alternationCount = 0;
		for (int i = cycleLength - 1; i < sequenceLength; i++) {
			Set<Object> seen = new HashSet<>(cycleLength);
			for (int k = 0; k < cycleLength; k++)
				seen.add(sequence.get(i - k));
			if (seen.size() == cycleLength)
				alternationCount++;
		}
		hasCounted = true;
	}

	public int getAlternationCount() {
		if (!hasCounted)
			countAlternations();
		return alternationCount;
	}

	public double getAlternationScore() {
		if (sequenceLength < cycleLength)
			return Double.NaN;
		if (!hasCounted)
			countAlternations();
		return (double) alternationCount / (sequenceLength - cycleLength + 1);
	}

	public static void main(String[] args) {
		List<Character> seq = new ArrayList<>();
		for (char c : "ACBCACABACACACACABCACABCABCABCACBCABACABCABCACABCACA"
				.toCharArray())
			// ABCBACBCAB
			seq.add(c);
		AlternationCounter analysis = new AlternationCounter(seq, 3);
		System.out.println(seq);
		System.out.printf("Count: %d, Score: %.2f %%\n",
				analysis.getAlternationCount(),
				100 * analysis.getAlternationScore());
	}

}
