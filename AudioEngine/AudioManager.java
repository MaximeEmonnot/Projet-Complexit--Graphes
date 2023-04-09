package AudioEngine;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

/*
 * Singleton responsable de la gestion des fichiers Audio
 * Permet d'ouvrir et de jouer des fichiers .WAV
 */
public class AudioManager {
    
    private AudioManager() {}

    public static synchronized AudioManager GetInstance()  {
        if (instance == null) 
            instance = new AudioManager();
        return instance;
    }

    public void PlaySound(String path) throws Exception{
        if (!sounds.containsKey(path))
            sounds.put(path, new File(path));
        
        audio = AudioSystem.getAudioInputStream(sounds.get(path));
        clip = AudioSystem.getClip();
        clip.open(audio);
        clip.start();
    }

    public void StopSound() throws Exception {
        if (audio != null && clip != null){
            audio.close();
            clip.close();
            clip.stop();
        }
    }

    private static AudioManager instance = null;

    private Map<String, File> sounds = new HashMap<String, File>();

    private Clip clip;
    private AudioInputStream audio;
}
