# TrisClientServer


<h1 id="tris-multiplayer---clientserver">Tris Multiplayer - Client/Server</h1>
<p>Un’implementazione del classico gioco del tris in Java con architettura client-server. Due o più giocatori possono connettersi al server e giocare partite online con tracking delle statistiche.</p>
<h2 id="indice">Indice</h2>
<ul>
<li><a href="#descrizione">Descrizione</a></li>
<li><a href="#caratteristiche">Caratteristiche</a></li>
<li><a href="#requisiti">Requisiti</a></li>
<li><a href="#struttura-del-progetto">Struttura del Progetto</a></li>
<li><a href="#installazione-e-compilazione">Installazione e Compilazione</a></li>
<li><a href="#avvio-dellapplicazione">Avvio dell’Applicazione</a></li>
<li><a href="#come-giocare">Come Giocare</a></li>
<li><a href="#protocollo-di-comunicazione">Protocollo di Comunicazione</a></li>
<li><a href="#casi-di-errore">Casi di errore</a></li>
</ul>
<h2 id="descrizione">Descrizione</h2>
<p>Questo progetto implementa un gioco del tris multiplayer dove:</p>
<ul>
<li>Un <strong>server</strong> gestisce le connessioni dei giocatori e coordina le partite</li>
<li>Più <strong>client</strong> possono connettersi contemporaneamente</li>
<li>Il <strong>matchmaking</strong> accoppia automaticamente due giocatori in attesa</li>
<li>Le <strong>statistiche</strong> (vittorie, sconfitte, pareggi) vengono tracciate per ogni sessione</li>
</ul>
<h2 id="caratteristiche">Caratteristiche</h2>
<ul>
<li>✅ Architettura client-server multi-threaded</li>
<li>✅ Matchmaking automatico</li>
<li>✅ Validazione delle mosse</li>
<li>✅ Gestione delle disconnessioni</li>
<li>✅ Tracking delle statistiche di gioco</li>
<li>✅ Sincronizzazione corretta tra i giocatori</li>
</ul>
<h2 id="requisiti">Requisiti</h2>
<ul>
<li><strong>Java JDK 1.5 o superiore</strong></li>
<li>Sistema operativo: Windows, Linux o macOS</li>
<li>Terminale/Console per eseguire il server e i client</li>
</ul>
<h2 id="struttura-del-progetto">Struttura del Progetto</h2>
<pre><code>tris/
├── tris_server/
│   ├── ServerMain.java       # Server principale
│   ├── ClientHandler.java    # Gestore delle connessioni client
│   └── Partita.java          # Logica della partita
└── tris_client/
    ├── ClientMain.java       # Client principale
    ├── ServerListener.java   # Listener per messaggi dal server
    ├── Connection.java       # Gestione della connessione
    └── GameBoard.java        # Rappresentazione della griglia
</code></pre>
<h2 id="installazione-e-compilazione">Installazione e Compilazione</h2>
<h3 id="opzione-1-compilazione-manuale">Opzione 1: Compilazione Manuale</h3>
<ol>
<li><strong>Crea la struttura delle directory:</strong></li>
</ol>
<pre class=" language-bash"><code class="prism  language-bash">   <span class="token function">mkdir</span> -p tris/tris_server
   <span class="token function">mkdir</span> -p tris/tris_client
