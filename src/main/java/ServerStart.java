import fileserver.Server;

public class ServerStart {
    public static void main(String[] args) {
        System.out.println("Запуск Сервера...");
        new Server().start();
    }
}
