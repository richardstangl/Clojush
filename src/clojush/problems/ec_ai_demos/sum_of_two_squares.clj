;; sum_of_two_squares.clj
;; an example problem for clojush, a Push/PushGP system written in Clojure
;; Nic McPhee, mcphee@morris.umn.edu, 2016

(ns clojush.problems.ec-ai-demos.sum-of-two-squares
  (:use [clojush.pushgp.pushgp]
        [clojush.random]
        [clojush pushstate interpreter]
        clojush.instructions.common))

;;;;;;;;;;;;
;; Take two non-negative integers, square each,
;; and push the sum of the resulting integers


(def input-set
  [[0 0]
   [1 1]
   [1 1]
   [1 2]
   [2 1]
   [1 2]
   [2 1]
   [2 2]
   [1 2]
   [1 3]
   [2 1]
   [3 1]
   [2 3]
   [3 2]
   [1 4]
   [1 5]
   [4 1]
   [5 1]
   [4 5]
   [5 4]
   [2 2]
   [2 3]
   [3 4]
   [4 2]
   [2 4]
   [5 8]])

(defn expected-output
  [inputs]
  (let [[x y] inputs]
    (+ (* x x)
          (* y y))))

; Make a new push state, and then add every
; input to the special `:input` stack.
; You shouldn't have to change this.
(defn make-start-state
  [inputs]
  (reduce (fn [state input]
            (push-item input :input state))
          (make-push-state)
          inputs))

; The only part of this you'd need to change is
; which stack(s) the return value(s) come from.
(defn actual-output
  [program inputs]
  (let [start-state (make-start-state inputs)
        end-state (run-push program start-state)
        top-int (top-item :integer end-state)]
    top-int))

(defn abs [n]
  (if (< n 0)
    (- n)
    n))

(defn all-errors
  [program]
  (doall
    (for [inputs input-set]
      (let [expected (expected-output inputs)
            actual (actual-output program inputs)]
        (if (= actual :no-stack-item)
          1000
          (abs (- expected actual)))))))

(def atom-generators
  (concat (registered-for-stacks [:integer :exec :integer_add :integer_mult])
          (list (fn [] (lrand-int 100))
                'in1 'in2)))

(def argmap
  {:error-function all-errors
   :atom-generators atom-generators
   })
