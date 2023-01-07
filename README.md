# supervisor
> dashboard for supervising pomelo

```
< mmm, pomelos are delicous >
 --------------------------
   \
    \     y
        .--.
       |. . |
       | O  |
      /      \
     (        )
      \_     /
        \.__.
```
# generic filter box
* input
  * list-name (used as key in qdb)
  * data-list - arbitrary list of data
  * filter-predicate
  * sort-by-main
  * sort-by-prop-list - list of props to create sort buttons for
  * item-component
* qdb state?
  * (list-name)-sort-by (:getter keyword)
  * (list-name)-sort-order (:asc :desc)

# TODO
* unit-filter-list-generic
  * displays a list of data
  * orders by selectables (create-at name email id...)
  * filters by a given prop search trem
    * multi fuzzy (name email id ...)
  * allows user to provide list item component
* unit-search-filter
* unit-qdb-filter-container
  * combine (unit-search-bar-generic unit-filter-list-generic)
* sticky-query-cleaner
  * keep track of components on page that need querys
  (remove querys from qdb when no components are loaded)
  * probs after a route switch with a tiny delay (allow for rerender)
  * :tag it with a name
  * inc each time componet mount
  * dec when unmount
  * handler for no more of :tag type
    * use for removing keys from :qdb

# about
* so what is in the dirs?
  * `unit/` - ui components that allow for custom props + react props (micro ui)
  * `part/` - ui components that only allow custom props (micro ui)
  * `book/` - ui components that are used for testing ui's in storybook
  * `page/` - ui components that are used for page routing (macro ui)
  * `data/` - re-frame-fx for storing and fetching data
  * `http/` - fns for creating `:api` fx form making api requets
  * `side/` - re-frame-intercepors for managing custom side effects
* custom re-fame effects in `side/`
  * :ldb -> a localstorge backed database for storing persistent data (like :db)
    * great for caching credentials and preferences
  * :qdb -> a query-string backed database for storing view-state data (like :db)
    * great for storing view-state
  * :api -> used to make api requests
    * can auto attach supervisor auth headers
    * requests are tracked with `request-ctx` and `request-metrix`
    * supports sending json and FormData
  * :debug -> used to log data when testing out a new side effect
    * this one is actual just a reg-event-fx so you have to dispatch data to it
* data
  * request-ctx - used to track metadata about a single api request
  * request-metrix - used to track metadata about all api requests

# conventions
* require data/\* libs as d-`<name>` and `<name>` by itself is an actual value
  * `(:require [supervisor.data.theme :as d-theme])`
  * `(let [theme @(d-theme/fetch)] ...)`

## unit component vs part component?
* `unit`'s are components that allow you to pass in normal `react` props
  * only have one arg `props` can have custom or react props
* `part`'s are components that have very specific props and will not
  * can have unique argument

## `item-*` component
* any component that starts with `item-` is used by a `map` to create a list
* they all will add `{:key <some-unique-value>}` to the root component
* items are not allowed to use reframe subscriptions or any global atoms
  * this means that all data used in this component will need to be passed by a
    prop
## story-component (in book\/)
* ui componets that are used for testing units and parts

## page-component
* page components allows take in 1 prop that is the route
* page routes are defined in `supervisor.data.route`
  * route configs can auto add links to the nave bar

## development
* setup development environment `npm i`
* start the dev server `npm run client:start`

## resources
* [clojurescript](https://clojurescript.org/)
* [clojurescript cheatsheet](https://cljs.info/cheatsheet/)
* [awesome clojurescript - resources list](https://github.com/hantuzun/awesome-clojurescript)
* [the clojure toolbox](https://www.clojure-toolbox.com/) - resources list
* [shadow-cljs - cljs build system](https://github.com/thheller/shadow-cljs)
* [clojure-lsip - clj/cljs LSP implementation](https://github.com/clojure-lsp/clojure-lsp)
* [kondo - a static analyzer and linter](https://github.com/clj-kondo/clj-kondo)
* [conjure - interactive evaluation for neovim](https://github.com/Olical/conjure)
* [parinfer - lisp paren editor mode](https://shaunlebron.github.io/parinfer/)
* [rust-parinfer - parinfer for neovim](https://shaunlebron.github.io/parinfer/)
* [reagent - minimal react for cljs](https://reagent-project.github.io/)
* [reframe - a reagent framework](https://day8.github.io/re-frame/)
* [secretary - cljs client side router](https://github.com/clj-commons/secretary)
* [cljs-ajax - http requests](https://github.com/JulianBirch/cljs-ajax)
* [garden - css in cljs](https://github.com/noprompt/garden)
* [spade - a css in cljs garden util](https://github.com/dhleong/spade)
* [oops - cljs macros for working with js objects](https://github.com/binaryage/cljs-oops)
* [`goog` closuer-library - googles common js library](https://github.com/google/closure-library)

# snips
  * cool sort-by snip: `(sort-by (juxt :lastname :firstname) data)`
