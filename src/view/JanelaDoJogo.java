package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
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
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

import controller.ControladorDoJogo;
import model.Jogo;
import model.ObservadoIF;
import model.ObservadorIF;

/**
 * Janela do tabuleiro (até 1400 x 1050). O tabuleiro e as peças são desenhados
 * com Java2D; à direita ficam os componentes Swing, agrupados em seções:
 * janelas, ações da coluna, ações extras, habilidade do herdeiro e fim do
 * turno. O movimento, o destino do Khan, a província do tributo e a parada
 * do yurt são escolhidos clicando no mapa. Para mover, basta clicar numa das
 * paradas contornadas de branco (sem botão, como pede a 2ª iteração); para o
 * Khan, o tributo e o yurt, o botão marca as opções em amarelo. A coluna é
 * ativada clicando na cartela.
 *
 * É observadora do Jogo: a cada notificação atualiza as opções das listas e
 * habilita só o que faz sentido no momento.
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

	private static final int LARGURA = 400;
	private static final String PILHA = "Topo da pilha";
	private static final String COR_DA_PECA = "Cor da peça";
	private static final String TEXTO_KHAN = "Mover o Khan (clicar no mapa)";
	private static final String TEXTO_TRIBUTO = "Pegar tributo (clicar no mapa)";
	private static final String TEXTO_YURT = "Escolher a parada no mapa";
	private static final Font DICA = new Font("SansSerif", Font.ITALIC, 11);

	private final Jogo jogo = Jogo.getJogo();
	private final PainelTabuleiro painelTabuleiro;
	private final List<JButton> botoes = new ArrayList<>();

	private final JComboBox<String> cidades = new JComboBox<>();
	private final JComboBox<String> tesouros = new JComboBox<>();
	private final JComboBox<String> conselheiros = new JComboBox<>();
	private final JComboBox<String> espacosDeMelhoria = new JComboBox<>();
	private final JComboBox<String> colunas = new JComboBox<>();
	private final JComboBox<String> bonus = new JComboBox<>();
	private final JLabel mensagem = new JLabel(" ");
	private final JButton botaoDoKhan;
	private final JButton botaoDoTributo;
	private final JButton botaoDoYurt;

	// seção da habilidade: só aparece a linha do herdeiro da vez
	private final TitledBorder tituloDaHabilidade = BorderFactory.createTitledBorder("Habilidade");
	private final JPanel secaoDaHabilidade;
	private final JPanel linhaDaAltani;
	private final JPanel linhaDoChagatai;
	private final JPanel linhaDoOgedei;
	private final JPanel linhaDoJochi;
	private final JPanel linhaDoTolui;

	public JanelaDoJogo() {
		super("Herdeiros de Khan");
		painelTabuleiro = new PainelTabuleiro(new Imagens());

		JPanel lateral = new JPanel();
		lateral.setLayout(new BoxLayout(lateral, BoxLayout.Y_AXIS));
		lateral.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
		PainelDaVez vez = new PainelDaVez();
		vez.setAlignmentX(Component.LEFT_ALIGNMENT); // misturar alinhamentos no BoxLayout soma as larguras
		lateral.add(vez);

		JPanel janelas = secao(lateral, BorderFactory.createTitledBorder("Janelas"));
		linha(janelas, botao("Mostrar cartela", CARTELA), botao("Mostrar cartas", CARTAS));

		JPanel coluna = secao(lateral, BorderFactory.createTitledBorder("Ações da coluna"));
		linha(coluna, dica("Mover: clique numa parada contornada de branco no mapa"));
		botaoDoKhan = botao(TEXTO_KHAN, KHAN);
		linha(coluna, botaoDoKhan);
		botaoDoTributo = botao(TEXTO_TRIBUTO, TRIBUTO);
		linha(coluna, botaoDoTributo);

		JPanel extras = secao(lateral, BorderFactory.createTitledBorder("Ações extras (a qualquer momento)"));
		linha(extras, rotulo("Atacar cidade"), cidades, tesouros);
		linha(extras, rotulo(""), botao("Atacar (gasta espadas)", ATACAR));
		botaoDoYurt = botao(TEXTO_YURT, YURT);
		linha(extras, rotulo("Construir yurt"), botaoDoYurt);
		linha(extras, rotulo("Entregar em"), conselheiros, botao("Entregar", ENTREGAR));
		linha(extras, rotulo("Melhoria"), espacosDeMelhoria, botao("Comprar", COMPRAR));
		linha(extras, rotulo(""), botao("Pegar grátis (linha completa)", GRATIS));
		linha(extras, rotulo("Peça de bônus"), bonus, botao("Usar", BONUS));

		secaoDaHabilidade = secao(lateral, tituloDaHabilidade);
		linhaDaAltani = linha(secaoDaHabilidade, botao("Trocar peão", TROCAR_PEAO), dica("antes de ativar a coluna"));
		linhaDoChagatai = linha(secaoDaHabilidade, botao("Trocar 1 cavalo por 1 espada", CAVALO_POR_ESPADA));
		linhaDoOgedei = linha(secaoDaHabilidade, botao("Pagar 1 moeda por 1 tesouro", MOEDA_POR_TESOURO),
				dica("usa a cidade de \"Atacar\""));
		linhaDoJochi = linha(secaoDaHabilidade, rotulo("Pôr melhoria em"), colunas);
		linhaDoTolui = linha(secaoDaHabilidade, dica("Pode construir yurt nas paradas vizinhas a ele"));

		JPanel fim = secao(lateral, BorderFactory.createEmptyBorder(6, 0, 0, 0));
		JButton encerrar = botao("Encerrar turno", ENCERRAR);
		encerrar.setFont(encerrar.getFont().deriveFont(Font.BOLD, 14f));
		linha(fim, encerrar);
		mensagem.setPreferredSize(new Dimension(LARGURA - 20, 50));
		mensagem.setForeground(new Color(170, 0, 0));
		JPanel linhaDaMensagem = linha(fim, mensagem);
		linhaDaMensagem.setPreferredSize(new Dimension(LARGURA - 12, 54));
		linhaDaMensagem.setMaximumSize(linhaDaMensagem.getPreferredSize());

		add(painelTabuleiro, BorderLayout.CENTER);
		add(lateral, BorderLayout.EAST);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResizable(false);
		pack();
		ControladorDoJogo.getControlador().registra(this);
		atualizar();
	}

	private JPanel secao(JPanel lateral, Border borda) {
		JPanel s = new JPanel();
		s.setLayout(new BoxLayout(s, BoxLayout.Y_AXIS));
		s.setBorder(borda);
		s.setAlignmentX(Component.LEFT_ALIGNMENT);
		lateral.add(s);
		return s;
	}

	private JButton botao(String texto, String comando) {
		JButton b = new JButton(texto);
		b.setActionCommand(comando);
		botoes.add(b);
		return b;
	}

	private JLabel rotulo(String texto) {
		JLabel l = new JLabel(texto);
		l.setPreferredSize(new Dimension(95, 24));
		return l;
	}

	private JLabel dica(String texto) {
		JLabel l = new JLabel(texto);
		l.setFont(DICA);
		return l;
	}

	private JPanel linha(JPanel secao, JComponent... componentes) {
		JPanel linha = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 1));
		for (JComponent c : componentes) {
			if (c instanceof JComboBox) {
				c.setPreferredSize(new Dimension(130, 24));
			}
			linha.add(c);
		}
		linha.setAlignmentX(Component.LEFT_ALIGNMENT);
		linha.setPreferredSize(new Dimension(LARGURA - 12, 30));
		linha.setMaximumSize(linha.getPreferredSize());
		secao.add(linha);
		return linha;
	}

	/**
	 * Põe as 3 janelas na tela sem sobreposição: o jogo à esquerda e, na faixa
	 * que sobra à direita, a cartela em cima e as cartas embaixo (cartela e
	 * cartas se reduzem para caber). Em tela estreita demais, a cartela e as
	 * cartas ficam lado a lado na parte de baixo, por cima do tabuleiro.
	 */
	public void arrumarJanelas(JanelaCartela cartela, JanelaCartas cartas) {
		Rectangle tela = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
		setLocation(tela.x, tela.y);
		int x = tela.x + getWidth();
		int largura = tela.x + tela.width - x;
		if (largura >= 400) {
			cartela.ajustarLargura(Math.min(largura, 1000));
			cartela.setLocation(x, tela.y);
			int yCartas = tela.y + cartela.getHeight();
			cartas.ocupar(x, yCartas, Math.min(largura, 560), tela.y + tela.height - yCartas);
		} else {
			// shortcut: em tela com menos de ~1800 px de largura não cabe tudo; as janelas cobrem parte do tabuleiro
			int metade = tela.width / 2;
			cartela.ajustarLargura(Math.min(metade, 1000));
			cartela.setLocation(tela.x, tela.y + tela.height - cartela.getHeight());
			cartas.ocupar(tela.x + metade, tela.y + tela.height - 400, Math.min(metade, 560), 400);
		}
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

	/**
	 * Começa (ou, com {@code comando} null, termina) uma escolha pelo clique no
	 * mapa: os lugares possíveis ficam marcados em amarelo e o botão do
	 * comando passa a servir para cancelar.
	 *
	 * @param lugares nomes de paradas (yurt), de províncias, ou "KHAN:" + destino do Khan
	 */
	public void escolherNoMapa(String comando, List<String> lugares) {
		painelTabuleiro.marcar(lugares);
		botaoDoKhan.setText(KHAN.equals(comando) ? "Cancelar a escolha do Khan" : TEXTO_KHAN);
		botaoDoTributo.setText(TRIBUTO.equals(comando) ? "Cancelar a escolha do tributo" : TEXTO_TRIBUTO);
		botaoDoYurt.setText(YURT.equals(comando) ? "Cancelar a escolha" : TEXTO_YURT);
	}

	/** Lugar marcado onde o jogador clicou, ou null. */
	public String lugarMarcadoEm(int x, int y) {
		return painelTabuleiro.marcadoEm(x, y);
	}

	/** Mensagem curta no rodapé (jogada inválida etc.), sem caixa de diálogo. */
	public void mostrarMensagem(String texto) {
		mensagem.setText("<html>" + texto + "</html>");
	}

	public String cidadeEscolhida() {
		return (String) cidades.getSelectedItem();
	}

	public String tesouroEscolhido() {
		return (String) tesouros.getSelectedItem();
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
		String herdeiro = jogo.herdeiroDe(cor);
		preencher(cidades, jogo.cidadesDisponiveis());
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

		tituloDaHabilidade.setTitle("Habilidade de " + herdeiro);
		linhaDaAltani.setVisible("Altani".equals(herdeiro));
		linhaDoChagatai.setVisible("Chagatai".equals(herdeiro));
		linhaDoOgedei.setVisible("Ogedei".equals(herdeiro));
		linhaDoJochi.setVisible("Jochi".equals(herdeiro));
		linhaDoTolui.setVisible("Tolui".equals(herdeiro));
		secaoDaHabilidade.revalidate();
		secaoDaHabilidade.repaint();

		for (JButton b : botoes) {
			String c = b.getActionCommand();
			boolean ligado = emTurno;
			if (CARTELA.equals(c) || CARTAS.equals(c)) {
				ligado = !jogo.partidaEncerrada();
			} else if (TROCAR_PEAO.equals(c)) {
				ligado = !jogo.turnoEmAndamento() && !jogo.partidaEncerrada();
			} else if (GRATIS.equals(c)) {
				ligado = emTurno && jogo.melhoriasGratisPendentes() > 0;
			} else if (KHAN.equals(c)) {
				ligado = emTurno && jogo.khanPendente();
			} else if (TRIBUTO.equals(c)) {
				ligado = emTurno && jogo.acoesDeTributoRestantes() > 0;
			}
			b.setEnabled(ligado);
		}
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
