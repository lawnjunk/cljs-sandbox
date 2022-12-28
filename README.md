# cljs-sandbox
> a nice lil re-frame example repo.

# TODO
* create a :qdb effect that uses the query string as a datastore
* create a :ldb effect that uses localstorge as a datastore
* ?create a :api effect that makes api requests and manages follow-up :fx
* create a component that demonstrates how to work with forms

# about
* so what is in the dirs?
    * unit - ui components (micro ui)
    * page - ui pages full of components (macro ui)
    * data - re-frame-fx for storing and fetching data
    * http - re-frame-fx for making api requests
    * side - re-frame-intercepors for managing custom side effects
* custom re-fame effects
    * :ldb -> a localstorge backed database for storing persistent data (like :db)
        * great for caching credentials and preferences
    * :qdb -> a query-string backed database for storing view-state data (like :db)
        * great for storing view-state
    * :api -> used to make api requests
* data
    * request-ctx - used to track metadata about a single api request
    * request-metrix - used to track metadata about all api requests

## learning goals
- [ ] build time configuration variables
- [x] page routing with params using secretary
- [x] style the components with garden/spade
- [ ] write components with inputs and forms
- [x] make ajax requests with cljs-ajax
- [x] manage application data with re-frame's app-db
- [x] manage side effects using custom re-frame intercepors
- [x] manage view-state with query strings
- [x] manage persistent state with localstorge

## development
* setup development environment `npm i`
* start the api server `npm run api:start`
* start the dev server `npm run client:start`

## resources
* [clojurescript](https://clojurescript.org/)
* [clojurescript cheatsheet](https://cljs.info/cheatsheet/)
* [awesome clojurescript - resources list](https://github.com/hantuzun/awesome-clojurescript)
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
* [`goog` closuer-library - googles common js library](https://github.com/google/closure-librarye)
