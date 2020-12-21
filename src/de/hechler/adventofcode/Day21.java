package de.hechler.adventofcode;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

/**
 * see: https://adventofcode.com/2020/day/1
 *
 */
public class Day21 {

	private final static String CONTAINS_RX = "^([a-z ]+) \\(contains ([a-z, ]+)\\)$";

	static class Food {
		Set<String> ingredients;
		Set<String> allergenes;
		Food(String[] ingredients, String[] allergenes) {
			this.ingredients = new LinkedHashSet<>(Arrays.asList(ingredients));
			this.allergenes = new LinkedHashSet<>(Arrays.asList(allergenes));
		}
		@Override
			public String toString() {
				return "F["+ingredients.toString()+"(contains "+allergenes+")]";
			}
	}
	
	static List<Food> foods;
	static Map<String, Set<Food>> allergene2foodMap;
	static Map<String, Set<Food>> ingredients2foodMap;
	static Map<String, Set<String>> allergene2ingredientsMap;
	static Set<String> ingredientsWithAllergenes;
	
	public static void mainPart1() throws FileNotFoundException {
		try (Scanner scanner = new Scanner(new File("input/day21.txt"))) {
			foods = new ArrayList<>();
			allergene2foodMap = new LinkedHashMap<>();
			ingredients2foodMap = new LinkedHashMap<>();
			while (scanner.hasNext()) {
				String line = scanner.nextLine().trim();
				if (line.isEmpty()) {
					continue;
				}
				if (!line.matches(CONTAINS_RX)) {
					throw new RuntimeException("invvalid contains line: '"+line+"'");
				}
				String[] ingredients = line.replaceFirst(CONTAINS_RX, "$1").trim().split(" "); 
				String[] allergenes = line.replaceFirst(CONTAINS_RX, "$2").trim().split(", ");
				Food food = new Food(ingredients, allergenes);
				add2maps(food);
			}
			System.out.println(foods);
			allergene2ingredientsMap = new LinkedHashMap<>();
			ingredientsWithAllergenes = new LinkedHashSet<>();
			for (String allergene:allergene2foodMap.keySet()) {
				Set<Food> foodList = allergene2foodMap.get(allergene);
				Set<String> commonIngredients = null;
				for (Food food:foodList) {
					if (commonIngredients == null) {
						commonIngredients = new LinkedHashSet<>(food.ingredients);
					}
					else {
						commonIngredients.retainAll(food.ingredients);
					}
				}
				System.out.println(allergene+": "+commonIngredients);
				allergene2ingredientsMap.put(allergene, commonIngredients);
				ingredientsWithAllergenes.addAll(commonIngredients);
			}
			
			int cnt = 0;
			for (Food food:foods) {
				Set<String> ings = new HashSet<>(food.ingredients);
				ings.removeAll(ingredientsWithAllergenes);
				cnt += ings.size();
			}
			System.out.println(cnt);
		}
	}

	public static void mainPart2() throws FileNotFoundException {
		try (Scanner scanner = new Scanner(new File("input/day21.txt"))) {
			foods = new ArrayList<>();
			allergene2foodMap = new LinkedHashMap<>();
			ingredients2foodMap = new LinkedHashMap<>();
			while (scanner.hasNext()) {
				String line = scanner.nextLine().trim();
				if (line.isEmpty()) {
					continue;
				}
				if (!line.matches(CONTAINS_RX)) {
					throw new RuntimeException("invvalid contains line: '"+line+"'");
				}
				String[] ingredients = line.replaceFirst(CONTAINS_RX, "$1").trim().split(" "); 
				String[] allergenes = line.replaceFirst(CONTAINS_RX, "$2").trim().split(", ");
				Food food = new Food(ingredients, allergenes);
				add2maps(food);
			}
			System.out.println(foods);
			allergene2ingredientsMap = new LinkedHashMap<>();
			ingredientsWithAllergenes = new LinkedHashSet<>();
			for (String allergene:allergene2foodMap.keySet()) {
				Set<Food> foodList = allergene2foodMap.get(allergene);
				Set<String> commonIngredients = null;
				for (Food food:foodList) {
					if (commonIngredients == null) {
						commonIngredients = new LinkedHashSet<>(food.ingredients);
					}
					else {
						commonIngredients.retainAll(food.ingredients);
					}
				}
				System.out.println(allergene+": "+commonIngredients);
				allergene2ingredientsMap.put(allergene, commonIngredients);
				ingredientsWithAllergenes.addAll(commonIngredients);
			}

			Map<String, Set<String>> reducedAllergene2ingredientsMap = new LinkedHashMap<>();
			for (String allergene:allergene2ingredientsMap.keySet()) {
				reducedAllergene2ingredientsMap.put(allergene, new LinkedHashSet<>(allergene2ingredientsMap.get(allergene)));
			}
			
			boolean changes = true;
			while (changes) {
				changes = false; 
				for (String allergene:reducedAllergene2ingredientsMap.keySet()) {
					if (reducedAllergene2ingredientsMap.get(allergene).size() == 1) {
						String ing = reducedAllergene2ingredientsMap.get(allergene).iterator().next();
						for (String a:reducedAllergene2ingredientsMap.keySet()) {
							Set<String> ings = reducedAllergene2ingredientsMap.get(a);
							if (ings.size() > 1) {
								if (ings.remove(ing)) {
									changes = true;
								}
							}
						}
						if (changes) {
							break;
						}
					}
				}
			}

			for (String allergene:reducedAllergene2ingredientsMap.keySet()) {
				System.out.println("R-"+allergene+": "+reducedAllergene2ingredientsMap.get(allergene));
			}
			
			List<String> sortAlgs = new ArrayList<>();
			reducedAllergene2ingredientsMap.keySet().forEach(alg -> sortAlgs.add(alg));
			Collections.sort(sortAlgs);
			String seperator = "";
			for (String alg:sortAlgs) {
				System.out.print(seperator+reducedAllergene2ingredientsMap.get(alg).iterator().next());
				seperator = ",";
			}
		}
	}

	private static void add2maps(Food food) {
		foods.add(food);
		for (String ingredient:food.ingredients) {
			Set<Food> foods = ingredients2foodMap.get(ingredient);
			if (foods == null) {
				foods = new LinkedHashSet<>();
				ingredients2foodMap.put(ingredient, foods);
			}
			foods.add(food);
		}
		for (String allergene:food.allergenes) {
			Set<Food> foods = allergene2foodMap.get(allergene);
			if (foods == null) {
				foods = new LinkedHashSet<>();
				allergene2foodMap.put(allergene, foods);
			}
			foods.add(food);
		}
	}

	public static void main(String[] args) throws FileNotFoundException {
		mainPart2();
	}

	
}
