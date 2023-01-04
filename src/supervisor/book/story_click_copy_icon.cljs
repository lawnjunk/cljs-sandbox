(ns supervisor.book.story-click-copy-icon
  (:require
    [spade.core :as spade]
    [reagent.core :as reagent]
    [oops.core :as oops]
    [supervisor.data.theme :as d-theme]
    [supervisor.base :as <>]
    [supervisor.rand :as fake]
    [supervisor.style :as style]
    [supervisor.unit.click-copy-icon :as click-copy-icon]))

(spade/defclass css-story-click-copy-icon
  [theme]
  (let [pallet (:pallet theme)
        color-about-bg (:black pallet)
        color-about-fg (:white pallet)
        color-textarea-bg (:button-main pallet)]
    [:&
     [:.about
      {:background color-about-bg
       :color color-about-fg
       :padding :10px
       }]
     [:textarea
      {:background color-textarea-bg
       :padding :10px
       :width :100%
       :outline :none
       } ]
     [:.clear
      (style/mixin-button-color color-textarea-bg)
      {:outline :none
       :padding [[:2px :8px]] }
      ]])
  )

(defn rand-text-gen
  []
  (fake/word (fake/integer 1 10)))

(defn story-click-copy-icon
  []
  (let [rand-text (reagent/atom (rand-text-gen))
        textarea-state (reagent/atom "")]
    (fn []
      (let [theme @(d-theme/fetch)]
        [:div.story {:class (css-story-click-copy-icon theme)}
         [:div.about
          [:h1 "story unit-click-copy-icon"]
          [:p "rand text: " @rand-text]
          [<>/ButtonDebug {:on-click #(swap! rand-text rand-text-gen)} "reset text"]]
         [:div.main
          [:section
           [ :h2 "w 30 h 60" ]
           [click-copy-icon/unit {:copy @rand-text :height :30px :width :60px }]]
          [:section
           [ :h2 "w 50 h 40" ]
           [click-copy-icon/unit {:copy @rand-text :height :40px :width :50px }]]
          [:section
           [:h3 "try it out"]
           [:button.clear {:on-click #(reset! textarea-state "")} "clear"]
           [:textarea
            {:placeholder "paste here"
             :value @textarea-state
             :on-change #(reset! textarea-state (oops/oget % ".target.value"))
             }]]
          ]]))))
