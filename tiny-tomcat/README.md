## tiny-tomcat

This project is to demonstrate the usage of socket in java as a `tiny-tomcat`.

There are socket code of c in attachment directory, and it can communicate with java socket. 


#### 2018-04-08

add RequestFacade to be as the bridge between raw request and `javax.servlet.http.HttpServletRequest`

NEXT:

parse context in a server

#### 2018-04-09

instantiate the RequestFacade object via `RequestFacade rf = new RequestFacade();`, and treat it as HttpServletRequest to use in any sub-class of HttpServlet.

NEXT:

parse context and server from requestURI

run `SocketServer.main` and access `http://127.0.0.1:27016` to show.

#### 2018-04-19

response the string via MainServlet

the url `http://127.0.0.1:27016/main/bbb/ccc` is translated into the request.getContextPath() = "main" 