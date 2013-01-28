/*
 * Sleuth Kit Data Model
 *
 * Copyright 2011 Basis Technology Corp.
 * Contact: carrier <at> sleuthkit <dot> org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.sleuthkit.datamodel;

import difflib.Delta;
import difflib.DiffUtils;
import difflib.Patch;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DiffUtil implements Runnable{

	static String pathOriginal, pathRevised, title;
	public DiffUtil(String path1, String path2, String title){
		pathOriginal = path1;
		pathRevised = path2;
		DiffUtil.title = title;
	}
	private static List<String> fileToLines(String filename) {
		List<String> lines = new LinkedList<String>();
		String line = "";
		try {
			BufferedReader in = new BufferedReader(new FileReader(new java.io.File(filename).getAbsolutePath()));
			while ((line = in.readLine()) != null) {
				lines.add(line);
			}
		} catch (IOException ex) {
			Logger.getLogger(DiffUtil.class.getName()).log(Level.SEVERE, "Couldn't read file", ex);
			throw new RuntimeException(ex);
		}
		return lines;
	}
	/**
	 * Returns the diff between the two given files
	 * @param pathOriginal The path to the original file
	 * @param pathRevised The path to the revised (new) file
	 * @return A representation of the diff
	 */
	public String getDiff() {
		List<String> originalLines, revisedLines;	
		originalLines = fileToLines(pathOriginal);
		revisedLines = fileToLines(pathRevised);
		java.io.File outp = new java.io.File(DataModelTestSuite.getRsltPath() + java.io.File.separator + title.replace(".txt","_Diff.txt"));
		// Compute diff. Get the Patch object. Patch is the container for computed deltas.
		Patch patch = DiffUtils.diff(originalLines, revisedLines);
		StringBuilder diff = new StringBuilder();

		for (Delta delta : patch.getDeltas()) {
			diff.append(delta.toString());
			diff.append("\n");
		}
		try {
			FileWriter out = new FileWriter(outp);
			out.append(diff);
			out.flush();
			out.close();
		} catch (IOException ex) {
			Logger.getLogger(DiffUtil.class.getName()).log(Level.SEVERE, "Couldn't write Diff to file", ex);
		}
		System.out.println(diff.toString());
		return diff.toString();
	}

	@Override
	public void run() {
		getDiff();
	}
}