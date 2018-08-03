
import java.io.File;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
// Make javafx sound work
// https://stackoverflow.com/questions/14025718/javafx-toolkit-not-initialized-when-trying-to-play-an-mp3-file-through-mediap
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
    final JFXPanel fxPanel = new JFXPanel(); // this makes it work by initialising a thing
    // TODO Auto-generated method stub
    playSound2("assets/sounds/lose.mp3");
    playSound2("assets/sounds/lose.wav");
    playSound2("assets/sounds/lose.ogg");
    playSound2("assets/sounds/win.mp3");
    playSound2("assets/sounds/win.wav");
    playSound2("assets/sounds/win.ogg");
    playSound2("assets/sounds/tick.mp3");
    playSound2("assets/sounds/tick.wav");
    playSound2("assets/sounds/tick.ogg");
  }

}
