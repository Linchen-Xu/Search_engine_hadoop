# Search_engine_hadoop
利用hadoop等实现的搜索引擎。前端只起到显示效果的作用，并没有进行优化。

整个项目包括四个部分：
#### InvertedIndex文件夹
eclipse Jee工程文件。用于计算倒排索引，并存入HBase。
此处的需要填写自己的master节点ip。src文件夹内代码中搜索your_ip_address并替换为自己的master节点ip即可。

#### saved_html文件夹
用于保存爬取的网页文本，内容已经过处理。

#### searchEngine文件夹
eclipse Jee工程文件。
用于监听HTTP请求，并返回搜索结果。

#### hadoop_homework_1.ipynb
使用jupyter notebook创建。
用于爬取网页文本并进行数据清洗与分词，最后保存到saved_html文件夹中。
