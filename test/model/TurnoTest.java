package model;

import static org.junit.Assert.assertEquals;

import java.util.Random;

import org.junit.Before;
import org.junit.Test;

/** Ordem de jogo e controle do turno. */
public class TurnoTest {
	private static final int DEFAULT_TIMEOUT = 2000;

	private Jogo jogo;

	@Before
	public void criaPartidaComTresJogadores() {
		jogo = new Jogo(new Random(42));
		jogo.iniciarPartida("VERMELHO", "AZUL", "VERDE");
	}

	@Test(timeout = DEFAULT_TIMEOUT)
	public void primeiroAJogarEhOPrimeiroDaLista() {
		assertEquals("Jogador da vez no início", "VERMELHO", jogo.corDaVez());
	}

	private void passaAVez() {
		jogo.iniciarTurno(0, 0, false);
		jogo.encerrarTurno();
	}

	@Test(timeout = DEFAULT_TIMEOUT)
	public void encerrarTurnoPassaAVezParaOProximoJogador() {
		passaAVez();
		assertEquals("Jogador da vez após 1 turno", "AZUL", jogo.corDaVez());
	}

	@Test(timeout = DEFAULT_TIMEOUT)
	public void depoisDoUltimoJogadorAVezVoltaParaOPrimeiro() {
		passaAVez();
		passaAVez();
		passaAVez();
		assertEquals("Jogador da vez após uma rodada", "VERMELHO", jogo.corDaVez());
	}

	@Test(timeout = DEFAULT_TIMEOUT, expected = JogadaInvalidaException.class)
	public void acaoSemTurnoIniciadoLancaExcecao() {
		jogo.mover("P1");
	}

	@Test(timeout = DEFAULT_TIMEOUT, expected = JogadaInvalidaException.class)
	public void iniciarTurnoComOutroEmAndamentoLancaExcecao() {
		jogo.iniciarTurno(1, 0, false);
		jogo.iniciarTurno(1, 0, false);
	}
}
