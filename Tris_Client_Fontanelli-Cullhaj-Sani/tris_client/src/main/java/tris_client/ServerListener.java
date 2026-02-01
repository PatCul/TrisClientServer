
// Autori: Fontanelli Nicolò, Cullhaj Patrik, Sani Gabriele


package tris_client;


//Thread che ascolta i messaggi provenienti dal server.Processa i messaggi e aggiorna lo stato del gioco di conseguenza
public class ServerListener implements Runnable {
	
	//Connessione al server
    private Connection conn;
    
    //Griglia di gioco
    private GameBoard board;
    
    //Riferimento al client principale
    private ClientMain client;
    
    //Costruttore
    public ServerListener(Connection conn, GameBoard board, ClientMain client) {
        this.conn = conn;
        this.board = board;
        this.client = client;
    }

    //Metodo run: ciclo principale del thread che ascolta i messaggi dal server
    public void run() {
        try {
        	//Loop infinito che legge i messaggi dal server
            while (true) {
            	//Leggi il prossimo comando dal server (bloccante)
                String cmd = conn.read();
                if (cmd == null) break;//Connessione chiusa

                //Processa il comando ricevuto
                switch (cmd) {

                    case "PARTITA_INIZIATA":
                    	//Una nuova partita è iniziata
                        client.setInPartita(true);
                        board.inizializza();//Reset della griglia
                        
                        //Leggi il simbolo assegnato (X o O)
                        String simbolo = conn.read();
                        System.out.println("\nPARTITA INIZIATA");
                        System.out.println("Simbolo: " + simbolo);
                        
                        //Leggi se è il tuo turno o dell'avversario
                        boolean primoTurno = conn.read().equals("TUO");
                        board.setMioTurno(primoTurno);
                        
                        //Leggi il nome dell'avversario
                        System.out.println("Avversario: " + conn.read());
                        
                        //Stampa la griglia iniziale
                        board.stampa();
                        
                        if (primoTurno) {
                        	//È il tuo turno, chiedi una mossa
                            System.out.println("\nTuo turno");
                            client.chiediMossa();
                        } else {
                        	//È il turno dell'avversario, aspetta
                            System.out.println("\nIn attesa dell'avversario...");
                        }
                        break;

                    case "TUO_TURNO":
                    	//È il tuo turno di giocare
                        board.setMioTurno(true);
                        board.stampa();//Stampa la griglia aggiornata
                        System.out.println("\nTuo turno");
                        client.chiediMossa();//Segnala al client di chiedere input
                        break;

                    case "MOSSA_OK":
                    	//La tua mossa è stata accettata dal server
                    	
                    	//Leggi la griglia aggiornata e stampala
                        board.aggiorna(conn.read());
                        board.stampa();
                        board.setMioTurno(false);//Non è più il tuo turno
                        
                        //Piccola pausa per permettere al server di inviare VITTORIA/PAREGGIO se la partita è finita
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {}
                        
                        //Controlla se c'è un messaggio già disponibile nel buffer (senza bloccarsi ad aspettare)
                        if (conn.ready()) {
                        	//C'è un messaggio disponibile (probabilmente VITTORIA o PAREGGIO)
                            String prossimo = conn.read();
                            if (prossimo != null) {
                            	//Se non è VITTORIA, stampa "In attesa..." prima di processare
                                //Se è VITTORIA non stampiamo "In attesa..."
                                if (!prossimo.equals("VITTORIA")) {
                                    System.out.println("\nIn attesa dell'avversario...");
                                }
                                //Processa il messaggio ricevuto
                                processaMessaggio(prossimo);
                            }
                        } else {
                        	//Nessun messaggio immediato quindi la partita continua normalmente
                            //Stampa "In attesa..." perché ora tocca all'avversario
                            System.out.println("\nIn attesa dell'avversario...");
                        }
                        break;

                    case "MOSSA_INVALIDA":
                    	//La mossa che è stata inviata non è valida (casella già occupata o coordinate fuori range)
                        System.out.println("\nMossa non valida, riprova");
                        client.chiediMossa();//Chiedi una nuova mossa
                        break;

                    case "MOSSA_AVVERSARIO":
                    	//L'avversario ha fatto una mossa
                    	
                    	//Aggiorna solo la griglia internamente
                        board.aggiorna(conn.read());
                        //Non stampa nulla qui,la griglia verrà stampata quando arriva TUO_TURNO subito dopo
                        break;

                    case "VITTORIA":
                    	//Hai vinto la partita
                        System.out.println("\nHai vinto");
                        client.setInPartita(false);//Fine partita
                        break;

                    case "SCONFITTA":
                    	//Hai perso la partita
                        board.stampa();//Stampa la griglia finale
                        System.out.println("\nHai perso");
                        client.setInPartita(false);//Fine partita
                        break;

                    case "PAREGGIO":
                    	//La partita è finita in pareggio
                        board.stampa();//Stampa la griglia finale
                        System.out.println("\nPareggio");
                        client.setInPartita(false);//Fine partita
                        break;

                    case "AVVERSARIO_DISCONNESSO":
                    	//L'avversario si è disconnesso improvvisamente durante la partita
                        //Vinci per abbandono
                        System.out.println("\nL'avversario si è disconnesso. Hai vinto");
                        client.setInPartita(false);//Fine partita
                        break;


                    case "---------------------------":
                    	//Inizio delle statistiche di fine partita
                        System.out.println("\n---------------------------");
                        if (conn.read().equals("STATISTICHE")) {
                        	//Leggi e stampa le statistiche
                            System.out.println("Statistiche:");
                            System.out.println("  Vittorie: " + conn.read());
                            System.out.println("  Sconfitte: " + conn.read());
                            System.out.println("  Pareggi: " + conn.read());
                            System.out.println("---------------------------");
                            System.out.println("\nScrivi: gioca | esci");
                        }
                        board.setMioTurno(false);//Finito, non è più il mio turno
                        break;

                    default:
                    	//Comando sconosciuto ricevuto dal server
                        System.out.println("Comando sconosciuto dal server: " + cmd);
                        break;
                }
            }
        } catch (Exception e) {
        	//Errore nella comunicazione con il server
            System.out.println("\nConnessione al server persa");
            client.termina();//Termina il client
        }
    }

    /*Processa un messaggio che è già stato letto dal buffer.
    Questo metodo viene usato quando leggiamo un messaggio in anticipo nel caso MOSSA_OK per decidere se stampare "In attesa..."  */
    private void processaMessaggio(String cmd) {
        try {
            switch (cmd) {
                case "MOSSA_AVVERSARIO":
                	//L'avversario ha mosso
                    board.aggiorna(conn.read());
                    break;

                case "VITTORIA":
                	//Hai vinto
                    System.out.println("\nHai vinto");
                    client.setInPartita(false);
                    break;

                case "SCONFITTA":
                	//Hai perso
                    board.stampa();
                    System.out.println("\nHai perso");
                    client.setInPartita(false);
                    break;

                case "PAREGGIO":
                	//Pareggio
                    board.stampa();
                    System.out.println("\nPareggio");
                    client.setInPartita(false);
                    break;

                case "TUO_TURNO":
                	//È il tuo turno
                    board.setMioTurno(true);
                    board.stampa();
                    System.out.println("\nTuo turno");
                    client.chiediMossa();
                    break;
               
                default:
                	//Altri comandi non vengono gestiti
                    System.out.println("Messaggio dopo MOSSA_OK non gestito: " + cmd);
                    break;
            }
        } catch (Exception e) {
            System.out.println("\nErrore nella processazione del messaggio");
        }
    }
}