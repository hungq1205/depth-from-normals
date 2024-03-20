import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.joml.Math;
import org.joml.Vector3f;

import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.awt.GLJPanel;

public class App {
    public static final int SCREEN_WIDTH = 1100; 
    public static final int SCREEN_HEIGHT = 750;
    public static final int NORMAL_IMAGE_SIZE = 310;
    public static final float PI = 3.14f;
    public static final String INIT_PATH = "D:\\Download\\normal.png";

    private static String imgPath = INIT_PATH;
    private static GLJPanel gljpanel;
    private static SurfaceDisplay surface;
    private static JLabel imgDisplay, filename;
    
    private static BufferedImage img = null;
    private static Point2D initMouse;
    private static boolean depthSliderChanged = false;
    private static int optimizeFactor = 2;

    public static void main(String[] args) throws Exception 
    {
        final GLProfile profile = GLProfile.get(GLProfile.GL4);
        GLCapabilities cap = new GLCapabilities(profile);

        try 
        {
            img = ImageIO.read(new File(imgPath));
        } catch (IOException e) {
            System.out.println(e);
        }
        
        surface = new SurfaceDisplay(null);
        calcNormalSurface(img);

        final JFrame frame = new JFrame (" Surface");
        frame.setSize(SCREEN_WIDTH, SCREEN_HEIGHT);
        
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new FlowLayout());
        controlPanel.setPreferredSize(new Dimension(1050, 700));
        frame.add(controlPanel);

        JPanel content = new JPanel();
        content.setPreferredSize(new Dimension(1050, 700));
        content.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.weightx = 1;

