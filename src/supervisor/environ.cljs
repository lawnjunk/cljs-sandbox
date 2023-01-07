(ns supervisor.environ)

(goog-define API_URI "")
(println "environ API_URL" API_URI)

; if you chage DEBUG_BOXES to true while developing
; supervisor.space boxes will get random colors that take president
; over the css which can be usefull to see what the heck is going on
(goog-define DEBUG_BOXES false)

(goog-define SKIP_HLJS true)
