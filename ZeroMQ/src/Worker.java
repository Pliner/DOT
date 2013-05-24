import com.google.gson.Gson;
import org.jeromq.ZMQ;

public class Worker
{
    private Gson gson = new Gson();

    public void run()
    {

        ZMQ.Context context = ZMQ.context(1);

        //  Socket to receive messages on
        ZMQ.Socket receiver = context.socket(ZMQ.SUB);
        receiver.connect("tcp://localhost:5557");

        //  Socket to send messages to
        ZMQ.Socket sender = context.socket(ZMQ.PUSH);
        sender.connect("tcp://localhost:5558");

        try
        {
            //  Process tasks forever
            while (true)
            {
                String string = new String(receiver.recv(0)).trim();
                TaskRequest request = gson.fromJson(string, TaskRequest.class);

                System.out.println(String.format(" [x] Received %s" + request));

                TaskResponse response = new TaskResponse(request.A + request.B, request.Id);
                sender.send(gson.toJson(response).getBytes(), 0);
                System.out.println(String.format(" [x] Sent %s" + request));

            }
        } catch (Exception e)
        {

        }
        sender.close();
        receiver.close();
        context.term();

    }
}
