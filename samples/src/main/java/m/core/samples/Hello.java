package m.core.samples;

import com.hackorama.mcore.server.spark.SparkServer;

public class Hello {

    public static void main(String[] args) {

        new HelloService().configureUsing(new SparkServer("hello")).start();

    }

}
