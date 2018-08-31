import java.applet.Applet;
import java.awt.Dimension;
import java.awt.Graphics;

public class MineSweeperApplet extends Applet
{
    private MineSweeper c;
    
    public void init()
    {
        c = new MineSweeper();
        c.setPreferredSize(new Dimension(650,650));
        c.setVisible(true);
        c.setFocusable(true);
        this.add(c);
        this.setVisible(true);
        this.setSize(new Dimension(650, 650));
    }
    
    public void paint(Graphics g)
    {
        this.setSize(new Dimension(650, 650));
        
    }
    
}