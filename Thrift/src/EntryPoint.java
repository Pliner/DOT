import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EntryPoint
{
    public static void main(String [] args) {


        BasicConfigurator.configure();
        Logger.getRootLogger().setLevel(Level.OFF);

        ExecutorService serviceThreadPool = Executors.newSingleThreadExecutor();

        serviceThreadPool.execute(new ServiceWrapper());


        ExecutorService clientThreadPool = Executors.newSingleThreadExecutor();
        clientThreadPool.execute(new ClientWrapper());
    }
}
