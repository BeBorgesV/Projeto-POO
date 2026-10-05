package model;

import java.util.Random;

/**
 * Monta o tabuleiro.
 *
 * Por enquanto só existe um mapa de exemplo, pequeno, para testar as regras.
 * TODO: passar o tabuleiro real (paradas, rotas, províncias, cidades e
 * conselheiros) para um novo método aqui.
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
