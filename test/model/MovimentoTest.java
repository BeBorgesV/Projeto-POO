package model;

import static org.junit.Assert.assertEquals;

import java.util.Random;

import org.junit.Before;
import org.junit.Test;

/**
 * Ação MOVER (manual, p. 7). Mapa de exemplo: Karakorum-P1-P2(dupla)-P3 e
 * Karakorum-P4-P5-P6. A província Estepe é vizinha de P1 e P4.
 */
public class MovimentoTest {
	private static final int DEFAULT_TIMEOUT = 2000;

	private Jogo jogo;
	private Jogador vermelho;
	private Jogador azul;

	@Before
	public void criaPartidaComVermelhoDaVezEmKarakorum() {
		jogo = new Jogo(new Random(42));
		jogo.iniciarPartida("VERMELHO", "AZUL");
		vermelho = jogo.jogador("VERMELHO");
		azul = jogo.jogador("AZUL");
	}

	private Parada parada(String nome) {
		return jogo.tabuleiro().parada(nome);
	}

	@Test(timeout = DEFAULT_TIMEOUT)
	public void moverParaParadaVizinhaLevaOPeaoAteEla() {
		jogo.iniciarTurno(1, 0, false);
		jogo.mover("P1");
		assertEquals("Posição após mover", "P1", jogo.posicaoDe("VERMELHO"));
	}

	@Test(timeout = DEFAULT_TIMEOUT, expected = JogadaInvalidaException.class)
	public void moverParaParadaQueNaoEhVizinhaLancaExcecao() {
		jogo.iniciarTurno(1, 0, false);
		jogo.mover("P2");
	}

	@Test(timeout = DEFAULT_TIMEOUT, expected = JogadaInvalidaException.class)
	public void moverSemMovimentosRestantesLancaExcecao() {
		jogo.iniciarTurno(1, 0, false);
		jogo.mover("P1");
		jogo.mover("P2");
	}

	@Test(timeout = DEFAULT_TIMEOUT)
	public void yurtProprioPermitePularAParadaComUmSoMovimento() {
		parada("P1").colocarYurt(vermelho);
		jogo.iniciarTurno(1, 0, false);
		jogo.mover("P2");
		assertEquals("Posição após pular o próprio yurt", "P2", jogo.posicaoDe("VERMELHO"));
	}

	@Test(timeout = DEFAULT_TIMEOUT, expected = JogadaInvalidaException.class)
	public void paradaPuladaComYurtNaoServeDeBaseParaPegarTributo() {
		parada("P1").colocarYurt(vermelho);
		jogo.iniciarTurno(1, 1, false);
		jogo.mover("P2");
		jogo.pegarTributo("Estepe"); // Estepe só é vizinha de P1 (pulada) e P4
	}

	@Test(timeout = DEFAULT_TIMEOUT, expected = JogadaInvalidaException.class)
	public void yurtDeOutroJogadorNaoPermitePular() {
		parada("P1").colocarYurt(azul);
		jogo.iniciarTurno(1, 0, false);
		jogo.mover("P2");
	}

	@Test(timeout = DEFAULT_TIMEOUT, expected = JogadaInvalidaException.class)
	public void ultimoMovimentoNaoPodeTerminarEmParadaSimplesOcupada() {
		azul.posicionarEm(parada("P1"));
		jogo.iniciarTurno(1, 0, false);
		jogo.mover("P1");
	}

	@Test(timeout = DEFAULT_TIMEOUT)
	public void podePassarPorParadaOcupadaSeAindaTemMovimento() {
		azul.posicionarEm(parada("P1"));
		jogo.iniciarTurno(2, 0, false);
		jogo.mover("P1");
		jogo.mover("P2");
		assertEquals("Posição após atravessar parada ocupada", "P2", jogo.posicaoDe("VERMELHO"));
	}

	@Test(timeout = DEFAULT_TIMEOUT, expected = JogadaInvalidaException.class)
	public void encerrarTurnoParadoEmParadaSimplesOcupadaLancaExcecao() {
		azul.posicionarEm(parada("P1"));
		jogo.iniciarTurno(2, 0, false);
		jogo.mover("P1");
		jogo.encerrarTurno();
	}

	@Test(timeout = DEFAULT_TIMEOUT)
	public void paradaDuplaAceitaDoisPeoes() {
		azul.posicionarEm(parada("P2"));
		vermelho.posicionarEm(parada("P1"));
		jogo.iniciarTurno(1, 0, false);
		jogo.mover("P2");
		jogo.encerrarTurno();
		assertEquals("Turno deveria ter passado para o AZUL", "AZUL", jogo.corDaVez());
	}

	@Test(timeout = DEFAULT_TIMEOUT, expected = JogadaInvalidaException.class)
	public void paradaDuplaNaoAceitaTerceiroPeao() {
		Jogo jogoDeTres = new Jogo(new Random(42));
		jogoDeTres.iniciarPartida("VERMELHO", "AZUL", "VERDE");
		Parada p2 = jogoDeTres.tabuleiro().parada("P2");
		jogoDeTres.jogador("AZUL").posicionarEm(p2);
		jogoDeTres.jogador("VERDE").posicionarEm(p2);
		jogoDeTres.jogador("VERMELHO").posicionarEm(jogoDeTres.tabuleiro().parada("P1"));
		jogoDeTres.iniciarTurno(1, 0, false);
		jogoDeTres.mover("P2");
	}
}
