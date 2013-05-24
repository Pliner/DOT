import java.io.Serializable;

public class Solution implements Serializable{
    public double x1;
    public double x2;

    public Solution(double x1, double x2)
    {
        this.x1 = x1;
        this.x2 = x2;
    }

    @Override
    public String toString()
    {
        return "Solution{" +
                "x1=" + x1 +
                ", x2=" + x2 +
                '}';
    }
}
