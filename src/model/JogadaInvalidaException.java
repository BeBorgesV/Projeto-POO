package model;

/**
 * Lançada quando uma jogada desrespeita as regras do jogo.
 *
 * É pública porque sai pela API: o Controller vai capturá-la para avisar
 * o jogador. Não representa um elemento do jogo.
 */
public class JogadaInvalidaException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public JogadaInvalidaException(String mensagem) {
		super(mensagem);
	}
}
