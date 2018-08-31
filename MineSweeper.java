import java.awt.Graphics;
import java.awt.Image;
import java.awt.Dimension;
import java.awt.Canvas;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.awt.Toolkit;
import java.awt.Dimension;
import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;
import javax.swing.SwingUtilities;
import javax.swing.JOptionPane;

public class MineSweeper extends Canvas implements Runnable,  MouseListener
{
    private int GRID_WIDTH = 20;
    private int GRID_HEIGHT = 20;
    private int BOX_WIDTH = 30;
    private int BOX_HEIGHT = 30;
    private int NO_OF_MINES = 80;
    private Thread runThread;
    private Image tileImage;
    private int[][] isExposed = new int[GRID_WIDTH][GRID_HEIGHT];
    private int[][] land = new int[GRID_WIDTH][GRID_HEIGHT];
    private int No_Of_Flags_Left = 40;
    
    public void init()
    {
        this.addMouseListener(this);
    }
    
    public void randomMineFill()
    {
        //plants mines at random spots
        int randomX, randomY;
        for(int i = 1; i <= NO_OF_MINES; i++ )
        {
            randomX = (int)(Math.random() * (GRID_WIDTH - 0.1));
            randomY = (int)(Math.random() * (GRID_HEIGHT - 0.1));
            if(land[randomX][randomY] != TileDescription.LANDMINE_PRESENT)
            land[randomX][randomY] = 10;
            else if(land[randomX][randomY] == TileDescription.LANDMINE_PRESENT)
            i--;
        }
    }
    public void placeNumbers()
    {
        //places number tiles or clues around land mines
        for(int i = 0; i < GRID_WIDTH; i++)
        {
            for(int j = 0; j < GRID_HEIGHT; j++)
            {
                if(land[i][j] != TileDescription.LANDMINE_PRESENT)
                land[i][j] = NoOfMinesAround(i,j);
            }
        }
    }
    public int NoOfMinesAround(int a, int b)
    {
        int c = 0;
        for(int i = a-1; i <= a+1; i++)
        {
            for(int j = b-1; j <= b+1; j++)
            {
                try{
                    if(i >= 0 && i < GRID_WIDTH && j >= 0 && j < GRID_HEIGHT)
                    {
                        if(land[i][j]  == TileDescription.LANDMINE_PRESENT)
                        c++;
                    }
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }
            }
        }
        return c;
    }
    public void paint(Graphics g)
    {
 
        DrawGame(g);
        if(runThread == null)
        {
            this.setPreferredSize(new Dimension(650, 650));
            this.addMouseListener(this);
            generateMineLand();
            exposeEmptySpaceUntilNumbersArrive(generateRandomEmptyTileLocation()[0],generateRandomEmptyTileLocation()[1]);
            runThread = new Thread(this);
            runThread.start();
        }
    }
    public void generateMineLand()
    {
       coverAll();
       clearLand();
       randomMineFill();
       placeNumbers();
    }
    public void clearLand()
    {
        for(int i = 0; i < GRID_WIDTH; i++)
        {
            for(int j = 0; j < GRID_HEIGHT; j++)
            {
                land[i][j] = 0;
            }
        }
    }
    public void coverAll()
    {
        for(int i = 0; i < GRID_WIDTH; i++)
        {
            for(int j = 0; j < GRID_HEIGHT; j++)
            {
                isExposed[i][j] = TileDescription.COVERED;
            }
        }
    }
    public void exposeAllMines()
    {
        for(int i = 0; i < GRID_WIDTH; i++)
        {
            for(int j = 0; j < GRID_HEIGHT; j++)
            {
                if(land[i][j] == TileDescription.LANDMINE_PRESENT)
                {
                    isExposed[i][j] = TileDescription.EXPOSED;
                }
            }
        }
    }
    public void exposeEmptySpaceUntilNumbersArrive(int a, int b)
    {   
        int cont = 0;
        int k = 1;
        do
        {
            for(int i = a-k; i <= a+k; i++)
            {
                for(int j = b-k; j <= b+k; j++)
                {
                    try{
                        if(i >= 0 && i < GRID_WIDTH && j >= 0 && j < GRID_HEIGHT)
                        {
                            if(land[i][j]  == TileDescription.NO_MINES_AROUND)
                            isExposed[i][j] = TileDescription.EXPOSED;
                            else if(land[i][j] != TileDescription.LANDMINE_PRESENT)
                            {
                                isExposed[i][j] = TileDescription.EXPOSED;
                                cont++;
                            }
                        }
                    }
                    catch(Exception e)
                    {
                        e.printStackTrace();
                    }
                }   
            }
            k++;
        }
        while(cont < 2);
    }
    public int[] generateRandomEmptyTileLocation()
    {
        int randomX = (int)(Math.random() * (GRID_WIDTH - 0.1));
        int randomY = (int)(Math.random() * (GRID_HEIGHT - 0.1));
        int[] Coord = new int[2];
        while(land[randomX][randomY] != TileDescription.NO_MINES_AROUND)
        {
            randomX = (int)(Math.random() * (GRID_WIDTH - 0.1));
            randomY = (int)(Math.random() * (GRID_HEIGHT - 0.1));
        }
        Coord[0] = randomX;
        Coord[1] = randomY;
        return Coord;
    }

