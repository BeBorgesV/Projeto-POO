package view;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Onde fica cada parada na imagem do tabuleiro, em pixels da imagem original.
 * Parada dupla tem 2 pontos e Karakorum tem 5, um para cada peão.
 *
 * Também guarda um ponto para os outros elementos desenhados: o ícone de
 * tributo de cada província, o centro de cada cidade, as cabeças do Khan,
 * os quadrados de bônus dos conselheiros e os 5 espaços da área de melhorias.
 */
class CoordenadasDoMapa {
	static final String KHAN = "KHAN:";
	static final String BONUS = "BONUS:";
	static final String AREA = "AREA:";

	private final Map<String, List<Point>> pontos = new HashMap<>();
	private final Map<String, Point> lugares = new HashMap<>();

	CoordenadasDoMapa() {
		ponto("Karakorum", 1310, 1390, 1585, 1390, 1330, 1470, 1565, 1470, 1447, 1530);

		ponto("R1", 488, 247);
		ponto("R2", 871, 166, 935, 253);
		ponto("R3", 122, 532);
		ponto("R4", 465, 532);
		ponto("R5", 851, 552);
		ponto("R6", 1272, 494);
		ponto("R7", 1084, 842);
		ponto("R8", 1752, 227);
		ponto("R9", 1560, 668);
		ponto("R10", 1894, 590);
		ponto("R11", 2179, 494, 2280, 494);

		ponto("P1", 465, 848, 456, 944);
		ponto("P2", 837, 1191);
		ponto("P3", 575, 1418);
		ponto("P4", 959, 1554);
		ponto("P5", 209, 1496);
		ponto("P6", 450, 1641);
		ponto("P7", 174, 1911);
		ponto("P8", 668, 1882, 779, 1897);
		ponto("P9", 1272, 1824);
		ponto("P10", 1476, 2048);
		ponto("P11", 1197, 2237);

		ponto("C1", 2492, 654);
		ponto("C2", 2766, 877);
		ponto("C3", 2522, 1075);
		ponto("C4", 2141, 1255);
		ponto("C5", 1888, 1598);
		ponto("C6", 2164, 1679);
		ponto("C7", 2556, 1496, 2556, 1606);
		ponto("C8", 2527, 2025);
		ponto("C9", 1772, 2208, 1882, 2231);
		ponto("C10", 2231, 2231);
		ponto("C11", 2585, 2400);

		lugar("Russia 1", 622, 415);
		lugar("Russia 2", 861, 363);
		lugar("Russia 3", 799, 934);
		lugar("Russia 4", 1245, 923);
		lugar("Russia 5", 1961, 751);
		lugar("Persia 1", 519, 1214);
		lugar("Persia 2", 602, 1660);
		lugar("Persia 3", 1515, 1629);
		lugar("China 1", 2158, 1037);
		lugar("China 2", 2397, 1556);
		lugar("China 3", 2065, 1826);
		lugar("China 4", 1681, 1919);

		lugar("Kiev", 301, 394);
		lugar("Moscou", 1079, 623);
		lugar("Sarai", 1722, 415);
		lugar("Samarcanda", 809, 1390);
		lugar("Bagda", 249, 1660);
		lugar("Cabul", 1266, 2034);
		lugar("Kaesong", 2552, 830);
		lugar("Pequim", 2075, 1453);
		lugar("Cantao", 2449, 2241);

		lugar(KHAN + "Russia 1", 633, 602);
		lugar(KHAN + "Russia 5", 1743, 903);
		lugar(KHAN + "Persia 2", 799, 1722);
		lugar(KHAN + "China 3", 2127, 2054);
		lugar(KHAN + "MELHORIAS", 93, 2781);

		lugar(BONUS + "R2", 1380, 156);
		lugar(BONUS + "R11", 2417, 290);
		lugar(BONUS + "P1", 228, 1037);
		lugar(BONUS + "P8", 716, 2096);
		lugar(BONUS + "C7", 2749, 1691);
		lugar(BONUS + "C9", 1691, 2386);
		lugar(BONUS + "Karakorum", 1452, 1411);

		int[] xDaArea = { 311, 571, 830, 1089, 1349 };
		for (int i = 0; i < xDaArea.length; i++) {
			lugar(AREA + i, xDaArea[i], 2791);
		}
	}

	private void lugar(String nome, int x, int y) {
		lugares.put(nome, new Point(x, y));
	}

	/** Ponto (na imagem original) de uma província, cidade ou lugar especial; null se não houver. */
	Point lugar(String nome) {
		return lugares.get(nome);
	}

	/** Recebe as coordenadas em pares: x1, y1, x2, y2, ... */
	private void ponto(String parada, int... xy) {
		List<Point> lista = new ArrayList<>();
		for (int i = 0; i < xy.length; i += 2) {
			lista.add(new Point(xy[i], xy[i + 1]));
		}
		pontos.put(parada, lista);
	}

	List<Point> pontosDe(String parada) {
		return pontos.get(parada);
	}

	/** Parada que tem algum ponto a até {@code raio} pixels de (x, y), ou null. */
	String paradaEm(int x, int y, int raio) {
		for (Map.Entry<String, List<Point>> e : pontos.entrySet()) {
			for (Point p : e.getValue()) {
				if (p.distance(x, y) <= raio) {
					return e.getKey();
				}
			}
		}
		return null;
	}
}
