use std::sync::{Arc, Mutex};
use std::thread;

fn main(){
    let contador = Arc::new(Mutex::new(0_i64));
    let mut handles = vec![];

    for _ in 0..4 {
        let c = Arc::clone(&contador);
        handles.push(thread::spawn(move || {
            for _ in 0..1_000 {
                let mut num = c.lock().unwrap();
                *num += 1; 
            }
        }));
    }

    for h in handles{
        h.join().unwrap();
    }

    println!("contador: {}", *contador.lock().unwrap()); 
}


