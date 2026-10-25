package model;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * Monta o tabuleiro.
 *
 * O mapa oficial é o do jogo de verdade. O mapa de exemplo é pequeno e serve
 * só para os testes das regras:
 *
 * <pre>
 *                 [Kiev]           [Sarai]
 *                   |                 |
 *   Karakorum ---- P4 ------------- P5 ------ P6 --[Cabul]
 *       |                                     |
 *      P1 ---- P2(dupla) ---- P3 -------------+
 *     /   \    /               |
 *  [Samarcanda]              [Bagda]
 *
 *  Províncias: Estepe (ESPADA, do Khan)  vizinha de P1, P4
 *              Vale   (MOEDA)            vizinha de P1, P2
 *              Deserto(YURT)             vizinha de P2, P3
 *              Planalto(ESPADA)          vizinha de P5, P6
 *              Montanha(YURT, do Khan)   vizinha de P3, P6
 * </pre>
 */
final class FabricaTabuleiro {

	private FabricaTabuleiro() {
	}

	/**
	 * Mapa do tabuleiro oficial, transcrito da imagem do tabuleiro.
	 * Paradas: R1-R11 na Rússia, P1-P11 na Pérsia, C1-C11 na China e Karakorum.
	 * As duplas são as 6 paradas com conselheiro.
	 */
	static Tabuleiro criarTabuleiroOficial(Random sorteio) {
		Tabuleiro t = new Tabuleiro(sorteio);

		List<String> duplas = Arrays.asList("R2", "R11", "P1", "P8", "C7", "C9");
		for (String regiao : Arrays.asList("R", "P", "C")) {
			for (int i = 1; i <= 11; i++) {
				String nome = regiao + i;
				t.novaParada(nome, duplas.contains(nome) ? Parada.Tipo.DUPLA : Parada.Tipo.SIMPLES);
			}
		}

		String k = Tabuleiro.KARAKORUM;
		// cada linha liga a primeira parada a todas as outras da linha
		ligar(t, "R1", "R2", "R3", "R4", "R5");
		ligar(t, "R2", "R6");
		ligar(t, "R3", "R4");
		ligar(t, "R4", "P1");
		ligar(t, "R5", "R6", "R7", "P1");
		ligar(t, "R6", "R7", "R9");
		ligar(t, "R7", "P2");
		ligar(t, "R8", "R9", "R10");
		ligar(t, "R9", "R10", k);
		ligar(t, "R10", "R11");
		ligar(t, "R11", "C1", k);
		ligar(t, "P1", "P2", "P5");
		ligar(t, "P2", "P3", "P4");
		ligar(t, "P3", "P4", "P5");
		ligar(t, "P4", "P9", k);
		ligar(t, "P5", "P6", "P7");
		ligar(t, "P6", "P7", "P8");
		ligar(t, "P8", "P9");
		ligar(t, "P9", "P10", "P11", "C5");
		ligar(t, "P10", "P11", "C9");
		ligar(t, "C1", "C2", "C3");
		ligar(t, "C2", "C3");
		ligar(t, "C3", "C4", "C7");
		ligar(t, "C4", "C5", "C6", k);
		ligar(t, "C5", "C6", "C9");
		ligar(t, "C6", "C8");
		ligar(t, "C7", "C8");
		ligar(t, "C8", "C10", "C11");
		ligar(t, "C9", "C10");
		ligar(t, "C10", "C11");

		t.novaCidade("Kiev", Regiao.RUSSIA, "R1", "R3", "R4");
		t.novaCidade("Moscou", Regiao.RUSSIA, "R5", "R6", "R7");
		t.novaCidade("Sarai", Regiao.RUSSIA, "R8", "R9", "R10");
		t.novaCidade("Samarcanda", Regiao.PERSIA, "P2", "P3", "P4");
		t.novaCidade("Bagda", Regiao.PERSIA, "P5", "P6", "P7");
		t.novaCidade("Cabul", Regiao.PERSIA, "P9", "P10", "P11");
		t.novaCidade("Kaesong", Regiao.CHINA, "C1", "C2", "C3");
		t.novaCidade("Pequim", Regiao.CHINA, "C4", "C5", "C6");
		t.novaCidade("Cantao", Regiao.CHINA, "C8", "C10", "C11");

		Provincia russia1 = t.novaProvincia("Russia 1", Regiao.RUSSIA, TipoTributo.YURT, "R1", "R4", "R5", "P1");
		Provincia russia2 = t.novaProvincia("Russia 2", Regiao.RUSSIA, TipoTributo.ESPADA, "R1", "R2", "R5", "R6");
		Provincia russia3 = t.novaProvincia("Russia 3", Regiao.RUSSIA, TipoTributo.MOEDA, "R5", "R7", "P1", "P2");
		Provincia russia4 = t.novaProvincia("Russia 4", Regiao.RUSSIA, TipoTributo.YURT,
				"R6", "R7", "R9", "P2", "P4", k);
		Provincia russia5 = t.novaProvincia("Russia 5", Regiao.RUSSIA, TipoTributo.MOEDA, "R9", "R10", "R11", k);
		Provincia persia1 = t.novaProvincia("Persia 1", Regiao.PERSIA, TipoTributo.ESPADA, "P1", "P2", "P3", "P5");
		Provincia persia2 = t.novaProvincia("Persia 2", Regiao.PERSIA, TipoTributo.MOEDA,
				"P3", "P4", "P5", "P6", "P8", "P9");
		Provincia persia3 = t.novaProvincia("Persia 3", Regiao.PERSIA, TipoTributo.YURT, "P4", "P9", "C4", "C5", k);
		Provincia china1 = t.novaProvincia("China 1", Regiao.CHINA, TipoTributo.ESPADA, "R11", "C1", "C3", "C4", k);
		Provincia china2 = t.novaProvincia("China 2", Regiao.CHINA, TipoTributo.MOEDA, "C3", "C4", "C6", "C7", "C8");
		Provincia china3 = t.novaProvincia("China 3", Regiao.CHINA, TipoTributo.YURT, "C5", "C6", "C8", "C9", "C10");
		Provincia china4 = t.novaProvincia("China 4", Regiao.CHINA, TipoTributo.ESPADA, "P9", "P10", "C5", "C9");

		russia1.tornarProvinciaDoKhan(russia2, russia3);
		russia5.tornarProvinciaDoKhan(russia4, china1);
		persia2.tornarProvinciaDoKhan(persia1, persia3);
		china3.tornarProvinciaDoKhan(china2, china4);

		t.prepararCidades();
		t.prepararProvincias();
		return t;
	}

