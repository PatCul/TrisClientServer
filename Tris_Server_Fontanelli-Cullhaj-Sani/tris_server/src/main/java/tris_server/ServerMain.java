
// Autori: Fontanelli Nicolò, Cullhaj Patrik, Sani Gabriele


package tris_server;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.io.IOException;


//Classe principale del server tris, gestisce le connessioni/disconnessioni dei client e il matchmaking
//Metodi e attributi static perchè condivisi da tutte le istanze della classe 
public class ServerMain {
	
	//Porta su cui il server ascolta
    public static final int PORTA = 1234;
    
    //Lista di tutti i client connessi
    public static ArrayList<ClientHandler> client = new ArrayList<ClientHandler>();
    
    //Lista dei client in attesa di una partita
    public static ArrayList<ClientHandler> attesa = new ArrayList<ClientHandler>();
    
    
    
    //Codice principale del server
    public static void main(String[] args) throws Exception {
    	
    	//Crea il server socket sulla porta specificata
        ServerSocket ss = new ServerSocket(PORTA);
        System.out.println("Server avviato sulla porta " + PORTA);
        try {
        	//Loop infinito per accettare nuove connessioni
            while (true) {
            	//Accetta una nuova connessione (bloccante)
                Socket s = ss.accept();
                
                //Crea un handler per gestire questo client
                ClientHandler ch = new ClientHandler(s);
                
                //Avvia un thread per gestire il client
                new Thread(ch).start();
            }
        } catch (IOException e) {
            System.out.println("Chiusura anomala server");
            ss.close();
        }
    }

    //Registra un nuovo client nella lista dei client connessi
    //Verifica che l'username non sia già in uso
    //Parametro c è il ClientHandler da registrare
    //Ritorna true se la registrazione è riuscita, false se l'username è già in uso
    //synchronized garantisce la mutua esclusione evitando race condition
    public static synchronized boolean registra(ClientHandler c) {
    	//Controlla se l'username è già in uso
        for (ClientHandler x : client)
            if (x.getUsername().equals(c.getUsername()))
                return false;//Username già in uso
        
        //Username disponibile, aggiungi il client
        client.add(c);
        return true;
    }
    
    //Aggiunge un client alla coda di attesa per una partita
    //Se ci sono almeno 2 giocatori in attesa, crea una nuova partita
    //Parametro c è il ClientHandler che cerca una partita
    //synchronized garantisce la mutua esclusione evitando race condition
    public static synchronized void cerca(ClientHandler c) {
        if (attesa.contains(c)) return; //Evita di aggiungere lo stesso client più volte

        //Aggiungi il client alla coda di attesa
        attesa.add(c);
        System.out.println("In attesa: " + attesa.size() + " giocatori");
        
        //Se ci sono almeno 2 giocatori, crea una partita
        if (attesa.size() >= 2) {
        	//Prendi i primi due giocatori dalla coda
            ClientHandler a = attesa.remove(0);
            ClientHandler b = attesa.remove(0);
            
            //Crea una nuova partita
            Partita p = new Partita(a, b);
            
            //Assegna la partita ai giocatori
            a.setPartita(p);
            b.setPartita(p);
            
            //Avvia la partita in un thread separato
            new Thread(p).start();
        }
    }

    
    //Rimuove un client dalle liste (client connessi e coda d'attesa). Chiamato quando un client si disconnette
    //Parametro c è il ClientHandler da rimuovere
    public static synchronized void rimuovi(ClientHandler c) {
        client.remove(c);
        attesa.remove(c);
    }
}