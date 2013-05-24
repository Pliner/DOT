import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Random;
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

            try {
                InetAddress ipAddress = InetAddress.getByName("127.0.0.1");
                Socket socket = new Socket(ipAddress, 6666);

                InputStream sin = socket.getInputStream();
                OutputStream sout = socket.getOutputStream();

                DataInputStream in = new DataInputStream(sin);
                DataOutputStream out = new DataOutputStream(sout);


                while (true) {
                    int a = random.nextInt();
                    int b = random.nextInt();
                    out.writeInt(a);
                    out.writeInt(b);
                    out.flush();
                    System.out.println(String.format("[x] Master sent %s + %s", a, b));
                    int c = in.readInt();
                    System.out.println(String.format("[x] Master received %s + %s = %s", a, b, c));
                    Thread.sleep(5000);
                }
            } catch (Exception x) {
                x.printStackTrace();
            }
        }
    }

}
