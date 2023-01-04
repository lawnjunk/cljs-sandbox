(ns supervisor.book.story-space-layout
  (:require
    [supervisor.base :as b]
    [supervisor.fake :as fake]
    [supervisor.space :as s]))

(defn rand-style-prop [w h]
  {:style
   {:width w
    :height h
    :background (fake/color-rgb)}})

(defn x-demo
  [f title]
  [f {}
    [s/box (rand-style-prop :auto :20px) title]
    [s/box (rand-style-prop :auto :20px) title]
    [s/box (rand-style-prop :auto :20px) title]
    [s/box (rand-style-prop :auto :20px) title]  ]
  )

(defn story
  []
  [:div
   [b/Invert {}
    [:h2 {:style {:padding :10px}} "set environ.DEBUG_BOXES to true to better understand this page"]]
    [s/n-row :500px
     [s/n-row :10%
      ]
     [s/n-row :90%
      [s/f-col :20%]
      [s/f-col :30%
       [ s/f-row :80% ]
       [ s/f-row :20%
         [s/f-col :20% ]
         [s/f-col :80%
           [s/f-row :20% "s p a c e:"]
           [s/f-row :30%
             [s/f-col :70% :seventy]
             [s/f-col :30% :thirty]]
           [s/f-row :50%
             [s/f-col :40% :fourty]
             [s/f-col :60% :sixty]
            ]]
        ]
       ]
      [s/f-col :50%
        [s/f-row :50%
          [s/f-row :50%
           [s/f-col :25%
             [x-demo s/y-start :y-start]]
           [s/f-col :25%
             [x-demo s/y-even :y-even]]
           [s/f-col :25%
             [x-demo s/y-center :y-center]]
           [s/f-col :25%
             [x-demo s/y-end :y-end]]]
           [x-demo s/x-start :x-start]
           [x-demo s/x-even :x-even]
           [x-demo s/x-center :x-center]
           [x-demo s/x-end :x-end]]]]]])
