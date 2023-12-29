// Press Shift twice to open the Search Everywhere dialog and type `show whitespaces`,
// then press Enter. You can now see whitespace characters in your code.
import java.util.*;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        // initialize ring
        Ring ring = new Ring();
        ring.add(new Node("A", "Autenticar Usuários",true));
        ring.add(new Node("B", "",true));
        ring.add(new Node("C", "Criptografar Dados",true));
        ring.add(new Node("D", "",true));
        ring.add(new Node("E", "Monitorar Velocidade da Rede",true));
        ring.add(new Node("F", "",true));
        ring.add(new Node("G", "Compartilhar Arquivos",true));
        ring.add(new Node("H", "",true));

        System.out.println("Você deseja verificar a rota para um ou dois serviços (digite '1' ou '2')");
        String node;
        String service;
        if(sc.nextLine().equals("1")){
            System.out.println("Digite seu nó inicial:");
            node = sc.nextLine();
            System.out.println("Digite seu serviço:");
            service = sc.nextLine();
            ring.findPath_oneService(node,service,ring,false);
        }
        else {
            System.out.println("Digite seu nó inicial:");
            node = sc.nextLine();
            System.out.println("Digite seu serviço 1:");
            service = sc.nextLine();
            System.out.println("Digite seu serviço 2:");
            String servic2 = sc.nextLine();
            ring.findPath_twoService(node,service,servic2,ring);
        }
    }
}