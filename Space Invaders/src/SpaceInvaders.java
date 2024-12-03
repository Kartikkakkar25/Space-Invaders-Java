import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;

public class SpaceInvaders extends JPanel implements ActionListener, KeyListener{
    class Block{
        int x;
        int y;
        int width;
        int height;
        Image img;
        boolean alive = true; // used for aliens
        boolean used = false; // used for bullets

        Block(int x, int y, int width, int height, Image img){
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.img = img;
        }

    }
    int tileSize = 32;
    int rows = 16;
    int columns = 16;
    int boardWidth = tileSize * columns;
    int boardHeight = tileSize * rows;

    Image shipImg;
    Image alienImg;
    Image alienCyanImg;
    Image alienMagentaImg;
    Image alienYellowImg;
    Image bgImage;
    ArrayList<Image> alienImgArray;

    //For Ship
    int shipWidth = tileSize*2;
    int shipHeight = tileSize;
    int shipX = tileSize*columns/2 - tileSize;
    int shipY = boardHeight - tileSize*2;
    int shipVelocityX = tileSize;
    Block ship;

    //For aliens
    ArrayList<Block> alienArray;
    int alienWidth = tileSize*2;
    int alienHeight = tileSize;
    int alienX = tileSize;
    int alienY = tileSize;

    int alienRows = 2;
    int alienColumns = 3;
    int alienCount = 0;
    int alienVelocityX = 1;

    ArrayList<Block> bulletArray;
    int bulletWidth = tileSize/8;
    int bulletHeight = tileSize/2;
    int bulletVelocityY = -10;

    Timer gameLoop;
    int score = 0;
    boolean gameover = false;

    SpaceInvaders(){
        setPreferredSize(new Dimension(boardWidth,boardHeight));
        setBackground(Color.black);
        setFocusable(true);
        addKeyListener(this);

        shipImg = new ImageIcon(getClass().getResource("./ship.png")).getImage();
        alienCyanImg = new ImageIcon(getClass().getResource("./alien-cyan.png")).getImage();
        alienImg = new ImageIcon(getClass().getResource("./alien.png")).getImage();
        alienYellowImg = new ImageIcon(getClass().getResource("./alien-yellow.png")).getImage();
        alienMagentaImg = new ImageIcon(getClass().getResource("./alien-magenta.png")).getImage();
        bgImage = new ImageIcon(getClass().getResource("./background.png")).getImage();

        alienImgArray = new ArrayList<Image>();
        alienImgArray.add(alienImg);
        alienImgArray.add(alienCyanImg);
        alienImgArray.add(alienMagentaImg);
        alienImgArray.add(alienYellowImg);
        

        ship = new Block(shipX, shipY, shipWidth, shipHeight, shipImg);
        alienArray = new ArrayList<Block>();
        bulletArray = new ArrayList<Block>();

        gameLoop = new Timer(1000/60, this);
        createAliens();
        gameLoop.start();
    }
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        draw(g);
    }
    public void draw(Graphics g){
        g.drawImage(bgImage, 0, 0, boardWidth, boardHeight, null);
        g.drawImage(ship.img, ship.x, ship.y, ship.width, ship.height, null);
        for(int i=0; i<alienArray.size(); i++){
            Block alien = alienArray.get(i);
            if(alien.alive){
                g.drawImage(alien.img, alien.x, alien.y, alien.width, alien.height,null);    
            }
        }
        g.setColor(Color.white);
        for(int i=0;i<bulletArray.size();i++){
            Block bullet = bulletArray.get(i);
            if(!bullet.used){
                // g.drawRect(bullet.x, bullet.y, bullet.width, bullet.height);
                g.fillRect(bullet.x, bullet.y, bullet.width, bullet.height);
            }
        }
        g.setColor(Color.white);
        g.setFont(new Font("Arial", Font.PLAIN, 32));
        if(gameover){
            g.drawString("Game Over: "+String.valueOf((int)score),10,35 );
            g.setFont(new Font("Arial", Font.PLAIN, 20));
            g.setColor(Color.red);
            g.drawString("Press any key to restart", 10, 55);

        }
        else{
            g.drawString(String.valueOf((int)score), 10, 35);
        }


    }

    public void move(){
        for(int i=0;i<alienArray.size();i++){
            Block alien = alienArray.get(i);
            if(alien.alive){
                alien.x += alienVelocityX;

                if(alien.x+alien.width >= boardWidth || alien.x<=0){
                    alienVelocityX *= -1;
                    alien.x += alienVelocityX*2;

                    for(int j=0;j<alienArray.size();j++){
                        alienArray.get(j).y += alienHeight;
                    }
                }
                if(alien.y>=ship.y){
                    gameover=true;

                }
            }
        }

        for(int i=0;i<bulletArray.size();i++){
            Block bullet = bulletArray.get(i);
            bullet.y += bulletVelocityY;

            for(int j=0;j<alienArray.size();j++){
                Block alien = alienArray.get(j);
                if(!bullet.used && alien.alive && detectCollision(bullet, alien)){
                    bullet.used = true;
                    alien.alive = false;
                    alienCount--;
                    score+=100;
                }
            }
        }
        while(bulletArray.size()>0 && (bulletArray.get(0).used||bulletArray.get(0).y<0)){
            bulletArray.remove(0);
        }
        if(alienCount ==0){
            score+=alienColumns*alienRows * 100;
            alienColumns = Math.min(alienColumns + 1, columns/2 - 2);
            alienRows = Math.min(alienRows +1, rows-6);
            alienArray.clear();
            bulletArray.clear();
            // alienVelocityX = 1;
            createAliens();
        }
    }

    public void createAliens(){
        Random random = new Random();
        for(int r=0;r<alienRows;r++){
            for(int c=0;c<alienColumns;c++){
                int randomImgIndex = random.nextInt(alienImgArray.size());
                Block alien = new Block(
                    alienX + c*alienWidth,
                    alienY + r*alienHeight,
                    alienWidth, alienHeight, alienImgArray.get(randomImgIndex)
                );
                alienArray.add(alien);
            }
        }
        alienCount = alienArray.size();
    }
    public boolean detectCollision(Block a, Block b){
        return a.x < b.x + b.width &&
        a.x + a.width > b.x &&
        a.y < b.y + b.height &&
        a.y + a.height > b.y; 
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        move();
        repaint(); //Built-in to call draw() again
        if(gameover){
            gameLoop.stop();
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if(gameover){
            ship.x = shipX;
            alienArray.clear();
            bulletArray.clear();
            score = 0;
            alienVelocityX = 1;
            alienColumns = 3;
            alienRows = 2;
            gameover = false;
            createAliens();
            gameLoop.start();
        }
        else if(e.getKeyCode() == KeyEvent.VK_LEFT && ship.x - shipVelocityX >= 0){
            ship.x -= shipVelocityX;
        }
        else if(e.getKeyCode() == KeyEvent.VK_RIGHT && ship.x + ship.width + shipVelocityX <= boardWidth){
            ship.x += shipVelocityX;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if(e.getKeyCode() == KeyEvent.VK_SPACE){
            Block bullet = new Block(ship.x+shipWidth*15/32, ship.y, bulletWidth, bulletHeight, null);
            bulletArray.add(bullet);
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {}
}
