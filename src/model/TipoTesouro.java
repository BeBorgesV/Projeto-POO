package model;

/** Os 5 tipos de tesouro (40 peças no jogo, 8 de cada tipo). */
enum TipoTesouro {
	PELE, FERRO, BOI, SEDA, ESPECIARIA;

	static final int PECAS_POR_TIPO = 8;

	static TipoTesouro de(String nome) {
		try {
			return valueOf(nome.trim().toUpperCase());
		} catch (RuntimeException e) {
			throw new JogadaInvalidaException("Tipo de tesouro desconhecido: " + nome);
		}
	}
}
