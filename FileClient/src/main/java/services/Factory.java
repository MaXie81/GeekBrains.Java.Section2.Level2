package services;

import filesystem.Dir;

public class Factory {
    public static services.Dir getDir() {
        return new services.Dir();
    }
    public static CommunicationService getCommunicationService(Dir dir) {
        return new CommunicationService(dir);
    }
}
