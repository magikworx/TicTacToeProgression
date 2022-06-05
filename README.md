# Tic Tac Toe Progression
## current folders
### base
base game implementation
### starter
base local players and AIs
### udp0
simple udp implementation
### udp1
- packet loss tolerant udp connection with retry
- introduced a fault injection class
  - The fault injector is a server/client that passes messages back and forth between the existing client and gui, a man-in-the-middle.
  - The fault injector for the next stage introduces the fault that makes the previous stage unsuitable.
  - For instance, in packet loss we randomly don't forward packets.
### udp2
- packet loss tolerant with retry
- corruption tolerant with TCP checksums
- expanded fault injection capability
  - we randomly scramble the bytes
### udp3
- message ids for matching retries
- message id history for rejecting retries
- expanded fault injection capability
  - we late send packets by swapping a packet with its retry

### TCP0

- ported UDP0 to use TCP and keep the same structure
- provides the same reliability as UDP3(mostly) with way less code

### util
utilities and base structures for packing and transporting data
## future folders
### tcp0
- tcp0 buys reliability and smarter control logic
- multi-threaded connections for many clients
- more than one game instance
- leader boards
### objectstreams0
- buys ability to transmit classes across a connection and intelligently use remote objects
- major cleanup from tcp0 object transmission
## future tech
### second game
- connect 4?
### load balancers
- spread the request load to multiple server instances
- same service, many servers
### proxies / gateways
- forward traffic through different routes
- provide services on many servers through unified gateway
- many servers, single server
### directory server
- intelligent routing to different remote gateways
### cdn(?)
- redirect traffic for static objects to more local servers

## References
### Networking

Raw Socket Programming Tutorial
https://beej.us/guide/bgnet/html/

Discussion of UDP network protocol
https://www.gafferongames.com/categories/building-a-game-network-protocol/

### Java

TBD

### C/C++

Intro to C
https://beej.us/guide/bgc/html/split/

Lightweight UDP library (Single Header)
https://github.com/benhenshaw/quick_udp

https://github.com/starwing/znet

Lightweight Game/Gui Engine (Single Header)
https://github.com/OneLoneCoder/olcPixelGameEngine

Lightweight BDD (Behavior-Driven Design) Framework / Integration Tests
https://github.com/grassator/bdd-for-c

Lightweight TDD (Test-Driven Design) Framework / Unit Tests
https://github.com/catchorg/Catch2

Curated Header Libraries for C++
https://github.com/p-ranav/awesome-hpp#networking

Great C libraries
https://github.com/nothings/stb

https://github.com/yhirose/cpp-httplib

