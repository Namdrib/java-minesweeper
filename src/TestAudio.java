
import java.io.File;
import java.util.Arrays;
import java.util.List;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import minesweeper.Global;
import javafx.embed.swing.JFXPanel;

public class TestAudio {
  // Takes an absolute path
  public static void playSound1(String s) {
    try {
      AudioInputStream audioInputStream =
          AudioSystem.getAudioInputStream(new File(s).getAbsoluteFile());
      Clip clip = AudioSystem.getClip();
      clip.open(audioInputStream);
      clip.start();
      clip.close();
      audioInputStream.close();
    } catch (Exception ex) {
      System.out.println("Error with playing sound.");
      ex.printStackTrace();
    }
  }

  // Path must be relative to the project folder,
  // not from this particular file
  public static void playSound2(String s) {
    try {
      Media hit = new Media(new File(s).toURI().toString());
      MediaPlayer mediaPlayer = new MediaPlayer(hit);
      mediaPlayer.play();
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  // Plays mp3 and wav, but not ogg
  public static void main(String[] args) {

    @SuppressWarnings("unused")
    // Make javafx sound work
    // https://stackoverflow.com/questions/14025718/javafx-toolkit-not-initialized-when-trying-to-play-an-mp3-file-through-mediap
    final JFXPanel fxPanel = new JFXPanel(); // this makes it work by initialising a thing

    List<String> extensions = Arrays.asList("mp3", "wav", "ogg");
    List<String> soundNames = Arrays.asList("lose", "win", "tick");

    for (String s : soundNames) {
      for (String e : extensions) {
        playSound2(Global.SOUND_PATH + s + "." + e);
      }
    }
  }

}
