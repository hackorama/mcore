package com.hackorama.mcore;


import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Main application entry point
 *
 * @author KITHO
 */
public class Main {

    /**
     * Main application entry point
     *
     * @param args
     *            Configuration options
     * @throws NoSuchMethodException
     * @throws SecurityException
     * @throws FileNotFoundException
     * @throws IOException
     */
    public static void main(String[] args)
            throws NoSuchMethodException, SecurityException, FileNotFoundException, IOException {
        usage(args);
        ServiceManager.start(args);
    }

    private static void usage(String[] args) {
        if (args.length > 0 && ("-h".equals(args[0]) || "--h".equals(args[0]) || "-help".equals(args[0]) || "--help".equals(args[0]) || "help".equals(args[0]))) {
            System.out.println("Usage : Main [path_to_property_file]");
            System.exit(1);
        }
    }

}
