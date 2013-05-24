import com.google.gson.Gson;
import com.rabbitmq.client.*;

import java.util.*;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class Master
{
    public void run()
    {
        try
        {
            ExecutorService executorService = Executors.newFixedThreadPool(2);
            ConcurrentSkipListMap<UUID, TaskRequest> tasks = new ConcurrentSkipListMap<UUID, TaskRequest>();
            executorService.execute(new MasterSender(tasks));
            executorService.execute(new MasterReceiver(tasks));

        } catch (Exception e)
        {
            System.err.println(e);
        }
    }

    public class MasterSender implements Runnable
    {
        private Gson gson = new Gson();
        private Random random = new Random();
        private ConcurrentSkipListMap<UUID, TaskRequest> tasks;

        public MasterSender(ConcurrentSkipListMap<UUID, TaskRequest> tasks)
        {
            this.tasks = tasks;
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
                channel.exchangeDeclare("TaskRequest", "fanout");

                try
                {

                    while (true)
                    {
                        TaskRequest taskRequest = new TaskRequest(random.nextInt(), random.nextInt(), UUID.randomUUID());
                        channel.basicPublish("TaskRequest", "", null, gson.toJson(taskRequest).getBytes());
                        channel.waitForConfirmsOrDie();
                        tasks.putIfAbsent(taskRequest.Id, taskRequest);
                        System.out.println(" [x] Master sent '" + taskRequest + "'");
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

    public class MasterReceiver implements Runnable
    {

        private Gson gson = new Gson();

        private ConcurrentSkipListMap<UUID, TaskRequest> tasks;

        public MasterReceiver(ConcurrentSkipListMap<UUID, TaskRequest> tasks)
        {
            this.tasks = tasks;
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

                channel.exchangeDeclare("TaskResponse", "fanout");
                channel.queueDeclare("TaskResponse", true, false, false, Collections.<String, Object>emptyMap());
                channel.queueBind("TaskResponse", "TaskResponse", "");

                QueueingConsumer consumer = new QueueingConsumer(channel);
                channel.basicConsume("TaskResponse", true, consumer);

                while (true)
                {
                    QueueingConsumer.Delivery delivery = consumer.nextDelivery();
                    TaskResponse response = gson.fromJson(new String(delivery.getBody()), TaskResponse.class);

                    TaskRequest request = tasks.get(response.Id);
                    if (request == null)
                    {
                        System.err.println(String.format(" [x] Master received a bad response with id=%s", response.Id));
                    } else
                    {
                        System.out.println(" [x] Master received '" + response + "' for " + request);
                    }

                }
            } catch (Exception e)
            {
                System.err.println(e);
            }
        }
    }
}
