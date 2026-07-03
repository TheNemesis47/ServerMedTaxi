# 🚑 ServerMedTaxi — Server Centrale

**Componente server del progetto MedTaxi**
Progetto d'esame di **Programmazione 3** — Università degli Studi di Napoli "Parthenope"
Realizzato da **Samuel Arenella**

---

## Cos'è

Questo è il **server centrale** del sistema **MedTaxi**, l'applicazione per la prenotazione di
ambulanze e trasporti sanitari (per cliniche, ospedali e utenti privati) con confronto di aziende
e tariffe e tracciamento in tempo reale, in stile Uber.

Il server gestisce la logica delle prenotazioni: riceve le richieste dal client, cerca le aziende
disponibili, calcola distanze e costi, e inoltra la richiesta all'azienda scelta, che accetta e
assegna un'ambulanza.

> L'applicazione client (frontend JavaFX) si trova nella repo separata:
> **https://github.com/TheNemesis47/MedTaxi**

---

## Architettura

- **Java 21**
- Comunicazione via **Socket TCP** sulla **porta `12346`**
- Scambio messaggi in **JSON** (`org.json`)
- Accesso al database **MySQL** (`test`) tramite **JDBC**
- Thread pool per la gestione di più client in parallelo
- Notifica alle aziende (in ascolto sulla porta `54321`) per l'accettazione delle prenotazioni

### File principali (`Server/src/`)

| File | Ruolo |
|---|---|
| `ServerCentrale.java` | Avvio del server (menu: `1` = avvia) |
| `AvviaServer.java` | Accettazione connessioni e thread pool (porta 12346) |
| `ClientHandler.java` | Gestione della richiesta del client (ricerca aziende, costi) |
| `AziendaHandler.java` | Inoltro della prenotazione all'azienda e inserimento a DB |
| `Prenotazione.java` | Logica della prenotazione (distanze, costi, query) |
| `Singleton/Database.java` | Connessione MySQL (Singleton) |

---

## Requisiti

- **JDK 21** (es. Eclipse Temurin 21)
- **MySQL** 8+ con database `test` importato (vedi la repo client per il dump `test.sql`)

## Avvio manuale

```bash
cd Server
# Compilazione
javac -cp "src/lib/*" -d build $(find src -name "*.java")
# Avvio (poi digita 1 per avviare il server)
java -cp "build:src/lib/*" ServerCentrale
```

> Per avviare **tutto il sistema** (MySQL + server + client) automaticamente, usa lo script
> `avvia.sh` presente nella repo client, con entrambe le repo clonate nella stessa cartella.
