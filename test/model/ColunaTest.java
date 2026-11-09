package model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Random;

import org.junit.Before;
import org.junit.Test;

/**
 * Peças de ativação e colunas do tabuleiro do jogador (manual, p. 6 e 9).
 * VERMELHO é o Chagatai: vermelha = 1 cavalo, 2 punhos e Khan;
 * verde = 1 cavalo, 1 punho e 1 moeda; amarela = 2 cavalos e 1 punho.
 */
public class ColunaTest {
	private static final int DEFAULT_TIMEOUT = 2000;

	private Jogo jogo;

	@Before
	public void partidaComSecretosJaEscolhidos() {
		jogo = Jogo.reiniciarParaTeste(new Random(42));
		jogo.iniciarPartida("VERMELHO", "AZUL");
		jogo.jogador("VERMELHO").escolherSecreto(0);
		jogo.jogador("AZUL").escolherSecreto(0);
	}

	/** Os dois jogadores ativam a coluna dada e passam a vez. */
	private void rodadaCom(String colunaDoVermelho, String colunaDoAzul) {
		jogo.ativarColuna(colunaDoVermelho);
		jogo.encerrarTurno();
		jogo.ativarColuna(colunaDoAzul);
		jogo.encerrarTurno();
	}

	@Test(timeout = DEFAULT_TIMEOUT)
	public void colunaVermelhaDoChagataiDaUmMovimento() {
		jogo.ativarColuna("VERMELHA");
		assertEquals("Movimentos da coluna vermelha do Chagatai", 1, jogo.movimentosRestantes());
	}

	@Test(timeout = DEFAULT_TIMEOUT)
	public void colunaVermelhaDoChagataiDaDuasAcoesDeTributo() {
		jogo.ativarColuna("VERMELHA");
		assertEquals("Punhos da coluna vermelha do Chagatai", 2, jogo.acoesDeTributoRestantes());
	}

	@Test(timeout = DEFAULT_TIMEOUT)
	public void colunaComIconeDoKhanObrigaAUsarOKhan() {
		jogo.ativarColuna("VERMELHA");
		assertTrue("Khan pendente após ativar coluna com o ícone", jogo.khanPendente());
	}

	@Test(timeout = DEFAULT_TIMEOUT)
	public void colunaComMoedaDaUmaMoedaVirtual() {
		jogo.ativarColuna("VERDE");
		assertEquals("Moedas virtuais da coluna verde do Chagatai", 1, jogo.moedasVirtuais());
	}

	@Test(timeout = DEFAULT_TIMEOUT, expected = JogadaInvalidaException.class)
	public void colunaComPecaDeAtivacaoNaoPodeSerUsadaDeNovo() {
		rodadaCom("VERDE", "VERDE");
		jogo.ativarColuna("VERDE");
	}

	@Test(timeout = DEFAULT_TIMEOUT)
	public void colunaBrancaRecuperaTodasAsPecasDeAtivacao() {
		rodadaCom("VERDE", "VERDE");
		jogo.ativarColuna("BRANCA");
		assertTrue("Colunas usadas depois da branca", jogo.colunasUsadasDe("VERMELHO").isEmpty());
	}

	@Test(timeout = DEFAULT_TIMEOUT)
	public void melhoriaDeCavalosSomaDoisMovimentosNaColuna() {
		jogo.jogador("VERMELHO").cartela().colocar(melhoria("CAV-A"), Coluna.AMARELA);
		jogo.ativarColuna("AMARELA");
		assertEquals("Movimentos: 2 da coluna + 2 da melhoria", 4, jogo.movimentosRestantes());
	}

	@Test(timeout = DEFAULT_TIMEOUT, expected = JogadaInvalidaException.class)
	public void ativarColunaSemEscolherOConselheiroSecretoLancaExcecao() {
		Jogo outro = Jogo.reiniciarParaTeste(new Random(42));
		outro.iniciarPartida("VERMELHO", "AZUL");
		outro.ativarColuna("VERDE");
	}

	static Melhoria melhoria(String id) {
		for (Melhoria m : Melhoria.todas()) {
			if (m.id.equals(id)) {
				return m;
			}
		}
		throw new IllegalArgumentException(id);
	}
}
