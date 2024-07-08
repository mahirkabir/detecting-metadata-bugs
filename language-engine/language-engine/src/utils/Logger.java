package utils;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.nio.charset.Charset;

import engine.EngineFactory;

public class Logger {
    public static void log(String message) {
        try (FileOutputStream fout = new FileOutputStream(EngineFactory.getLogPath(), true)) {
            BufferedOutputStream bout = new BufferedOutputStream(fout);
            bout.write(message.getBytes(Charset.forName("UTF-8")));
            bout.write("\n".getBytes(Charset.forName("UTF-8")));
            bout.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void output(String message) {
        try (FileOutputStream fout = new FileOutputStream(EngineFactory.getOutputPath(), true)) {
            BufferedOutputStream bout = new BufferedOutputStream(fout);
            bout.write(message.getBytes(Charset.forName("UTF-8")));
            bout.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
