package Kirilenko_lab45;
import java.awt.BorderLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
@SuppressWarnings("serial")

public class MainFrame extends JFrame {
    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;
    private JFileChooser fileChooser = null;
    private JCheckBoxMenuItem showAxisMenuItem;
    private JCheckBoxMenuItem showMarkersMenuItem;
    private JCheckBoxMenuItem showAreaMenuItem;
    private JCheckBoxMenuItem TurnMenuItem;
    private GraphicsDisplay display = new GraphicsDisplay();
    private boolean fileLoaded = false;
    private Action RestAction;
    private Action SaveAction;
    public MainFrame() {
        super("Построение графиков функций на основе заранее подготовленных файлов");
        setSize(WIDTH, HEIGHT);
        Toolkit kit = Toolkit.getDefaultToolkit();
        setLocation((kit.getScreenSize().width - WIDTH)/2,(kit.getScreenSize().height - HEIGHT)/2);
        setExtendedState(MAXIMIZED_BOTH);
        JMenuBar menuBar = new JMenuBar();
        setJMenuBar(menuBar);
        JMenu fileMenu = new JMenu("Файл");
        menuBar.add(fileMenu);
        Action openGraphicsAction = new AbstractAction("Открыть файл с графиком") 
        {
        public void actionPerformed(ActionEvent event) {
            if (fileChooser==null) {
                fileChooser = new JFileChooser();
                fileChooser.setCurrentDirectory(new File("."));
            }
            if (fileChooser.showOpenDialog(MainFrame.this) == JFileChooser.APPROVE_OPTION)
                openGraphics(fileChooser.getSelectedFile());
        }
        };
        fileMenu.add(openGraphicsAction);
        RestAction = new AbstractAction("Очистить") {
            public void actionPerformed(ActionEvent event) 
            {
                display.reset();
            }
        };
        fileMenu.add(RestAction);
        RestAction.setEnabled(false);
        SaveAction = new AbstractAction("Сохранить") {
            public void actionPerformed(ActionEvent event) {
                if (fileChooser == null) {
                    fileChooser = new JFileChooser();
                    fileChooser.setCurrentDirectory(new File("."));
                }
                if (fileChooser.showSaveDialog(MainFrame.this) ==
                        JFileChooser.APPROVE_OPTION) ;
                SaveFile(fileChooser.getSelectedFile());
            }
        };
        fileMenu.add(SaveAction);
        SaveAction.setEnabled(false);
        JMenu graphicsMenu = new JMenu("График");
        menuBar.add(graphicsMenu);
        Action showAxisAction = new AbstractAction("Показать оси координат") {
            public void actionPerformed(ActionEvent event) {
                display.setShowAxis(showAxisMenuItem.isSelected());
            }
        };
        showAxisMenuItem = new JCheckBoxMenuItem(showAxisAction);
        graphicsMenu.add(showAxisMenuItem);
        showAxisMenuItem.setSelected(true);
        Action showAreaAction = new AbstractAction("Показать площадь") {
            public void actionPerformed(ActionEvent event) {
                display.setShowArea(showAreaMenuItem.isSelected());
            }
        };
        showAreaMenuItem = new JCheckBoxMenuItem(showAreaAction);
        graphicsMenu.add(showAreaMenuItem);
        showAreaMenuItem.setSelected(false);
        showAreaMenuItem.setEnabled(false);
        Action RotateAction = new AbstractAction("Повернуть влево на 90 градусов") {
            public void actionPerformed(ActionEvent event) {
                display.setTurn(TurnMenuItem.isSelected());
            }
        };
        TurnMenuItem = new JCheckBoxMenuItem(RotateAction);
        graphicsMenu.add(TurnMenuItem);
        TurnMenuItem.setSelected(false);
        TurnMenuItem.setEnabled(false);
        
        Action showMarkersAction = new AbstractAction("Показать маркеры точек") {
            public void actionPerformed(ActionEvent event) {
                display.setShowMarkers(showMarkersMenuItem.isSelected());
            }
        };
        showMarkersMenuItem = new JCheckBoxMenuItem(showMarkersAction);
        graphicsMenu.add(showMarkersMenuItem);
        showMarkersMenuItem.setSelected(true);
        graphicsMenu.addMenuListener(new GraphicsMenuListener());
        getContentPane().add(display, BorderLayout.CENTER);
        }
    protected void openGraphics(File selectedFile) {
    	try (FileReader fr = new FileReader(selectedFile);
    	        BufferedReader br = new BufferedReader(fr))
    	{
    		int j=0;
    	    String line = br.readLine();
    	    String[] numbersStrings = line.split(",");
    	    Double [][] graphicsData = new Double[numbersStrings.length/2] [];
    	    Double[][] originalData = new Double[numbersStrings.length/2] [];
    	    double[] numbers = new double[numbersStrings.length];
    	    for (int i = 0; i < numbersStrings.length; i++)
    	    {
    	    	Double x = Double.parseDouble(numbersStrings[i]);
    	    	numbers[i] = Double.parseDouble(numbersStrings[i]);
    	        i++;
    	        Double y = Double.parseDouble(numbersStrings[i]);
    	        numbers[i] = Double.parseDouble(numbersStrings[i]);
    	        graphicsData[j] = new Double [] {x,y};
    	        originalData[j++] = new Double[]{x, y};
    	    }
    	    System.out.println(Arrays.toString( numbers));
    	    if (graphicsData!=null && graphicsData.length>0) {
    	    	fileLoaded = true;
    	    	display.showGraphics(graphicsData,originalData);
    	    	}
        } catch (FileNotFoundException ex) {
        JOptionPane.showMessageDialog(MainFrame.this, "Указанный файл не найден","Ошибка загрузки данных",JOptionPane.WARNING_MESSAGE);
            RestAction.setEnabled(false);
            showAreaMenuItem.setEnabled(false);
            TurnMenuItem.setEnabled(false);
            SaveAction.setEnabled(false);
            return;
        } catch (IOException ex) {
        JOptionPane.showMessageDialog(MainFrame.this, "Ошибка чтения координат точек из файла", "Ошибка загрузки данных",JOptionPane.WARNING_MESSAGE);
            RestAction.setEnabled(false);
            showAreaMenuItem.setEnabled(false);
            TurnMenuItem.setEnabled(false);
            SaveAction.setEnabled(false);
            return;
        }
        showAreaMenuItem.setEnabled(true);
        TurnMenuItem.setEnabled(true);
        RestAction.setEnabled(true);
        SaveAction.setEnabled(true);
    }
    private void SaveFile(File selectedFile){
        try {
            DataOutputStream out = new DataOutputStream(new FileOutputStream(selectedFile));
            Double[][] data = display.getGraphicsData();
            for (int i = 0; i < data.length; i++) {
                out.writeDouble((Double) data[i][0]);
                out.writeDouble((Double) data[i][1]);
            }
            out.close();
        } catch (Exception e) {}
    }
public static void main(String[] args) {
        MainFrame frame = new MainFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        }

private class GraphicsMenuListener implements MenuListener {
    public void menuSelected(MenuEvent e) {
        showAxisMenuItem.setEnabled(fileLoaded);
        showMarkersMenuItem.setEnabled(fileLoaded);
    }

    public void menuDeselected(MenuEvent e) {

    }

    public void menuCanceled(MenuEvent e) {

    }

}

}
