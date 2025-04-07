package ru.shze.demo_project.utils;

import lombok.NoArgsConstructor;
import org.jaudiotagger.tag.id3.reference.MediaPlayerRating;
import org.springframework.stereotype.Service;
import ru.shze.demo_project.model.SoundEntity;

import javax.print.attribute.standard.Media;
import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.TimerTask;

@Service
public class SoundPlayer implements AutoCloseable {
    private AudioInputStream stream;
    private Clip clip = null;
    private FloatControl floatControl = null;
    private boolean playing = false;
    private boolean released = false;

    public SoundPlayer() {
    }

    public SoundPlayer setProperty(String file) throws Exception {
        Path $pathToFile = Paths.get(file);
        File $file = $pathToFile.toFile();
        try {
            this.stream = AudioSystem.getAudioInputStream($file);
            this.clip = AudioSystem.getClip();
            this.clip.open(stream);
            this.clip.addLineListener(new Listener());
            this.floatControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            this.released = true;
        } catch (IOException | UnsupportedAudioFileException | LineUnavailableException exc) {
            exc.printStackTrace();
            this.released = false;
            close();
        }

        return this;
    }

    public SoundPlayer setVolume(float x) {
        if (x < 0) x = 0;
        if (x > 1) x = 1;
        float min = floatControl.getMinimum();
        float max = floatControl.getMaximum();
        this.floatControl.setValue((max - min) * x + min);
        return this;
    }

    public SoundPlayer play() {
        if (released) {
            this.clip.stop();
            this.clip.setFramePosition(0);
            this.clip.start();
            this.playing = true;
        }
        return this;
    }


    public float getVolume() {
        float v = floatControl.getValue();
        float min = floatControl.getMinimum();
        float max = floatControl.getMaximum();
        return (v - min) / (max - min);
    }

    public void join() {
        if (!this.released) return;
        synchronized (clip) {
            try {
                while (this.playing)
                    this.clip.wait();
            } catch (InterruptedException ignored) {
            }
        }
    }

    @Override
    public void close() throws Exception {
        if (this.clip != null)
            this.clip.close();

        if (this.stream != null)
            try {
                this.stream.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
    }

    private class Listener implements LineListener {

        public void update(LineEvent ev) {
            if (ev.getType() == LineEvent.Type.STOP) {
                playing = false;
                synchronized (clip) {
                    clip.notify();
                }
            }

        }
    }
}
