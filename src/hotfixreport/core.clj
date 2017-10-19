(ns hotfixreport.core
  (:gen-class))

(require '[clojure.string :as str])
(require '[clojure-csv.core :as csv])

(def fixes
  [ "KB4041676", "KB4041691", "KB4041678", "KB4041681", "KB4041686" ]
)

(defn isvulnerable
   [kb]
   (not (some
     #(str/includes? (nth kb 4) %) fixes))
)

(defn get-data
  "Drop the first header line, and parse the tab delimters"
  [input]
  (map #(str/split %  #"\t")
    (rest
      (str/split (slurp input) #"\n"))))

(defn parsereport
  [inputfile]
  (->> (get-data inputfile)
   (filter isvulnerable)
   (map (fn [[id job fqdn garbage kb]]
     (zipmap [:host :domain]  
       (str/split fqdn #"\." 2))))))

(defn runreport
  [inputfile]
  (csv/write-csv 
    (into [] 
      (map (fn [{host :host domain :domain}] [host domain])
         (parsereport inputfile)))))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (runreport "../scanout.txt")
  (println "Creating parsed report"))
