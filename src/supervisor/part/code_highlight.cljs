(ns supervisor.part.code-highlight
  (:require
    [spade.core :as spade]
    [supervisor.space :as s]
    [supervisor.style :as style]
    [supervisor.data.theme :as d-theme]
    ))

(spade/defclass css-part-code-highlight
  []
  (let [pallet @(d-theme/fetch-pallet)
        bg (:blue-dot-code-bg pallet)]
    [:&
     {:background bg
      :white-space :pre
      :padding :20px
      :height :100%
      :width :100%
      :overflow :scroll}
     [:code
      {:background bg}]]))


(defn part
  "code-highlight using hljs"
  [language content]
  (let [hljs-class-name (str "language-" (name language))]
    [s/hook {:postload #(.highlightAll js/hljs)
             :class (css-part-code-highlight)}
     [:pre [:code (style/tag hljs-class-name) content]]]))
