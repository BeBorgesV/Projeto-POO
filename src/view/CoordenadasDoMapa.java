package view;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Onde fica cada parada na imagem do tabuleiro, em pixels da imagem original.
 * Parada dupla tem 2 pontos e Karakorum tem 5, um para cada peão.
 */
class CoordenadasDoMapa {
	private final Map<String, List<Point>> pontos = new HashMap<>();

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
