(ns clojush.instructions.vectors
  (:use [clojush pushstate globals]
        clojush.instructions.common))
;      [clojure.string :only [split trim]])
;(:require [clojure.string :as string]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; stack instructions for vectors

(define-registered vector_integer_pop (popper :vector_integer))
(define-registered vector_float_pop (popper :vector_float))
(define-registered vector_boolean_pop (popper :vector_boolean))
(define-registered vector_string_pop (popper :vector_string))

(define-registered vector_integer_dup (duper :vector_integer))
(define-registered vector_float_dup (duper :vector_float))
(define-registered vector_boolean_dup (duper :vector_boolean))
(define-registered vector_string_dup (duper :vector_string))

(define-registered vector_integer_swap (swapper :vector_integer))
(define-registered vector_float_swap (swapper :vector_float))
(define-registered vector_boolean_swap (swapper :vector_boolean))
(define-registered vector_string_swap (swapper :vector_string))

(define-registered vector_integer_rot (rotter :vector_integer))
(define-registered vector_float_rot (rotter :vector_float))
(define-registered vector_boolean_rot (rotter :vector_boolean))
(define-registered vector_string_rot (rotter :vector_string))

(define-registered vector_integer_flush (flusher :vector_integer))
(define-registered vector_float_flush (flusher :vector_float))
(define-registered vector_boolean_flush (flusher :vector_boolean))
(define-registered vector_string_flush (flusher :vector_string))

(define-registered vector_integer_eq (eqer :vector_integer))
(define-registered vector_float_eq (eqer :vector_float))
(define-registered vector_boolean_eq (eqer :vector_boolean))
(define-registered vector_string_eq (eqer :vector_string))

(define-registered vector_integer_stackdepth (stackdepther :vector_integer))
(define-registered vector_float_stackdepth (stackdepther :vector_float))
(define-registered vector_boolean_stackdepth (stackdepther :vector_boolean))
(define-registered vector_string_stackdepth (stackdepther :vector_string))

(define-registered vector_integer_yank (yanker :vector_integer))
(define-registered vector_float_yank (yanker :vector_float))
(define-registered vector_boolean_yank (yanker :vector_boolean))
(define-registered vector_string_yank (yanker :vector_string))

(define-registered vector_integer_yankdup (yankduper :vector_integer))
(define-registered vector_float_yankdup (yankduper :vector_float))
(define-registered vector_boolean_yankdup (yankduper :vector_boolean))
(define-registered vector_string_yankdup (yankduper :vector_string))

(define-registered vector_integer_shove (shover :vector_integer))
(define-registered vector_float_shove (shover :vector_float))
(define-registered vector_boolean_shove (shover :vector_boolean))
(define-registered vector_string_shove (shover :vector_string))

(define-registered vector_integer_empty (emptyer :vector_integer))
(define-registered vector_float_empty (emptyer :vector_float))
(define-registered vector_boolean_empty (emptyer :vector_boolean))
(define-registered vector_string_empty (emptyer :vector_string))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; common instructions for vectors

(defn concater
  "Returns a function that takes a state and concats two vectors on the type stack."
  [type]
  (fn [state]
    (if (not (empty? (rest (type state))))
      (let [first-item (stack-ref type 0 state)
            second-item (stack-ref type 1 state)]
        (if (>= max-vector-length (+ (count first-item)
                                     (count second-item)))
          (->> (pop-item type state) 
            (pop-item type)
            (push-item (vec (concat second-item first-item)) type))
          state))
      state)))

(define-registered vector_integer_concat (concater :vector_integer))
(define-registered vector_float_concat (concater :vector_float))
(define-registered vector_boolean_concat (concater :vector_boolean))
(define-registered vector_string_concat (concater :vector_string))

(defn conjer
  "Returns a function that takes a state and conj's an item onto the type stack."
  [vec-type lit-type]
  (fn [state]
    (if (and (not (empty? (vec-type state)))
             (not (empty? (lit-type state))))
      (let [result (conj (vec (top-item vec-type state)) (top-item lit-type state))]
        (println result)
        (if (>= max-vector-length (count result))
          (push-item result
                     vec-type
                     (pop-item lit-type (pop-item vec-type state)))
          state))
      state)))

(define-registered vector_integer_conj (conjer :vector_integer :integer))
(define-registered vector_float_conj (conjer :vector_float :float))
(define-registered vector_boolean_conj (conjer :vector_boolean :boolean))
(define-registered vector_string_conj (conjer :vector_string :string))

(defn taker
  "Returns a function that takes a state and takes the first N items from the type
   stack, where N is from the integer stack."
  [type]
  (fn [state]
    (if (and (not (empty? (type state)))
             (not (empty? (:integer state))))
      (push-item (vec (take (top-item :integer state)
                            (top-item type state)))
                 type
                 (pop-item type (pop-item :integer state)))
      state)))

(define-registered vector_integer_take (taker :vector_integer))
(define-registered vector_float_take (taker :vector_float))
(define-registered vector_boolean_take (taker :vector_boolean))
(define-registered vector_string_take (taker :vector_string))

(defn subvecer
  "Returns a function that takes a state and takes the subvec of the top item
   on the type stack."
  [type]
  (fn [state]
    (if (and (not (empty? (type state)))
             (not (empty? (rest (:integer state)))))
      (let [vect (top-item type state)
            first-index (min (count vect) (max 0 (stack-ref :integer 1 state)))
            second-index (min (count vect) (max first-index (stack-ref :integer 0 state)))]
        (push-item (subvec vect first-index second-index)
                   type
                   (pop-item type (pop-item :integer (pop-item :integer state)))))
      state)))

(define-registered vector_integer_subvec (subvecer :vector_integer))
(define-registered vector_float_subvec (subvecer :vector_float))
(define-registered vector_boolean_subvec (subvecer :vector_boolean))
(define-registered vector_string_subvec (subvecer :vector_string))

(defn firster
  "Returns a function that takes a state and gets the first item from the type stack."
  [type lit-type]
  (fn [state]
    (if (and (not (empty? (type state)))
             (not (empty? (first (type state))))) ;Make sure the top vec isn't empty
      (push-item (first (top-item type state))
                 lit-type
                 (pop-item type state))
      state)))

(define-registered vector_integer_first (firster :vector_integer :integer))
(define-registered vector_float_first (firster :vector_float :float))
(define-registered vector_boolean_first (firster :vector_boolean :boolean))
(define-registered vector_string_first (firster :vector_string :string))

(defn laster
  "Returns a function that takes a state and gets the last item from the type stack."
  [type lit-type]
  (fn [state]
    (if (and (not (empty? (type state)))
             (not (empty? (first (type state))))) ;Make sure the top vec isn't empty
      (push-item (last (top-item type state))
                 lit-type
                 (pop-item type state))
      state)))

(define-registered vector_integer_last (laster :vector_integer :integer))
(define-registered vector_float_last (laster :vector_float :float))
(define-registered vector_boolean_last (laster :vector_boolean :boolean))
(define-registered vector_string_last (laster :vector_string :string))

(defn nther
  "Returns a function that takes a state and gets the nth item from the type stack."
  [type lit-type]
  (fn [state]
    (if (and (not (empty? (type state)))
             (not (empty? (:integer state)))
             (not (empty? (first (type state))))) ;Make sure the top vec isn't empty
      (let [vect (stack-ref type 0 state)
            index (mod (stack-ref :integer 0 state) (count vect))]
        (push-item (nth vect index)
                   lit-type
                   (pop-item :integer (pop-item type state))))
      state)))

(define-registered vector_integer_nth (nther :vector_integer :integer))
(define-registered vector_float_nth (nther :vector_float :float))
(define-registered vector_boolean_nth (nther :vector_boolean :boolean))
(define-registered vector_string_nth (nther :vector_string :string))

(defn rester
  "Returns a function that takes a state and takes the rest of the top item
   on the type stack."
  [type]
  (fn [state]
    (if (not (empty? (type state)))
      (push-item (vec (rest (top-item type state)))
                 type
                 (pop-item type state))
      state)))

(define-registered vector_integer_rest (rester :vector_integer))
(define-registered vector_float_rest (rester :vector_float))
(define-registered vector_boolean_rest (rester :vector_boolean))
(define-registered vector_string_rest (rester :vector_string))

(defn butlaster
  "Returns a function that takes a state and takes the butlast of the top item
   on the type stack."
  [type]
  (fn [state]
    (if (not (empty? (type state)))
      (push-item (vec (butlast (top-item type state)))
                 type
                 (pop-item type state))
      state)))

(define-registered vector_integer_butlast (butlaster :vector_integer))
(define-registered vector_float_butlast (butlaster :vector_float))
(define-registered vector_boolean_butlast (butlaster :vector_boolean))
(define-registered vector_string_butlast (butlaster :vector_string))

(defn lengther
  "Returns a function that takes a state and takes the length of the top item
   on the type stack."
  [type]
  (fn [state]
    (if (not (empty? (type state)))
      (push-item (count (top-item type state))
                 :integer
                 (pop-item type state))
      state)))

(define-registered vector_integer_length (lengther :vector_integer))
(define-registered vector_float_length (lengther :vector_float))
(define-registered vector_boolean_length (lengther :vector_boolean))
(define-registered vector_string_length (lengther :vector_string))

(defn reverser
  "Returns a function that takes a state and takes the reverse of the top item
   on the type stack."
  [type]
  (fn [state]
    (if (not (empty? (type state)))
      (push-item (vec (reverse (top-item type state)))
                 type
                 (pop-item type state))
      state)))

(define-registered vector_integer_reverse (reverser :vector_integer))
(define-registered vector_float_reverse (reverser :vector_float))
(define-registered vector_boolean_reverse (reverser :vector_boolean))
(define-registered vector_string_reverse (reverser :vector_string))

(defn pushaller
  "Returns a function that takes a state and pushes every item from the first
   vector onto the appropriate stack."
  [type lit-type]
  (fn [state]
    (if (empty? (type state))
      state
      (loop [lit-list (reverse (top-item type state))
             loop-state (pop-item type state)]
        (if (empty? lit-list)
          loop-state
          (recur (rest lit-list)
                 (push-item (first lit-list) lit-type loop-state)))))))

(define-registered vector_integer_pushall (pushaller :vector_integer :integer))
(define-registered vector_float_pushall (pushaller :vector_float :float))
(define-registered vector_boolean_pushall (pushaller :vector_boolean :boolean))
(define-registered vector_string_pushall (pushaller :vector_string :string))

(defn emptyvectorer
  "Returns a function that takes a state and pushes a boolean of whether the top
   vector is empty."
  [type]
  (fn [state]
    (if (not (empty? (type state)))
      (push-item (empty? (top-item type state))
                 :boolean
                 (pop-item type state))
      state)))

(define-registered vector_integer_emptyvector (emptyvectorer :vector_integer))
(define-registered vector_float_emptyvector (emptyvectorer :vector_float))
(define-registered vector_boolean_emptyvector (emptyvectorer :vector_boolean))
(define-registered vector_string_emptyvector (emptyvectorer :vector_string))



;
;(define-registered
;  string_contains ;;true if top string is a substring of second string; false otherwise
;  (fn [state]
;    (if (empty? (rest (:string state)))
;      state
;      (let [sub (top-item :string state)
;            full (stack-ref :string 1 state)
;            result-boolean (if (<= 0 (.indexOf full sub))
;                             true
;                             false)]
;        (push-item result-boolean
;                   :boolean
;                   (pop-item :string (pop-item :string state)))))))
;
;(define-registered
;  string_containschar ; true if the top char is in the top string
;  (fn [state]
;    (if (or (empty? (:string state))
;            (empty? (:char state)))
;      state
;      (let [sub (str (top-item :char state))
;            full (stack-ref :string 0 state)
;            result (<= 0 (.indexOf full sub))]
;        (push-item result
;                   :boolean
;                   (pop-item :char (pop-item :string state)))))))
;
;(define-registered
;  string_indexofchar ; puts on the integer stack the index of the top char in the top string
;  (fn [state]
;    (if (or (empty? (:string state))
;            (empty? (:char state)))
;      state
;      (let [sub (str (top-item :char state))
;            full (stack-ref :string 0 state)
;            index (.indexOf full sub)]
;        (push-item index
;                   :integer
;                   (pop-item :char (pop-item :string state)))))))
;
;(define-registered
;  string_occurencesofchar ; the number of times the top char is in the top string
;  (fn [state]
;    (if (or (empty? (:string state))
;            (empty? (:char state)))
;      state
;      (let [ch (stack-ref :char 0 state)
;            st (stack-ref :string 0 state)
;            occ (count (filter #{ch} st))]
;        (push-item occ
;                   :integer
;                   (pop-item :char (pop-item :string state)))))))
;
;(define-registered
;  string_replace ; In third string on stack, replaces all occurences of second string with first string
;  (fn [state]
;    (if (<= 3 (count (:string state)))
;      (let [result (string/replace (stack-ref :string 2 state)
;                                   (stack-ref :string 1 state)
;                                   (stack-ref :string 0 state))]
;        (if (>= max-string-length (count result))
;          (push-item result
;                     :string
;                     (pop-item :string (pop-item :string (pop-item :string state))))
;          state))
;      state)))
;
;(define-registered
;  string_replacefirst ; In third string on stack, replaces first occurence of second string with first string
;  (fn [state]
;    (if (<= 3 (count (:string state)))
;      (let [result (string/replace-first (stack-ref :string 2 state)
;                                         (stack-ref :string 1 state)
;                                         (stack-ref :string 0 state))]
;        (if (>= max-string-length (count result))
;          (push-item result
;                     :string
;                     (pop-item :string (pop-item :string (pop-item :string state))))
;          state))
;      state)))
;
;(define-registered
;  string_replacechar ; In top string on stack, replaces all occurences of second char with first char
;  (fn [state]
;    (if (and (not (empty? (:string state)))
;             (<= 2 (count (:char state))))
;      (let [result (string/replace (stack-ref :string 0 state)
;                                   (stack-ref :char 1 state)
;                                   (stack-ref :char 0 state))]
;        (if (>= max-string-length (count result))
;          (push-item result
;                     :string
;                     (pop-item :char (pop-item :char (pop-item :string state))))
;          state))
;      state)))
;
;(define-registered
;  string_replacefirstchar ; In top string on stack, replaces first occurence of second char with first char
;  (fn [state]
;    (if (and (not (empty? (:string state)))
;             (<= 2 (count (:char state))))
;      (let [result (string/replace-first (stack-ref :string 0 state)
;                                   (stack-ref :char 1 state)
;                                   (stack-ref :char 0 state))]
;        (if (>= max-string-length (count result))
;          (push-item result
;                     :string
;                     (pop-item :char (pop-item :char (pop-item :string state))))
;          state))
;      state)))
;
;(define-registered
;  string_removechar ; In top string on stack, remove all occurences of char
;  (fn [state]
;    (if (and (not (empty? (:string state)))
;             (not (empty? (:char state))))
;      (let [result (apply str (remove #{(stack-ref :char 0 state)}
;                                      (stack-ref :string 0 state)))]
;        (push-item result
;                   :string
;                   (pop-item :char (pop-item :string state))))
;      state)))