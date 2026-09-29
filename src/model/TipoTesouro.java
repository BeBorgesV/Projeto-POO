package model;

/**
 * Os 5 tipos de tesouro (40 peças no jogo, 8 de cada tipo).
 *
 * O manual só cita pelo nome "pele" e "ferro". Os outros três aparecem
 * apenas como ícones: conferir nas peças do jogo e renomear aqui.
 */
enum TipoTesouro {
	PELE, FERRO, TESOURO_3, TESOURO_4, TESOURO_5;

	static final int PECAS_POR_TIPO = 8;

	static TipoTesouro de(String nome) {
		try {
			return valueOf(nome.trim().toUpperCase());
		} catch (RuntimeException e) {
			throw new JogadaInvalidaException("Tipo de tesouro desconhecido: " + nome);
		}
	}
}
