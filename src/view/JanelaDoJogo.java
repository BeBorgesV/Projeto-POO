package view;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionListener;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import controller.ControladorDoJogo;
import model.Jogo;
import model.ObservadoIF;
import model.ObservadorIF;

/**
 * Janela do tabuleiro (até 1400 x 1050). O tabuleiro e as peças são desenhados
 * com Java2D; à direita ficam os componentes Swing das ações que precisam de
 * escolha do jogador (o movimento é pelo clique no mapa e a coluna é ativada
 * pelo clique na cartela).
 *
 * É observadora do Jogo: a cada notificação atualiza as opções das listas e
 * habilita só os botões que fazem sentido.
 */
public class JanelaDoJogo extends JFrame implements ObservadorIF {
	private static final long serialVersionUID = 1L;

	// comandos dos botões (ActionEvent.getActionCommand), tratados no Controller
	public static final String CARTELA = "CARTELA";
	public static final String CARTAS = "CARTAS";
	public static final String TROCAR_PEAO = "TROCAR_PEAO";
	public static final String CAVALO_POR_ESPADA = "CAVALO_POR_ESPADA";
	public static final String TRIBUTO = "TRIBUTO";
	public static final String KHAN = "KHAN";
	public static final String ATACAR = "ATACAR";
	public static final String MOEDA_POR_TESOURO = "MOEDA_POR_TESOURO";
	public static final String YURT = "YURT";
	public static final String ENTREGAR = "ENTREGAR";
	public static final String COMPRAR = "COMPRAR";
	public static final String GRATIS = "GRATIS";
	public static final String BONUS = "BONUS";
	public static final String ENCERRAR = "ENCERRAR";

	private static final String PILHA = "Pilha";
	private static final String COR_DA_PECA = "Cor da peça";

	private final Jogo jogo = Jogo.getJogo();
	private final PainelTabuleiro painelTabuleiro;
	private final List<JButton> botoes = new ArrayList<>();

	private final JComboBox<String> provincias = new JComboBox<>();
	private final JComboBox<String> destinosDoKhan = new JComboBox<>();
	private final JComboBox<String> cidades = new JComboBox<>();
	private final JComboBox<String> tesouros = new JComboBox<>();
	private final JComboBox<String> paradasParaYurt = new JComboBox<>();
	private final JComboBox<String> conselheiros = new JComboBox<>();
	private final JComboBox<String> espacosDeMelhoria = new JComboBox<>();
	private final JComboBox<String> colunas = new JComboBox<>();
	private final JComboBox<String> bonus = new JComboBox<>();
	private final JLabel mensagem = new JLabel(" ");

	public JanelaDoJogo() {
		super("Herdeiros de Khan");
		painelTabuleiro = new PainelTabuleiro(new Imagens());

		JPanel lateral = new JPanel();
		lateral.setLayout(new BoxLayout(lateral, BoxLayout.Y_AXIS));
		lateral.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
		PainelDaVez vez = new PainelDaVez();
		vez.setAlignmentX(Component.LEFT_ALIGNMENT); // misturar alinhamentos no BoxLayout soma as larguras
		lateral.add(vez);
		linha(lateral, botao("Mostrar cartela", CARTELA), botao("Mostrar cartas", CARTAS));
		linha(lateral, botao("Trocar peão", TROCAR_PEAO), botao("Cavalo → espada", CAVALO_POR_ESPADA));
		linha(lateral, rotulo("Tributo"), provincias, botao("Pegar", TRIBUTO));
		linha(lateral, rotulo("Khan"), destinosDoKhan, botao("Mover Khan", KHAN));
		linha(lateral, rotulo("Cidade"), cidades, tesouros);
		linha(lateral, botao("Atacar", ATACAR), botao("Moeda → tesouro", MOEDA_POR_TESOURO));
		linha(lateral, rotulo("Yurt"), paradasParaYurt, botao("Construir", YURT));
		linha(lateral, rotulo("Conselheiro"), conselheiros, botao("Entregar", ENTREGAR));
		linha(lateral, rotulo("Melhoria"), espacosDeMelhoria, colunas);
		linha(lateral, botao("Comprar (2 moedas)", COMPRAR), botao("Grátis", GRATIS));
		linha(lateral, rotulo("Bônus"), bonus, botao("Usar", BONUS));
		linha(lateral, botao("Encerrar turno", ENCERRAR));
		mensagem.setPreferredSize(new Dimension(390, 40));
		mensagem.setAlignmentX(Component.LEFT_ALIGNMENT);
		lateral.add(mensagem);

		add(painelTabuleiro, BorderLayout.CENTER);
		add(lateral, BorderLayout.EAST);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResizable(false);
		pack();
		ControladorDoJogo.getControlador().registra(this);
		atualizar();
	}

	private JButton botao(String texto, String comando) {
		JButton b = new JButton(texto);
		b.setActionCommand(comando);
		botoes.add(b);
		return b;
	}

	private JLabel rotulo(String texto) {
		JLabel l = new JLabel(texto);
		l.setPreferredSize(new Dimension(75, 24));
		return l;
	}

