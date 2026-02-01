
// Autori: Fontanelli Nicolò, Cullhaj Patrik, Sani Gabriele


package tris_server;

//Rappresenta una partita di tris tra due giocatori
//Gestisce la logica di gioco e determina vincitori/perdenti/pareggi
public class Partita implements Runnable {

	//I due giocatori
    private ClientHandler p1, p2;
    
    //Griglia di gioco 3x3
    private char[][] g = new char[3][3];
    
    //Riferimento al giocatore di cui è il turno
    private ClientHandler turno;
    
    //Flag che indica se la partita è ancora attiva
    private boolean partitaAttiva = true;

    
    
    //Costruttore inizializza una nuova partita
    //Parametro a è il primo giocatore (riceverà il simbolo X)
    //Parametero b è il secondo giocatore (riceverà il simbolo O)
    public Partita(ClientHandler a, ClientHandler b) {
        p1 = a;
        p2 = b;
        turno = p1;//Il primo giocatore inizia
        
        //Inizializza la griglia vuota
        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 3; j++)
                g[i][j] = '-';
    }

    
    //Avvia la partita inviando i messaggi iniziali ai giocatori
    public void run() {
        System.out.println("Partita iniziata: " + p1.getUsername() + " vs " + p2.getUsername());
        
        //Notifica il primo giocatore (X)
        p1.send("PARTITA_INIZIATA");
        p1.send("X");//Simbolo
        p1.send("TUO");//È il suo turno
        p1.send(p2.getUsername());//Nome avversario

        
        //Notifica il secondo giocatore (O)
        p2.send("PARTITA_INIZIATA");
        p2.send("O");//Simbolo
        p2.send("NON_TUO");//Non è il suo turno
        p2.send(p1.getUsername());//Nome avversario
    }

    //Gestisce una mossa di un giocatore:valida la mossa, aggiorna la griglia e verifica vittoria/pareggio/sconfitta
    //Parametro c è il giocatore che ha fatto la mossa
    //Parametro r è la riga della mossa (0-2)
    //Parametro col è la colonna della mossa (0-2)
    //synchronized garantisce la mutua esclusione evitando race condition
    public synchronized void gestisciMossa(ClientHandler c, int r, int col) {
    	//Se la partita è finita, ignora la mossa
        if (!partitaAttiva) return;

        //Verifica che sia il turno del giocatore corretto
        if (c != turno) {
        	//Non è il suo turno, ignora la mossa
            return;
        }

        //Valida che le coordinate siano nel range corretto
        if (r < 0 || r > 2 || col < 0 || col > 2) {
            c.send("MOSSA_INVALIDA");
            return;
        }

        //Verifica che la casella sia vuota
        if (g[r][col] != '-') {
            c.send("MOSSA_INVALIDA");
            return;
        }

        //Mossa valida, eseguila
        
        //Determina il simbolo del giocatore (X o O)
        char simbolo = (c == p1 ? 'X' : 'O');
        g[r][col] = simbolo;

        //Identifica l'avversario
        ClientHandler avv = (c == p1 ? p2 : p1);

        //Notifica il giocatore che la mossa è stata accettata
        c.send("MOSSA_OK");
        c.send(serializza());//Invia la griglia aggiornata

        //Notifica l'avversario della mossa
        avv.send("MOSSA_AVVERSARIO");
        avv.send(serializza());//Invia la griglia aggiornata
        
        //Controlla se c'è un vincitore
        if (vinto(simbolo)) {
        	//Il giocatore corrente ha vinto
            c.addVittoria();
            avv.addSconfitta();

            c.send("VITTORIA");
            avv.send("SCONFITTA");

            System.out.println(c.getUsername() + " ha vinto contro " + avv.getUsername());
            
            //Invia le statistiche a entrambi
            inviaStatistiche(c);
            inviaStatistiche(avv);
            terminaPartita();
            return;
        }

        //Controlla se la griglia è piena (pareggio)
        if (piena()) {
        	//Pareggio
            c.addPareggio();
            avv.addPareggio();

            c.send("PAREGGIO");
            avv.send("PAREGGIO");

            System.out.println("Pareggio tra " + p1.getUsername() + " e " + p2.getUsername());
            
            //Invia le statistiche a entrambi
            inviaStatistiche(c);
            inviaStatistiche(avv);
            terminaPartita();
            return;
        }

        //La partita continua, passa il turno all'avversario
        turno = avv;
        turno.send("TUO_TURNO");
    }

    //Gestisce la disconnessione improvvisa di un giocatore, l'avversario vince automaticamente
    //Parametro disconnesso è il giocatore che si è disconnesso
    //synchronized garantisce la mutua esclusione e evita race condition
    public synchronized void gestisciDisconnessione(ClientHandler disconnesso) {
    	//Se la partita è già finita, non fare nulla
        if (!partitaAttiva) return;

        
        //Determina il vincitore (l'altro giocatore)
        ClientHandler vincitore = (disconnesso == p1 ? p2 : p1);

        //Aggiorna le statistiche
        vincitore.addVittoria();
        disconnesso.addSconfitta();

        //Notifica il vincitore
        vincitore.send("AVVERSARIO_DISCONNESSO");
        inviaStatistiche(vincitore);

        System.out.println(disconnesso.getUsername() + " disconnesso, " + vincitore.getUsername() + " vince");
        
        terminaPartita();
    }
    
    //Termina la partita e pulisce lo stato
    private void terminaPartita() {
        partitaAttiva = false;
        p1.finePartita();
        p2.finePartita();
    }
    
    
    //Serializza la griglia in una stringa di 9 caratteri Letta da sinistra a destra, dall'alto in basso
    //Ritorna una stringa rappresentante la griglia
    private String serializza() {
        String s = "";
        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 3; j++)
                s += g[i][j];
        return s;
    }
    
    
    
    //Verifica se un giocatore ha vinto controllando righe, colonne e diagonali
    //Parametro sè il simbolo da controllare (X o O)
    //Ritorna true se il simbolo ha vinto, false altrimenti
    private boolean vinto(char s) {
    	//Controlla le righe
        for (int i = 0; i < 3; i++)
            if (g[i][0] == s && g[i][1] == s && g[i][2] == s) return true;

        //Controlla le colonne
        for (int j = 0; j < 3; j++)
            if (g[0][j] == s && g[1][j] == s && g[2][j] == s) return true;

        //Controlla le diagonali
        return (g[0][0] == s && g[1][1] == s && g[2][2] == s) ||
               (g[0][2] == s && g[1][1] == s && g[2][0] == s);
    }

    //Verifica se la griglia è piena (tutte le caselle occupate)
    //Ritorna true se piena, false se ci sono ancora caselle vuote
    private boolean piena() {
        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 3; j++)
                if (g[i][j] == '-') return false;//Trovata una casella vuota
        return true;//Nessuna casella vuota
    }
    
    //Invia le statistiche di gioco a un client
    //Parametro c è il client a cui inviare le statistiche
    private void inviaStatistiche(ClientHandler c) {
        c.send("---------------------------");
        c.send("STATISTICHE");
        c.send(String.valueOf(c.getVittorie())); //valueof ritorna la rappresentazione in formato String dell'intero passato
        c.send(String.valueOf(c.getSconfitte()));
        c.send(String.valueOf(c.getPareggi()));
    }
}