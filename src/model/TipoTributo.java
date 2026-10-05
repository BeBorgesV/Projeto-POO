package model;

/** Os três tipos de peça de tributo que ficam nas províncias. */
enum TipoTributo {
	/** Permite atacar cidades. */
	ESPADA,
	/** Permite construir um yurt no tabuleiro. */
	YURT,
	/** Duas moedas compram uma peça de melhoria. */
	MOEDA;

	/** Converte o nome recebido pela API ("ESPADA", "yurt"...) no tipo. */
	static TipoTributo de(String nome) {
		try {
			return valueOf(nome.trim().toUpperCase());
		} catch (RuntimeException e) {
			throw new JogadaInvalidaException("Tipo de tributo desconhecido: " + nome);
		}
	}
}
