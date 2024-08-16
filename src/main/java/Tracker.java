import org.json.JSONObject;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import javax.imageio.ImageIO;
import javax.swing.*;
import static java.lang.Thread.sleep;

public class Tracker {

    /*
        Latitude/longitude will be converted to x/y coordinates,
        which will then be used to place the ISS on a Mercator projection.
     */
    static double latitude, longitude, x, y;
    static final int mapWidth = 1024;
    static final int mapHeight = 1024;

    public static void main(String[] args) throws Exception {
        // mapPicture and issPicture are the actual image files
        BufferedImage mapPicture = ImageIO.read(new File("src/main/java/mercatorsquare.jfif"));
        BufferedImage issPicture = ImageIO.read(new File("src/main/java/iss.png"));

        // To place these inside of a JPanel, we convert the image files to JLabel objects.
        JLabel mapLabel = new JLabel(new ImageIcon(mapPicture));
        mapLabel.setBounds(0, 0, mapWidth, mapHeight);
        JLabel issLabel = new JLabel(new ImageIcon(issPicture));
        issLabel.setBounds(0, 0, 30, 19);

        // Setting the JPanel layout to null allows us to manually control positioning of the JLabel objects.
        JPanel panel = new JPanel(null);
        panel.setBounds(0, 0, mapWidth, mapHeight);

        // Initialize our frame, set the ISS label size, and add the ISS and map labels to the JPanel.
        JFrame f = new JFrame();
        f.setSize(mapWidth, mapHeight);
        issLabel.setSize(30, 19);
        panel.add(issLabel);
        panel.add(mapLabel);
        mapLabel.setLocation(0, 0);

        // Finally, add the finished panel to the frame and enable frame visibility.
        f.add(panel);
        f.setVisible(true);
        f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        URL dataURL = new URL("http://api.open-notify.org/iss-now.json?callback=?");
        while (true) {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(dataURL.openStream(), "UTF-8"))) {
                String line = reader.readLine();
                String temp = line;
                line = line.substring(2, temp.length()-1);
                JSONObject dataJSON = new JSONObject(line).getJSONObject("iss_position");
                longitude = Double.parseDouble(dataJSON.getString("longitude"));
                latitude = Double.parseDouble(dataJSON.getString("latitude"));
                double latRad = latitude*Math.PI / 180;

                double mercN = Math.log(Math.tan((Math.PI/4) + (latRad/2)));
                x = (longitude+180)*(mapWidth/360); // or 180 and 360?
                y = (mapHeight/2) - (mapWidth*mercN / (2*Math.PI));
                System.out.println("Latitude: " + latitude + " " + "Longitude: " + longitude);
                System.out.println("Point X:" + x + ", " + "Point Y: " + y);
                issLabel.setLocation((int) x, (int) y);
                sleep(2000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}  