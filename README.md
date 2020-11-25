# Description of implementation choices and preferences 

##Naming
* The first app is the one which serves as a proxy for rest API is called "Proxy"
* The second one is called "Calculator"

##Stack
I took scala pure functional stack because I have a little more experience with it than with akka. 
I used akka http and akka actors in pet projects, but that's it.

Also usually scala software engineers are keen on functional programming, pure stuff and so on.

Technologies:  
1. Cats and Cats effects V2 to make all the code highly abstract
2. FS2 as a library for streams computations
3. http4s + tapir to make routes and build swagger
4. gRPC to perform communication between the services. I picked gRPC because I have it on my current job, but actually I have never worked with it yet. So I just decided to mix business with pleasure.
5. pureConfig was chosen because everyone uses this library for configurations of scala apps(I believe)
6. logging libraries are pretty standard also
7. ScalaFmt and better-monadic-for are also a widespread stuff in apps written on scala

##Implementation remarks
1. The first service does all the work with errors. Catches errors from the second service and rethrows its own if there will be any with a set of defined exceptions. The route has validation on the number(it should be >= 2).
2. Homemade circuit breaker was implemented as a recursive method without any extra libs. It just does several retries and doesn't include regular circuit breaker options like "Closed", "Open" and other statuses.
It doesn't retry if the second app is down, because IMHO there is not too much sense. This part of the code could be approached more precisely, but the logic is clear. There are situations when it's better to do a retry and there are when it doesn't make too much sense.
3. I added healthchecks to foresee both apps to dockerise it and kubernetise it and I wanted to write a docker compose, but I couldn't succeed from several attempts. So I decided to let it be as it is and leave apps without docker. 
Moreover, there is no any infrastructural stuff like database or a queue, so I decided to stop it there and to write README after all.
4. The second app has a pretty simple prime calculation algorithm. From the beginning I wanted to write more optimal algorithm, but then decided the current one is enough(you can find the draw in commits history if you want). 
It has a lazy stream, and it saves the previous results. In case of a restart of the application we lose all the calculations, but to do a database to save the data on disk here is IMHO overkill.
5. Unit tests were added to services to cover the most venerable parts of code.          

##Things which could be added
1. Better swagger descriptions
2. Rate limits on the "Proxy" to prevent possible "DDoS"
3. Clever caching in the "Calculator" not to lose all the data in case of restart. Any in memory database to have fast access to all the calculations.
4. Better circuit breaker
5. Dockerisation and kubernetesation
6. A lot of small details
7. Tracing. For example: Zipkin
8. Integration testing. For example with library from https://github.com/dimafeng/testcontainers-scala
9. Metrics and dashboards to measure "load". Grafana + Prometheus


##How to launch
1. Download the repository
2. Run the main class in calculator module
3. Run the main class in proxy module
4. Wait till both apps are ready to serve
4. Perform rest requests to "localhost:8083/prime/{number}" or use swagger http://localhost:8083/docs/index.html?url=/docs/docs.yaml
 