    public void LostGame()
    {
        exposeAllMines();
        String response = JOptionPane.showInputDialog("You stepped on a mine! Wanna play again? Type \'YES\' or \'NO\'");
        if(response.equalsIgnoreCase("yes"))
        generateMineLand();
        exposeEmptySpaceUntilNumbersArrive(generateRandomEmptyTileLocation()[0],generateRandomEmptyTileLocation()[1]);
        
    }
    public boolean ScanningResult()
    { 
        int instances = 0;
        for(int i = 0; i < GRID_WIDTH; i++)
        {
            for(int j = 0; j < GRID_HEIGHT; j++)
            {
                if((land[i][j] == TileDescription.LANDMINE_PRESENT && isExposed[i][j] == TileDescription.FLAGGED))
                {
                    instances++;
                    
            }
            
           }
        }
        if(instances == NO_OF_MINES)
        return true;
        else
        return false;
    }
    public void WonGame()
    {
        if(ScanningResult())
        {
            String response = JOptionPane.showInputDialog("Congratulations! You've detected all mines! Wanna Play Again? Type \'Yes\' if Yes");
            if(response.equalsIgnoreCase("Yes"))
            generateMineLand();
        }
    }
    public void update(Graphics g)
    {
        paint(g);
        
    }
    public void DrawGame(Graphics g)
    {
        BufferedImage buffer = new BufferedImage(BOX_WIDTH * GRID_WIDTH, BOX_HEIGHT * GRID_HEIGHT, BufferedImage.TYPE_INT_ARGB);
        Graphics bg = buffer.getGraphics();
        for(int i=0; i<GRID_WIDTH; i++)
        {
            for(int j=0; j<GRID_HEIGHT; j++)
            {
                if(isExposed[i][j] == TileDescription.COVERED)
                {
                    URL ImagePath = MineSweeper.class.getResource("CoveredLand.png");
                    this.tileImage = Toolkit.getDefaultToolkit().getImage(ImagePath);
                    bg.drawImage(tileImage, BOX_WIDTH*i, BOX_HEIGHT*j, BOX_WIDTH*i + BOX_WIDTH, BOX_HEIGHT*j + BOX_HEIGHT ,0, 0, BOX_WIDTH, BOX_HEIGHT, this);
                }
                else if(isExposed[i][j] == TileDescription.FLAGGED)
                    {
                        URL ImagePath = MineSweeper.class.getResource("CoveredLandFlagged.png");
                        this.tileImage = Toolkit.getDefaultToolkit().getImage(ImagePath);
                        bg.drawImage(tileImage, BOX_WIDTH*i, BOX_HEIGHT*j, BOX_WIDTH*i + BOX_WIDTH, BOX_HEIGHT*j + BOX_HEIGHT, 0, 0, BOX_WIDTH, BOX_HEIGHT, this);
                    }
                else if(isExposed[i][j] == TileDescription.EXPOSED)
                {
                    if(land[i][j] == TileDescription.NO_MINES_AROUND)
                    {
                        URL ImagePath = MineSweeper.class.getResource("ExposedLand" + "0" + ".png");
                        this.tileImage = Toolkit.getDefaultToolkit().getImage(ImagePath);
                        bg.drawImage(tileImage, BOX_WIDTH*i, BOX_HEIGHT*j, BOX_WIDTH*i + BOX_WIDTH, BOX_HEIGHT*j + BOX_HEIGHT, 0, 0, BOX_WIDTH, BOX_HEIGHT, this);
                    }
                    else if(land[i][j] == TileDescription.ONE_MINE_AROUND)
                    {
                        URL ImagePath = MineSweeper.class.getResource("EXposedLand1.png");
                        this.tileImage = Toolkit.getDefaultToolkit().getImage(ImagePath);
                        bg.drawImage(tileImage, BOX_WIDTH*i, BOX_HEIGHT*j, BOX_WIDTH*i + BOX_WIDTH, BOX_HEIGHT*j + BOX_HEIGHT, 0, 0, BOX_WIDTH, BOX_HEIGHT, this);
                    }
                    else if(land[i][j] == TileDescription.TWO_MINES_AROUND)
                    {
                        URL ImagePath = MineSweeper.class.getResource("ExposedLand2.png");
                        this.tileImage = Toolkit.getDefaultToolkit().getImage(ImagePath);
                        bg.drawImage(tileImage, BOX_WIDTH*i, BOX_HEIGHT*j, BOX_WIDTH*i + BOX_WIDTH, BOX_HEIGHT*j + BOX_HEIGHT, 0, 0, BOX_WIDTH, BOX_HEIGHT, this);
                    }
                    else if(land[i][j] == TileDescription.THREE_MINES_AROUND)
                    {
                        URL ImagePath = MineSweeper.class.getResource("ExposedLand3.png");
                        this.tileImage = Toolkit.getDefaultToolkit().getImage(ImagePath);
                        bg.drawImage(tileImage, BOX_WIDTH*i, BOX_HEIGHT*j, BOX_WIDTH*i + BOX_WIDTH, BOX_HEIGHT*j + BOX_HEIGHT, 0, 0, BOX_WIDTH, BOX_HEIGHT, this);
                    }
                    else if(land[i][j] == TileDescription.FOUR_MINES_AROUND)
                    {
                        URL ImagePath = MineSweeper.class.getResource("ExposedLand4.png");
                        this.tileImage = Toolkit.getDefaultToolkit().getImage(ImagePath);
                        bg.drawImage(tileImage, BOX_WIDTH*i, BOX_HEIGHT*j, BOX_WIDTH*i + BOX_WIDTH, BOX_HEIGHT*j + BOX_HEIGHT, 0, 0, BOX_WIDTH, BOX_HEIGHT, this);
                    }
                    else if(land[i][j] == TileDescription.FIVE_MINES_AROUND)
                    {
                        URL ImagePath = MineSweeper.class.getResource("ExposedLand5.png");
                        this.tileImage = Toolkit.getDefaultToolkit().getImage(ImagePath);
                        bg.drawImage(tileImage, BOX_WIDTH*i, BOX_HEIGHT*j, BOX_WIDTH*i + BOX_WIDTH, BOX_HEIGHT*j + BOX_HEIGHT, 0, 0, BOX_WIDTH, BOX_HEIGHT, this);
                    }
                    else if(land[i][j] == TileDescription.SIX_MINES_AROUND)
                    {
                        URL ImagePath = MineSweeper.class.getResource("ExposedLand6.png");
                        this.tileImage = Toolkit.getDefaultToolkit().getImage(ImagePath);
                        bg.drawImage(tileImage, BOX_WIDTH*i, BOX_HEIGHT*j, BOX_WIDTH*i + BOX_WIDTH, BOX_HEIGHT*j + BOX_HEIGHT, 0, 0, BOX_WIDTH, BOX_HEIGHT, this);
                    }
                    else if(land[i][j] == TileDescription.SEVEN_MINES_AROUND)
                    {
                        URL ImagePath = MineSweeper.class.getResource("ExposedLand7.png");
                        this.tileImage = Toolkit.getDefaultToolkit().getImage(ImagePath);
                        bg.drawImage(tileImage, BOX_WIDTH*i, BOX_HEIGHT*j, BOX_WIDTH*i + BOX_WIDTH, BOX_HEIGHT*j + BOX_HEIGHT, 0, 0, BOX_WIDTH, BOX_HEIGHT, this);
                    }
                    else if(land[i][j] == TileDescription.EIGHT_MINES_AROUND)
                    {
                        URL ImagePath = MineSweeper.class.getResource("ExposedLand8.png");
                        this.tileImage = Toolkit.getDefaultToolkit().getImage(ImagePath);
                        bg.drawImage(tileImage, BOX_WIDTH*i, BOX_HEIGHT*j, BOX_WIDTH*i + BOX_WIDTH, BOX_HEIGHT*j + BOX_HEIGHT, 0, 0, BOX_WIDTH, BOX_HEIGHT, this);
                    }
                     else if(land[i][j] == TileDescription.LANDMINE_PRESENT)
                    {
                        URL ImagePath = MineSweeper.class.getResource("MineExploded.png");
                        this.tileImage = Toolkit.getDefaultToolkit().getImage(ImagePath);
                        bg.drawImage(tileImage, BOX_WIDTH*i, BOX_HEIGHT*j, BOX_WIDTH*i + BOX_WIDTH, BOX_HEIGHT*j + BOX_HEIGHT, 0, 0, BOX_WIDTH, BOX_HEIGHT, this);
                    }
                }
            }
           }
           //flip
           g.drawImage(buffer, 0, 0, BOX_WIDTH * GRID_WIDTH, BOX_HEIGHT * GRID_HEIGHT, this);
        }
        public void run()
        {
            while(true)
            {
                //runs indefinitely
                repaint();
                WonGame();
                try
                {
                    Thread.currentThread();
                    Thread.sleep(50);
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                    
                }
                
            
            }
        }
        public void mousePressed(MouseEvent e)
        {
        }
        public void mouseReleased(MouseEvent e)
        {
        }
        public void mouseEntered(MouseEvent e)
        {
        }
        public void mouseExited(MouseEvent e)
        {
        }

