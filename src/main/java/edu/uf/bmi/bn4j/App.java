package edu.uf.bmi.bn4j;

import java.io.IOException;
/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( final String[] args ) throws IOException
    {
        Bn4jStarter b = new Bn4jStarter();
        b.createDb();
        b.removeData();
        b.shutDown();
    }
}
