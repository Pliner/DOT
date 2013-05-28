import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TThreadPoolServer;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TTransportException;

public class ServiceWrapper implements Runnable
{

    @Override
    public void run()
    {
        try
        {
            TServerSocket serverTransport = new TServerSocket(6666);

            DownloadServiceHandler handler = new DownloadServiceHandler();
            DownloadService.Processor<DownloadService.Iface> processor
                    = new DownloadService.Processor<DownloadService.Iface>(handler);

            TServer server = new TThreadPoolServer(new TThreadPoolServer.Args(serverTransport).processor(processor));

            server.serve();
        } catch (TTransportException e)
        {
            e.printStackTrace();

        }


    }

    public static void main(String[] args)
    {
        new ServiceWrapper().run();
    }
}