</code></pre>
<ol start="2">
<li>
<p><strong>Copia i file nelle rispettive directory</strong> seguendo la struttura sopra</p>
</li>
<li>
<p><strong>Compila il server:</strong></p>
</li>
</ol>
<pre class=" language-bash"><code class="prism  language-bash">   <span class="token function">cd</span> tris
   javac tris_server/*.java
</code></pre>
<ol start="4">
<li><strong>Compila il client:</strong></li>
</ol>
<pre class=" language-bash"><code class="prism  language-bash">   javac tris_client/*.java
</code></pre>
<h3 id="opzione-2-usando-un-ide-eclipse">Opzione 2: Usando un IDE (Eclipse)</h3>
<ol>
<li>
<p>Importa entrambi i progetti nel workspace:</p>
<p>file → open project from file system → archive →        selezioni lo zip del progetto da importare → selezioni la destinazione → import</p>
</li>
<li>
<p>L’IDE compilerà automaticamente</p>
</li>
</ol>
<h2 id="avvio-dellapplicazione">Avvio dell’Applicazione</h2>
<h3 id="passo-1-avvia-il-server">Passo 1: Avvia il Server</h3>
<p>Apri un terminale e esegui:</p>
<pre class=" language-bash"><code class="prism  language-bash"><span class="token function">cd</span> tris
java tris_server.ServerMain
</code></pre>
<p>Su Eclipse run → ServerMain</p>
<p><strong>Output atteso:</strong></p>
<pre><code>Server avviato sulla porta 1234
</code></pre>
<p>Il server ora è in ascolto sulla porta <strong>1234</strong> e aspetta le connessioni dei client.</p>
<h3 id="passo-2-avvia-il-primo-client">Passo 2: Avvia il Primo Client</h3>
<p>Apri un <strong>nuovo terminale</strong> (lascia il server in esecuzione) ed esegui:</p>
<pre class=" language-bash"><code class="prism  language-bash"><span class="token function">cd</span> tris
java tris_client.ClientMain
</code></pre>
<p>Su Eclipse Run → ClientMain</p>
<p><strong>Output atteso:</strong></p>
<pre><code>Username: 
</code></pre>
<p>Inserisci un username (es. “Giocatore1”) e premi Invio.</p>
<pre><code>Username: Giocatore1
Benvenuto Giocatore1

Scrivi: gioca | esci
</code></pre>
<h3 id="passo-3-avvia-il-secondo-client">Passo 3: Avvia il Secondo Client</h3>
<p>Apri un <strong>terzo terminale</strong> ed esegui lo stesso comando:</p>
<pre class=" language-bash"><code class="prism  language-bash"><span class="token function">cd</span> tris
java tris_client.ClientMain
</code></pre>
<p>Su Eclipse Run → ClientMain</p>
<p><strong>Output atteso:</strong></p>
<pre><code>Username: 
</code></pre>
<p>Inserisci un username diverso (es. “Giocatore2”) e premi invio.</p>
<pre><code>Username: Giocatore2
Benvenuto Giocatore2

Scrivi: gioca | esci
</code></pre>
<h3 id="passo-4-inizia-a-giocare">Passo 4: Inizia a Giocare</h3>
<p>In <strong>entrambi i terminali dei client</strong>, digita:</p>
<pre><code>gioca
</code></pre>
<p>I due giocatori verranno automaticamente accoppiati e la partita inizierà</p>
<h2 id="come-giocare">Come Giocare</h2>
<h3 id="comandi-disponibili">Comandi Disponibili</h3>
<p><strong>Fuori dalla partita:</strong></p>
<ul>
<li><code>gioca</code> - Cerca una partita</li>
<li><code>esci</code> - Disconnetti dal server</li>
</ul>
<p><strong>Durante la partita:</strong></p>
<ul>
<li>Quando è il tuo turno:
<ul>
<li>Inserisci riga (0-2):→ Digita 0, 1 o 2</li>
<li>Inserisci colonna (0-2): → Digita 0, 1 o 2</li>
</ul>
</li>
</ul>
<h3 id="griglia-di-gioco">Griglia di Gioco</h3>
<p>La griglia è numerata come segue:</p>
<pre><code>     Colonne
     0   1   2
   +---+---+---+
0  |   |   |   |  Righe
   +---+---+---+
1  |   |   |   |
   +---+---+---+
2  |   |   |   |
   +---+---+---+
</code></pre>
<h3 id="esempio-di-partita">Esempio di Partita</h3>
<p><strong>Client 1 (X):</strong></p>
<pre><code>PARTITA INIZIATA
Simbolo: X
Avversario: Giocatore2
 - | - | -
---+---+---
 - | - | -
---+---+---
 - | - | -
Tuo turno
Inserisci riga (0-2): 1
Inserisci colonna (0-2): 1
 - | - | -
---+---+---
 - | X | -
---+---+---
 - | - | -
In attesa dell'avversario...
</code></pre>
<p><strong>Client 2 (O):</strong></p>
<pre><code>PARTITA INIZIATA
Simbolo: O
Avversario: Giocatore1 
 - | - | -
---+---+---
 - | - | -
---+---+---
 - | - | -
In attesa dell'avversario...
 - | - | -
---+---+---
 - | X | -
---+---+---
 - | - | -
Tuo turno
Inserisci riga (0-2): 0
Inserisci colonna (0-2): 0
</code></pre>
<h3 id="fine-partita">Fine Partita</h3>
<p>Alla fine della partita, vengono mostrate le statistiche :</p>
<pre><code>Hai vinto!
---------------------------
Statistiche:
  Vittorie: 1
  Sconfitte: 0
  Pareggi: 0
---------------------------

Scrivi: gioca | esci
</code></pre>
<h2 id="protocollo-di-comunicazione">Protocollo di Comunicazione</h2>
<h3 id="messaggi-client-→-server">Messaggi Client → Server</h3>

<table>
<thead>
<tr>
<th>Comando</th>
<th>Parametri</th>
<th>Descrizione</th>
</tr>
</thead>
<tbody>
<tr>
<td><code>CONNECT</code></td>
<td>username</td>
<td>Richiesta di connessione</td>
</tr>
<tr>
<td><code>GIOCA</code></td>
<td>-</td>
<td>Richiesta di cercare una partita</td>
</tr>
<tr>
<td><code>MOSSA</code></td>
<td>riga, colonna</td>
<td>Invio di una mossa</td>
</tr>
<tr>
<td><code>ESCI</code></td>
<td>-</td>
<td>Disconnessione</td>
</tr>
</tbody>
</table><h3 id="messaggi-server-→-client">Messaggi Server → Client</h3>

<table>
<thead>
<tr>
<th>Comando</th>
<th>Parametri</th>
<th>Descrizione</th>
</tr>
</thead>
<tbody>
<tr>
<td><code>CONNECT_OK</code></td>
<td>messaggio</td>
<td>Connessione accettata</td>
</tr>
<tr>
<td><code>CONNECT_ERROR</code></td>
<td>messaggio</td>
<td>Connessione rifiutata</td>
</tr>
<tr>
<td><code>PARTITA_INIZIATA</code></td>
<td>simbolo, turno, avversario</td>
<td>Inizio partita</td>
</tr>
<tr>
<td><code>TUO_TURNO</code></td>
<td>-</td>
<td>È il turno del giocatore</td>
</tr>
<tr>
<td><code>MOSSA_OK</code></td>
<td>griglia</td>
<td>Mossa accettata</td>
</tr>
<tr>
<td><code>MOSSA_INVALIDA</code></td>
<td>-</td>
<td>Mossa rifiutata</td>
</tr>
<tr>
<td><code>MOSSA_AVVERSARIO</code></td>
<td>griglia</td>
<td>L’avversario ha mosso</td>
</tr>
<tr>
<td><code>VITTORIA</code></td>
<td>-</td>
<td>Hai vinto</td>
</tr>
<tr>
<td><code>SCONFITTA</code></td>
<td>-</td>
<td>Hai perso</td>
</tr>
<tr>
<td><code>PAREGGIO</code></td>
<td>-</td>
<td>Partita pari</td>
</tr>
<tr>
<td><code>AVVERSARIO_DISCONNESSO</code></td>
<td>-</td>
<td>L’avversario si è disconnesso</td>
</tr>
</tbody>
</table><h2 id="casi-di-errore">Casi di errore</h2>
<h3 id="problema-address-already-in-use">Problema: “Address already in use”</h3>
<p><strong>Causa:</strong> La porta 1234 è già occupata da un altro processo.</p>
<p><strong>Soluzione:</strong></p>
<ol>
<li>Chiudi eventuali istanze del server già in esecuzione</li>
<li>Oppure cambia la porta in <code>ServerMain.java</code>:</li>
</ol>
<pre class=" language-java"><code class="prism  language-java">   <span class="token keyword">public</span> <span class="token keyword">static</span> <span class="token keyword">final</span> <span class="token keyword">int</span> PORTA <span class="token operator">=</span> <span class="token number">5000</span><span class="token punctuation">;</span> <span class="token comment">// Usa una porta diversa</span>
</code></pre>
<p>E in <code>ClientMain.java</code>:</p>
<pre class=" language-java"><code class="prism  language-java">   socket <span class="token operator">=</span> <span class="token keyword">new</span> <span class="token class-name">Socket</span><span class="token punctuation">(</span><span class="token string">"localhost"</span><span class="token punctuation">,</span> <span class="token number">5000</span><span class="token punctuation">)</span><span class="token punctuation">;</span>
</code></pre>
<h3 id="problema-connection-refused">Problema: “Connection refused”</h3>
<p><strong>Causa:</strong> Il server non è in esecuzione o non è raggiungibile.</p>
<p><strong>Soluzione:</strong></p>
<ol>
<li>Verifica che il server sia avviato</li>
<li>Controlla che stia ascoltando sulla porta corretta</li>
<li>Verifica che non ci siano firewall/misure di sicurezza che bloccano la connessione</li>
</ol>
<h3 id="problema-username-già-in-uso">Problema: “Username già in uso”</h3>
<p><strong>Causa:</strong> Hai tentato di connetterti con un username già utilizzato.</p>
<p><strong>Soluzione:</strong> Usa un username diverso.</p>
<h3 id="problema-il-client-non-risponde-dopo-aver-inviato-una-mossa">Problema: Il client non risponde dopo aver inviato una mossa</h3>
<p><strong>Causa:</strong> Possibile problema di sincronizzazione o disconnessione.</p>
<p><strong>Soluzione:</strong></p>
<ol>
<li>Verifica che il server sia ancora in esecuzione</li>
<li>Riavvia client e server</li>
<li>Controlla i log del server per errori</li>
</ol>
<h3 id="problema-mossa-non-valida-anche-se-la-casella-è-vuota">Problema: “Mossa non valida” anche se la casella è vuota</h3>
<p><strong>Causa:</strong> Non è il tuo turno o c’è un problema di sincronizzazione.</p>
<p><strong>Soluzione:</strong></p>
<ol>
<li>Aspetta il messaggio “Tuo turno” prima di inserire una mossa</li>
<li>Riavvia la partita se il problema persiste</li>
</ol>
<h2 id="note-tecniche">Note Tecniche</h2>
<h3 id="porte-e-connessioni">Porte e Connessioni</h3>
<ul>
<li><strong>Porta predefinita:</strong> 1234</li>
<li><strong>Connessione:</strong> localhost (127.0.0.1)</li>
<li><strong>Protocollo:</strong> TCP/IP</li>
</ul>
<h3 id="thread">Thread</h3>
<ul>
<li>Il server crea un thread per ogni client connesso</li>
<li>Ogni partita viene eseguita in un thread separato</li>
<li>Il client ha un thread principale e un thread per ascoltare il server</li>
</ul>
<h3 id="sincronizzazione">Sincronizzazione</h3>
<ul>
<li>Le operazioni critiche nella classe <code>Partita</code> sono sincronizzate</li>
<li>I metodi di matchmaking in <code>ServerMain</code> sono sincronizzati</li>
<li>Il buffer di input viene svuotato prima di leggere le mosse</li>
</ul>
<h2 id="autori">Autori</h2>
<p>Nicolò Fontanelli, Patrik Cullhaj, Gabriele Sani</p>
<h1 id="buon-divertimento"><strong>Buon divertimento!</strong></h1>

