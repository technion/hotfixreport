(ns hotfixreport.core
  (:gen-class))

(require '[clojure.string :as str])
(require '[clojure-csv.core :as csv])

(def fixes
  [ "KB4041676", "KB4041691", "KB4041678", "KB4041681", "KB4041686" ])

(defn isvulnerable
   "Removes a line where a fix kb exists in column 4"
   [kb]
   (not (some
     #(str/includes? (nth kb 4) %) fixes)))

(defn get-data
  "Drop the first header line, and parse the tab delimters"
  [input]
  (map #(str/split %  #"\t")
    (rest
      (str/split (slurp input) #"\n"))))

(defn parsereport
  [input]
  (->> 
   (filter isvulnerable input)
   (map (fn [[id job fqdn garbage kb]]
     (zipmap [:host :domain]  
       (str/split fqdn #"\." 2))))))

(defn runreport
  [inputfile]
  (csv/write-csv 
    (into [] 
      (map (fn [{host :host domain :domain}] [host domain])
         (parsereport (get-data inputfile))))))

(defn -main
  [& args]
  (spit "parsed.tsv" (runreport "../scanout.txt") )
  (println "Creating parsed report"))
