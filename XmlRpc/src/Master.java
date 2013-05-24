import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;
import org.apache.xmlrpc.client.util.ClientFactory;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URL;
import java.util.Random;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class Master
{


    public void run()
    {
        try
        {
            ExecutorService executorService = Executors.newSingleThreadExecutor();
            executorService.execute(new MasterSender());

        } catch (Exception e)
        {
            System.err.println(e);
        }
    }

    public class MasterSender implements Runnable
    {
        private Random random = new Random();

        @Override
        public void run()
        {
            try
            {
                XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
                config.setServerURL(new URL("http://127.0.0.1:8080/solve"));
                config.setEnabledForExtensions(true);
                config.setConnectionTimeout(60 * 1000);
                config.setReplyTimeout(60 * 1000);
                XmlRpcClient client = new XmlRpcClient();
                client.setConfig(config);
                ClientFactory factory = new ClientFactory(client);
                ISolver solver = (ISolver) factory.newInstance(ISolver.class);
                while (true)
                {

                    int a = random.nextInt(100);
                    int b = random.nextInt(100);
                    int c = random.nextInt(100);
                    System.out.println(String.format("[x] Master sent a=%s b=%s c=%s", a, b, c));
                    Solution solution = solver.solve(a, b, c);
                    if (solution == null)
                    {
                        System.out.println(String.format("[x] Master received no solution for a=%s b=%s c=%s", a, b, c));
                    } else
                    {
                        System.out.println(String.format("[x] Master received x1=%s x2=%s for a=%s b=%s c=%s", solution.x1, solution.x2, a, b, c));
                    }
                }

            } catch (Exception exception)
            {
                exception.printStackTrace();
            }

        }
    }

}
