package model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.util.Random;

import org.junit.Before;
import org.junit.Test;

/** Padrões Singleton e Observer na API do Model (Cap. 17). */
public class PadroesTest {
	private static final int DEFAULT_TIMEOUT = 2000;

	private Jogo jogo;
	private ObservadorDeTeste observador;

	/** Observador que só conta as notificações e guarda o último evento. */
	private static class ObservadorDeTeste implements ObservadorIF {
		int notificacoes;
		int ultimoEvento = -1;

		@Override
		public void notify(ObservadoIF o) {
			notificacoes++;
			ultimoEvento = o.get(Jogo.EVENTO);
		}
	}

	@Before
	public void partidaComUmObservador() {
		jogo = Jogo.reiniciarParaTeste(new Random(42));
		jogo.iniciarPartida("VERMELHO", "AZUL");
		observador = new ObservadorDeTeste();
		jogo.add(observador);
		jogo.iniciarTurno(1, 0, false);
	}

	@Test(timeout = DEFAULT_TIMEOUT)
	public void getJogoDevolveSempreAMesmaInstancia() {
		assertSame("Instâncias do Singleton", Jogo.getJogo(), Jogo.getJogo());
	}

	@Test(timeout = DEFAULT_TIMEOUT)
	public void moverNotificaOObservador() {
		jogo.mover("P1");
		assertEquals("Notificações após mover", 1, observador.notificacoes);
	}

	@Test(timeout = DEFAULT_TIMEOUT)
	public void observadorDescobreQueFoiUmMovimento() {
		jogo.mover("P1");
		assertEquals("Evento recebido", Jogo.EV_MOVIMENTO, observador.ultimoEvento);
	}

	@Test(timeout = DEFAULT_TIMEOUT)
	public void jogadaInvalidaNaoNotifica() {
		try {
			jogo.mover("P6");
		} catch (JogadaInvalidaException e) {
			// esperado: P6 não é vizinha de Karakorum
		}
		assertEquals("Notificações após jogada inválida", 0, observador.notificacoes);
	}

	@Test(timeout = DEFAULT_TIMEOUT)
	public void observadorRemovidoNaoEhMaisNotificado() {
		jogo.remove(observador);
		jogo.mover("P1");
		assertEquals("Notificações após remover", 0, observador.notificacoes);
	}
}
