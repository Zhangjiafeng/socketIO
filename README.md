# sockeIO

代码用于文件传输

其中客户端先向文件分配服务器发送请求，文件分配服务器解析文件的大小以及各个服务器的压力，然后向压力最小的服务器发送一个请求，服务器收到请求之后向需要传输文件的客户端发送连接请求，连接成功后，客户端向对应的服务器传输文件。

client为客户端，可以选择文件，发送文件

assign-server为分配服务器，为client分配一个服务器

server为服务器