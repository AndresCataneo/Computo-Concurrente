use std::collections::HashMap;
use rand::Rng;
use std::sync::{
    atomic::{AtomicU64, Ordering},
    Arc, Mutex,
};
use std::thread;

struct TokenContract {
    balances: Mutex<HashMap<String, u64>>,

    //Contadores para saber el balance
    minted: AtomicU64,
    burned: AtomicU64,
}

impl TokenContract {
    fn new() -> Self {
        let mut state = HashMap::new();

        state.insert("Alice".to_string(), 1000);
        state.insert("Bob".to_string(), 500);

        TokenContract {
            balances: Mutex::new(state),

            minted: AtomicU64::new(0),
            burned: AtomicU64::new(0),
        }
    }

    //Transfer no afecta el balance porque solo mueve el dinero, no aumentan ni quema tokens.
    fn transfer(&self, from: &str, to: &str, amount: u64) -> bool {
        let mut b = self.balances.lock().unwrap();

        let from_bal = b.get(from).copied().unwrap_or(0);

        if from_bal < amount {
            return false;
        }

        *b.entry(from.to_string()).or_insert(0) -= amount;
        *b.entry(to.to_string()).or_insert(0) += amount;

        true
    }

    //Mint si afecta el balance total
    fn mint(&self, addr: &str, amount: u64) {
        let mut b = self.balances.lock().unwrap();

        *b.entry(addr.to_string()).or_insert(0) += amount;

        //Incrementamos el contador global
        self.minted.fetch_add(amount, Ordering::SeqCst);
    }

    //Burn si afecta al balance total
    fn burn(&self, addr: &str, amount: u64) -> bool {
        let mut b = self.balances.lock().unwrap();

        let balance = b.get(addr).copied().unwrap_or(0);

        if balance < amount {
            return false;
        }

        *b.entry(addr.to_string()).or_insert(0) -= amount;

        //Incrementamos el contador global
        self.burned.fetch_add(amount, Ordering::SeqCst);

        true
    }

    //Metodo que regresa la cantidad de tokens de un usuario (Si es que existe)
    fn balance(&self, addr: &str) -> u64 {
        self.balances
            .lock()
            .unwrap()
            .get(addr)
            .copied()
            .unwrap_or(0)
    }

    //Suma total de tokens 
    fn total(&self) -> u64 {
        let b = self.balances.lock().unwrap();

        b.values().sum()
    }
}

fn main() {
    let contract = Arc::new(TokenContract::new());

    let initial_token = contract.total();

    let mut handles = vec![];

    //Para 10 hilos concurrentes necesitamos hacer distintas operaciones
    for thread_id in 0..10 {
        let c = Arc::clone(&contract);

        handles.push(thread::spawn(move || {
            for i in 0..100 {
                //Utilizmos la siguiente linea para definir que accion realizar
                //let op = (thread_id + i) % 3;
                let op = rand::thread_rng().gen_range(0..3);

                match op {
                    // transfer
                    0 => {
                        c.transfer("Alice", "Bob", 1);
                    }

                    // mint
                    1 => {
                        c.mint("Alice", 2);
                    }

                    // burn
                    _ => {
                        c.burn("Bob", 1);
                    }
                }
            }
        }));
    }

    for h in handles {
        h.join().unwrap();
    }

    let total_token = contract.total();

    let total_minted = contract.minted.load(Ordering::SeqCst);

    let total_burned = contract.burned.load(Ordering::SeqCst);

    println!("====================");
    println!("Tokens iniciales : {}",initial_token);
    println!("Tokens al final   : {}", total_token);
    println!("Total de mint   : {}", total_minted);
    println!("Total de burn   : {}", total_burned);

    //Calculamos la invariante propuesta.
    let expected = initial_token + total_minted - total_burned;

    println!("Tokens esperados para la invariante: {}", expected);

    if total_token == expected {
        println!("Se cumplio con la invariante, no hay token de mas ni de menos");
    } else {
        println!("Hubo un error y la invariante no se cumplio");
    }
}