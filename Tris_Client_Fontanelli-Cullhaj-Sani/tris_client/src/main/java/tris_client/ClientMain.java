

// Autori: Fontanelli Nicolò, Cullhaj Patrik, Sani Gabriele


package tris_client;

import java.net.Socket;
import java.util.Scanner;



//Classe principale del client per il gioco del tris. Gestisce la connessione al server, l'input dell'utente e il flusso del gioco
public class ClientMain {
	
	//Connessione socket al server
    private Socket socket;
    
    //Gestione comunicazione con il server
    private Connection conn;
    
    //Rappresentazione della griglia di gioco
    private GameBoard board;
    
    //Scanner per leggere l'input dell'utente
    private Scanner scanner;
    
    //Flag che indica se il client è ancora attivo
    private boolean attivo = true;
    
    //Flag che indica se il giocatore è in partita
    private boolean inPartita = false;
    
    //Flag che indica se è il momento di inserire una mossa
    private boolean aspettaMossa = false;
    
    
    //Avvia l'applicazione client
    public static void main(String[] args) {
        new ClientMain().start();
    }
    
    
    //Avvia client e gestisce il ciclo principale del gioco
    public void start() {
        scanner = new Scanner(System.in);

        try {
        	//Connessione al server locale sulla porta 1234
            socket = new Socket("localhost", 1234);
            conn = new Connection(socket);
            board = new GameBoard();

            //Loop per richiedere username finché non è valido
            boolean connesso = false;
            while (!connesso) {
                System.out.print("Username: ");
                String username = scanner.nextLine().trim(); //trim() toglie gli spazi
                
                //Valida che l'username non sia vuoto
                if (username.isEmpty()) {
                    System.out.println("L'username non può essere vuoto");
                    continue;
                }
                
                //Invia richiesta di connessione con username al server
                conn.send("CONNECT");
                conn.send(username);
                
                //Leggi la risposta del server
                String risposta = conn.read();
                if (risposta != null && risposta.equals("CONNECT_OK")) {
                	//Connessione riuscita
                    System.out.println(conn.read());//Stampa messaggio di benvenuto
                    connesso = true;
                } else if (risposta != null && risposta.equals("CONNECT_ERROR")) {
                	//Connessione fallita (username già in uso o non valido)
                    System.out.println(conn.read());//Stampa messaggio di errore
                }
            }
            
            //Avvia il thread che ascolta i messaggi dal server
            ServerListener listener = new ServerListener(conn, board, this); //this è l'oggetto ClientMain corrente
            new Thread(listener).start();

            System.out.println("\nScrivi: gioca | esci");
            
            //Ciclo principale del client
            while (attivo) {
                if (aspettaMossa) {
                	//È il turno del giocatore, chiedi riga e colonna
                	
                    // Svuota completamente il buffer prima di leggere la mossa
                    svuotaBufferCompleto();
                    
                    //Lettura della riga
                    System.out.print("Inserisci riga (0-2): ");
                    System.out.flush();//Scrive immediatamente i dati memorizzati nel buffer sulla console
                    int riga = leggiNumero();
                    if (riga == -1) break;// Errore nella lettura
                    
                    
                    //Lettura della colonna
                    System.out.print("Inserisci colonna (0-2): ");
                    System.out.flush();//Scrive immediatamente i dati memorizzati nel buffer sulla console
                    int colonna = leggiNumero();
                    if (colonna == -1) break; //Errore nella lettura
                    
                    
                    //Invia la mossa al server
                    conn.send("MOSSA");
                    conn.send(String.valueOf(riga)); //Ritorna la rappresentazione in formato String dell'intero passato
                    conn.send(String.valueOf(colonna));//Vedi precedente
                    
                    //Reset del flag, non aspetto più una mossa
                    aspettaMossa = false;
                    
                } else if (!inPartita) {
                	//Non in partita gestisci comandi "gioca" ed "esci"
                    try {
                    	//Piccola pausa per simulazione e non consumare troppa CPU
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        break;
                    }
                    //Controlla se c'è input disponibile
                    if (System.in.available() > 0) { //Non bloccante, ritorna numero byte disponibili da leggere
                        String cmd = scanner.nextLine().trim();

                        if (cmd.equalsIgnoreCase("gioca")) { //Ignora maiuscole/minuscole
                        	//Richiedi di iniziare una partita
                            conn.send("GIOCA");
                        } else if (cmd.equalsIgnoreCase("esci")) { //Ignora maiuscole/minuscole
                        	//Esci dal gioco
                            conn.send("ESCI");
                            attivo = false;
                        } else if (!cmd.isEmpty()) { //Se non vuoto/gioca/esci
                            System.out.println("Comando non valido. Scrivi: gioca | esci");
                        }
                    }
                } else {
                	//In partita ma non è il mio turno, svuoto eventuali input non voluti
                    //Questo evita che l'input digitato fuori turno venga letto quando è il turno
                    try {
                        Thread.sleep(50);
                        //Svuota tutto l'input scritto mentre non era il turno
                        while (System.in.available() > 0) {
                            System.in.read();
                        }
                    } catch (Exception e) {
                        break;
                    }
                }
            }
            //Chiudi la connessione quando il client termina
            socket.close();

        } catch (Exception e) {
            System.out.println("Errore");
        }
    }

    
    /*Svuota completamente il buffer di input per evitare letture spurie.
    Questo è importante quando chiediamo una mossa dopo che il giocatore potrebbe aver scritto qualcosa mentre non era il suo turno */
    private void svuotaBufferCompleto() {
        try {
        	//Svuota il buffer di System.in (input stream del sistema)
            while (System.in.available() > 0) {
                System.in.read(); //Legge numero byte finchè non è vuoto
            }
            
            //Crea un nuovo Scanner per resettare anche il buffer interno
            //Lo scanner ha un proprio buffer che potrebbe contenere dati
            scanner = new Scanner(System.in);
            
            //Piccola pausa per assicurarsi che tutto sia pulito
            Thread.sleep(10);
        } catch (Exception e) {
        	//Ignora eventuali errori durante lo svuotamento
        }
    }

