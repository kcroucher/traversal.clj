(ns traversal
  (:require clojure.string))

;; Read input args and grid file
(let [[arg1 arg2 arg3 arg4 arg5] *command-line-args*
      filename arg1
      line-start {:x (Float/parseFloat arg2) :y (Float/parseFloat arg3)}
      line-end {:x (Float/parseFloat arg4) :y (Float/parseFloat arg5)}
      file-contents (slurp filename)
      ;; Text file is loaded into a 2D array where outer array = x, inner array = y.
      ;; In a grid cell, 1 = true = collision, 0 = false = no collision
      grid (into [] (for [line (clojure.string/split file-contents #"\n")]
                       (into [] (for [c line]
                                  (not (zero? (Integer/parseInt (str c))))))))]
  ;; Traversal variables calculated on init
  (let [grid-size {:x (count grid)
                   :y (count (get grid 0))}
        displacement {:x (- (:x line-end) (:x line-start))
                      :y (- (:y line-end) (:y line-start))}
        step {:x (if (>= (:x displacement) 0) 1 -1)
              :y (if (>= (:y displacement) 0) 1 -1)}
        t-delta {:x (if (not (zero? (:x displacement)))
                      (abs (/ 1 (:x displacement)))
                      0)
                 :y (if (not (zero? (:y displacement)))
                      (abs (/ 1 (:y displacement)))
                      0)}]
    (defn find-first-collision []
      (loop [current-cell {:x (int (:x line-start))
                           :y (int (:y line-start))}
             t-current 0
             t-max {:x (if (not (zero? (:x displacement)))
                          (-> (:x current-cell)
                              (+ (if (= (:x step) 1) 1 0))
                              (- (:x line-start))
                              (/ (:x displacement)))
                          Float/MAX_VALUE)
                     :y (if (not (zero? (:y displacement)))
                          (-> (:y current-cell)
                              (+ (if (= (:y step) 1) 1 0))
                              (- (:y line-start))
                              (/ (:y displacement)))
                          Float/MAX_VALUE)}]
        (let [is-current-cell-in-grid (and
                                       (<= 0 (:x current-cell) (:x grid-size))
                                       (<= 0 (:y current-cell) (:y grid-size)))
              is-collision-in-current-cell (if is-current-cell-in-grid
                                             (get-in grid [(:x current-cell) (:y current-cell)])
                                             false)
              has-reached-end-of-line (>= t-current 1)]
          (if (or is-collision-in-current-cell has-reached-end-of-line)
            (if is-collision-in-current-cell
              current-cell
              nil)
            (let [traverse-along-x-axis (< (:x t-max) (:y t-max))
                  new-t-current (if traverse-along-x-axis (:x t-max) (:y t-max))
                  new-t-max (if traverse-along-x-axis
                               (merge t-max {:x (+ (:x t-max) (:x t-delta))})
                               (merge t-max {:y (+ (:y t-max) (:y t-delta))}))
                  new-current-cell (if traverse-along-x-axis
                                     (merge current-cell {:x (+ (:x current-cell) (:x step))})
                                     (merge current-cell {:y (+ (:y current-cell) (:y step))}))]
              (recur new-current-cell new-t-current new-t-max))))))
    (println (find-first-collision))))