package model;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
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

	/** O 1º tesouro de uma cidade no turno custa 1 espada; cada seguinte custa 2 (manual, p. 10). */
	int custoEmEspadas(Cidade c) {
		return tesourosTomados.getOrDefault(c, 0) == 0 ? 1 : 2;
	}

	void registrarTesouroTomado(Cidade c) {
		tesourosTomados.merge(c, 1, Integer::sum);
	}

	// ---------- Khan ----------

	void registrarUsoDoKhan() {
		khanUsado = true;
	}

	boolean pendenteUsarKhan() {
		return khanObrigatorio && !khanUsado;
	}
}
