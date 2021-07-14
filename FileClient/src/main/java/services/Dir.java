package services;

import factory.PropertiesFactory;

import java.io.IOException;
import java.util.Properties;

public class Dir {
    private final String PATH_START;

    private filesystem.Dir dir;

    public Dir() {
        Properties properties = PropertiesFactory.getProperties(true);

        PATH_START = properties.getProperty("PATH_START");

        try {
            dir = new filesystem.Dir(PATH_START, false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public filesystem.Dir getDir() {
        return dir;
    }
}
