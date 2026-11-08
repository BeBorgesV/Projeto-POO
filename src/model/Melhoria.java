package model;

import java.util.ArrayList;
import java.util.List;

/**
 * Peça de melhoria. Acrescenta uma ação à coluna onde for colocada.
 *
 * O {@link #id} identifica a imagem da peça na View (ex.: "MOE-A" = moeda
 * virtual de fundo amarelo), para ela ser achada num HashMap em tempo
 * constante. O Model só guarda o significado da peça.
 */
class Melhoria {
	final String id;
	final Acao acao;
	final Coluna cor;
	/** Só para a melhoria de tesouro virtual; null nas demais. */
	final TipoTesouro tesouro;

	private Melhoria(String id, Acao acao, Coluna cor, TipoTesouro tesouro) {
		this.id = id;
		this.acao = acao;
		this.cor = cor;
		this.tesouro = tesouro;
	}

	/** Ações que a peça dá à coluna: a de cavalos vale 2 movimentos (manual, p. 16). */
	List<Acao> acoes() {
		List<Acao> a = new ArrayList<>();
		a.add(acao);
		if (acao == Acao.CAVALO) {
			a.add(Acao.CAVALO);
		}
		return a;
	}

	/** Símbolo da peça: tesouros diferentes contam como símbolos diferentes (Colecionador, Sacerdote). */
	String simbolo() {
		return tesouro == null ? acao.name() : acao.name() + "-" + tesouro.name();
	}

	/**
	 * As 44 melhorias do jogo (manual, p. 16). As imagens mostram uma peça de
	 * cada tipo e cor, mas não quantas há de cada cor.
	 * shortcut: a distribuição por cor abaixo é nossa (total por tipo segue o
	 * manual: 8 moedas, 8 espadas, 8 yurts, 9 cavalos, 6 punhos, 5 tesouros);
	 * trocar se o grupo achar a contagem oficial.
	 */
	static List<Melhoria> todas() {
		List<Melhoria> m = new ArrayList<>();
		adicionar(m, "MOE", Acao.MOEDA, 3, 3, 2);
		adicionar(m, "ESP", Acao.ESPADA, 3, 3, 2);
		adicionar(m, "CAS", Acao.YURT, 2, 3, 3);
		adicionar(m, "CAV", Acao.CAVALO, 3, 3, 3);
		adicionar(m, "MAO", Acao.PUNHO, 2, 2, 2);
		m.add(new Melhoria("BOI-A", Acao.TESOURO, Coluna.AMARELA, TipoTesouro.BOI));
		m.add(new Melhoria("CUI-A", Acao.TESOURO, Coluna.AMARELA, TipoTesouro.ESPECIARIA));
		m.add(new Melhoria("MOB-R", Acao.TESOURO, Coluna.VERMELHA, TipoTesouro.FERRO));
		m.add(new Melhoria("MOP-V", Acao.TESOURO, Coluna.VERDE, TipoTesouro.PELE));
		m.add(new Melhoria("PER-V", Acao.TESOURO, Coluna.VERDE, TipoTesouro.SEDA));
		return m;
	}

	private static void adicionar(List<Melhoria> m, String codigo, Acao acao, int vermelhas, int verdes, int amarelas) {
		for (int i = 0; i < vermelhas; i++) {
			m.add(new Melhoria(codigo + "-R", acao, Coluna.VERMELHA, null));
		}
		for (int i = 0; i < verdes; i++) {
			m.add(new Melhoria(codigo + "-V", acao, Coluna.VERDE, null));
		}
		for (int i = 0; i < amarelas; i++) {
			m.add(new Melhoria(codigo + "-A", acao, Coluna.AMARELA, null));
		}
	}

	@Override
	public String toString() {
		return id;
	}
}
