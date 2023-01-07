(ns supervisor.part.code-highlight
  (:require
    [spade.core :as spade]
    [supervisor.space :as s]
    [supervisor.style :as style]
    [supervisor.util :as util]
    [supervisor.environ :as environ]
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

(defn hljs-el-with-id
  [id]
  (let [el (.getElementById js/document id)]
   (when (not environ/SKIP_HLJS)
     (.highlightElement js/hljs el))
   ))

(defn part
  "code-highlight using hljs"
  [language content]
  (let [random-id (util/id-gen)]
    (fn []
      (let [hljs-class-name (str "language-" (name language))]
        [s/hook {:id random-id
                 :postload #(hljs-el-with-id random-id)
                 :class (style/tag-value  [:code-highlight (css-part-code-highlight)])}
         [:pre [:code (style/tag hljs-class-name) content]]]))))
