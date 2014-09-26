(ns scraping.core
  (:gen-class)
  (:use net.cgrand.enlive-html)
  (:import java.net.URL)
  (:import java.nio.file.Paths)
  (:require [clojure.java.io :as io]))

(def img-exs-l '("jpg" "png" "gif"))

(defn fetch-to-file!
  "URLの画像をローカルに保存する"
  [url file]
  (with-open [in (io/input-stream url)
              out (io/output-stream file)]
    (io/copy in out)))

(defn img-url?
  "URLが画像の拡張子かどうか判定する"
  [url img-exs]
  (if (empty? url)
    false
    (let [bl (for [img-ex img-exs]
               (and (.contains url (str "." img-ex))))]
      (if (some true? bl)
        true
        false))))

(defn get-img-ex
  "URLに画像の拡張子があったら拡張子を返す"
  [url img-exs]
  (first
   (for [img-ex img-exs
         :when (.contains url img-ex)]
     img-ex)))

(defn split-img-url
  "URLを画像の拡張子で切る
  例:
  http://test.jpg?hogehoge -> http://test.jpg"
  [url img-exs]
  (let [b (img-url? url img-exs)
        img-ex (get-img-ex url img-exs)]
    (if (true? b)
      (str (first (.split url (str "." img-ex))) (str "." img-ex))
      false)))

(defn cons-rel-img-url
  "相対パスの画像urlを補完して正しいurlを返す"
  [rel-img-url url]
  (if (.startsWith rel-img-url "http")
    rel-img-url ;httpから始まっていたらそのまま返す
    (str url rel-img-url)))

(defn cons-img-url-list-a
  "URLのリストから画像URLを摘出しリストを生成する aタグ用
  マップの中の:attrsの値の:contentの値のリストの中のマップの:attrタグの値の:src"
  [url url-list img-exs]
  (for [img url-list
        :when (img-url? (:href (:attrs img)) img-exs)]
    (let [iurl (:href (:attrs img))]
      (-> iurl
          (split-img-url img-exs)
          (cons-rel-img-url url)))))

(defn cons-img-url-list-img
  "URLのリストから画像URLを摘出しリストを生成する imgタグ用
  マップの中の:attrsの値(マップ)の中の:src"
  [url url-list img-exs]
  (for [img url-list
        :when (img-url? (:src (:attrs img)) img-exs)]
    (let [iurl (:src (:attrs img))]
      (-> iurl
          (split-img-url img-exs)
          (cons-rel-img-url img-exs)))))

(defn get-file-name
  "保存用ファイル名取得"
  [file]
  (last (. file split "/")))

(defn fetch-to-files!
  "渡された複数のURLの画像をローカルに保存する"
  [urls save-f-dir]
  (for [url urls]
    (->> url
         get-file-name
         (str save-f-dir)
         (fetch-to-file! url))))

(defn cons-img-list
  "指定されたURLのソース内を指定されたタグで摘出したリストを生成"
  [url tag]
  (-> url
      URL. html-resource
      (select [tag])))

(defn save-page-img
  "指定されたURLのimgタグの画像をローカルに保存する"
  [url dir img-exs]
  (fetch-to-files! (cons-img-url-list-img url (cons-img-list url :img) img-exs) dir))

(defn save-page-a
  "指定されたURLのimgタグの画像をローカルに保存する"
  [url dir img-exs]
  (fetch-to-files! (cons-img-url-list-a url (cons-img-list url :a) img-exs) dir))

;; (save-page-img "http://www.yahoo.co.jp/" "C:\\Users\\" img-exs-l)
;; (save-page-a "http://windows.microsoft.com/ja-jp/windows/wallpaper" "C:\\Users\\" img-exs-l)

