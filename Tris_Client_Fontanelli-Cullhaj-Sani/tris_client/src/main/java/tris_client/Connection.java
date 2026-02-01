
// Autori: Fontanelli Nicolò, Cullhaj Patrik, Sani Gabriele

package tris_client;

import java.io.*;
import java.net.Socket;

//Classe per la comunicazione client-server.Gestisce l'invio e la ricezione di messaggi tramite socket

public class Connection {
	//Stream per leggere dal server
    private BufferedReader in;
    
    //Stream per scrivere al server
    private PrintWriter out;
    
    
    //Costruttore - inizializza gli stream di input/output
    //Parametro socket è il socket client connesso al server
    //throws IOException se c'è un errore nella creazione degli stream
    public Connection(Socket socket) throws IOException {
    	
    	//Crea un BufferedReader per leggere dal server
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        
        //Crea un PrintWriter per scrivere al server
        //Il parametro true abilita l'auto-flush (invia subito i dati)
        out = new PrintWriter(socket.getOutputStream(), true);
    }
    
    //Invia un messaggio al server
    public void send(String msg) {
        out.println(msg);//Scrive il messaggio seguito da newline (in quanto server legge linee readLine())
    }
    
    //Legge un messaggio dal server (bloccante)
    //Ritorna il messaggio ricevuto, o null se la connessione è chiusa
    //throws IOException se c'è un errore nella lettura
    public String read() throws IOException {
        return in.readLine();//Legge una linea dal server
    }

    //Controlla se ci sono dati disponibili da leggere senza bloccarsi
    //Questo è utile per verificare se il server ha già inviato un messaggio senza dover aspettare
    //Ritorna true se ci sono dati disponibili, false altrimenti
    //throws IOException se c'è un errore nel controllo
    public boolean ready() throws IOException {
        return in.ready();
    }
}