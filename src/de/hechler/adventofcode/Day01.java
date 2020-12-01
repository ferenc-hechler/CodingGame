package de.hechler.adventofcode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * see: https://adventofcode.com/2020/day/1
 *
 */
public class Day01 {

	public static void mainPart1(String[] args) {
		List<Integer> values = new ArrayList<>();
		try (Scanner scanner = new Scanner(System.in)) {
			while (true) {
				int value = scanner.nextInt();
				for (int otherValue:values) {
					if (value + otherValue == 2020) {
						System.out.println(otherValue+" * " + value+" = "+ value*otherValue);
						return;
					}
				}
				values.add(value);
			}
		}
	}

	public static void mainPart2(String[] args) {
		try {
			List<Long> values = new ArrayList<>();
			try (Scanner scanner = new Scanner(System.in)) {
				while (true) {
					long value = scanner.nextInt();
					for (int i=0; i<values.size()-1; i++) {
						for (int j=i+1; j<values.size(); j++) {
							if (values.get(i) + values.get(j) + value == 2020) {
								System.out.println(values.get(i)+" * " + values.get(j)+" * " + value+" = "+ values.get(i)*values.get(j)*value);
								return;
							}
						}
					}
					values.add(value);
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void mainPart1Short(String[] args) {
//		Set<Integer> values = new HashSet<>(Arrays.asList(1721, 979, 366, 299, 675, 1456));
		Set<Integer> values = new HashSet<>(Arrays.asList(408,1614,1321,1028,1018,2008,1061,1433,1434,1383,1645,1841,1594,1218,1729,1908,1237,1152,1771,1837,1709,1449,1876,1763,1676,1491,1983,1743,1845,999,1478,1929,1819,1385,1308,1703,1246,1831,1964,1469,1977,1488,1698,1640,1513,1136,1794,1685,1802,1520,1807,1654,1547,1917,1792,1949,1268,1626,1493,1534,1700,1844,1146,1049,1811,1627,1630,1755,1887,1290,1446,1968,168,1749,1479,1651,1646,1839,14,1918,1568,1554,1926,1942,1862,1966,1536,1599,1439,1766,1643,1045,1537,1786,1596,1954,1390,1981,1362,1292,1573,1541,1515,1567,1860,1066,1879,1800,1309,1533,1812,1774,1119,1602,1677,482,1054,1424,1631,1829,1550,1636,1604,185,1642,1304,1843,1773,1667,1530,1047,1584,1958,1160,1570,1705,1582,1692,1886,1673,1842,1402,1517,1805,1386,1165,1867,1153,1467,1473,1803,1967,1485,1448,1922,1258,1590,1996,1208,1241,1412,1610,1219,523,1813,1123,1916,1861,1020,1783,1052,1140,1994,1761,747,1885,1675,1957,1476,1382,1878,1099,1882,855,1905,1037,1714,1988,1648,1135,1859,1798,1333,1158,1909,652,1934,1830,1442,1224));
		values.stream().filter(x -> values.contains(2020-x)).limit(1).forEach(x->System.out.println(x*(2020-x)));
	}

	public static void mainPart2Short(String[] args) {
//		Set<Integer> values = new HashSet<>(Arrays.asList(1721, 979, 366, 299, 675, 1456));
		Set<Integer> vs = new HashSet<>(Arrays.asList(408,1614,1321,1028,1018,2008,1061,1433,1434,1383,1645,1841,1594,1218,1729,1908,1237,1152,1771,1837,1709,1449,1876,1763,1676,1491,1983,1743,1845,999,1478,1929,1819,1385,1308,1703,1246,1831,1964,1469,1977,1488,1698,1640,1513,1136,1794,1685,1802,1520,1807,1654,1547,1917,1792,1949,1268,1626,1493,1534,1700,1844,1146,1049,1811,1627,1630,1755,1887,1290,1446,1968,168,1749,1479,1651,1646,1839,14,1918,1568,1554,1926,1942,1862,1966,1536,1599,1439,1766,1643,1045,1537,1786,1596,1954,1390,1981,1362,1292,1573,1541,1515,1567,1860,1066,1879,1800,1309,1533,1812,1774,1119,1602,1677,482,1054,1424,1631,1829,1550,1636,1604,185,1642,1304,1843,1773,1667,1530,1047,1584,1958,1160,1570,1705,1582,1692,1886,1673,1842,1402,1517,1805,1386,1165,1867,1153,1467,1473,1803,1967,1485,1448,1922,1258,1590,1996,1208,1241,1412,1610,1219,523,1813,1123,1916,1861,1020,1783,1052,1140,1994,1761,747,1885,1675,1957,1476,1382,1878,1099,1882,855,1905,1037,1714,1988,1648,1135,1859,1798,1333,1158,1909,652,1934,1830,1442,1224));
		vs.stream().map(x->vs.stream().filter(v->v!=x&&v!=2020-v-x&&x!=2020-v-x&&vs.contains(2020-v-x)).map(v->x*v*(2020-v-x))).flatMap(x->x).limit(1).forEach(System.out::println);
		vs.stream().map(x->vs.stream().filter(v->v!=x&&v!=2020-v-x&&x!=2020-v-x&&vs.contains(2020-v-x)).map(v->x*v*(2020-v-x))).flatMap(x->x).findFirst().ifPresent(System.out::println);
		int result = vs.stream().map(x->vs.stream().filter(v->v!=x&&v!=2020-v-x&&x!=2020-v-x&&vs.contains(2020-v-x)).map(v->x*v*(2020-v-x))).flatMap(x->x).findFirst().get();
		System.out.println(result);
	}


	
	public static void main(String[] args) {
		mainPart2Short(args);
	}

	
}
