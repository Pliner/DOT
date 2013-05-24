import com.google.gson.Gson;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.QueueingConsumer;

import java.util.Collections;
import java.util.UUID;
import java.util.concurrent.*;

public class Worker
{
    public void run() {
        try
        {
            ExecutorService executorService = Executors.newFixedThreadPool(2);
            ConcurrentLinkedQueue<TaskResponse> responses = new ConcurrentLinkedQueue<TaskResponse>();
            executorService.execute(new WorkerSender(responses));
            executorService.execute(new WorkerReceiver(responses));

        } catch (Exception e)
        {
            System.err.println(e);
        }
    }

    public class WorkerReceiver implements Runnable {

        private Gson gson = new Gson();
        private ConcurrentLinkedQueue<TaskResponse> responses;

        public WorkerReceiver(ConcurrentLinkedQueue<TaskResponse> responses) {
            this.responses = responses;
        }

        @Override
        public void run()
        {
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost("localhost");
            factory.setRequestedHeartbeat(10);
            try
            {

                Connection connection = factory.newConnection();
                Channel channel = connection.createChannel();

                channel.exchangeDeclare("TaskRequest", "fanout");
                channel.queueDeclare("TaskRequest", true, false, false, Collections.<String, Object>emptyMap());
                channel.queueBind("TaskRequest", "TaskRequest", "");

                System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

                QueueingConsumer consumer = new QueueingConsumer(channel);
                channel.basicConsume("TaskRequest", true, consumer);

                while (true)
                {
                    QueueingConsumer.Delivery delivery = consumer.nextDelivery();
                    TaskRequest request = gson.fromJson(new String(delivery.getBody()), TaskRequest.class);

                    responses.add(new TaskResponse(request.A + request.B, request.Id));
                }
            } catch (Exception e)
            {
                System.err.println(e);
            }
        }
    }

    public class WorkerSender implements Runnable{
        private ConcurrentLinkedQueue<TaskResponse> responses;
        private Gson gson = new Gson();

        public WorkerSender(ConcurrentLinkedQueue<TaskResponse> responses) {

            this.responses = responses;
        }

        @Override
        public void run()
        {
            try
            {
                ConnectionFactory factory = new ConnectionFactory();
                factory.setHost("localhost");
                Connection connection = factory.newConnection();
                Channel channel = connection.createChannel();
                channel.confirmSelect();
                channel.exchangeDeclare("TaskResponse", "fanout");

                try
                {

                    while (true)
                    {
                        TaskResponse response = responses.poll();
                        if(response != null) {

                            channel.basicPublish("TaskResponse", "", null, gson.toJson(response).getBytes());
                            channel.waitForConfirmsOrDie();
                            System.out.println(" [x] Sent '" + response + "'");
                            Thread.sleep(5000);
                        }
                    }
                } finally
                {
                    connection.close();
                }
            } catch (Exception e)
            {
                System.err.println(e);
            }
        }
    }
}
