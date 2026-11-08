package model;

/**
 * As 7 peças de bônus (manual, p. 17). Quem faz a 1ª entrega a um
 * conselheiro leva a peça que está nele.
 *
 * As de recurso (voto, espadas, moedas, yurts, tesouro) valem na hora em que
 * a peça é recebida: as peças de tributo ficam guardadas de qualquer jeito.
 * As de ação (cavalos e punhos) ficam guardadas para um turno à escolha.
 */
enum PecaBonus {
	VOTO(false), ESPADAS(false), MOEDAS(false), YURTS(false), TESOURO(false),
	CAVALOS_E_PUNHO(true), PUNHOS(true);

	final boolean deTurno;

	PecaBonus(boolean deTurno) {
		this.deTurno = deTurno;
	}

	static PecaBonus de(String nome) {
		try {
			return valueOf(nome.trim().toUpperCase());
		} catch (RuntimeException e) {
			throw new JogadaInvalidaException("Peça de bônus desconhecida: " + nome);
		}
	}
}
