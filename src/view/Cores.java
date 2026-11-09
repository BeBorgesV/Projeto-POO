package view;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

/** Cor de tela de cada jogador (as mesmas das peças de ativação). */
final class Cores {
	private static final Map<String, Color> CORES = new HashMap<>();

	static {
		CORES.put("azul", new Color(47, 95, 179));
		CORES.put("amarelo", new Color(242, 194, 48));
		CORES.put("preto", new Color(60, 60, 60));
		CORES.put("vermelho", new Color(208, 48, 48));
		CORES.put("verde", new Color(60, 154, 60));
	}

	private Cores() {
	}

	static Color de(String cor) {
		Color c = CORES.get(cor.toLowerCase());
		return c == null ? Color.GRAY : c;
	}

	/** Cor do texto escrito sobre a cor do jogador. */
	static Color texto(String cor) {
		return "amarelo".equalsIgnoreCase(cor) ? Color.BLACK : Color.WHITE;
	}
}
