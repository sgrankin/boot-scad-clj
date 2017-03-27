(require '[boot.core :refer :all]                           ; IntelliJ "integration"
         '[boot.task.built-in :refer :all]
         '[clojure.java.io :as io])
(set-env!
  :dependencies
  '[[boot/core "2.6.0" :scope "provided"]
    [onetom/boot-lein-generate "0.1.3" :scope "test"]
    [scad-clj "0.5.2"]]
  :source-paths #{"src"})

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
              m/write-scad))))

(defn- clj->scad
  [path]
  (.replaceAll path "\\.clj$" ".scad"))

(deftask scadc
  "compile clj-scad clojure files"
  []
  (with-pre-wrap fileset
    (let [tmp (tmp-dir!)
          in-files (input-files fileset)
          in-files (by-ext [".clj"] in-files)]
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
