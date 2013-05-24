import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
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
                ServerSocket ss = new ServerSocket(6666);
                Socket socket = ss.accept();
                InputStream sin = socket.getInputStream();
                OutputStream sout = socket.getOutputStream();

                DataInputStream in = new DataInputStream(sin);
                DataOutputStream out = new DataOutputStream(sout);

                while (true)
                {
                    int a = in.readInt();
                    int b = in.readInt();
                    System.out.println(String.format("[x] Worker received %s + %s", a, b));
                    out.writeInt(a + b);
                    out.flush();
                    System.out.println(String.format("[x] Worker sent %s ", a + b));
                }
            } catch (Exception x)
            {
                x.printStackTrace();
            }
        }
    }
}