        public void mouseClicked(MouseEvent e)
        {
                
                if(SwingUtilities.isLeftMouseButton(e))
                {
                   if(isExposed[e.getX()/BOX_WIDTH][e.getY()/BOX_HEIGHT] != TileDescription.FLAGGED)
                   {
                       isExposed[e.getX()/BOX_WIDTH][e.getY()/BOX_HEIGHT] = TileDescription.EXPOSED;
                       No_Of_Flags_Left++;
                       if(land[e.getX()/BOX_WIDTH][e.getY()/BOX_HEIGHT] == TileDescription.NO_MINES_AROUND)
                       exposeEmptySpaceUntilNumbersArrive(e.getX()/BOX_WIDTH, e.getY()/BOX_HEIGHT);

                    }
                   
                   if(land[e.getX()/BOX_WIDTH][e.getY()/BOX_HEIGHT] == TileDescription.LANDMINE_PRESENT)
                   {
                       LostGame();
                    }
                  
                }
                    
                else if(SwingUtilities.isRightMouseButton(e))
                {
                                      
                    if(isExposed[e.getX()/BOX_WIDTH][e.getY()/BOX_HEIGHT] == TileDescription.COVERED)
                    {
                        isExposed[e.getX()/BOX_WIDTH][e.getY()/BOX_HEIGHT] = TileDescription.FLAGGED;
                        No_Of_Flags_Left--; 
                    }
                    else if(isExposed[e.getX()/BOX_WIDTH][e.getY()/BOX_HEIGHT] == TileDescription.FLAGGED)
                    {
                        isExposed[e.getX()/BOX_WIDTH][e.getY()/BOX_HEIGHT] = TileDescription.COVERED;
                        No_Of_Flags_Left++;
                    }  
                }
            }
}

                    