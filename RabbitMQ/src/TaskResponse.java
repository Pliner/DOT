import java.util.UUID;

public class TaskResponse
{
    public int C;
    public UUID Id;

    public TaskResponse() {}

    public TaskResponse(int c,  UUID id)
    {
        C = c;
        Id = id;
    }


    @Override
    public String toString()
    {
        return String.format("Id=%s C=%s", Id, C);
    }
}
