package model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.Random;

import org.junit.Before;
import org.junit.Test;

/**
 * Ações extras ENTREGAR TESOUROS AOS CONSELHEIROS e CUMPRIR CARTAS DE
 * CONSELHEIRO ABERTO (manual, p. 12-13 e 18). No mapa de exemplo, o
 * conselheiro de P2 pede 2 peles e o de Karakorum, ferro + pele + boi.
 */
public class ConselheiroTest {
	private static final int DEFAULT_TIMEOUT = 2000;

	private Jogo jogo;
	private Jogador vermelho;

	@Before
	public void vermelhoEmP2ComDuasPeles() {
		jogo = Jogo.reiniciarParaTeste(new Random(42));
		jogo.iniciarPartida("VERMELHO", "AZUL");
		vermelho = jogo.jogador("VERMELHO");
		vermelho.posicionarEm(jogo.tabuleiro().parada("P2"));
		vermelho.receberTesouro(TipoTesouro.PELE);
		vermelho.receberTesouro(TipoTesouro.PELE);
		jogo.tabuleiro().conselheiro("P2").entregarBonus(); // tira o bônus sorteado
		jogo.tabuleiro().conselheiro(Tabuleiro.KARAKORUM).entregarBonus();
		jogo.abertosParaTeste(); // nenhuma carta aberta, para não dar votos extras
		jogo.iniciarTurno(1, 0, false);
	}

	@Test(timeout = DEFAULT_TIMEOUT)
	public void entregarAoConselheiroDaUmVoto() {
		jogo.entregarTesouros("P2");
		assertEquals("Votos após a entrega em P2", 1, jogo.votosDe("VERMELHO"));
	}

	@Test(timeout = DEFAULT_TIMEOUT)
	public void entregarGastaOsTesourosPedidos() {
		jogo.entregarTesouros("P2");
		assertEquals("Peles após a entrega", 0, jogo.tesourosDoTipo("VERMELHO", "PELE"));
	}

	@Test(timeout = DEFAULT_TIMEOUT)
	public void primeiraEntregaLevaAPecaDeBonus() {
		jogo.tabuleiro().conselheiro("P2").receberBonus(PecaBonus.MOEDAS);
		jogo.entregarTesouros("P2");
		assertEquals("Moedas: 1 inicial + 2 do bônus", 3, jogo.tributosDe("VERMELHO", "MOEDA"));
	}

	@Test(timeout = DEFAULT_TIMEOUT)
	public void karakorumDaDoisVotos() {
		vermelho.receberTesouro(TipoTesouro.FERRO);
		vermelho.receberTesouro(TipoTesouro.BOI);
		jogo.mover("P1");
		jogo.encerrarTurno();
		jogo.iniciarTurno(0, 0, false); // AZUL
		jogo.encerrarTurno();
		jogo.iniciarTurno(1, 0, false); // VERMELHO volta a Karakorum
		jogo.mover(Tabuleiro.KARAKORUM);
		jogo.entregarTesouros(Tabuleiro.KARAKORUM);
		assertEquals("Votos da entrega em Karakorum", 2, jogo.votosDe("VERMELHO"));
	}

	@Test(timeout = DEFAULT_TIMEOUT, expected = JogadaInvalidaException.class)
	public void entregarSemOsTesourosPedidosLancaExcecao() {
		vermelho.gastarTesouros(java.util.Arrays.asList(TipoTesouro.PELE));
		jogo.entregarTesouros("P2");
	}

	@Test(timeout = DEFAULT_TIMEOUT, expected = JogadaInvalidaException.class)
	public void entregarEmConselheiroOndeNaoParouLancaExcecao() {
		jogo.entregarTesouros(Tabuleiro.KARAKORUM);
	}

	@Test(timeout = DEFAULT_TIMEOUT)
	public void cumprirCartaAbertaDaUmVoto() {
		jogo.abertosParaTeste(ConselheiroAberto.TESOUROS_4);
		vermelho.receberTesouro(TipoTesouro.BOI);
		vermelho.receberTesouro(TipoTesouro.SEDA);
		jogo.mover("P1"); // qualquer ação confere as cartas abertas
		assertEquals("Votos após cumprir a carta de 4 tesouros", 1, jogo.votosDe("VERMELHO"));
	}

	@Test(timeout = DEFAULT_TIMEOUT)
	public void cartaAbertaCumpridaSaiDoJogo() {
		jogo.abertosParaTeste(ConselheiroAberto.TESOUROS_4);
		vermelho.receberTesouro(TipoTesouro.BOI);
		vermelho.receberTesouro(TipoTesouro.SEDA);
		jogo.mover("P1");
		assertFalse("Carta continua na mesa", jogo.conselheirosAbertos().contains("TESOUROS_4"));
	}
}
