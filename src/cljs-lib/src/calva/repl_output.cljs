(ns calva.repl-output
  (:require ["vscode" :refer [window] :as vscode]
            [clojure.pprint :refer [pprint]]))

(defonce repl-output-channel (atom nil))

(defn pprint-enabled? []
  (.. vscode -workspace (getConfiguration "calva") -prettyPrintingOptions -enabled))

(defn create-repl-output-channel!
  "Creates the REPL output channel if it doesn't exist yet. Returns the existing or new output channel."
  []
  (if-let [output-channel @repl-output-channel]
    output-channel
    (reset! repl-output-channel
            (.. window (createOutputChannel "Calva REPL Output" "clojure")))))

(defn append-line [content]
  (.. ^js @repl-output-channel
      (appendLine (if (pprint-enabled?)
                    (with-out-str (pprint content))
                    content))))

(comment
  (append-line big-map)
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