	private void linha(JPanel lateral, JComponent... componentes) {
		JPanel linha = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 2));
		for (JComponent c : componentes) {
			if (c instanceof JComboBox) {
				c.setPreferredSize(new Dimension(140, 24));
			}
			linha.add(c);
		}
		linha.setAlignmentX(Component.LEFT_ALIGNMENT);
		linha.setPreferredSize(new Dimension(400, 32));
		linha.setMaximumSize(linha.getPreferredSize());
		lateral.add(linha);
	}

	// ---------- ligação com o Controller ----------

	/** Um único tratador recebe todos os botões e decide pelo comando. */
	public void adicionarTratadorDosBotoes(ActionListener tratador) {
		for (JButton b : botoes) {
			b.addActionListener(tratador);
		}
	}

	public void adicionarTratadorDoTabuleiro(MouseListener tratador) {
		painelTabuleiro.addMouseListener(tratador);
	}

	public String paradaEm(int x, int y) {
		return painelTabuleiro.paradaEm(x, y);
	}

	/** Mensagem curta no rodapé (jogada inválida etc.), sem caixa de diálogo. */
	public void mostrarMensagem(String texto) {
		mensagem.setText("<html>" + texto + "</html>");
	}

	public String provinciaEscolhida() {
		return (String) provincias.getSelectedItem();
	}

	public String destinoDoKhanEscolhido() {
		return (String) destinosDoKhan.getSelectedItem();
	}

	public String cidadeEscolhida() {
		return (String) cidades.getSelectedItem();
	}

	public String tesouroEscolhido() {
		return (String) tesouros.getSelectedItem();
	}

	public String paradaParaYurtEscolhida() {
		return (String) paradasParaYurt.getSelectedItem();
	}

	public String conselheiroEscolhido() {
		return (String) conselheiros.getSelectedItem();
	}

	/** Espaço da área (0 a 4) ou -1 para o topo da pilha. */
	public int espacoDeMelhoriaEscolhido() {
		return espacosDeMelhoria.getSelectedIndex() - 1;
	}

	/** Coluna escolhida para a melhoria; "" = a da cor da peça. */
	public String colunaEscolhida() {
		String c = (String) colunas.getSelectedItem();
		return c == null || COR_DA_PECA.equals(c) ? "" : c;
	}

	public String bonusEscolhido() {
		return (String) bonus.getSelectedItem();
	}

	// ---------- Observer ----------

	@Override
	public void notify(ObservadoIF o) {
		atualizar();
	}

	private void atualizar() {
		if (!jogo.partidaIniciada()) {
			return;
		}
		boolean emTurno = jogo.turnoEmAndamento() && !jogo.partidaEncerrada();
		String cor = jogo.corDaVez();
		preencher(provincias, jogo.provinciasParaTributo());
		preencher(destinosDoKhan, jogo.destinosDoKhan());
		preencher(cidades, jogo.cidadesDisponiveis());
		preencher(paradasParaYurt, jogo.paradasParaYurt());
		preencher(conselheiros, jogo.conselheirosParaEntrega());
		preencher(bonus, jogo.bonusGuardadosDe(cor));
		if (tesouros.getItemCount() == 0) {
			preencher(tesouros, jogo.tiposDeTesouro());
		}
		if (espacosDeMelhoria.getItemCount() == 0) {
			List<String> espacos = new ArrayList<>();
			espacos.add(PILHA);
			for (int i = 1; i <= 5; i++) {
				espacos.add("Espaço " + i);
			}
			preencher(espacosDeMelhoria, espacos);
			List<String> nomes = new ArrayList<>();
			nomes.add(COR_DA_PECA);
			nomes.addAll(jogo.colunas().subList(0, 3));
			preencher(colunas, nomes);
		}
		for (JButton b : botoes) {
			String c = b.getActionCommand();
			boolean ligado = emTurno;
			if (CARTELA.equals(c) || CARTAS.equals(c)) {
				ligado = !jogo.partidaEncerrada();
			} else if (TROCAR_PEAO.equals(c)) {
				ligado = !jogo.turnoEmAndamento() && !jogo.partidaEncerrada() && jogo.quantidadeDePeoes(cor) > 1;
			} else if (CAVALO_POR_ESPADA.equals(c)) {
				ligado = emTurno && "Chagatai".equals(jogo.herdeiroDe(cor));
			} else if (MOEDA_POR_TESOURO.equals(c)) {
				ligado = emTurno && "Ogedei".equals(jogo.herdeiroDe(cor));
			} else if (GRATIS.equals(c)) {
				ligado = emTurno && jogo.melhoriasGratisPendentes() > 0;
			} else if (KHAN.equals(c)) {
				ligado = emTurno && jogo.khanPendente();
			}
			b.setEnabled(ligado);
		}
		colunas.setEnabled("Jochi".equals(jogo.herdeiroDe(cor)));
		painelTabuleiro.repaint();
	}

	private void preencher(JComboBox<String> combo, List<String> itens) {
		Object escolhido = combo.getSelectedItem();
		combo.removeAllItems();
		for (String s : itens) {
			combo.addItem(s);
		}
		if (escolhido != null && itens.contains(escolhido)) {
			combo.setSelectedItem(escolhido);
		}
	}
}
