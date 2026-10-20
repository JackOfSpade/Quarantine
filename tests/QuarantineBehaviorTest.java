import java.awt.Rectangle;
import java.io.InputStream;
import java.util.jar.JarFile;
import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

/** Runs against the fresh distributable JAR without opening windows, playing audio, or loading native VLC. */
public final class QuarantineBehaviorTest {
    private static int assertions;

    private static void require(boolean condition, String message) {
        assertions++;
        if (!condition) throw new AssertionError(message);
    }

    public static void main(String[] args) throws Exception {
        Rectangle player = new Rectangle(0, 0, 10, 10);
        require(Quarantine.IntersectionCheck(player, new Rectangle(10, 3, 4, 4)), "touching building edge must block movement");
        require(!Quarantine.IntersectionCheck(player, new Rectangle(11, 3, 4, 4)), "separated buildings must allow movement");
        require(Quarantine.IntersectionCheck(player, new Rectangle(-20, -20, 50, 50)), "a player inside a building is colliding");
        require(Quarantine.ArrayIntersectionCheck(player, new Rectangle[]{new Rectangle(30, 30, 5, 5), new Rectangle(8, 8, 5, 5)}), "a later building must still block movement");
        require(!Quarantine.ArrayIntersectionCheck(player, new Rectangle[0]), "empty building field is traversable");
        require(Quarantine.ChaseCheck(player, new Rectangle(3, 4, 10, 10), 5), "awareness includes exact center-distance boundary");
        require(!Quarantine.ChaseCheck(player, new Rectangle(3, 4, 10, 10), 4), "zombies must not chase beyond awareness");
        require(Quarantine.PlayerVisibilityCheck(player, new Rectangle(120, 0, 10, 10)), "player sees the visibility boundary");
        require(!Quarantine.PlayerVisibilityCheck(player, new Rectangle(121, 0, 10, 10)), "player cannot see beyond its radius");
        require(Quarantine.LightVisibilityCheck(new Rectangle[]{new Rectangle(300, 0, 10, 10), player}, new Rectangle(75, 0, 10, 10)), "every light contributes visibility");
        require(!Quarantine.LightVisibilityCheck(new Rectangle[]{player}, new Rectangle(76, 0, 10, 10)), "lights must respect their own smaller radius");
        Zombie fast = new FastZombie();
        Zombie slow = new SlowZombie();
        require(fast.getSpeed() > slow.getSpeed() && slow.getSpeed() > 0, "fast and slow enemies must retain distinct movement speeds");
        fast.maxAwareness();
        require(fast.getAwareness() > 100, "alerted zombie must see beyond normal awareness");
        for (int i = 0; i < 128; i++) {
            fast.regularAwareness();
            require(fast.getAwareness() >= 25 && fast.getAwareness() <= 100, "normal awareness must recover after alert expires");
        }
        RectangleAndZombiePair pair = new RectangleAndZombiePair(fast, new Rectangle());
        pair.setRectangleBounds(20, 30, 10, 12);
        require(pair.getZombie() == fast && pair.getRecZombie().equals(new Rectangle(20, 30, 10, 12)), "movement must preserve the paired zombie's state");
        for (String resource : new String[]{"Instructions.jpeg", "Helicopter.gif"}) {
            try (InputStream in = Quarantine.class.getResourceAsStream('/' + resource)) {
                require(in != null && ImageIO.read(in) != null, "missing or invalid packaged image: " + resource);
            }
        }
        for (String resource : new String[]{"Evac.wav", "GetToDaChoppa.wav", "ZombieAmbience.wav"}) {
            try (AudioInputStream in = AudioSystem.getAudioInputStream(Quarantine.class.getResource('/' + resource))) {
                require(in.getFormat().getChannels() > 0 && in.read(new byte[in.getFormat().getFrameSize() * 256]) > 0, "missing or invalid packaged sound: " + resource);
            }
        }
        ClassLoader loader = Quarantine.class.getClassLoader();
        for (String type : new String[]{"VLCJPlayer", "uk.co.caprica.vlcj.component.EmbeddedMediaPlayerComponent", "com.sun.jna.Native", "org.slf4j.Logger"}) {
            require(Class.forName(type, false, loader) != null, "missing packaged dependency: " + type);
        }
        try (JarFile jar = new JarFile("build/Quarantine.jar")) {
            require("Quarantine".equals(jar.getManifest().getMainAttributes().getValue("Main-Class")), "JAR entry point must launch the game");
            require(jar.getEntry("QuarantineBehaviorTest.class") == null, "test code must not ship in the game");
        }
        System.out.println("Packaged game smoke test passed: " + assertions + " assertions");
    }
}
