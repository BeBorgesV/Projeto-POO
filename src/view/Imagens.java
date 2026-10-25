package view;

import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

/** Carrega as imagens da pasta images/ uma vez só e guarda por nome. */
class Imagens {
	private static final String PASTA = "images/";

	private final Map<String, Image> porNome = new HashMap<>();

	Image get(String arquivo) {
		Image img = porNome.get(arquivo);
		if (img == null) {
			try {
				img = ImageIO.read(new File(PASTA + arquivo));
			} catch (IOException e) {
				throw new IllegalStateException("Não foi possível ler a imagem " + PASTA + arquivo, e);
			}
			if (img == null) {
				throw new IllegalStateException("Formato de imagem não suportado: " + PASTA + arquivo);
			}
			porNome.put(arquivo, img);
		}
		return img;
	}
}
