import java.util.UUID;

public class TaskRequest
{
    public int A;
    public int B;
    public UUID Id;

    public TaskRequest() {}

    public TaskRequest(int a, int b, UUID id)
    {
        A = a;
        B = b;
        Id = id;
    }


    @Override
    public String toString()
    {
        return String.format("Id=%s A=%s B=%s", Id, A, B);
    }
}



