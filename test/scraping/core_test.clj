(ns scraping.core-test
  (:require [clojure.test :refer :all]
            [scraping.core :refer :all]))

(deftest img-url?-test
  (testing "img-url? test"
    ;; 拡張子を判定
    (is (= (img-url? "http://livedoor.blogimg.jp/ko_jo/imgs/4/e/4e3ddaef.jpg" img-exs-l) true))
    (is (= (img-url? "http://livedoor.blogimg.jp/ko_jo/imgs/4/e/4e3ddaef.jp" img-exs-l) false))

    (is (= (img-url? "https://livedoor.blogimg.jp/ko_jo/imgs/4/e/4e3ddaef.jpg" img-exs-l) true))

    ;; 対応拡張子のテスト
    (is (= (img-url? "http://livedoor.blogimg.jp/ko_jo/imgs/4/e/4e3ddaef.png" img-exs-l) true))
    (is (= (img-url? "http://livedoor.blogimg.jp/ko_jo/imgs/4/e/4e3ddaef.gif" img-exs-l) true))
    (is (= (img-url? "http://livedoor.blogimg.jp/ko_jo/imgs/4/e/4e3ddaef.swf" img-exs-l) false))

    ;; その他
    (is (= (img-url? "" img-exs-l) false))
    (is (= (img-url? () img-exs-l) false))
    (is (thrown? IllegalArgumentException (img-url? 1 img-exs-l)))
    (is (thrown? IllegalArgumentException (img-url? 0 img-exs-l)))
    (is (thrown? IllegalArgumentException (img-url? true img-exs-l)))
    (is (thrown? IllegalArgumentException (img-url? false img-exs-l)))))

(deftest cons-rel-img-url-test
  (testing "cons-rel-img-url test")
  ;; httpから始まってるか判定
  (is (= (cons-rel-img-url "4e3ddaef.jpg" "http://livedoor.blogimg.jp/ko_jo/imgs/4/e/") "http://livedoor.blogimg.jp/ko_jo/imgs/4/e/4e3ddaef.jpg"))
  (is (= (cons-rel-img-url "http://livedoor.blogimg.jp/ko_jo/imgs/4/e/4e3ddaef.jpg" "http://livedoor.blogimg.jp/ko_jo/imgs/4/e/") "http://livedoor.blogimg.jp/ko_jo/imgs/4/e/4e3ddaef.jpg")))

(def maps-a-test '({:tag :a, :attrs {:target "_blank", :href "http://livedoor.blogimg.jp/ko_jo/imgs/4/e/4e3ddaef.jpg"}, :content ({:tag :img, :attrs {:width "500", :hspace "5", :height "500", :border "0", :class "pict", :alt "112_3", :src "http://livedoor.blogimg.jp/ko_jo/imgs/4/e/4e3ddaef-s.jpg"}, :content nil})}
                         {:tag :a, :attrs {:target "_blank", :href "http://f24.aaa.livedoor.jp/%7Eyosuke/blacksabbathblacksabbath.JPG"}, :content nil}
                         {:tag :a, :attrs {:target "_blank", :href "http://livedoor.blogimg.jp/ko_jo/imgs/6/3/635c8827.jpg"}, :content ({:tag :img, :attrs {:width "500", :hspace "5", :height "500", :border "0", :class "pict", :alt "114_4", :src "http://livedoor.blogimg.jp/ko_jo/imgs/6/3/635c8827-s.jpg"}, :content nil})}
                         {:tag :a, :attrs {:target "_blank", :href "http://g-ec2.images-amazon.com/images/G/09/ciu/5c/7a/1a3dd0920ea01421429f1210.L.jpg"}, :content nil}))

(deftest cons-img-url-list-a-test
  (testing "cons-img-url-list-a test"
    (is (= 3 (count (cons-img-url-list-a "http://livedoor.blogimg.jp/" maps-a-test img-exs-l))))))

