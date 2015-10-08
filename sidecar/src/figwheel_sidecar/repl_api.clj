(ns figwheel-sidecar.repl-api
  (:require [figwheel-sidecar.repl :as fr]))

(defn start-figwheel!
  "If you aren't connected to an env where fighweel is running already,
  this method will start the figwheel server with the passed in build info."
  [{:keys [figwheel-options all-builds build-ids] :as autobuild-options}]
  (if-not fr/*autobuild-env*
    (let [env (fr/start-figwheel! autobuild-options)]
      (alter-var-root (var fr/*autobuild-env*) (fn [_] env))
      nil)
    (println "Figwheel system already initialized!")))

(defn stop-figwheel!
  "If a figwheel process is running, this will stop all the Figwheel autobuilders and stop the figwheel Websocket/HTTP server."
  []
  (when fr/*autobuild-env*
    (fr/stop-figwheel! fr/*autobuild-env*)
    (alter-var-root (var fr/*autobuild-env*) (fn [_] false))
    nil))

(comment
  ;; example usage
  (require 'figwheel-sidecar.repl-api)

  (in-ns 'figwheel-sidecar.repl-api)
  
  (start-figwheel!
   {:figwheel-options {}
    :build-ids ["example"]
    :all-builds [{ :id "example"
                  :source-paths ["src" "dev"]
                  :compiler {:main "example.dev"
                             :asset-path "js/out"
                             :output-to "resources/public/js/example.js"
                             :output-dir "resources/public/js/out"
                             :source-map true
                             :source-map-timestamp true
                             :cache-analysis true
                             :optimizations :none}}]})
  )

(defn build-once
  "Compiles the builds with the provided build ids (or the current default ids) once."
  [& ids]
  (fr/build-once ids))

(defn clean-builds
  "Deletes the compiled artifacts for the builds with the provided build ids (or the current default ids)."
  [& ids]
  (fr/clean-builds ids))

(defn stop-autobuild
  "Stops the currently running autobuild process."
  []
  (fr/stop-autobuild))

(defn start-autobuild
  "Starts a Figwheel autobuild process for the builds associated with the provided ids (or the current default ids)."
  [& ids]
  (fr/start-autobuild ids))

(defn switch-to-build
  "Stops the currently running autobuilder and starts building the builds with the provided ids."
  [& ids]
  (fr/switch-to-build ids))

(defn reset-autobuild
  "Stops the currently running autobuilder, cleans the current builds, and starts building the default builds again."
  []
  (fr/reset-autobuild))

(defn reload-config
  "Reloads the build config, and resets the autobuild."
  []
  (fr/reload-config))

(defn cljs-repl
  "Starts a Figwheel ClojureScript REPL for the provided build id (or the first default id)."
  ([] (fr/cljs-repl))
  ([id]
   (fr/cljs-repl id)))

(defn fig-status
  "Display the current status of the running Figwheel system."
  []
  (fr/status))

(defn add-dep
  "Attempts to add a maven dependency from clojars."
  [dep]
  (fr/add-dep* dep))

(defn- doc* [v]
  (let [{:keys [name doc arglists]} (meta v)]
    (print name " ")
    (prn arglists)
    (println doc)
    (newline)))

(defn api-help
  "Print out help for the Figwheel REPL api"
  []
  (mapv
   doc*
   [#'cljs-repl
    #'fig-status
    #'start-autobuild
    #'stop-autobuild
    #'build-once
    #'clean-builds
    #'switch-to-build
    #'reset-autobuild
    #'reload-config    
    #'api-help
    #'add-dep])
  nil)
