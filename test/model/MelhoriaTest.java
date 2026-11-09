package model;

import static org.junit.Assert.assertEquals;

import java.util.List;
import java.util.Random;

import org.junit.Before;
import org.junit.Test;

/**
 * Ação extra COMPRAR MELHORIAS COM MOEDAS (manual, p. 11-12 e 16).
 * VERMELHO (Chagatai) ativa a coluna amarela, que não dá moeda virtual.
 */
public class MelhoriaTest {
	private static final int DEFAULT_TIMEOUT = 2000;

	private Jogo jogo;
	private Jogador vermelho;

	@Before
	public void vermelhoComTresMoedasNaColunaAmarela() {
		jogo = Jogo.reiniciarParaTeste(new Random(42));
		jogo.iniciarPartida("VERMELHO", "AZUL");
		vermelho = jogo.jogador("VERMELHO");
		vermelho.escolherSecreto(0);
		vermelho.receberTributo(TipoTributo.MOEDA, 2); // 1 inicial + 2
		jogo.ativarColuna("AMARELA");
	}

	@Test(timeout = DEFAULT_TIMEOUT)
	public void comprarMelhoriaCustaDuasMoedas() {
		jogo.comprarMelhoria(4, "");
		assertEquals("Moedas após comprar", 1, jogo.tributosDe("VERMELHO", "MOEDA"));
	}

	@Test(timeout = DEFAULT_TIMEOUT)
	public void melhoriaCompradaVaiParaAColunaDaSuaCor() {
		Melhoria m = jogo.area().ver(4);
		jogo.comprarMelhoria(4, "");
		assertEquals("Melhorias na coluna da cor da peça", 1, jogo.melhoriasDe("VERMELHO", m.cor.name()).size());
	}

	@Test(timeout = DEFAULT_TIMEOUT)
	public void comprarDeUmEspacoFazAsDaEsquerdaDeslizaremParaADireita() {
		List<String> antes = jogo.melhoriasDisponiveis();
		jogo.comprarMelhoria(2, "");
		List<String> depois = jogo.melhoriasDisponiveis();
		assertEquals("Área após comprar o 3º espaço",
				java.util.Arrays.asList("", antes.get(0), antes.get(1), antes.get(3), antes.get(4)), depois);
	}

	@Test(timeout = DEFAULT_TIMEOUT, expected = JogadaInvalidaException.class)
	public void quemNaoEhOJochiNaoPodeTrocarACorDaColuna() {
		Melhoria m = jogo.area().ver(4);
		Coluna outra = m.cor == Coluna.VERDE ? Coluna.AMARELA : Coluna.VERDE;
		jogo.comprarMelhoria(4, outra.name());
	}

	@Test(timeout = DEFAULT_TIMEOUT, expected = JogadaInvalidaException.class)
	public void comprarComMenosDeDuasMoedasLancaExcecao() {
		vermelho.gastarTributo(TipoTributo.MOEDA, 2);
		jogo.comprarMelhoria(4, "");
	}

	@Test(timeout = DEFAULT_TIMEOUT)
	public void completarUmaLinhaDeixaUmaMelhoriaGratisPendente() {
		CartelaDoJogador cartela = vermelho.cartela();
		cartela.colocar(ColunaTest.melhoria("CAV-R"), Coluna.VERMELHA);
		cartela.colocar(ColunaTest.melhoria("CAV-V"), Coluna.VERDE);
		cartela.colocar(ColunaTest.melhoria("CAV-A"), Coluna.AMARELA);
		assertEquals("Melhorias grátis pendentes após completar a 1ª linha", 1, jogo.melhoriasGratisPendentes());
	}

	@Test(timeout = DEFAULT_TIMEOUT)
	public void melhoriaGratisVaiParaAColunaBranca() {
		CartelaDoJogador cartela = vermelho.cartela();
		cartela.colocar(ColunaTest.melhoria("CAV-R"), Coluna.VERMELHA);
		cartela.colocar(ColunaTest.melhoria("CAV-V"), Coluna.VERDE);
		cartela.colocar(ColunaTest.melhoria("CAV-A"), Coluna.AMARELA);
		jogo.pegarMelhoriaGratis(0);
		assertEquals("Melhorias na coluna branca", 1, jogo.melhoriasDe("VERMELHO", "BRANCA").size());
	}

	@Test(timeout = DEFAULT_TIMEOUT)
	public void khanNaAreaDeMelhoriasComEspacoDaMoedaVazioDaUmaMoeda() {
		jogo.comprarMelhoria(4, ""); // esvazia o 1º espaço; o da moeda (2º) ainda está cheio
		vermelho.receberTributo(TipoTributo.MOEDA, 1);
		jogo.comprarMelhoria(4, ""); // agora o 2º espaço também ficou vazio
		jogo.encerrarTurno();
		jogo.jogador("AZUL").escolherSecreto(0);
		jogo.ativarColuna("BRANCA"); // AZUL: coluna com o Khan
		jogo.moverKhan("MELHORIAS");
		assertEquals("Moedas do AZUL: 1 inicial + 1 do espaço da moeda", 2, jogo.tributosDe("AZUL", "MOEDA"));
	}
}
