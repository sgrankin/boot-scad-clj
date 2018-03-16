(require '[scad-clj.model :as m]
         '[scad-clj.geometry :as g])

(def bot {:od (* 55/64 25.4) :id 15.91 :length 3.5 :pitch 254/270})
(def top {:od 18.50 :id 15.0 :length 7.0 :pitch 254/270})
(def mid {:od (-> (max (:od bot) (:od top)) (+ 2.0))
          :id-bot (:id bot)
          :id-top (:id top)
          :length 5
          :sides 8})
[
 (m/fn! 64)
 (m/include "threads.scad")
 (->>
  (m/difference
   (m/call 'metric_thread
           {:diameter (:od top)
            :pitch    (:pitch top)
            :length   (:length top)})
   (m/cylinder (/ (:id top) 2) (+ (:length top) 1) :center false))
  (m/translate [0 0 (+ (:length bot) (:length mid))])
  )
 (->>
  (m/difference
   (m/with-fn
    (:sides mid)
    (m/cylinder (/ (:od mid) 2) (:length mid) :center false))
   (m/cylinder [(/ (:id-bot mid) 2) (/ (:id-top mid) 2)] (:length mid) :center false))
  (m/translate [0 0 (:length bot)])
  )
 (->>
  (m/difference
   (m/call 'metric_thread
           {:diameter (:od bot)
            :pitch    (:pitch bot)
            :length   (:length bot)})
   (->> (m/cylinder (/ (:id bot) 2) (+ (:length bot) 1) :center false)
        (m/translate [0 0 -1])))
  )
 ]
