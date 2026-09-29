package model;

import static org.junit.Assert.assertEquals;

import java.util.Random;

import org.junit.Before;
import org.junit.Test;

/**
 * Ação USAR O KHAN (manual, p. 9). No mapa de exemplo, Estepe é província do
 * Khan e suas setas apontam para Vale e Planalto.
 */
public class KhanTest {
	private static final int DEFAULT_TIMEOUT = 2000;

	private Jogo jogo;

	@Before
	public void criaPartidaEIniciaTurnoComKhan() {
		jogo = new Jogo(new Random(42));
		jogo.iniciarPartida("VERMELHO", "AZUL");
		jogo.iniciarTurno(0, 0, true);
	}

	@Test(timeout = DEFAULT_TIMEOUT)
	public void khanEmProvinciaDoKhanPoeUmTributoNela() {
		jogo.moverKhan("Estepe");
		assertEquals("Peças na Estepe", 2, jogo.pecasNaProvincia("Estepe"));
	}

	@Test(timeout = DEFAULT_TIMEOUT)
	public void khanPoeTributoNaPrimeiraProvinciaApontadaPelasSetas() {
		jogo.moverKhan("Estepe");
		assertEquals("Peças no Vale", 2, jogo.pecasNaProvincia("Vale"));
	}

	@Test(timeout = DEFAULT_TIMEOUT)
	public void khanPoeTributoNaSegundaProvinciaApontadaPelasSetas() {
		jogo.moverKhan("Estepe");
		assertEquals("Peças no Planalto", 2, jogo.pecasNaProvincia("Planalto"));
	}

	@Test(timeout = DEFAULT_TIMEOUT)
	public void provinciaNaoPassaDeTresPecasDeTributo() {
		Provincia estepe = jogo.tabuleiro().provincia("Estepe");
		estepe.receberTributo();
		estepe.receberTributo(); // agora tem 3
		jogo.moverKhan("Estepe");
		assertEquals("Máximo de peças por província", 3, jogo.pecasNaProvincia("Estepe"));
	}

	@Test(timeout = DEFAULT_TIMEOUT, expected = JogadaInvalidaException.class)
	public void khanNaoPodeFicarOndeJaEsta() {
		jogo.moverKhan("Estepe");
		jogo.encerrarTurno();
		jogo.iniciarTurno(0, 0, true);
		jogo.moverKhan("Estepe");
	}

	@Test(timeout = DEFAULT_TIMEOUT, expected = JogadaInvalidaException.class)
	public void khanSoVaiParaProvinciaDoKhan() {
		jogo.moverKhan("Vale");
	}

	@Test(timeout = DEFAULT_TIMEOUT)
	public void khanPodeIrParaAAreaDeMelhorias() {
		jogo.moverKhan("MELHORIAS");
		assertEquals("Local do Khan", "MELHORIAS", jogo.localDoKhan());
	}

	@Test(timeout = DEFAULT_TIMEOUT, expected = JogadaInvalidaException.class)
	public void colunaComKhanNaoDeixaEncerrarSemUsarOKhan() {
		jogo.encerrarTurno();
	}
}
