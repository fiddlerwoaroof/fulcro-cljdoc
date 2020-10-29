(ns fulcro-cljdoc.ui.root
  (:require
   [fulcro.client.mutations :as m]
   [garden.core :as g]
   [garden.selectors :as gs]
   [fulcro-css.css :as css]
   [fulcro.client.util :as util]
   [fulcro.client.data-fetch :as df]
   [clojure.string]
   #?(:cljs [fulcro.client.dom :as dom!]
      :clj [fulcro.client.dom-server :as dom!])
   #?(:cljs [fulcro.client.localized-dom :as dom]
      :clj [fulcro.client.localized-dom-server :as dom])
   [fulcro-cljdoc.api.mutations :as api]
   [fulcro.client.primitives :as prim :refer [defsc]]
   [fulcro.i18n :as i18n :refer [tr trf]]))

(defn ensure-seq 
  "make sure that m-seq is a seq, otherwise seqify it"
  [m-seq]
  (if (seq? m-seq)
    m-seq
    [m-seq]))

(defsc Sidebar [this {items :items}]
  {:css [[:.sidebar {:display :block
                     :color "rgb(63,63,63)"
                     :background "rgb(197,199,168)"
                     :border-right "double 5px #88a"
                     :height "100vh"
                     :min-width "9em"
                     :text-align "right"
                     :width "12.5vw"}]]}
  (dom/nav :.sidebar
           (dom/ul (map #(apply (comp dom/li
                                      dom/a)
                                %)
                        (ensure-seq items)))))
(def ui-sidebar (prim/factory Sidebar))

(defn h* [{level :level :as props} & children]
  (apply (case level
           1 dom/h1
           2 dom/h2
           3 dom/h3
           4 dom/h4
           5 dom/h5
           6 dom/h6)
         (dissoc props :level)
         children))

(defsc Header [this {title :title}]
  {:css [[:header {:text-shadow "0 0 0.1em rgb(63,63,63), 0 0 0.2em #fff"}]
         [:h1 {:font-size "1.5em"}]]}
  (dom/header
   (h* {:level 1} title)
   (prim/children this)))

(def ui-header (prim/factory Header))

;; The main UI of your application
(defsc Layout [this props]
  {:css [[:.container {:display :flex}]
         [:main (gs/> :.container :nav)
          {:padding "1em 1.5em"
           :scroll-y :auto}]
         [(gs/> :.container :nav)
          {:padding-top "5em"}]
         [:$content {}]
         [:main     {:background "rgb(63,63,63)"
                     :color "rgb(197,199,168)"
                     :height "100vh"
                     :flex-grow 1}]
         [:header {:border-bottom "double 5px #88a"
                   :margin "0 -1.5em"
                   :margin-bottom "0.75em"
                   :padding-bottom "1em"
                   :padding-left "1.5em"}]]
   :css-include [Sidebar Header]}
  (let [children (prim/children this)
        sidebar (util/first-node Sidebar children)
        header (util/first-node Header children)
        others (filter #(and (not= sidebar %)
                             (not= header %))
                       children)]
    (let [out (dom/div :.container
                       sidebar
                       (dom/main
                        header
                        (dom/section :$content others)))]
      out)))

(def layout (prim/factory Layout))

(defsc Root [this props]
  {:css-include [Layout]}
  (let [publics (ns-publics 'fulcro-cljdoc.ui.root)
        names (map #(do [{:href "#"}
                         (name %)])
                   (sort-by (comp clojure.string/lower-case
                                  name)
                            (keys publics)))]
    (layout {}
            (ui-sidebar {:items names})
            (ui-header {:title "Hi there"})
            (dom/div {}
                     "hello world"
                     (:doc
                      (meta
                       (get publics
                            (symbol "ensure-seq"))))))))

#?(:cljs (css/upsert-css "root-css" Root))
