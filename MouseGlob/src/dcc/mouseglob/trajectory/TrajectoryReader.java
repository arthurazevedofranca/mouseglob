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
package dcc.mouseglob.trajectory;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

/**
 * @author Daniel Coelho de Castro
 */
public class TrajectoryReader {
	private static final String SEPARATOR = "\"?,\"?";
	private Scanner scanner;
	private int numColumns;

	private double scale;
	private long time;
	private List<Trajectory> trajectories;

	/**
	 * Constructor for the <code>TrajectoryScanner</code> class.
	 * 
	 * @param filename
	 *            - the coordinates file name
	 * @throws FileNotFoundException
	 */
	public TrajectoryReader(String filename) throws FileNotFoundException {
		scanner = new Scanner(new File(filename));
		scanner.useDelimiter(SEPARATOR);
	}

	/**
	 * Reads and parses the content of the coordinates file. Should be called
	 * right after the constructor.
	 * 
	 * @throws InputMismatchException
	 */
	public void parse() throws InputMismatchException {
		scanner.nextLine(); // Skip header line
		scanner.nextLine(); // Skip size line
		numColumns = 2 + scanner.nextInt();
		scanner.nextLine();

		scanner.next(); // Skip "Scale" text
		scale = scanner.nextFloat();
		scanner.nextLine(); // Skip remainder of scale line

		createTrajectories();
	}

	/**
	 * Gets the scale acquired from the file.
	 * 
	 * @return the scale
	 */
	public double getScale() {
		return scale;
	}

	/**
	 * Gets the time window of the trajectories.
	 * 
	 * @return the total time
	 */
	public long getTime() {
		return time;
	}

	/**
	 * Gets the trajectories acquired from the file.
	 * 
	 * @return a vector containing the trajectories
	 */
	public List<Trajectory> getTrajectories() {
		return trajectories;
	}

	private void createTrajectories() throws InputMismatchException {
		String[] tableLine = scanner.nextLine().split(SEPARATOR);
		trajectories = new ArrayList<Trajectory>();
		time = 0;

		int n = (tableLine.length - 1) / numColumns;
		for (int i = 0; i < n; i++) {
			String s = tableLine[1 + i * numColumns];
			Trajectory temp = new Trajectory();
			temp.setName(s.substring(0, s.lastIndexOf(" x")));
			trajectories.add(temp);
		}

		int skip = numColumns - 2;

		while (scanner.hasNextLine()) {
			time = scanner.nextLong();
			for (Trajectory trajectory : trajectories) {
				double x = scanner.nextDouble();
				double y = scanner.nextDouble();
				if (scale != 0) {
					trajectory.addPoint(x / scale, y / scale, time);
				} else
					trajectory.addPoint(x, y, time);
				for (int s = 0; s < skip; s++)
					scanner.next();
			}
			scanner.nextLine();
		}
	}
}
