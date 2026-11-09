package model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Random;

import org.junit.Before;
import org.junit.Test;

/**
 * FIM DO JOGO (manual, p. 14-16): gatilho, rodada final e apuração.
 * Com 2 jogadores o fim dispara quando a ficha neutra chega a 10 votos.
 */
public class FimDeJogoTest {
	private static final int DEFAULT_TIMEOUT = 2000;

	private Jogo jogo;
	private Jogador vermelho;
	private Jogador azul;

	@Before
	public void partidaDeDoisJogadores() {
		jogo = Jogo.reiniciarParaTeste(new Random(42));
		jogo.iniciarPartida("VERMELHO", "AZUL");
		vermelho = jogo.jogador("VERMELHO");
		azul = jogo.jogador("AZUL");
	}

	private void passaAVez() {
		jogo.iniciarTurno(0, 0, false);
		jogo.encerrarTurno();
	}

	/** Com o gatilho já disparado no 1º turno: termina a rodada e joga a rodada final (2 jogadores). */
	private void jogaAteOFim() {
		passaAVez();
		passaAVez();
		passaAVez();
		passaAVez();
	}

	@Test(timeout = DEFAULT_TIMEOUT)
	public void fichaNeutraNoLimiteDisparaOFim() {
		jogo.kurultai().registrarVotos(azul, 10);
		passaAVez();
		assertTrue("Fim disparado", jogo.fimDePartidaDisparado());
	}

	@Test(timeout = DEFAULT_TIMEOUT)
	public void depoisDoGatilhoAindaSeJogaUmaRodadaCompleta() {
		jogo.kurultai().registrarVotos(azul, 10);
		passaAVez(); // VERMELHO, turno do gatilho
		passaAVez(); // AZUL fecha a rodada atual
		passaAVez(); // VERMELHO, rodada final
		assertFalse("Partida acabou antes da rodada final terminar", jogo.partidaEncerrada());
	}

	@Test(timeout = DEFAULT_TIMEOUT)
	public void partidaAcabaNoFimDaRodadaFinal() {
		jogo.kurultai().registrarVotos(azul, 10);
		passaAVez();
		passaAVez();
		passaAVez();
		passaAVez();
		assertTrue("Partida encerrada após a rodada final", jogo.partidaEncerrada());
	}

	@Test(timeout = DEFAULT_TIMEOUT)
	public void maioriaDeYurtsNaRegiaoDaVotosDeInfluencia() {
		// P1 é da Pérsia; VERMELHO tem a maioria e nenhuma melhoria na coluna verde: 1 voto
		jogo.tabuleiro().parada("P1").colocarYurt(vermelho);
		vermelho.usarYurt();
		jogo.kurultai().registrarVotos(azul, 10);
		jogaAteOFim();
		assertEquals("Votos finais do VERMELHO", 1, jogo.votosFinaisDe("VERMELHO"));
	}

	@Test(timeout = DEFAULT_TIMEOUT)
	public void quemTemMaisVotosVence() {
		jogo.kurultai().registrarVotos(azul, 10);
		jogaAteOFim();
		assertEquals("Vencedores", Arrays.asList("AZUL"), jogo.vencedores());
	}

	@Test(timeout = DEFAULT_TIMEOUT)
	public void empateNosVotosEhDecididoPelosTesouros() {
		jogo.kurultai().registrarVotos(azul, 5);
		jogo.kurultai().registrarVotos(vermelho, 5);
		vermelho.receberTesouro(TipoTesouro.BOI);
		jogaAteOFim();
		assertEquals("Vencedor no desempate", Arrays.asList("VERMELHO"), jogo.vencedores());
	}

	@Test(timeout = DEFAULT_TIMEOUT, expected = JogadaInvalidaException.class)
	public void depoisDoFimNaoSeAtivaColuna() {
		jogo.kurultai().registrarVotos(azul, 10);
		jogaAteOFim();
		jogo.ativarColuna("VERDE");
	}
}
