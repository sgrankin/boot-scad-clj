(require '[scad-clj.model :as m]
         '[scad-clj.geometry :as g])

;; pitch 254/180 -> 1.5 -> 1.56
; length: 7.5 -> 8
(def bot {:od 25.0 :id 22.0 :length 8.0 :pitch 1.567})
(def mid {:od 27
          :id-bot (-> (:id bot) (- 2.0 ))
          :id-top 15.91
          :length 5
          :sides 8})
(def top {:od (* 55/64 25.4) :id 15.91 :length 3.5 :pitch 254/270})
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
