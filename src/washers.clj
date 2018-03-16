(require '[scad-clj.model :as m]
         '[scad-clj.geometry :as g])

(let [id 3.0
      od 7.0
      h 1.0 ]
  [(m/fn! 64)
   (m/difference
     (m/cylinder od h :center true)
     (m/cylinder id h :center true)
     )
   ])
