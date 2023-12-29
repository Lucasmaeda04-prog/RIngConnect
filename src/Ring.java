import java.util.*;

class Ring {

    final double wifi_cost = 1.5;
    int n_elements;
    Node head;


    public Ring() {
        this.head = null;
        n_elements = 0;
    }

    public void add(Node node) {
        if (head == null) {
            head = node;
            head.nextNode = head;
            head.prevNode = head;
        } else {
            Node tail = this.head.prevNode;
            tail.nextNode = node;
            node.prevNode = tail;
            node.nextNode = head;
            head.prevNode = node;
        }
        this.n_elements++;
    }

    public void showDistances(Node p) {
        Node tmp = p.nextNode;
        while (tmp != p) {
            System.out.println(tmp.name + p.EdgesMap.get(tmp.name));
            tmp = tmp.nextNode;
        }
    }

    public Node getNodeByName(String nodeName) {// return a node through his name
        Node current = head;
        for (int i = 0; i < n_elements; i++) {
            if (current.name.equals(nodeName)) {
                return current;
            }
            current = current.nextNode;
        }
        return null; //didn't find the specific node
    }

    public List<Node> getNodesByService(String service) { // return a list of nodes that can do a specific service
        List<Node> nodesWithService = new ArrayList<>();
        Node current = head;
        for (int i = 0; i < n_elements; i++) {
            if (current.service.equals(service)) {
                nodesWithService.add(current);
            }
            current = current.nextNode;
        }
        return nodesWithService;
    }

    // DISTANCIAS----------------------------------------------------------------------------------------------------------------------------------

    public void Distance_node(Node initial) {
        initial.EdgesMap.put(initial.name, 0.0);
        double distance = 1;
        Node left = initial.prevNode; // runs through the ring in both directions
        Node right = initial.nextNode;
        while (left.nextNode != right && (left.nextNode != right.prevNode || left.nextNode == initial)) { // only stops when they meet each other or when one bypass the other
            if (left.status) {
                initial.EdgesMap.put(left.name, distance);
                left = left.prevNode;
            } else {
                initial.EdgesMap.put(left.name, Double.POSITIVE_INFINITY);
            }
            if (right.status) {
                initial.EdgesMap.put(right.name, distance);
                right = right.nextNode;
            } else {
                initial.EdgesMap.put(right.name, Double.POSITIVE_INFINITY);
            }
            // two or more broken computer means that there are no ways to reach those nodes
            if (!left.status && !right.status) {
                while (left.nextNode != right && left.nextNode != right.prevNode) {
                    initial.EdgesMap.put(right.name, Double.POSITIVE_INFINITY);
                    initial.EdgesMap.put(left.name, Double.POSITIVE_INFINITY);
                    left = left.prevNode;
                    right = right.nextNode;
                }
                break;
            }

            distance++;
        }
    }

    public void Distance_wifi(Node initial) {
        double distance = 1;
        initial.EdgesMap.put(initial.name, 0.0);

        Node left = initial.prevNode;
        Node right = initial.nextNode;
        while (left.prevNode != right && (left.nextNode != right.prevNode || left.nextNode == initial)) {
            if (left.status) {
                initial.EdgesMap.put(left.name, this.wifi_cost); // the weight it's going to always be the same
                left = left.prevNode;
            } else { // it means that this node/computer it's broken, so even with the wifi we cannot reach it, but we can still reach the next one.
                initial.EdgesMap.put(left.name, Double.POSITIVE_INFINITY);
                left = left.prevNode;
            }
            if (right.status) {
                initial.EdgesMap.put(right.name, this.wifi_cost);
                right = right.nextNode;
            } else {
                initial.EdgesMap.put(right.name, Double.POSITIVE_INFINITY);
                right = right.nextNode;
            }
        }
    }
//  CAMINHO---------------------------------------------------------------------------------------------------------------


