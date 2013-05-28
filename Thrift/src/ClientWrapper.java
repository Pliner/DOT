import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;
import sun.misc.Regexp;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ClientWrapper implements Runnable
{
    public final String regex = "\\b(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";

    public final Pattern uri = Pattern.compile(regex);

    @Override
    public void run()
    {
        TTransport transport;
        try
        {
            System.out.println("Waiting two second for starting");
            Thread.sleep(2000);
            transport = new TSocket("localhost", 6666);
            transport.open();

            TProtocol protocol = new TBinaryProtocol(transport);

            DownloadService.Client client = new DownloadService.Client(protocol);

            String content = client.download("http://www.google.ru");

            Matcher matcher = uri.matcher(content);
            int count = 0;
            while (matcher.find())
                count++;

            System.out.println("Url count = " + count);

            transport.close();
        } catch (TTransportException e)
        {
            e.printStackTrace();
        } catch (TException e)
        {
            e.printStackTrace();
        } catch (InterruptedException e)
        {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    public static void main(String[] args) {
        new ClientWrapper().run();
    }

}