        gljpanel = new GLJPanel(cap);
        gljpanel.addGLEventListener(surface);
        gljpanel.setPreferredSize(new Dimension(700, 700));
        gljpanel.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e)
            {
                initMouse = e.getPoint();
            }

            public void mouseReleased(MouseEvent e)
            {
                if (e.getPoint().distanceSq(initMouse) > 250)
                {
                    surface.transform.rotation.x += (e.getY() - initMouse.getY()) * PI / 300; 
                    surface.transform.rotation.y += (e.getX() - initMouse.getX()) * PI / 300;
                    reloadSurface();
                }
            }
        });
        gljpanel.addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent e)
            {
                if (e.getPoint().distanceSq(initMouse) > 500)
                {
                    surface.transform.rotation.x += (e.getY() - initMouse.getY()) * PI / 300; 
                    surface.transform.rotation.y += (e.getX() - initMouse.getX()) * PI / 300;
                    reloadSurface();
                    initMouse = e.getPoint();
                }
            }
        });
        content.add(gljpanel, gbc);
        
        gbc.weightx = 0;

        JButton button = new JButton("Browse image");
        button.setPreferredSize(new Dimension(150, 30));
        button.setAlignmentX(JButton.CENTER_ALIGNMENT);
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
                JFileChooser fileChooser = new JFileChooser();

                fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("Image(jpg, png)", "jpg", "png"));

                if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION)
                    chooseImage(fileChooser.getSelectedFile().getAbsolutePath());
            }
        });
        
        filename = new JLabel(imgPath);
        filename.setBorder(new EmptyBorder(5, 0, 0, 0));
        filename.setAlignmentX(JLabel.CENTER_ALIGNMENT);

        imgDisplay = new JLabel(new ImageIcon(img.getScaledInstance(NORMAL_IMAGE_SIZE, NORMAL_IMAGE_SIZE, java.awt.Image.SCALE_DEFAULT)));
        imgDisplay.setMaximumSize(new Dimension(NORMAL_IMAGE_SIZE, NORMAL_IMAGE_SIZE));
        imgDisplay.setBorder(new EmptyBorder(30, 0, 10, 0));
        imgDisplay.setAlignmentX(JLabel.CENTER_ALIGNMENT);
 
        JLabel depthSliderLabel = new JLabel("Depth intensity: (10:1)");
        depthSliderLabel.setBorder(new EmptyBorder(0, 0, 0, 0));
        depthSliderLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);

        JSlider depthSlider = new JSlider(JSlider.HORIZONTAL, 0, 100, 10);
        depthSlider.setMinorTickSpacing(2);
        depthSlider.setMajorTickSpacing(10);
        depthSlider.setPaintTicks(true);
        depthSlider.setPaintLabels(true);
        depthSlider.setBorder(new EmptyBorder(15, 0, 20, 0));
        depthSlider.setSize(new Dimension(100, 200));
        depthSlider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                depthSliderChanged = true;
            }
        });
        depthSlider.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(MouseEvent e) {
                if (depthSliderChanged)
                {
                    surface.depthMultiplier = (float)(((JSlider) e.getSource()).getValue()) / 10f;
                    reloadSurface();
                    depthSliderChanged = false;
                }
            }
        });
        
        JButton topDownViewButton = new JButton("Top down view");
        topDownViewButton.setPreferredSize(new Dimension(150, 30));
        topDownViewButton.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                surface.transform.rotation = new Vector3f((float)(Math.PI / 2), 0, 0);
                reloadSurface();
            }
        });
        
        JButton defaultViewButton = new JButton("Default view");
        defaultViewButton.setPreferredSize(new Dimension(150, 30));
        defaultViewButton.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                surface.transform.rotation = new Vector3f(0.65f, -0.57f, 0);
                reloadSurface();
            }
        });
        
        JPanel viewPanel = new JPanel();
        viewPanel.setBorder(new EmptyBorder(10, 0, 0, 0));
        viewPanel.setLayout(new FlowLayout());
        viewPanel.add(topDownViewButton);
        viewPanel.add(defaultViewButton);

        JLabel optSliderLabel = new JLabel("Optimization:");
        optSliderLabel.setBorder(new EmptyBorder(0, 0, 0, 0));
        optSliderLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);

        JSlider optSlider = new JSlider(JSlider.HORIZONTAL, 1, 10, optimizeFactor);
        optSlider.setMajorTickSpacing(1);
        optSlider.setPaintTicks(true);
        optSlider.setPaintLabels(true);
        optSlider.setBorder(new EmptyBorder(15, 0, 20, 0));
        optSlider.setSize(new Dimension(100, 200));
        optSlider.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(MouseEvent e) {
                optimizeFactor = ((JSlider) e.getSource()).getValue();
                calcNormalSurface(img);
                reloadSurface();
            }
        });

        JPanel sidePanel = new JPanel();
        sidePanel.setLayout(new BoxLayout(sidePanel, BoxLayout.Y_AXIS));
        sidePanel.setBorder(new EmptyBorder(20, 10, 30, 0));
        sidePanel.setPreferredSize(new Dimension(350, 700));
        sidePanel.add(depthSliderLabel);
        sidePanel.add(depthSlider);
        sidePanel.add(button);
        sidePanel.add(filename);
        sidePanel.add(imgDisplay);
        sidePanel.add(viewPanel);
        sidePanel.add(optSliderLabel);
        sidePanel.add(optSlider);

        content.add(sidePanel, gbc);
        controlPanel.add(content);

        frame.setVisible(true);
    }

    public static void chooseImage(String path)
    {
        BufferedImage newImg = null;
        try 
        {
            newImg = ImageIO.read(new File(path));
        } catch (IOException e) {
            System.out.println(e);
        }

        img.flush();
        img = newImg;
        imgPath = path;
        filename.setText(path);
        imgDisplay.setIcon(new ImageIcon(img.getScaledInstance(NORMAL_IMAGE_SIZE, NORMAL_IMAGE_SIZE, java.awt.Image.SCALE_DEFAULT)));

        int newOpt = img.getHeight() / 1024;

        if (newOpt <= 1)
            optimizeFactor = 1;
        else if (newOpt > 10)
            optimizeFactor = 10;
        else 
            optimizeFactor = newOpt;

        calcNormalSurface(img);
        if (gljpanel != null)
            reloadSurface();
    }

    public static void calcNormalSurface(BufferedImage img)
    {
        float meshWidth = 0.5f;
        NormalMap normalMap = rgbToNormalMap(img, optimizeFactor);
        SurfaceMesh surfaceMesh = SurfaceMesh.generateXZSurface(normalMap.xdim(), normalMap.ydim(), -meshWidth, meshWidth, -meshWidth, meshWidth);
        float xRatio = meshWidth * 2 / normalMap.xdim(), yRatio = meshWidth * 2 / normalMap.ydim(); 
        
        FloatMap gradMapX = normalMap.calcGradientMapToX().mul(xRatio);
        FloatMap gradMapY = normalMap.calcGradientMapToY().mul(yRatio);
        FloatMap gradMap = FloatMap.sum(
            FloatMap.getReverseAccumulateAlongX(gradMapX).invert(), 
            gradMapX.accumulateAlongX(), 
            FloatMap.getReverseAccumulateAlongY(gradMapY).invert(), 
            gradMapY.accumulateAlongY()).mul(1f / 4);
        surface.mesh = surfaceMesh;
        surface.depthMap = gradMap.content;
    }

    public static void reloadSurface()
    {
        if (gljpanel.getSize().width == gljpanel.getPreferredSize().width)
            gljpanel.setSize(new Dimension(gljpanel.getPreferredSize().width - 1, gljpanel.getHeight() - 1));
        else
            gljpanel.setSize(gljpanel.getPreferredSize());
    }

    public static NormalMap rgbToNormalMap(BufferedImage img, int optimizeFactor)
    {
        final int 
            bHex = 0xff,
            gHex = 0xff00, 
            rHex = 0xff0000; 

        NormalMap map = new NormalMap(img.getHeight() / optimizeFactor, img.getWidth() / optimizeFactor);
        for (int y = 0; y < img.getHeight() / optimizeFactor; y++)
            for (int x = 0; x < img.getWidth() / optimizeFactor; x++)
            {
                int color = img.getRGB(x * optimizeFactor, y * optimizeFactor);
                int r = (color & rHex) >> 16;
                int g = (color & gHex) >> 8;
                int b = color & bHex;

                map.set(x, y, new Vector3f(r, g, b).div(255));
            }
        
        return map;
    }

    public static float[] genRandom(int size)
    {
        float[] depthMap = new float[size];
        Random rand = new Random();

        for (int i = 0; i < size; i++)
            depthMap[i] = 0.2f * (float)Math.abs(rand.nextGaussian());

        return depthMap;
    }
}
