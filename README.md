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

# TODO
* unit-click-copy-span
* unit-blue-dot (with height-js)
  * would be cool if you could fuzzy filter lines :)
* unit-search-bar-generic
  * only sets the search term
* unit-filter-list-generic
  * displays a list of data
  * orders by selectables (create-at name email id...)
  * filters by a given prop search trem
    * multi fuzzy (name email id ...)
  * allows user to provide list item component
* unit-search-filter
  * combine (unit-search-bar-generic unit-filter-list-generic)
* unit-qdb-filter-container
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
  * `unit/` - ui components (micro ui)
  * `page/` - ui pages full of components (macro ui)
  * `data/` - re-frame-fx for storing and fetching data
  * `http/` - re-frame-fx for making api requests
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
* data
  * request-ctx - used to track metadata about a single api request
  * request-metrix - used to track metadata about all api requests

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
