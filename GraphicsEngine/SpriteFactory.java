package GraphicsEngine;
import java.util.HashMap;
import java.util.Map;

import javax.swing.ImageIcon;

/*
 * Singleton responsable du stockage des images
 * Utilise un système de poids mouche pour lier un chemin à un fichier
 */
public class SpriteFactory {
    private SpriteFactory(){}

    public static SpriteFactory GetInstance() {
        if (instance == null)
            instance = new SpriteFactory();
        return instance;
    }

    public ImageIcon GetSprite(String path) {
        if (!sprites.containsKey(path)) sprites.put(path, new ImageIcon(path));
        return sprites.get(path);
    }

    private static SpriteFactory instance = null;

    private Map<String, ImageIcon> sprites = new HashMap<String, ImageIcon>();
}
