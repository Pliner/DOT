
public class EntryPoint
{
    public static void main(String [] args) {
        Master master = new Master();
        master.run();
        Worker worker = new Worker();
        worker.run();
    }
}
