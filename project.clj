(defproject fulcro-cljdoc "0.1.0-SNAPSHOT"
  :description "My Cool Project"
  :license {:name "MIT" :url "https://opensource.org/licenses/MIT"}
  :min-lein-version "2.7.0"

  :dependencies [[org.clojure/clojure "1.9.0"]
                 [thheller/shadow-cljs "2.6.7"]
                 [fulcrologic/fulcro "2.6.3"]

                 ;; Only required if you use server
                 [http-kit "2.3.0"]
                 [ring/ring-core "1.7.0" :exclusions [commons-codec]]
                 [bk/ring-gzip "0.3.0"]
                 [bidi "2.1.4"]

                 ;; only required if you want to use this for tests
                 [fulcrologic/fulcro-spec "2.1.1" :scope "test" :exclusions [fulcrologic/fulcro]]

                 [binaryage/dirac "RELEASE"]
                 ]

  :uberjar-name "fulcro_cljdoc.jar"

  :source-paths ["src/main"]
  :test-paths ["src/test"]

  :test-refresh {:report       fulcro-spec.reporters.terminal/fulcro-report
                 :with-repl    true
                 :changes-only true}

  :profiles {:uberjar    {:main fulcro-cljdoc.server-main
                          :aot :all
                          :jar-exclusions [#"public/js/test" #"public/js/cards" #"public/cards.html"]
                          :prep-tasks ["clean" ["clean"]
                                       "compile" ["with-profile" "cljs" "run" "-m" "shadow.cljs.devtools.cli" "release" "main"]]}
             :production {}
             :cljs       {:source-paths ["src/main" "src/test" "src/cards"]
                          :dependencies [[binaryage/devtools "0.9.10"]
                                         [org.clojure/core.async "0.4.474"]
                                         [fulcrologic/fulcro-inspect "2.2.2" :exclusions [fulcrologic/fulcro-css]]
                                         [cljsjs/marked "0.3.5-1"]
                                         [devcards "0.2.6" :exclusions [cljsjs/react cljsjs/react-dom]]]
                          :repl-options
                          {:init-ns shadow.user
                           :nrepl-middleware [shadow.cljs.devtools.server.nrepl/cljs-load-file
                                              shadow.cljs.devtools.server.nrepl/cljs-eval
                                              shadow.cljs.devtools.server.nrepl/cljs-select
                                              dirac.nrepl/middleware]}}
             :dev        [:cljs
                          {:source-paths ["src/dev" "src/main" "src/cards"]
                           :jvm-opts     ["-XX:-OmitStackTraceInFastThrow" "-client" "-XX:+TieredCompilation" "-XX:TieredStopAtLevel=1"
                                          "-Xmx1g" "-XX:+UseConcMarkSweepGC" "-XX:+CMSClassUnloadingEnabled" "-Xverify:none"]

                           :plugins      [[com.jakemccrary/lein-test-refresh "0.21.1"]]

                           :dependencies [[org.clojure/tools.namespace "0.3.0-alpha4"]
                                          [org.clojure/tools.nrepl "0.2.13"]
                                          [com.cemerick/piggieback "0.2.2"]]
                           :repl-options {:init-ns          user
                                          :nrepl-middleware [cemerick.piggieback/wrap-cljs-repl]}}]})
