(ns clojush.instructions.numbers
  (:use [clojush.pushstate]
        [clojush.util]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; instructions for numbers

(defn adder
  "Returns a function that pushes the sum of the top two items."
  [type]
  (fn [state]
    (if (not (empty? (rest (type state))))
      (let [first (stack-ref type 0 state)
            second (stack-ref type 1 state)]
        (->> (pop-item type state)
             (pop-item type)
             (push-item (keep-number-reasonable (+' first second)) type)))
      state)))

(define-registered integer_add (adder :integer))
(define-registered float_add (adder :float))

(defn subtracter
  "Returns a function that pushes the difference of the top two items."
  [type]
  (fn [state]
    (if (not (empty? (rest (type state))))
      (let [first (stack-ref type 0 state)
            second (stack-ref type 1 state)]
        (->> (pop-item type state)
             (pop-item type)
             (push-item (keep-number-reasonable (- second first)) type)))
      state)))

(define-registered integer_sub (subtracter :integer))
(define-registered float_sub (subtracter :float))

(defn multiplier
  "Returns a function that pushes the product of the top two items."
  [type]
  (fn [state]
    (if (not (empty? (rest (type state))))
      (let [first (stack-ref type 0 state)
            second (stack-ref type 1 state)]
        (->> (pop-item type state)
             (pop-item type)
             (push-item (keep-number-reasonable (*' second first)) type)))
      state)))

(define-registered integer_mult (multiplier :integer))
(define-registered float_mult (multiplier :float))

(defn divider
  "Returns a function that pushes the quotient of the top two items. Does
   nothing if the denominator would be zero."
  [type]
  (fn [state]
    (if (and (not (empty? (rest (type state))))
             (not (zero? (stack-ref type 0 state))))
      (let [first (stack-ref type 0 state)
            second (stack-ref type 1 state)]
        (->> (pop-item type state)
             (pop-item type)
             (push-item (if (= type :integer)
                          (truncate (keep-number-reasonable (/ second first)))
                          (keep-number-reasonable (/ second first)))
                        type)))
      state)))

(define-registered integer_div (divider :integer))
(define-registered float_div (divider :float))

(defn modder
  "Returns a function that pushes the modulus of the top two items. Does
   nothing if the denominator would be zero."
  [type]
  (fn [state]
    (if (and (not (empty? (rest (type state))))
             (not (zero? (stack-ref type 0 state))))
      (let [frst (stack-ref type 0 state)
            scnd (stack-ref type 1 state)]
        (->> (pop-item type state)
             (pop-item type)
             (push-item (if (= type :integer)
                          (truncate (keep-number-reasonable (mod scnd frst)))
                          (keep-number-reasonable (mod scnd frst)))
                        type)))
      state)))

(define-registered integer_mod (modder :integer))
(define-registered float_mod (modder :float))

(defn comparer
  "Returns a function that pushes the result of comparator of the top two items
   on the 'type' stack onto the boolean stack."
  [type comparator]
  (fn [state]
    (if (not (empty? (rest (type state))))
      (let [first (stack-ref type 0 state)
            second (stack-ref type 1 state)]
        (->> (pop-item type state)
             (pop-item type)
             (push-item (comparator second first) :boolean)))
      state)))

(define-registered integer_lt (comparer :integer <))
(define-registered integer_lte (comparer :integer <=))
(define-registered integer_gt (comparer :integer >))
(define-registered integer_gte (comparer :integer >=))
(define-registered float_lt (comparer :float <))
(define-registered float_lte (comparer :float <=))
(define-registered float_gt (comparer :float >))
(define-registered float_gte (comparer :float >=))

(define-registered
  integer_fromboolean
  (fn [state]
    (if (not (empty? (:boolean state)))
      (let [item (stack-ref :boolean 0 state)]
        (->> (pop-item :boolean state)
             (push-item (if item 1 0) :integer)))
      state)))

(define-registered
  float_fromboolean
  (fn [state]
    (if (not (empty? (:boolean state)))
      (let [item (stack-ref :boolean 0 state)]
        (->> (pop-item :boolean state)
             (push-item (if item 1.0 0.0) :float)))
      state)))

(define-registered
  integer_fromfloat
  (fn [state]
    (if (not (empty? (:float state)))
      (let [item (stack-ref :float 0 state)]
        (->> (pop-item :float state)
             (push-item (truncate item) :integer)))
      state)))

(define-registered
  float_frominteger
  (fn [state]
    (if (not (empty? (:integer state)))
      (let [item (stack-ref :integer 0 state)]
        (->> (pop-item :integer state)
             (push-item (*' 1.0 item) :float)))
      state)))

(define-registered
  integer_fromstring
  (fn [state]
    (if (not (empty? (:string state)))
      (try (pop-item :string
                     (push-item (Integer/parseInt (top-item :string state))
                                :integer state))
        (catch Exception e state))
      state)))

(define-registered
  float_fromstring
  (fn [state]
    (if (not (empty? (:string state)))
      (try (pop-item :string
                     (push-item (Float/parseFloat (top-item :string state))
                                :float state))
        (catch Exception e state))
      state)))

(define-registered
  integer_fromchar
  (fn [state]
    (if (not (empty? (:char state)))
      (let [item (stack-ref :char 0 state)]
        (->> (pop-item :char state)
             (push-item (int item) :integer)))
      state)))

(define-registered
  float_fromchar
  (fn [state]
    (if (not (empty? (:char state)))
      (let [item (stack-ref :char 0 state)]
        (->> (pop-item :char state)
             (push-item (float (int item)) :float)))
      state)))

(defn minner
  "Returns a function that pushes the minimum of the top two items."
  [type]
  (fn [state]
    (if (not (empty? (rest (type state))))
      (let [first (stack-ref type 0 state)
            second (stack-ref type 1 state)]
        (->> (pop-item type state)
             (pop-item type)
             (push-item (min second first) type)))
      state)))

(define-registered integer_min (minner :integer))
(define-registered float_min (minner :float))

(defn maxer
  "Returns a function that pushes the maximum of the top two items."
  [type]
  (fn [state]
    (if (not (empty? (rest (type state))))
      (let [first (stack-ref type 0 state)
            second (stack-ref type 1 state)]
        (->> (pop-item type state)
             (pop-item type)
             (push-item (max second first) type)))
      state)))

(define-registered integer_max (maxer :integer))
(define-registered float_max (maxer :float))

(define-registered
  float_sin
  (fn [state]
    (if (not (empty? (:float state)))
      (push-item (keep-number-reasonable (Math/sin (stack-ref :float 0 state)))
                 :float
                 (pop-item :float state))
      state)))

(define-registered
  float_cos
  (fn [state]
    (if (not (empty? (:float state)))
      (push-item (keep-number-reasonable (Math/cos (stack-ref :float 0 state)))
                 :float
                 (pop-item :float state))
      state)))

(define-registered
  float_tan
  (fn [state]
    (if (not (empty? (:float state)))
      (push-item (keep-number-reasonable (Math/tan (stack-ref :float 0 state)))
                 :float
                 (pop-item :float state))
      state)))

(define-registered
  integer_inc
  (fn [state]
    (if (not (empty? (:integer state)))
      (push-item (inc (stack-ref :integer 0 state))
                 :integer
                 (pop-item :integer state))
      state)))

(define-registered
  integer_dec
  (fn [state]
    (if (not (empty? (:integer state)))
      (push-item (dec (stack-ref :integer 0 state))
                 :integer
                 (pop-item :integer state))
      state)))

(define-registered
  float_inc
  (fn [state]
    (if (not (empty? (:float state)))
      (push-item (inc (stack-ref :float 0 state))
                 :float
                 (pop-item :float state))
      state)))

(define-registered
  float_dec
  (fn [state]
    (if (not (empty? (:float state)))
      (push-item (dec (stack-ref :float 0 state))
                 :float
                 (pop-item :float state))
      state)))
