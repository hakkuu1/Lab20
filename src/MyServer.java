import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class MyServer {
    private final int SERVER_PORT = 8189;
    private final String SERVER_START = "������ �������";
    private final String SERVER_WAIT = "������ ������� �����������";
    private final String SERVER_STOP = "������ ����������";
    private final String CLIENT = "������";
    private final String MESSAGE_FROM = "��������� ��: ";
    private final String CLIENT_DISCONNECT = "������ ����������";
    private final String EXIT_COMMAND = "exit"; //������� ��� ������
    private List<ClientHandler> clients;
    private AuthService authService;

    public AuthService getAuthService() {
        return authService;
    }
    public MyServer() {
        try (ServerSocket server = new ServerSocket(SERVER_PORT)) {
            System.out.println(SERVER_START);
            authService = new BaseAuthService();
            authService.start();
            clients = new ArrayList<>();
            System.out.println(SERVER_WAIT);
            while (true) {
                Socket socket = server.accept();
                new ClientHandler(this, socket);
            }
        } catch (IOException e) {
            System.out.println("������ � ������ �������");
        } finally {
            if (authService != null) {
                authService.stop();
            }
        }
    }
    public synchronized boolean isNickBusy(String nick) {
        for (ClientHandler o : clients) {
            if (o.getName().equals(nick)) {
                return true;
            }
        }
        return false;
    }
    public synchronized void broadcastMsg(String msg) {
        for (ClientHandler o : clients) {
            o.sendMsg(msg);
        }
    }

    public synchronized void unsubscribe(ClientHandler o) {
        clients.remove(o);
    }
    public synchronized void subscribe(ClientHandler o) {
        clients.add(o);
    }
    public synchronized void broadcastMsgToNick(String nameFrom, String nameTo, String msg) {
        for (ClientHandler o : clients) {
            if (o.getName().equals(nameTo)) {
                o.sendMsg("��������� �� " + nameFrom + ": " + msg);
            }
        }
    }
    public static void main (String[] args) { new MyServer(); }
}