    public String findPath_oneService(String name, String service, Ring ring, boolean cryptograph) {
        Node initial = this.getNodeByName(name);
        String target_name = ""; // target node name
        double distance_min_wire = Double.POSITIVE_INFINITY; // distance from initial to target only with wire
        double distance_min_wifi = Double.POSITIVE_INFINITY; // distance from initial to target only with wifi
        double distance_min_cripto = Double.POSITIVE_INFINITY; // distance from initial to crypto with wire + crypto to target with wife
        double distance_tmp = Double.POSITIVE_INFINITY; // only cache use
        double wifi_distance = Double.POSITIVE_INFINITY;// distance via wifi from A to B
        double wire_distance = Double.POSITIVE_INFINITY; // distance via wire from A to B
        double cripto_distance = Double.POSITIVE_INFINITY;  // distance to reach a certain crypto node
        Node cripto = null; // represents a cripto node, ideally the closest one
        ring.Distance_node(initial);
        List<Node> targets = this.getNodesByService(service);

        if(service.equals(initial.service)){
            System.out.println(initial.name);
            return null;
        }
        if(cryptograph){ // it means that we had already done the cryptograph in the previous service
            // searching for the closest node with this service, it could have more than one node with the same service
            if (service.equals("Criptografar Dados")){
                return null;
            }
            for (Node n : targets) {
                distance_tmp = initial.EdgesMap.get(n.name);
                if (distance_tmp <= distance_min_wire) {
                    target_name = n.name;
                    distance_min_wire = distance_tmp;
                }
            }
            ring.Distance_wifi(initial); // adjusting the distances to see via wi-fi,not using with  wifi_cost value since the target can be broken and have infinty cost to aquire it.
            distance_min_wifi = initial.EdgesMap.get(target_name);
            // comparing wifi cost with wire cost
            if (distance_min_wire < distance_min_wifi) {
                System.out.println(initial.name + " -> cabo -> " + target_name);
                return target_name;
            } else if (distance_min_wire == Double.POSITIVE_INFINITY && distance_min_wifi == Double.POSITIVE_INFINITY) {
                System.out.println("Não é possível se conectar via wifi nem cabo com a máquina. Serviço não concluído ;-;");
                return null;
            }
            // wifi is faster and safe
            else {
                System.out.println(initial.name + " -> wifi -> " + target_name);
                return null;
            }
        }
        else {

            // USER AUTHENTICATOR -- doesn't require a safety layer through the wire, so it's not mandatory to pass by the crypt node, only if it's  going via wi-fi,
            if (service.equals("Autenticar Usuários")) {
                // searching for the closest node with this service, it could have more than one node with the same service

                for (Node n : targets) {
                    wire_distance = initial.EdgesMap.get(n.name);
                    if (wire_distance <= distance_min_wire) {
                        target_name = n.name;
                        distance_min_wire = wire_distance;
                    }
                }
                // comparing if the distance to go the cript then going to the service via wi-fi is faster than going straight through wire
                List<Node> cripto_nodes = this.getNodesByService("Criptografar Dados");
                for (Node n : cripto_nodes) {  // getting the node which overall route is cheaper
                    ring.Distance_wifi(n);
                    distance_tmp = initial.EdgesMap.get(n.name) + n.EdgesMap.get(target_name);  // obs: not automatically adding the cost as the "wifi cost" variable, since the target node  can be broken and have  infinity weight
                    if (distance_tmp <= distance_min_cripto) {
                        distance_min_cripto = distance_tmp;
                        cripto = n;
                    }
                }

                // wire is faster and safer
                if (distance_min_wire <= distance_min_cripto && distance_min_wire != Double.POSITIVE_INFINITY) {
                    System.out.println(initial.name + " -> cabo -> " + target_name);
                    return target_name;
                }
                //  at least two computers is broken, and we cannot make a connection with wire.
                else if (distance_min_wire == Double.POSITIVE_INFINITY) {
                    // if you cannot reach it with cripto + wifi
                    if (distance_min_cripto == Double.POSITIVE_INFINITY) {
                        ring.Distance_wifi(initial);
                        if (initial.EdgesMap.get(target_name) != Double.POSITIVE_INFINITY) { // we can connect to the computer via wifi, but it will not be a safe connection
                            System.out.println("Não é possível se conectar de  maneira  segura a máquina, a seguinte routa é possível, mas não ha como garantir a integridade e segurança da conexão");
                            System.out.println(initial.name + " -> wifi -> " + target_name);
                            return null;
                        } else {
                            System.out.println("Não é possível se conectar via wifi nem cabo com a máquina. Autenticar não concluído ;-;");
                            return null;
                        }
                    }
                    // wife is faster and safe
                    else {
                        System.out.println(initial.name + " -> cabo -> " + cripto.name + " -> wifi -> " + target_name);
                        initial.safety_conecction = true; // it means that the connection made by the initial node is safe
                        return target_name;
                    }
                }

            }

            //MONITORING THE INTERNET  -- doesn't require a safety layer, so it's not mandatory to pass by the cript node -----------------------------------------------
            if (service.equals("Monitorar Velocidade da Rede")) {

                // searching for the closest node with this service, it could have more than one node with the same service
                for (Node n : targets) {
                    distance_tmp = initial.EdgesMap.get(n.name);
                    if (distance_tmp <= distance_min_wire) {
                        target_name = n.name;
                        distance_min_wire = distance_tmp;
                    }
                }
                ring.Distance_wifi(initial); // adjusting the distances to see via wi-fi,not using with  wifi_cost value since the target can be broken and have infinty cost to aquire it.
                distance_min_wifi = initial.EdgesMap.get(target_name);
                // comparing wifi cost with wire cost
                if (distance_min_wire < distance_min_wifi) {
                    System.out.println(initial.name + " -> cabo -> " + target_name);
                    return target_name;
                } else if (distance_min_wire == Double.POSITIVE_INFINITY && distance_min_wifi == Double.POSITIVE_INFINITY) {
                    System.out.println("Não é possível se conectar via wifi nem cabo com a máquina. Monitorar velocidade não concluído ;-;");
                    return null;
                }
                // wifi is faster and safe
                else {
                    System.out.println(initial.name + " -> wifi -> " + target_name);
                    return null;
                }
            }
            // COMPARTILHAMENTO DE ARQUIVOS -------------------------------------------------------------------------------------
            if (service.equals("Compartilhar Arquivos")) {
                List<Node> cripto_nodes = this.getNodesByService("Criptografar Dados");

                // seeing if the node exists and with we can go via wire or wifi
                for (Node n : targets) {
                    distance_tmp = initial.EdgesMap.get(n.name);
                    if (distance_tmp <= distance_min_wire) {
                        distance_min_wire = distance_tmp;
                        target_name = n.name;
                    }
                    ring.Distance_wifi(initial);
                    distance_tmp = initial.EdgesMap.get(n.name);
                    if (distance_tmp <= distance_min_wifi) {
                        distance_min_wifi = distance_tmp;
                        target_name = n.name;
                    }
                }
                if (distance_min_wire == Double.POSITIVE_INFINITY && distance_min_wifi == Double.POSITIVE_INFINITY) { // service node is broken
                    System.out.println("Não é possível se conectar via wifi nem cabo com a máquina. Compartilhar arquivos não concluído ;-;");
                    return null;
                }
                // it means that the service node exists
                else {
                    for (Node n : cripto_nodes) { // getting the closest cripto node via wire, it doesn't make sense to go with wifi since it would lose the security
                        distance_tmp = initial.EdgesMap.get(n.name);
                        if (distance_tmp <= cripto_distance) {
                            cripto_distance = distance_tmp;
                            cripto = n;
                        }
                    }
                    if (cripto_distance == Double.POSITIVE_INFINITY) { // there is not a safe way since the cripto node is broken, we can suggest a unsafe route.
                        if (distance_min_wire <= distance_min_wifi) {
                            System.out.println("Não é possível se conectar de  maneira  segura a máquina, a seguinte routa é possível, mas não ha garantia de integridade e segunrança da conexão");
                            System.out.println(initial.name + " -> wire -> " + target_name);
                            return null;
                        } else {
                            System.out.println("Não é possível se conectar de  maneira  segura a máquina, a seguinte routa é possível, mas não há garantia de integridade e segunrança da conexão");
                            System.out.println(initial.name + " -> wifi -> " + target_name);
                            return null;
                        }
                    } else { // there is a safe way
                        initial.safety_conecction = true; // it means that the connection made by the initial node is safe
                        ring.Distance_node(cripto);
                        for (Node n : targets) { //
                            distance_tmp = cripto.EdgesMap.get(n.name);
                            if (distance_tmp <= wire_distance) {
                                wire_distance = distance_tmp;
                                target_name = n.name;
                            }
                            ring.Distance_wifi(cripto);
                            distance_tmp = cripto.EdgesMap.get(n.name);
                            if (distance_tmp <= wifi_distance) {
                                wifi_distance = distance_tmp;
                                target_name = n.name;
                            }
                        }
                        if (wire_distance <= wifi_distance) { // comparing if it's faster with wire or with wifi
                            System.out.println(initial.name + " -> cabo -> " + cripto.name + " -> cabo -> " + target_name);
                            return target_name;
                        } else {
                            System.out.println(initial.name + " -> cabo -> " + cripto.name + " -> wifi -> " + target_name);
                            return target_name;
                        }
                    }
                }
            }
            // CRYPTOGRAPHY DATA ---- require security so it the best option it's to go with wire.
            if (service.equals("Criptografar Dados")) {

                // searching for the closest node with this service (with wire), it could have more than one node with the same service
                for (Node n : targets) {
                    distance_tmp = initial.EdgesMap.get(n.name);
                    if (distance_tmp <= distance_min_wire) {
                        target_name = n.name;
                        distance_min_wire = distance_tmp;
                    }
                }
                ring.Distance_wifi(initial); // adjusting the distances to see via wi-fi,not using with  wifi_cost value since the target can be broken and have infinty cost to aquire it.
                distance_min_wifi = initial.EdgesMap.get(target_name);
                if (distance_min_wire != Double.POSITIVE_INFINITY) { // checking if we can make a safe connection with wire
                    initial.safety_conecction = true; // it means that the connection made by the initial node is safe
                    System.out.println(initial.name + " -> cabo -> " + target_name);
                    return target_name;
                } else if (distance_min_wire == Double.POSITIVE_INFINITY && distance_min_wifi == Double.POSITIVE_INFINITY) { // cripto node is broken
                    System.out.println("Não é possível se conectar via wifi nem cabo com a máquina. Criptografar não concluído ;-;");
                    return null;
                } else {
                    System.out.println("Não é possível se conectar de  maneira  segura a máquina, a seguinte routa é possível, mas não há garantia de integridade e segunrança da conexão");
                    System.out.println(initial.name + " -> wifi -> " + target_name);
                    return null;
                }
            }
            return null;
        }
    }


    public void findPath_twoService(String name, String serviceA, String serviceB, Ring ring) {
        Node initial = ring.getNodeByName(name);
        String newInitial = findPath_oneService(name,serviceA,ring,false);
        if(newInitial == null || !initial.status) {
            findPath_oneService(name, serviceB, ring,false);
        } else {
            findPath_oneService(newInitial,serviceB,ring,true);
        }
    }
}