# Algoritmi di Mutua Esclusione

Questo repository contiene l'implementazione in Java di quattro algoritmi fondamentali per la gestione della **Mutua Esclusione** in sistemi distribuiti. L'obiettivo comune è garantire che l'accesso a una risorsa condivisa (Sezione Critica) avvenga in modo sicuro, evitando conflitti e garantendo la vitalità del sistema attraverso l'uso di messaggi asincroni.

## Algoritmi Implementati

Il progetto esplora diverse filosofie di coordinamento distribuito per risolvere il problema della mutua esclusione:

1. **Algoritmo Centralizzato** (`centralized`)
   - Basato su un unico **Coordinatore** centrale che gestisce una coda di richieste (FIFO).
   - I nodi inviano una `REQUEST`, attendono l'autorizzazione (`OK`) e rilasciano la risorsa con un messaggio di `RELEASE`.
   - Vantaggi: Semplice da implementare e richiede solo $3$ messaggi per ogni accesso alla sezione critica.

2. **Algoritmo Token Ring** (`tokenring`)
   - I nodi sono organizzati in un **anello logico** e comunicano solo con il nodo successivo.
   - Un messaggio speciale, il **Token**, circola nell'anello; solo chi lo possiede può accedere alla risorsa.
   - Vantaggi: Garantisce l'assenza di starvation e non richiede un coordinatore centrale.

3. **Algoritmo di Ricart & Agrawala** (`timestamp`)
   - Algoritmo completamente distribuito basato sui **Timestamp** di Lamport.
   - Un nodo invia una richiesta a tutti gli altri nodi ($n-1$) e deve ricevere un consenso da ognuno per procedere.
   - I conflitti sono risolti tramite l'ordinamento totale dato dalla coppia $(timestamp, id)$.

4. **Algoritmo Decentralizzato** (`decentralized`)
   - La risorsa è replicata su $m$ coordinatori indipendenti. Un nodo deve ottenere il **quorum** (maggioranza dei voti, ovvero $V > m/2$) per entrare in sezione critica.
   - Gestisce attivamente i conflitti tramite il rilascio dei voti parziali e un meccanismo di *backoff* casuale per evitare deadlock.

## Struttura del Codice

- `common`: Contiene le astrazioni di base del sistema (`AbstractNode`, `Message`, `MessageType`) e l'interfaccia `MutualExclusionAlgorithm`.
- `doc`: Contiene la documentazione Javadoc generata automaticamente.
- Pacchetti specifici: Ogni algoritmo risiede in un proprio package contenente le classi per i nodi, i coordinatori (dove previsti) e la classe di simulazione.

## Requisiti e Utilizzo

- **Requisiti**: Java JDK 11 o superiore.
- **Compilazione**: Può essere compilato tramite qualsiasi IDE Java (Eclipse, IntelliJ) o via terminale.`

## Esecuzione

Ogni implementazione dispone di una classe `Simulation`per avviare il test:
- **Centralizzato**: Eseguire `CentralizedSimulation.java`
- **Token Ring**: Eseguire `TokenRingSimulation.java`
- **Distribuito**: Eseguire `TimeStampSimulation.java`
- **Decentralizzato**: Eseguire `DecentralizedSimulation.java`

---
**Autore**: [Yammi2002]  
**Corso**: Sistemi Distribuiti - Università degli Studi di Parma