    /*Legge un numero valido (0-2) dall'utente e continua a chiedere finché non riceve un input valido,
    ritorna il numero inserito dall'utente (0-2), o -1 in caso di errore*/
    private int leggiNumero() {
        while (true) {
            String input = scanner.nextLine().trim();
            
            //Controlla che l'input non sia vuoto
            if (input.isEmpty()) {
                System.out.println("Devi inserire un valore");
                System.out.print("Riprova: ");
                System.out.flush();//Scrive immediatamente i dati memorizzati nel buffer sulla console
                continue;//Serve per saltare alla fine di un blocco ma in questo caso non viene terminato il ciclo ma solamente interrotta l'iterazione corrente e l'esecuzione salta immediatamente alla valutazione della condizione di terminazione
            }

            try {
            	//Prova a convertire l'input (String) in intero
                int numero = Integer.parseInt(input);
                
                //Valida che il numero sia nel range corretto
                if (numero < 0 || numero > 2) {
                    System.out.println("Il numero deve essere tra 0 e 2");
                    System.out.print("Riprova: ");
                    System.out.flush();//Scrive immediatamente i dati memorizzati nel buffer sulla console
                    continue;//Vedi riga 195
                }
                
                //Numero valido, restituiscilo
                return numero;

            } catch (NumberFormatException e) {
            	//L'input (String) non è valido per essere convertito in formato numerico
                System.out.println("Inserisci un numero valido (0, 1 o 2)");
                System.out.print("Riprova: ");
                System.out.flush();//Scrive immediatamente i dati memorizzati nel buffer sulla console
            }
        }
    }
    
    /*Imposta lo stato "in partita" del client. Chiamato dal ServerListener quando una partita inizia o finisce,
    Parametro value true se il giocatore è in partita, false altrimenti
    */
    public void setInPartita(boolean value) {
        inPartita = value;
        if (!value) {
        	//Se non siamo più in partita, assicurati di non aspettare mosse
            aspettaMossa = false;
        }
    }
    
    //Segnala che è il momento di chiedere una mossa al giocatore. Chiamato dal ServerListener quando è il turno del giocatore
    public void chiediMossa() {
        aspettaMossa = true;
    }
    
    //Termina il client. Chiamato dal ServerListener in caso di disconnessione dal server
    public void termina() {
        attivo = false;
    }
}