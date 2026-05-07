(ns P6.core)

(def cuenta-alice (ref 1000))
(def cuenta-bob (ref 500))

;; atom: actualizacion atomica sin coordinacion con otros refs
(def intentos (atom 0))

(defn transferir
  "Transfiere cantidad de origen a destino de forma atomica .
  10 Si dos hilos colisionan , el STM reintenta automaticamente .
  11 No es posible deadlock : no hay locks , solo transacciones STM ."
  [origen destino cantidad]
  (dosync
   (swap! intentos inc) ; <- DENTRO del dosync: corre en cada reintento
   (let [saldo @origen]
     (if (>= saldo cantidad)
       (do (alter origen - cantidad)
           (alter destino + cantidad))
       nil))))

(defn transferir-con-print
  "Misma transferencia pero con un println para mostrar el efecto de los reintentos ."
  [origen destino cantidad]
  (dosync
   (println "ejecutando transaccion") ; efecto secundario en STM
   (let [saldo @origen]
     (if (>= saldo cantidad)
       (do (alter origen - cantidad)
           (alter destino + cantidad))
       nil))))

(defn simular [n monto]
  (reset! intentos 0)
  ;;(let [fs (doall (repeatedly n #(future (transferir cuenta-alice cuenta-bob monto))))]
  (let [fs (doall (repeatedly n #(future (transferir-con-print cuenta-alice cuenta-bob monto))))]
    (doseq [f fs] @f)
    {:alice @cuenta-alice
     :bob @cuenta-bob
     :intentos @intentos
     :futuros n}))

;; Ejecuta con distintos niveles de contencion:

(defn -main []
  ;(println "--- INICIANDO PARTE A ---")
  ;(println (simular 10 10))
  ;(println (simular 100 10))
  ;(println (simular 500 5))

  (println "\n--- INICIANDO PARTE B ---")
  (println (simular 50 10))
  (shutdown-agents))
