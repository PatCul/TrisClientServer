
// Autori: Fontanelli Nicolò, Cullhaj Patrik, Sani Gabriele



package tris_client;


//Rappresenta la griglia di gioco del tris (3x3)
//Gestisce l'aggiornamento,la visualizzazione della griglia e i turni
public class GameBoard {
	
	//Griglia 3x3 che contiene 'X', 'O' o '-' (vuota)
    private char[][] griglia = new char[3][3];
    
    //Flag che indica se è il turno del giocatore
    private boolean mioTurno = false;

    
    //Inizializza la griglia con tutte le caselle vuote. Chiamato all'inizio di ogni nuova partita
    public void inizializza() {
        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 3; j++)
                griglia[i][j] = '-';//'-' indica casella vuota
    }
    
    //Aggiorna la griglia con i dati ricevuti dal server. La stringa contiene 9 caratteri che rappresentano la griglia
    //Letti da sinistra a destra, dall'alto in basso
    public void aggiorna(String s) {
        int k = 0;//Indice nella stringa
        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 3; j++)
            	//Assegna il carattere alla posizione corrispondente
                griglia[i][j] = s.charAt(k++);
    }

    //Stampa la griglia in formato visuale
    public void stampa() {
        System.out.println();//Riga vuota per spaziatura
        for (int i = 0; i < 3; i++) {
        	//Stampa la riga corrente con separatori
            System.out.println(" " + griglia[i][0] + " | " + griglia[i][1] + " | " + griglia[i][2]);
            //Stampa la linea di separazione (tranne dopo l'ultima riga)
            if (i < 2) System.out.println("---+---+---");
        }
    }

    //Verifica se è il turno del giocatore, ritorna true se è il turno del giocatore, false altrimenti
    public boolean isMioTurno() {
        return mioTurno;
    }

    //Imposta se è il turno del giocatore
    public void setMioTurno(boolean v) {
        mioTurno = v;
    }
}