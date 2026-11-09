package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Estado do turno do jogador da vez.
 *
 * Guarda quantos movimentos e ações de pegar tributo ainda restam, por quais
 * paradas o peão realmente passou (paradas puladas com yurt não contam) e
 * quantos tesouros já foram tomados de cada cidade (para o custo em espadas).
 */
class Turno {
	private final Jogador jogador;
	private int movimentosRestantes;
	private int acoesDeTributoRestantes;
	private final boolean khanObrigatorio;
	private boolean khanUsado;
	private final Set<Parada> paradasVisitadas = new HashSet<>();
	private final Map<Cidade, Integer> tesourosTomados = new HashMap<>();
	// recursos virtuais das melhorias: só valem neste turno
	private int moedasVirtuais;
	private int espadasVirtuais;
	private int yurtsVirtuais;
	private final List<TipoTesouro> tesourosVirtuais = new ArrayList<>();
	// habilidades "1x por turno"
	private boolean cavaloTrocado;
	private boolean moedaPorTesouroUsada;

	/** Turno com as ações da coluna ativada (iniciais do herdeiro + melhorias). */
	static Turno daColuna(Jogador j, List<Acao> acoes, List<TipoTesouro> tesouros) {
		Turno t = new Turno(j, Collections.frequency(acoes, Acao.CAVALO), Collections.frequency(acoes, Acao.PUNHO),
				acoes.contains(Acao.KHAN));
		t.moedasVirtuais = Collections.frequency(acoes, Acao.MOEDA);
		t.espadasVirtuais = Collections.frequency(acoes, Acao.ESPADA);
		t.yurtsVirtuais = Collections.frequency(acoes, Acao.YURT);
		t.tesourosVirtuais.addAll(tesouros);
		return t;
	}

	Turno(Jogador jogador, int movimentos, int acoesDeTributo, boolean khanObrigatorio) {
		if (movimentos < 0 || acoesDeTributo < 0) {
			throw new JogadaInvalidaException("Movimentos e ações não podem ser negativos");
		}
		this.jogador = jogador;
		this.movimentosRestantes = movimentos;
		this.acoesDeTributoRestantes = acoesDeTributo;
		this.khanObrigatorio = khanObrigatorio;
		// A parada onde o turno começa também conta ("antes, durante ou após seu movimento").
		paradasVisitadas.add(jogador.posicao());
	}

	Jogador jogador() {
		return jogador;
	}

	// ---------- movimento ----------

	int movimentosRestantes() {
		return movimentosRestantes;
	}

	void gastarMovimento() {
		if (movimentosRestantes == 0) {
			throw new JogadaInvalidaException("Não há mais movimentos neste turno");
		}
		movimentosRestantes--;
	}

	void registrarVisita(Parada p) {
		paradasVisitadas.add(p);
	}

	boolean visitou(Parada p) {
		return paradasVisitadas.contains(p);
	}

	Set<Parada> paradasVisitadas() {
		return Collections.unmodifiableSet(paradasVisitadas);
	}

	// ---------- tributo ----------

	void gastarAcaoDeTributo() {
		if (acoesDeTributoRestantes == 0) {
			throw new JogadaInvalidaException("Não há mais ações de pegar tributo neste turno");
		}
		acoesDeTributoRestantes--;
	}

	int acoesDeTributoRestantes() {
		return acoesDeTributoRestantes;
	}

	// ---------- ataque ----------

	/** O 1º tesouro de uma cidade no turno custa 1 espada; cada seguinte custa 2. */
	int custoEmEspadas(Cidade c) {
		return tesourosTomados.getOrDefault(c, 0) == 0 ? 1 : 2;
	}

	void registrarTesouroTomado(Cidade c) {
		tesourosTomados.put(c, tesourosTomados.getOrDefault(c, 0) + 1);
	}

	/** Peças de bônus de ação (cavalos e punhos extras). */
	void acrescentar(int movimentos, int acoesDeTributo) {
		movimentosRestantes += movimentos;
		acoesDeTributoRestantes += acoesDeTributo;
	}

	// ---------- recursos virtuais ----------

	int moedasVirtuais() {
		return moedasVirtuais;
	}

	int espadasVirtuais() {
		return espadasVirtuais;
	}

	int yurtsVirtuais() {
		return yurtsVirtuais;
	}

	List<TipoTesouro> tesourosVirtuais() {
		return Collections.unmodifiableList(tesourosVirtuais);
	}

	/**
	 * Paga {@code quantidade} peças de um tipo de tributo usando primeiro as
	 * virtuais do turno e depois as do jogador. Só mexe em algo se der para
	 * pagar tudo.
	 */
	void pagar(TipoTributo tipo, int quantidade) {
		int virtuais = tipo == TipoTributo.MOEDA ? moedasVirtuais : tipo == TipoTributo.ESPADA ? espadasVirtuais : yurtsVirtuais;
		int usadasVirtuais = Math.min(virtuais, quantidade);
		int doJogador = quantidade - usadasVirtuais;
		if (jogador.tributos(tipo) < doJogador) {
			throw new JogadaInvalidaException(jogador.cor() + " precisa de " + quantidade + " " + tipo
					+ " e só tem " + (jogador.tributos(tipo) + virtuais));
		}
		jogador.gastarTributo(tipo, doJogador);
		if (tipo == TipoTributo.MOEDA) {
			moedasVirtuais -= usadasVirtuais;
		} else if (tipo == TipoTributo.ESPADA) {
			espadasVirtuais -= usadasVirtuais;
		} else {
			yurtsVirtuais -= usadasVirtuais;
		}
	}

	int disponivel(TipoTributo tipo) {
		int virtuais = tipo == TipoTributo.MOEDA ? moedasVirtuais : tipo == TipoTributo.ESPADA ? espadasVirtuais : yurtsVirtuais;
		return jogador.tributos(tipo) + virtuais;
	}

	void gastarTesouroVirtual(TipoTesouro t) {
		tesourosVirtuais.remove(t);
	}

	// ---------- habilidades ----------

	/** Chagatai: 1x por turno, troca 1 cavalo por 1 peça de espada. */
	void trocarCavaloPorEspada() {
		if (cavaloTrocado) {
			throw new JogadaInvalidaException("A troca de cavalo por espada só vale 1x por turno");
		}
		gastarMovimento();
		cavaloTrocado = true;
		jogador.receberTributo(TipoTributo.ESPADA, 1);
	}

	/** Ogedei: marca o uso de "1 moeda por 1 tesouro" (1x por turno). */
	void usarMoedaPorTesouro() {
		if (moedaPorTesouroUsada) {
			throw new JogadaInvalidaException("Pegar tesouro com moeda só vale 1x por turno");
		}
		pagar(TipoTributo.MOEDA, 1);
		moedaPorTesouroUsada = true;
	}

	// ---------- Khan ----------

	/** O Khan só pode ser usado se a coluna tiver o ícone, e uma vez por turno. */
	void exigirKhanDisponivel() {
		if (!khanObrigatorio) {
			throw new JogadaInvalidaException("A coluna ativada não tem o ícone do Khan");
		}
		if (khanUsado) {
			throw new JogadaInvalidaException("O Khan já foi usado neste turno");
		}
	}

	void registrarUsoDoKhan() {
		khanUsado = true;
	}

	boolean pendenteUsarKhan() {
		return khanObrigatorio && !khanUsado;
	}
}
