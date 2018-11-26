package client.gui;

import javafx.scene.image.Image;

import java.util.HashMap;
import java.util.Map;

/**
 * Loads images from files and caches them for later use, so that the images don't have to be loaded and parsed
 * again and again.
 */
public class JavaFXImageCache {

    private static Map<String,Image> cache = new HashMap<>();

    /**
     * Get an image and store it in the cache.
     * If the image isn't in the cache, it will be loaded from disk and put into the cache.
     *
     * @param path The path to the image file to load
     * @return The parsed image
     */
    public static Image get(String path){
        Image img = cache.get(path);
        if (img != null)
            return img;

        img = new Image(path);
        cache.put(path, img);
        return img;
    }
}
