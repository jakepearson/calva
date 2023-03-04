(ns calva.repl-output
  (:require ["vscode" :refer [window] :as vscode]
            [clojure.pprint :refer [pprint]]))

(defonce repl-output-channel (atom nil))

;; Dummy comment
(defn pprint-enabled? []
  (.. vscode -workspace (getConfiguration "calva") -prettyPrintingOptions -enabled))

(defn create-repl-output-channel!
  "Creates the REPL output channel if it doesn't exist yet. Returns the existing or new output channel."
  []
  (if-let [output-channel @repl-output-channel]
    output-channel
    (reset! repl-output-channel
            (.. window (createOutputChannel "Calva REPL Output" "clojure")))))

(defn show-repl-output-channel! []
  (when-let [^js output-channel @repl-output-channel]
    (.. output-channel (show true))))

(defn append-line [^String content]
  (when-let [^js output-channel @repl-output-channel]
    (.. output-channel (appendLine content))))


(defn initialize-repl-output-channel!
  "Creates the REPL output channel if it does not exist and shows it."
  []
  (create-repl-output-channel!)
  (append-line "This is the Calva REPL output channel. You'll see REPL output here.")
  (append-line "\n---\n")
  (show-repl-output-channel!))

(comment
  (initialize-repl-output-channel!)
  (append-line big-map)
  (with-out-str (pprint big-map))
  (.. vscode -workspace (getConfiguration "calva") -prettyPrintingOptions -enabled)
  (create-repl-output-channel!)
  (.. vscode -window (showInformationMessage "hello"))

  ;;;; Output Window
  (def repl-output-channel (.. vscode -window (createOutputChannel "Calva REPL Output" "clojure")))
  (.. repl-output-channel (show true))
  (def big-map (zipmap
                [:a :b :c :d :e]
                (repeat
                 (zipmap [:a :b :c :d :e]
                         (take 5 (range))))))
  (.. repl-output-channel (clear))
  (.. repl-output-channel (appendLine "This is the Calva REPL Output window"))
  (.. repl-output-channel (appendLine (js/Error. "hello")))

  (dotimes [n 1000]
    (.. repl-output-channel (appendLine big-map #_(with-out-str (pprint big-map)))))

  (def repl-markdown-output-chan (.. vscode -window (createOutputChannel "Calva Markdown REPL Output" "markdown")))
  (.. repl-markdown-output-chan (show true))
  (.. repl-markdown-output-chan (clear))
  (.. repl-markdown-output-chan (appendLine "# Hello Markdown!"))
  (.. repl-markdown-output-chan (appendLine "```clojure\n(println \"Hello Markdown!\")\n```"))
  :rcf)

