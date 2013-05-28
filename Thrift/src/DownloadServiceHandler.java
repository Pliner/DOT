import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.thrift.TException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class DownloadServiceHandler implements DownloadService.Iface
{
    @Override
    public String download(String uri) throws TException
    {
        System.out.println(String.format("Uri = %s was requested", uri));

        HttpClient client = new DefaultHttpClient();

        HttpGet request = new HttpGet(uri);
        try
        {
            HttpResponse response = client.execute(request);


            BufferedReader rd = new BufferedReader
                    (new InputStreamReader(response.getEntity().getContent()));

            String line;
            StringBuilder result = new StringBuilder();
            while ((line = rd.readLine()) != null)
            {
                result.append(line);
            }

            return result.toString();
        } catch (IOException e)
        {
            return "";
        }
    }
}

