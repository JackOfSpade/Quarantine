import uk.co.caprica.vlcj.component.EmbeddedMediaPlayerComponent;
import uk.co.caprica.vlcj.player.MediaPlayer;
import uk.co.caprica.vlcj.player.MediaPlayerEventAdapter;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class VLCJPlayer {

    private final JFrame frame;

    private final EmbeddedMediaPlayerComponent mediaPlayerComponent;

    public VLCJPlayer(String videoLocation, int xCoordinate, int yCoordinate, int state) {
        frame = new JFrame("   Loading...   Please wait...");
        frame.setBounds(xCoordinate, yCoordinate, 2000, 1300);
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.out.println(e);
                mediaPlayerComponent.release();
                System.exit(0);
            }
        });

        mediaPlayerComponent = new EmbeddedMediaPlayerComponent();

        mediaPlayerComponent.getMediaPlayer().addMediaPlayerEventListener(new MediaPlayerEventAdapter() {
            @Override
            public void playing(MediaPlayer mediaPlayer) {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        if(state == 0)
                        {
                            frame.setTitle("   Intro");
                        }
                        else if(state == 1)
                        {
                            frame.setTitle("   You are being evacuated");
                        }

                        frame.setVisible(true);
                    }
                });
            }

            @Override
            public void finished(MediaPlayer mediaPlayer) {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        frame.setVisible(false);

                        if(state == 1)
                        {
                            System.exit(0);
                        }
                    }
                });
            }

            @Override
            public void error(MediaPlayer mediaPlayer) {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        JOptionPane.showMessageDialog(
                                frame,
                                "   Failed to play media",
                                "   Error",
                                JOptionPane.ERROR_MESSAGE
                        );
                    }
                });
            }
        });

        frame.setContentPane(mediaPlayerComponent);
        frame.setVisible(true);

        mediaPlayerComponent.getMediaPlayer().prepareMedia(videoLocation);
        mediaPlayerComponent.getMediaPlayer().play();
    }
}