package nju.java.creatures;

import nju.java.creatures.Good.Grandpa;
import org.junit.Test;

import javax.swing.*;

import java.net.URL;

/**
 * Created by cbcwestwolf on 2018/1/3.
 */
public class GrandpaTest {
    @Test
    public void testString() throws Exception {
        Grandpa grandpa = new Grandpa(0,0,null);
        assert(grandpa.toString().equals("爷爷"));
    }

    @Test
    public void testDead() throws Exception{
        Grandpa grandpa = new Grandpa(0,0,null);
        assert( ! grandpa.isDead());
        grandpa.setBlood(grandpa.getBlood()-100);
        assert( grandpa.isDead());
    }

    @Test
    public void testLocation() throws Exception{
        Grandpa grandpa = new Grandpa(0,0,null);
        assert( grandpa.getX() == 0);
        assert( grandpa.getY() == 0);
        assert( grandpa.getPower() == 10 );
        grandpa.setX(grandpa.getX() + 5 );
        assert( grandpa.getX() == 5 );
    }

    @Test
    public void testImage() throws Exception{
        Grandpa grandpa = new Grandpa(0,0,null);
        URL url = this.getClass().getClassLoader().getResource("爷爷.png");
        ImageIcon imageIcon = new ImageIcon(url);
        assert (grandpa.getImage() == null);
        grandpa.setImage(imageIcon.getImage());
        assert( grandpa.getImage() == imageIcon.getImage() );
    }

}