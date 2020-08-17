(defproject items_service "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.10.1"]
                 [ring/ring-core "1.6.3"]
                 [ring/ring-jetty-adapter "1.6.3"]
                 [liberator "0.15.2"]
                 [bidi "2.1.6"]
                 [b-social/liberator-mixin "0.0.49"]
                 [clj-http "3.10.0"]
                 [uswitch/opencensus-clojure "0.2.84"]
                 [io.opencensus/opencensus-exporter-trace-jaeger "0.19.2"]
                 [io.opencensus/opencensus-exporter-trace-logging "0.19.2"]]

  :repl-options {:init-ns core
                 :init (go)
                 :timeout 180000})
