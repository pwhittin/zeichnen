(ns zeichnen.core
  (:require [clojure.pprint :refer [pprint]]
            [clojure.java.io :as io]
            [clojure.string :as cstr]
            [clojure.tools.cli :as cli]
            [quil.core :as q])
  (:gen-class))

(def core-cli-options
  [["-i" "--input-file INPUTFILE" "File spec of input file"
    :default "zauberons.dat"]
   ["-h" "--help"]])

(defn validate-cli-map [cli-map]
  (let [input-file (get-in cli-map [:options :input-file])]
    (cond
      (cstr/blank? input-file)
      (update cli-map :errors conj "Input file must not be blank")

      (not (.exists (io/file input-file)))
      (update cli-map :errors conj (str "Input file '" input-file "' must exist"))
      :else cli-map)))

(defn valid-cli-map? [cli-map]
  (not (cli-map :errors)))

(defn print-cli-options [cli-map]
  (println "Command Line Options:")
  (println "  --------------------------------------------------------------------")
  (println "  short-form, long-form [long-form-value-name] [default] [description]")
  (println "  --------------------------------------------------------------------")
  (println (cli-map :summary)))

(defn print-cli-map-errors [cli-map]
  (print-cli-options cli-map)
  (println)
  (print "Error(s):")
  (doseq [error (cli-map :errors)]
    (println (str "\n" error))))

(defn args->cli-map [args cli-options]
  (->> (sort-by first cli-options) (cli/parse-opts args) validate-cli-map))

(defn help? [cli-map]
  (get-in cli-map [:options :help]))

(defn print-exception [e]
  (println)
  (println "***** Exception:")
  (pprint e)
  (println))

(defn main [args exit-fn]
  (try
    (let [cli-options core-cli-options
          cli-map (args->cli-map args cli-options)]
      (if (valid-cli-map? cli-map)
        (if (help? cli-map)
          (do (println) (print-cli-options cli-map) (println) (exit-fn 0))
          (do (println "Do It!") (exit-fn 0)))
        (do (println) (print-cli-map-errors cli-map) (println) (exit-fn 1))))
    (catch Exception e (print-exception e) (exit-fn 1))))

(defn -main [& args]
  (main args #(System/exit %)))

(comment

  (main [] identity)

  ; comment
  )
