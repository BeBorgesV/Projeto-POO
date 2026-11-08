package model;

/**
 * As 4 colunas do tabuleiro do jogador. Cada coluna colorida é ligada à região
 * da mesma cor no mapa (manual, p. 12): vermelha = China, verde = Pérsia,
 * amarela = Rússia. A branca não tem região e recupera as peças de ativação.
 */
enum Coluna {
	VERMELHA(Regiao.CHINA), VERDE(Regiao.PERSIA), AMARELA(Regiao.RUSSIA), BRANCA(null);

	static final int ESPACOS = 4;

	final Regiao regiao;

	Coluna(Regiao regiao) {
		this.regiao = regiao;
	}

	boolean colorida() {
		return this != BRANCA;
	}

	static Coluna de(String nome) {
		try {
			return valueOf(nome.trim().toUpperCase());
		} catch (RuntimeException e) {
			throw new JogadaInvalidaException("Coluna desconhecida: " + nome);
		}
	}

	static Coluna daRegiao(Regiao r) {
		for (Coluna c : values()) {
			if (c.regiao == r) {
				return c;
			}
		}
		return null;
	}
}
