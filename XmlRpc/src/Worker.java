import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.server.PropertyHandlerMapping;
import org.apache.xmlrpc.server.XmlRpcHandlerMapping;
import org.apache.xmlrpc.server.XmlRpcServerConfigImpl;
import org.apache.xmlrpc.server.XmlRpcStreamServer;
import org.apache.xmlrpc.webserver.WebServer;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Worker
{
    public void run()
    {
        try
        {
            ExecutorService executorService = Executors.newSingleThreadExecutor();
            executorService.execute(new WorkerProcessor());

        } catch (Exception e)
        {
            System.err.println(e);
        }
    }


    public class WorkerProcessor implements Runnable
    {


        @Override
        public void run()
        {
            try
            {
                WebServer server = new WebServer(6666);
                XmlRpcStreamServer xmlRpcServer = server.getXmlRpcServer();
                PropertyHandlerMapping propertyHandlerMapping = new PropertyHandlerMapping();

                propertyHandlerMapping.addHandler(ISolver.class.getName(), Solver.class);

                xmlRpcServer.setHandlerMapping(propertyHandlerMapping);

                XmlRpcServerConfigImpl serverConfig = (XmlRpcServerConfigImpl)
                        xmlRpcServer.getConfig();

                serverConfig.setEnabledForExtensions(true);
                serverConfig.setContentLengthOptional(false);
                server.start();
            } catch (Exception exception)
            {
                System.err.println("JavaServer: " + exception);
            }
        }
    }
}
