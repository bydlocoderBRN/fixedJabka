import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Rectangle2D;
import java.io.File;

public class FractalExplorer {
    private int size_fractal;
    private JImageDisplay content;
    private FractalGenerator fractalGenerator;
    private Rectangle2D.Double rect;
    private JFrame frame;
    private JButton buttonReset;
    private JComboBox<Object> combo = new JComboBox<Object>();
    private FractalGenerator tricorn;
    private FractalGenerator burningShip;
    private FractalGenerator mandelbrot;
    private JPanel paneltop;
    private JPanel panelbot;
    private JLabel lbl1;
    private JButton buttonSave;
    private JFileChooser saver = new JFileChooser();


    public FractalExplorer(int size){
        size_fractal=size;
        mandelbrot = new Mandelbrot();
        fractalGenerator = mandelbrot;
        tricorn = new Tricorn();
        burningShip = new BurningShip();
        content = new JImageDisplay(size_fractal,size_fractal);
        rect = new Rectangle2D.Double();
        fractalGenerator.getInitialRange(rect);


    }
    public void createAndShowGUI(){
        content.setLayout(new BorderLayout());
        frame = new JFrame("Фрактал");
        buttonReset = new JButton("Сброс");
        buttonSave = new JButton("Сохранить");
        ActionListener resetlistener = new ResetActionListener();
        buttonReset.addActionListener(resetlistener);
        buttonSave.addActionListener(resetlistener);
        MouseListener click = new MouseZoomListener();
        content.addMouseListener(click);
        combo.addItem(mandelbrot);
        combo.addItem(tricorn);
        combo.addItem(burningShip);
        combo.addActionListener(resetlistener);
        lbl1 = new JLabel("Выберите фрактал:");
        paneltop = new JPanel();
        panelbot = new JPanel();
        paneltop.add(lbl1);
        paneltop.add(combo);
        panelbot.add(buttonReset);
        panelbot.add(buttonSave);
        frame.add(paneltop, BorderLayout.NORTH);
        frame.add(content, BorderLayout.CENTER);
        frame.add(panelbot,BorderLayout.SOUTH);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.pack ();
        frame.setVisible (true);
        frame.setResizable (false);


    }
    private void drawFractal (){
        double xCoord;
        double yCoord;
        float hue;
        int rgbColor;
        for (int x= 0; x <size_fractal; x++){
            for (int y = 0; y < size_fractal; y++){
                xCoord = FractalGenerator.getCoord (rect.x, rect.x + rect.width,
                        size_fractal, x);
                yCoord = FractalGenerator.getCoord (rect.y, rect.y + rect.height,
                        size_fractal, y);
                if (fractalGenerator.numIterations(xCoord,yCoord) == -1)
                    content.drawPixel(x,y,0);
                else {
                    hue = 0.7f + (float) fractalGenerator.numIterations(xCoord,yCoord) / 200f;
                    rgbColor = Color.HSBtoRGB(hue, 1f, 1f);
                    content.drawPixel(x,y,rgbColor);

                };
            }
        }
        content.repaint();

    }
    class ResetActionListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            if (e.getSource() == buttonReset){
                fractalGenerator.getInitialRange(rect);
                drawFractal();
            }
            if (e.getSource() == combo){
                if (combo.getSelectedItem() == tricorn){
                    fractalGenerator = tricorn;
                    fractalGenerator.getInitialRange(rect);
                    drawFractal();
                }
                if (combo.getSelectedItem() == burningShip){
                    fractalGenerator = burningShip;
                    fractalGenerator.getInitialRange(rect);
                    drawFractal();
                }
                if (combo.getSelectedItem() == mandelbrot){
                    fractalGenerator = mandelbrot;
                    fractalGenerator.getInitialRange(rect);
                    drawFractal();
                }

                    }
            if (e.getSource() == buttonSave){
                FileFilter filter = new FileNameExtensionFilter("PNG Images", "png");
                saver.setFileFilter(filter);
                saver.setAcceptAllFileFilterUsed(false);
                if (saver.showSaveDialog(frame)== JFileChooser.APPROVE_OPTION){
                    File pathh = saver.getSelectedFile();
                    File path = new File(pathh.getPath() + ".png");
                    try {
                        ImageIO.write(content.getImage(), "png", path);
                    }
                    catch (Exception exception) {
                        JOptionPane.showMessageDialog(content, exception.getMessage(), "Cannot Save Image", JOptionPane.ERROR_MESSAGE);
                    }

                }
            }

        }
    }
    class MouseZoomListener extends MouseAdapter{
        @Override
        public void mouseClicked(MouseEvent e){
            double xCoord = FractalGenerator.getCoord (rect.x, rect.x + rect.width,
                    size_fractal, e.getX());
            double yCoord = FractalGenerator.getCoord (rect.y, rect.y + rect.height,
                    size_fractal, e.getY());
            fractalGenerator.recenterAndZoomRange(rect,xCoord,yCoord,0.5);
            drawFractal();
        }
    }


    public static void main(String[] args){
        FractalExplorer fractal = new FractalExplorer(800);
        fractal.createAndShowGUI();
        fractal.drawFractal();

    }
}