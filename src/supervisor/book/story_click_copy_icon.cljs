(ns supervisor.book.story-click-copy-icon
  (:require
    [spade.core :as spade]
    [reagent.core :as reagent]
    [oops.core :as oops]
    [supervisor.data.theme :as d-theme]
    [supervisor.base :as <>]
    [supervisor.fake :as fake]
    [supervisor.style :as style]
    [supervisor.space :as space]
    [supervisor.unit.click-copy-icon :as click-copy-icon]
    [supervisor.unit.click-copy-span :as click-copy-span]
    ))

(spade/defclass css-story-click-copy-icon
  [theme]
  (let [pallet (:pallet theme)
        color-about-bg (:black pallet)
        color-about-fg (:white pallet)
        color-textarea-bg (:button-main pallet)]
    [:&
     [:.wat
      {:position :absolute
       :background :white
       :top :5px
       :right :5px
       }]

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
     [:.space {:height :10px }]
     [:.clear
      (style/mixin-button-color color-textarea-bg)
      {:outline :none
       :padding [[:2px :8px]] }
      ]])
  )

(defn rand-text-gen
  []
  (fake/word (fake/integer 1 10)))

; TODO make an <>/HSPACE for dynamic horizontal spaceing
; and <>/VSPACE for vertical spacing (:size *default 10px*))
; actualy supervisor.space (space/v and space/h ) [space/v :45px]
; TODO can space have hbox and vbox for doing layout?
; [[vbox :10% [hbox :100% ...]]
;  [vbox :90% [hbox :20% ...] [hbox :80% ...]]
(defn story-unit-click-copy
  []
  (let [rand-text (reagent/atom (rand-text-gen))
        textarea-state (reagent/atom "")]
    (fn []
      (let [theme @(d-theme/fetch)]
        [:div.story {:class (css-story-click-copy-icon theme)}
         [:div.about
          [:h1 "story click-copy"]
          [space/n-col :10px]
          [:p "rand text: " @rand-text]
          [space/n-col :10px]
          [<>/ButtonDebug {:on-click #(swap! rand-text rand-text-gen)} "reset text"]]

         [:div.space]
         [:div.about
          [:h2 "unit-click-cocpy-icon"] ]
         [:div.main
          [:section
           [ :h2 "w 30 h 60" ]
           [click-copy-icon/unit {:copy @rand-text :height :30px :width :60px }]]
          [:section
           [ :h2 "w 50 h 40" ]
           [click-copy-icon/unit {:copy @rand-text :height :40px :width :50px }]]
          [:div.space]
         [:div.about
          [:h2 "unit-click-cocpy-span"] ]
          [:section
           [:h1 ]
           [click-copy-span/unit {:copy @rand-text :height :40px :width :50px }]]
          [:section
           [:h3 "try it out"]
           [:button.clear {:on-click #(reset! textarea-state "")} "clear"]
           [:textarea
            {:placeholder "paste here"
             :value @textarea-state
             :on-change #(reset! textarea-state (oops/oget % ".target.value"))
             }]]
          ]]))))
