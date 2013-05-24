public class Solver implements ISolver
{
    public Solution solve(int a, int b, int c)
    {
        try
        {
            Thread.sleep(5000);
        } catch (InterruptedException e)
        {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        double D = b * b - 4 * a * c;
        if(D < 0)
            return null;
        double x1 = (-b + Math.sqrt(D)) / 2 / a;
        double x2;
        if(D == 0)
            x2 = x1;
        else
            x2 = (-b - Math.sqrt(D)) / 2 / a;

        return new Solution(x1, x2);
    }
}
