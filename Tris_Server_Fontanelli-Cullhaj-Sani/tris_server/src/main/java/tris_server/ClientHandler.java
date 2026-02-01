
// Autori: Fontanelli Nicolò, Cullhaj Patrik, Sani Gabriele

package tris_server;

import java.io.*;
import java.net.Socket;

//Gestisce la comunicazione con un singolo client. Ogni client connesso ha il suo ClientHandler in un thread separato
public class ClientHandler implements Runnable {

	//Socket di connessione con il client
    private Socket socket;
    
    //Stream per leggere dal client
    private BufferedReader in;
    
    //Stream per scrivere al client
    private PrintWriter out;
    
    //Nome utente del client
    private String username;
    
    //Riferimento alla partita in corso (null se non in partita)
    private Partita partita;
    
    //Statistiche del giocatore
    private int vittorie = 0;
    private int sconfitte = 0;
    private int pareggi = 0;
    
    //Flag che indica se il client è attualmente in partita
    private boolean inPartita = false;
    
    
    //Costruttore
    //Parametro s è il socket connesso al client
    public ClientHandler(Socket s) {
        socket = s;
    }
    
    //Metodi per aggiornare le statistiche
    public void addVittoria() { vittorie++; }
    public void addSconfitta() { sconfitte++; }
    public void addPareggio() { pareggi++; }
    
    //Getter per le statistiche
    public int getVittorie() { return vittorie; }
    public int getSconfitte() { return sconfitte; }
    public int getPareggi() { return pareggi; }
    
    //Restituisce l'username del client
    public String getUsername() {
        return username;
    }

    
    //Assegna una partita al client
    //Parametro p è la partita a cui il client sta partecipando
    public void setPartita(Partita p) {
        partita = p;
        inPartita = true;
    }
    
    //Segna la fine della partita per questo client
    public void finePartita() {
        inPartita = false;
        partita = null;
    }
    
    
    //Verifica se il client è in partita
    //Ritorna true se in partita, false altrimenti
    public boolean isInPartita() {
        return inPartita;
    }
    
    
    //Invia un messaggio al client
    //Parametro s è il messaggio da inviare
    public void send(String s) {
        out.println(s);
    }

    
    //Legge un messaggio dal client
    //Ritorna il messaggio ricevuto, o null se la connessione è chiusa
    //throws IOException se c'è un errore nella lettura
    public String read() throws IOException {
        return in.readLine();
    }

    
    //Loop principale del thread che gestisce il client
    public void run() {
        try {
        	//Inizializza gli stream di comunicazione
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            
            //Loop per accettare username finché non è valido
            boolean connesso = false;
            while (!connesso) {
                String rd = read();
                //Attendi comando CONNECT
                if (rd != null && rd.equals("CONNECT")) {
                	//Leggi l'username proposto
                    username = read();
                    
                    //Valida che l'username non sia vuoto
                    if (username == null || username.trim().isEmpty()) {
                        send("CONNECT_ERROR");
                        send("Username non valido");
                        continue;
                    }
                    
                    //Prova a registrare l'username
                    if (!ServerMain.registra(this)) {
                    	//Username già in uso
                        send("CONNECT_ERROR");
                        send("Username già in uso");
                    } else {
                    	//Registrazione riuscita
                        send("CONNECT_OK");
                        send("Benvenuto " + username);
                        connesso = true;
                        System.out.println("Client connesso: " + username);
                    }
                }
            }

            //Loop principale che gestisce i comandi del client
            while (true) {
                String cmd = read();
                if (cmd == null) {
                	//Se read() restituisce null, il client si è disconnesso
                    if (inPartita) {
                        System.out.println(username + " disconnesso in modo anomalo durante la partita");
                    }
                    break;
                }

                if (cmd.equals("GIOCA")) {
                	//Il client vuole giocare una partita
                	System.out.println(username + " cerca partita...");
                	//Aggiungi alla coda di attesa
                    ServerMain.cerca(this);

                } else if (cmd.equals("MOSSA")) {
                	//Il client ha inviato una mossa
                    try {
                    	//Leggi le coordinate
                        String rigaStr = read();
                        String colStr = read();
                        
                        //Verifica che le coordinate siano state ricevute
                        if (rigaStr == null || colStr == null) {
                            send("MOSSA_INVALIDA");
                            continue;
                        }
                        
                        //Converti in interi
                        int r = Integer.parseInt(rigaStr);
                        int c = Integer.parseInt(colStr);
                        
                        //Se il client è in partita, delega alla partita
                        if (partita != null) {
                            partita.gestisciMossa(this, r, c);
                        }
                    } catch (NumberFormatException e) {
                    	//Coordinate non valide (non numeri)
                        send("MOSSA_INVALIDA");
                    }

                } else if (cmd.equals("ESCI")) {
                	//Disconnessione normale
                    System.out.println(username + " si è disconnesso normalmente");
                    break;
                    
                }
            }

        } catch (Exception e) {
        	//Errore o disconnessione improvvisa
            if (inPartita) {
                System.out.println(username + " disconnesso in modo anomalo durante la partita");
            } else {
                System.out.println(username + " disconnesso");
            }
            
            
        } finally { //Il codice all'interno di finally viene eseguito indipendentemente dal fatto che un'eccezione venga lanciata, catturata o meno
        	//Cleanup quando il client si disconnette
            //Se era in partita, notifica l'avversario
            if (inPartita && partita != null) {
                partita.gestisciDisconnessione(this);
            }
            
            //Rimuovi il client dalle liste del server
            ServerMain.rimuovi(this);
 
            
            //Chiudi il socket
            try {
                if (socket != null && !socket.isClosed())
                    socket.close();
            } catch (Exception ignored) {}
 
            System.out.println("Client rimosso: " + username);
        }
    }
}