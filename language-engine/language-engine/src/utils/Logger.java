package utils;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.nio.charset.Charset;

public class Logger {
    public static void log(String message) {
        System.out.println(message);
    }

    public static void output(String message) {
        try (FileOutputStream fout = new FileOutputStream("output.txt", true)) {
            BufferedOutputStream bout = new BufferedOutputStream(fout);
            bout.write(message.getBytes(Charset.forName("UTF-8")));
            bout.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
