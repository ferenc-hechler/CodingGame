package de.hechler.adventofcode;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

/**
 * see: https://adventofcode.com/2020/day/1
 *
 */
public class Day08 {

	public static class VM {
		public int acc;
		public int ip;
		public boolean finished;
		private List<Command> code;
		public VM() {
			this(new ArrayList<>());
		}
		public VM(List<Command> code) {
			this.acc = 0;
			this.ip = 0;
			this.code = code; 
			this.finished = false;
		}
		public void step() {
			Command command = code.get(ip);
			System.out.println(ip+": "+command);
			command.execute(this);
			ip += 1;
		}
		public int getAcc() { return acc; }
		public int getIp() { return ip; }
		public void addCommand(Command command) { code.add(command); }
		public List<Command> getCode() { return code; }
	}
	
	public interface Command {
		void execute(VM vm);
	}
	
	public static class NopCommand implements Command {
		public int num;
		public NopCommand(int num) {}
		@Override public void execute(VM vm) {}
		@Override public String toString() { return "nop "+num; }
	}
	
	public static class JmpCommand implements Command {
		public int offset;
		public JmpCommand(int offset) { this.offset = offset; }
		@Override public void execute(VM vm) { vm.ip += offset-1; }
		@Override public String toString() { return "jmp "+offset; }
	}
	
	public static class AccCommand implements Command {
		private int add;
		public AccCommand(int add) { this.add = add; }
		@Override public void execute(VM vm) { vm.acc += add; }
		@Override public String toString() { return "acc "+add; }
	}
	
	public static class FinCommand implements Command {
		public FinCommand() {}
		@Override public void execute(VM vm) { vm.finished = true; }
		@Override public String toString() { return "fin"; }
	}
	
	
	
	private final static String CMD_RX = "^(nop|acc|jmp) ([+-][0-9]+)$";

	public static Command createCommand(String line) {
		if (!line.matches(CMD_RX)) {
			throw new RuntimeException("invalid command '"+line+"'");
		}
		String cmd = line.replaceFirst(CMD_RX, "$1");
		int num = Integer.parseInt(line.replaceFirst(CMD_RX, "$2"));
		switch(cmd) {
		case ("nop"):
			return new NopCommand(num);
		case ("acc"):
			return new AccCommand(num);
		case ("jmp"):
			return new JmpCommand(num);
		default:
			throw new RuntimeException("unknown command '"+cmd+"' in line '"+line+"'");
		}
	}
	
	public static void mainPart1(String[] args) throws FileNotFoundException {
		VM vm = new VM();
		try (Scanner scanner = new Scanner(new File("input/day08.txt"))) {
			while (scanner.hasNext()) {
				String line = scanner.nextLine();
				vm.addCommand(createCommand(line));
			}
		}
		Set<Integer> visitedCodeLines = new LinkedHashSet<>();
		while (!visitedCodeLines.contains(vm.getIp())) {
			visitedCodeLines.add(vm.getIp());
			vm.step();
		}
		System.out.println("acc="+vm.getAcc());
	}


	public static void mainPart2(String[] args) throws FileNotFoundException {
		VM vm = new VM();
		try (Scanner scanner = new Scanner(new File("input/day08.txt"))) {
			while (scanner.hasNext()) {
				String line = scanner.nextLine();
				vm.addCommand(createCommand(line));
			}
		}
		vm.addCommand(new FinCommand());
		List<Command> origCode = vm.getCode();
		for (int i=0; i<origCode.size(); i++) {
			List<Command> copyCode = new ArrayList<>(origCode);
			Command cmd = copyCode.get(i);
			if (cmd instanceof AccCommand) {
				continue;
			}
			if (cmd instanceof NopCommand) {
				copyCode.set(i, new JmpCommand(((NopCommand) cmd).num));
			}
			if (cmd instanceof JmpCommand) {
				copyCode.set(i, new NopCommand(((JmpCommand) cmd).offset));
			}
			vm = new VM(copyCode);
			Set<Integer> visitedCodeLines = new LinkedHashSet<>();
			while (!visitedCodeLines.contains(vm.getIp())) {
				visitedCodeLines.add(vm.getIp());
				vm.step();
				if (vm.finished == true) {
					System.out.println("changed "+i+" -> acc="+vm.getAcc());
					return; 
				}
			}
		}
		System.out.println("acc="+vm.getAcc());
	}



	public static void main(String[] args) throws FileNotFoundException {
		mainPart2(args);
	}

	
}
