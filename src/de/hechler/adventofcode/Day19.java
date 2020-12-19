package de.hechler.adventofcode;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * see: https://adventofcode.com/2020/day/1
 *
 */
public class Day19 {

	private final static String FINAL_RX = "^(\\d+): \"([ab])\"$";
	private final static String SEQUENCE_RX = "^(\\d+): \\d+( \\d+)*$";
	private final static String SEQUENCE_RIGHT_RX = "^(\\d+): ([ 0-9]+)*$";
	private final static String ALTERNATIVE_RX = "^(\\d+): ([ 0-9]+) [|] ([ 0-9]+)$";
	private final static String MESSAGE_RX = "^([ab]+)$";

	interface Rule {
		int check(String txt, int pos);
	}
	
	static Map<Integer, Rule> rules;
	
	static class FinalRule implements Rule {
		public int id;
		private char c;
		FinalRule(int id, char c) {
			this.id = id;
			this.c = c;
		}
		@Override
		public int check(String txt, int pos) {
			if (pos >= txt.length()) {
				return -1;
			}
			if (txt.charAt(pos) == c) {
				return pos+1;
			}
			return -1;
		}
	}
	
	static class SequenceRule implements Rule {
		public int id;
		private List<Integer> sequenceRules;
		public SequenceRule(int id) {
			this.id = id;
			this.sequenceRules = new ArrayList<>();
		}
		public void addRule(int ruleId) {
			sequenceRules.add(ruleId);
		}
		@Override
		public int check(String txt, int pos) {
			int result = pos;
			for (int ruleId:sequenceRules) {
				result = rules.get(ruleId).check(txt, result);
				if (result == -1) {
					return -1;
				}
			}
			return result;
		}
	}
	
	static class AlternativeRule implements Rule {
		public int id;
		private SequenceRule ruleA;
		private SequenceRule ruleB;
		public AlternativeRule(int id, SequenceRule ruleA, SequenceRule ruleB) {
			this.id = id;
			this.ruleA = ruleA;
			this.ruleB = ruleB;
		}
		@Override
		public int check(String txt, int pos) {
			int result = ruleA.check(txt, pos);
			if (result == -1) {
				result = ruleB.check(txt, pos);
			}
			return result;
		}
	}
	
	public static void mainPart1() throws FileNotFoundException {
		try (Scanner scanner = new Scanner(new File("input/day19.txt"))) {
			rules = new LinkedHashMap<>();
			while (scanner.hasNext()) {
				String line = scanner.nextLine().trim();
				if (line.isEmpty()) {
					break;
				}
				if (line.matches(FINAL_RX)) {
					System.out.println("FINAL: "+line);
					int ruleId = Integer.parseInt(line.replaceFirst(FINAL_RX, "$1"));
					String c = line.replaceFirst(FINAL_RX, "$2");
					rules.put(ruleId, new FinalRule(ruleId, c.charAt(0)));
				}
				else if (line.matches(SEQUENCE_RX)) {
					System.out.println("SEQUENCE: "+line);
					int ruleId = Integer.parseInt(line.replaceFirst(SEQUENCE_RX, "$1"));
					SequenceRule rule = new SequenceRule(ruleId);
					String[] seqs = line.replaceFirst(SEQUENCE_RIGHT_RX, "$2").split(" ");
					for (String seq:seqs) {
						rule.addRule(Integer.parseInt(seq));
					}
					rules.put(ruleId, rule);
				}
				else if (line.matches(ALTERNATIVE_RX)) {
					System.out.println("ALTERNATIVE: "+line);
					int ruleId = Integer.parseInt(line.replaceFirst(ALTERNATIVE_RX, "$1"));
					String[] aIds = line.replaceFirst(ALTERNATIVE_RX, "$2").trim().split(" ");
					String[] bIds = line.replaceFirst(ALTERNATIVE_RX, "$3").trim().split(" ");
					SequenceRule ruleA = new SequenceRule(ruleId);
					for (String aId:aIds) {
						ruleA.addRule(Integer.parseInt(aId));
					}
					SequenceRule ruleB = new SequenceRule(ruleId);
					for (String bId:bIds) {
						ruleB.addRule(Integer.parseInt(bId));
					}
					AlternativeRule rule = new AlternativeRule(ruleId, ruleA, ruleB);
					rules.put(ruleId, rule);
				}
				else {
					throw new RuntimeException("invalid rule '"+line+"'");
				}
			}
			System.out.println();
			int cntValid = 0;
			while (scanner.hasNext()) {
				String msg =scanner.next();
				if (!msg.matches(MESSAGE_RX)) {
					throw new RuntimeException("invalid msg '"+msg+"'");
				}
				int result = rules.get(0).check(msg, 0);
				if (result == msg.length()) {
					System.out.println(msg);
					cntValid++;
				}
				else {
					System.err.println(msg);
				}
			}
			System.out.println("VALID: "+cntValid);
		}
	}

	public static void main(String[] args) throws FileNotFoundException {
		mainPart1();
	}

	
}
