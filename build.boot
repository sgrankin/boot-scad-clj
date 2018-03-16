(require '[boot.core :refer :all]                           ; IntelliJ "integration"
         '[boot.task.built-in :refer :all]
         '[clojure.java.io :as io])
(set-env!
 :dependencies
 '[[org.clojure/clojure "1.9.0" :scope "provided"]
   [boot/core "2.7.2" :scope "provided"]
   [onetom/boot-lein-generate "0.1.3" :scope "test"]
   [scad-clj "0.5.2"]]
 :source-paths #{"src"}
 :resource-paths #{"lib"}
 )

(require '[boot.lein])
(boot.lein/generate)

(require '[scad-clj.scad :as scad])

(defn- compile-scad!
  [in-file out-file]
  (doto out-file
    io/make-parents
    (spit (-> in-file
              io/reader
              load-reader
              scad/write-scad))))

(defn- clj->scad
  [path]
  (.replaceAll path "\\.clj$" ".scad"))

(deftask scadc
  "compile clj-scad clojure files"
  []
  (with-pre-wrap fileset
    (let [tmp (tmp-dir!)
          in-files (input-files fileset)
          in-files (by-ext [".clj"] in-files)
          ]
      (empty-dir! tmp)
      (doseq [in in-files]
        (let [in-file (tmp-file in)
              in-path (tmp-path in)
              out-path (clj->scad in-path)
              out-file (io/file tmp out-path)]
          (compile-scad! in-file out-file)))
      (-> fileset
          (add-asset tmp)
          commit!))))

; vim:set ft=clojure:
