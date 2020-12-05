package de.hechler.adventofcode;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 * see: https://adventofcode.com/2020/day/1
 *
 */
public class Day05 {


	
	public static void main(String[] args) throws FileNotFoundException {
		String rot = "abcdefghijklmnopqrstuvwxyz";
		String rotrot = rot+rot;
		String text = "obm rfokb o xigh w qzcgsf";
		text = "gml, xwwd qgmj se sdkg";
		text = "gcaspcrm, vcas vor hc ohhsbhwcb w'a xcvbbm! ogy ibrsfghobr! vwg tfwsbrg wg diby? boas wb ywzzsr mci'fs ghcaoqv";
		for (int r=0; r<26; r++) {
			System.out.print(r+": ");
			for (int i=0; i<text.length(); i++) { 
				int pos = rotrot.indexOf(text.charAt(i));
				if (pos == -1) {
					System.out.print(text.charAt(i));
					continue;
				}
				System.out.print(rotrot.charAt(pos+r));
			}
			System.out.println();
		}
	}
	
}