	private static void ligar(Tabuleiro t, String parada, String... vizinhas) {
		for (String v : vizinhas) {
			t.ligar(parada, v);
		}
	}

	static Tabuleiro criarTabuleiroDeExemplo(Random sorteio) {
		Tabuleiro t = new Tabuleiro(sorteio);

		t.novaParada("P1", Parada.Tipo.SIMPLES);
		t.novaParada("P2", Parada.Tipo.DUPLA);
		t.novaParada("P3", Parada.Tipo.SIMPLES);
		t.novaParada("P4", Parada.Tipo.SIMPLES);
		t.novaParada("P5", Parada.Tipo.SIMPLES);
		t.novaParada("P6", Parada.Tipo.SIMPLES);

		t.ligar(Tabuleiro.KARAKORUM, "P1");
		t.ligar("P1", "P2");
		t.ligar("P2", "P3");
		t.ligar(Tabuleiro.KARAKORUM, "P4");
		t.ligar("P4", "P5");
		t.ligar("P5", "P6");
		t.ligar("P3", "P6");

		t.novaCidade("Samarcanda", Regiao.PERSIA, "P1", "P2");
		t.novaCidade("Bagda", Regiao.PERSIA, "P3");
		t.novaCidade("Kiev", Regiao.RUSSIA, "P4");
		t.novaCidade("Sarai", Regiao.RUSSIA, "P5");
		t.novaCidade("Cabul", Regiao.CHINA, "P6");

		Provincia estepe = t.novaProvincia("Estepe", Regiao.RUSSIA, TipoTributo.ESPADA, "P1", "P4");
		Provincia vale = t.novaProvincia("Vale", Regiao.PERSIA, TipoTributo.MOEDA, "P1", "P2");
		Provincia deserto = t.novaProvincia("Deserto", Regiao.PERSIA, TipoTributo.YURT, "P2", "P3");
		Provincia planalto = t.novaProvincia("Planalto", Regiao.CHINA, TipoTributo.ESPADA, "P5", "P6");
		Provincia montanha = t.novaProvincia("Montanha", Regiao.CHINA, TipoTributo.YURT, "P3", "P6");

		estepe.tornarProvinciaDoKhan(vale, planalto);
		montanha.tornarProvinciaDoKhan(deserto, planalto);

		t.prepararCidades();
		t.prepararProvincias();
		return t;
	}
}
