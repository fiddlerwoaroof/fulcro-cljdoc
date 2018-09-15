(ns ^:dev/always fulcro-cljdoc.client-test-main
  (:require [fulcro-spec.selectors :as sel]
            [fulcro-spec.suite :as suite]))

(suite/def-test-suite client-tests {:ns-regex #"fulcro-cljdoc.*-spec"}
  {:default   #{::sel/none :focused}
   :available #{:focused}})

(client-tests)