(def maps-img-test '({:tag :img, :attrs {:width "790", :height "120", :border "0", :alt "【2ch】コピペ情報局", :src "http://livedoor.blogimg.jp/ko_jo/imgs/1/5/15c21205.png"}, :content nil}
                           {:tag :img, :attrs {:title "このエントリーを含むはてなブックマーク", :alt "このエントリーを含むはてなブックマーク", :style "border: none;", :height "12", :width "16", :src "http://livedoor.2.blogimg.jp/ko_jo/imgs/d/d/dd30fc5b.gif"}, :content nil}
                           {:tag :img, :attrs {:width "500", :hspace "5", :height "491", :border "0", :class "pict", :alt "1_1", :src "http://livedoor.blogimg.jp/ko_jo/imgs/6/6/66d46977.jpg"}, :content nil}
                           {:tag :img, :attrs {:width "496", :hspace "5", :height "500", :border "0", :class "pict", :alt "1_2", :src "http://livedoor.blogimg.jp/ko_jo/imgs/4/6/46fe25c8.jpg"}, :content nil}
                           {:tag :img, :attrs {:width "499", :hspace "5", :height "500", :border "0", :class "pict", :alt "1_3", :src "http://livedoor.blogimg.jp/ko_jo/imgs/e/9/e912c6cc.jpg"}, :content nil}
                           {:tag :img, :attrs {:width "500", :hspace "5", :height "490", :border "0", :class "pict", :alt "1_4", :src "http://livedoor.blogimg.jp/ko_jo/imgs/8/7/87283b23-s.jpg"}, :content nil}))

(deftest cons-img-url-list-img-test
  (testing "cons-img-url-list-img test"
    (is (= 6 (count (cons-img-url-list-img "http://livedoor.blogimg.jp/" maps-img-test img-exs-l))))))

(deftest split-img-url-test
  (testing "split-img-url test"
    (is (= (split-img-url "http://2014.yurugp.jp/vote/result_ranking.jpg" img-exs-l) "http://2014.yurugp.jp/vote/result_ranking.jpg"))
    (is (= (split-img-url "http://2014.yurugp.jp/vote/result_ranking.jpg?" img-exs-l) "http://2014.yurugp.jp/vote/result_ranking.jpg"))
    (is (= (split-img-url "http://2014.yurugp.jp/vote/result_ranking.jpg?a" img-exs-l) "http://2014.yurugp.jp/vote/result_ranking.jpg"))
    (is (= (split-img-url "http://2014.yurugp.jp/vote/result_ranking.jpg?aaa" img-exs-l) "http://2014.yurugp.jp/vote/result_ranking.jpg"))
    (is (= (split-img-url "http://2014.yurugp.jp/vote/result_ranking.png?aaa" img-exs-l) "http://2014.yurugp.jp/vote/result_ranking.png"))
    (is (= (split-img-url "http://2014.yurugp.jp/vote/result_ranking.gif?aaa" img-exs-l) "http://2014.yurugp.jp/vote/result_ranking.gif"))
    (is (= (split-img-url "http://2014.yurugp.jp/vote/result_ranking.swf" img-exs-l) false))
    (is (= (split-img-url "http://2014.yurugp.jp/vote/result_ranking.swf?aaaa" img-exs-l) false))
    (is (= (split-img-url "http://2014.yurugp.jp/vote/result_ranking.jpg" img-exs-l) "http://2014.yurugp.jp/vote/result_ranking.jpg"))
    (is (= (split-img-url "http://2014.yurugp.jp/vote/result_ranking.gif" img-exs-l) "http://2014.yurugp.jp/vote/result_ranking.gif"))
    (is (= (split-img-url "http://2014.yurugp.jp/vote/result_ranking.png" img-exs-l) "http://2014.yurugp.jp/vote/result_ranking.png"))
    ;例外的な
    (is (= (split-img-url "" img-exs-l) false))
    (is (thrown? NullPointerException (split-img-url nil img-exs-l)))
    (is (= (split-img-url () img-exs-l) false))
    (is (thrown? IllegalArgumentException (split-img-url 0 img-exs-l)))))






