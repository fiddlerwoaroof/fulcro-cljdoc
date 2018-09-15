(ns fulcro-cljdoc.ui.root
  (:require
   [fulcro.client.mutations :as m]
   [garden.core :as g]
   [fulcro-css.css :as css]
   [fulcro.client.util :as util]
   [fulcro.client.data-fetch :as df]
   #?(:cljs [fulcro.client.localized-dom :as dom] :clj [fulcro.client.localized-dom-server :as dom])
   [fulcro-cljdoc.api.mutations :as api]
   [fulcro.client.primitives :as prim :refer [defsc]]
   [fulcro.i18n :as i18n :refer [tr trf]]))

(defn ensure-seq [m-seq]
  "make sure that m-seq is a seq, otherwise seqify it"
  (if (seq? m-seq)
    m-seq
    [m-seq]))

(defsc Sidebar [this {items :items}]
  (dom/ul (map #(apply (comp dom/li
                             dom/a)
                       %)
               (ensure-seq items))))
(def ui-sidebar (prim/factory Sidebar))

;; The main UI of your application
(defsc Layout [this props]
  {:css [[:.container {:display :flex}]
         [:main :.sidebar
          {:padding "1em 1.5em"
           :vertical-scroll :auto}]
         [:main     {:background "rgb(63,63,63)"
                     :color "rgb(197,199,168)"
                     :height "100vh"
                     :flex-grow 1}]
         [:.sidebar {:display :block
                     :color "rgb(63,63,63)"
                     :background "rgb(197,199,168)"
                     :border-right "double 5px #88a"
                     :height "100vh"
                     :min-width "25vw"
                     :max-width "33vw"}]]}
  (let [children (prim/children props)]
    (let [names (map #(do [{:href "#"} (name %)])
                     (keys (ns-publics 'fulcro-cljdoc.ui.root)))
          out (dom/div :.container
                       (dom/nav :.sidebar (ui-sidebar {:items names}))
                       (dom/main 
                        children))]
      out)))

(def layout (prim/factory Layout))

(defsc Root [this props]
  {:css-include [Layout]}
  (println "hi")
  ;; (css/upsert-css "root-css" Root)
  (layout 
   (ui-sidebar)
   (dom/div "hi")))

#?(:cljs (css/upsert-css "root-css" Layout))
;; (dom/style {:dangerouslySetInnerHTML {:__html (g/css (css/get-css Root))}})
