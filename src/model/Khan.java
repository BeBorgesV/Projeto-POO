package model;

/**
 * O peão do Genghis Khan.
 *
 * Começa fora do tabuleiro. Pode ir para uma das províncias do Khan ou para
 * a área de melhorias, mas nunca pode ficar onde já está.
 */
class Khan {
	static final String AREA_DE_MELHORIAS = "MELHORIAS";

	/** Nome da província ou {@link #AREA_DE_MELHORIAS}; {@code null} = fora do tabuleiro. */
	private String local;

	String local() {
		return local;
	}

	void moverPara(String destino) {
		if (destino.equals(local)) {
			throw new JogadaInvalidaException("O Khan não pode ficar onde já está (" + local + ")");
		}
		local = destino;
	}
}
